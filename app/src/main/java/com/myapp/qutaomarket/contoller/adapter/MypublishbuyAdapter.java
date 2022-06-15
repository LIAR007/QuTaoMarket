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
import com.myapp.qutaomarket.contoller.activity.PublishbuyActivity;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MypublishbuyAdapter extends BaseAdapter {
    // 创建ImageLoader对象
    private Context context;
    private List<Map<String, Object>> list;
    private int myPosition;
    //进度条
    private ProgressDialog pd;
    public MypublishbuyAdapter(Context context, List<Map<String, Object>> list, ListView listView){
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
        final MypublishbuyAdapter.ViewHolder holder;
        //初始化进度条
        pd = new ProgressDialog(context);
        pd.setMessage("请稍等...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        if(convertView ==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.mypubbuylist_adapter, parent, false);
            holder = new MypublishbuyAdapter.ViewHolder();

            holder.iv_mypubbuylist_bimage = (ImageView) convertView.findViewById(R.id.iv_mypubbuylist_bimage);
            holder.tv_mypubbuylist_bname = (TextView) convertView.findViewById(R.id.tv_mypubbuylist_bname);
            holder.tv_mypubbuylist_bdetail = (TextView) convertView.findViewById(R.id.tv_mypubbuylist_bdetail);
            holder.tv_mypubbuylist_edit = (TextView) convertView.findViewById(R.id.tv_mypubbuylist_edit);
            holder.tv_mypubbuylist_delete = (TextView) convertView.findViewById(R.id.tv_mypubbuylist_delete);
            holder.tv_mypubbuylist_bsprice = (TextView) convertView.findViewById(R.id.tv_mypubbuylist_bsprice);
            holder.tv_mypubbuylist_bbprice = (TextView) convertView.findViewById(R.id.tv_mypubbuylist_bbprice);
//            holder.tv_mypubbuylist_bscannum = (TextView) convertView.findViewById(R.id.tv_mypubbuylist_bscannum);

            convertView.setTag(holder);
        }else{
            holder = (MypublishbuyAdapter.ViewHolder) convertView.getTag();
        }
        Glide.with(convertView.getContext())
                .load(list.get(position).get("bimage").toString())
                .placeholder(R.drawable.ic_moren_goods)
                .error(R.drawable.ic_moren_goods)
                .into(holder.iv_mypubbuylist_bimage);
        holder.tv_mypubbuylist_bname.setText(list.get(position).get("bname").toString());
        holder.tv_mypubbuylist_bdetail.setText(list.get(position).get("bdetail").toString());
        holder.tv_mypubbuylist_bsprice.setText(list.get(position).get("bsprice").toString());
        holder.tv_mypubbuylist_bbprice.setText(list.get(position).get("bbprice").toString());
//        holder.tv_mypubbuylist_bscannum.setText(list.get(position).get("bscannum").toString());
        holder.tv_mypubbuylist_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PublishbuyActivity.class);
                //传递参数
                intent.putExtra("title", "编辑求购");
                intent.putExtra("type", "editbuy");
                intent.putExtra("data", (Serializable)list.get(position));
                context.startActivity(intent);
            }
        });
        holder.tv_mypubbuylist_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("删除求购")
                        .setMessage("确定删除该求购信息吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pd.show();
                                myPosition = position;
                                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("requesttop", "deletebuy");
                                        params.put("bid", list.get(position).get("bid").toString());
                                        String strUrlpath = context.getResources().getString(R.string.burl) + "Mypublish_Servlet";
                                        String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                        System.out.println("结果为：" + Result);
                                        Message message = new Message();
                                        message.what = 1;
                                        message.obj = Result;
                                        handler.sendMessage(message);
                                    }
                                });
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                alertDialog.show();
            }
        });
        return convertView;
    }
    static class ViewHolder{
        TextView tv_mypubbuylist_bname,tv_mypubbuylist_bdetail,tv_mypubbuylist_bsprice,tv_mypubbuylist_bbprice,tv_mypubbuylist_bscannum,tv_mypubbuylist_edit,tv_mypubbuylist_delete;
        ImageView iv_mypubbuylist_bimage;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int deleteBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject deleteResult = new JSONObject(msg.obj.toString().trim());
                            deleteBack = deleteResult.getInt("code");
                            if(deleteBack == 1){
                                pd.cancel();
                                Toast.makeText(context,"已删除",Toast.LENGTH_SHORT).show();
                                list.remove(list.get(myPosition));
                                notifyDataSetChanged();
                            } else {
                                pd.cancel();
                                Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(context,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
}
