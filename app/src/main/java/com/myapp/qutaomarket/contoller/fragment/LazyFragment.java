package com.myapp.qutaomarket.contoller.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class LazyFragment extends Fragment {
    // 标志位，标志已经初始化完成。
    protected boolean isPrepared;
    //判断数据是否加载过，防止数据重复加载
    protected boolean isLoad;
    protected boolean isVisible;
    protected abstract void lazyLoad();//子类实现

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {//当可见的时候执行的操作
            isVisible = true;
            onVisible();
        } else {//不可见时执行的操作
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void onInvisible() {
    }
}
