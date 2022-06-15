package com.myapp.qutaomarket.contoller.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myapp.qutaomarket.QTApplication;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.activity.MybuyActivity;
import com.myapp.qutaomarket.contoller.activity.MyoutActivity;
import com.myapp.qutaomarket.contoller.activity.MypublishActivity;
import com.myapp.qutaomarket.contoller.activity.PersonActivity;
import com.myapp.qutaomarket.contoller.activity.SettingActivity;
import com.myapp.qutaomarket.model.db.User;
import com.myapp.qutaomarket.utils.UserUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * 个人中心页面
 */
public class F5Fragment extends Fragment {
    //初始化控件
    private ImageView iv_f5_photo;

    private TextView tv_f5_nickname, tv_f5_account,tv_f5_balance,tv_f5_reputation;

//    private LinearLayout ll_f5_publish, ll_f5_buy, ll_f5_out, ll_f5_collect;
//    private RelativeLayout rl_f5_person, rl_f5_free,rl_f5_charity,rl_f5_setting;

    private LinearLayout ll_f5_publish, ll_f5_buy, ll_f5_out;
    private RelativeLayout rl_f5_person,rl_f5_setting;

    private ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    private ArrayList<Map<String, Object>> mmessageData = new ArrayList<Map<String, Object>>();

    private OnClickListener listener;

    private int userid;
    private String headimage = null;


    public F5Fragment() {
        // Required empty public constructor
    }

    //创建view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f5, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化控件
        initView();
    }

    //初始化控件
    public void initView() {
        //头像
        iv_f5_photo = (ImageView) getActivity().findViewById(R.id.iv_f5_photo);
        //textview
        tv_f5_nickname = (TextView)getActivity().findViewById(R.id.tv_f5_nickname);
        tv_f5_account = (TextView)getActivity().findViewById(R.id.tv_f5_account);
        tv_f5_balance = (TextView)getActivity().findViewById(R.id.tv_f5_balance);
        tv_f5_reputation = (TextView)getActivity().findViewById(R.id.tv_f5_reputation);
        //LinearLayout
        ll_f5_publish = (LinearLayout) getActivity().findViewById(R.id.ll_f5_publish);
        ll_f5_buy = (LinearLayout) getActivity().findViewById(R.id.ll_f5_buy);
        ll_f5_out = (LinearLayout) getActivity().findViewById(R.id.ll_f5_out);
//        ll_f5_collect = (LinearLayout) getActivity().findViewById(R.id.ll_f5_collect);
        rl_f5_person = (RelativeLayout) getActivity().findViewById(R.id.rl_f5_person);
//        rl_f5_free = (RelativeLayout) getActivity().findViewById(R.id.rl_f5_free);
//        rl_f5_charity = (RelativeLayout) getActivity().findViewById(R.id.rl_f5_charity);
        rl_f5_setting = (RelativeLayout) getActivity().findViewById(R.id.rl_f5_setting);

        //初始化监听
        listener = new OnClickListener();
        ll_f5_publish.setOnClickListener(listener);
        ll_f5_buy.setOnClickListener(listener);
        ll_f5_out.setOnClickListener(listener);
//        ll_f5_collect.setOnClickListener(listener);
        rl_f5_person.setOnClickListener(listener);
//        rl_f5_free.setOnClickListener(listener);
//        rl_f5_charity.setOnClickListener(listener);
        rl_f5_setting.setOnClickListener(listener);

        //加载头像、昵称、账号等数据
        loadData();
    }

    //按键监听
    private class  OnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                //我发布的
                case R.id.ll_f5_publish:
                    Intent publishIntent = new Intent(getActivity(), MypublishActivity.class);
                    startActivity(publishIntent);
                    break;
                //我买到的
                case R.id.ll_f5_buy:
                    Intent buyIntent = new Intent(getActivity(), MybuyActivity.class);
                    startActivity(buyIntent);
                    break;
                //我卖出的
                case R.id.ll_f5_out:
                    Intent outIntent = new Intent(getActivity(), MyoutActivity.class);
                    startActivity(outIntent);
                    break;
                    //个人资料
                case R.id.rl_f5_person:
                    Intent intent_person = new Intent(getActivity(), PersonActivity.class);
                    startActivityForResult(intent_person,1);
                    break;
                    //设置
                case R.id.rl_f5_setting:
                    Intent intent_setting = new Intent(QTApplication.getmContext(), SettingActivity.class);
                    startActivity(intent_setting);
                    break;
                    default:break;
            }
        }
    }
    //加载头像、昵称、用户名等
    private void loadData(){
        //从数据库获取用户信息
        User user = UserUtils.getCurrentUser();
        //设置头像
        if(!"null".equals(user.getHeadPhoto().trim())){
            String imagePath = "null";
            try {
                imagePath = URLEncoder.encode(user.getHeadPhoto().trim(), "utf-8");
                System.out.println("imagepath:"+imagePath);
                if("null".equals(imagePath)){
                    Glide.with(getActivity())
                            .load(R.drawable.moren_headphoto)
                            .circleCrop()
                            .placeholder(R.drawable.moren_headphoto)
                            .error(R.drawable.moren_headphoto)
                            .into(iv_f5_photo);
                }else {
                    Glide.with(getActivity())
                            .load(getResources().getString(R.string.burl)+ "Image_Servlet?" + imagePath)
                            .circleCrop()
                            .placeholder(R.drawable.moren_headphoto)
                            .error(R.drawable.moren_headphoto)
                            .into(iv_f5_photo);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //设置账号
        tv_f5_account.setText(user.getAccount().trim());
        //设置昵称
        tv_f5_nickname.setText(user.getNickName().trim());
        //设置信誉值
        tv_f5_reputation.setText(String.valueOf(user.getReputation()));
        //设置账户余额
        tv_f5_balance.setText(String.valueOf(user.getBalance()));
    }

    //其他活动返回数据的处理
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                loadData();
                break;
        }
    }
}