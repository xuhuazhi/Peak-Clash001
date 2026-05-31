package com.xiguazhi.ui;

import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import com.xiguazhi.domain.Character;
import com.xiguazhi.domain.HeroCharacter;
import com.xiguazhi.domain.EnemyCharacter;
import com.xiguazhi.domain.Skills;

public class FightingGame {
    private Scanner sc = new Scanner(System.in);
    private Random rand = new Random();

    public void GameStart(String username) {
        System.out.println("╔════════════════════════════════╗");
        System.out.println("  🎮  " + username + " 欢迎来到巅峰对决  🎮  ");
        System.out.println("╚════════════════════════════════╝");

        HeroCharacter hero = createPlayer(username);
        System.out.println("\n========== 英雄创建成功 ==========");
        hero.show();
        printSkills(hero);
        System.out.println("==================================");

        // 战斗循环
        while (hero.isAlive()) {
            EnemyCharacter enemy = createEnemy();
            System.out.println("\n========== 敌人出现了 ==========");
            enemy.show();
            printSkills(enemy);
            System.out.println("==================================");

            // 单场战斗
            boolean won = battle(hero, enemy);
            if (!won) {
                System.out.println("\n💀 游戏结束！" + hero.name + "倒在了第" + (hero.wins + 1) + "场战斗...");
                return;
            }
            hero.addWin();
            enemy.onDefeated();
            System.out.println("\n当前胜场：" + hero.wins + "  |  当前等级：" + hero.level);
            //这里设置输入y继续下一场，输入n退出
            System.out.println("输入y继续下一场，输入n退出...");
            if (sc.next().equals("n")) {
                System.out.println("游戏结束，最终胜场：" + hero.wins + "，等级：" + hero.level);
                return;
            }
            //英雄每打赢一把加100hp和50mb
            hero.recover(100, 50); // 英雄每打赢一把加100hp和50mb

        }
    }

    // 单场战斗，返回true=玩家胜
    private boolean battle(HeroCharacter hero, EnemyCharacter enemy) {
        while (hero.isAlive() && enemy.isAlive()) {
            // 1. 显示双方状态
            System.out.println("\n╔════════════════════════════════╗");
            hero.showStatus();
            enemy.showStatus();
            System.out.println("╚════════════════════════════════╝");

            // 2. 玩家回合
            playerTurn(hero, enemy);
            if (!enemy.isAlive()) {
                System.out.println("\n🎉 战斗胜利！" + hero.name + "击败了" + enemy.name + "！");
                return true;
            }

            // 4. 敌人回合
            enemyTurn(enemy, hero);
            if (!hero.isAlive()) {
                return false;
            }
        }
        return hero.isAlive();
    }

    // 玩家回合
    private void playerTurn(HeroCharacter hero, EnemyCharacter enemy) {
        Skills s1 = hero.skillsList.get(0);
        Skills s2 = hero.skillsList.get(1);
        System.out.println("\n你的回合！选择行动：");
        System.out.println("  1.普通攻击");
        System.out.println("  2.【" + s1.name + "】(伤害+" + s1.damage + " 蓝耗" + s1.MB + ")");
        System.out.println("  3.【" + s2.name + "】(伤害+" + s2.damage + " 蓝耗" + s2.MB + ")");
        int action = sc.nextInt();
        switch (action) {
            case 1:
                hero.normalAttack(enemy);
                break;
            case 2:
                hero.useSkill(s1, enemy);
                break;
            case 3:
                hero.useSkill(s2, enemy);
                break;
            default:
                System.out.println("无效选择，执行普通攻击");
                hero.normalAttack(enemy);
        }
    }

    // 敌人回合（随机AI）
    private void enemyTurn(EnemyCharacter enemy, HeroCharacter hero) {
        System.out.println("\n" + enemy.name + "的回合！");
        int skillCount = enemy.skillsList.size();

        // 选择行动：0=普攻, 1=技能1, 2=技能2
        int action = rand.nextInt(1 + skillCount);

        if (action == 0) {
            enemy.normalAttack(hero);
        } else if (action == 1 && skillCount >= 1) {
            Skills s = enemy.skillsList.get(0);
            if (enemy.MB >= s.MB) {
                enemy.useSkill(s, hero);
            } else {
                enemy.normalAttack(hero);
            }
        } else if (action == 2 && skillCount >= 2) {
            Skills s = enemy.skillsList.get(1);
            if (enemy.MB >= s.MB) {
                enemy.useSkill(s, hero);
            } else {
                enemy.normalAttack(hero);
            }
        } else {
            enemy.normalAttack(hero);
        }
    }

    // 打印技能列表
    private void printSkills(Character c) {
        if (!(c instanceof HeroCharacter) && !(c instanceof EnemyCharacter)) return;
        ArrayList<Skills> list;
        if (c instanceof HeroCharacter) list = ((HeroCharacter) c).skillsList;
        else list = ((EnemyCharacter) c).skillsList;

        System.out.println("技能列表：");
        for (int i = 0; i < list.size(); i++) {
            Skills s = list.get(i);
            System.out.println("  " + (i + 1) + "." + s.name + "（伤害:" + s.damage
                    + " 蓝耗:" + s.MB + "）— " + s.description);
        }
    }

    // 创建玩家角色
    public HeroCharacter createPlayer(String username) {
        System.out.println("创建您的角色");
        System.out.println("您的角色名称：" + username);
        System.out.println("请选择角色：1.弓箭女皇 2.野蛮人之王 3.大守护者 4.飞盾战神 5.亡灵王子 6.飞龙公爵");
        int choose = sc.nextInt();
        HeroCharacter hero = new HeroCharacter();
        hero.initHero(choose);
        return hero;
    }

    // 随机生成敌人
    public EnemyCharacter createEnemy() {
        return EnemyCharacter.randomEnemy();
    }
}
