package com.myapp.qutaomarket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.text.TextUtils;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupReadAck;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.model.dao.ChatListDao;
import com.myapp.qutaomarket.model.db.User;
import com.myapp.qutaomarket.utils.UserUtils;
import com.myapp.qutaomarket.view.MyUserProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class HxEaseuiHelper {
    private static HxEaseuiHelper instance = null;
    protected EMMessageListener messageListener = null;
    private Context appContext;
    private String username;
    private EaseUI easeUI;
    private Map<String, EaseUser> contactList;

    private String TAG="HxEaseuiHelper";

    public synchronized static HxEaseuiHelper getInstance() {
        if (instance == null) {
            instance = new HxEaseuiHelper();
        }
        return instance;
    }


    public void init(Context context) {
        EMOptions emOptions = new EMOptions();
        //默认添加好友时不需要验证,true:自动验证，false:手动验证
        emOptions.setAcceptInvitationAlways(true);
        //群邀请，ture:自动验证,false:手动验证
        emOptions.setAutoAcceptGroupInvitation(true);
        //初始化EaseUI
        EaseUI.getInstance().init(context , null);
        // 设置自己实现的提供用户名和昵称的provider
        EaseUI.getInstance().setUserProfileProvider(MyUserProvider.getInstance());
        //初始化数据模型层
        Model.getInstance().init(context);
        //EaseUI初始化
        if(EaseUI.getInstance().init(context, emOptions)){
            appContext = context;
            appContext = context;

            //获取easeui实例
            easeUI = EaseUI.getInstance();
            //初始化easeui
            easeUI.init(appContext,emOptions);
            //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
            EMClient.getInstance().setDebugMode(true);
            setEaseUIProviders();
            //设置全局监听
             setGlobalListeners();
        }

    }

    protected void setEaseUIProviders() {
        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
    }

    private EaseUser getUserInfo(String username){
        //获取 EaseUser实例, 这里从内存中读取
        //如果你是从服务器中读读取到的，最好在本地进行缓存
        EaseUser easeUser = null;
        String imagePath = "null";
        //如果用户是本人，就设置自己的头像
        if(username.equals(EMClient.getInstance().getCurrentUser())){
            User user = UserUtils.getCurrentUser();
            try {
                easeUser=new EaseUser(username);
                imagePath = URLEncoder.encode(user.getHeadPhoto().trim(), "utf-8");
                if("null".equals(imagePath)){
                    easeUser.setAvatar("null");
                }else {
                    String imageUrl = appContext.getResources().getString(R.string.burl)+ "Image_Servlet?" + imagePath;
                    easeUser.setAvatar(imageUrl);
                }
                easeUser.setNickname(user.getNickName());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return easeUser;
        }
        //收到别人的消息，设置别人的头像
        if (contactList!=null && contactList.containsKey(username)){
            easeUser=contactList.get(username);
        }else { //如果内存中没有，则将本地数据库中的取出到内存中
            contactList = ChatListDao.getContactList();
            easeUser=contactList.get(username);
            System.out.println("11111111111"+contactList);
        }

        //如果用户不是你的联系人，则进行初始化
        if(easeUser == null){
            easeUser = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(easeUser);
        }else {
            if (TextUtils.isEmpty(easeUser.getNickname())){//如果名字为空，则显示环信号码
                easeUser.setNickname(easeUser.getUsername());
            }
        }
        return easeUser;
    }

    /**
     *获取所有的联系人信息
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        // return a empty non-null object to avoid app crash
        if(contactList == null){
            return new Hashtable<String, EaseUser>();
        }
        return contactList;
    }
    /**
     * set global listener
     */
    protected void setGlobalListeners(){
        registerMessageListener();
    }

    /**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     */
    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());

                    //接收并处理扩展消息
                    String userName=message.getStringAttribute(Constant.USER_NAME,"");
                    String userId=message.getStringAttribute(Constant.USER_ID,"");
                    String userPic=message.getStringAttribute(Constant.HEAD_IMAGE_URL,"");
                    String hxIdFrom=message.getFrom();
                    System.out.println("helper接收到的用户名："+userName+"helper接收到的id："+userId+"helper头像："+userPic);
                    EaseUser easeUser=new EaseUser(hxIdFrom);
                    easeUser.setAvatar(userPic);
                    easeUser.setNickname(userName);

                    //存入内存
                    getContactList();
                    contactList.put(hxIdFrom,easeUser);

                    //存入db
                    ChatListDao chatList=new ChatListDao();
                    List<EaseUser> users=new ArrayList<EaseUser>();
                    users.add(easeUser);
                    chatList.saveChatLiat(users);
                    //设置本地消息推送通知
                    if(!easeUI.hasForegroundActivies()){
                        getNotifier().vibrateAndPlayTone(message);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "receive command message");
                    //get message body
                    //end of red packet code
                    //获取扩展属性 此处省略
                    //maybe you need get extension of your message
                    //message.getStringAttribute("");
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }

            @Override
            public void onGroupMessageRead(List<EMGroupReadAck> list) {

            }

            @Override
            public void onReadAckForGroupMessageUpdated() {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }

            @Override
            public void onMessageRecalled(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }
    public EaseNotifier getNotifier(){
        return easeUI.getNotifier();
    }
}
