package com.myapp.qutaomarket.contoller.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.activity.MainActivity;
import com.myapp.qutaomarket.contoller.adapter.GoodsAdapter;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 附近的fragment
 */
public class F2Fragment extends LazyFragment {

    private TextView tv_f2_title;

    private RecyclerView rv_f2_goodslist;

    //设置进度条
    private ProgressDialog pd;

    //商品列表
    private ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f2, container, false);
        rv_f2_goodslist = (RecyclerView)view.findViewById(R.id.rv_f2_goodslist);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        rv_f2_goodslist.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化视图
        initView();
    }

    //初始化视图
    private void initView(){
        //初始化进度条
        pd = new ProgressDialog(getActivity());
        pd.setMessage("加载数据...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        tv_f2_title = (TextView)getActivity().findViewById(R.id.tv_f2_title);
    }

    //数据的懒加载
    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && !isLoad) {
            isLoad = true;//在此将isLoad 设置为 true ,防止数据重复加载
            //把标题设置成位置
            tv_f2_title.setText(MainActivity.myLocation);
            //获取附近商品数据数据
            getGoodsInfo();
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
                        try{
                            JSONArray data = new JSONArray();
                            JSONObject goodsResult = new JSONObject(msg.obj.toString().trim());
                            goodsBack = goodsResult.getInt("code");
                            if(goodsBack == 1){
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
                                adapter.notifyDataSetChanged();
                                rv_f2_goodslist.setAdapter(adapter);
                                //取消进度条
                                pd.cancel();
                            }
                            else {
                                //取消进度条
                                pd.cancel();
                                Toast.makeText(getActivity(),"获取数据失败",Toast.LENGTH_SHORT).show();
                            }
                            //设置位置
//                            tv_f2_title.setText(MainActivity.myLocation);
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        //设置位置
                        tv_f2_title.setText(MainActivity.myLocation);
                        //取消进度条
                        pd.cancel();
                        Toast.makeText(getActivity(),"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //获取商品数据
    private void getGoodsInfo(){
        pd.show();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            HttpUtils httpUtils = new HttpUtils();
            @Override
            public void run() {
                mData.clear();
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop","localgoods");
                params.put("state","1");
                params.put("location", MainActivity.myLocation);
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
}
