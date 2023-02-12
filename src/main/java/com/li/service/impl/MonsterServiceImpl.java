package com.li.service.impl;

import com.li.entity.Monster;
import com.li.myspringmvc.annotation.Service;
import com.li.service.MonsterService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李
 * @version 1.0
 * MonsterServiceImpl 作为一个Service对象注入容器
 */
@Service
public class MonsterServiceImpl implements MonsterService {

    @Override
    public List<Monster> listMonster() {
        //这里模拟到 DB获取数据
        List<Monster> monsters = new ArrayList<>();
        monsters.add(new Monster(100, "牛魔王", "芭蕉扇", 400));
        monsters.add(new Monster(200, "猫妖", "撕咬", 800));
        return monsters;
    }

    @Override
    public List<Monster> findMonsterByName(String name) {
        //这里模拟到 DB获取数据
        List<Monster> monsters = new ArrayList<>();
        monsters.add(new Monster(100, "牛魔王", "芭蕉扇", 400));
        monsters.add(new Monster(200, "猫妖", "撕咬", 800));
        monsters.add(new Monster(300, "鼠精", "偷灯油", 200));
        monsters.add(new Monster(400, "大象精", "运木头", 300));
        monsters.add(new Monster(500, "白骨精", "吐烟雾", 500));

        //创建集合返回查询到的monster集合
        List<Monster> findMonsters = new ArrayList<>();
        //遍历monster集合，将符合条件的放到findMonster集合中
        for (Monster monster : monsters) {
            if (monster.getName().contains(name)) {
                findMonsters.add(monster);
            }
        }
        return findMonsters;
    }

    @Override
    public boolean login(String name) {
        //模拟DB
        if ("白骨精".equals(name)) {
            return true;
        } else {
            return false;
        }
    }
}
