package com.myapp.qutaomarket;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.myapp.qutaomarket.model.dao.ChatListDao;

import org.litepal.LitePal;

public class QTApplication extends Application {
    //创建上下文对象
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取全局上下文对象，对全局上下文进行管理
        mContext = getApplicationContext();
        //初始化百度地图SDK
        SDKInitializer.initialize(mContext);
        //初始化EaseUI
        HxEaseuiHelper.getInstance().init(mContext);
        //初始化数据库
        LitePal.initialize(mContext);
    }
    //创建全局获取上下文的方法
    public static Context getmContext(){
        return mContext;
    }
}
