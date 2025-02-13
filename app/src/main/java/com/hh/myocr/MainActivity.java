package com.hh.myocr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ocr.decode.OcrDecodeFactory;
import com.tomcat.ocr.idcard.R;
import com.tomcat.ocr.idcard.databinding.ActivityMainBinding;

import java.util.List;

import com.hh.myocr.permissions.*;

public class MainActivity extends AppCompatActivity implements EasyPermission.PermissionCallback {
    private Context context;
    private ActivityMainBinding binding;
    private String broadcastAction = "com.idCard.result";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        context = this;
        try {
            context.getPackageManager().getApplicationInfo("", PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SimpleCameraActivity.class);
                intent.putExtra("saveImage", false);
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });


        //扫描之前,提前申请权限..
        requestPermission();
        //注册扫描广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broadcastAction);
        intentFilter.addCategory(getPackageName());
        resultReceiver = new ResultReceiver();
        registerReceiver(resultReceiver, intentFilter);
    }

    private ResultReceiver resultReceiver;

    private class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (broadcastAction.equals(intent.getAction())) {
                String result = intent.getStringExtra("OCRResult");
                Toast.makeText(context, "从广播中接收到扫描数据: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }


    String[] permissions = new String[]{
            Manifest.permission.CAMERA
    };

    private boolean isHavePermission = true;

    private void requestPermission() {
        if (EasyPermission.hasPermissions(context, permissions)) {
            isHavePermission = true;
        } else {
            //TODO 请求相机权限
//            EasyPermission.with(this)
//                    .rationale(getString(com.msd.ocr.idcard.R.string.rationale_camera))
//                    .addRequestCode(REQUEST_PERMISS)
//                    .permissions(permissions)
//                    .request();
        }
    }

    private final int REQUEST_CODE = 1;
    private final int REQUEST_PERMISS = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String result = data.getStringExtra("OCRResult");
            try {
//                JSONObject jo = new JSONObject(result);
//                StringBuffer sb = new StringBuffer();
//                sb.append(String.format("正面 = %s\n", jo.opt("type")));
//                sb.append(String.format("姓名 = %s\n", jo.opt("name")));
//                sb.append(String.format("性别 = %s\n", jo.opt("sex")));
//                sb.append(String.format("民族 = %s\n", jo.opt("folk")));
//                sb.append(String.format("日期 = %s\n", jo.opt("birt")));
//                sb.append(String.format("号码 = %s\n", jo.opt("num")));
//                sb.append(String.format("住址 = %s\n", jo.opt("addr")));
//                sb.append(String.format("签发机关 = %s\n", jo.opt("issue")));
//                sb.append(String.format("有效期限 = %s\n", jo.opt("valid")));
//                sb.append(String.format("整体照片 = %s\n", jo.opt("imgPath")));
//                sb.append(String.format("头像路径 = %s\n", jo.opt("headPath")));
//                sb.append("\n驾照专属字段\n");
//                sb.append(String.format("国家 = %s\n", jo.opt("nation")));
//                sb.append(String.format("初始领证 = %s\n", jo.opt("startTime")));
//                sb.append(String.format("准驾车型 = %s\n", jo.opt("drivingType")));
//                sb.append(String.format("有效期限 = %s\n", jo.opt("registerDate")));
                binding.textview.setText(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        isHavePermission = true;
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        Toast.makeText(context, "没有相机权限", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(resultReceiver); //记得要注销
    }
}
