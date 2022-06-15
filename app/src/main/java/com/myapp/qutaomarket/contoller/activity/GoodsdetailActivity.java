package com.myapp.qutaomarket.contoller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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

public class GoodsdetailActivity extends AppCompatActivity {

    //定义返回数据列表
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();

    //定义变量
    private ImageView iv_gdetail_headphoto,iv_goodsdetail_back,iv_gdetail_gimage;

    private TextView tv_detail_nickname,tv_gdetail_repu,tv_gdetail_gname,tv_detail_price,tv_gdetail_goprice,tv_gdetail_gtype,
            tv_detail_hownew,tv_detail_getway,tv_gdetail_gdetail,tv_detail_city,tv_gdetail_pnumber,tv_gdetail_send,tv_gdetail_buy;

    private EditText et_gdetail_input;

    private ListView lv_detail_comments;

//    private LinearLayout ll_detail_chat;

//    //定义是否被收藏
//    private int isCollected = 0;
//
//    //收藏id
//    private String colid;

    //按键监听变量
    private GoodsdetailActivity.OnClickListener listener;

    //进度条
    private ProgressDialog pd;

    //定义从其他活动中传过来的数据变量
    Map<String, Object> uagdata = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodsdetail);

        //接收数据
        uagdata = (Map<String, Object>) getIntent().getSerializableExtra("data");

        //初始化视图
        initView();
        //初始化数据
        initData();
    }

    //初始化视图
    private void initView() {
        //初始化进度条
        pd = new ProgressDialog(GoodsdetailActivity.this);
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        //imageView
        iv_gdetail_headphoto = (ImageView)findViewById(R.id.iv_gdetail_headphoto);
//        iv_gdetail_call = (ImageView)findViewById(R.id.iv_gdetail_call);
//        iv_gdetail_collect = (ImageView)findViewById(R.id.iv_gdetail_collect);
        iv_goodsdetail_back = (ImageView)findViewById(R.id.iv_goodsdetail_back);
        //RoundAngleImageView
        iv_gdetail_gimage = (ImageView)findViewById(R.id.iv_gdetail_gimage);
        //TextView
        tv_detail_nickname = (TextView)findViewById(R.id.tv_detail_nickname);
        tv_gdetail_repu = (TextView)findViewById(R.id.tv_gdetail_repu);
        tv_gdetail_gname = (TextView)findViewById(R.id.tv_gdetail_gname);
        tv_detail_price = (TextView)findViewById(R.id.tv_detail_price);
        tv_gdetail_goprice = (TextView)findViewById(R.id.tv_gdetail_goprice);
        tv_gdetail_gtype = (TextView)findViewById(R.id.tv_gdetail_gtype);
        tv_detail_hownew = (TextView)findViewById(R.id.tv_detail_hownew);
        tv_detail_getway = (TextView)findViewById(R.id.tv_detail_getway);
        tv_gdetail_gdetail = (TextView)findViewById(R.id.tv_gdetail_gdetail);
        tv_detail_city = (TextView)findViewById(R.id.tv_detail_city);
//        tv_detail_number = (TextView)findViewById(R.id.tv_detail_number);
        tv_gdetail_pnumber = (TextView)findViewById(R.id.tv_gdetail_pnumber);
        tv_gdetail_send = (TextView)findViewById(R.id.tv_gdetail_send);
        tv_gdetail_buy = (TextView)findViewById(R.id.tv_gdetail_buy);
        //EditView
        et_gdetail_input = (EditText)findViewById(R.id.et_gdetail_input);
        //ListView
        lv_detail_comments = (ListView)findViewById(R.id.lv_detail_comments);
//        //LinearLayout
//        ll_detail_chat = (LinearLayout)findViewById(R.id.ll_detail_chat);

        //初始化监听
        listener = new OnClickListener();
//        iv_gdetail_call.setOnClickListener(listener);
//        iv_gdetail_collect.setOnClickListener(listener);
//        ll_detail_chat.setOnClickListener(listener);
        tv_gdetail_send.setOnClickListener(listener);
        tv_gdetail_buy.setOnClickListener(listener);
        iv_goodsdetail_back.setOnClickListener(listener);
    }

    //初始化数据
    private void initData(){
        //加载用户头像
        if((getResources().getString(R.string.burl)+"Image_Servlet?null").equals(uagdata.get("headphoto").toString().trim())){
            Glide.with(this)
                    .load(R.drawable.moren_headphoto)
                    .circleCrop()
                    .placeholder(R.drawable.moren_headphoto)
                    .error(R.drawable.moren_headphoto)
                    .into(iv_gdetail_headphoto);
        }else {
            Glide.with(this)
                    .load(uagdata.get("headphoto").toString().trim())
                    .circleCrop()
                    .placeholder(R.drawable.moren_headphoto)
                    .error(R.drawable.moren_headphoto)
                    .into(iv_gdetail_headphoto);
        }
        //加载昵称
        tv_detail_nickname.setText(uagdata.get("nickname").toString());
        //加载信誉度
        int value = Integer.valueOf(uagdata.get("reputation").toString());
        if(value<500){
            tv_gdetail_repu.setText("信用一般");
        }else if((value>=500)&&(value<700)){
            tv_gdetail_repu.setText("信用良好");
        }else if((value>=700)&&(value<900)){
            tv_gdetail_repu.setText("信用优秀");
        }else if((value>=900)&&(value<=1000)){
            tv_gdetail_repu.setText("信用极好");
        }
        //加载商品名称
        tv_gdetail_gname.setText(uagdata.get("gname").toString());
        //设置价格
        tv_detail_price.setText("¥ " + uagdata.get("gprice").toString());
        tv_gdetail_goprice.setText("¥ " + uagdata.get("goprice").toString());
        tv_gdetail_goprice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
        //商品类别
        tv_gdetail_gtype.setText(uagdata.get("gtype").toString());
        //新旧程度
        tv_detail_hownew.setText(uagdata.get("ghownew").toString());
        //获取方式
        tv_detail_getway.setText(uagdata.get("ggetway").toString());
        //商品详情
        tv_gdetail_gdetail.setText(uagdata.get("gdetail").toString());
        //加载商品图片
        RequestOptions options = new RequestOptions();
        options.centerCrop()
                .placeholder(R.drawable.ic_moren_goods)
                .error(R.drawable.ic_moren_goods)
                .fallback(R.drawable.ic_moren_goods)
                .transform(new RoundTransform(this));
        Glide.with(this)
                .applyDefaultRequestOptions(options)
                .load(uagdata.get("gimage").toString())
                .into(iv_gdetail_gimage);
        //设置发布地点
        tv_detail_city.setText(uagdata.get("gaddress").toString());
//        //设置浏览人数
//        tv_detail_number.setText(uagdata.get("gscannum").toString());
        //如果商品状态不是1,则把购买按钮去掉
        if(Integer.valueOf(uagdata.get("gstate").toString()) != 1){
            tv_gdetail_buy.setVisibility(View.GONE);
        }

        //长按删除评论，私信回复
        lv_detail_comments.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(Menu.NONE, 0, 0, "删除");
                menu.add(Menu.NONE, 1, 0, "私信");
            }
        });

//        //设置浏览人数加一
//        setScannum();
//        //获取是否被收藏
//        getCollect();
    }

    //handler处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case 1:
                    int getConBack;
                    JSONArray data = new JSONArray();
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject getConResult = new JSONObject(msg.obj.toString().trim());
                            getConBack = getConResult.getInt("code");
                            if(getConBack == 1){
                                //获取评论数据
                                data = getConResult.getJSONArray("data");
                                tv_gdetail_pnumber.setText(String.valueOf(data.length()));
                                //遍历所有的评论并添加到数据列表中
                                for(int i = 0; i < data.length(); i++) {
                                    String userimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("userdata").getString("headphoto"), "utf-8");
                                    Map<String, Object> conment = new HashMap<>();
                                    conment.put("headphoto", getResources().getString(R.string.burl)+"Image_Servlet?" + userimageurl);
                                    conment.put("hxid", data.getJSONObject(i).getJSONObject("userdata").getString("hxid"));
                                    conment.put("account", data.getJSONObject(i).getJSONObject("userdata").getString("account"));
                                    conment.put("nickname", data.getJSONObject(i).getJSONObject("userdata").getString("nickname"));
                                    conment.put("content", data.getJSONObject(i).getJSONObject("conmentsdata").getString("concontent"));
                                    conment.put("time", data.getJSONObject(i).getJSONObject("conmentsdata").getString("contime"));
                                    conment.put("conid", data.getJSONObject(i).getJSONObject("conmentsdata").getString("conid"));
                                    mData.add(conment);
                                }
                            } else if(getConBack == 0){
                                tv_gdetail_pnumber.setText("0");
                            }else {
                                Toast.makeText(GoodsdetailActivity.this,"获取评论失败！",Toast.LENGTH_SHORT).show();
                            }
                            //设置评论列表的适配器
                            ConmentslistAdapter adapter = new ConmentslistAdapter(GoodsdetailActivity.this, mData);
                            adapter.notifyDataSetChanged();
                            lv_detail_comments.setAdapter(adapter);
                            SetViewHeight.setListViewHeight(lv_detail_comments);
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
                        Toast.makeText(GoodsdetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
//                case 2:
//                    //是否被收藏的返回结果
//                    int getCollectBack;
//                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
//                    {
//                        try{
//                            JSONObject getCollectResult = new JSONObject(msg.obj.toString().trim());
//                            getCollectBack = getCollectResult.getInt("code");
//                            if(getCollectBack == 1){
//                                isCollected = 1;
//                                colid = getCollectResult.getString("colid").trim();
//                                iv_gdetail_collect.setImageResource(R.drawable.ic_collected);
//                            } else if(getCollectBack == 2){
//                                isCollected = 0;
//                                iv_gdetail_collect.setImageResource(R.drawable.ic_collect);
//                            }else {
//                                isCollected = 0;
//                                iv_gdetail_collect.setImageResource(R.drawable.ic_collect);
//                            }
//                            //获取评论
//                            getConments();
//                        }catch (JSONException e){
//                            e.printStackTrace();
//                        }
//                    }else {
//                        isCollected = 0;
//                        iv_gdetail_collect.setImageResource(R.drawable.ic_collect);
//                        //获取评论
//                        getConments();
//                    }
//                    break;
//                case 3:
//                    //收藏商品的返回结果
//                    int collectBack;
//                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
//                    {
//                        try{
//                            JSONObject collectResult = new JSONObject(msg.obj.toString().trim());
//                            collectBack = collectResult.getInt("code");
//                            if(collectBack == 1){
//                                isCollected = 1;
//                                iv_gdetail_collect.setImageResource(R.drawable.ic_collected);
//                                Toast.makeText(GoodsdetailActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(GoodsdetailActivity.this,"收藏失败",Toast.LENGTH_SHORT).show();
//                            }
//                        }catch (JSONException e){
//                            e.printStackTrace();
//                        }
//                    }else {
//                        Toast.makeText(GoodsdetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                case 4:
//                    //取消收藏的返回结果
//                    int unCollectBack;
//                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
//                    {
//                        try{
//                            JSONObject unCollectResult = new JSONObject(msg.obj.toString().trim());
//                            unCollectBack = unCollectResult.getInt("code");
//                            if(unCollectBack == 1){
//                                isCollected = 0;
//                                iv_gdetail_collect.setImageResource(R.drawable.ic_collect);
//                                Toast.makeText(GoodsdetailActivity.this,"取消成功",Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(GoodsdetailActivity.this,"取消失败",Toast.LENGTH_SHORT).show();
//                            }
//                        }catch (JSONException e){
//                            e.printStackTrace();
//                        }
//                    }else {
//                        Toast.makeText(GoodsdetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
//                    }
//                    break;
                case 5:
                    //购买商品的结果
                    int buyBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject buyResult = new JSONObject(msg.obj.toString().trim());
                            buyBack = buyResult.getInt("code");
                            if(buyBack == 1){
                                User user = UserUtils.getCurrentUser();
                                user.setBalance(buyResult.getDouble("balance"));
                                user.save();
                                pd.cancel();
                                Toast.makeText(GoodsdetailActivity.this,"订单已提交",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(GoodsdetailActivity.this,"提交订单失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(GoodsdetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 6:
                    //发送评论的返回结果
                    int sendBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject sendResult = new JSONObject(msg.obj.toString().trim());
                            sendBack = sendResult.getInt("code");
                            if(sendBack == 1){
                                getConments();
                                et_gdetail_input.setText("");
                                pd.cancel();
                                Toast.makeText(GoodsdetailActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                            } else {
                                pd.cancel();
                                Toast.makeText(GoodsdetailActivity.this,"评论失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(GoodsdetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 7:
                    //删除评论的返回结果
                    int delConmentBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject delConmentResult = new JSONObject(msg.obj.toString().trim());
                            delConmentBack = delConmentResult.getInt("code");
                            if(delConmentBack == 1){
                                //重新从服务器获取评论
                                getConments();
                                pd.cancel();
                                Toast.makeText(GoodsdetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            } else {
                                pd.cancel();
                                Toast.makeText(GoodsdetailActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(GoodsdetailActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
//                case 8:
//                    //获取浏览人数的返回结果
//                    int scannumBack;
//                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
//                    {
//                        try{
//                            JSONObject scannumResult = new JSONObject(msg.obj.toString().trim());
//                            scannumBack = scannumResult.getInt("code");
//                            if(scannumBack == 1){
//                                //设置浏览人数
//                                tv_detail_number.setText(scannumResult.getString("data"));
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
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_goodsdetail_back:
                    finish();
                    break;
//                case R.id.iv_gdetail_call:
//                    userCall(uagdata.get("tel").toString());
//                    break;
//                case R.id.iv_gdetail_collect:
//                    setCollect();
//                    break;
//                case R.id.ll_detail_chat:
//                    userChat(uagdata.get("account").toString());
//                    break;
                case R.id.tv_gdetail_send:
                    sendConments();
                    break;
                case R.id.tv_gdetail_buy:
                    buyGoods();
                    break;
                default:
                    break;
            }
        }
    }

    //获取评论
    private void getConments(){
        mData.clear();
        pd.setMessage("数据加载中...");
        pd.show();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop","getconments");
                params.put("gid",uagdata.get("gid").toString());
                String strUrlpath = getResources().getString(R.string.burl) + "Goodsdetail_Servlet";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 1;
                message.obj = Result;
                handler.sendMessage(message);
            }
        });
    }

//    //获取商品是否被收藏
//    private void getCollect(){
//        User user = UserUtils.getCurrentUser();
//        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("requesttop","getcollect");
//                params.put("account",user.getAccount());
//                params.put("gid",uagdata.get("gid").toString());
//                String strUrlpath = getResources().getString(R.string.burl) + "Goodsdetail_Servlet";
//                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
//                System.out.println("结果为：" + Result);
//                Message message = new Message();
//                message.what = 2;
//                message.obj = Result;
//                handler.sendMessage(message);
//            }
//        });
//    }
//
//    //收藏或取消收藏
//    private void setCollect(){
//        //获取时间
//        final Date d = new Date();
//        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        User user = UserUtils.getCurrentUser();
//        //判断是不是自己的商品
//        if(user.getAccount().equals(uagdata.get("account").toString())){
//            Toast.makeText(GoodsdetailActivity.this, "不能收藏自己的商品！", Toast.LENGTH_SHORT).show();
//        }else {
//            if (isCollected == 0) {           //收藏
//                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("requesttop", "collect");
//                        params.put("account", user.getAccount());
//                        params.put("uid", uagdata.get("uid").toString());
//                        params.put("gid", uagdata.get("gid").toString());
//                        params.put("time", sdf.format(d));
//                        String strUrlpath = getResources().getString(R.string.burl) + "Goodsdetail_Servlet";
//                        String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
//                        System.out.println("结果为：" + Result);
//                        Message message = new Message();
//                        message.what = 3;
//                        message.obj = Result;
//                        handler.sendMessage(message);
//                    }
//                });
//            } else if (isCollected == 1) {             //取消收藏
//                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("requesttop", "uncollect");
//                        params.put("colid", colid);
//                        String strUrlpath = getResources().getString(R.string.burl) + "Goodsdetail_Servlet";
//                        String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
//                        System.out.println("结果为：" + Result);
//                        Message message = new Message();
//                        message.what = 4;
//                        message.obj = Result;
//                        handler.sendMessage(message);
//                    }
//                });
//            }
//        }
//    }

//    //用户聊天
//    private void userChat(String hxid){
//        if(UserUtils.getCurrentUser().getHxId().trim().equals(hxid)){
//            Toast.makeText(GoodsdetailActivity.this, "不能跟自己聊天！", Toast.LENGTH_SHORT).show();
//        }else {
//            Intent intent = new Intent(GoodsdetailActivity.this, ChatActivity.class);
//            //传递参数
//            intent.putExtra(EaseConstant.EXTRA_USER_ID, hxid);
//            //单聊
//            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
//            startActivity(intent);
//        }
//    }

    //调用发起通话的页面
    private void userCall(String tel){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+ tel));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
    }

    //购买
    private void buyGoods(){
        //获取时间
        final Date d = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前用户
        User user = UserUtils.getCurrentUser();
        //判断是否是自己的商品
        if(user.getAccount().trim().equals(uagdata.get("account").toString())) Toast.makeText(GoodsdetailActivity.this,"不能买自己的宝贝",Toast.LENGTH_SHORT).show();
        //判断余额够不够
        else if(Double.valueOf(uagdata.get("gprice").toString()) > user.getBalance()){
            Toast.makeText(GoodsdetailActivity.this,"余额不足请充值",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GoodsdetailActivity.this,PersonActivity.class);
            startActivity(intent);
        } else if("null".equals(user.getAddress().trim())){           //判断地址是不是空
            Toast.makeText(GoodsdetailActivity.this,"地址不能为空",Toast.LENGTH_SHORT).show();
            //跳转到个人信息页面设置地址
            Intent intent = new Intent(GoodsdetailActivity.this,PersonActivity.class);
            startActivity(intent);
        } else {
            //设置提示
            AlertDialog.Builder normalDialog = new AlertDialog.Builder(GoodsdetailActivity.this);
            normalDialog.setTitle("提示");
            normalDialog.setMessage("你确定要购买吗？");
            normalDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //显示进度条
                            pd.setMessage("下单中...");
                            pd.show();
                            //开启进程
                            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("requesttop","buygoods");
                                    params.put("account",user.getAccount());
                                    params.put("guid",uagdata.get("uid").toString());
                                    params.put("gid",uagdata.get("gid").toString());
                                    params.put("gname",uagdata.get("gname").toString());
                                    params.put("atime",sdf.format(d));
                                    params.put("abill",uagdata.get("gprice").toString());
                                    params.put("hxid",uagdata.get("hxid").toString());
                                    params.put("gname",uagdata.get("gname").toString());
                                    params.put("astate","1");
                                    String strUrlpath = getResources().getString(R.string.burl) + "Goodsdetail_Servlet";
                                    String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                    System.out.println("结果为：" + Result);
                                    Message message = new Message();
                                    message.what = 5;
                                    message.obj = Result;
                                    handler.sendMessage(message);
                                }
                            });
                        }
                    });
            normalDialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //...To-do
                        }
                    });
            // 显示
            normalDialog.show();
        }
    }

    //发送评论
    private void sendConments(){
        //获取时间
        final Date d = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User user = UserUtils.getCurrentUser();
        //判断输入是否为空
        if(et_gdetail_input.getText().toString().trim().isEmpty())
        {
            Toast.makeText(GoodsdetailActivity.this,"评论不能为空哦！",Toast.LENGTH_SHORT).show();
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
                    params.put("gid",uagdata.get("gid").toString());
                    params.put("gname",uagdata.get("gname").toString());
                    params.put("concontent",et_gdetail_input.getText().toString().trim());
                    params.put("contime",sdf.format(d));
                    params.put("constate","1");
                    String strUrlpath = getResources().getString(R.string.burl) + "Goodsdetail_Servlet";
                    String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                    System.out.println("结果为：" + Result);
                    Message message = new Message();
                    message.what = 6;
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
                params.put("conid",Integer.toString(conid));
                String strUrlpath = getResources().getString(R.string.burl) + "Goodsdetail_Servlet";
                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                System.out.println("结果为：" + Result);
                Message msg = new Message();
                msg.what = 7;
                msg.obj = Result;
                handler.sendMessage(msg);
            }
        });
    }

    //评论长按的点击事件处理
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        User user = UserUtils.getCurrentUser();
        Map<String,Object> item2 = mData.get((int) menuInfo.id);
        switch (item.getItemId()){
            case 0:           //删除评论
                if(user.getAccount().equals(item2.get("account").toString())||user.getAccount().equals(uagdata.get("account").toString())){
                    deleteConment(Integer.valueOf(item2.get("conid").toString()));
                }else Toast.makeText(GoodsdetailActivity.this, "您不能删除该评论！", Toast.LENGTH_SHORT).show();
                break;
//            case 1:          //私信
//                userChat(item2.get("account").toString());
//                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

//    //设置浏览人数加一
//    private void setScannum(){
//        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("requesttop","setscannum");
//                params.put("gid",uagdata.get("gid").toString());
//                String strUrlpath = getResources().getString(R.string.burl) + "Goodsdetail_Servlet";
//                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
//                System.out.println("结果为：" + Result);
//                Message msg = new Message();
//                msg.what = 8;
//                msg.obj = Result;
//                handler.sendMessage(msg);
//            }
//        });
//    }
}
