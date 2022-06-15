package com.myapp.qutaomarket.contoller.fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.activity.PublishbuyActivity;
import com.myapp.qutaomarket.contoller.activity.PublishgoodsActivity;

import java.io.Serializable;

/**
 * 发布的fragment
 */
public class F3Fragment extends Fragment {

//    private RelativeLayout rl_f3_publish,rl_f3_free,rl_f3_buy,rl_f3_charity,rl_f3_auction;
    private RelativeLayout rl_f3_publish,rl_f3_buy;

    private OnClickListener listener;

    public F3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f3, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化控件
        initView();
    }

    //初始化控件
    public void initView() {
        //将Linearlayout初始化
        rl_f3_publish = (RelativeLayout) getActivity().findViewById(R.id.rl_f3_publish);
//        rl_f3_free = (RelativeLayout) getActivity().findViewById(R.id.rl_f3_free);
        rl_f3_buy = (RelativeLayout) getActivity().findViewById(R.id.rl_f3_buy);
//        rl_f3_charity = (RelativeLayout) getActivity().findViewById(R.id.rl_f3_charity);
//        rl_f3_auction = (RelativeLayout) getActivity().findViewById(R.id.rl_f3_auction);

        //初始化监听
        listener = new OnClickListener();
        rl_f3_publish.setOnClickListener(listener);
//        rl_f3_free.setOnClickListener(listener);
        rl_f3_buy.setOnClickListener(listener);
//        rl_f3_charity.setOnClickListener(listener);
//        rl_f3_auction.setOnClickListener(listener);
    }

    //按键监听
    private class  OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //发布闲置
                case R.id.rl_f3_publish:
                    Intent publishIntent1 = new Intent(getActivity(), PublishgoodsActivity.class);
                    publishIntent1.putExtra("title", "发布闲置");
                    publishIntent1.putExtra("type","publishgoods");
                    publishIntent1.putExtra("data",(Serializable)null);
                    startActivity(publishIntent1);
                    break;
                //发布免费送
//                case R.id.rl_f3_free:
//                    Intent publishIntent2 = new Intent(getActivity(), PublishgoodsActivity.class);
//                    publishIntent2.putExtra("title", "发布免费送");
//                    publishIntent2.putExtra("type", "publishfree");
//                    publishIntent2.putExtra("data",(Serializable)null);
//                    startActivity(publishIntent2);
//                    break;
                //发布求购
                case R.id.rl_f3_buy:
                    Intent buyIntent = new Intent(getActivity(), PublishbuyActivity.class);
                    buyIntent.putExtra("title", "发布求购");
                    buyIntent.putExtra("type", "publishbuy");
                    buyIntent.putExtra("data",(Serializable)null);
                    startActivity(buyIntent);
                    break;
                //发布公益
//                case R.id.rl_f3_charity:
//                    Intent charityIntent = new Intent(getActivity(), PublishcharityActivity.class);
//                    charityIntent.putExtra("title", "发起公益");
//                    charityIntent.putExtra("type", "publishcharity");
//                    charityIntent.putExtra("data",(Serializable)null);
//                    startActivity(charityIntent);
//                    break;
                //发布拍卖
//                case R.id.rl_f3_auction:
//                    Auction();
//                    break;

            }
        }
    }

    //拍卖的逻辑处理，暂时不做这个功能，留下接口
//    public void Auction() {
//        AlertDialog alert = new AlertDialog.Builder(getActivity()).setTitle("发起拍卖")
//                .setMessage("拍卖功能还在开发中哦！")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {//设置确定按钮
//                    @Override//处理确定按钮点击事件
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();//对话框关闭。
//                    }
//                }).create();
//        alert.show();
//    }
}
