package com.myapp.qutaomarket.contoller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.util.FileUtils;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PersonActivity extends AppCompatActivity {

    //定义控件变量
    private ImageView iv_personinfo_back,iv_info_headPhoto;
    private RelativeLayout rl_info_adrress,rl_info_headphoto,rl_info_nick,rl_info_sex,rl_info_school,rl_info_tel,rl_info_balance;
    private TextView tv_info_nickname,tv_info_balance,tv_info_sex,tv_info_school,tv_info_tel,tv_info_account,tv_info_repu;

    //定义控件点击监听
    private PersonActivity.onClickListener listener;

    //设置弹出的alertdialog的初值
    private CharSequence[] pictureChoice = {"拍照","从相册选择"};
    private CharSequence[] balanceChoice = {"充值","提现"};
    private CharSequence[] sexChoice = {"男","女"};

    //定义图片操作的变量
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int SELECT_PIC = 0;
    private Uri imageUri; //图片路径
    private String filename; //图片名称
    private Uri cropImageUri; //裁剪后的图片路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        //初始化
        initView();
    }

    //初始化
    private void initView() {
        rl_info_adrress = (RelativeLayout)findViewById(R.id.rl_info_adrress);
        rl_info_headphoto = (RelativeLayout)findViewById(R.id.rl_info_headphoto);
        rl_info_nick = (RelativeLayout)findViewById(R.id.rl_info_nick);
        rl_info_sex = (RelativeLayout)findViewById(R.id.rl_info_sex);
        rl_info_school = (RelativeLayout)findViewById(R.id.rl_info_school);
        rl_info_tel = (RelativeLayout)findViewById(R.id.rl_info_tel);
        rl_info_balance = (RelativeLayout)findViewById(R.id.rl_info_balance);
        iv_personinfo_back = (ImageView) findViewById(R.id.iv_personinfo_back);
        iv_info_headPhoto = (ImageView) findViewById(R.id.iv_info_headPhoto);
        tv_info_nickname = (TextView) findViewById(R.id.tv_info_nickname);
        tv_info_balance = (TextView) findViewById(R.id.tv_info_balance);
        tv_info_sex = (TextView) findViewById(R.id.tv_info_sex);
        tv_info_school = (TextView) findViewById(R.id.tv_info_school);
        tv_info_tel = (TextView) findViewById(R.id.tv_info_tel);
        tv_info_account = (TextView) findViewById(R.id.tv_info_account);
        tv_info_repu = (TextView) findViewById(R.id.tv_info_repu);
        //返回按钮的监听
        listener = new onClickListener();
        iv_personinfo_back.setOnClickListener(listener);
        rl_info_adrress.setOnClickListener(listener);
        rl_info_headphoto.setOnClickListener(listener);
        rl_info_nick.setOnClickListener(listener);
        rl_info_sex.setOnClickListener(listener);
        rl_info_school.setOnClickListener(listener);
        rl_info_tel.setOnClickListener(listener);
        rl_info_balance.setOnClickListener(listener);
        //从数据库获取用户信息
        User user = UserUtils.getCurrentUser();
        //设置头像
        if(!"null".equals(user.getHeadPhoto().trim())){
            String imagePath = null;
            try {
                imagePath = URLEncoder.encode(user.getHeadPhoto().trim(), "utf-8");
                System.out.println("imagepath:"+imagePath);
                if("null".equals(imagePath)){
                    Glide.with(PersonActivity.this)
                            .load(R.drawable.moren_headphoto)
                            .circleCrop()
                            .placeholder(R.drawable.moren_headphoto)
                            .error(R.drawable.moren_headphoto)
                            .into(iv_info_headPhoto);
                }else {
                    Glide.with(PersonActivity.this)
                            .load(getResources().getString(R.string.burl)+ "Image_Servlet?" + imagePath)
                            .circleCrop()
                            .placeholder(R.drawable.moren_headphoto)
                            .error(R.drawable.moren_headphoto)
                            .into(iv_info_headPhoto);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //设置学校
        if(!"null".equals(user.getSchool().trim())){
            tv_info_school.setText(user.getSchool().trim());
        }
        //设置账号
        tv_info_account.setText(user.getAccount().trim());
        //设置昵称
        tv_info_nickname.setText(user.getNickName().trim());
        //设置信誉值
        int reputation = user.getReputation();
        if(reputation < 500){
            tv_info_repu.setText(Integer.toString(reputation) + " | 信用一般");
        }else if((reputation>=500)&&(reputation<700)){
            tv_info_repu.setText(Integer.toString(reputation) + " | 信用良好");
        }else if((reputation>=700)&&(reputation<900)){
            tv_info_repu.setText(Integer.toString(reputation) + " | 信用优秀");
        }else if((reputation>=900)&&(reputation<=1000)){
            tv_info_repu.setText(Integer.toString(reputation) + " | 信用极好");
        }
        //设置账户余额
        tv_info_balance.setText(String.valueOf(user.getBalance()));
        //设置性别
        tv_info_sex.setText((user.getSex() == 1) ? "男" : "女");
        //设置手机号
        tv_info_tel.setText(user.getTel().trim());
    }

    //控件点击事件的逻辑实现
    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //返回按键
                case R.id.iv_personinfo_back:
                    finish();
                    break;
                case R.id.rl_info_adrress://地址
                    Intent addressIntent = new Intent(PersonActivity.this, AddressActivity.class);
                    startActivity(addressIntent);
                    break;
                case R.id.rl_info_headphoto://头像
                    setPhoto();
                    break;
                case R.id.rl_info_nick://昵称
                    changeNickname();
                    break;
                case R.id.rl_info_balance://余额
                    changeBalance();
                    break;
                case R.id.rl_info_sex://性别
                    setSex();
                    break;
                case R.id.rl_info_school://学校
                    setSchool();
                    break;
                case R.id.rl_info_tel://联系方式
                    setPhone();
                    break;

            }
        }
    }

    //处理网络请求返回的数据
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            User user = UserUtils.getCurrentUser();
            switch (msg.what){
                case 1:                  //修改头像
                    int photoBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject photoResult = new JSONObject(msg.obj.toString().trim());
                            JSONObject photoParams = new JSONObject(photoResult.getString("params"));
                            photoBack = photoParams.getInt("code");
                            if(photoBack == 1){
                                user.setHeadPhoto(photoParams.getString("path").trim());
                                user.save();
                                //加载头像
                                Glide.with(PersonActivity.this)
                                        .load(imageUri)
                                        .circleCrop()
                                        .placeholder(R.drawable.moren_headphoto)
                                        .error(R.drawable.moren_headphoto)
                                        .into(iv_info_headPhoto);
                                Toast.makeText(PersonActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(PersonActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    else Toast.makeText(PersonActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    int nameBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject nameResult = new JSONObject(msg.obj.toString().trim());
                            nameBack = nameResult.getInt("code");
                            JSONObject nameParams = new JSONObject(nameResult.getString("data"));
                            if(nameBack == 1){
                                user.setNickName(nameParams.getString("name").trim());
                                user.save();
                                //修改显示的昵称
                                tv_info_nickname.setText(nameParams.getString("name").trim());
                                Toast.makeText(PersonActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(PersonActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else Toast.makeText(PersonActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    int chargeBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject chargeResult = new JSONObject(msg.obj.toString().trim());
                            chargeBack = chargeResult.getInt("code");
                            JSONObject chargeParams = new JSONObject(chargeResult.getString("data"));
                            if(chargeBack == 1){
                                user.setBalance(chargeParams.getDouble("balance"));
                                user.save();
                                //修改显示的余额
                                tv_info_balance.setText(chargeParams.getString("balance"));
                                Toast.makeText(PersonActivity.this,"充值成功",Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(PersonActivity.this,"充值失败",Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(PersonActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4:
                    int tixianBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject tixianResult = new JSONObject(msg.obj.toString().trim());
                            tixianBack = tixianResult.getInt("code");
                            JSONObject tixianParams = new JSONObject(tixianResult.getString("data"));
                            if(tixianBack == 1){
                                user.setBalance(tixianParams.getDouble("balance"));
                                user.save();
                                //修改显示的余额
                                tv_info_balance.setText(tixianParams.getString("balance"));
                                Toast.makeText(PersonActivity.this,"提现成功",Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(PersonActivity.this,"提现失败",Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(PersonActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 5:
                    int sexBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject sexResult = new JSONObject(msg.obj.toString().trim());
                            sexBack = sexResult.getInt("code");
                            JSONObject sexParams = new JSONObject(sexResult.getString("data"));
                            if(sexBack == 1){
                                user.setSex(sexParams.getInt("sex"));
                                user.save();
                                //修改显示的性别
                                tv_info_sex.setText((sexParams.getInt("sex") == 1) ? "男" : "女");
                                Toast.makeText(PersonActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(PersonActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(PersonActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 6:
                    int schoolBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject schoolResult = new JSONObject(msg.obj.toString().trim());
                            schoolBack = schoolResult.getInt("code");
                            JSONObject schoolParams = new JSONObject(schoolResult.getString("data"));
                            if(schoolBack == 1){
                                user.setSchool(schoolParams.getString("school"));
                                user.save();
                                //修改显示的学校
                                tv_info_school.setText(schoolParams.getString("school"));
                                Toast.makeText(PersonActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(PersonActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(PersonActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 7:
                    int telBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        try{
                            JSONObject telResult = new JSONObject(msg.obj.toString().trim());
                            telBack = telResult.getInt("code");
                            JSONObject telParams = new JSONObject(telResult.getString("data"));
                            if(telBack == 1){
                                user.setTel(telParams.getString("tel"));
                                user.save();
                                //修改显示的电话号
                                tv_info_tel.setText(telParams.getString("tel"));
                                Toast.makeText(PersonActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(PersonActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(PersonActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //设置头像
    private void setPhoto()
    {
        //设置日期的格式
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        new AlertDialog.Builder(PersonActivity.this)
                .setTitle("更换头像")
                .setItems(pictureChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            switch (which)
                            {
                                case 0://拍照
                                    //图片名称 时间命名
                                    //System.currentTimeMillis()获取当前系统时间
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
                                            imageUri = FileUtils.getUriForFile(PersonActivity.this, outputImage);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case SELECT_PIC://相册
                String path = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
                File file = new File(path);
                filename = file.getName();
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        imageUri = FileUtils.getUriForFile(PersonActivity.this, file);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    imageUri = Uri.fromFile(file);
                }
                //裁剪图片，返回裁剪好的Uri
                cropImageUri = ImageDeal.startUCrop(PersonActivity.this, imageUri, CROP_PHOTO, 500, 500);
                break;
            case TAKE_PHOTO://相机
                try {
                    //广播刷新相册
                    Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intentBc.setData(imageUri);
                    this.sendBroadcast(intentBc);
                    //裁剪图片，返回裁剪好的Uri
                    cropImageUri = ImageDeal.startUCrop(PersonActivity.this, imageUri, CROP_PHOTO, 500, 500);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CROP_PHOTO:
                uploadPicture();   //上传头像
                break;
            default:
                break;
        }
    }

    //上传头像
    public  void uploadPicture()
    {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap finalBitmap = bitmap;
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop", "headPhoto");  //设置请求头
                params.put("account",UserUtils.getCurrentUser().getAccount());
                params.put("imagestr", ImageDeal.Bitmap2String(finalBitmap));
                params.put("imagename",filename);
                System.out.println("图片名为：" + filename);
                String Result = HttpUtils.submitPostData(getResources().getString(R.string.burl) + "Person_Servlet", params, "utf-8");
                System.out.println("结果为：" + Result);
                Message message = new Message();
                message.what = 1;
                message.obj = Result;
                handler.sendMessage(message);
            }
        });
    }

    //修改昵称
    private void changeNickname(){
        //加载布局
        final LinearLayout layout_changename = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_changename,null);
        new AlertDialog.Builder(this)
                .setView(layout_changename)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        EditText newName = layout_changename.findViewById(R.id.et_layout_changename);
                        if(newName.getText().toString().trim().isEmpty())
                        {
                            Toast.makeText(PersonActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("requesttop","changeNickname");
                                    params.put("account", UserUtils.getCurrentUser().getAccount());
                                    params.put("newname", newName.getText().toString().trim());
                                    String Result = HttpUtils.submitPostData(getResources().getString(R.string.burl) + "Person_Servlet", params, "utf-8");
                                    System.out.println("结果为：" + Result);
                                    Message message = new Message();
                                    message.what = 2;
                                    message.obj = Result;
                                    handler.sendMessage(message);
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理取消按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

    //修改余额
    private void changeBalance() {
        new AlertDialog.Builder(PersonActivity.this)
                .setItems(balanceChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            switch (which) {
                                case 0://充值
                                    //加载布局
                                    final LinearLayout layout_charge = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_charge,null);
                                    new AlertDialog.Builder(PersonActivity.this)
                                            .setView(layout_charge)
                                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                                                @Override//处理确定按钮点击事件
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DecimalFormat decimalFormat =new DecimalFormat("0.00");
                                                    EditText et_layout_charge = layout_charge.findViewById(R.id.et_layout_charge);
                                                    if(et_layout_charge.getText().toString().trim().isEmpty())
                                                    {
                                                        Toast.makeText(PersonActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        final String monney =decimalFormat.format(Double.parseDouble(et_layout_charge.getText().toString().trim()));
                                                        System.out.println("看一下输入的金额"+monney);
                                                        if(monney.trim().equals("0.00"))
                                                        {
                                                            Toast.makeText(PersonActivity.this,"金额不能为0！！！",Toast.LENGTH_SHORT).show();
                                                        }else {
                                                            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Map<String, String> params = new HashMap<String, String>();
                                                                    params.put("requesttop","charge");
                                                                    params.put("account", UserUtils.getCurrentUser().getAccount());
                                                                    params.put("charge", et_layout_charge.getText().toString().trim());
                                                                    String Result = HttpUtils.submitPostData(getResources().getString(R.string.burl) + "Person_Servlet", params, "utf-8");
                                                                    System.out.println("结果为：" + Result);
                                                                    Message message = new Message();
                                                                    message.what = 3;
                                                                    message.obj = Result;
                                                                    handler.sendMessage(message);
                                                                }
                                                            });
                                                        }

                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置确定按钮
                                                @Override//处理取消按钮点击事件
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).create().show();
                                    break;
                                case 1://提现
                                    //加载布局
                                    final LinearLayout layout_tixian = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_tixian,null);
                                    new AlertDialog.Builder(PersonActivity.this)
                                            .setView(layout_tixian)
                                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                                                @Override//处理确定按钮点击事件
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DecimalFormat decimalFormat =new DecimalFormat("0.00");
                                                    EditText et_layout_tixian = layout_tixian.findViewById(R.id.et_layout_tixian);
                                                    if(et_layout_tixian.getText().toString().trim().isEmpty())
                                                    {
                                                        Toast.makeText(PersonActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        final String monney =decimalFormat.format(Double.parseDouble(et_layout_tixian.getText().toString().trim()));
                                                        System.out.println("看一下输入的金额"+monney);
                                                        if(monney.trim().equals("0.00"))
                                                        {
                                                            Toast.makeText(PersonActivity.this,"提现不能为0！",Toast.LENGTH_SHORT).show();
                                                        }else if(Double.parseDouble(et_layout_tixian.getText().toString().trim()) > UserUtils.getCurrentUser().getBalance())
                                                        {
                                                            Toast.makeText(PersonActivity.this,"提现超额！",Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Map<String, String> params = new HashMap<String, String>();
                                                                    params.put("requesttop","tixian");
                                                                    params.put("account", UserUtils.getCurrentUser().getAccount());
                                                                    params.put("tixian", et_layout_tixian.getText().toString().trim());
                                                                    String Result = HttpUtils.submitPostData(getResources().getString(R.string.burl) + "Person_Servlet", params, "utf-8");
                                                                    System.out.println("结果为：" + Result);
                                                                    Message message = new Message();
                                                                    message.what = 4;
                                                                    message.obj = Result;
                                                                    handler.sendMessage(message);
                                                                }
                                                            });
                                                        }

                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置确定按钮
                                                @Override//处理取消按钮点击事件
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).create().show();
                                    break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }).create().show();
    }

    //修改性别
    private void setSex(){
        new AlertDialog.Builder(PersonActivity.this)
                .setItems(sexChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            switch (which) {
                                case 0://男
                                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("requesttop","sex");
                                            params.put("account", UserUtils.getCurrentUser().getAccount());
                                            params.put("sex", "1");
                                            String Result = HttpUtils.submitPostData(getResources().getString(R.string.burl) + "Person_Servlet", params, "utf-8");
                                            System.out.println("结果为：" + Result);
                                            Message message = new Message();
                                            message.what = 5;
                                            message.obj = Result;
                                            handler.sendMessage(message);
                                        }
                                    });
                                    break;
                                case 1://女
                                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("requesttop","sex");
                                            params.put("account", UserUtils.getCurrentUser().getAccount());
                                            params.put("sex", "2");
                                            String Result = HttpUtils.submitPostData(getResources().getString(R.string.burl) + "Person_Servlet", params, "utf-8");
                                            System.out.println("结果为：" + Result);
                                            Message message = new Message();
                                            message.what = 5;
                                            message.obj = Result;
                                            handler.sendMessage(message);
                                        }
                                    });
                                    break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }).create().show();
    }

    //修改学校
    private void setSchool(){
        //加载布局
        final LinearLayout layout_setschool = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_setschool,null);
        new AlertDialog.Builder(this)
                .setView(layout_setschool)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et_layout_school = layout_setschool.findViewById(R.id.et_layout_school);
                        if(et_layout_school.getText().toString().trim().isEmpty())
                        {
                            Toast.makeText(PersonActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
                        } else {
                            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("requesttop","setschool");
                                    params.put("account", UserUtils.getCurrentUser().getAccount());
                                    params.put("school", et_layout_school.getText().toString().trim());
                                    String Result = HttpUtils.submitPostData(getResources().getString(R.string.burl) + "Person_Servlet", params, "utf-8");
                                    System.out.println("结果为：" + Result);
                                    Message message = new Message();
                                    message.what = 6;
                                    message.obj = Result;
                                    handler.sendMessage(message);
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理取消按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

    //修改联系方式
    private void setPhone(){
        //加载布局
        final LinearLayout layout_phone = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_phone,null);
        new AlertDialog.Builder(this)
                .setView(layout_phone)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理确定按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et_layout_phone = layout_phone.findViewById(R.id.et_layout_phone);
                        if(et_layout_phone.getText().toString().trim().isEmpty())
                        {
                            Toast.makeText(PersonActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
                        } else {
                            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("requesttop","settel");
                                    params.put("account", UserUtils.getCurrentUser().getAccount());
                                    params.put("tel", et_layout_phone.getText().toString().trim());
                                    String Result = HttpUtils.submitPostData(getResources().getString(R.string.burl) + "Person_Servlet", params, "utf-8");
                                    System.out.println("结果为：" + Result);
                                    Message message = new Message();
                                    message.what = 7;
                                    message.obj = Result;
                                    handler.sendMessage(message);
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置确定按钮
                    @Override//处理取消按钮点击事件
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }
    //活动销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
