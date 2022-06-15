package com.myapp.qutaomarket.contoller.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.myapp.qutaomarket.QTApplication;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.adapter.FragmentAdapter;
import com.myapp.qutaomarket.contoller.fragment.F1Fragment;
import com.myapp.qutaomarket.contoller.fragment.F2Fragment;
import com.myapp.qutaomarket.contoller.fragment.F3Fragment;
import com.myapp.qutaomarket.contoller.fragment.F5Fragment;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    //定义五个Fragment
    private F1Fragment f1;         //主页面
    private F2Fragment f2;         //同城页面
    private F3Fragment f3;         //发布页面
//    private F4Fragment f4;         //消息页面
    private F5Fragment f5;         //我的页面

    //定义一个ViewPager容器
    private ViewPager vp_main_viewpager;
    private ArrayList<Fragment> fragmentsList;
    private FragmentAdapter mAdapter;

    //定义activity_mian中的控件变量
//    private LinearLayout ll_main_mainpage_layout,ll_main_local_layout,ll_main_publish_layout,ll_main_message_layout,ll_main_me_layout;
//
//    private TextView tv_main_mainpage_text,tv_main_local_text,tv_main_publish_text,tv_main_message_text,tv_main_me_text;
//
//    private ImageView iv_main_mainpage_image,iv_main_local_image,iv_main_publish_image,iv_main_message_image,iv_main_me_image;
    private LinearLayout ll_main_mainpage_layout,ll_main_local_layout,ll_main_publish_layout,ll_main_me_layout;

    private TextView tv_main_mainpage_text,tv_main_local_text,tv_main_publish_text,tv_main_me_text;

    private ImageView iv_main_mainpage_image,iv_main_local_image,iv_main_publish_image,iv_main_me_image;


    //定义颜色值
    private int Gray = 0xFF999999;
    private int Green =0xFF45C01A;

    //定义FragmentManager对象
    private FragmentManager fManager;

    //定义一个Onclick全局对象
    private MainActivity.OnClickListener onClickListener;
    private MainActivity.PageChangeListener pageChangeListener;

    //进度条
    private ProgressDialog pd;

    public static String myLocation = "";
    public static String myAddress = "";
    private LocationClient mLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fManager = getSupportFragmentManager();

        //初始化ViewPager
        initViewPager();
        //初始化控件
        initView();
        //初始化各个图标的状态
        initState();
        //初始化位置
        initLocation();
        mLocationClient.start();
    }

    //初始化位置
    private void initLocation(){
        //定义位置监听变量
        mLocationClient = new LocationClient(QTApplication.getmContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(10000);
        option.setIgnoreKillProcess(false);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
        //注册监听
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                myLocation = bdLocation.getCity().trim();
                myAddress = bdLocation.getCity().trim() + " " + bdLocation.getDistrict();
            }
        });
    }

    //初始化ViewPager
    private void initViewPager()
    {
        fragmentsList = new ArrayList<Fragment>();
        f1 = new F1Fragment();
        f2 = new F2Fragment();
        f3 = new F3Fragment();
//        f4 = new F4Fragment();
        f5 = new F5Fragment();
        fragmentsList.add(f1);
        fragmentsList.add(f2);
        fragmentsList.add(f3);
//        fragmentsList.add(f4);
        fragmentsList.add(f5);
        mAdapter = new FragmentAdapter(fManager,fragmentsList);
    }

    //初始化控件
    private void initView() {
        //ViewPager
        vp_main_viewpager = (ViewPager)findViewById(R.id.vp_main_viewpager);

        //LinearLayout
        ll_main_mainpage_layout = (LinearLayout)findViewById(R.id.ll_main_mainpage_layout);
        ll_main_local_layout = (LinearLayout)findViewById(R.id.ll_main_local_layout);
        ll_main_publish_layout = (LinearLayout)findViewById(R.id.ll_main_publish_layout);
//        ll_main_message_layout = (LinearLayout)findViewById(R.id.ll_main_message_layout);
        ll_main_me_layout = (LinearLayout)findViewById(R.id.ll_main_me_layout);

        //TextView
        tv_main_mainpage_text = (TextView)findViewById(R.id.tv_main_mainpage_text);
        tv_main_local_text = (TextView)findViewById(R.id.tv_main_local_text);
        tv_main_publish_text = (TextView)findViewById(R.id.tv_main_publish_text);
//        tv_main_message_text = (TextView)findViewById(R.id.tv_main_message_text);
        tv_main_me_text = (TextView)findViewById(R.id.tv_main_me_text);

        //ImageView
        iv_main_mainpage_image = (ImageView)findViewById(R.id.iv_main_mainpage_image);
        iv_main_local_image = (ImageView)findViewById(R.id.iv_main_local_image);
        iv_main_publish_image = (ImageView)findViewById(R.id.iv_main_publish_image);
//        iv_main_message_image = (ImageView)findViewById(R.id.iv_main_message_image);
        iv_main_me_image = (ImageView)findViewById(R.id.iv_main_me_image);

        //点击监听
        onClickListener = new OnClickListener();
        ll_main_mainpage_layout.setOnClickListener(onClickListener);
        ll_main_local_layout.setOnClickListener(onClickListener);
        ll_main_publish_layout.setOnClickListener(onClickListener);
//        ll_main_message_layout.setOnClickListener(onClickListener);
        ll_main_me_layout.setOnClickListener(onClickListener);

        //ViewPager滑动监听
        pageChangeListener = new PageChangeListener();
        vp_main_viewpager.setAdapter(mAdapter);
        vp_main_viewpager.setOffscreenPageLimit(0);      //设置预加载的页数
        vp_main_viewpager.setOnPageChangeListener(pageChangeListener);
    }

    //定义一个设置初始状态的方法
    private void initState()
    {
        tv_main_mainpage_text.setTextColor(Green);
        iv_main_mainpage_image.setImageResource(R.drawable.mainpage_botton_green);
        tv_main_local_text.setTextColor(Gray);
        iv_main_local_image.setImageResource(R.drawable.local_botton);
        tv_main_publish_text.setTextColor(Gray);
        iv_main_publish_image.setImageResource(R.drawable.publish_botton);
//        tv_main_message_text.setTextColor(Gray);
//        iv_main_message_image.setImageResource(R.drawable.message_botton);
        tv_main_me_text.setTextColor(Gray);
        iv_main_me_image.setImageResource(R.drawable.me_botton);
        vp_main_viewpager.setCurrentItem(0);
    }

    //点击事件处理
    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            clearChioce();
            iconChange(v.getId());
        }
    }
    //页面滑动事件处理
    private class PageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //state表示滑动过程中的三种状态，0滑动结束，1正在滑动，2滑动结束
            if(state == 2)
            {
                int i = vp_main_viewpager.getCurrentItem();
                clearChioce();
                iconChange(i);
            }
        }
    }

    //建立一个清空选中状态的方法
    private void clearChioce()
    {
        tv_main_mainpage_text.setTextColor(Gray);
        iv_main_mainpage_image.setImageResource(R.drawable.mainpage_botton);
        tv_main_local_text.setTextColor(Gray);
        iv_main_local_image.setImageResource(R.drawable.local_botton);
        tv_main_publish_text.setTextColor(Gray);
        iv_main_publish_image.setImageResource(R.drawable.publish_botton);
//        tv_main_message_text.setTextColor(Gray);
//        iv_main_message_image.setImageResource(R.drawable.message_botton);
        tv_main_me_text.setTextColor(Gray);
        iv_main_me_image.setImageResource(R.drawable.me_botton);
    }

    //定义一个底部导航栏图标变化的方法
    private void iconChange(int num)
    {
        switch (num) {
            case R.id.ll_main_mainpage_layout:case 0:
                tv_main_mainpage_text.setTextColor(Green);
                iv_main_mainpage_image.setImageResource(R.drawable.mainpage_botton_green);
                vp_main_viewpager.setCurrentItem(0);
                break;
            case R.id.ll_main_local_layout:case 1:
                tv_main_local_text.setTextColor(Green);
                iv_main_local_image.setImageResource(R.drawable.local_botton_green);
                vp_main_viewpager.setCurrentItem(1);
                break;
            case R.id.ll_main_publish_layout:case 2:
                tv_main_publish_text.setTextColor(Green);
                iv_main_publish_image.setImageResource(R.drawable.publish_botton_green);
                vp_main_viewpager.setCurrentItem(2);
                break;
//            case R.id.ll_main_message_layout:case 3:
//                tv_main_message_text.setTextColor(Green);
//                iv_main_message_image.setImageResource(R.drawable.message_botton_green);
//                vp_main_viewpager.setCurrentItem(3);
//                break;
            case R.id.ll_main_me_layout:case 4:
                tv_main_me_text.setTextColor(Green);
                iv_main_me_image.setImageResource(R.drawable.me_botton_green);
                vp_main_viewpager.setCurrentItem(4);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //结束位置监听
        mLocationClient.stop();
    }
}
