package com.myapp.qutaomarket.contoller.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.activity.MainActivity;
import com.myapp.qutaomarket.model.db.City;
import com.myapp.qutaomarket.model.db.County;
import com.myapp.qutaomarket.model.db.Province;
import com.myapp.qutaomarket.utils.HttpUtils;
import com.myapp.qutaomarket.utils.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    //所选中城市的水平
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    //定义变量
    private ImageView iv_choosearea_back;
    private TextView tv_choosearea_text,tv_choosearea_location;
    private ProgressDialog progressDialog;
    private ListView lv_choosearea_list;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private RelativeLayout rl_pubaddress_curlocation;
    //省列表
    private List<Province> provienceList = new ArrayList<>();
    //市列表
    private List<City> cityList = new ArrayList<>();
    //县列表
    private List<County> countyList = new ArrayList<>();
    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        tv_choosearea_text = (TextView) view.findViewById(R.id.tv_choosearea_text);
        tv_choosearea_location = (TextView) view.findViewById(R.id.tv_choosearea_location);
        iv_choosearea_back = (ImageView) view.findViewById(R.id.iv_choosearea_back);
        lv_choosearea_list = (ListView) view.findViewById(R.id.lv_choosearea_list);
        rl_pubaddress_curlocation = (RelativeLayout) view.findViewById(R.id.rl_pubaddress_curlocation);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        lv_choosearea_list.setAdapter(adapter);
        currentLevel = LEVEL_PROVINCE;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //设置当前定位的位置
        tv_choosearea_location.setText(MainActivity.myAddress);

        //区域列表点击事件
        lv_choosearea_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provienceList.get(i);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    String addressData = selectedCity.getCityName() + " " + countyList.get(i).getCountyName();
                    Intent dataIntent = new Intent();
                    dataIntent.putExtra("address", addressData);
                    getActivity().setResult(getActivity().RESULT_OK, dataIntent);
                    getActivity().finish();
                }
            }
        });
        //返回按钮点击事件
        iv_choosearea_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_PROVINCE){
                    getActivity().finish();
                } else if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                } else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        rl_pubaddress_curlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationIntent = new Intent();
                locationIntent.putExtra("address", tv_choosearea_location.getText().toString());
                getActivity().setResult(Activity.RESULT_OK, locationIntent);
                getActivity().finish();
            }
        });
        queryProvinces();
    }

    //查询全国所有的省，优先从数据库查，如果没查到再去服务器查
    private void queryProvinces() {
        tv_choosearea_text.setText("中国");
        provienceList = LitePal.findAll(Province.class);
        if(provienceList.size() > 0){
            dataList.clear();
            for(Province province:provienceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lv_choosearea_list.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            //借鉴的郭霖大神的请求地址的网址，有空自己写一个
            String address = "http://guolin.tech/api/china";
            queryFromSever(address, "province");
        }
    }
    //查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器查询
    private void queryCities() {
        tv_choosearea_text.setText(selectedProvince.getProvinceName());
        cityList = LitePal.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size() >0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv_choosearea_list.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode = selectedProvince.getProviceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromSever(address, "city");
        }
    }
    //查询选中市内所有的县，优先从数据库获取，没有再去服务器上查询
    private void queryCounties() {
        tv_choosearea_text.setText(selectedCity.getCityName());
        countyList = LitePal.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size() > 0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lv_choosearea_list.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProviceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromSever(address, "county");
        }
    }
    //根据传入的地址和类型从服务器上查询省市县数据
    private void queryFromSever(String address, final String type) {
        showProgressDialog();
        HttpUtils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
                System.out.println("打印一下接收："+responseText);
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //通过运行runOnUiThread()的方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    //关闭进度对话框
    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
    //显示进度对话框
    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
}
