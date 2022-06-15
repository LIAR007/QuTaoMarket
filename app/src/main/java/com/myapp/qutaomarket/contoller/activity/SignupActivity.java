package com.myapp.qutaomarket.contoller.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends Activity {
    //创建注册布局中的控件变量
    private EditText et_signup_account,et_signup_pwd,et_signup_repwd,et_signup_nickname,et_signup_tel;

    private TextView tv_signup_sign,tv_signup_tologin,tx_signup_account,tx_signup_pwd,tx_signup_repwd,tx_signup_nickname,tx_signup_tel;

    private RadioButton rb_signup_man,rb_signup_woman;

    //性别，男为1，女为2
    private int sex;

    //进度条
    ProgressDialog pd;

    //点击事件的对象
    private SignupActivity.OnClickListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //初始化控件
        initView();
    }

    private void initView() {
        //初始化进度条
        pd = new ProgressDialog(SignupActivity.this);
        pd.setMessage("注册中...请稍等");
        pd.setIndeterminate(true);       //进度不确定的模糊模式
        pd.setCancelable(false);         //点击不可取消

        //初始化控件
        et_signup_account = (EditText)findViewById(R.id.et_signup_account);
        et_signup_pwd = (EditText)findViewById(R.id.et_signup_pwd);
        et_signup_repwd = (EditText)findViewById(R.id.et_signup_repwd);
        et_signup_nickname = (EditText)findViewById(R.id.et_signup_nickname);
        et_signup_tel = (EditText)findViewById(R.id.et_signup_tel);
        tx_signup_account = (TextView)findViewById(R.id.tx_signup_account);
        tx_signup_pwd = (TextView)findViewById(R.id.tx_signup_pwd);
        tx_signup_repwd = (TextView)findViewById(R.id.tx_signup_repwd);
        tx_signup_nickname = (TextView)findViewById(R.id.tx_signup_nickname);
        tx_signup_tel = (TextView)findViewById(R.id.tx_signup_tel);
        tv_signup_sign = (TextView)findViewById(R.id.tv_signup_sign);
        tv_signup_tologin = (TextView)findViewById(R.id.tv_signup_tologin);
        rb_signup_man = (RadioButton)findViewById(R.id.rb_signup_man);
        rb_signup_woman = (RadioButton)findViewById(R.id.rb_signup_woman);

        //设置登录和返回的点击监听
        listener = new OnClickListener();
        tv_signup_sign.setOnClickListener(listener);
        tv_signup_tologin.setOnClickListener(listener);

        //焦点处理
        et_signup_account.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if(et_signup_account.getText().toString().trim().length()<6)
                        tx_signup_account.setVisibility(View.VISIBLE);
                    else   tx_signup_account.setVisibility(View.INVISIBLE);

                } else {
                    // 此处为失去焦点时的处理内容
                    if(et_signup_account.getText().toString().trim().length()<6)
                        tx_signup_account.setVisibility(View.VISIBLE);
                    else   tx_signup_account.setVisibility(View.INVISIBLE);
                }
            }
        });

        et_signup_pwd.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if(et_signup_pwd.getText().toString().trim().length()<6)
                        tx_signup_pwd.setVisibility(View.VISIBLE);
                    else tx_signup_pwd.setVisibility(View.INVISIBLE);

                } else {
                    // 此处为失去焦点时的处理内容
                    if(et_signup_pwd.getText().toString().trim().length()<6)
                        tx_signup_pwd.setVisibility(View.VISIBLE);
                    else  tx_signup_pwd.setVisibility(View.INVISIBLE);
                }
            }
        });

        et_signup_repwd.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if(et_signup_repwd.getText().toString().equals(et_signup_pwd.getText().toString()))
                        tx_signup_repwd.setVisibility(View.INVISIBLE);

                    else   tx_signup_repwd.setVisibility(View.VISIBLE);


                } else {
                    // 此处为失去焦点时的处理内容
                    if(et_signup_repwd.getText().toString().equals(et_signup_pwd.getText().toString()))
                        tx_signup_repwd.setVisibility(View.INVISIBLE);
                    else tx_signup_repwd.setVisibility(View.VISIBLE);
                }
            }
        });

        et_signup_nickname.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if(et_signup_nickname.getText().toString().trim().isEmpty()) tx_signup_nickname.setVisibility(View.VISIBLE);
                    else  tx_signup_nickname.setVisibility(View.INVISIBLE);

                } else {
                    // 此处为失去焦点时的处理内容
                    if(et_signup_nickname.getText().toString().trim().isEmpty()) tx_signup_nickname.setVisibility(View.VISIBLE);
                    else   tx_signup_nickname.setVisibility(View.INVISIBLE);
                }
            }
        });
        et_signup_tel.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    if(et_signup_tel.getText().toString().trim().isEmpty()) tx_signup_tel.setVisibility(View.VISIBLE);
                    else tx_signup_tel.setVisibility(View.INVISIBLE);
                } else {
                    // 此处为失去焦点时的处理内容
                    if(et_signup_tel.getText().toString().trim().isEmpty()) tx_signup_tel.setVisibility(View.VISIBLE);
                    else tx_signup_tel.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //点击事件处理
    private class OnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_signup_sign:
                    //执行注册的逻辑
                    signUp();
                    break;
                case R.id.tv_signup_tologin:
                    //结束当前界面，跳转到登录页面
                    finish();
                    break;
            }
        }
    }
    //处理服务器返回的数据
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int code=3;
                    if(msg.obj.toString().trim().equals("-1")){
                        pd.cancel();          //取消进度条
                        Toast.makeText(SignupActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                    }else{
                        try{
                            //解析服务器返回的JSON数据
                            JSONObject result = new JSONObject(msg.obj.toString().trim());
                            JSONObject data = new JSONObject(result.getString("params"));
                            code = data.getInt("code");
                            if(code==0 || code==3)        //code=0表示APP服务器注册出错，code=3表示注册环信账号时出错
                            {
                                pd.cancel();
                                Toast.makeText(SignupActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                            }
                            else if(code==1)             //注册成功
                            {
                                pd.cancel();
                                Toast.makeText(SignupActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else if(code==2)              //注册失败
                            {
                                pd.cancel();
                                Toast.makeText(SignupActivity.this, "用户名已被注册", Toast.LENGTH_SHORT).show();
                            }
                            else                          //出现一些其他问题，一般是网络问题
                            {
                                pd.cancel();
                                Toast.makeText(SignupActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //注册账号
    private void signUp() {
        sex = 1;
        //判断输入的账户密码是否为空，密码和第二次输入的密码是否相同
        if(et_signup_account.getText().toString().trim().length()<6||et_signup_pwd.getText().toString().trim().length()<6||et_signup_nickname.getText().toString().trim().isEmpty()||et_signup_tel.getText().toString().trim().isEmpty()||et_signup_repwd.getText().toString().trim().equals(et_signup_pwd.getText().toString().trim())==false)
            //提示
            Toast.makeText(SignupActivity.this,"信息不符合规范！",Toast.LENGTH_SHORT).show();
        else {
            //显示进度条
            pd.show();
            //开启进程，网络访问必须在子进程中执行
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //将输入框中的信息填入到params里
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("account", et_signup_account.getText().toString().trim());
                    params.put("password", et_signup_pwd.getText().toString().trim());
                    params.put("nickname", et_signup_nickname.getText().toString().trim());
                    params.put("tel", et_signup_tel.getText().toString().trim());
                    if(rb_signup_man.isChecked()) sex = 1;            //男
                    else if(rb_signup_woman.isChecked()) sex=2;       //女
                    params.put("sex", Integer.toString(sex));

                    //发起http请求的网址
                    String strUrlpath = getResources().getString(R.string.burl) + "Signup_Servlet";
                    System.out.println("输出为：" + params);
                    //发起http的POST请求，把用户数据发送到服务器并接收返回的数据
                    String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                    System.out.println("结果为：" + Result);

                    //message处理
                    Message message = new Message();
                    message.what = 1;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            });
        }
    }
}
