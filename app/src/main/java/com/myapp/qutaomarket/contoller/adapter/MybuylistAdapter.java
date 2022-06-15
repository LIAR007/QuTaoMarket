package com.myapp.qutaomarket.contoller.adapter;

import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.EaseConstant;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybuylistAdapter extends BaseAdapter {
    // 创建ImageLoader对象
    private Context context;
    private List<Map<String, Object>> list;
    private int myPosition;
    public MybuylistAdapter(Context context, List<Map<String, Object>> list, ListView listView){
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
        final MybuylistAdapter.ViewHolder holder;
        myPosition = position;
        if(convertView ==null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_mybuy_list, parent, false);
            holder = new MybuylistAdapter.ViewHolder();

            holder.iv_mybuylist_gimage = (ImageView) convertView.findViewById(R.id.iv_mybuylist_gimage);
            holder.tv_mybuylist_gdetail = (TextView) convertView.findViewById(R.id.tv_mybuylist_gdetail);
            holder.tv_mybuylist_aprice = (TextView) convertView.findViewById(R.id.tv_mybuylist_aprice);
            holder.tv_mybuylist_astate = (TextView) convertView.findViewById(R.id.tv_mybuylist_astate);
            holder.tv_mybuylist_delete = (TextView) convertView.findViewById(R.id.tv_mybuylist_delete);
//            holder.tv_mybuyadapter_chat = (TextView) convertView.findViewById(R.id.tv_mybuyadapter_chat);
//            holder.ll_mybuylist_chat = (LinearLayout)convertView.findViewById(R.id.ll_mybuylist_chat);

            convertView.setTag(holder);
        }else{
            holder = (MybuylistAdapter.ViewHolder) convertView.getTag();
        }
        Glide.with(convertView.getContext())
                .load(list.get(position).get("gimage").toString())
                .placeholder(R.drawable.ic_moren_goods)
                .error(R.drawable.ic_moren_goods)
                .into(holder.iv_mybuylist_gimage);
        holder.tv_mybuylist_gdetail.setText(list.get(position).get("gdetail").toString());
        if(Integer.valueOf(list.get(position).get("astate").toString()) == 1){
            holder.tv_mybuylist_astate.setText("等待卖家发货");
        }else if(Integer.valueOf(list.get(position).get("astate").toString()) == 2){
            holder.tv_mybuylist_astate.setText("等待卖家确认收货");
        }else if(Integer.valueOf(list.get(position).get("astate").toString()) == 3){
            holder.tv_mybuylist_astate.setText("交易成功");
            holder.tv_mybuylist_delete.setVisibility(View.VISIBLE);
        }else if(Integer.valueOf(list.get(position).get("astate").toString()) == 4){
            holder.tv_mybuylist_astate.setText("退款中");
        }else if(Integer.valueOf(list.get(position).get("astate").toString()) == 5){
            holder.tv_mybuylist_astate.setText("交易失败");
            holder.tv_mybuylist_delete.setVisibility(View.VISIBLE);
        }
        holder.tv_mybuylist_aprice.setText(list.get(position).get("gprice").toString());
//        if("myout".equals(list.get(position).get("datatype").toString())){
//            holder.tv_mybuyadapter_chat.setText("联系买家");
//        }else {
//            holder.tv_mybuyadapter_chat.setText("联系卖家");
//        }
//        holder.ll_mybuylist_chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, ChatActivity.class);
//                //传递参数
//                intent.putExtra(EaseConstant.EXTRA_USER_ID, list.get(position).get("hxid").toString());
//                //单聊
//                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
//                context.startActivity(intent);
//            }
//        });
        holder.tv_mybuylist_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("删除订单")
                        .setMessage("删除后将不可恢复，确认删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("requesttop", "deleteaccount");
                                        params.put("aid", list.get(position).get("aid").toString());
                                        String strUrlpath = context.getResources().getString(R.string.burl) + "Mybuy_Servlet";
                                        String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                        System.out.println("结果为：" + Result);
                                        Message message = new Message();
                                        message.what = 1;
                                        message.obj = Result;
                                        handler.sendMessage(message);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
        TextView tv_mybuylist_gdetail,tv_mybuylist_aprice,tv_mybuylist_astate,tv_mybuylist_delete,tv_mybuyadapter_chat;
        ImageView iv_mybuylist_gimage;
        LinearLayout ll_mybuylist_chat;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int unCollectBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject unCollectResult = new JSONObject(msg.obj.toString().trim());
                            unCollectBack = unCollectResult.getInt("code");
                            if(unCollectBack == 1){
                                Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                                list.remove(myPosition);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(context,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
}
