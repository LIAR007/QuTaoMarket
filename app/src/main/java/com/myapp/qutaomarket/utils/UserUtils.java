package com.myapp.qutaomarket.utils;

import com.myapp.qutaomarket.model.db.User;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

//获取用户信息的工具类
public class UserUtils {
    //获取当前登录用户的信息
    public static User getCurrentUser(){
        User user = new User();
        int id = -1;
        //返回登录的用户数据
        List<User> users = LitePal.findAll(User.class);
        for(User u : users){
            if(u.isLogin()){
                id = u.getId();
            }
        }
        user = LitePal.find(User.class, id);
        return user;
    }

}
