package com.myapp.qutaomarket.contoller.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.myapp.qutaomarket.QTApplication;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.model.db.User;
import com.myapp.qutaomarket.utils.HttpUtils;
import com.myapp.qutaomarket.utils.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    //定义变量
    private ImageView iv_setting_back;

    private LinearLayout ll_setting_logout;

    RelativeLayout rl_setting_repwd, rl_setting_aboutus, rl_setting_version;

    private SettingActivity.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //初始化视图
        initView();
    }

    //初始化视图
    private void initView() {
        iv_setting_back = (ImageView) findViewById(R.id.iv_setting_back);
        ll_setting_logout = (LinearLayout) findViewById(R.id.ll_setting_logout);
        rl_setting_repwd = (RelativeLayout) findViewById(R.id.rl_setting_repwd);
        rl_setting_aboutus = (RelativeLayout) findViewById(R.id.rl_setting_aboutus);
        rl_setting_version = (RelativeLayout) findViewById(R.id.rl_setting_version);

        listener = new OnClickListener();
        iv_setting_back.setOnClickListener(listener);
        ll_setting_logout.setOnClickListener(listener);
        rl_setting_repwd.setOnClickListener(listener);
        rl_setting_aboutus.setOnClickListener(listener);
        rl_setting_version.setOnClickListener(listener);
    }

    private class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_setting_back:
                    finish();
                    break;
                case R.id.ll_setting_logout:
                    setLogout();
                    break;
                case R.id.rl_setting_repwd:
                    rePwd();
                    break;
                case R.id.rl_setting_aboutus:
                    setAboutus();
                    break;
                case R.id.rl_setting_version:
                    setVersion();
                    break;
            }
        }
    }

    //处理服务器返回的数据
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            User user = UserUtils.getCurrentUser();
            switch (msg.what) {
                case 0:
                    int code = 2;
                    if (msg.obj.toString().trim().equals("-1"))         //返回的数据为空时的处理
                    {
                        Toast.makeText(SettingActivity.this, "请检查网络！", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject result = new JSONObject(msg.obj.toString().trim());
                            code = Integer.valueOf(result.getString("code"));
                            if (code == 0) {
                                Toast.makeText(SettingActivity.this, "退出登录失败！", Toast.LENGTH_SHORT).show();
                            } else if (code == 1) {
                                //开启退出环信服务器的进程
                                hxLogout.start();
                            } else {
                                Toast.makeText(SettingActivity.this, "请检查网络！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    int pwdBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject pwdResult = new JSONObject(msg.obj.toString().trim());
                            pwdBack = pwdResult.getInt("code");
                            JSONObject pwdParams = new JSONObject(pwdResult.getString("data"));
                            if(pwdBack == 1){
                                user.setPassword(pwdParams.getString("password"));
                                user.save();
                                Toast.makeText(SettingActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(SettingActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(SettingActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //退出登录的逻辑处理
    public void setLogout() {
        AlertDialog alert = new AlertDialog.Builder(SettingActivity.this).setTitle("提示")
                .setMessage("确定要注销么？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        Logout();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//设置取消按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//对话框关闭。
                    }
                }).create();
        alert.show();
    }

    //关于我们的逻辑处理
    public void setAboutus() {
        AlertDialog alert = new AlertDialog.Builder(SettingActivity.this).setTitle("关于我")
                .setMessage(
                        "这次毕业设计我采用了Tomcat做服务器," +
                                "Android Studio写客户端\n" +
                                "集成了环信的easeui,实现聊天功能\n" +
                                "集成了BaiduMapSDK,实现用户的定位\n" +
                                "作者：11111\n" +
                                "微信：11111\n" +
                                "时间：2019年12月")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//对话框关闭。
                    }
                }).create();
        alert.show();
    }

    //版本信息处理逻辑
    public void setVersion() {
        AlertDialog alert = new AlertDialog.Builder(SettingActivity.this).setTitle("版本信息")
                .setMessage("version：1.0.1\n" +
                        "此版本基本完成:\n.二手交易基本功能\n.界面美化")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//对话框关闭。
                    }
                }).create();
        alert.show();
    }

    //向服务器发送推出标志
    private void Logout() {
        //开启进程，网络请求必须在子进程中进行
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //将输入的账号密码拼接成params
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop", "logout");
                params.put("account", UserUtils.getCurrentUser().getAccount().trim());
                //设置访问网址
                String strUrlPath = getResources().getString(R.string.burl) + "Setting_Servlet";
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

    //退出环信服务器的逻辑实现
    Thread hxLogout = new Thread(new Runnable() {
        @Override
        public void run() {
            //异步登出聊天服务器
            EMClient.getInstance().logout(false, new EMCallBack() {
                @Override
                public void onSuccess() {
                    User user = UserUtils.getCurrentUser();
                    user.setLogin(false);
                    user.save();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onError(int i, String s) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QTApplication.getmContext(), "退出失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    });

    //修改密码
    private void rePwd(){
        User user = UserUtils.getCurrentUser();
        //加载布局
        final LinearLayout layout_changepwd = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_changepwd,null);
        new AlertDialog.Builder(this)
                .setView(layout_changepwd)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        EditText pwd = layout_changepwd.findViewById(R.id.et_layout_pwd);
                        EditText newpwd = layout_changepwd.findViewById(R.id.et_layout_newpwd);
                        if(pwd.getText().toString().trim().isEmpty()){
                            Toast.makeText(SettingActivity.this,"请输入旧密码！",Toast.LENGTH_SHORT).show();
                        }else {
                            if(pwd.getText().toString().trim().equals(user.getPassword().trim())){
                                if(newpwd.getText().toString().trim().isEmpty())
                                {
                                    Toast.makeText(SettingActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
                                } else {
                                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("requesttop","modifypwd");
                                            params.put("account", user.getAccount().trim());
                                            params.put("pwd", pwd.getText().toString().trim());
                                            params.put("newpwd", newpwd.getText().toString().trim());
                                            String Result = HttpUtils.submitPostData(getResources().getString(R.string.burl) + "Setting_Servlet", params, "utf-8");
                                            System.out.println("结果为：" + Result);
                                            Message message = new Message();
                                            message.what = 1;
                                            message.obj = Result;
                                            handler.sendMessage(message);
                                        }
                                    });
                                }
                            }else {
                                Toast.makeText(SettingActivity.this,"密码不正确",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理取消按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }
}

