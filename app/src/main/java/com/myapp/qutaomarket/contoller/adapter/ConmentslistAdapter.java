package com.myapp.qutaomarket.contoller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myapp.qutaomarket.R;

import java.util.List;
import java.util.Map;


public class ConmentslistAdapter extends BaseAdapter {
    // 创建ImageLoader对象
    private Context context;
    private List<Map<String, Object>> list;
    public ConmentslistAdapter(Context context, List<Map<String, Object>> list){
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
        // TODO Auto-generated method stub
        final ConmentslistAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView  = LayoutInflater.from(context).inflate(R.layout.layout_conmentslist,parent,false);
            holder = new ConmentslistAdapter.ViewHolder();

            holder.iv_conlist_headphoto = (ImageView) convertView.findViewById(R.id.iv_conlist_headphoto);
            holder.tv_conlist_nickname = (TextView) convertView.findViewById(R.id.tv_conlist_nickname);
            holder.tv_conlist_conment = (TextView) convertView.findViewById(R.id.tv_conlist_conment);
            holder.tv_conlist_time = (TextView) convertView.findViewById(R.id.tv_conlist_time);

            convertView.setTag(holder);
        }else {
            holder = (ConmentslistAdapter.ViewHolder) convertView.getTag();
        }
        //加载用户头像
        if((context.getResources().getString(R.string.burl)+"Image_Servlet?null").equals(list.get(position).get("headphoto").toString())){
            Glide.with(convertView.getContext())
                    .load(R.drawable.moren_headphoto)
                    .circleCrop()
                    .placeholder(R.drawable.moren_headphoto)
                    .error(R.drawable.moren_headphoto)
                    .into(holder.iv_conlist_headphoto);
        }else {
            Glide.with(convertView.getContext())
                    .load(list.get(position).get("headphoto").toString())
                    .circleCrop()
                    .placeholder(R.drawable.moren_headphoto)
                    .error(R.drawable.moren_headphoto)
                    .into(holder.iv_conlist_headphoto);
        }
        holder.tv_conlist_nickname.setText(list.get(position).get("nickname").toString());
        holder.tv_conlist_conment.setText(list.get(position).get("content").toString());
        holder.tv_conlist_time.setText(list.get(position).get("time").toString());
        return convertView;
    }
    class ViewHolder{
        TextView tv_conlist_nickname,tv_conlist_conment,tv_conlist_time;
        ImageView iv_conlist_headphoto;
    }

}
