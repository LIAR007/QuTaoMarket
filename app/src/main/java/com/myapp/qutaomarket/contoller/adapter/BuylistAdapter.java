package com.myapp.qutaomarket.contoller.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.activity.PublishgoodsActivity;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuylistAdapter extends BaseAdapter {
    // 创建ImageLoader对象
    private Context context;
    private List<Map<String, Object>> list;
    private int myPosition;
    //进度条
    private ProgressDialog pd;
    public BuylistAdapter(Context context, List<Map<String, Object>> list, ListView listView){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BuylistAdapter.ViewHolder holder;
        //初始化进度条
        pd = new ProgressDialog(context);
        pd.setMessage("请稍等...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        if(convertView ==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_buylist, parent, false);
            holder = new BuylistAdapter.ViewHolder();

            holder.iv_buylist_bimage = (ImageView) convertView.findViewById(R.id.iv_buylist_bimage);
            holder.tv_buylist_bname = (TextView) convertView.findViewById(R.id.tv_buylist_bname);
            holder.tv_buylist_bdetail = (TextView) convertView.findViewById(R.id.tv_buylist_bdetail);
            holder.tv_buylist_bsprice = (TextView) convertView.findViewById(R.id.tv_buylist_bsprice);
            holder.tv_buylist_bbprice = (TextView) convertView.findViewById(R.id.tv_buylist_bbprice);
//            holder.tv_buylist_bscannum = (TextView) convertView.findViewById(R.id.tv_buylist_bscannum);
            convertView.setTag(holder);
        }else{
            holder = (BuylistAdapter.ViewHolder) convertView.getTag();
        }
        Glide.with(convertView.getContext())
                .load(list.get(position).get("bimage").toString())
                .placeholder(R.drawable.ic_moren_goods)
                .error(R.drawable.ic_moren_goods)
                .into(holder.iv_buylist_bimage);
        holder.tv_buylist_bname.setText(list.get(position).get("bname").toString());
        holder.tv_buylist_bdetail.setText(list.get(position).get("bdetail").toString());
        holder.tv_buylist_bsprice.setText(list.get(position).get("bsprice").toString());
        holder.tv_buylist_bbprice.setText(list.get(position).get("bbprice").toString());
//        holder.tv_buylist_bscannum.setText(list.get(position).get("bscannum").toString());
        return convertView;
    }
    static class ViewHolder{
//        TextView tv_buylist_bname,tv_buylist_bdetail,tv_buylist_bsprice,tv_buylist_bbprice,tv_buylist_bscannum;
        TextView tv_buylist_bname,tv_buylist_bdetail,tv_buylist_bsprice,tv_buylist_bbprice;
        ImageView iv_buylist_bimage;
    }
}
