package com.myapp.qutaomarket.contoller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.model.db.User;
import com.myapp.qutaomarket.utils.HttpUtils;
import com.myapp.qutaomarket.utils.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//设置个人资料地址的Activity
public class AddressActivity extends AppCompatActivity {

    //定义变量
    private ImageView iv_address_back,iv_address_submit;

    private EditText et_address_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        //初始化
        initView();
    }

    private void initView() {
        //初始化变量
        iv_address_back = (ImageView)findViewById(R.id.iv_address_back);
        iv_address_submit = (ImageView)findViewById(R.id.iv_address_submit);
        et_address_address = (EditText) findViewById(R.id.et_address_address);

        //设置返回按键的监听
        iv_address_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置提交按键的监听
        iv_address_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAddress();
            }
        });
        //获取当前的地址设置到地址的编辑框里
        User user = UserUtils.getCurrentUser();
        //判断地址是不是空
        if(!"null".equals(user.getAddress())){
            et_address_address.setText(user.getAddress().trim());
        }
    }

    //handler处理
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            User user = UserUtils.getCurrentUser();
            switch (msg.what){
                case 1:
                    int addressBack;                  //发起请求返回的地址
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject addressResult = new JSONObject(msg.obj.toString().trim());
                            addressBack = addressResult.getInt("code");
                            if(addressBack == 1){
                                JSONObject addressParams = new JSONObject(addressResult.getString("data"));
                                //将地址存储进数据库
                                user.setAddress(addressParams.getString("address").trim());
                                user.save();
                                Toast.makeText(AddressActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else Toast.makeText(AddressActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else Toast.makeText(AddressActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //上传地址
    private void uploadAddress() {
        //判断输入的地址是否为空
        if(et_address_address.getText().toString().trim().isEmpty())
        {
            Toast.makeText(AddressActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
        }else if(et_address_address.getText().toString().trim().length() < 6){
            //判断输入的地址是否少于6个字
            Toast.makeText(AddressActivity.this,"请输入详细地址！",Toast.LENGTH_SHORT).show();
        } else {
            //开启子线程，发起网络请求
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("requesttop","address");
                    params.put("account", UserUtils.getCurrentUser().getAccount());
                    params.put("address", et_address_address.getText().toString().trim());
                    String Result = HttpUtils.submitPostData(getResources().getString(R.string.burl) + "Person_Servlet", params, "utf-8");
                    System.out.println("结果为：" + Result);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            });
        }
    }
}
