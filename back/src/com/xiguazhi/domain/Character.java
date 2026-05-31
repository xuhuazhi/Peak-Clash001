package com.xiguazhi.domain;

public class Character {
    //属性：姓名，血量，最大血量，攻击力，防御力，蓝条
  public String name;
  public int HP;
  public int maxHP;
  public int attack;
  public int defense;
  public int MB;
  public int maxMB;
  public int wins;
  public int level = 1;


 public Character() {
 }
//刚创建的时候，血量等于最大血量
 public Character(String name, int HP, int attack, int defense, int MB) {
  this.name = name;
  this.HP = HP;
  this.maxHP = HP;
  this.attack = attack;
  this.defense = defense;
  this.MB = MB;
  this.maxMB = MB;
 }
 //判断是否存活
    public boolean isAlive() {
        return HP > 0;
    }
    //恢复血量
    //作用：恢复了多少血量
    //参数：恢复的血量
    public void recover(int amount,int MBamount) {
        HP += amount;
        MB += MBamount;
        if (HP > maxHP) {
            HP = maxHP;
        }
        if (MB > maxMB) {
            MB = maxMB;
        }
    }
    //受到伤害
    //作用：受到多少伤害，最后还剩多少血量
    //参数：受到的伤害
    public void suffer(int damage) {
        HP -= damage;
        if (HP < 0) {
            HP = 0;
        }
        //做一个判断，如果血量小于0，则提示死亡
        if (HP == 0) {
            System.out.println(name + "已死亡！");
        }
    }
    //消耗的蓝条
    public void consumeMB(int amount) {
        MB -= amount;
        if (MB < 0) {
            MB = 0;
        }
        if (MB == 0) {
            System.out.println(name + "蓝条已耗尽,无法使用技能！");
        }
    }
    //展示一下角色的属性
    public void show() {
        System.out.println("角色名称：" + name);
        System.out.println("血量：" + HP + "/" + maxHP);
        System.out.println("攻击力：" + attack);
        System.out.println("防御力：" + defense);
        System.out.println("蓝条：" + MB);
        System.out.println("等级：" + level + "  胜场：" + wins);
    }

    // 伤害计算
    public int calcDamage(int skillDamage, Character target) {
        int damage = this.attack + skillDamage - target.defense;
        return Math.max(damage, 1);
    }

    // 普通攻击（无技能伤害加成）
    public void normalAttack(Character target) {
        int damage = calcDamage(0, target);
        System.out.println(name + "普通攻击，造成 " + damage + " 点伤害！");
        target.suffer(damage);
    }

    // 渲染进度条
    private String renderBar(int current, int max) {
        int barLen = 20;
        int filled = (int)((double)current / max * barLen);
        if (filled < 0) filled = 0;
        if (filled > barLen) filled = barLen;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLen; i++) {
            bar.append(i < filled ? "█" : " ");
        }
        bar.append("]");
        return bar.toString();
    }

    // 显示战斗状态（血条+蓝条）
    public void showStatus() {
        System.out.println(name + ": " + renderBar(HP, maxHP) + " " + HP + "/" + maxHP + " HP");
        System.out.println("      " + renderBar(MB, maxMB > 0 ? maxMB : 1) + " " + MB + "/" + (maxMB > 0 ? maxMB : MB) + " MB");
    }

    // 对目标使用技能
    public void useSkill(Skills skill, Character target) {
        if (this.MB < skill.MB) {
            System.out.println(name + "蓝量不足，无法使用【" + skill.name + "】！");
            return;
        }
        consumeMB(skill.MB);
        int damage = calcDamage(skill.damage, target);
        System.out.println(name + "使用【" + skill.name + "】，造成 " + damage + " 点伤害！");
        target.suffer(damage);
    }

    //升级：提升属性并回满血
    public void levelUp() {
        level++;
        maxHP += 80;
        HP = maxHP;
        attack += 15;
        defense += 5;
        System.out.println("🎉 " + name + " 升级了！当前等级：" + level);
        System.out.println("   生命+" + 80 + "  攻击+" + 15 + "  防御+" + 5);
    }

}
