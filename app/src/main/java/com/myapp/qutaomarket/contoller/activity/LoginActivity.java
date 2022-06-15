package com.myapp.qutaomarket.contoller.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.model.db.User;
import com.myapp.qutaomarket.utils.Code;
import com.myapp.qutaomarket.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    //定义控件变量
    private EditText et_logic_account,et_logic_pwd,et_logic_ver;

    private ImageView iv_logic_vercode,iv_login_login;

    private TextView tv_logic_refresh,tv_logic_signup;

    //点击事件的对象
    private LoginActivity.OnClickListener listener;

    //产生的验证码
    private String realCode;

    //进度条
    private ProgressDialog pd;

    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化控件
        initView();

        //将验证码用图片的形式显示出来
        refreshCode();
    }

    //初始化控件
    private void initView() {
        //初始化等待进度条
        pd = new ProgressDialog(LoginActivity.this);
        //初始化控件
        et_logic_account = (EditText)findViewById(R.id.et_logic_account);
        et_logic_pwd = (EditText)findViewById(R.id.et_logic_pwd);
        et_logic_ver = (EditText)findViewById(R.id.et_logic_ver);
        iv_logic_vercode = (ImageView)findViewById(R.id.iv_logic_vercode);
        iv_login_login = (ImageView)findViewById(R.id.iv_login_login);
        tv_logic_refresh = (TextView)findViewById(R.id.tv_logic_refresh);
        tv_logic_signup = (TextView)findViewById(R.id.tv_logic_signup);

        //设置按钮的点击监听
        listener = new OnClickListener();
        iv_login_login.setOnClickListener(listener);
        tv_logic_refresh.setOnClickListener(listener);
        tv_logic_signup.setOnClickListener(listener);
    }

    //登录页面中所有的点击事件
    private class OnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_login_login:           //点击登录按钮的处理
                    login();
                   break;
                case R.id.tv_logic_refresh:         //点击刷新验证码的处理
                    //刷新验证码
                    refreshCode();
                    break;
                case R.id.tv_logic_signup:           //点击注册按钮的处理
                    //跳转到注册页面
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    //验证验证码的对错
    private boolean verifyCode() {
        //获取输入的验证码
        String inputCode = et_logic_ver.getText().toString().trim().toLowerCase();
        if(realCode.equals(inputCode)){
            return true;
        }
        return false;
    }

    //刷新验证码
    private void refreshCode() {
        iv_logic_vercode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }

    //处理服务器返回的数据
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //what:用户自定义的消息代码，这样接受者可以了解这个消息的信息。每个handler各自包含自己的消息代码，所以不用担心自定义的消息跟其他handlers有冲突。
            switch (msg.what) {
                case 0:
                    int code=2;
                    if(msg.obj.toString().trim().equals("-1"))         //返回的数据为空时的处理
                    {
                        pd.cancel();
                        Toast.makeText(LoginActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try{
                            JSONObject result = new JSONObject(msg.obj.toString().trim());
                            JSONObject data = new JSONObject(result.getString("code"));
                            code = data.getInt("code");
                            if(code==0)
                            {
                                pd.cancel();
                                Toast.makeText(LoginActivity.this,"账号或密码错误！",Toast.LENGTH_SHORT).show();
                            }
                            else if(code==1)
                            {
                                user = new User();
                                JSONObject userData = new JSONObject(result.getString("data"));
                                //将接收到的数据存入数据库
                                user.setAccount(userData.getString("account"));
                                user.setPassword(userData.getString("password"));
                                user.setHxId(userData.getString("hxid"));
                                user.setNickName(userData.getString("nickname"));
                                //user.setHeadPhoto(userData.getString("headphoto"));
                                user.setSex(userData.getInt("sex"));
                                //user.setBalance(userData.getDouble("balance"));
                                //user.setAddress(userData.getString("address"));
                                //user.setSchool(userData.getString("school"));
                                //user.setReputation(userData.getInt("reputation"));
                                user.setTel(userData.getString("tel"));
                                user.setLogin(true);      //将该账号设置成登录状态
                                hxThread.start();
                            }
                            else if(code==2)
                            {
                                //取消进度条
                                pd.cancel();
                                Toast.makeText(LoginActivity.this,"用户在其他设备登录！",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //取消进度条
                                pd.cancel();
                                Toast.makeText(LoginActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    //登录逻辑处理
    private void login() {
        if("".equals(et_logic_ver.getText().toString().trim().toLowerCase())){
            Toast.makeText(LoginActivity.this, "请输入验证码！", Toast.LENGTH_LONG).show();
            //刷新验证码
            refreshCode();
            return;
        }else {
            if(verifyCode()){
                //先判断
                if(et_logic_account.getText().toString().trim().isEmpty()||et_logic_pwd.getText().toString().trim().isEmpty()){
                    Toast.makeText(LoginActivity.this, "账户或密码不能为空！", Toast.LENGTH_LONG).show();
                    //刷新验证码
                    refreshCode();
                }else {
                    //设置等待进度条
                    pd.setMessage("登陆中...");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();       //进度条显示

                    //开启进程，网络请求必须在子进程中进行
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            //将输入的账号密码拼接成params
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("account", et_logic_account.getText().toString().trim());
                            params.put("password", et_logic_pwd.getText().toString().trim());
                            //设置访问网址
                            String strUrlPath = getResources().getString(R.string.burl) + "Login_Servlet";
                            System.out.println("输出为：" + params);
                            //发起http请求
                            String Result = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                            System.out.println("结果为：" + Result);

                            //请求结果的message处理
                            Message message = new Message();
                            message.what = 0;
                            message.obj = Result;
                            handler.sendMessage(message);
                        }
                    });
                }
            }else {
                //验证码输入出错时的处理
                Toast.makeText(LoginActivity.this, "验证码错误！", Toast.LENGTH_LONG).show();
                //刷新验证码
                refreshCode();
            }
        }
    }
    Thread hxThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //传入在应用（appkey）下注册的IM用户的账号和密码，用于登录环信服务器
                    EMClient.getInstance().login(user.getHxId().trim(),user.getPassword().trim(),new EMCallBack() {//回调
                        @Override
                        public void onSuccess() {
                            //将数据存入数据库
                            user.save();
                            //取消进度条
                            pd.cancel();
                            //跳转到主页面
                            Intent i = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(i);
                            //结束该活动
                            finish();
                        }
                        @Override
                        public void onProgress(int progress, String status) {

                        }
                        @Override
                        public void onError(int code, String message) {
                            pd.cancel();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }
    });
}
