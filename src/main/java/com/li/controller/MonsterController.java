package com.li.controller;

import com.li.entity.Monster;
import com.li.myspringmvc.annotation.*;
import com.li.service.MonsterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 李
 * @version 1.0
 * 用于测试的 Controller
 */
@Controller
public class MonsterController {

    //属性
    @AutoWired
    private MonsterService monsterService;

    //编写方法，可以列出妖怪列表
    //springmvc支持原生的servlet api，为了看到底层机制，这里直接放入两个参数
    @RequestMapping(value = "/monster/list")
    public void listMonster(HttpServletRequest request, HttpServletResponse response) {
        //设置编码
        response.setContentType("text/html;charset=utf-8");
        StringBuilder content = new StringBuilder("<h1>妖怪列表信息</h1>");
        content.append("<table border='1px' width='500px' style='border-collapse:collapse'>");
        //调用 monsterService的方法
        List<Monster> monsters = monsterService.listMonster();
        for (Monster monster : monsters) {
            content.append("<tr>" +
                    "<td>" + monster.getId() + "</td>" +
                    "<td>" + monster.getName() + "</td>" +
                    "<td>" + monster.getSkill() + "</td>" +
                    "<td>" + monster.getAge() + "</td></tr>");
        }
        content.append("</table>");
        //获取writer，返回提示信息
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //增加方法，通过name返回对应的monster集合
    @RequestMapping(value = "/monster/find")
    public void findMonsterByName(HttpServletRequest request,
                                  HttpServletResponse response, String name) {
        //设置编码
        response.setContentType("text/html;charset=utf-8");

        System.out.println("----接收到的name=" + name);
        StringBuilder content = new StringBuilder("<h1>妖怪列表信息</h1>");
        content.append("<table border='1px' width='400px' style='border-collapse:collapse'>");
        //调用 monsterService的方法
        List<Monster> monsters = monsterService.findMonsterByName(name);
        for (Monster monster : monsters) {
            content.append("<tr>" +
                    "<td>" + monster.getId() + "</td>" +
                    "<td>" + monster.getName() + "</td>" +
                    "<td>" + monster.getSkill() + "</td>" +
                    "<td>" + monster.getAge() + "</td></tr>");
        }
        content.append("</table>");
        //获取writer，返回提示信息
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //处理登录的方法,返回要请求转发或重定向的字符串
    @RequestMapping(value = "/monster/login")
    public String login(HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestParam(value = "monsterName") String mName) {
        System.out.println("----接收到的mName-->" + mName);
        request.setAttribute("mName", mName);
        boolean b = monsterService.login(mName);
        if (b) {//登录成功
            // 请求转发到login_ok.jsp
            //return "forward:/login_ok.jsp";
            //return "redirect:/login_ok.jsp";
            return "login_ok.jsp";
        } else {//登录失败
            //return "forward:/login_error.jsp";
            //return "redirect:/login_error.jsp";
            return "login_error.jsp";
        }
    }

    /**
     * 编写方法，返回json格式的数据
     * 1.目标方法返回的结果是给SpringMVC底层通过反射调用的位置
     * 2.我们在SpringMVC底层反射调用的位置接收到结果并进行解析即可
     * 3. @ResponseBody(value = "json") 表示希望以json格式返回数据给浏览器
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/monster/list/json")
    @ResponseBody(value = "json")
    public List<Monster> listMonsterByJson(HttpServletRequest request,
                                           HttpServletResponse response) {
        List<Monster> monsters = monsterService.listMonster();
        return monsters;

    }
}
