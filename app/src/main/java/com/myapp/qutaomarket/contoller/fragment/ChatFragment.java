package com.myapp.qutaomarket.contoller.fragment;

import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.myapp.qutaomarket.Constant;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.model.db.User;
import com.myapp.qutaomarket.utils.UserUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentHelper {

    @Override
    protected void setUpView() {
        super.setUpView();
        setChatFragmentHelper(this);
    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        User user = UserUtils.getCurrentUser();
        try {
            String imagePath = URLEncoder.encode(user.getHeadPhoto().trim(), "utf-8");
            //设置要发送扩展消息用户昵称
            message.setAttribute(Constant.USER_NAME, user.getNickName());
            //设置要发送扩展消息用户头像
            String headImgUrl=getResources().getString(R.string.burl)+ "Image_Servlet?" + imagePath;
            message.setAttribute(Constant.HEAD_IMAGE_URL, headImgUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {

    }

    @Override
    public void onAvatarLongClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        return false;
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return null;
    }
}
