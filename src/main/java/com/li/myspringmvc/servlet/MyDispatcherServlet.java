package com.li.myspringmvc.servlet;

import com.li.myspringmvc.annotation.Controller;
import com.li.myspringmvc.annotation.RequestMapping;
import com.li.myspringmvc.context.MyWebApplicationContext;
import com.li.myspringmvc.handler.MyHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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
    public void init() throws ServletException {
        //初始化ioc容器
        myWebApplicationContext = new MyWebApplicationContext();
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
        int length = getServletContext().getContextPath().length();
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
                myHandler.getMethod()
                        .invoke(myHandler.getController(), request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
