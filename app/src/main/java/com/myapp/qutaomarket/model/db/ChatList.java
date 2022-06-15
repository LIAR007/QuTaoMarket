package com.myapp.qutaomarket.model.db;

import org.litepal.crud.LitePalSupport;

//储存聊天列表
public class ChatList extends LitePalSupport {
    //列表id
    private int id;
    //用户账号，用来确定哪个账号的列表
    private String account;
    //聊天对象的环信id
    private String hxid;
    //聊天对象的昵称
    private String nickname;
    //聊天对象的头像
    private String avatar;
    //get和set方法

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getHxid() {
        return hxid;
    }

    public void setHxid(String hxid) {
        this.hxid = hxid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
