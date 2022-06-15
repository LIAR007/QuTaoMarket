package com.myapp.qutaomarket.contoller.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.model.db.User;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

//欢迎页面
public class WelcomeActivity extends Activity {
    //用户列表
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //发送两秒钟的延迟
        //handler.sendMessageDelayed(Message.obtain(),2000);

        //定义权限列表变量
        List<String> permissionList = new ArrayList<>();
        //依次向权限列表中添加权限
        if(ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if(ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);
        }
        //如果权限列表为空则表示权限已被允许
        if(!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(WelcomeActivity.this, permissions, 1);
        }else {
            handler.sendMessageDelayed(Message.obtain(),2000);
        }

    }

    //处理权限的申请结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0) {
                    //遍历对每个权限的申请结果
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            //如果有不被授权的权限则发出一条提示
                            Toast.makeText(WelcomeActivity.this, "必须同意全部权限才可使用本软件！", Toast.LENGTH_SHORT).show();
                            //直接退出
                            finish();
                            return;
                        }
                    }
                    //所有权限都授予之后开启两秒钟延时
                    handler.sendMessageDelayed(Message.obtain(),2000);
                }else {
                    Toast.makeText(WelcomeActivity.this, "发生未知错误！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
                default:
                    break;
        }
    }

    protected Handler handler = new Handler(){
        public void handleMessage(Message msg){
            //如果当前Activity已经退出，不处理消息，直接返回
            if(isFinishing()){
                return;
            }
            //判断是进入主页面还是登录页面
            toMainorLogin();
        }
    };

    //判断是进入主页面还是登录页面
    private void toMainorLogin() {
        //在进程中进行
        Model.getInstance().getGlobalThreadPool().execute(new Runnable(){
            @Override
            public void run() {
                //建立一个User对象
                User user = new User();
                //清空userList中的数据
                userList.clear();
                //获取数据库中所有的用户信息
                userList = LitePal.findAll(User.class);
                //判断userList是否为空
                if(userList.size() > 0){
                    //遍历userList列表
                    for(User u : userList){
                        if(u.isLogin()){
                            user = u;
                        }
                    }
                    System.out.println("1111111111欢迎界面：" + user.getAccount());
                    System.out.println("1111111111欢迎界面：" + user.isLogin());
                    System.out.println("1111111111欢迎界面：" + user);
                    //如果之前有账号登录过，判断是否有账号在线
                    if(user.getAccount() == null){
                        //跳转到登录界面
                        Intent i = new Intent(WelcomeActivity.this,LoginActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        //跳转到主界面
                        Intent i = new Intent(WelcomeActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }else {
                    //跳转到登录界面
                    Intent i = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    //销毁活动
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
