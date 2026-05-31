package com.xiguazhi.domain;

public class User {
    //id、用户名、密码、状态
    private String id;
    private String username;
    private String password;
    private boolean status;

    public User() {
        //分配id给用户
        id = createId();
        //修改状态为true
        status = true;
    }

    public User( String username, String password) {
        id = createId();
        this.username = username;
        this.password = password;
        status = true;
    }

    //用户无法设置，是自动生成的，格式为：xuguazhi+5位数字的随机数
    public String createId() {
        return "xuguazhi" + (int) (Math.random() * 100000);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
