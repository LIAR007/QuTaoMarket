package com.myapp.qutaomarket.contoller.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.util.FileUtils;
import com.myapp.qutaomarket.QTApplication;
import com.myapp.qutaomarket.R;
import com.myapp.qutaomarket.model.Model;
import com.myapp.qutaomarket.utils.HttpUtils;
import com.myapp.qutaomarket.utils.ImageDeal;
import com.myapp.qutaomarket.utils.RealPathFromUriUtils;
import com.myapp.qutaomarket.utils.UserUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishgoodsActivity extends AppCompatActivity {

    //初始化控件变量
    private RelativeLayout rl_pub_address, rl_pub_price, rl_pub_oprice,rl_pub_style;
    private Spinner sp_pubgoods_type,sp_pubgoods_hownew;
    private TextView tv_pub_title, tv_pub_style, tv_pub_address, tv_pub_price, tv_pub_oprice;
    private ImageView iv_pubgoods_back, iv_pubgoods_goods, iv_pub_publish;
    private EditText et_pub_name, et_pub_detail;
//    private CheckBox cb_pub_ji;
    private List<String> type_list;
    private List<String> hownew_list;
    private ArrayAdapter<String> type_adapter;
    private ArrayAdapter<String> hownew_adapter;

    //标题和要发布的商品类型
    private String title,type;
    //商品id
    private int gid;

    //设置控件监听变量
    private PublishgoodsActivity.OnClickListener listener;

    //设置进度条
    private ProgressDialog pd;

    //设置弹出的alertdialog的初值
    private CharSequence[] pictureChoice = {"拍照","从相册选择"};
    private CharSequence[] dealChoice = {"邮寄","自提"};

    //定义图片操作的变量
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int SELECT_PIC = 0;
    private Uri imageUri; //图片路径
    private Uri cropImageUri; //裁剪后的图片路径
    private String filename; //图片名称

    private Bitmap goodsImage = null;

    Map<String, Object> goodsdata = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publishgoods);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        type = intent.getStringExtra("type");
        goodsdata = (Map<String, Object>) intent.getSerializableExtra("data");

        initView();
        //初始化数据
        initData();
    }

    private void initView() {
        //初始化进度条
        pd = new ProgressDialog(PublishgoodsActivity.this);
        pd.setMessage("发布中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        //商品种类
        type_list = new ArrayList<String>();
        type_list.add("请选择...");
        type_list.add("手机数码");
        type_list.add("生活百货");
        type_list.add("游戏装备");
        type_list.add("家用电器");
        type_list.add("运动户外");
        type_list.add("服饰配件");
        type_list.add("二手图书");
        type_list.add("美妆");
        type_list.add("儿童玩具");
        type_list.add("园艺农用");
        type_list.add("健身器材");
        type_list.add("箱包");
        type_list.add("其他分类");

        //新数据
        hownew_list = new ArrayList<String>();
        hownew_list.add("十成新");
        hownew_list.add("九五新");
        hownew_list.add("九成新");
        hownew_list.add("八五新");
        hownew_list.add("八成新");
        hownew_list.add("七五新");
        hownew_list.add("七成新");
        hownew_list.add("六成新");
        hownew_list.add("五成新");
        hownew_list.add("五成以下");
        //初始化
        rl_pub_address = (RelativeLayout)findViewById(R.id.rl_pub_address);
        rl_pub_price = (RelativeLayout)findViewById(R.id.rl_pub_price);
        rl_pub_oprice = (RelativeLayout)findViewById(R.id.rl_pub_oprice);
        rl_pub_style = (RelativeLayout)findViewById(R.id.rl_pub_style);
        sp_pubgoods_type = (Spinner)findViewById(R.id.sp_pubgoods_type);
        sp_pubgoods_hownew = (Spinner)findViewById(R.id.sp_pubgoods_hownew);
        tv_pub_title = (TextView) findViewById(R.id.tv_pub_title);
        tv_pub_address = (TextView) findViewById(R.id.tv_pub_address);
        tv_pub_style = (TextView) findViewById(R.id.tv_pub_style);
        tv_pub_price = (TextView) findViewById(R.id.tv_pub_price);
        tv_pub_oprice = (TextView) findViewById(R.id.tv_pub_oprice);
        iv_pubgoods_back = (ImageView) findViewById(R.id.iv_pubgoods_back);
        iv_pubgoods_goods = (ImageView) findViewById(R.id.iv_pubgoods_goods);
        iv_pub_publish = (ImageView) findViewById(R.id.iv_pub_publish);
        et_pub_name = (EditText)findViewById(R.id.et_pub_name);
        et_pub_detail = (EditText)findViewById(R.id.et_pub_detail);
//        cb_pub_ji = (CheckBox)findViewById(R.id.cb_pub_ji);

        //设置标题
        tv_pub_title.setText(title);

        //设置按键的监听
        listener = new OnClickListener();
        rl_pub_address.setOnClickListener(listener);
        iv_pubgoods_back.setOnClickListener(listener);
        rl_pub_price.setOnClickListener(listener);
        rl_pub_oprice.setOnClickListener(listener);
        rl_pub_style.setOnClickListener(listener);
        iv_pubgoods_goods.setOnClickListener(listener);
        iv_pub_publish.setOnClickListener(listener);

        //适配器
        type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_list);
        hownew_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hownew_list);
        //设置样式
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hownew_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        sp_pubgoods_type.setAdapter(type_adapter);
        sp_pubgoods_hownew.setAdapter(hownew_adapter);
    }

    //初始化数据
    private void initData(){
        //发布免费送
        if("publishfree".equals(type)){
            rl_pub_price.setClickable(false);
            rl_pub_oprice.setClickable(false);
            tv_pub_price.setText("0.00");
            tv_pub_oprice.setText("0.00");
//            cb_pub_ji.setClickable(false);
            tv_pub_address.setText(MainActivity.myAddress);
        }else if("editgoods".equals(type)){       //编辑商品
            //编辑商品是不可更改图片
            iv_pubgoods_goods.setClickable(false);
            filename = goodsdata.get("gimage").toString();
            //加载商品图片
            RequestOptions options = new RequestOptions();
            options.fitCenter()
                    .placeholder(R.drawable.ic_moren_goods)
                    .error(R.drawable.ic_moren_goods)
                    .fallback(R.drawable.ic_moren_goods);
            Glide.with(this)
                    .applyDefaultRequestOptions(options)
                    .load(goodsdata.get("gimage").toString())
                    .into(iv_pubgoods_goods);
            //设置商品名称
            et_pub_name.setText(goodsdata.get("gname").toString());
            //设置细节
            et_pub_detail.setText(goodsdata.get("gdetail").toString());
            //设置价格
            tv_pub_price.setText(goodsdata.get("gprice").toString());
            //设置原价
            tv_pub_oprice.setText(goodsdata.get("goprice").toString());
            //设置商品类型
            switch (goodsdata.get("gtype").toString()){
                case "手机数码":
                    sp_pubgoods_type.setSelection(1);
                    break;
                case "生活百货":
                    sp_pubgoods_type.setSelection(2);
                    break;
                case "游戏装备":
                    sp_pubgoods_type.setSelection(3);
                    break;
                case "家用电器":
                    sp_pubgoods_type.setSelection(4);
                    break;
                case "运动户外":
                    sp_pubgoods_type.setSelection(5);
                    break;
                case "服饰配件":
                    sp_pubgoods_type.setSelection(6);
                    break;
                case "二手图书":
                    sp_pubgoods_type.setSelection(7);
                    break;
                case "美妆":
                    sp_pubgoods_type.setSelection(8);
                    break;
                case "儿童玩具":
                    sp_pubgoods_type.setSelection(9);
                    break;
                case "园艺农用":
                    sp_pubgoods_type.setSelection(10);
                    break;
                case "健身器材":
                    sp_pubgoods_type.setSelection(11);
                    break;
                case "箱包":
                    sp_pubgoods_type.setSelection(12);
                    break;
                case "其他分类":
                    sp_pubgoods_type.setSelection(13);
                    break;
                default:
                    break;
            }
            //设置新旧程度
            switch (goodsdata.get("ghownew").toString()){
                case "十成新":
                    sp_pubgoods_hownew.setSelection(0);
                    break;
                case "九五新":
                    sp_pubgoods_hownew.setSelection(1);
                    break;
                case "九成新":
                    sp_pubgoods_hownew.setSelection(2);
                    break;
                case "八五新":
                    sp_pubgoods_hownew.setSelection(3);
                    break;
                case "八成新":
                    sp_pubgoods_hownew.setSelection(4);
                    break;
                case "七五新":
                    sp_pubgoods_hownew.setSelection(5);
                    break;
                case "七成新":
                    sp_pubgoods_hownew.setSelection(6);
                    break;
                case "六成新":
                    sp_pubgoods_hownew.setSelection(7);
                    break;
                case "五成新":
                    sp_pubgoods_hownew.setSelection(8);
                    break;
                case "五成以下":
                    sp_pubgoods_hownew.setSelection(9);
                    break;
                    default:
                        break;
            }
            //设置交易方式
            tv_pub_style.setText(goodsdata.get("ggetway").toString());
//            //设置是否加急
//            if(!"1".equals(goodsdata.get("gemergent").toString())){
//                cb_pub_ji.setChecked(true);
//            }else cb_pub_ji.setChecked(false);
            //设置发货地点
            tv_pub_address.setText(goodsdata.get("gaddress").toString());
        }else if("editfree".equals(type)){       //编辑免费送商品
            //编辑商品是不可更改图片
            iv_pubgoods_goods.setClickable(false);
            filename = goodsdata.get("gimage").toString();
            //加载商品图片
            RequestOptions options = new RequestOptions();
            options.fitCenter()
                    .placeholder(R.drawable.ic_moren_goods)
                    .error(R.drawable.ic_moren_goods)
                    .fallback(R.drawable.ic_moren_goods);
            Glide.with(this)
                    .applyDefaultRequestOptions(options)
                    .load(goodsdata.get("gimage").toString())
                    .into(iv_pubgoods_goods);
            //设置商品名称
            et_pub_name.setText(goodsdata.get("gname").toString());
            //设置细节
            et_pub_detail.setText(goodsdata.get("gdetail").toString());
            //设置价格和加急不可点击
            rl_pub_price.setClickable(false);
            rl_pub_oprice.setClickable(false);
            tv_pub_price.setText("0.00");
            tv_pub_oprice.setText("0.00");
//            cb_pub_ji.setClickable(false);
            //设置商品类型
            switch (goodsdata.get("gtype").toString()){
                case "手机数码":
                    sp_pubgoods_type.setSelection(1);
                    break;
                case "生活百货":
                    sp_pubgoods_type.setSelection(2);
                    break;
                case "游戏装备":
                    sp_pubgoods_type.setSelection(3);
                    break;
                case "家用电器":
                    sp_pubgoods_type.setSelection(4);
                    break;
                case "运动户外":
                    sp_pubgoods_type.setSelection(5);
                    break;
                case "服饰配件":
                    sp_pubgoods_type.setSelection(6);
                    break;
                case "二手图书":
                    sp_pubgoods_type.setSelection(7);
                    break;
                case "美妆":
                    sp_pubgoods_type.setSelection(8);
                    break;
                case "儿童玩具":
                    sp_pubgoods_type.setSelection(9);
                    break;
                case "园艺农用":
                    sp_pubgoods_type.setSelection(10);
                    break;
                case "健身器材":
                    sp_pubgoods_type.setSelection(11);
                    break;
                case "箱包":
                    sp_pubgoods_type.setSelection(12);
                    break;
                case "其他分类":
                    sp_pubgoods_type.setSelection(13);
                    break;
                default:
                    break;
            }
            //设置新旧程度
            switch (goodsdata.get("ghownew").toString()){
                case "十成新":
                    sp_pubgoods_hownew.setSelection(0);
                    break;
                case "九五新":
                    sp_pubgoods_hownew.setSelection(1);
                    break;
                case "九成新":
                    sp_pubgoods_hownew.setSelection(2);
                    break;
                case "八五新":
                    sp_pubgoods_hownew.setSelection(3);
                    break;
                case "八成新":
                    sp_pubgoods_hownew.setSelection(4);
                    break;
                case "七五新":
                    sp_pubgoods_hownew.setSelection(5);
                    break;
                case "七成新":
                    sp_pubgoods_hownew.setSelection(6);
                    break;
                case "六成新":
                    sp_pubgoods_hownew.setSelection(7);
                    break;
                case "五成新":
                    sp_pubgoods_hownew.setSelection(8);
                    break;
                case "五成以下":
                    sp_pubgoods_hownew.setSelection(9);
                    break;
                default:
                    break;
            }
            //设置交易方式
            tv_pub_style.setText(goodsdata.get("ggetway").toString());
            //设置发货地点
            tv_pub_address.setText(goodsdata.get("gaddress").toString());
        }else {
            tv_pub_address.setText(MainActivity.myAddress);
        };
    }

    //按键点击的逻辑处理
    private class OnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_pubgoods_back:
                    finish();
                    break;
                case R.id.rl_pub_address:
                    Intent addressIntent = new Intent(PublishgoodsActivity.this, PubaddressActivity.class);
                    startActivityForResult(addressIntent, 3);
                    break;
                case R.id.rl_pub_price:
                    goodsPrice();
                    break;
                case R.id.rl_pub_oprice:
                    goodsOprice();
                    break;
                case R.id.rl_pub_style:
                    goodsDeal();
                    break;
                case R.id.iv_pubgoods_goods:
                    addImage();
                    break;
                case R.id.iv_pub_publish:
                    publish();
                    break;
            }
        }
    }

    //handler消息处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int goodsBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject goodsResult = new JSONObject(msg.obj.toString().trim());
                            goodsBack = goodsResult.getInt("code");
                            if(goodsBack == 1){
                                pd.cancel();
                                Toast.makeText(PublishgoodsActivity.this,"商品发布成功",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(PublishgoodsActivity.this,"商品发布失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(PublishgoodsActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    int republishBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject republishResult = new JSONObject(msg.obj.toString().trim());
                            republishBack = republishResult.getInt("code");
                            if(republishBack == 1){
                                pd.cancel();
                                Toast.makeText(PublishgoodsActivity.this,"商品编辑成功",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(PublishgoodsActivity.this,"商品编辑失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(PublishgoodsActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //上传商品信息
    private void publish() {
        Map<String, String> params = new HashMap<>();
        //获取时间
        final Date d = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取图片
        if(filename == null) {
            Toast.makeText(PublishgoodsActivity.this, "请选择图片！", Toast.LENGTH_SHORT).show();
        }else if("".equals(et_pub_name.getText().toString().trim())){
            Toast.makeText(PublishgoodsActivity.this, "请输入商品名！", Toast.LENGTH_SHORT).show();
        } else if("".equals(et_pub_detail.getText().toString().trim())){
            Toast.makeText(PublishgoodsActivity.this, "请输入商品详情！", Toast.LENGTH_SHORT).show();
        }else if("goods".equals(type)&&(Double.valueOf(tv_pub_price.getText().toString().trim()) == 0)){
            Toast.makeText(PublishgoodsActivity.this, "价格不能为0！", Toast.LENGTH_SHORT).show();
        }else if("请选择...".equals(sp_pubgoods_type.getSelectedItem().toString().trim())){
            Toast.makeText(PublishgoodsActivity.this, "请选择类型！", Toast.LENGTH_SHORT).show();
        }else if("null".equals(UserUtils.getCurrentUser().getAddress().trim())){
            Toast.makeText(PublishgoodsActivity.this, "请先设置地址！", Toast.LENGTH_SHORT).show();
        }else{
            if("publishgoods".equals(type) || "publishfree".equals(type)){
                //上传数据
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(PublishgoodsActivity.this);
                normalDialog.setTitle("提示");
                normalDialog.setMessage("图片发布后不可更改，确定发布吗？");
                normalDialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.show();
                        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                //设置数据
                                params.put("requesttop","publish");
                                params.put("gname", et_pub_name.getText().toString().trim());
                                params.put("imagestr", ImageDeal.Bitmap2String(goodsImage));
                                params.put("imagename",filename);
                                params.put("gdetail", et_pub_detail.getText().toString().trim());
                                params.put("gprice", tv_pub_price.getText().toString().trim());
                                params.put("goprice", tv_pub_oprice.getText().toString().trim());
                                params.put("gtype", sp_pubgoods_type.getSelectedItem().toString().trim());
                                params.put("ghownew", sp_pubgoods_hownew.getSelectedItem().toString().trim());
                                params.put("ggetway", tv_pub_style.getText().toString().trim());
//                                if(cb_pub_ji.isChecked()) params.put("gemergent", "2");
//                                else
                                    params.put("gemergent", "1");
                                params.put("uaccount", UserUtils.getCurrentUser().getAccount());
                                params.put("gaddress", tv_pub_address.getText().toString().trim());
                                params.put("gscannum", "0");
                                params.put("gstate", "1");
                                params.put("gtime", sdf.format(d));

                                String strUrlpath = getResources().getString(R.string.burl) + "Publish_Servlet";
                                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                System.out.println("结果为：" + Result);
                                Message message = new Message();
                                message.what = 1;
                                message.obj = Result;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });
                normalDialog.setNegativeButton("关闭",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                normalDialog.show();
            }else {
                //上传修改后的数据
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(PublishgoodsActivity.this);
                normalDialog.setTitle("提示");
                normalDialog.setMessage("确定更改吗？");
                normalDialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.show();
                        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                //设置数据
                                params.put("requesttop","republish");
                                params.put("gid", goodsdata.get("gid").toString());
                                params.put("gname", et_pub_name.getText().toString().trim());
                                params.put("gdetail", et_pub_detail.getText().toString().trim());
                                params.put("gprice", tv_pub_price.getText().toString().trim());
                                params.put("goprice", tv_pub_oprice.getText().toString().trim());
                                params.put("gtype", sp_pubgoods_type.getSelectedItem().toString().trim());
                                params.put("ghownew", sp_pubgoods_hownew.getSelectedItem().toString().trim());
                                params.put("ggetway", tv_pub_style.getText().toString().trim());
//                                if(cb_pub_ji.isChecked()) params.put("gemergent", "2");
//                                else
                                    params.put("gemergent", "1");
                                params.put("gaddress", tv_pub_address.getText().toString().trim());
                                //发起请求
                                String strUrlpath = getResources().getString(R.string.burl) + "Publish_Servlet";
                                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                System.out.println("结果为：" + Result);
                                Message message = new Message();
                                message.what = 2;
                                message.obj = Result;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });
                normalDialog.setNegativeButton("关闭",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                normalDialog.show();
            }

        }
    }

    //从其他activity返回的数据处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case SELECT_PIC://相册
                String path = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
                File file = new File(path);
                filename = file.getName();
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        imageUri = FileUtils.getUriForFile(PublishgoodsActivity.this, file);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    imageUri = Uri.fromFile(file);
                }
                //裁剪图片，返回裁剪好的Uri
                cropImageUri = ImageDeal.startUCrop(PublishgoodsActivity.this, imageUri, CROP_PHOTO, 800, 1200);
                break;
            case TAKE_PHOTO://相机
                try {
                    //广播刷新相册
                    Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intentBc.setData(imageUri);
                    this.sendBroadcast(intentBc);
                    //裁剪图片，返回裁剪好的Uri
                    cropImageUri = ImageDeal.startUCrop(PublishgoodsActivity.this, imageUri, CROP_PHOTO, 800, 1200);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CROP_PHOTO:
                //图片解析成Bitmap对象
                try {
                    goodsImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //加载商品图片
                Glide.with(PublishgoodsActivity.this)
                        .load(cropImageUri)
                        .placeholder(R.drawable.ic_moren_goods)
                        .error(R.drawable.ic_moren_goods)
                        .into(iv_pubgoods_goods);
                break;
            case 3:
                tv_pub_address.setText(data.getStringExtra("address").trim());
                break;
            default:
                break;
        }
    }

    //添加商品图片
    private void addImage(){
        //设置日期的格式
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        new AlertDialog.Builder(PublishgoodsActivity.this)
                .setTitle("添加图片")
                .setItems(pictureChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            switch (which)
                            {
                                case 0://拍照
                                    //图片名称 时间命名
                                    Date date = new Date(System.currentTimeMillis());
                                    filename = format.format(date);
                                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                                    File outputImage = new File(path,filename+".jpg");
                                    filename = filename + ".jpg";
                                    try {
                                        if(outputImage.exists()) {
                                            outputImage.delete();
                                        }
                                        outputImage.createNewFile();
                                    } catch(IOException e) {
                                        e.printStackTrace();
                                    }
                                    //将File对象转换为Uri并启动照相程序
                                    if (Build.VERSION.SDK_INT >= 24) {
                                        try{
                                            imageUri = FileUtils.getUriForFile(PublishgoodsActivity.this, outputImage);
                                        }catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        imageUri = Uri.fromFile(outputImage);
                                    }
                                    Intent tTntent = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
                                    tTntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
                                    startActivityForResult(tTntent,TAKE_PHOTO); //启动照相
                                    break;
                                case 1://从相册选择
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                                    startActivityForResult(intent,SELECT_PIC);
                                    break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                })
                .create()
                .show();
    }

    //商品金额
    private void goodsPrice(){
        //加载布局
        final LinearLayout layout_goodsprice = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_goodsprice,null);
        new AlertDialog.Builder(this)
                .setView(layout_goodsprice)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @SuppressLint("ResourceAsColor")
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        EditText goodsprice = layout_goodsprice.findViewById(R.id.et_layout_goodsprice);
                        if(goodsprice.getText().toString().trim().isEmpty())
                        {
                            Toast.makeText(PublishgoodsActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
                        }else if(Double.valueOf(goodsprice.getText().toString().trim()) <= 0){
                            Toast.makeText(PublishgoodsActivity.this,"输入金额有误！",Toast.LENGTH_SHORT).show();
                        }else
                        {
                            //将金额显示出来
                            tv_pub_price.setText(goodsprice.getText().toString());
                        }
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理取消按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    //商品原价
    private void goodsOprice(){
        //加载布局
        final LinearLayout layout_goodsoprice = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_goodsoprice,null);
        new AlertDialog.Builder(this)
                .setView(layout_goodsoprice)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @SuppressLint("ResourceAsColor")
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        EditText goodsoprice = layout_goodsoprice.findViewById(R.id.et_layout_goodsoprice);
                        if(Double.valueOf(goodsoprice.getText().toString().trim()) < 0){
                            Toast.makeText(PublishgoodsActivity.this,"输入金额有误！",Toast.LENGTH_SHORT).show();
                        }else
                        {
                            //将金额显示出来
                            tv_pub_oprice.setText(goodsoprice.getText().toString());
                        }
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理取消按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    //交易方式
    private void goodsDeal(){
        new AlertDialog.Builder(PublishgoodsActivity.this)
                .setItems(dealChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                tv_pub_style.setText("邮寄");
                                break;
                            case 1:
                                tv_pub_style.setText("自提");
                                break;
                                default:
                                    break;
                        }
                    }
                }).create().show();
    }
}
