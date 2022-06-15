package com.myapp.qutaomarket.model;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//数据模型层全局类，控制模型层和控制层的数据交互
public class Model {
    //创建上下文对象
    private Context mContext;
    //创建线程池
    private ExecutorService executorService = Executors.newCachedThreadPool();
    //创建对象
    private static Model model = new Model();
    //私有化构造
    private Model(){
    }
    //获取单例对象
    public static Model getInstance(){
        return model;
    }
    //初始化方法
    public void init(Context context){
        mContext = context;
    }
    //获取全局线程池对象
    public ExecutorService getGlobalThreadPool(){
        return executorService;
    }

}
