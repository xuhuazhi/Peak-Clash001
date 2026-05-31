package com.xiguazhi.domain;

public class Skills {
    //技能名称 伤害 蓝耗 技能描述
    public String name;
    public int damage;
    public int MB;
    public String description;

    public Skills() {
    }

    public Skills(String name, int damage, int MB, String description) {
        this.name = name;
        this.damage = damage;
        this.MB = MB;
        this.description = description;
    }
}

