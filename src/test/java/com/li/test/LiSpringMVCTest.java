package com.li.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.entity.Monster;
import com.li.myspringmvc.xml.XMLParse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李
 * @version 1.0
 */
public class LiSpringMVCTest {
    @Test
    public void readXML() {
        String basePackage = XMLParse.getBasePackage("myspringmvc.xml");
        System.out.println("basePackage=" + basePackage);
    }

    @Test
    public void jacksonTest(){
        List<Monster> monsters = new ArrayList<>();
        monsters.add(new Monster(100, "牛魔王", "芭蕉扇", 400));
        monsters.add(new Monster(200, "猫妖", "撕咬", 800));
        //将monsters转成json
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String monstersJson = objectMapper.writeValueAsString(monsters);
            System.out.println(monstersJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
