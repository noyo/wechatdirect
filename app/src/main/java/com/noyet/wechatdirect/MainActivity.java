package com.noyet.wechatdirect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.noyet.wechatdirect.application.MyApplication;
import com.noyet.wechatdirect.reference.Reference;
import com.noyet.wechatdirect.service.MyAccessibilityService;

public class MainActivity extends Activity implements View.OnClickListener {
    private Intent mIntent;
    /*private WindowManager mWm;
    private WindowManager.LayoutParams mParams;
    private View mFloatView;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mIntent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        /*mWm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.CENTER;
        mFloatView = LayoutInflater.from(this).inflate(R.layout.fw_jump_hint, null);
        mFloatView.findViewById(R.id.fw_back).setOnClickListener(this);*/
    }

    public void test(View view) {
        if (!isAccessibilitySettingsOn(getApplicationContext())) {
            openAccessibilityService();
            return;
        }
        switch (view.getId()) {
            case R.id.btn_wc_scan:
                MyApplication.sOpenType = Reference.RANK_SCAN;
                break;
            case R.id.btn_wc_moments:
                MyApplication.sOpenType = Reference.RANK_MOMENTS;
                break;
            case R.id.btn_add_mp:
                MyApplication.sOpenType = Reference.RANK_MP_SEARCH;
                break;
        }
        MyApplication.sStartJump = true;
        startActivity(mIntent);
        /*mWm.addView(mFloatView, mParams);*/
    }

    /**
     * 开启辅助服务
     */
    private void openAccessibilityService() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("还未开启辅助服务，是否开启").setPositiveButton("开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 判断当前服务是否正在运行
     */
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.i(MainActivity.class.getCanonicalName(), "exception of isAccessibilitySettingsOn: " + e.toString());
            return false;
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fw_back:
                /*if (mFloatView != null) {
                    mWm.removeView(mFloatView);
                }*/
                break;
        }

    }
}
