package com.myapp.qutaomarket.model.dao;

import com.hyphenate.easeui.domain.EaseUser;
import com.myapp.qutaomarket.model.db.ChatList;
import com.myapp.qutaomarket.model.db.User;
import com.myapp.qutaomarket.utils.UserUtils;

import org.litepal.LitePal;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

//聊天列表操作类
public class ChatListDao {
    //储存聊天列表
    public void saveChatLiat(List<EaseUser> chatList){
        User user = UserUtils.getCurrentUser();
        ChatList contact = new ChatList();
        for(EaseUser easeUser : chatList){
            List<ChatList> contactList = LitePal.where("account = ? and hxid = ?", user.getAccount(), easeUser.getUsername()).find(ChatList.class);
            if(contactList.isEmpty()) {
                contact.setAccount(user.getAccount());
                contact.setHxid(easeUser.getUsername());
                contact.setNickname(easeUser.getNickname());
                contact.setAvatar(easeUser.getAvatar());
                contact.save();
            }else {
                contact.setNickname(easeUser.getNickname());
                contact.setAvatar(easeUser.getAvatar());
                contact.updateAll("account = ? and hxid = ?", user.getAccount(), easeUser.getUsername());
            }
        }
    }

    //获取聊天列表
    public List<ChatList> getChatList(){
        User user = UserUtils.getCurrentUser();
        List<ChatList> chatLists = LitePal.where("account = ?", user.getAccount()).find(ChatList.class);
        return chatLists;
    }

    //获取所有EaseUser对象
    public static Map<String, EaseUser> getContactList(){
        User user = UserUtils.getCurrentUser();
        Map<String, EaseUser> users = new Hashtable<String, EaseUser>();
        List<ChatList> chatLists = LitePal.where("account = ?", user.getAccount()).find(ChatList.class);
        for(ChatList chatList : chatLists){
            EaseUser easeUser = new EaseUser(chatList.getHxid());
            easeUser.setNickname(chatList.getNickname());
            easeUser.setAvatar(chatList.getAvatar());
            users.put(chatList.getHxid(), easeUser);
        }
        return users;
    }

    //储存单个对话联系人
    public void saveChat(EaseUser easeUser){
        User user = UserUtils.getCurrentUser();
        ChatList contact = new ChatList();
        contact.setAccount(user.getAccount());
        contact.setHxid(easeUser.getUsername());
        contact.setNickname(easeUser.getNickname());
        contact.setAvatar(easeUser.getAvatar());
        contact.save();
    }

    //更新联系人数据
    public void updateChat(EaseUser easeUser){
        ChatList contact = new ChatList();
        contact.setNickname(easeUser.getNickname());
        contact.setAvatar(easeUser.getAvatar());
        contact.updateAll("hxid = ?", easeUser.getUsername());
    }

    //获取单个联系人信息
    public ChatList getContact(String hxid){
        User user = UserUtils.getCurrentUser();
        ChatList chatList = (ChatList) LitePal.where("account = ? and hxid = ?", user.getAccount(), hxid).find(ChatList.class);
        return chatList;
    }

    //删除单个聊天对象
    public void deleteChat(String hxid){
        User user = UserUtils.getCurrentUser();
        LitePal.deleteAll(ChatList.class, "account = ? and hxid = ?", user.getAccount(), hxid);
    }

    //删除单个账号的聊天列表
    public void deleteChatList(String account){
        LitePal.deleteAll(ChatList.class, "account = ?", account);
    }
}
