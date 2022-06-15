package com.myapp.qutaomarket.contoller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.contoller.activity.GoodsdetailActivity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private List<Map<String, Object>> mlist;
    // 创建ImageLoader对象
    private Context context;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View Goodsview;
        ImageView tv_gadapter_image, iv_gadapter_headphoto;
        TextView tv_gadapter_name, tv_gadapter_price, tv_gadapter_hownew, tv_gadapter_getway, tv_gadapter_nickname, tv_gadapter_repu,tv_gadapter_symbol1;
        public ViewHolder(View itemView) {
            super(itemView);
            Goodsview = itemView;
            tv_gadapter_image = (ImageView)itemView.findViewById(R.id.tv_gadapter_image);
            iv_gadapter_headphoto = (ImageView)itemView.findViewById(R.id.iv_gadapter_headphoto);
            tv_gadapter_price = (TextView)itemView.findViewById(R.id.tv_gadapter_price);
            tv_gadapter_hownew = (TextView)itemView.findViewById(R.id.tv_gadapter_hownew);
            tv_gadapter_getway = (TextView)itemView.findViewById(R.id.tv_gadapter_getway);
            tv_gadapter_nickname = (TextView)itemView.findViewById(R.id.tv_gadapter_nickname);
            tv_gadapter_repu = (TextView)itemView.findViewById(R.id.tv_gadapter_repu);
            tv_gadapter_name = (TextView)itemView.findViewById(R.id.tv_gadapter_name);
            tv_gadapter_symbol1 = (TextView)itemView.findViewById(R.id.tv_gadapter_symbol1);
        }
    }

    public GoodsAdapter(Context context, List<Map<String, Object>> list){
        this.context = context;
        mlist = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.goodsadapter_item,parent,false);
        final ViewHolder holder = new ViewHolder(itemView);
        holder.Goodsview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(view.getContext(), GoodsdetailActivity.class);
                intent.putExtra("state","1");
                intent.putExtra("data",(Serializable)mlist.get(position));
                view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int value;
        final View view  = holder.itemView;
        Glide.with(view.getContext())
                .load(mlist.get(position).get("gimage").toString())
                .placeholder(R.drawable.ic_moren_goods)
                .error(R.drawable.ic_moren_goods)
                .into(holder.tv_gadapter_image);
        if((context.getResources().getString(R.string.burl)+"Image_Servlet?null").equals(mlist.get(position).get("headphoto").toString())){
            Glide.with(view.getContext())
                    .load(R.drawable.moren_headphoto)
                    .circleCrop()
                    .placeholder(R.drawable.moren_headphoto)
                    .error(R.drawable.moren_headphoto)
                    .into(holder.iv_gadapter_headphoto);
        }else {
            Glide.with(view.getContext())
                    .load(mlist.get(position).get("headphoto").toString())
                    .circleCrop()
                    .placeholder(R.drawable.moren_headphoto)
                    .error(R.drawable.moren_headphoto)
                    .into(holder.iv_gadapter_headphoto);
        }
        holder.tv_gadapter_name.setText(mlist.get(position).get("gname").toString());
        holder.tv_gadapter_hownew.setText(mlist.get(position).get("ghownew").toString());
        holder.tv_gadapter_getway.setText(mlist.get(position).get("ggetway").toString());
        if(Double.valueOf(mlist.get(position).get("gprice").toString()) == 0){
            holder.tv_gadapter_price.setText("免费送");
        }else holder.tv_gadapter_price.setText(mlist.get(position).get("gprice").toString());
        holder.tv_gadapter_nickname.setText(mlist.get(position).get("nickname").toString());
        value = Integer.valueOf(mlist.get(position).get("reputation").toString());
        if(value<500){
            holder.tv_gadapter_repu.setText("信用一般");
        }else if((value>=500)&&(value<700)){
            holder.tv_gadapter_repu.setText("信用良好");
        }else if((value>=700)&&(value<900)){
            holder.tv_gadapter_repu.setText("信用优秀");
        }else if((value>=900)&&(value<=1000)){
            holder.tv_gadapter_repu.setText("信用极好");
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
