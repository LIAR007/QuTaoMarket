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

    //?????????????????????
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

    //?????????????????????????????????
    private String title,type;
    //??????id
    private int gid;

    //????????????????????????
    private PublishgoodsActivity.OnClickListener listener;

    //???????????????
    private ProgressDialog pd;

    //???????????????alertdialog?????????
    private CharSequence[] pictureChoice = {"??????","???????????????"};
    private CharSequence[] dealChoice = {"??????","??????"};

    //???????????????????????????
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int SELECT_PIC = 0;
    private Uri imageUri; //????????????
    private Uri cropImageUri; //????????????????????????
    private String filename; //????????????

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
        //???????????????
        initData();
    }

    private void initView() {
        //??????????????????
        pd = new ProgressDialog(PublishgoodsActivity.this);
        pd.setMessage("?????????...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        //????????????
        type_list = new ArrayList<String>();
        type_list.add("?????????...");
        type_list.add("????????????");
        type_list.add("????????????");
        type_list.add("????????????");
        type_list.add("????????????");
        type_list.add("????????????");
        type_list.add("????????????");
        type_list.add("????????????");
        type_list.add("??????");
        type_list.add("????????????");
        type_list.add("????????????");
        type_list.add("????????????");
        type_list.add("??????");
        type_list.add("????????????");

        //?????????
        hownew_list = new ArrayList<String>();
        hownew_list.add("?????????");
        hownew_list.add("?????????");
        hownew_list.add("?????????");
        hownew_list.add("?????????");
        hownew_list.add("?????????");
        hownew_list.add("?????????");
        hownew_list.add("?????????");
        hownew_list.add("?????????");
        hownew_list.add("?????????");
        hownew_list.add("????????????");
        //?????????
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

        //????????????
        tv_pub_title.setText(title);

        //?????????????????????
        listener = new OnClickListener();
        rl_pub_address.setOnClickListener(listener);
        iv_pubgoods_back.setOnClickListener(listener);
        rl_pub_price.setOnClickListener(listener);
        rl_pub_oprice.setOnClickListener(listener);
        rl_pub_style.setOnClickListener(listener);
        iv_pubgoods_goods.setOnClickListener(listener);
        iv_pub_publish.setOnClickListener(listener);

        //?????????
        type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_list);
        hownew_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hownew_list);
        //????????????
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hownew_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //???????????????
        sp_pubgoods_type.setAdapter(type_adapter);
        sp_pubgoods_hownew.setAdapter(hownew_adapter);
    }

    //???????????????
    private void initData(){
        //???????????????
        if("publishfree".equals(type)){
            rl_pub_price.setClickable(false);
            rl_pub_oprice.setClickable(false);
            tv_pub_price.setText("0.00");
            tv_pub_oprice.setText("0.00");
//            cb_pub_ji.setClickable(false);
            tv_pub_address.setText(MainActivity.myAddress);
        }else if("editgoods".equals(type)){       //????????????
            //?????????????????????????????????
            iv_pubgoods_goods.setClickable(false);
            filename = goodsdata.get("gimage").toString();
            //??????????????????
            RequestOptions options = new RequestOptions();
            options.fitCenter()
                    .placeholder(R.drawable.ic_moren_goods)
                    .error(R.drawable.ic_moren_goods)
                    .fallback(R.drawable.ic_moren_goods);
            Glide.with(this)
                    .applyDefaultRequestOptions(options)
                    .load(goodsdata.get("gimage").toString())
                    .into(iv_pubgoods_goods);
            //??????????????????
            et_pub_name.setText(goodsdata.get("gname").toString());
            //????????????
            et_pub_detail.setText(goodsdata.get("gdetail").toString());
            //????????????
            tv_pub_price.setText(goodsdata.get("gprice").toString());
            //????????????
            tv_pub_oprice.setText(goodsdata.get("goprice").toString());
            //??????????????????
            switch (goodsdata.get("gtype").toString()){
                case "????????????":
                    sp_pubgoods_type.setSelection(1);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(2);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(3);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(4);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(5);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(6);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(7);
                    break;
                case "??????":
                    sp_pubgoods_type.setSelection(8);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(9);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(10);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(11);
                    break;
                case "??????":
                    sp_pubgoods_type.setSelection(12);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(13);
                    break;
                default:
                    break;
            }
            //??????????????????
            switch (goodsdata.get("ghownew").toString()){
                case "?????????":
                    sp_pubgoods_hownew.setSelection(0);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(1);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(2);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(3);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(4);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(5);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(6);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(7);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(8);
                    break;
                case "????????????":
                    sp_pubgoods_hownew.setSelection(9);
                    break;
                    default:
                        break;
            }
            //??????????????????
            tv_pub_style.setText(goodsdata.get("ggetway").toString());
//            //??????????????????
//            if(!"1".equals(goodsdata.get("gemergent").toString())){
//                cb_pub_ji.setChecked(true);
//            }else cb_pub_ji.setChecked(false);
            //??????????????????
            tv_pub_address.setText(goodsdata.get("gaddress").toString());
        }else if("editfree".equals(type)){       //?????????????????????
            //?????????????????????????????????
            iv_pubgoods_goods.setClickable(false);
            filename = goodsdata.get("gimage").toString();
            //??????????????????
            RequestOptions options = new RequestOptions();
            options.fitCenter()
                    .placeholder(R.drawable.ic_moren_goods)
                    .error(R.drawable.ic_moren_goods)
                    .fallback(R.drawable.ic_moren_goods);
            Glide.with(this)
                    .applyDefaultRequestOptions(options)
                    .load(goodsdata.get("gimage").toString())
                    .into(iv_pubgoods_goods);
            //??????????????????
            et_pub_name.setText(goodsdata.get("gname").toString());
            //????????????
            et_pub_detail.setText(goodsdata.get("gdetail").toString());
            //?????????????????????????????????
            rl_pub_price.setClickable(false);
            rl_pub_oprice.setClickable(false);
            tv_pub_price.setText("0.00");
            tv_pub_oprice.setText("0.00");
//            cb_pub_ji.setClickable(false);
            //??????????????????
            switch (goodsdata.get("gtype").toString()){
                case "????????????":
                    sp_pubgoods_type.setSelection(1);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(2);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(3);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(4);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(5);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(6);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(7);
                    break;
                case "??????":
                    sp_pubgoods_type.setSelection(8);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(9);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(10);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(11);
                    break;
                case "??????":
                    sp_pubgoods_type.setSelection(12);
                    break;
                case "????????????":
                    sp_pubgoods_type.setSelection(13);
                    break;
                default:
                    break;
            }
            //??????????????????
            switch (goodsdata.get("ghownew").toString()){
                case "?????????":
                    sp_pubgoods_hownew.setSelection(0);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(1);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(2);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(3);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(4);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(5);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(6);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(7);
                    break;
                case "?????????":
                    sp_pubgoods_hownew.setSelection(8);
                    break;
                case "????????????":
                    sp_pubgoods_hownew.setSelection(9);
                    break;
                default:
                    break;
            }
            //??????????????????
            tv_pub_style.setText(goodsdata.get("ggetway").toString());
            //??????????????????
            tv_pub_address.setText(goodsdata.get("gaddress").toString());
        }else {
            tv_pub_address.setText(MainActivity.myAddress);
        };
    }

    //???????????????????????????
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

    //handler????????????
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
                                Toast.makeText(PublishgoodsActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(PublishgoodsActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(PublishgoodsActivity.this,"???????????????",Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PublishgoodsActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                pd.cancel();
                                Toast.makeText(PublishgoodsActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(PublishgoodsActivity.this,"???????????????",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //??????????????????
    private void publish() {
        Map<String, String> params = new HashMap<>();
        //????????????
        final Date d = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //????????????
        if(filename == null) {
            Toast.makeText(PublishgoodsActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
        }else if("".equals(et_pub_name.getText().toString().trim())){
            Toast.makeText(PublishgoodsActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
        } else if("".equals(et_pub_detail.getText().toString().trim())){
            Toast.makeText(PublishgoodsActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
        }else if("goods".equals(type)&&(Double.valueOf(tv_pub_price.getText().toString().trim()) == 0)){
            Toast.makeText(PublishgoodsActivity.this, "???????????????0???", Toast.LENGTH_SHORT).show();
        }else if("?????????...".equals(sp_pubgoods_type.getSelectedItem().toString().trim())){
            Toast.makeText(PublishgoodsActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
        }else if("null".equals(UserUtils.getCurrentUser().getAddress().trim())){
            Toast.makeText(PublishgoodsActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
        }else{
            if("publishgoods".equals(type) || "publishfree".equals(type)){
                //????????????
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(PublishgoodsActivity.this);
                normalDialog.setTitle("??????");
                normalDialog.setMessage("????????????????????????????????????????????????");
                normalDialog.setPositiveButton("??????",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.show();
                        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                //????????????
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
                                System.out.println("????????????" + Result);
                                Message message = new Message();
                                message.what = 1;
                                message.obj = Result;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });
                normalDialog.setNegativeButton("??????",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // ??????
                normalDialog.show();
            }else {
                //????????????????????????
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(PublishgoodsActivity.this);
                normalDialog.setTitle("??????");
                normalDialog.setMessage("??????????????????");
                normalDialog.setPositiveButton("??????",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.show();
                        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                //????????????
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
                                //????????????
                                String strUrlpath = getResources().getString(R.string.burl) + "Publish_Servlet";
                                String Result = HttpUtils.submitPostData(strUrlpath, params, "utf-8");
                                System.out.println("????????????" + Result);
                                Message message = new Message();
                                message.what = 2;
                                message.obj = Result;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });
                normalDialog.setNegativeButton("??????",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // ??????
                normalDialog.show();
            }

        }
    }

    //?????????activity?????????????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case SELECT_PIC://??????
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
                //?????????????????????????????????Uri
                cropImageUri = ImageDeal.startUCrop(PublishgoodsActivity.this, imageUri, CROP_PHOTO, 800, 1200);
                break;
            case TAKE_PHOTO://??????
                try {
                    //??????????????????
                    Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intentBc.setData(imageUri);
                    this.sendBroadcast(intentBc);
                    //?????????????????????????????????Uri
                    cropImageUri = ImageDeal.startUCrop(PublishgoodsActivity.this, imageUri, CROP_PHOTO, 800, 1200);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CROP_PHOTO:
                //???????????????Bitmap??????
                try {
                    goodsImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //??????????????????
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

    //??????????????????
    private void addImage(){
        //?????????????????????
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        new AlertDialog.Builder(PublishgoodsActivity.this)
                .setTitle("????????????")
                .setItems(pictureChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            switch (which)
                            {
                                case 0://??????
                                    //???????????? ????????????
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
                                    //???File???????????????Uri?????????????????????
                                    if (Build.VERSION.SDK_INT >= 24) {
                                        try{
                                            imageUri = FileUtils.getUriForFile(PublishgoodsActivity.this, outputImage);
                                        }catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        imageUri = Uri.fromFile(outputImage);
                                    }
                                    Intent tTntent = new Intent("android.media.action.IMAGE_CAPTURE"); //??????
                                    tTntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //????????????????????????
                                    startActivityForResult(tTntent,TAKE_PHOTO); //????????????
                                    break;
                                case 1://???????????????
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

    //????????????
    private void goodsPrice(){
        //????????????
        final LinearLayout layout_goodsprice = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_goodsprice,null);
        new AlertDialog.Builder(this)
                .setView(layout_goodsprice)
                .setPositiveButton("??????",new DialogInterface.OnClickListener() {//??????????????????
                    @SuppressLint("ResourceAsColor")
                    @Override//??????????????????????????????
                    public void onClick(DialogInterface dialog, int which) {
                        EditText goodsprice = layout_goodsprice.findViewById(R.id.et_layout_goodsprice);
                        if(goodsprice.getText().toString().trim().isEmpty())
                        {
                            Toast.makeText(PublishgoodsActivity.this,"?????????????????????",Toast.LENGTH_SHORT).show();
                        }else if(Double.valueOf(goodsprice.getText().toString().trim()) <= 0){
                            Toast.makeText(PublishgoodsActivity.this,"?????????????????????",Toast.LENGTH_SHORT).show();
                        }else
                        {
                            //?????????????????????
                            tv_pub_price.setText(goodsprice.getText().toString());
                        }
                    }
                })
                .setNegativeButton("??????",new DialogInterface.OnClickListener() {//??????????????????
                    @Override//??????????????????????????????
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    //????????????
    private void goodsOprice(){
        //????????????
        final LinearLayout layout_goodsoprice = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_goodsoprice,null);
        new AlertDialog.Builder(this)
                .setView(layout_goodsoprice)
                .setPositiveButton("??????",new DialogInterface.OnClickListener() {//??????????????????
                    @SuppressLint("ResourceAsColor")
                    @Override//??????????????????????????????
                    public void onClick(DialogInterface dialog, int which) {
                        EditText goodsoprice = layout_goodsoprice.findViewById(R.id.et_layout_goodsoprice);
                        if(Double.valueOf(goodsoprice.getText().toString().trim()) < 0){
                            Toast.makeText(PublishgoodsActivity.this,"?????????????????????",Toast.LENGTH_SHORT).show();
                        }else
                        {
                            //?????????????????????
                            tv_pub_oprice.setText(goodsoprice.getText().toString());
                        }
                    }
                })
                .setNegativeButton("??????",new DialogInterface.OnClickListener() {//??????????????????
                    @Override//??????????????????????????????
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    //????????????
    private void goodsDeal(){
        new AlertDialog.Builder(PublishgoodsActivity.this)
                .setItems(dealChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                tv_pub_style.setText("??????");
                                break;
                            case 1:
                                tv_pub_style.setText("??????");
                                break;
                                default:
                                    break;
                        }
                    }
                }).create().show();
    }
}
