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
        monsters.add(new Monster(100, "猫妖", "撕咬", 800));
        return monsters;
    }
}
