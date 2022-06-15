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

public class MypublistAdapter extends BaseAdapter {
    // 创建ImageLoader对象
    private Context context;
    private List<Map<String, Object>> list;
    private int myPosition;
    //进度条
    private ProgressDialog pd;
    public MypublistAdapter(Context context, List<Map<String, Object>> list, ListView listView){
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
        final MypublistAdapter.ViewHolder holder;
        //初始化进度条
        pd = new ProgressDialog(context);
        pd.setMessage("请稍等...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        if(convertView ==null)

        {
            convertView = LayoutInflater.from(context).inflate(R.layout.mypublist_adapter, parent, false);
            holder = new MypublistAdapter.ViewHolder();

            holder.iv_mypublist_gimage = (ImageView) convertView.findViewById(R.id.iv_mypublist_gimage);
            holder.tv_mypublist_gname = (TextView) convertView.findViewById(R.id.tv_mypublist_gname);
            holder.tv_mypublist_gdetail = (TextView) convertView.findViewById(R.id.tv_mypublist_gdetail);
            holder.tv_mypublist_edit = (TextView) convertView.findViewById(R.id.tv_mypublist_edit);
            holder.tv_mypublist_delete = (TextView) convertView.findViewById(R.id.tv_mypublist_delete);
            holder.tv_mypublist_gprice = (TextView) convertView.findViewById(R.id.tv_mypublist_gprice);
//            holder.tv_mypublist_gscannum = (TextView) convertView.findViewById(R.id.tv_mypublist_gscannum);
            holder.tv_mypublist_symbol = (TextView) convertView.findViewById(R.id.tv_mypublist_symbol);

            convertView.setTag(holder);
        }else{
            holder = (MypublistAdapter.ViewHolder) convertView.getTag();
        }
        Glide.with(convertView.getContext())
                .load(list.get(position).get("gimage").toString())
                .placeholder(R.drawable.ic_moren_goods)
                .error(R.drawable.ic_moren_goods)
                .into(holder.iv_mypublist_gimage);
        holder.tv_mypublist_gname.setText(list.get(position).get("gname").toString());
        holder.tv_mypublist_gdetail.setText(list.get(position).get("gdetail").toString());
        if(Double.valueOf(list.get(position).get("gprice").toString()) == 0){
            holder.tv_mypublist_symbol.setVisibility(View.GONE);
            holder.tv_mypublist_gprice.setText("免费送");
        }else {
            holder.tv_mypublist_gprice.setText(list.get(position).get("gprice").toString());
        }
//        holder.tv_mypublist_gscannum.setText(list.get(position).get("gscannum").toString());
        holder.tv_mypublist_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PublishgoodsActivity.class);
                //传递参数
                intent.putExtra("title", "编辑商品");
                if(Double.valueOf(list.get(position).get("gprice").toString()) == 0){
                    intent.putExtra("type", "editfree");
                }else {
                    intent.putExtra("type", "editgoods");
                }
                intent.putExtra("data", (Serializable)list.get(position));
                context.startActivity(intent);
            }
        });
        holder.tv_mypublist_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("删除商品")
                        .setMessage("确定删除该商品吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pd.show();
                                myPosition = position;
                                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("requesttop", "deletegoods");
                                        params.put("gid", list.get(position).get("gid").toString());
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
        TextView tv_mypublist_gname,tv_mypublist_gdetail,tv_mypublist_symbol,tv_mypublist_gprice,tv_mypublist_gscannum,tv_mypublist_edit,tv_mypublist_delete;
        ImageView iv_mypublist_gimage;
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
                                list.remove(myPosition);
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
