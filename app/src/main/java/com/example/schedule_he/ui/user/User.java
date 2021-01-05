package com.example.schedule_he.ui.user;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {

    private  String username;
    private  String password;
    private  String sex;//性别   1/0   �?�?
    private  String universty;//大�dsds
    private  String myflag;//立flag  相当于个性签�? 或者是简�?

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUniversty() {
        return universty;
    }

    public void setUniversty(String universty) {
        this.universty = universty;
    }

    public String getMyflag() {
        return myflag;
    }

    public void setMyflag(String myflag) {
        this.myflag = myflag;
    }

}
