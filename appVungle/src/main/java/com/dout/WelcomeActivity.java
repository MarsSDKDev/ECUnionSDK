/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.dout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;


import com.helper.union.ec.appvungle.MainActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class WelcomeActivity extends Activity {
    private static String TAG = "crazyWelcomeActivity";

    private static final int REQ_PERMISSION = 8230;
    private String[] initReqs;
    private AlertDialog dialog;
    private List<String> deniedList;
    //private WindSplashAD mWindSplashAD;

    @SuppressLint("ResourceType")
    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);

        initReqs = getAllPermission(this);

        deniedList = new ArrayList<>();
        if(initReqs == null || initReqs.length <= 0){
            //进入游戏
            finish();
            startActivity(new Intent(this,MainActivity.class));

        }else{
            reqPermission(this,initReqs);
        }


    }

//

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */

    public boolean canJumpImmediately = false;

    private void jumpWhenCanClick() {
        if (canJumpImmediately) {
            jumpMainActivity();
        } else {
            canJumpImmediately = true;
        }
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jumpMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQ_PERMISSION:
                try {
                    if(null != permissions && null != grantResults){
                        int size = permissions.length;
                        for (int i = 0 ; i < size ; i++){
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED && !deniedList.contains(permissions[i])){
                                deniedList.add(permissions[i]);
                            }
                        }
                    }
                    if(isAllRequestedPermissionGranted(this)){
                        //进入游戏
                        finish();
                        startActivity(new Intent(this,MainActivity.class));
                        //showSpAd();
                    }else{
                        showWaringDialog();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }

    }

    public boolean isAllRequestedPermissionGranted(Activity activity) {
        for (String permission : initReqs) {
            Log.d(TAG,"permission:"+permission + " state:"+ ContextCompat.checkSelfPermission(activity, permission));
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, permission)) {
                return false;
            }
        }
        return true;
    }

    private void reqPermission(Activity context,String[] reqPermission){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            deniedList.clear();
            requestPermissions(reqPermission, REQ_PERMISSION);
        }
    }

    private String[] getAllPermission(Context context){
        List<String> permissionInfoList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionInfoList.add(READ_PHONE_STATE);
            permissionInfoList.add(ACCESS_FINE_LOCATION);
            permissionInfoList.add(WRITE_EXTERNAL_STORAGE);
        }
        return permissionInfoList.toArray(new String[permissionInfoList.size()]);
    }

    private void showWaringDialog() {
        if(null == dialog){
            dialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("权限请求")
                    .setMessage("由于游戏需要一些必要的权限才能正常运行，请给予游戏权限继续呢~")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                            //finish();
                            if(null != deniedList && deniedList.size() > 0){
                                reqPermission(WelcomeActivity.this,deniedList.toArray(new String[deniedList.size()]));
                            }
                        }
                    }).create();
        }

        if(!dialog.isShowing()){
            dialog.show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        canJumpImmediately = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

}
