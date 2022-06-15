package com.myapp.qutaomarket.contoller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.adapter.MybuylistAdapter;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.model.db.User;
import com.myapp.qutaomarket.utils.HttpUtils;
import com.myapp.qutaomarket.utils.UserUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyoutActivity extends AppCompatActivity {

    //定义变量
    private ImageView iv_myout_back;

    private ListView lv_myout_list;

    //进度条
    private ProgressDialog pd;

    //订单列表
    private ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myout);

        //初始化视图
        initView();
        //获取订单数据
        getAccountData();
    }

    private void initView() {
        //初始化进度条
        pd = new ProgressDialog(MyoutActivity.this);
        pd.setMessage("数据加载中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        //初始化控件
        lv_myout_list = (ListView)findViewById(R.id.lv_myout_list);
        iv_myout_back = (ImageView)findViewById(R.id.iv_myout_back);

        iv_myout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //handler处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int getDataBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        pd.cancel();
                        try{
                            JSONArray data = new JSONArray();
                            JSONObject getDataResult = new JSONObject(msg.obj.toString().trim());
                            getDataBack = getDataResult.getInt("code");
                            if(getDataBack == 1){
                                data = getDataResult.getJSONArray("data");
                                for(int i=0;i<data.length();i++){
                                    String goodsimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("goodsdata").getString("gimage"), "utf-8");
                                    String userimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("userdata").getString("headphoto"), "utf-8");
                                    //liatview显示
                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("datatype","myout");
                                    item.put("uid",data.getJSONObject(i).getJSONObject("userdata").getInt("uid"));
                                    item.put("account",data.getJSONObject(i).getJSONObject("userdata").getString("account"));
                                    item.put("nickname",data.getJSONObject(i).getJSONObject("userdata").getString("nickname"));
                                    item.put("reputation",data.getJSONObject(i).getJSONObject("userdata").getString("reputation"));
                                    item.put("tel",data.getJSONObject(i).getJSONObject("userdata").getString("tel"));
                                    item.put("hxid",data.getJSONObject(i).getJSONObject("userdata").getString("hxid"));
                                    item.put("address",data.getJSONObject(i).getJSONObject("userdata").getString("address"));
                                    item.put("gid",data.getJSONObject(i).getJSONObject("goodsdata").getInt("gid"));
                                    item.put("gname",data.getJSONObject(i).getJSONObject("goodsdata").getString("gname"));
                                    item.put("ghownew",data.getJSONObject(i).getJSONObject("goodsdata").getString("ghownew"));
                                    item.put("gprice",Double.toString(data.getJSONObject(i).getJSONObject("goodsdata").getDouble("gprice")));
                                    item.put("ggetway",data.getJSONObject(i).getJSONObject("goodsdata").getString("ggetway"));
                                    item.put("goprice",data.getJSONObject(i).getJSONObject("goodsdata").getString("goprice"));
                                    item.put("gdetail",data.getJSONObject(i).getJSONObject("goodsdata").getString("gdetail"));
                                    item.put("gaddress",data.getJSONObject(i).getJSONObject("goodsdata").getString("gaddress"));
                                    item.put("gscannum",data.getJSONObject(i).getJSONObject("goodsdata").getString("gscannum"));
                                    item.put("gtype",data.getJSONObject(i).getJSONObject("goodsdata").getString("gtype"));
                                    item.put("gstate",data.getJSONObject(i).getJSONObject("goodsdata").getString("gstate"));
                                    item.put("gemergent",data.getJSONObject(i).getJSONObject("goodsdata").getString("gemergent"));
                                    item.put("aid",data.getJSONObject(i).getJSONObject("accountdata").getString("aid"));
                                    item.put("gid",data.getJSONObject(i).getJSONObject("accountdata").getString("gid"));
                                    item.put("uid",data.getJSONObject(i).getJSONObject("accountdata").getString("uid"));
                                    item.put("guid",data.getJSONObject(i).getJSONObject("accountdata").getString("guid"));
                                    item.put("anumber",data.getJSONObject(i).getJSONObject("accountdata").getString("anumber"));
                                    item.put("abill",data.getJSONObject(i).getJSONObject("accountdata").getString("abill"));
                                    item.put("atime",data.getJSONObject(i).getJSONObject("accountdata").getString("atime"));
                                    item.put("astate",data.getJSONObject(i).getJSONObject("accountdata").getString("astate"));
                                    item.put("gimage",getResources().getString(R.string.burl)+"Image_Servlet?" + goodsimageurl);
                                    item.put("headphoto",getResources().getString(R.string.burl)+"Image_Servlet?" + userimageurl);
                                    mData.add(item);
                                }
                                //设置我买到的适配器
                                MybuylistAdapter adapter = new MybuylistAdapter(MyoutActivity.this,mData, lv_myout_list);
                                lv_myout_list.setAdapter(adapter);
                                //设置适配器点击监听
                                lv_myout_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                            long id) {
                                        Intent intent = new Intent(view.getContext(), AccountActivity.class);
                                        intent.putExtra("data",(Serializable)mData.get(position));
                                        view.getContext().startActivity(intent);
                                    }
                                });
                            }else {
                                pd.cancel();
                                Toast.makeText(MyoutActivity.this,"还没有卖出过商品哦！",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(MyoutActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //获取订单信息
    private void getAccountData(){
        pd.show();
        User user = UserUtils.getCurrentUser();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop","getmyout");
                params.put("account",user.getAccount());
                String strUrlpath = getResources().getString(R.string.burl) + "Mybuy_Servlet";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 1;
                message.obj = Result;
                handler.sendMessage(message);
            }
        });
    }
}
