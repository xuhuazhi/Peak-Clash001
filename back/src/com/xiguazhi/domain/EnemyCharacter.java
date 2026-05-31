package com.xiguazhi.domain;

import java.util.ArrayList;
import java.util.Random;

public class EnemyCharacter extends Character {
    public ArrayList<Skills> skillsList;
    boolean defending;
    public static int difficultyLevel = 0; // 全局难度等级

    public EnemyCharacter() {
        super();
        skillsList = new ArrayList<>();
    }

    public EnemyCharacter(String name, int HP, int attack, int defense, int MB) {
        super(name, HP, attack, defense, MB);
        skillsList = new ArrayList<>();
    }

    // 根据编号初始化敌人
    public void initEnemy(int type) {
        switch (type) {
            case 1: // 哥布林
                this.name = "哥布林";
                this.HP = 200;
                this.maxHP = 200;
                this.attack = 80;
                this.defense = 20;
                this.MB = 60;
                skillsList.add(new Skills("偷袭", 50, 20, "快速偷袭，造成50点伤害"));
                break;
            case 2: // 骷髅战士
                this.name = "骷髅战士";
                this.HP = 450;
                this.maxHP = 450;
                this.attack = 100;
                this.defense = 40;
                this.MB = 90;
                skillsList.add(new Skills("骨盾", 0, 40, "进入防御状态，伤害减半"));
                skillsList.add(new Skills("亡者斩击", 120, 50, "奋力一击，造成120点伤害"));
                break;
            case 3: // 暗黑骑士
                this.name = "暗黑骑士";
                this.HP = 700;
                this.maxHP = 700;
                this.attack = 130;
                this.defense = 60;
                this.MB = 110;
                skillsList.add(new Skills("暗影冲锋", 100, 50, "暗影冲锋，造成100点伤害"));
                skillsList.add(new Skills("黑暗波动", 180, 80, "释放黑暗波动，造成180点伤害"));
                break;
            case 4: // 石像鬼
                this.name = "石像鬼";
                this.HP = 350;
                this.maxHP = 350;
                this.attack = 90;
                this.defense = 30;
                this.MB = 80;
                skillsList.add(new Skills("石化凝视", 80, 40, "石化凝视，造成80点伤害并减速"));
                skillsList.add(new Skills("俯冲攻击", 140, 60, "俯冲攻击，造成140点伤害"));
                break;
            case 5: // 巨型蜘蛛
                this.name = "巨型蜘蛛";
                this.HP = 550;
                this.maxHP = 550;
                this.attack = 110;
                this.defense = 40;
                this.MB = 100;
                skillsList.add(new Skills("毒液喷射", 100, 40, "喷射毒液，造成100点伤害并中毒"));
                skillsList.add(new Skills("蛛网束缚", 80, 50, "蛛网束缚，造成80点伤害并限制行动"));
                break;
            case 6: // 火龙 - Boss级
                this.name = "火龙";
                this.HP = 900;
                this.maxHP = 900;
                this.attack = 160;
                this.defense = 70;
                this.MB = 140;
                skillsList.add(new Skills("火焰吐息", 180, 60, "喷吐火焰，造成180点范围伤害"));
                skillsList.add(new Skills("龙之怒", 250, 90, "龙之怒，造成250点巨额伤害"));
                break;
            default:
                System.out.println("无效的敌人编号！");
                break;
        }

        // 根据难度等级缩放属性
        if (difficultyLevel > 0) {
            double scale = 1.0 + difficultyLevel * 0.1;
            this.HP = (int)(this.HP * scale);
            this.maxHP = (int)(this.maxHP * scale);
            this.attack = (int)(this.attack * scale);
            this.defense = (int)(this.defense * scale);
        }
        this.maxMB = this.MB;
    }

    // 全局难度+1
    public static void increaseDifficulty() {
        difficultyLevel++;
        System.out.println("⚠ 敌人势力增强了！当前难度等级：" + difficultyLevel);
    }

    // 敌人战败时调用
    public void onDefeated() {
        wins++;
        increaseDifficulty();
    }

    // 随机生成一个敌人
    public static EnemyCharacter randomEnemy() {
        EnemyCharacter enemy = new EnemyCharacter();
        Random rand = new Random();
        int type = rand.nextInt(6) + 1; // 1~6
        enemy.initEnemy(type);
        return enemy;
    }

    @Override
    public void suffer(int damage) {
        //如果敌人处于防御状态的话，伤害减半
        if (defending) {
            damage = damage / 2 > 1 ? damage / 2 : 1;
            defending = false;
        }
        //扣血
        super.suffer(damage);
    }
}
