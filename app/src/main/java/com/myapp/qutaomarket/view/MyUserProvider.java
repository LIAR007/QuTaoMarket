package com.myapp.qutaomarket.view;

import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.HashMap;
import java.util.Map;

public class MyUserProvider implements EaseUI.EaseUserProfileProvider {
    private static MyUserProvider myUserProvider;
    // 设计成了单类
    private Map<String,EaseUser> userList = new HashMap<>();
    // 我服务器上获得头像的url
    private String ImgUrl="";
    @Override
    public EaseUser getUser(String username) {
        if(userList.containsKey(username))
            //有就返回这个对象
            return userList.get(username);
        System.out.println("error ： 没有 数据" + username);
        return null;
    }
    // 封装了一下
    public void setUser(String username,String nickname, String imageUrl){
        if(!userList.containsKey(username)) {
            EaseUser easeUser = new EaseUser(username);
            userList.put(username,easeUser);
        }
        EaseUser easeUser = getUser(username);
        // 不用怀疑,环信的easerUser的父类有一个setNickname 的方法可以用来设置昵称，直接调用就好。。
        easeUser.setNickname(nickname);
        // 同理，设置一个图像的url就好，因为他加载头像是使用glide加载的
        easeUser.setAvatar(imageUrl+username);
    }
    // 按照你喜欢的修改一下初始化函数吧，
    private MyUserProvider(){
        System.out.println("init myTestProfileProvider");
    }
    // 获取单类。。
    public static MyUserProvider getInstance() {
        if (myUserProvider == null) {
            myUserProvider = new MyUserProvider();
        }
        return myUserProvider;
    }
}
