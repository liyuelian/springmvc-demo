package com.li.myspringmvc.context;

import com.li.myspringmvc.annotation.Controller;
import com.li.myspringmvc.xml.XMLParse;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
