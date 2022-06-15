package com.myapp.qutaomarket.contoller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.EaseConstant;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.adapter.ConmentslistAdapter;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.model.db.User;
import com.myapp.qutaomarket.utils.HttpUtils;
import com.myapp.qutaomarket.utils.RoundTransform;
import com.myapp.qutaomarket.utils.SetViewHeight;
import com.myapp.qutaomarket.utils.UserUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//求购商品细节的Activity
public class BuydetailActivity extends AppCompatActivity {

    //定义数据的接受列表
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();

    //定义ImageView变量
    private ImageView iv_buydetail_back,iv_buydetail_headphoto;

    //定义TextView变量
    private TextView tv_buydetail_title,tv_buydetail_nickname,tv_buydetail_repu,tv_buydetail_bname,tv_buydetail_sprice,tv_buydetail_bprice,
            tv_buydetail_bgetway,tv_buydetail_bhownew,tv_buydetail_btype,tv_buydetail_bcontent,tv_buydetail_bcity,
            tv_buydetail_baccountnum,tv_buydetail_bsend;

    //定义ListView变量
    private ListView lv_buydetail_comments;

    //定义LinearLayout变量
    private LinearLayout ll_buydetail_chat;

    //定义EditText变量
    private EditText et_buydetail_conments;

    //设置监听变量
    private BuydetailActivity.OnClickListener listener;

    //进度条
    private ProgressDialog pd;

    //定义数据接收变量
    Map<String, Object> uagdata = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buydetail);

        //接收数据
        uagdata = (Map<String, Object>) getIntent().getSerializableExtra("data");

        //初始化视图
        initView();
        //初始化数据
        initData();
        //获取评论信息
        getConments();
    }

    //初始化视图
    private void initView(){
        //初始化进度条
        pd = new ProgressDialog(BuydetailActivity.this);
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        //ImageView
        iv_buydetail_back = (ImageView)findViewById(R.id.iv_buydetail_back);
        iv_buydetail_headphoto = (ImageView)findViewById(R.id.iv_buydetail_headphoto);
        //TextView
        tv_buydetail_title = (TextView)findViewById(R.id.tv_buydetail_title);
        tv_buydetail_nickname = (TextView)findViewById(R.id.tv_buydetail_nickname);
        tv_buydetail_repu = (TextView)findViewById(R.id.tv_buydetail_repu);
        tv_buydetail_bname = (TextView)findViewById(R.id.tv_buydetail_bname);
        tv_buydetail_sprice = (TextView)findViewById(R.id.tv_buydetail_sprice);
        tv_buydetail_bprice = (TextView)findViewById(R.id.tv_buydetail_bprice);
        tv_buydetail_bgetway = (TextView)findViewById(R.id.tv_buydetail_bgetway);
        tv_buydetail_bhownew = (TextView)findViewById(R.id.tv_buydetail_bhownew);
        tv_buydetail_bcontent = (TextView)findViewById(R.id.tv_buydetail_bcontent);
        tv_buydetail_btype = (TextView)findViewById(R.id.tv_buydetail_btype);
        tv_buydetail_bcity = (TextView)findViewById(R.id.tv_buydetail_bcity);
//        tv_buydetail_bnumber = (TextView)findViewById(R.id.tv_buydetail_bnumber);
        tv_buydetail_baccountnum = (TextView)findViewById(R.id.tv_buydetail_baccountnum);
        tv_buydetail_bsend = (TextView)findViewById(R.id.tv_buydetail_bsend);
        //ListView
        lv_buydetail_comments = (ListView)findViewById(R.id.lv_buydetail_comments);
        //LinearLayout
        ll_buydetail_chat = (LinearLayout)findViewById(R.id.ll_buydetail_chat);
        //EditText
        et_buydetail_conments = (EditText)findViewById(R.id.et_buydetail_conments);

        //初始化监听
        listener = new OnClickListener();
        iv_buydetail_back.setOnClickListener(listener);
        tv_buydetail_bsend.setOnClickListener(listener);
    }

    //初始化数据
    private void initData(){
        //加载用户头像，判断有没有设置头像，如果设置过头像则加载头像，否则加载默认头像
        if((getResources().getString(R.string.burl)+"Image_Servlet?null").equals(uagdata.get("headphoto").toString().trim())){
            Glide.with(this)
                    .load(R.drawable.moren_headphoto)
                    .circleCrop()
                    .placeholder(R.drawable.moren_headphoto)
                    .error(R.drawable.moren_headphoto)
                    .into(iv_buydetail_headphoto);
        }else {
            Glide.with(this)
                    .load(uagdata.get("headphoto").toString().trim())
                    .circleCrop()
                    .placeholder(R.drawable.moren_headphoto)
                    .error(R.drawable.moren_headphoto)
                    .into(iv_buydetail_headphoto);
        }
        //加载昵称
        tv_buydetail_nickname.setText(uagdata.get("nickname").toString());
        //加载信誉度
        int value = Integer.valueOf(uagdata.get("reputation").toString());
        if(value<500){
            tv_buydetail_repu.setText("信用一般");
        }else if((value>=500)&&(value<700)){
            tv_buydetail_repu.setText("信用良好");
        }else if((value>=700)&&(value<900)){
            tv_buydetail_repu.setText("信用优秀");
        }else if((value>=900)&&(value<=1000)){
            tv_buydetail_repu.setText("信用极好");
        }
        //加载商品名称
        tv_buydetail_bname.setText(uagdata.get("bname").toString());
        //设置价格
        tv_buydetail_sprice.setText(uagdata.get("bsprice").toString());
        tv_buydetail_bprice.setText(uagdata.get("bbprice").toString());
        //商品类别
        tv_buydetail_btype.setText(uagdata.get("btype").toString());
        //新旧程度
        tv_buydetail_bhownew.setText(uagdata.get("bhownew").toString());
        //获取方式
        tv_buydetail_bgetway.setText(uagdata.get("bgetway").toString());
        //商品详情
        tv_buydetail_bcontent.setText(uagdata.get("bdetail").toString());
        //加载商品图片
        RequestOptions options = new RequestOptions();
        options.centerCrop()
                .placeholder(R.drawable.ic_moren_goods)
                .error(R.drawable.ic_moren_goods)
                .fallback(R.drawable.ic_moren_goods)
                .transform(new RoundTransform(this));
//        Glide.with(this)
//                .applyDefaultRequestOptions(options)
//                .load(uagdata.get("bimage").toString())
//                .into(iv_buydetail_image);
        //设置发布地点
        tv_buydetail_bcity.setText(uagdata.get("baddress").toString());
//        //设置浏览人数
//        tv_buydetail_bnumber.setText(uagdata.get("bscannum").toString());
//        setScannum();
    }

    //handler处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int getConBack;
                    JSONArray data = new JSONArray();
                    //如果返回了数据
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            //解析出code字段
                            JSONObject getConResult = new JSONObject(msg.obj.toString().trim());
                            getConBack = getConResult.getInt("code");
                            //如果code=1则表示返回正确的数据
                            if(getConBack == 1){
                                //获取返回的数据
                                data = getConResult.getJSONArray("data");
                                tv_buydetail_baccountnum.setText(String.valueOf(data.length()));
                                //把返回的数据列表依次遍历存入数据列表里
                                for(int i = 0; i < data.length(); i++) {
                                    String userimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("userdata").getString("headphoto"), "utf-8");
                                    Map<String, Object> conment = new HashMap<>();
                                    conment.put("headphoto", getResources().getString(R.string.burl)+"Image_Servlet?" + userimageurl);
                                    conment.put("hxid", data.getJSONObject(i).getJSONObject("userdata").getString("hxid"));
                                    conment.put("account", data.getJSONObject(i).getJSONObject("userdata").getString("account"));
                                    conment.put("nickname", data.getJSONObject(i).getJSONObject("userdata").getString("nickname"));
                                    conment.put("content", data.getJSONObject(i).getJSONObject("conmentsdata").getString("bconcontent"));
                                    conment.put("time", data.getJSONObject(i).getJSONObject("conmentsdata").getString("bcontime"));
                                    conment.put("conid", data.getJSONObject(i).getJSONObject("conmentsdata").getString("bconid"));
                                    mData.add(conment);
                                }
                            } else if(getConBack == 0){
                                tv_buydetail_baccountnum.setText("0");
                            }
                            //把数据添加到适配器里
                            ConmentslistAdapter adapter = new ConmentslistAdapter(BuydetailActivity.this, mData);
                            adapter.notifyDataSetChanged();
                            //设置适配器
                            lv_buydetail_comments.setAdapter(adapter);
                            SetViewHeight.setListViewHeight(lv_buydetail_comments);
                            pd.cancel();
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(BuydetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    int sendBack;
                    //发送评论的返回结果
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject sendResult = new JSONObject(msg.obj.toString().trim());
                            sendBack = sendResult.getInt("code");
                            if(sendBack == 1){
                                getConments();
                                et_buydetail_conments.setText("");
                                pd.cancel();
                                Toast.makeText(BuydetailActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                            } else {
                                pd.cancel();
                                Toast.makeText(BuydetailActivity.this,"评论失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(BuydetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 3:
                    int delConmentBack;
                    //删除评论的返回结果
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject delConmentResult = new JSONObject(msg.obj.toString().trim());
                            delConmentBack = delConmentResult.getInt("code");
                            if(delConmentBack == 1){
                                //重新从服务器获取评论
                                getConments();
                                pd.cancel();
                                Toast.makeText(BuydetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            } else {
                                pd.cancel();
                                Toast.makeText(BuydetailActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(BuydetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
//                case 4:
//                    int scannumBack;
//                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
//                    {
//                        try{
//                            JSONObject scannumResult = new JSONObject(msg.obj.toString().trim());
//                            scannumBack = scannumResult.getInt("code");
//                            if(scannumBack == 1){
//                                //设置浏览人数
//                                tv_buydetail_bnumber.setText(scannumResult.getString("data"));
//                            }
//                        }catch (JSONException e){
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
                    default:
                        break;
            }
        }
    };

    //按键监听
    private class OnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //返回按键
                case R.id.iv_buydetail_back:
                    finish();
                    break;
                //发送评论的按键
                case R.id.tv_buydetail_bsend:
                    sendConments();
                    break;
                default:
                    break;
            }
        }
    }

    //获取评论
    private void getConments(){
        //或缺数据之前清空数据列表
        mData.clear();
        //设置进度条
        pd.setMessage("数据加载中...");
        pd.show();
        //开启进程
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop","getconments");
                params.put("bid",uagdata.get("bid").toString());
                String strUrlpath = getResources().getString(R.string.burl) + "Buydetail_Servlet";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 1;
                message.obj = Result;
                handler.sendMessage(message);
            }
        });
    }

    //发送评论
    private void sendConments(){
        //获取时间
        final Date d = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User user = UserUtils.getCurrentUser();
        if(et_buydetail_conments.getText().toString().trim().isEmpty())
        {
            Toast.makeText(BuydetailActivity.this,"评论不能为空哦！",Toast.LENGTH_SHORT).show();
        } else {
            pd.setMessage("评论中...");
            pd.show();
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("requesttop","conments");
                    params.put("account",user.getAccount());
                    params.put("uid",uagdata.get("uid").toString());
                    params.put("bid",uagdata.get("bid").toString());
                    params.put("bname",uagdata.get("bname").toString());
                    params.put("bconcontent",et_buydetail_conments.getText().toString().trim());
                    params.put("bcontime",sdf.format(d));
                    params.put("bconstate","1");
                    String strUrlpath = getResources().getString(R.string.burl) + "Buydetail_Servlet";
                    String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                    System.out.println("结果为：" + Result);
                    Message message = new Message();
                    message.what = 2;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            });
        }
    }

    //删除评论
    private void deleteConment(int conid){
        pd.setMessage("请稍后...");
        pd.show();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop","deletecomments");
                params.put("bconid",Integer.toString(conid));
                String strUrlpath = getResources().getString(R.string.burl) + "Buydetail_Servlet";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message msg = new Message();
                msg.what = 3;
                msg.obj = Result;
                handler.sendMessage(msg);
            }
        });
    }

//    //设置浏览人数加一
//    private void setScannum(){
//        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("requesttop","setscannum");
//                params.put("bid",uagdata.get("bid").toString());
//                String strUrlpath = getResources().getString(R.string.burl) + "Buydetail_Servlet";
//                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
//                System.out.println("结果为：" + Result);
//                Message msg = new Message();
//                msg.what = 4;
//                msg.obj = Result;
//                handler.sendMessage(msg);
//            }
//        });
//    }

}
