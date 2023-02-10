package com.li.myspringmvc.handler;

import java.lang.reflect.Method;

/**
 * @author 李
 * @version 1.0
 * MyHandler对象用于记录 请求的url 和 控制器方法的映射关系
 */
public class MyHandler {
    private String url;//正确的url
    private Object controller;//需要调用的控制器
    private Method method;//控制器中url对应的方法

    public MyHandler(String url, Object controller, Method method) {
        this.url = url;
        this.controller = controller;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "MyHandler{" +
                "url='" + url + '\'' +
                ", controller=" + controller +
                ", method=" + method +
                '}';
    }
}
