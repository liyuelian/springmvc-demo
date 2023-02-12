package com.li.service;

import com.li.entity.Monster;

import java.util.List;

/**
 * @author 李
 * @version 1.0
 */
public interface MonsterService {
    //增加方法，返回Monster列表
    public List<Monster> listMonster();

    //增加方法，通过传入的名字返回 monster列表
    public List<Monster> findMonsterByName(String name);
}
