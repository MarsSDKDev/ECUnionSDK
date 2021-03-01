package com.game.helper;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.pb.cdsbkncdd.R;
import com.sigmob.windad.Splash.WindSplashAD;
import com.sigmob.windad.Splash.WindSplashADListener;
import com.sigmob.windad.Splash.WindSplashAdRequest;
import com.sigmob.windad.WindAdError;
import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.demo.Constants;
import com.sigmob.windad.demo.MainActivity;

import java.util.ArrayList;


public class SplashActivity extends AppCompatActivity implements WindSplashADListener {

    private WindSplashAD mWindSplashAD;

    private ArrayList<String> logs;

    private void initSDK() {
        WindAds ads = WindAds.sharedAds();

        //enable or disable debug log

        SharedPreferences sharedPreferences = getSharedPreferences("setting", 0);
        String appId = sharedPreferences.getString(Constants.CONF_APPID, Constants.app_id);
        String appKey = sharedPreferences.getString(Constants.CONF_APPKEY, Constants.app_key);

        logs = new ArrayList<String>();
        logs.add("init SDK appId :" + appId + " appKey: " + appKey);
        ads.startWithOptions(this, new WindAdOptions(appId, appKey));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Constants.loadDefualtAdSetting(this);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ViewGroup viewGroup = findViewById(R.id.splash_container);

        initSDK();
        SharedPreferences sharedPreferences = getSharedPreferences("setting", 0);

        String placementId = sharedPreferences.getString(Constants.CONF_SPLASH_PLACEMENTID, Constants.splash_placement_id);
        String userId = sharedPreferences.getString(Constants.CONF_USERID, null);


        String appTitle = sharedPreferences.getString(Constants.CONF_APP_TITLE, null);
        String appDesc = sharedPreferences.getString(Constants.CONF_APP_DESC, null);
        boolean halfSplash = sharedPreferences.getBoolean(Constants.CONF_HALF_SPLASH, false);

        WindSplashAdRequest splashAdRequest = new WindSplashAdRequest(placementId, userId, null);

        /**
         *  广告结束，广告内容是否自动隐藏。
         *  若开屏和应用共用Activity，建议false。
         *  开屏是单独Activity ，建议true。
         */
        splashAdRequest.setDisableAutoHideAd(true);

        /**
         * 广告允许最大等待返回时间
         */
        splashAdRequest.setFetchDelay(5);

        if (halfSplash) {
            if (!TextUtils.isEmpty(appTitle)) {
                /**
                 * 设置应用LOGO标题及描述
                 */
                splashAdRequest.setAppTitle(appTitle);
                splashAdRequest.setAppDesc(appDesc);
                mWindSplashAD = new WindSplashAD(this, null, splashAdRequest, this);
            } else {
                /**
                 * 采用容器展示开屏广告内容
                 */
                mWindSplashAD = new WindSplashAD(this, viewGroup, splashAdRequest, this);
            }
        } else {
            /**
             * 全屏开屏展示
             */
            mWindSplashAD = new WindSplashAD(this, null, splashAdRequest, this);
        }
    }


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
        String[] list = logs.toArray(new String[logs.size()]);

        intent.putExtra("logs", list);
        this.startActivity(intent);
        this.finish();
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


    @Override
    public void onSplashAdSuccessPresentScreen() {

        logs.add("onSplashAdSuccessPresentScreen");

    }

    @Override
    public void onSplashAdFailToPresent(WindAdError error, String placementId) {
        logs.add("onSplashAdFailToPresent: " + error + " placementId: " + placementId);

        jumpMainActivity();

    }

    @Override
    public void onSplashAdClicked() {
        logs.add("onSplashAdClicked");

    }

    @Override
    public void onSplashClosed() {
        logs.add("onSplashClosed");

        jumpWhenCanClick();

    }
}
