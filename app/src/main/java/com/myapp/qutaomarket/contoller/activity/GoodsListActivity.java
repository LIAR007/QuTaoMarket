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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.adapter.BuylistAdapter;
import com.myapp.qutaomarket.contoller.adapter.GoodslistAdapter;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoodsListActivity extends AppCompatActivity {

    //定义变量
    private String goodsType,searchType;

    private TextView tv_goodslist_title,tv_goodslist_morentext,tv_goodslist_renqitext,tv_goodslist_timetext,tv_goodslist_lpricetext,tv_goodslist_hpricetext;

    private ImageView iv_goodslist_back;

    private LinearLayout ll_goodslist_moren,ll_goodslist_renqi,ll_goodslist_time,ll_goodslist_lprice,ll_goodslist_hprice,ll_goodslist_first;

    private ListView lv_goodslist_list;

    private GoodsListActivity.onClickListener listener;

    //定义颜色值
    private int Black = 0xFF000000;
    private int Red =0xFFFF0000;

    //进度条
    private ProgressDialog pd;

    //商品列表
    private ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_list);

        //接收intent传过来的值
        Intent goodsListIntent = getIntent();
        //搜索类型，搜索商品名还是商品类型
        searchType = goodsListIntent.getStringExtra("searchType");
        //要搜索的商品名或者是商品类型
        goodsType = goodsListIntent.getStringExtra("goodsType");
        //初始化变量
        initView();
        //初始化状态
        initState();
        //获取数据
        getGoodsData();
    }

    private void initView() {
        //初始化进度条
        pd = new ProgressDialog(GoodsListActivity.this);
        pd.setMessage("数据加载中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        //textView
        tv_goodslist_morentext = (TextView) findViewById(R.id.tv_goodslist_morentext);
        tv_goodslist_renqitext = (TextView) findViewById(R.id.tv_goodslist_renqitext);
        tv_goodslist_timetext = (TextView) findViewById(R.id.tv_goodslist_timetext);
        tv_goodslist_lpricetext = (TextView) findViewById(R.id.tv_goodslist_lpricetext);
        tv_goodslist_hpricetext = (TextView) findViewById(R.id.tv_goodslist_hpricetext);
        tv_goodslist_title = (TextView) findViewById(R.id.tv_goodslist_title);
        //将标题栏设置成相对应的名称
        tv_goodslist_title.setText(goodsType);
        //ImageView
        iv_goodslist_back = (ImageView) findViewById(R.id.iv_goodslist_back);
        //LinearLayout
        ll_goodslist_moren = (LinearLayout)findViewById(R.id.ll_goodslist_moren);
        ll_goodslist_renqi = (LinearLayout)findViewById(R.id.ll_goodslist_renqi);
        ll_goodslist_time = (LinearLayout)findViewById(R.id.ll_goodslist_time);
        ll_goodslist_lprice = (LinearLayout)findViewById(R.id.ll_goodslist_lprice);
        ll_goodslist_hprice = (LinearLayout)findViewById(R.id.ll_goodslist_hprice);
        ll_goodslist_first = (LinearLayout)findViewById(R.id.ll_goodslist_first);
        //ListView
        lv_goodslist_list = (ListView)findViewById(R.id.lv_goodslist_list);
        //返回按钮的监听
        listener = new onClickListener();
        iv_goodslist_back.setOnClickListener(listener);
        //LinaerLayout的点击监听
        ll_goodslist_moren.setOnClickListener(listener);
        ll_goodslist_renqi.setOnClickListener(listener);
        ll_goodslist_time.setOnClickListener(listener);
        ll_goodslist_lprice.setOnClickListener(listener);
        ll_goodslist_hprice.setOnClickListener(listener);

        if("goodsFree".equals(searchType.trim()) || "goodsBuy".equals(searchType.trim())){
            ll_goodslist_first.setVisibility(View.GONE);
        }
    }

    //handler处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int goodsBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        pd.cancel();
                        try{
                            JSONArray data = new JSONArray();
                            JSONObject goodsResult = new JSONObject(msg.obj.toString().trim());
                            goodsBack = goodsResult.getInt("code");
                            if(goodsBack == 1){
                                data = goodsResult.getJSONArray("data");
                                for(int i=0;i<data.length();i++){
                                    String goodsimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("goodsdata").getString("gimage"), "utf-8");
                                    String userimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("userdata").getString("headphoto"), "utf-8");
                                    //把返回的数据设置到数据列表里面
                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("uid",data.getJSONObject(i).getJSONObject("userdata").getInt("uid"));
                                    item.put("account",data.getJSONObject(i).getJSONObject("userdata").getString("account"));
                                    item.put("nickname",data.getJSONObject(i).getJSONObject("userdata").getString("nickname"));
                                    item.put("reputation",data.getJSONObject(i).getJSONObject("userdata").getString("reputation"));
                                    item.put("tel",data.getJSONObject(i).getJSONObject("userdata").getString("tel"));
                                    item.put("hxid",data.getJSONObject(i).getJSONObject("userdata").getString("hxid"));
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
                                    item.put("gimage",getResources().getString(R.string.burl)+"Image_Servlet?" + goodsimageurl);
                                    item.put("headphoto",getResources().getString(R.string.burl)+"Image_Servlet?" + userimageurl);
                                    mData.add(item);
                                }
                                //设置商品列表适配器
                                GoodslistAdapter adapter = new GoodslistAdapter(GoodsListActivity.this,mData, lv_goodslist_list);
                                lv_goodslist_list.setAdapter(adapter);
                                //设置商品列表的点击事件
                                lv_goodslist_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                            long id) {
                                        //跳转到商品详情页面
                                        Intent intent = new Intent(view.getContext(), GoodsdetailActivity.class);
                                        intent.putExtra("data",(Serializable)mData.get(position));
                                        view.getContext().startActivity(intent);
                                    }
                                });
                            }
                            else {
                                pd.cancel();
                                Toast.makeText(GoodsListActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(GoodsListActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
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
                                    String goodsimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("buydata").getString("bimage"), "utf-8");
                                    String userimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("userdata").getString("headphoto"), "utf-8");
                                    //liatview显示
                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("uid",data.getJSONObject(i).getJSONObject("userdata").getInt("uid"));
                                    item.put("account",data.getJSONObject(i).getJSONObject("userdata").getString("account"));
                                    item.put("nickname",data.getJSONObject(i).getJSONObject("userdata").getString("nickname"));
                                    item.put("reputation",data.getJSONObject(i).getJSONObject("userdata").getString("reputation"));
                                    item.put("tel",data.getJSONObject(i).getJSONObject("userdata").getString("tel"));
                                    item.put("hxid",data.getJSONObject(i).getJSONObject("userdata").getString("hxid"));
                                    item.put("bid",data.getJSONObject(i).getJSONObject("buydata").getInt("bid"));
                                    item.put("bname",data.getJSONObject(i).getJSONObject("buydata").getString("bname"));
                                    item.put("bdetail",data.getJSONObject(i).getJSONObject("buydata").getString("bdetail"));
                                    item.put("bsprice",data.getJSONObject(i).getJSONObject("buydata").getDouble("bsprice"));
                                    item.put("bbprice",data.getJSONObject(i).getJSONObject("buydata").getString("bbprice"));
                                    item.put("bhownew",data.getJSONObject(i).getJSONObject("buydata").getString("bhownew"));
                                    item.put("btype",data.getJSONObject(i).getJSONObject("buydata").getString("btype"));
                                    item.put("bgetway",data.getJSONObject(i).getJSONObject("buydata").getString("bgetway"));
                                    item.put("baddress",data.getJSONObject(i).getJSONObject("buydata").getString("baddress"));
                                    item.put("bscannum",data.getJSONObject(i).getJSONObject("buydata").getString("bscannum"));
                                    item.put("bstate",data.getJSONObject(i).getJSONObject("buydata").getString("bstate"));
                                    item.put("btime",data.getJSONObject(i).getJSONObject("buydata").getString("btime"));
                                    item.put("bimage",getResources().getString(R.string.burl)+"Image_Servlet?" + goodsimageurl);
                                    item.put("headphoto",getResources().getString(R.string.burl)+"Image_Servlet?" + userimageurl);
                                    mData.add(item);
                                }
                                BuylistAdapter adapter = new BuylistAdapter(GoodsListActivity.this,mData, lv_goodslist_list);
                                lv_goodslist_list.setAdapter(adapter);
                                lv_goodslist_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                            long id) {
                                        Intent intent = new Intent(view.getContext(), BuydetailActivity.class);
                                        intent.putExtra("data",(Serializable)mData.get(position));
                                        view.getContext().startActivity(intent);
                                    }
                                });
                            }else {
                                pd.cancel();
                                Toast.makeText(GoodsListActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(GoodsListActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //点击事件处理
    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_goodslist_back:
                    finish();
                    break;
                case R.id.ll_goodslist_moren:
                    clearChoice();
                    tv_goodslist_morentext.setTextColor(Red);
                    getGoodsData();
                    break;
                case R.id.ll_goodslist_renqi:
                    clearChoice();
                    tv_goodslist_renqitext.setTextColor(Red);
                    break;
                case R.id.ll_goodslist_time:
                    clearChoice();
                    tv_goodslist_timetext.setTextColor(Red);
                    break;
                case R.id.ll_goodslist_lprice:
                    clearChoice();
                    tv_goodslist_lpricetext.setTextColor(Red);
                    break;
                case R.id.ll_goodslist_hprice:
                    clearChoice();
                    tv_goodslist_hpricetext.setTextColor(Red);
                    break;
                default:
                    break;
            }
        }
    }

    //获取商品数据
    private void getGoodsData() {
        //从商品类型景来的安商品类型获取数据
        if("goodsType".equals(searchType.trim())){
            pd.show();
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                HttpUtils httpUtils = new HttpUtils();
                @Override
                public void run() {
                    mData.clear();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("requesttop","goodstype");
                    params.put("type", goodsType.trim());
                    params.put("state","1");
                    String strUrlpath = getResources().getString(R.string.burl) + "Goodslist_Servlet";
                    String Result = httpUtils.AsubmitPostData(strUrlpath, params, "utf-8");
                    System.out.println("获取的结果为：" + Result);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            });
        }else if("goodsName".equals(searchType.trim())){              //商品名进来的就按商品名发起请求
            pd.show();
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                HttpUtils httpUtils = new HttpUtils();
                @Override
                public void run() {
                    mData.clear();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("requesttop","goodsname");
                    params.put("name", goodsType.trim());
                    params.put("state","1");
                    String strUrlpath = getResources().getString(R.string.burl) + "Goodslist_Servlet";
                    String Result = httpUtils.AsubmitPostData(strUrlpath, params, "utf-8");
                    System.out.println("获取的结果为：" + Result);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            });
        }else if("goodsFree".equals(searchType.trim())){              //零元购商品
            pd.show();
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                HttpUtils httpUtils = new HttpUtils();
                @Override
                public void run() {
                    mData.clear();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("requesttop","freegoods");
                    params.put("state","1");
                    String strUrlpath = getResources().getString(R.string.burl) + "Goodslist_Servlet";
                    String Result = httpUtils.AsubmitPostData(strUrlpath, params, "utf-8");
                    System.out.println("获取的结果为：" + Result);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            });
        }else if("goodsNew".equals(searchType.trim())){                       //最新发布的商品
            pd.show();
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                HttpUtils httpUtils = new HttpUtils();
                @Override
                public void run() {
                    mData.clear();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("requesttop","goodsinfo");
                    params.put("state","1");
                    String strUrlpath = getResources().getString(R.string.burl) + "F1Fragment_Servlet";
                    String Result = httpUtils.AsubmitPostData(strUrlpath, params, "utf-8");
                    System.out.println("获取的结果为：" + Result);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            });
        }else if("goodsJi".equals(searchType.trim())){                      //急售商品
            pd.show();
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                HttpUtils httpUtils = new HttpUtils();
                @Override
                public void run() {
                    mData.clear();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("requesttop","jigoods");
                    params.put("state","1");
                    String strUrlpath = getResources().getString(R.string.burl) + "Goodslist_Servlet";
                    String Result = httpUtils.AsubmitPostData(strUrlpath, params, "utf-8");
                    System.out.println("获取的结果为：" + Result);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            });
        }else if("goodsBuy".equals(searchType.trim())){                         //求购商品
            pd.show();
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                HttpUtils httpUtils = new HttpUtils();
                @Override
                public void run() {
                    mData.clear();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("requesttop","goodsbuy");
                    params.put("state","1");
                    String strUrlpath = getResources().getString(R.string.burl) + "Goodslist_Servlet";
                    String Result = httpUtils.AsubmitPostData(strUrlpath, params, "utf-8");
                    System.out.println("获取的结果为：" + Result);
                    Message message = new Message();
                    message.what = 2;
                    message.obj = Result;
                    handler.sendMessage(message);
                }
            });
        }else;
    }

    //清空选择
    private void clearChoice(){
        tv_goodslist_morentext.setTextColor(Black);
        tv_goodslist_renqitext.setTextColor(Black);
        tv_goodslist_timetext.setTextColor(Black);
        tv_goodslist_lpricetext.setTextColor(Black);
        tv_goodslist_hpricetext.setTextColor(Black);
    }
    //初始化状态
    private void initState(){
        tv_goodslist_morentext.setTextColor(Red);
        tv_goodslist_renqitext.setTextColor(Black);
        tv_goodslist_timetext.setTextColor(Black);
        tv_goodslist_lpricetext.setTextColor(Black);
        tv_goodslist_hpricetext.setTextColor(Black);
    }
}
