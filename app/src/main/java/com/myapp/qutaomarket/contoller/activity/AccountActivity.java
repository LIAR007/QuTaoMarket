package com.myapp.qutaomarket.contoller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.EaseConstant;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.utils.HttpUtils;
import com.myapp.qutaomarket.utils.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    //定义ImageView的变量
    private ImageView iv_account_back,iv_account_gimage;

    //定义TextView的变量
    private TextView tv_account_state,tv_account_gprice,tv_account_userdata,tv_account_allprice,tv_account_gallprice,
            tv_account_nicktitle,tv_account_nickname,tv_account_accounttitle,tv_account_account,tv_account_number,tv_account_time,tv_account_gdetail,
            tv_account_refund,tv_account_affirm,tv_account_delete,tv_account_address,tv_account_addtitle;

    //定义LinearLayout的变量
    private LinearLayout ll_account_goods;

    //设置按键监听变量
    private AccountActivity.OnClickListener listener;

    //用户和商品数据
    Map<String, Object> uagdata = new HashMap<>();

    //进度条
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //接收其他Activity传过来的数据
        uagdata = (Map<String, Object>) getIntent().getSerializableExtra("data");

        //初始化视图
        initView();
        //初始化数据
        initData();
    }

    //初始化视图
    private void initView(){
        //初始化进度条
        pd = new ProgressDialog(AccountActivity.this);
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        //设置变量
        iv_account_back = (ImageView)findViewById(R.id.iv_account_back);
        iv_account_gimage = (ImageView)findViewById(R.id.iv_account_gimage);
        ll_account_goods = (LinearLayout)findViewById(R.id.ll_account_goods);
        tv_account_state = (TextView)findViewById(R.id.tv_account_state);
        tv_account_gprice = (TextView)findViewById(R.id.tv_account_gprice);
        tv_account_allprice = (TextView)findViewById(R.id.tv_account_allprice);
        tv_account_gallprice = (TextView)findViewById(R.id.tv_account_gallprice);
        tv_account_nicktitle = (TextView)findViewById(R.id.tv_account_nicktitle);
        tv_account_nickname = (TextView)findViewById(R.id.tv_account_nickname);
        tv_account_accounttitle = (TextView)findViewById(R.id.tv_account_accounttitle);
        tv_account_account = (TextView)findViewById(R.id.tv_account_account);
        tv_account_number = (TextView)findViewById(R.id.tv_account_number);
        tv_account_time = (TextView)findViewById(R.id.tv_account_time);
        tv_account_refund = (TextView)findViewById(R.id.tv_account_refund);
        tv_account_affirm = (TextView)findViewById(R.id.tv_account_affirm);
        tv_account_delete = (TextView)findViewById(R.id.tv_account_delete);
        tv_account_gdetail = (TextView)findViewById(R.id.tv_account_gdetail);
        tv_account_userdata = (TextView)findViewById(R.id.tv_account_userdata);
        tv_account_address = (TextView)findViewById(R.id.tv_account_address);
        tv_account_addtitle = (TextView)findViewById(R.id.tv_account_addtitle);

        //设置按键监听
        listener = new OnClickListener();
        iv_account_back.setOnClickListener(listener);
        ll_account_goods.setOnClickListener(listener);
        tv_account_refund.setOnClickListener(listener);
        tv_account_affirm.setOnClickListener(listener);
        tv_account_delete.setOnClickListener(listener);
    }

    //初始化数据
    private void initData(){
        //根据不同的订单状态设置不同的消息
        if(Integer.valueOf(uagdata.get("astate").toString()) == 1){          //等待卖家发货
            //设置交易状态
            tv_account_state.setText("等待卖家发货");
            //根据我是买家还是卖家分别设置不同的消息
            if("myout".equals(uagdata.get("datatype").toString())){       //我是卖家
                tv_account_addtitle.setText("买家地址： ");
                tv_account_nicktitle.setText("买家昵称：");
                tv_account_accounttitle.setText("买家账号：");
                tv_account_affirm.setText("确认发货");
                tv_account_delete.setVisibility(View.GONE);
                tv_account_refund.setVisibility(View.VISIBLE);
                tv_account_affirm.setVisibility(View.VISIBLE);
            }else {                                   //我是买家
                tv_account_addtitle.setText("卖家地址： ");
                tv_account_nicktitle.setText("卖家昵称：");
                tv_account_accounttitle.setText("卖家账号：");
                tv_account_affirm.setText("确认收货");
                tv_account_delete.setVisibility(View.GONE);
                tv_account_refund.setVisibility(View.VISIBLE);
                tv_account_affirm.setVisibility(View.GONE);
            }
        }else if(Integer.valueOf(uagdata.get("astate").toString()) == 2){         //等待买家收货
            tv_account_state.setText("等待买家确认收货");
            if("myout".equals(uagdata.get("datatype").toString())){
                tv_account_addtitle.setText("买家地址： ");
                tv_account_nicktitle.setText("买家昵称：");
                tv_account_accounttitle.setText("买家账号：");
                tv_account_affirm.setText("确认发货");
                tv_account_delete.setVisibility(View.GONE);
                tv_account_refund.setVisibility(View.GONE);
                tv_account_affirm.setVisibility(View.GONE);
            }else {
                tv_account_addtitle.setText("卖家地址： ");
                tv_account_nicktitle.setText("卖家昵称：");
                tv_account_accounttitle.setText("卖家账号：");
                tv_account_affirm.setText("确认收货");
                tv_account_delete.setVisibility(View.GONE);
                tv_account_refund.setVisibility(View.VISIBLE);
                tv_account_affirm.setVisibility(View.VISIBLE);
            }
        }else if(Integer.valueOf(uagdata.get("astate").toString()) == 3){            //交易成功
            tv_account_state.setText("交易成功");
            if("myout".equals(uagdata.get("datatype").toString())){
                tv_account_addtitle.setText("买家地址： ");
                tv_account_nicktitle.setText("买家昵称：");
                tv_account_accounttitle.setText("买家账号：");
                tv_account_affirm.setText("确认发货");
                tv_account_delete.setVisibility(View.VISIBLE);
                tv_account_refund.setVisibility(View.GONE);
                tv_account_affirm.setVisibility(View.GONE);
            }else {
                tv_account_addtitle.setText("卖家地址： ");
                tv_account_nicktitle.setText("卖家昵称：");
                tv_account_accounttitle.setText("卖家账号：");
                tv_account_affirm.setText("确认收货");
                tv_account_delete.setVisibility(View.VISIBLE);
                tv_account_refund.setVisibility(View.GONE);
                tv_account_affirm.setVisibility(View.GONE);
            }
        }else if(Integer.valueOf(uagdata.get("astate").toString()) == 4){                 //退款状态
            tv_account_state.setText("退款中");
            if("myout".equals(uagdata.get("datatype").toString())){
                tv_account_addtitle.setText("买家地址： ");
                tv_account_nicktitle.setText("买家昵称：");
                tv_account_accounttitle.setText("买家账号：");
                tv_account_affirm.setText("确认退款");
                tv_account_delete.setVisibility(View.GONE);
                tv_account_refund.setVisibility(View.GONE);
                tv_account_affirm.setVisibility(View.VISIBLE);
            }else {
                tv_account_addtitle.setText("卖家地址： ");
                tv_account_nicktitle.setText("卖家昵称：");
                tv_account_accounttitle.setText("卖家账号：");
                tv_account_affirm.setText("确认收货");
                tv_account_delete.setVisibility(View.GONE);
                tv_account_refund.setVisibility(View.GONE);
                tv_account_affirm.setVisibility(View.GONE);
            }
        }else if(Integer.valueOf(uagdata.get("astate").toString()) == 5){                  //交易失败
            tv_account_state.setText("交易失败");
            if("myout".equals(uagdata.get("datatype").toString())){
                tv_account_addtitle.setText("买家地址： ");
                tv_account_nicktitle.setText("买家昵称：");
                tv_account_accounttitle.setText("买家账号：");
                tv_account_affirm.setText("确认发货");
                tv_account_delete.setVisibility(View.VISIBLE);
                tv_account_refund.setVisibility(View.GONE);
                tv_account_affirm.setVisibility(View.GONE);
            }else {
                tv_account_addtitle.setText("卖家地址： ");
                tv_account_nicktitle.setText("卖家昵称：");
                tv_account_accounttitle.setText("卖家账号：");
                tv_account_affirm.setText("确认收货");
                tv_account_delete.setVisibility(View.VISIBLE);
                tv_account_refund.setVisibility(View.GONE);
                tv_account_affirm.setVisibility(View.GONE);
            }
        }
        //设置商品信息
        Glide.with(AccountActivity.this)
                .load(uagdata.get("gimage").toString())
                .placeholder(R.drawable.ic_moren_goods)
                .error(R.drawable.ic_moren_goods)
                .into(iv_account_gimage);
        tv_account_gdetail.setText(uagdata.get("gdetail").toString());
        tv_account_gprice.setText(uagdata.get("gprice").toString());
        tv_account_gallprice.setText(uagdata.get("gprice").toString());
        tv_account_allprice.setText(uagdata.get("gprice").toString());

        //收货地址
        String address = uagdata.get("nickname").toString() + "  " + uagdata.get("tel").toString();
        tv_account_userdata.setText(address);
        tv_account_address.setText(uagdata.get("address").toString());

        //订单信息
        tv_account_nickname.setText(uagdata.get("nickname").toString());
        tv_account_account.setText(uagdata.get("account").toString());
        tv_account_number.setText(uagdata.get("anumber").toString());
        tv_account_time.setText(uagdata.get("atime").toString());
    }

    //handler处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case 1:                       //取消订单
                    int cancleBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            //获取服务端返回的code字段，判断取消成功还是失败
                            JSONObject cancleResult = new JSONObject(msg.obj.toString().trim());
                            cancleBack = cancleResult.getInt("code");
                            if(cancleBack == 1){
                                pd.cancel();
                                Toast.makeText(AccountActivity.this,"取消成功",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(AccountActivity.this,"取消失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(AccountActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:                       //确认发货
                    int sendBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject sendResult = new JSONObject(msg.obj.toString().trim());
                            sendBack = sendResult.getInt("code");
                            if(sendBack == 1){
                                pd.cancel();
                                Toast.makeText(AccountActivity.this,"已确认发货",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(AccountActivity.this,"发货失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(AccountActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 3:                                  //确认收货
                    int getBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject getResult = new JSONObject(msg.obj.toString().trim());
                            getBack = getResult.getInt("code");
                            if(getBack == 1){
                                pd.cancel();
                                Toast.makeText(AccountActivity.this,"收货成功",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(AccountActivity.this,"收货失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(AccountActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4:                                      //删除订单
                    int deleteBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject deleteResult = new JSONObject(msg.obj.toString().trim());
                            deleteBack = deleteResult.getInt("code");
                            if(deleteBack == 1){
                                pd.cancel();
                                Toast.makeText(AccountActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(AccountActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(AccountActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //点击监听
    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //返回按键
                case R.id.iv_account_back:
                    finish();
                    break;
                case R.id.ll_account_goods:
                    //点击后进入商品细节页面，留下接口，以后实现
                    break;
                //取消订单
                case R.id.tv_account_refund:
                    cancelAccount();
                    break;
                //确认收货
                case R.id.tv_account_affirm:
                    affirm();
                    break;
                //删除订单
                case R.id.tv_account_delete:
                    delAccount();
                    break;
                default:
                    break;
            }
        }
    }

    //取消订单
    private void cancelAccount(){
        //获取时间
        final Date d = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //设置进度条的消息并显示
        pd.setMessage("请稍后...");
        pd.show();
        //开启进程，进行网络请求
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //设置上传的数据
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop","cancleaccount");
                params.put("aid",uagdata.get("aid").toString());
                params.put("gid",uagdata.get("gid").toString());
                params.put("gname",uagdata.get("gname").toString());
                params.put("uid",uagdata.get("uid").toString());
                params.put("abill",uagdata.get("abill").toString());
                params.put("hxid",uagdata.get("hxid").toString());
                params.put("time",sdf.format(d));
                //设置请求网址
                String strUrlpath = getResources().getString(R.string.burl) + "Account_Servlet";
                //提交数据，并获取返回结果
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                //把返回的数据发送到handler中处理，数据不在子线程中处理
                Message msg = new Message();
                msg.what = 1;
                msg.obj = Result;
                handler.sendMessage(msg);
            }
        });
    }

    //确认收货或发货
    private void affirm(){
        //获取时间
        final Date d = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //判断是确认收货还是确认发货
        if("确认发货".equals(tv_account_affirm.getText().toString())){
            pd.setMessage("请稍后...");
            pd.show();
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("requesttop","affrimsend");
                    params.put("aid",uagdata.get("aid").toString());
                    params.put("gname",uagdata.get("gname").toString());
                    params.put("hxid",uagdata.get("hxid").toString());
                    params.put("uid",uagdata.get("uid").toString());
                    params.put("time",sdf.format(d));
                    String strUrlpath = getResources().getString(R.string.burl) + "Account_Servlet";
                    String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                    System.out.println("结果为：" + Result);
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = Result;
                    handler.sendMessage(msg);
                }
            });
        }else {
            pd.setMessage("请稍后...");
            pd.show();
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("requesttop","affrimget");
                    params.put("aid",uagdata.get("aid").toString());
                    params.put("gid",uagdata.get("gid").toString());
                    params.put("gname",uagdata.get("gname").toString());
                    params.put("uid",uagdata.get("uid").toString());
                    params.put("guid",uagdata.get("guid").toString());
                    params.put("abill",uagdata.get("abill").toString());
                    params.put("hxid",uagdata.get("hxid").toString());
                    params.put("time",sdf.format(d));
                    String strUrlpath = getResources().getString(R.string.burl) + "Account_Servlet";
                    String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                    System.out.println("结果为：" + Result);
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = Result;
                    handler.sendMessage(msg);
                }
            });
        }
    }

    //删除订单
    private void delAccount(){
        pd.setMessage("请稍后...");
        pd.show();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop","deleteaccount");
                params.put("aid",uagdata.get("aid").toString());
                String strUrlpath = getResources().getString(R.string.burl) + "Mybuy_Servlet";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message msg = new Message();
                msg.what = 4;
                msg.obj = Result;
                handler.sendMessage(msg);
            }
        });
    }
}
