package com.myapp.qutaomarket.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SetViewHeight {
    static public void setListViewHeight(ListView listView){
        //获取adapter
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null){
            return;
        }
        int totalHeight = 0;
        //listAdapter.getCount()返回数据项的数目
        for(int i=0;i<listAdapter.getCount();i++){
            View listItem = listAdapter.getView(i,null,listView);
            listItem.measure(0,0);  //计算子项View的高度
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项总高度
        }
        /*
        listView.getDividerHeight()获取子项间分隔符占用的高度
        params.height 最后得到ListView完整显示需要的高度
         */
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+(listView.getDividerHeight()*(listAdapter.getCount()-1));
        listView.setLayoutParams(params);
    }
}
