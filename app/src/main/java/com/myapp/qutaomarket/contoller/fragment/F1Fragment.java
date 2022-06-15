package com.myapp.qutaomarket.contoller.fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.qutaomarket.QTApplication;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.activity.GoodsListActivity;
import com.myapp.qutaomarket.contoller.activity.TypeActivity;
import com.myapp.qutaomarket.contoller.adapter.GoodsAdapter;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.utils.HttpUtils;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页fragment
 */
public class F1Fragment extends Fragment {

    //定义变量
    //输入框
    private EditText et_f1_search;
    //搜索按钮
    private ImageView iv_f1_search;
    //下拉刷新的控件
    private SwipeRefreshLayout swipe_f1_refresh;
    //商品列表
    private ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
    //急售商品
    private ArrayList<Map<String,Object>> jiData= new ArrayList<Map<String,Object>>();

    private LinearLayout ll_f1_new,ll_f1_buy,ll_f1_phone,ll_f1_daily,ll_f1_elec,ll_f1_outsports,
            ll_f1_clothes,ll_f1_toy,ll_f1_game,ll_f1_moreclass;

//    private RelativeLayout rl_f1_urgentmore;

//    private TextView tv_f1_jistyle1,tv_f1_jicontent1,tv_f1_jistyle2,tv_f1_jicontent2,tv_f1_jistyle3,tv_f1_jicontent3;
//
//    //商品列表
//    private RecyclerView rv_f1_goodslist;

    private OnClickListener listener;

    //进度条
    private ProgressDialog pd;

    public F1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_f1, container, false);
        view.getResources().flushLayoutCache();
//        rv_f1_goodslist = (RecyclerView) view.findViewById(R.id.rv_f1_goodslist);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
//        rv_f1_goodslist.setLayoutManager(layoutManager);
//        rv_f1_goodslist.setHasFixedSize(true);
//        rv_f1_goodslist.setNestedScrollingEnabled(false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化控件
        initView();
        //获取三条急售商品的数据
//        getThreeEmergentInfo();
    }

    //初始化控件
    private void initView() {
        //初始化进度条
        pd = new ProgressDialog(getActivity());
        pd.setMessage("数据加载中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        //初始化控件
        et_f1_search = (EditText) getActivity().findViewById(R.id.et_f1_search);

        iv_f1_search = (ImageView) getActivity().findViewById(R.id.iv_f1_search);

        swipe_f1_refresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_f1_refresh);

        ll_f1_new = (LinearLayout) getActivity().findViewById(R.id.ll_f1_new);
//        ll_f1_free = (LinearLayout) getActivity().findViewById(R.id.ll_f1_free);
//        ll_f1_charity = (LinearLayout) getActivity().findViewById(R.id.ll_f1_charity);
        ll_f1_buy = (LinearLayout) getActivity().findViewById(R.id.ll_f1_buy);
        ll_f1_phone = (LinearLayout) getActivity().findViewById(R.id.ll_f1_phone);
        ll_f1_daily = (LinearLayout) getActivity().findViewById(R.id.ll_f1_daily);
        ll_f1_elec = (LinearLayout) getActivity().findViewById(R.id.ll_f1_elec);
        ll_f1_outsports = (LinearLayout) getActivity().findViewById(R.id.ll_f1_outsports);
        ll_f1_clothes = (LinearLayout) getActivity().findViewById(R.id.ll_f1_clothes);
        ll_f1_toy = (LinearLayout) getActivity().findViewById(R.id.ll_f1_toy);
        ll_f1_game = (LinearLayout) getActivity().findViewById(R.id.ll_f1_game);
        ll_f1_moreclass = (LinearLayout) getActivity().findViewById(R.id.ll_f1_moreclass);
//        ll_f1_urgent1 = (LinearLayout) getActivity().findViewById(R.id.ll_f1_urgent1);
//        ll_f1_urgent2 = (LinearLayout) getActivity().findViewById(R.id.ll_f1_urgent2);
//        ll_f1_urgent3 = (LinearLayout) getActivity().findViewById(R.id.ll_f1_urgent3);
//        rl_f1_urgentmore = (RelativeLayout) getActivity().findViewById(R.id.rl_f1_urgentmore);
//
//        tv_f1_jistyle1 = (TextView)getActivity().findViewById(R.id.tv_f1_jistyle1);
//        tv_f1_jicontent1 = (TextView)getActivity().findViewById(R.id.tv_f1_jicontent1);
//        tv_f1_jistyle2 = (TextView)getActivity().findViewById(R.id.tv_f1_jistyle2);
//        tv_f1_jicontent2 = (TextView)getActivity().findViewById(R.id.tv_f1_jicontent2);
//        tv_f1_jistyle3 = (TextView)getActivity().findViewById(R.id.tv_f1_jistyle3);
//        tv_f1_jicontent3 = (TextView)getActivity().findViewById(R.id.tv_f1_jicontent3);

        //初始化监听
        listener = new OnClickListener();
        iv_f1_search.setOnClickListener(listener);
        ll_f1_new.setOnClickListener(listener);
//        ll_f1_free.setOnClickListener(listener);
//        ll_f1_charity.setOnClickListener(listener);
        ll_f1_buy.setOnClickListener(listener);
        ll_f1_phone.setOnClickListener(listener);
        ll_f1_daily.setOnClickListener(listener);
        ll_f1_elec.setOnClickListener(listener);
        ll_f1_outsports.setOnClickListener(listener);
        ll_f1_clothes.setOnClickListener(listener);
        ll_f1_toy.setOnClickListener(listener);
        ll_f1_game.setOnClickListener(listener);
        ll_f1_moreclass.setOnClickListener(listener);
//        ll_f1_urgent1.setOnClickListener(listener);
//        ll_f1_urgent2.setOnClickListener(listener);
//        ll_f1_urgent3.setOnClickListener(listener);
//        rl_f1_urgentmore.setOnClickListener(listener);

        //下拉刷新
        swipe_f1_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //获取急售物品的数据
//                getThreeEmergentInfo();
            }
        });
    }

    //handler数据处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int goodsBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONArray data = new JSONArray();
                            JSONObject goodsResult = new JSONObject(msg.obj.toString().trim());
                            goodsBack = goodsResult.getInt("code");
                            if(goodsBack == 1){
                                pd.cancel();
                                data = goodsResult.getJSONArray("data");
                                for(int i=0;i<data.length();i++){
                                    String goodsimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("goodsdata").getString("gimage"), "utf-8");
                                    String userimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("userdata").getString("headphoto"), "utf-8");
                                    //recycleview显示
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
                                GoodsAdapter adapter = new GoodsAdapter(getActivity(),mData);
//                                rv_f1_goodslist.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                if(swipe_f1_refresh.isRefreshing()){
                                    swipe_f1_refresh.setRefreshing(false);
                                }
                            } else {
                                pd.cancel();
                                Toast.makeText(getActivity(),"获取数据失败",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(),"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
//                case 2:
//                    int jiBack;
//                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
//                    {
//                        try{
//                            JSONArray data = new JSONArray();
//                            JSONObject jiResult = new JSONObject(msg.obj.toString().trim());
//                            jiBack = jiResult.getInt("code");
//                            if(jiBack == 1){
//                                //pd.cancel();
//                                data = jiResult.getJSONArray("data");
//                                for(int i=0;i<data.length();i++){
//                                    String goodsimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("goodsdata").getString("gimage"), "utf-8");
//                                    String userimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("userdata").getString("headphoto"), "utf-8");
//                                    //recycleview显示
//                                    Map<String,Object> item = new HashMap<String,Object>();
//                                    item.put("uid",data.getJSONObject(i).getJSONObject("userdata").getInt("uid"));
//                                    item.put("account",data.getJSONObject(i).getJSONObject("userdata").getString("account"));
//                                    item.put("nickname",data.getJSONObject(i).getJSONObject("userdata").getString("nickname"));
//                                    item.put("reputation",data.getJSONObject(i).getJSONObject("userdata").getString("reputation"));
//                                    item.put("tel",data.getJSONObject(i).getJSONObject("userdata").getString("tel"));
//                                    item.put("hxid",data.getJSONObject(i).getJSONObject("userdata").getString("hxid"));
//                                    item.put("gid",data.getJSONObject(i).getJSONObject("goodsdata").getInt("gid"));
//                                    item.put("gname",data.getJSONObject(i).getJSONObject("goodsdata").getString("gname"));
//                                    item.put("ghownew",data.getJSONObject(i).getJSONObject("goodsdata").getString("ghownew"));
//                                    item.put("gprice",Double.toString(data.getJSONObject(i).getJSONObject("goodsdata").getDouble("gprice")));
//                                    item.put("ggetway",data.getJSONObject(i).getJSONObject("goodsdata").getString("ggetway"));
//                                    item.put("goprice",data.getJSONObject(i).getJSONObject("goodsdata").getString("goprice"));
//                                    item.put("gdetail",data.getJSONObject(i).getJSONObject("goodsdata").getString("gdetail"));
//                                    item.put("gaddress",data.getJSONObject(i).getJSONObject("goodsdata").getString("gaddress"));
//                                    item.put("gscannum",data.getJSONObject(i).getJSONObject("goodsdata").getString("gscannum"));
//                                    item.put("gstate",data.getJSONObject(i).getJSONObject("goodsdata").getString("gstate"));
//                                    item.put("gtype",data.getJSONObject(i).getJSONObject("goodsdata").getString("gtype"));
//                                    item.put("gimage",getResources().getString(R.string.burl)+"Image_Servlet?" + goodsimageurl);
//                                    item.put("headphoto",getResources().getString(R.string.burl)+"Image_Servlet?" + userimageurl);
//                                    jiData.add(item);
//                                }
//                                //将获取到的急售商品数据设置到急售窗
//                                tv_f1_jistyle1.setText("【" + jiData.get(0).get("gtype").toString() + "】");
//                                tv_f1_jicontent1.setText(jiData.get(0).get("gdetail").toString());
//                                tv_f1_jistyle2.setText("【" + jiData.get(1).get("gtype").toString() + "】");
//                                tv_f1_jicontent2.setText(jiData.get(1).get("gdetail").toString());
//                                tv_f1_jistyle3.setText("【" + jiData.get(2).get("gtype").toString() + "】");
//                                tv_f1_jicontent3.setText(jiData.get(2).get("gdetail").toString());
//                                //停止刷新
//                                if(swipe_f1_refresh.isRefreshing()){
//                                    swipe_f1_refresh.setRefreshing(false);
//                                }
//                                //获取猜你喜欢的数据
//                                getGoodsInfo();
//                            } else {
//                                pd.cancel();
//                                Toast.makeText(getActivity(),"获取数据失败",Toast.LENGTH_SHORT).show();
//                            }
//                        }catch (JSONException e){
//                            pd.cancel();
//                            e.printStackTrace();
//                        } catch (UnsupportedEncodingException e) {
//                            pd.cancel();
//                            e.printStackTrace();
//                        }
//                    }else {
//                        pd.cancel();
//                        Toast.makeText(getActivity(),"请检查网络",Toast.LENGTH_SHORT).show();
//                    }
//                    break;
                    default:
                        break;
            }
        }
    };

    //点击事件的集中处理
    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.iv_f1_search:
                    if("".equals(et_f1_search.getText().toString().trim())){
                        Toast.makeText(QTApplication.getmContext(), "请输入要搜索的商品哦！", Toast.LENGTH_LONG).show();
                    }else {
                        Intent searchIntent = new Intent(getActivity(), GoodsListActivity.class);
                        searchIntent.putExtra("searchType", "goodsName");
                        searchIntent.putExtra("goodsType", et_f1_search.getText().toString().trim());
                        startActivity(searchIntent);
                        et_f1_search.setText("");
                    }
                    break;
                case R.id.ll_f1_new:
                    Intent newIntent = new Intent(getActivity(), GoodsListActivity.class);
                    newIntent.putExtra("searchType", "goodsNew");
                    newIntent.putExtra("goodsType", "最新发布");
                    startActivity(newIntent);
                    break;
//                case R.id.ll_f1_free:
//                    Intent freeIntent = new Intent(getActivity(), GoodsListActivity.class);
//                    freeIntent.putExtra("searchType", "goodsFree");
//                    freeIntent.putExtra("goodsType", "免费送");
//                    startActivity(freeIntent);
//                    break;
//                case R.id.ll_f1_charity:
//                    Intent charityIntent = new Intent(getActivity(), CharityListActivity.class);
//                    startActivity(charityIntent);
//                    break;
                case R.id.ll_f1_buy:
                    Intent buyIntent = new Intent(getActivity(), GoodsListActivity.class);
                    buyIntent.putExtra("searchType", "goodsBuy");
                    buyIntent.putExtra("goodsType", "我要买");
                    startActivity(buyIntent);
                    break;
                case R.id.ll_f1_phone:
                    Intent phoneIntent = new Intent(getActivity(), GoodsListActivity.class);
                    phoneIntent.putExtra("searchType", "goodsType");
                    phoneIntent.putExtra("goodsType", "手机数码");
                    startActivity(phoneIntent);
                    break;
                case R.id.ll_f1_daily:
                    Intent dailyIntent = new Intent(getActivity(), GoodsListActivity.class);
                    dailyIntent.putExtra("searchType", "goodsType");
                    dailyIntent.putExtra("goodsType", "生活百货");
                    startActivity(dailyIntent);
                    break;
                case R.id.ll_f1_elec:
                    Intent elecIntent = new Intent(getActivity(), GoodsListActivity.class);
                    elecIntent.putExtra("searchType", "goodsType");
                    elecIntent.putExtra("goodsType", "家用电器");
                    startActivity(elecIntent);
                    break;
                case R.id.ll_f1_outsports:
                    Intent outsportsIntent = new Intent(getActivity(), GoodsListActivity.class);
                    outsportsIntent.putExtra("searchType", "goodsType");
                    outsportsIntent.putExtra("goodsType", "户外运动");
                    startActivity(outsportsIntent);
                    break;
                case R.id.ll_f1_clothes:
                    Intent clothesIntent = new Intent(getActivity(), GoodsListActivity.class);
                    clothesIntent.putExtra("searchType", "goodsType");
                    clothesIntent.putExtra("goodsType", "服饰配件");
                    startActivity(clothesIntent);
                    break;
                case R.id.ll_f1_toy:
                    Intent toyIntent = new Intent(getActivity(), GoodsListActivity.class);
                    toyIntent.putExtra("searchType", "goodsType");
                    toyIntent.putExtra("goodsType", "儿童玩具");
                    startActivity(toyIntent);
                    break;
                case R.id.ll_f1_game:
                    Intent gameIntent = new Intent(getActivity(), GoodsListActivity.class);
                    gameIntent.putExtra("searchType", "goodsType");
                    gameIntent.putExtra("goodsType", "游戏装备");
                    startActivity(gameIntent);
                    break;
                case R.id.ll_f1_moreclass:
                    Intent moreclassIntent = new Intent(getActivity(), TypeActivity.class);
                    startActivity(moreclassIntent);
                    break;
//                case R.id.rl_f1_urgentmore:
//                    Intent urgentIntent = new Intent(getActivity(), GoodsListActivity.class);
//                    urgentIntent.putExtra("searchType", "goodsJi");
//                    urgentIntent.putExtra("goodsType", "急售商品");
//                    startActivity(urgentIntent);
//                    break;
//                case R.id.ll_f1_urgent1:
//                    Intent urgent1Intent = new Intent(getActivity(), GoodsdetailActivity.class);
//                    urgent1Intent.putExtra("data",(Serializable)jiData.get(0));
//                    urgent1Intent.putExtra("state","1");
//                    getActivity().startActivity(urgent1Intent);
//                    break;
//                case R.id.ll_f1_urgent2:
//                    Intent urgent2Intent = new Intent(getActivity(), GoodsdetailActivity.class);
//                    urgent2Intent.putExtra("data",(Serializable)jiData.get(1));
//                    urgent2Intent.putExtra("state","1");
//                    getActivity().startActivity(urgent2Intent);
//                    break;
//                case R.id.ll_f1_urgent3:
//                    Intent urgent3Intent = new Intent(getActivity(), GoodsdetailActivity.class);
//                    urgent3Intent.putExtra("data",(Serializable)jiData.get(2));
//                    urgent3Intent.putExtra("state","1");
//                    getActivity().startActivity(urgent3Intent);
//                    break;
            }
        }
    }

    //获取猜你喜欢的商品信息
    private void getGoodsInfo(){
        //pd.show();
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
    }

    //获取前三条急售商品的信息
//    private void getThreeEmergentInfo(){
//        pd.show();
//        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
//            HttpUtils httpUtils = new HttpUtils();
//            @Override
//            public void run() {
//                jiData.clear();
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("requesttop","goodsji");
//                params.put("state","1");
//                String strUrlpath = getResources().getString(R.string.burl) + "F1Fragment_Servlet";
//                String Result = httpUtils.AsubmitPostData(strUrlpath, params, "utf-8");
//                System.out.println("获取的结果为：" + Result);
//                Message message = new Message();
//                message.what = 2;
//                message.obj = Result;
//                handler.sendMessage(message);
//            }
//        });
//    }
}
