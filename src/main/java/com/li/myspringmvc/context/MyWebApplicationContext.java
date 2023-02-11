package com.li.myspringmvc.context;

import com.li.myspringmvc.annotation.AutoWired;
import com.li.myspringmvc.annotation.Controller;
import com.li.myspringmvc.annotation.Service;
import com.li.myspringmvc.xml.XMLParse;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 李
 * @version 1.0
 * MyWebApplicationContext 是我们自定义的spring容器
 */
public class MyWebApplicationContext {
    //属性classFullPathList用于保存扫描包/子包的类的全路径
    private List<String> classFullPathList = new ArrayList<>();
    //定义属性ioc，用于存放反射生成的 bean对象（单例的）
    public ConcurrentHashMap<String, Object> ioc = new ConcurrentHashMap<>();

    public MyWebApplicationContext() {
    }

    private String configLocation;//属性，表示spring容器配置文件名

    public MyWebApplicationContext(String configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * 该方法完成对自己的 spring容器的初始化
     */
    public void init() {
        //返回的是我们在容器文件中配置的base-package的value
        String basePackage = XMLParse.getBasePackage(configLocation.split(":")[1]);
        //System.out.println(configLocation.split(":")[1]);
        //这时你的 basePackage是像 com.li.controller,com.li.service 这样子的
        //通过逗号进行分割包
        String[] basePackages = basePackage.split(",");
        if (basePackages.length > 0) {
            //遍历这些包
            for (String pack : basePackages) {
                scanPackage(pack);
            }
        }
        System.out.println("扫描后的路径classFullPathList=" + classFullPathList);
        //将扫描到的类反射到ioc容器
        executeInstance();
        System.out.println("扫描后的ioc容器=" + ioc);
        //完成注入bean对象的属性装配
        executeAutoWired();
        System.out.println("装配后ioc容器=" + ioc);
    }

    /**
     * 该方法完成对包的扫描
     *
     * @param pack 表示要扫描的包，如 "com.li.controller"
     */
    public void scanPackage(String pack) {
        //得到包所在的工作路径[绝对路径]
        // (1)通过类的加载器，得到指定包的工作路径[绝对路径]
        // (2)然后用斜杠代替点=>如 com.li.controller=>com/li/controller
        URL url =
                this.getClass().getClassLoader()
                        .getResource("/" + pack.replaceAll("\\.", "/"));
        // url=file:/D:/IDEA-workspace/li-springmvc/target/li-springmvc
        // /WEB-INF/classes/com/li/controller/
        //System.out.println("url=" + url);

        //根据得到的路径，对其进行扫描，把类的全路径保存到 classFullPathList属性中
        String path = url.getFile();
        System.out.println("path=" + path);
        //在io中，把目录也视为一个文件
        File dir = new File(path);
        //遍历 dir目录,因为可能会有[多个文件/子目录]
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                //如果是目录，需要递归扫描
                //pack加上下一级的目录名继续下一层的扫描
                scanPackage(pack + "." + file.getName());
            } else {
                //这时得到的文件可能是.class文件，也可能是其他文件
                //就算是class文件，还需要考虑是否要注入到容器的问题
                //目前先把所有文件的全路径都保存到集合中，后面注入对象到spring容器时再考虑过滤
                String classFullPath =
                        pack + "." + file.getName().replaceAll(".class", "");
                classFullPathList.add(classFullPath);
            }
        }
    }

    /**
     * 该方法将扫描到的类，在满足条件的情况下进行反射，并放入到ioc容器中
     */
    public void executeInstance() {
        //是否扫描到了类
        if (classFullPathList.size() == 0) {//没有扫描到类
            return;
        }
        //遍历 classFullPathList，进行反射
        try {
            for (String classFullPath : classFullPathList) {
                Class<?> clazz = Class.forName(classFullPath);
                //判断是否要进行反射（即是否添加了注解）
                if (clazz.isAnnotationPresent(Controller.class)) {
                    Object instance = clazz.newInstance();
                    //获取该对象的id，默认情况下为类名(首字母小写)
                    String beanName = clazz.getSimpleName().substring(0, 1).toLowerCase()
                            + clazz.getSimpleName().substring(1);
                    String value = clazz.getAnnotation(Controller.class).value();
                    if (!"".equals(value)) {//如果注解的value指定了id
                        beanName = value;
                    }
                    ioc.put(beanName, instance);
                }//如果有其他注解，可以进行扩展
                else if (clazz.isAnnotationPresent(Service.class)) {//判断是否添加了 @Service注解
                    //获取 @Service注解的value值作为 beanName
                    String beanName = clazz.getAnnotation(Service.class).value();
                    //如果没有指定value
                    if ("".equals(beanName)) {
                        //可以通过接口名/列名（首字母小写）作为id注入ioc容器
                        //1.通过反射，得到所有接口的名称
                        Class<?>[] interfaces = clazz.getInterfaces();
                        Object instance = clazz.newInstance();
                        //2.遍历接口，然后通过多个接口名来分别作为这个实例的id
                        for (Class<?> anInterface : interfaces) {
                            //接口名（首字母小写）
                            String beanName2 = anInterface.getSimpleName().substring(0, 1).toLowerCase()
                                    + anInterface.getSimpleName().substring(1);
                            //ioc容器中多个key（接口名）匹配同一个Instance实例
                            ioc.put(beanName2, instance);
                        }
                        //3.同时通过类名（首字母小写）来作为这个实例的id
                        String beanName3 = clazz.getSimpleName().substring(0, 1).toLowerCase()
                                + clazz.getSimpleName().substring(1);
                        ioc.put(beanName3, instance);
                    } else {
                        //如果指定了 beanName
                        ioc.put(beanName, clazz.newInstance());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * executeAutoWired 方法完成属性的自动装配
     */
    public void executeAutoWired() {
        //判断ioc有没有要装配的对象
        if (ioc.isEmpty()) {
            return;
        }
        //遍历ioc所有的 bean对象，然后判断每个bean的属性字段是否需要装配
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            //一个entry对象一对 k-v
            // <String,Object>,String为 beanId，Object为 bean对象
            //String key = entry.getKey();
            Object bean = entry.getValue();
            //得到当前bean的所有字段/属性
            Field[] declaredFields = bean.getClass().getDeclaredFields();
            //遍历判断字段是否要装配
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(AutoWired.class)) {
                    //得到当前字段的 @AutoWired注解的 value值
                    String beanName = declaredField.getAnnotation(AutoWired.class).value();
                    if ("".equals(beanName)) {//如果没有设置value，按照默认规则
                        //即按照字段类型的名称（首字母小写）作为 beanName来装配
                        //得到字段的类型
                        Class<?> type = declaredField.getType();
                        //获取要匹配的名称（首字母小写）
                        beanName = type.getSimpleName().substring(0, 1).toLowerCase()
                                + type.getSimpleName().substring(1);
                    }
                    //如果设置了value，直接按照 beanName类进行装配
                    //ioc中没有找到对应名称的 bean
                    if (null == ioc.get(beanName)) {
                        throw new RuntimeException("ioc容器中不存在属性" + beanName + "要装配的对象！");
                    }
                    //ioc中找到了对应名称的 bean
                    //防止属性为private，使用暴破
                    declaredField.setAccessible(true);
                    try {
                        //装配属性
                        //第一个参数为当前字段所在类的 bean，第二个参数为当前的字段要关联的 bean
                        declaredField.set(bean, ioc.get(beanName));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

        }

    }

}
