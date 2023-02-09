package com.li.myspringmvc.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 李
 * @version 1.0
 * 1.MyDispatcherServlet充当原生的 DispatcherServlet，它的本质就是一个Servlet
 * 因此继承 HttpServlet
 */
public class MyDispatcherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("MyDispatcherServlet doGet() 被调用..");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("MyDispatcherServlet doPost() 被调用..");
    }
}
