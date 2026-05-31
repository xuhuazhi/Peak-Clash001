package com.xiguazhi.domain;

import java.util.ArrayList;

public class HeroCharacter extends Character {
    public ArrayList<Skills> skillsList ;

    public HeroCharacter() {
        super();
        skillsList = new ArrayList<>();
    }
    public HeroCharacter(String name, int HP, int attack, int defense, int MB) {
        super(name, HP, attack, defense, MB);
        skillsList = new ArrayList<>();
    }

    // 根据选择编号初始化英雄的属性和技能
    public void initHero(int choose) {
        switch (choose) {
            case 1:
                this.name = "弓箭女皇";
                this.HP = 725;
                this.maxHP = 725;
                this.attack = 160;
                this.defense = 10;
                this.MB = 200;
                skillsList.add(new Skills("皇室披风", 200, 80, "隐身3.6秒，回血150，召5个弓箭手"));
                skillsList.add(new Skills("巨型箭矢", 500, 100, "单发高伤箭，点杀关键防御"));
                break;
            case 2:
                this.name = "野蛮人之王";
                this.HP = 1445;
                this.maxHP = 1445;
                this.attack = 102;
                this.defense = 20;
                this.MB = 180;
                skillsList.add(new Skills("铁拳", 150, 80, "回血200，增伤提速，召6个野蛮人"));
                skillsList.add(new Skills("震地金靴", 300, 100, "重击地面，伤建筑、小范围震晕"));
                break;
            case 3:
                this.name = "大守护者";
                this.HP = 850;
                this.maxHP = 850;
                this.attack = 43;
                this.defense = 15;
                this.MB = 300;
                skillsList.add(new Skills("永恒书卷", 0, 120, "范围内友军无敌3秒，自身回血90"));
                skillsList.add(new Skills("狂暴宝石", 80, 60, "光环友军小幅度增伤"));
                break;
            case 4:
                this.name = "飞盾战神";
                this.HP = 2508;
                this.maxHP = 2508;
                this.attack = 340;
                this.defense = 12;
                this.MB = 220;
                skillsList.add(new Skills("飞盾寻踪", 1250, 100, "掷盾打4个防御，每目标1250伤害"));
                skillsList.add(new Skills("火箭长矛", 200, 60, "前几次攻击附加额外伤害"));
                break;
            case 5:
                this.name = "亡灵王子";
                this.HP = 2600;
                this.maxHP = 2600;
                this.attack = 320;
                this.defense = 10;
                this.MB = 250;
                skillsList.add(new Skills("护卫木偶", 150, 70, "召2个护卫，隐身1秒，回血200"));
                skillsList.add(new Skills("暗黑魔球", 250, 90, "范围减速防御攻速，小幅增伤"));
                break;
            case 6:
                this.name = "飞龙公爵";
                this.HP = 9100;
                this.maxHP = 9100;
                this.attack = 304;
                this.defense = 18;
                this.MB = 220;
                skillsList.add(new Skills("烈焰之心", 200, 70, "被动回血，阵亡小范围爆炸"));
                skillsList.add(new Skills("赤焰风鼓", 350, 90, "锥形火焰，12格小范围AOE"));
                break;
            default:
                System.out.println("无效的英雄编号！");
                break;

        }
        this.maxMB = this.MB;
    }

    // 每胜利3场升级一次
    public void addWin() {
        wins++;
        if (wins % 3 == 0) {
            levelUp();
        }
    }
}
