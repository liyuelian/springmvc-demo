package com.li.controller;

import com.li.myspringmvc.annotation.Controller;
import com.li.myspringmvc.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 李
 * @version 1.0
 * 用于测试的Controller
 */
@Controller
public class MonsterController {
    //编写方法，可以列出妖怪列表
    //springmvc支持原生的servlet api，为了看到底层机制，这里直接放入两个参数
    @RequestMapping(value = "/monster/list")
    public void listMonster(HttpServletRequest request, HttpServletResponse response) {
        //设置编码
        response.setContentType("text/html;charset=utf-8");
        //获取writer，返回提示信息
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print("<h1>妖怪列表信息</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
