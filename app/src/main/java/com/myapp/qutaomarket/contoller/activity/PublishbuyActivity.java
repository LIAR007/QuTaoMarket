package com.myapp.qutaomarket.contoller.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.myapp.qutaomarket.model.db.User;
import com.myapp.qutaomarket.utils.HttpUtils;
import com.myapp.qutaomarket.utils.ImageDeal;
import com.myapp.qutaomarket.utils.RealPathFromUriUtils;
import com.myapp.qutaomarket.utils.UserUtils;

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

public class PublishbuyActivity extends AppCompatActivity {

    //定义变量
    private RelativeLayout rl_pubbuy_address,rl_pubbuy_gprice,rl_pubbuy_lprice,rl_pubbuy_getway;
    private Spinner sp_pubbuy_type,sp_pubbuy_hownew;
    private ImageView iv_pubbuy_back,iv_pubbuy_publish,iv_pubbuy_image;
    private TextView tv_pubbuy_location,tv_pubbuy_publish,tv_pubbuy_gprice,tv_pubbuy_lprice,tv_pubbuy_getway;
    private EditText et_pubbuy_name,et_pubbuy_detail;

    //定义数据列表
    private List<String> type_list;
    private List<String> hownew_list;
    private ArrayAdapter<String> type_adapter;
    private ArrayAdapter<String> hownew_adapter;

    private PublishbuyActivity.OnClickListener listener;

    //标题和要发布还是要编辑
    private String type;

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

    private Bitmap buyImage = null;

    Map<String, Object> buydata = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publishbuy);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        buydata = (Map<String, Object>) intent.getSerializableExtra("data");

        initView();
        //初始化数据
        initData();
    }

    private void initView() {
        //初始化进度条
        pd = new ProgressDialog(PublishbuyActivity.this);
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

        //新旧程度
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

        //初始化变量
        rl_pubbuy_address = (RelativeLayout)findViewById(R.id.rl_pubbuy_address);
        rl_pubbuy_gprice = (RelativeLayout)findViewById(R.id.rl_pubbuy_gprice);
        rl_pubbuy_lprice = (RelativeLayout)findViewById(R.id.rl_pubbuy_lprice);
        rl_pubbuy_getway = (RelativeLayout)findViewById(R.id.rl_pubbuy_getway);
        sp_pubbuy_type = (Spinner)findViewById(R.id.sp_pubbuy_type);
        sp_pubbuy_hownew = (Spinner)findViewById(R.id.sp_pubbuy_hownew);
        iv_pubbuy_back = (ImageView)findViewById(R.id.iv_pubbuy_back);
        iv_pubbuy_publish = (ImageView)findViewById(R.id.iv_pubbuy_publish);
        iv_pubbuy_image = (ImageView)findViewById(R.id.iv_pubbuy_image);
        tv_pubbuy_location = (TextView) findViewById(R.id.tv_pubbuy_location);
        tv_pubbuy_publish = (TextView) findViewById(R.id.tv_pubbuy_publish);
        tv_pubbuy_gprice = (TextView) findViewById(R.id.tv_pubbuy_gprice);
        tv_pubbuy_lprice = (TextView) findViewById(R.id.tv_pubbuy_lprice);
        tv_pubbuy_getway = (TextView) findViewById(R.id.tv_pubbuy_getway);
        et_pubbuy_name = (EditText)findViewById(R.id.et_pubbuy_name);
        et_pubbuy_detail = (EditText)findViewById(R.id.et_pubbuy_detail);

        //设置监听
        listener = new PublishbuyActivity.OnClickListener();
        rl_pubbuy_address.setOnClickListener(listener);
        iv_pubbuy_back.setOnClickListener(listener);
        iv_pubbuy_publish.setOnClickListener(listener);
        iv_pubbuy_image.setOnClickListener(listener);
        rl_pubbuy_lprice.setOnClickListener(listener);
        rl_pubbuy_gprice.setOnClickListener(listener);
        rl_pubbuy_getway.setOnClickListener(listener);
        //适配器
        type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_list);
        hownew_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hownew_list);
        //设置样式
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hownew_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        sp_pubbuy_type.setAdapter(type_adapter);
        sp_pubbuy_hownew.setAdapter(hownew_adapter);
    }

    //初始化数据
    private void initData(){
        if("editbuy".equals(type)){
            tv_pubbuy_publish.setText("编辑求购");
            //编辑商品是不可更改图片
            iv_pubbuy_image.setClickable(false);
            filename = buydata.get("bimage").toString();
            //加载商品图片
            RequestOptions options = new RequestOptions();
            options.fitCenter()
                    .placeholder(R.drawable.ic_moren_goods)
                    .error(R.drawable.ic_moren_goods)
                    .fallback(R.drawable.ic_moren_goods);
            Glide.with(this)
                    .applyDefaultRequestOptions(options)
                    .load(buydata.get("bimage").toString())
                    .into(iv_pubbuy_image);
            //设置商品名称
            et_pubbuy_name.setText(buydata.get("bname").toString());
            //设置细节
            et_pubbuy_detail.setText(buydata.get("bdetail").toString());
            //设置最低价
            tv_pubbuy_gprice.setText(buydata.get("bsprice").toString());
            //设置最高价
            tv_pubbuy_lprice.setText(buydata.get("bbprice").toString());
            //设置商品类型
            switch (buydata.get("btype").toString()){
                case "手机数码":
                    sp_pubbuy_type.setSelection(1);
                    break;
                case "生活百货":
                    sp_pubbuy_type.setSelection(2);
                    break;
                case "游戏装备":
                    sp_pubbuy_type.setSelection(3);
                    break;
                case "家用电器":
                    sp_pubbuy_type.setSelection(4);
                    break;
                case "运动户外":
                    sp_pubbuy_type.setSelection(5);
                    break;
                case "服饰配件":
                    sp_pubbuy_type.setSelection(6);
                    break;
                case "二手图书":
                    sp_pubbuy_type.setSelection(7);
                    break;
                case "美妆":
                    sp_pubbuy_type.setSelection(8);
                    break;
                case "儿童玩具":
                    sp_pubbuy_type.setSelection(9);
                    break;
                case "园艺农用":
                    sp_pubbuy_type.setSelection(10);
                    break;
                case "健身器材":
                    sp_pubbuy_type.setSelection(11);
                    break;
                case "箱包":
                    sp_pubbuy_type.setSelection(12);
                    break;
                case "其他分类":
                    sp_pubbuy_type.setSelection(13);
                    break;
                default:
                    break;
            }
            //设置新旧程度
            switch (buydata.get("bhownew").toString()){
                case "十成新":
                    sp_pubbuy_hownew.setSelection(0);
                    break;
                case "九五新":
                    sp_pubbuy_hownew.setSelection(1);
                    break;
                case "九成新":
                    sp_pubbuy_hownew.setSelection(2);
                    break;
                case "八五新":
                    sp_pubbuy_hownew.setSelection(3);
                    break;
                case "八成新":
                    sp_pubbuy_hownew.setSelection(4);
                    break;
                case "七五新":
                    sp_pubbuy_hownew.setSelection(5);
                    break;
                case "七成新":
                    sp_pubbuy_hownew.setSelection(6);
                    break;
                case "六成新":
                    sp_pubbuy_hownew.setSelection(7);
                    break;
                case "五成新":
                    sp_pubbuy_hownew.setSelection(8);
                    break;
                case "五成以下":
                    sp_pubbuy_hownew.setSelection(9);
                    break;
                default:
                    break;
            }
            //设置交易方式
            tv_pubbuy_getway.setText(buydata.get("bgetway").toString());
            //设置发货地点
            tv_pubbuy_location.setText(buydata.get("baddress").toString());
        }else {
            //设置标题
            tv_pubbuy_publish.setText("发布求购");
            //设置发货地点
            tv_pubbuy_location.setText(MainActivity.myAddress);
        }
    }

    //handler处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int buyBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject buyResult = new JSONObject(msg.obj.toString().trim());
                            buyBack = buyResult.getInt("code");
                            if(buyBack == 1){
                                pd.cancel();
                                Toast.makeText(PublishbuyActivity.this,"求购信息发布成功",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(PublishbuyActivity.this,"求购信息发布失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(PublishbuyActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PublishbuyActivity.this,"求购编辑成功",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(PublishbuyActivity.this,"求购编辑失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(PublishbuyActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_pubbuy_back:
                    finish();
                    break;
                case R.id.rl_pubbuy_address:
                    Intent addressIntent = new Intent(PublishbuyActivity.this, PubaddressActivity.class);
                    startActivityForResult(addressIntent, 3);
                    break;
                case R.id.iv_pubbuy_image:
                    addImage();
                    break;
                case R.id.rl_pubbuy_gprice:
                    buyGprice();
                    break;
                case R.id.rl_pubbuy_lprice:
                    buyLprice();
                    break;
                case R.id.rl_pubbuy_getway:
                    goodsDeal();
                    break;
                case R.id.iv_pubbuy_publish:
                    publish();
                    break;
            }
        }
    }

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
                        imageUri = FileUtils.getUriForFile(PublishbuyActivity.this, file);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    imageUri = Uri.fromFile(file);
                }
                //裁剪图片，返回裁剪好的Uri
                cropImageUri = ImageDeal.startUCrop(PublishbuyActivity.this, imageUri, CROP_PHOTO, 800, 1200);
                break;
            case TAKE_PHOTO://相机
                try {
                    //广播刷新相册
                    Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intentBc.setData(imageUri);
                    this.sendBroadcast(intentBc);
                    //裁剪图片，返回裁剪好的Uri
                    cropImageUri = ImageDeal.startUCrop(PublishbuyActivity.this, imageUri, CROP_PHOTO, 800, 1200);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CROP_PHOTO:
                //图片解析成Bitmap对象
                try {
                    buyImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //加载商品图片
                Glide.with(PublishbuyActivity.this)
                        .load(cropImageUri)
                        .placeholder(R.drawable.ic_moren_goods)
                        .error(R.drawable.ic_moren_goods)
                        .into(iv_pubbuy_image);
                break;
            case 3:
                tv_pubbuy_location.setText(data.getStringExtra("address").trim());
                break;
        }
    }

    //添加商品图片
    private void addImage(){
        //设置日期的格式
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        new AlertDialog.Builder(PublishbuyActivity.this)
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
                                    System.out.println(outputImage);
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
                                            imageUri = FileUtils.getUriForFile(PublishbuyActivity.this, outputImage);
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

    //商品最低价
    private void buyGprice(){
        //加载布局
        final LinearLayout layout_bsprice = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_bsprice,null);
        new AlertDialog.Builder(this)
                .setView(layout_bsprice)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @SuppressLint("ResourceAsColor")
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        EditText buysprice = layout_bsprice.findViewById(R.id.et_layout_bsprice);
                        if(buysprice.getText().toString().trim().isEmpty()){
                            Toast.makeText(PublishbuyActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
                        } else if(Double.valueOf(buysprice.getText().toString().trim()) < 0){
                            Toast.makeText(PublishbuyActivity.this,"输入金额有误！",Toast.LENGTH_SHORT).show();
                        }else {
                            //将金额显示出来
                            tv_pubbuy_gprice.setText(buysprice.getText().toString());
                        }
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理取消按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    //商品最高价
    private void buyLprice(){
        //加载布局
        final LinearLayout layout_bbprice = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_bbprice,null);
        new AlertDialog.Builder(this)
                .setView(layout_bbprice)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @SuppressLint("ResourceAsColor")
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        EditText buybprice = layout_bbprice.findViewById(R.id.et_layout_bbprice);
                        if(buybprice.getText().toString().trim().isEmpty()){
                            Toast.makeText(PublishbuyActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
                        } else if(Double.valueOf(buybprice.getText().toString().trim()) < 0){
                            Toast.makeText(PublishbuyActivity.this,"输入金额有误！",Toast.LENGTH_SHORT).show();
                        }else {
                            //将金额显示出来
                            tv_pubbuy_lprice.setText(buybprice.getText().toString());
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
        new AlertDialog.Builder(PublishbuyActivity.this)
                .setItems(dealChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                tv_pubbuy_getway.setText("邮寄");
                                break;
                            case 1:
                                tv_pubbuy_getway.setText("自提");
                                break;
                            default:
                                break;
                        }
                    }
                }).create().show();
    }

    //上传商品信息
    private void publish() {
        Map<String, String> params = new HashMap<>();
        User user = UserUtils.getCurrentUser();
        //获取时间
        final Date d = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取图片
        if(filename == null) {
            Toast.makeText(PublishbuyActivity.this, "请选择图片！", Toast.LENGTH_SHORT).show();
        }else if("".equals(et_pubbuy_name.getText().toString().trim())){
            Toast.makeText(PublishbuyActivity.this, "请输入商品名！", Toast.LENGTH_SHORT).show();
        } else if("".equals(et_pubbuy_detail.getText().toString().trim())){
            Toast.makeText(PublishbuyActivity.this, "请输入商品详情！", Toast.LENGTH_SHORT).show();
        }else if(Double.valueOf(tv_pubbuy_gprice.getText().toString().trim()) > Double.valueOf(tv_pubbuy_lprice.getText().toString().trim())){
            Toast.makeText(PublishbuyActivity.this, "最低价不能小于最高价！", Toast.LENGTH_SHORT).show();
        }else if(Double.valueOf(tv_pubbuy_lprice.getText().toString().trim()) <= 0){
            Toast.makeText(PublishbuyActivity.this, "请设置最高价！", Toast.LENGTH_SHORT).show();
        }else if("请选择...".equals(sp_pubbuy_type.getSelectedItem().toString().trim())){
            Toast.makeText(PublishbuyActivity.this, "请选择类型！", Toast.LENGTH_SHORT).show();
        }else if("null".equals(UserUtils.getCurrentUser().getAddress().trim())){
            Toast.makeText(PublishbuyActivity.this, "请先设置地址！", Toast.LENGTH_SHORT).show();
        }else{
            if("publishbuy".equals(type)){
                //上传数据
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(PublishbuyActivity.this);
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
                                params.put("requesttop","publishbuy");
                                params.put("bname", et_pubbuy_name.getText().toString().trim());
                                params.put("imagestr", ImageDeal.Bitmap2String(buyImage));
                                params.put("imagename",filename);
                                params.put("bdetail", et_pubbuy_detail.getText().toString().trim());
                                params.put("bsprice", tv_pubbuy_gprice.getText().toString().trim());
                                params.put("bbprice", tv_pubbuy_lprice.getText().toString().trim());
                                params.put("btype", sp_pubbuy_type.getSelectedItem().toString().trim());
                                params.put("bhownew", sp_pubbuy_hownew.getSelectedItem().toString().trim());
                                params.put("bgetway", tv_pubbuy_getway.getText().toString().trim());
                                params.put("uaccount", user.getAccount());
                                params.put("baddress", tv_pubbuy_location.getText().toString().trim());
                                params.put("bscannum", "0");
                                params.put("bstate", "1");
                                params.put("btime", sdf.format(d));
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
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(PublishbuyActivity.this);
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
                                params.put("requesttop","republishbuy");
                                params.put("bid", buydata.get("gid").toString());
                                params.put("bname", et_pubbuy_name.getText().toString().trim());
                                params.put("bdetail", et_pubbuy_detail.getText().toString().trim());
                                params.put("bsprice", tv_pubbuy_gprice.getText().toString().trim());
                                params.put("bbprice", tv_pubbuy_lprice.getText().toString().trim());
                                params.put("btype", sp_pubbuy_type.getSelectedItem().toString().trim());
                                params.put("bhownew", sp_pubbuy_hownew.getSelectedItem().toString().trim());
                                params.put("bgetway", tv_pubbuy_getway.getText().toString().trim());
                                params.put("baddress", tv_pubbuy_location.getText().toString().trim());
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
}
