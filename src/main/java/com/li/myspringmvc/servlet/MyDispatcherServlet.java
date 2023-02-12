package com.li.myspringmvc.servlet;

import com.li.myspringmvc.annotation.Controller;
import com.li.myspringmvc.annotation.RequestMapping;
import com.li.myspringmvc.annotation.RequestParam;
import com.li.myspringmvc.context.MyWebApplicationContext;
import com.li.myspringmvc.handler.MyHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author 李
 * @version 1.0
 * 1.MyDispatcherServlet充当原生的 DispatcherServlet，它的本质就是一个Servlet
 * 因此继承 HttpServlet
 */
public class MyDispatcherServlet extends HttpServlet {
    //定义属性 handlerList，保存 MyHandler对象[url和控制器方法的映射关系]
    private ArrayList<MyHandler> handlerList = new ArrayList<>();

    //定义ioc容器
    MyWebApplicationContext myWebApplicationContext = null;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //获取到web.xml文件中的 contextConfigLocation的值
        String configLocation = servletConfig.getInitParameter("contextConfigLocation");
        //初始化自定义的 ioc容器
        myWebApplicationContext = new MyWebApplicationContext(configLocation);
        myWebApplicationContext.init();
        //调用 initHandlerMapping()，完成url和控制器方法的映射
        initHandlerMapping();
        //测试输出 handlerList
        System.out.println("handlerList输出的结果=" + handlerList);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //System.out.println("MyDispatcherServlet doPost() 被调用..");
        //调用方法，完成分发请求
        executeDispatch(req, resp);
    }

    //该方法完成url和控制器方法的映射关联
    private void initHandlerMapping() {
        //判断当前的ioc容器是否为空
        if (myWebApplicationContext.ioc.isEmpty()) {
            //如果为空，就退出
            return;
        }
        //如果不为空，就遍历ioc容器的 bean对象，进行url映射处理
        //map的遍历
        Set<Map.Entry<String, Object>> entries = myWebApplicationContext.ioc.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            //先取出 bean的clazz对象
            Class<?> clazz = entry.getValue().getClass();
            //如果bean对象是一个Controller
            if (clazz.isAnnotationPresent(Controller.class)) {
                //取出所有的方法
                Method[] declaredMethods = clazz.getDeclaredMethods();
                //遍历所有的方法
                for (Method declaredMethod : declaredMethods) {
                    //如果该方法有 @RequestMapping注解
                    if (declaredMethod.isAnnotationPresent(RequestMapping.class)) {
                        //如果有，就取出@RequestMapping注解的value值，即该方法的映射路径
                        String url = declaredMethod.getAnnotation(RequestMapping.class).value();
                        //创建 MyHandler对象，一个 MyHandler 对象就是一个映射关系
                        MyHandler myHandler = new MyHandler(url, entry.getValue(), declaredMethod);
                        //handlerList 集合保存映射关系
                        handlerList.add(myHandler);
                    }
                }
            }
        }
    }

    /**
     * 通过request对象的url匹配 MyHandler对象的url，如果没有就返回404
     * 如果匹配就反射调用对应的方法
     */
    public MyHandler getMyHandler(HttpServletRequest request) {
        //获取用户请求 url
        //这里的 requestURL为 /web工程路径/xxx 形式的
        String requestURL = request.getRequestURI();
        //方案一：切割掉前面的 web工程路径
        int length = request.getContextPath().length();
        requestURL = requestURL.substring(length);
        System.out.println("requestURL=" + requestURL);
        //方案二：tomcat直接配置项目工程路径为 /
        //方案三：保存 MyHandler对象时的 url 连项目工程路径一起保存

        //遍历 handlerList
        for (MyHandler myHandler : handlerList) {
            //如果 requestURI和集合中的某个url相等
            if (requestURL.equals(myHandler.getUrl())) {
                //返回这个对象
                return myHandler;
            }
        }
        //如果没有匹配，即返回null
        return null;
    }

    //编写方法，完成分发请求
    private void executeDispatch(HttpServletRequest request, HttpServletResponse response) {
        MyHandler myHandler = getMyHandler(request);
        try {
            //如果 myHandler为 null，说明请求 url没有匹配的方法，即用户请求的资源不存在
            if (myHandler == null) {
                response.getWriter().print("<h1>404 NOT FOUND</h1>");
            } else {//匹配成功,就反射调用控制器的方法
                /**
                 * 1.原先的写法为 myHandler.getMethod()
                 *      .invoke(myHandler.getController(), request, response);
                 *  它的局限性是目标方法只能有两个形参： HttPServletRequest 和 HttPServletResponse
                 * 2.改进：将需要传递给目标方法的实参，封装到一个参数数组，然后以反射调用的方式传递给目标方法
                 * 3.public Object invoke(Object obj, Object... args)
                 */
                //1.先获取目标方法的所有形参的参数信息
                Class<?>[] parameterTypes = myHandler.getMethod().getParameterTypes();
                //2.创建一个参数数组（对应实参数组），在后面反射调动目标方法时会用到
                Object[] params = new Object[parameterTypes.length];
                //遍历形参数组 parameterTypes，根据形参数组的信息，将实参填充到实参数组中

                //步骤一：将方法的 HttpServletRequest 和 HttpServletResponse 参数封装到参数数组，进行反射调用
                for (int i = 0; i < parameterTypes.length; i++) {
                    //取出当前的形参的类型
                    Class<?> parameterType = parameterTypes[i];
                    //如果这个形参是 HttpServletRequest，将request填充到实参数组params
                    //在原生的SpringMVC中，是按照类型来匹配的，这里为了简化就按照名称来匹配
                    if ("HttpServletRequest".equals(parameterType.getSimpleName())) {
                        params[i] = request;
                    } else if ("HttpServletResponse".equals(parameterType.getSimpleName())) {
                        params[i] = response;
                    }
                }
                //步骤二：将 http请求的参数封装到 params数组中[要注意填充实参数组的顺序问题]
                // 获取http请求的参数集合 Map<String, String[]>
                // 第一个参数 String 表示 http请求的参数名，
                // 第二个参数 String[]数组，之所以为数组，是因为前端有可能传入像checkbox这种多选的参数
                Map<String, String[]> parameterMap = request.getParameterMap();
                // 遍历 parameterMap，将请求参数按照顺序填充到实参数组 params
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    //取出请求参数的名
                    String name = entry.getKey();
                    //取出请求参数的值（这里为了简化，只考虑参数是单值的情况，不考虑类似checkbox的提交的数据）
                    String value = entry.getValue()[0];
                    //找到请求的参数对应目标方法的形参的索引，然后将其填充到实参数组
                    //1.[请求参数名和 @RequestParam 注解的 value值 匹配]
                    int indexOfRequestParameterIndex =
                            getIndexOfRequestParameterIndex(myHandler.getMethod(), name);
                    if (indexOfRequestParameterIndex != -1) {//找到了对应位置
                        //将请求参数的值放入实参数组中
                        params[indexOfRequestParameterIndex] = value;
                    } else {
                        //没有在目标方法的形参数组中找到对应的下标位置
                        //2.使用默认机制进行匹配 [即请求参数名和形参名匹配]
                        // (1)拿到目标方法的所有形参名
                        List<String> parameterNames = getParameterNames(myHandler.getMethod());
                        // (2)对形参名进行遍历,如果匹配，把当前请求的参数值填充到实参数组的相同索引位置
                        for (int i = 0; i < parameterNames.size(); i++) {
                            //如果形参名和请求的参数名相同
                            if (name.equals(parameterNames.get(i))) {
                                //将请求的参数的value值放入实参数组中
                                params[i] = value;
                                break;
                            }
                        }
                    }
                }

                myHandler.getMethod().invoke(myHandler.getController(), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 编写方法，返回请求参数是目标方法的第几个形参
     * [请求参数名和 @RequestParam 注解的 value值 匹配]
     *
     * @param method 目标方法
     * @param name   请求的参数名
     * @return 返回请求的参数匹配目标方法形参的索引位置
     */
    public int getIndexOfRequestParameterIndex(Method method, String name) {
        //得到 method的所有形参参数
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            //取出当前的形参
            Parameter parameter = parameters[i];
            //先处理前面有 @RequestParam 注解修饰的形参
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                //取出当前形参parameter的注解 @RequestParam的 value值
                String value = parameter.getAnnotation(RequestParam.class).value();
                //将请求的参数和注解指定的value匹配，如果相同就说明找到了目标方法的形参位置
                if (name.equals(value)) {
                    return i;//返回的是匹配的形参的位置
                }
            }
        }
        return -1;//如果没有匹配成功，就返回-1
    }

    /**
     * 编写方法，得到目标方法的所有形参的名称，并放入到集合中返回
     *
     * @param method
     * @return
     */
    public List<String> getParameterNames(Method method) {
        ArrayList<String> paramNamesList = new ArrayList<>();
        //获取到所有的参数名--->这里有一个细节
        //默认情况下 parameter.getName() 返回的的名称不是真正的形参名 request,response,name...
        //而是 [arg0, arg1, arg2...]
        //这里我们使用java8的特性，并且在pom.xml文件中配置maven编译插件，才能得到真正的名称
        Parameter[] parameters = method.getParameters();
        //遍历parameters，取出名称，放入 paramNamesList
        for (Parameter parameter : parameters) {
            paramNamesList.add(parameter.getName());
        }
        System.out.println("目标方法的形参参数列表名称=" + paramNamesList);
        return paramNamesList;
    }
}
