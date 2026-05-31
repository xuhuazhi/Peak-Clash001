package com.xiguazhi.ui;

import com.xiguazhi.domain.User;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Scanner;

public class Login {
    //这个方法表示就是登录注册的主页面，是以控制台的形式进行展示的
    public void start() {
        System.out.println("欢迎来到登录注册页面");
        //定义一个集合，用于保存用户信息
        ArrayList<User> users = new ArrayList<>();

          while (true) {
            System.out.println("╔════════════════════════════════╗");
            System.out.println("    🎮 欢迎来到巅峰对决 🎮   ");
            System.out.println("╚════════════════════════════════╝");
            System.out.println("请选择操作：1登录 2注册 3退出");

            Scanner sc = new Scanner(System.in);
            String choose = sc.next();
            switch (choose) {
                case "1" -> login(users);
                case "2" -> register(users);//容器：集合（可长、可短），数据库，数组
                case "3" -> {
                    System.out.println("该用户选择了退出操作");
                    System.exit(0);
                }
                case "1008611" -> {
                    FightingGame fg = new FightingGame();
                    fg.GameStart("xhz");
                }
                default -> System.out.println("该输入错误，请输入正确的选项");
            }

        }
    }
        //用户登录的操作
        public void login (ArrayList<User>  users) {
            System.out.println("该用户选择了用户登录操作");
            //登录的步骤：
            //1. 键盘录入用户名
            //2. 键盘录入密码
            //3. 键盘录入验证码
            //4. 登录最多重试三次，三次错误账号锁定
            /*验证要求：
             用户名如果未注册提示：用户名未注册，请先注册

             用户被锁定提示：用户xxx已经锁定，请联系管理员客服：XXX-XXXXX

             验证码错误提示：验证码输入错误，请重新输入，并生成一个新的验证码

             判断用户名和密码是否正确，有3次机会，满3次账户锁定。
             */
          //登录的完整逻辑
            //判断用户名是否存在
            //不存在:提醒注册
            //存在：禁用，联系客服
            //存在:验证验证码是否正确
            //验证密码是否正确（三次）
            //1.键盘录入验证码
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入用户名：");
            String username = sc.next();

            //检查用户是否存在
            boolean found = false;
            for (User u : users) {
                if (username.equals(u.getUsername())) {
                    found = true;
                    if (!u.isStatus()) {
                        System.out.println("用户" + username + "已经锁定，请联系管理员客服：XXX-XXXXX");
                        return ;
                    }
                    else
                    {
                      //获取验证码
                      String code = getCode();
                      System.out.println("验证码为：" + code);
                      System.out.println("请输入验证码：");
                      String codeInput = sc.next();
                      while (!code.equals(codeInput)) {
                        System.out.println("验证码输入错误，请重新输入，并生成一个新的验证码");
                        code = getCode();
                        System.out.println("新的验证码为：" + code);
                        codeInput = sc.next();
                    }
                      System.out.println("验证码正确");
                      //输入密码有三次机会，否则就锁定账号
                        for (int i = 0; i < 3; i++) {
                            System.out.println("请输入密码：");
                            String password = sc.next();
                            if (password.equals(u.getPassword())) {
                                System.out.println("登录成功");
                                System.out.println("尊敬的" + username + "用户，您好！！！");
                                FightingGame fg = new FightingGame();
                                fg.GameStart(username);
                                return ;
                            }
                            System.out.println("密码输入错误，请重新输入");
                        }
                        System.out.println("密码输入错误次数过多，账户锁定");
                        u.setStatus(false);
                        return ;
                    }
                }


            }
            if (!found) {
                System.out.println("用户" + username + "未注册，请先注册");
            }

                }

        //用户注册的操作
        public void register (ArrayList<User>  users) {
            System.out.println("该用户选择了用户注册操作");
            //注册的步骤：
           //1.创建User对象（空参构造方法）
           User u = new User();

             // 2.键盘录入用户名（进行校验是否符合要求）
            //用户名唯一
            // 长度必须在3 ~ 16位
            // 只能由字母、数字组成，不能是纯数字
            System.out.println("请输入用户名：");
            String username = new Scanner(System.in).next();
            while (!username.matches("(?=.*[a-zA-Z])[a-zA-Z0-9]{3,16}")) {
                System.out.println("用户名格式错误，请重新输入：");
                username = new Scanner(System.in).next();
            }
             // 3.键盘录入密码（进行验证是否符合要求）
           // 3，密码要求
           // 长度3 ~ 8位
           // 只能是字母加数字的组合，不能有其他字母
            //输入两次，两次输入的密码要一致
            System.out.println("请输入密码：");
            String password1= new Scanner(System.in).next();
            System.out.println("请再次输入密码：");
            String password2 = new Scanner(System.in).next();
            while (!password1.equals(password2)) {
                System.out.println("两次输入的密码不一致，请重新输入：");
                password1 = new Scanner(System.in).next();
                password2 = new Scanner(System.in).next();
            }
            while (!password1.matches("[a-zA-Z0-9]{3,8}")) {
                System.out.println("密码格式错误，请重新输入：");
                password1 = new Scanner(System.in).next();
            }
            // 4. 将用户名和密码设置到用户对象中
            u.setUsername(username);
            u.setPassword(password1);
            // 5.将用户对象添加到集合中
            users.add(u);
              // 6.提示用户注册成功
            System.out.println("用户注册成功");





    }
    //获取验证码
    public static String getCode() {
        ArrayList<Character> list = new ArrayList<>();
        for (int i = 0; i <= 25; i++) {
            list.add((char) (i + 'a'));
            list.add((char) (i + 'A'));

        }
        //从集合当中随机抽取1个字母（四次）
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int index = (int) (Math.random() * list.size());
            char ch = list.get(index);
            sb.append(ch);

        }
        //随机生成一个数字，然后拼接到sb中
        int index = (int) (Math.random() * 10);
        //数字的位置是任意的，随机生成一个位置
        int numIndex = (int) (Math.random() * sb.length());
        sb.insert(numIndex, index);
        return sb.toString();//将StringBuilder（可变）转换为String类型(不可变)
    }




}
