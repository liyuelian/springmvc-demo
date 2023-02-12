package com.li.controller;

import com.li.entity.Monster;
import com.li.myspringmvc.annotation.AutoWired;
import com.li.myspringmvc.annotation.Controller;
import com.li.myspringmvc.annotation.RequestMapping;
import com.li.myspringmvc.annotation.RequestParam;
import com.li.service.MonsterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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

}
