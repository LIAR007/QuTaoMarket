package com.myapp.qutaomarket.contoller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.myapp.qutaomarket.R;

public class TypeActivity extends AppCompatActivity {

    //返回按钮
    private ImageView iv_type_back;
    //各个分类
    private RelativeLayout rl_type_gameclass,rl_type_phoneclass,rl_type_dailyclass;
    private LinearLayout ll_type_elecclass,ll_type_sportsclass,ll_type_cloclass,ll_type_bookclass,ll_type_toyclass,
            ll_type_beautyclass,ll_type_farmclass,ll_type_fitclass,ll_type_backclass,ll_type_otherclass;

    //定义监听变量
    private TypeActivity.onClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        //初始化变量
        initView();
    }

    private void initView() {
        //初始化返回按钮
        iv_type_back = (ImageView) findViewById(R.id.iv_type_back);
        //初始化其他分类模块
        rl_type_gameclass = (RelativeLayout)findViewById(R.id.rl_type_gameclass);
        rl_type_phoneclass = (RelativeLayout)findViewById(R.id.rl_type_phoneclass);
        rl_type_dailyclass = (RelativeLayout)findViewById(R.id.rl_type_dailyclass);
        ll_type_elecclass = (LinearLayout)findViewById(R.id.ll_type_elecclass);
        ll_type_sportsclass = (LinearLayout)findViewById(R.id.ll_type_sportsclass);
        ll_type_cloclass = (LinearLayout)findViewById(R.id.ll_type_cloclass);
        ll_type_bookclass = (LinearLayout)findViewById(R.id.ll_type_bookclass);
        ll_type_toyclass = (LinearLayout)findViewById(R.id.ll_type_toyclass);
        ll_type_beautyclass = (LinearLayout)findViewById(R.id.ll_type_beautyclass);
        ll_type_farmclass = (LinearLayout)findViewById(R.id.ll_type_farmclass);
        ll_type_fitclass = (LinearLayout)findViewById(R.id.ll_type_fitclass);
        ll_type_backclass = (LinearLayout)findViewById(R.id.ll_type_backclass);
        ll_type_otherclass = (LinearLayout)findViewById(R.id.ll_type_otherclass);
        //返回按钮的监听
        listener = new onClickListener();
        iv_type_back.setOnClickListener(listener);
        //各个分类模块的点击监听
        rl_type_gameclass.setOnClickListener(listener);
        rl_type_phoneclass.setOnClickListener(listener);
        rl_type_dailyclass.setOnClickListener(listener);
        ll_type_elecclass.setOnClickListener(listener);
        ll_type_sportsclass.setOnClickListener(listener);
        ll_type_cloclass.setOnClickListener(listener);
        ll_type_bookclass.setOnClickListener(listener);
        ll_type_toyclass.setOnClickListener(listener);
        ll_type_beautyclass.setOnClickListener(listener);
        ll_type_farmclass.setOnClickListener(listener);
        ll_type_fitclass.setOnClickListener(listener);
        ll_type_backclass.setOnClickListener(listener);
        ll_type_otherclass.setOnClickListener(listener);
    }

    //点击事件的逻辑处理
    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_type_back:
                    finish();
                    break;
                case R.id.rl_type_gameclass:
                    Intent gameIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    gameIntent.putExtra("searchType", "goodsType");
                    gameIntent.putExtra("goodsType", "游戏装备");
                    startActivity(gameIntent);
                    break;
                case R.id.rl_type_phoneclass:
                    Intent phoneIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    phoneIntent.putExtra("searchType", "goodsType");
                    phoneIntent.putExtra("goodsType", "手机数码");
                    startActivity(phoneIntent);
                    break;
                case R.id.rl_type_dailyclass:
                    Intent dailIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    dailIntent.putExtra("searchType", "goodsType");
                    dailIntent.putExtra("goodsType", "生活百货");
                    startActivity(dailIntent);
                    break;
                case R.id.ll_type_elecclass:
                    Intent elecIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    elecIntent.putExtra("searchType", "goodsType");
                    elecIntent.putExtra("goodsType", "家用电器");
                    startActivity(elecIntent);
                    break;
                case R.id.ll_type_sportsclass:
                    Intent sportsIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    sportsIntent.putExtra("searchType", "goodsType");
                    sportsIntent.putExtra("goodsType", "运动户外");
                    startActivity(sportsIntent);
                    break;
                case R.id.ll_type_cloclass:
                    Intent cloIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    cloIntent.putExtra("searchType", "goodsType");
                    cloIntent.putExtra("goodsType", "服饰配件");
                    startActivity(cloIntent);
                    break;
                case R.id.ll_type_bookclass:
                    Intent bookIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    bookIntent.putExtra("searchType", "goodsType");
                    bookIntent.putExtra("goodsType", "二手图书");
                    startActivity(bookIntent);
                    break;
                case R.id.ll_type_toyclass:
                    Intent toyIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    toyIntent.putExtra("searchType", "goodsType");
                    toyIntent.putExtra("goodsType", "儿童玩具");
                    startActivity(toyIntent);
                    break;
                case R.id.ll_type_beautyclass:
                    Intent beautyIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    beautyIntent.putExtra("searchType", "goodsType");
                    beautyIntent.putExtra("goodsType", "美妆");
                    startActivity(beautyIntent);
                    break;
                case R.id.ll_type_farmclass:
                    Intent farmIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    farmIntent.putExtra("searchType", "goodsType");
                    farmIntent.putExtra("goodsType", "园艺农用");
                    startActivity(farmIntent);
                    break;
                case R.id.ll_type_fitclass:
                    Intent fitIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    fitIntent.putExtra("searchType", "goodsType");
                    fitIntent.putExtra("goodsType", "健身器材");
                    startActivity(fitIntent);
                    break;
                case R.id.ll_type_backclass:
                    Intent backIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    backIntent.putExtra("searchType", "goodsType");
                    backIntent.putExtra("goodsType", "箱包");
                    startActivity(backIntent);
                    break;
                case R.id.ll_type_otherclass:
                    Intent otherIntent = new Intent(TypeActivity.this, GoodsListActivity.class);
                    otherIntent.putExtra("searchType", "goodsType");
                    otherIntent.putExtra("goodsType", "其他商品");
                    startActivity(otherIntent);
                    break;

            }
        }
    }
}
