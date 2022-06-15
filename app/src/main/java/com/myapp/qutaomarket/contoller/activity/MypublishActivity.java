package com.myapp.qutaomarket.contoller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.fragment.MyPublishBuyFragment;
import com.myapp.qutaomarket.contoller.fragment.MyPublishGoodsFragment;

public class MypublishActivity extends AppCompatActivity {

    //定义变量
    private MyPublishGoodsFragment myPublishGoodsFragment;

    private MyPublishBuyFragment myPublishBuyFragment;

    private ImageView iv_mypublish_back;

    private RelativeLayout rl_mypublish_mypub,rl_mypublish_mybuy;

    private TextView tv_mypublish_mypub,tv_mypublish_mybuy;

    //定义颜色值
    private int Black = 0xFF000000;
    private int Red =0xFFFF0000;

    //定义监听变量
    private MypublishActivity.onClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypublish);

        //初始化视图
        initView();
        //初始化状态
        initState();
    }

    //初始化视图
    private void initView() {

        //初始化两个Fragment
        myPublishGoodsFragment = new MyPublishGoodsFragment();
        myPublishBuyFragment = new MyPublishBuyFragment();

        //初始化变量
        rl_mypublish_mypub = (RelativeLayout)findViewById(R.id.rl_mypublish_mypub);
        rl_mypublish_mybuy = (RelativeLayout)findViewById(R.id.rl_mypublish_mybuy);
        tv_mypublish_mypub = (TextView)findViewById(R.id.tv_mypublish_mypub);
        tv_mypublish_mybuy = (TextView)findViewById(R.id.tv_mypublish_mybuy);
        iv_mypublish_back = (ImageView)findViewById(R.id.iv_mypublish_back);

        //设置监听
        listener = new onClickListener();
        rl_mypublish_mypub.setOnClickListener(listener);
        rl_mypublish_mybuy.setOnClickListener(listener);
        iv_mypublish_back.setOnClickListener(listener);

        //清空选中
        clearChoice();
        //设置fragment
        switchFragment(myPublishGoodsFragment);
    }

    //监听事件处理
    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_mypublish_back:
                    finish();
                    break;
                case R.id.rl_mypublish_mypub:
                    clearChoice();
                    tv_mypublish_mypub.setTextColor(Red);
                    //实现切换fragment
                    switchFragment(myPublishGoodsFragment);
                    break;
                case R.id.rl_mypublish_mybuy:
                    clearChoice();
                    tv_mypublish_mybuy.setTextColor(Red);
                    //实现切换fragment
                    switchFragment(myPublishBuyFragment);
                    break;
            }
        }
    }

    //切换fragment
    private void switchFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.beginTransaction().replace(R.id.fragment_mypublish_list, fragment).commit();
    }

    //初始化状态
    private void initState(){
        tv_mypublish_mypub.setTextColor(Red);
        tv_mypublish_mybuy.setTextColor(Black);
    }

    //清空选中
    private void clearChoice(){
        tv_mypublish_mypub.setTextColor(Black);
        tv_mypublish_mybuy.setTextColor(Black);
    }
}
