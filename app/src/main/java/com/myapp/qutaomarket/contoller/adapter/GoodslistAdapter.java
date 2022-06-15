package com.myapp.qutaomarket.contoller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myapp.qutaomarket.R;

import java.util.List;
import java.util.Map;

public class GoodslistAdapter extends BaseAdapter implements AbsListView.OnScrollListener{
    // 创建ImageLoader对象
    private Context context;
    private List<Map<String, Object>> list;
    public GoodslistAdapter(Context context, List<Map<String, Object>> list, ListView listView){
        this.context = context;
        this.list = list;
        listView.setOnScrollListener(this);
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
            final ViewHolder holder;
        if(convertView ==null)

            {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_searchlist, parent, false);
                holder = new ViewHolder();

                holder.iv_gladapter_gimage = (ImageView) convertView.findViewById(R.id.iv_gladapter_gimage);
                holder.tv_gladapter_gname = (TextView) convertView.findViewById(R.id.tv_gladapter_gname);
                holder.tv_gladapter_gdetail = (TextView) convertView.findViewById(R.id.tv_gladapter_gdetail);
                holder.tv_gladapter_gtype = (TextView) convertView.findViewById(R.id.tv_gladapter_gtype);
                holder.tv_gladapter_ghownew = (TextView) convertView.findViewById(R.id.tv_gladapter_ghownew);
                holder.tv_gladapter_ggetway = (TextView) convertView.findViewById(R.id.tv_gladapter_ggetway);
                holder.tv_gladapter_gprice = (TextView) convertView.findViewById(R.id.tv_gladapter_gprice);
//                holder.tv_gladapter_gscannum = (TextView) convertView.findViewById(R.id.tv_gladapter_gscannum);
                holder.tv_gladapter_symbol = (TextView) convertView.findViewById(R.id.tv_gladapter_symbol);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
        Glide.with(convertView.getContext())
             .load(list.get(position).get("gimage").toString())
             .placeholder(R.drawable.ic_moren_goods)
             .error(R.drawable.ic_moren_goods)
             .into(holder.iv_gladapter_gimage);
        holder.tv_gladapter_gname.setText(list.get(position).get("gname").toString());
        holder.tv_gladapter_gdetail.setText(list.get(position).get("gdetail").toString());
        holder.tv_gladapter_gtype.setText(list.get(position).get("gtype").toString());
        holder.tv_gladapter_ghownew.setText(list.get(position).get("ghownew").toString());
        holder.tv_gladapter_ggetway.setText(list.get(position).get("ggetway").toString());
        if(Double.valueOf(list.get(position).get("gprice").toString()) == 0){
            holder.tv_gladapter_symbol.setVisibility(View.GONE);
//            holder.tv_gladapter_gprice.setText("免费送");
        }else {
            holder.tv_gladapter_gprice.setText(list.get(position).get("gprice").toString());
        }
//        holder.tv_gladapter_gscannum.setText(list.get(position).get("gscannum").toString());
        return convertView;
    }
    static class ViewHolder{
        TextView tv_gladapter_symbol,tv_gladapter_gname,tv_gladapter_gdetail,tv_gladapter_gtype,tv_gladapter_ghownew,tv_gladapter_ggetway,tv_gladapter_gprice,tv_gladapter_gscannum;
        ImageView iv_gladapter_gimage;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState){
        switch (scrollState){
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                Glide.with(view.getContext()).pauseRequests();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                Glide.with(view.getContext()).resumeRequests();
                notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}