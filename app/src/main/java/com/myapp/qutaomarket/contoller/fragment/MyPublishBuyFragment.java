package com.myapp.qutaomarket.contoller.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.activity.BuydetailActivity;
import com.myapp.qutaomarket.contoller.activity.GoodsdetailActivity;
import com.myapp.qutaomarket.contoller.adapter.MypublishbuyAdapter;
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

public class MyPublishBuyFragment extends Fragment {
    private ListView lv_mypubbuy_list;

    //进度条
    private ProgressDialog pd;

    //发布列表
    private ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();

    //构造方法
    public MyPublishBuyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buy_mypublish, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lv_mypubbuy_list = (ListView)getActivity().findViewById(R.id.lv_mypubbuy_list);

        //初始化进度条
        pd = new ProgressDialog(getActivity());
        pd.setMessage("数据加载中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        //获取数据
        getBuyData();
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
                                MypublishbuyAdapter adapter = new MypublishbuyAdapter(getActivity(),mData, lv_mypubbuy_list);
                                lv_mypubbuy_list.setAdapter(adapter);
                                lv_mypubbuy_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                Toast.makeText(getActivity(),"还没有发布求购！",Toast.LENGTH_SHORT).show();
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
            }
        }
    };

    //获取发布商品的数据
    private void getBuyData(){
        mData.clear();
        User user = UserUtils.getCurrentUser();
        pd.show();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop","getmypubbuy");
                params.put("account",user.getAccount());
                String strUrlpath = getResources().getString(R.string.burl) + "Mypublish_Servlet";
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
