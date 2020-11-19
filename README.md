#ECUnionSDK接入文档
----
目录
-----
[TOC]


## 接入方只需要接好接口，SDK要我方二次打包后才会生效。
### 1.说明
```
1.当你检查确认接入无误后（确认接口有调用到），将接好包体传给我方人员。
2.接入方不需要考虑应用权限适配，SDK内部逻辑已经适配处理
3.一个广告位id对应一个广告实例。不可以一个广告实例使用多个广告位id。
4.类文件所在路径不要使用包名作为路径，因为在二次打包时修改包名会出问题。比如包名为com.aaa.bbb、类名为MainActivtiy.java。类路径就不能为com.aaa.bbb.MainActivtiy.java。如果出现该情况，请重新命名路径。
```

### 2.搭建开发环境

1)拷贝libs目录的SDK文件到您项目的libs目录下。

2)配置AndroidManifest.xml

2.1)添加权限
```java
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
```

3)请务必确保工程的模块级build.gradle文件里的包含以下依赖：
```xml
dependencies {
    //引入sdk依赖
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    implementation 'com.android.support:design:28.0.0'
	implementation 'com.android.support:recyclerview-v7:28.0.0'
    implementation 'pl.droidsonroids.gif:android-gif-drawable:1.2.18'
}
```

4)请在AndroidManifest在application的标签中给游戏加上硬件加速：如
```xml
<application
        android:hardwareAccelerated="true">
        ...
</application>
```
5)minSdkVersion最小值需要19以上。



###3.1 SDK的Application初始化（必接）
ECUnionSDK类:

| 方法名 | 说明 |
| ---- | ---- |
| public static void attachBaseContext(Application application) | SDK初始化，在Application子类的attachBaseContext方法中调用,传入application|
| public static void onApplicationCreate(Application application) |  SDK初始化，在Application子类的onApplicationCreate方法中调用,传入application |

```
自定义您的Application，调用示例如下:
注意：
接口调用的位置不能错。
要在Androidmanifest里的application标签的android:name属性设置该类，否则该类不被使用。
```

```java

import android.app.Application;
import android.content.Context;

import com.ec.union.ecu.pub.ECUnionSDK;

//注意：要在Androidmanifest里的application标签的android:name属性设置该类，否则该类不被使用。
public class GameApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //1.application初始化
        ECUnionSDK.attachBaseContext(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //2.application初始化
        ECUnionSDK.onApplicationCreate(this);
    }

}
```




###3.2 SDK主activity初始化（必接）

ECUnionSDK类:

| 方法名 | 说明 |
| ---- | ---- |
| public static void onMainActivityCreate(Activity activity) | 主activity初始化接口 |
| public static void onResume(Activity activity)|当activity在界面上展示时，广告处理相应逻辑|
| public static void onPause(Activity activity) |当activity失去焦点暂停时，广告处理对应逻辑|
| public static void onStop(Activity activity)|当activity停止时，广告进行处理相应逻辑|
| public static void onDestroy(Activity activity)|当activity被销毁时，并回收相应资源|
| public static void onStart(Activity activity)|当activity启动时，广告进行处理相应逻辑|
| public static void onRestart(Activity activity)|当activity重新启动时，广告进行处理相应逻辑|
| public static void onNewIntent(Activity activity)|当activity接受新的intent，广告进行处理相应逻辑|
| public static void void onActivityResult(Activity activity,int requestCode, int resultCode, Intent data)|进入到权限界面申请权限后返回activity，权限结果回调
| public static void onRequestPermissionsResult(Activity activity,int requestCode, String[] permissions,  int[] grantResults)|申请权限结果回调

```
请在应用（游戏）主activity对应的方法里调用SDK接口。
```


如下所示：

```java

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //3.主activity初始化
        ECUnionSDK.onMainActivityCreate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ECUnionSDK.onStart(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ECUnionSDK.onRestart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ECUnionSDK.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ECUnionSDK.onPause(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        ECUnionSDK.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ECUnionSDK.onDestroy(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ECUnionSDK.onNewIntent(this, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ECUnionSDK.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ECUnionSDK.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}

```





###3.3 开屏广告

ECSplashMgr类:

| 方法名 | 说明 |
| ---- | ---- |
| public void createSplash(IECAdListener listener) |创建开屏,传入广告侦听器 |
| public void showSplash(String posId) |  开屏创建完成后，展示开屏，传入广告位id |
| public void onCreate(Activity activity, Bundle savedInstanceState)|此activity添加闪屏作为入口activity,初始化广告相关参数|
| public void onResume()|当activity在界面上展示时，广告处理相应逻辑|
| public void onPause() |当activity失去焦点暂停时，广告处理对应逻辑|
| public void onStop()|当activity停止时，广告进行处理相应逻辑|
| public void onDestroy()|当activity被销毁时，并回收相应资源|
| public void onStart()|当activity开始时，广告进行处理相应逻辑|
| public void onRestart()|当activity重新开始时，广告进行处理相应逻辑|
| public void onNewIntent()|当activity接受新的intent，广告进行处理相应逻辑|
| public void onSaveInstanceState()|保存activity状态，广告进行处理相应逻辑|
| public void onConfigurationChanged()|当Configuration发生变化，广告进行处理相应逻辑|
| public void void onActivityResult(int requestCode, int resultCode, Intent data)|进入到权限界面申请权限后返回activity，权限结果回调
| public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)|申请权限结果回调
| public boolean onKeyDown(int keyCode, KeyEvent event)|闪屏，屏蔽返回键
| public void setSplashEntryClsNm(String className) |  在展示之前调用，传入要跳转的activity的全类名 |

```xml
注意：
1.闪屏activity必须创建一个单独的activity承托,不能跟应用主activity混在一起，闪屏结束后会自动跳转，接入方不要做跳转操作。
2.闪屏activity在androidmanifest里要指定好方向（横屏或竖屏）。
3.HbSplashMgr.getInstance().setSplashEntryClsNm(); //设置开屏广告结束后要跳转的activity全类名，务必在调用开屏展示接口之前调用。
4.创建的开屏activity要在Androidmanifest里的application标签里注册，否则会出错。
        <activity
            android:name="com.xxx.xxx.GameSplashActivity"
            android:configChanges="keyboard|keyboardHidden|navigation|orientation|screenSize|screenLayout"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen">

            <!--这个intent-filter表示该activity是启动界面，在Androidmanifest里请保持唯一，不能有多个-->
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
```


####3.3.1开屏接入方式一：自定义Activity继承ECSplashActivity来展示闪屏。推荐此方式接入闪屏！
示例如下:
```java
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ec.union.ad.sdk.api.ECSplashActivity;
import com.ec.union.ad.sdk.api.ECSplashMgr;
import com.ec.union.ad.sdk.platform.ECAdError;
import com.ec.union.ad.sdk.platform.IECAdListener;

public class GameSplashActivity extends ECSplashActivity{
    private static final String TAG = GameSplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //展示开屏广告之前,先创建
        ECSplashMgr.getInstance().createSplash(new IECAdListener() {
            @Override
            public void onAdShow() {
                Log.i(TAG, "splash onAdShow");
            }

            @Override
            public void onAdFailed(ECAdError error) {
                Log.i(TAG, "splash onAdFailed: " + error.toString());
            }

            @Override
            public void onAdReady() {
                Log.i(TAG, "splash onAdReady");
            }

            @Override
            public void onAdClick() {
                Log.i(TAG, "splash onAdClick");
            }

            @Override
            public void onAdDismissed() {
                Log.i(TAG, "splash onAdDismissed");
            }

            @Override
            public void onAdReward() {
                Log.i(TAG, "splash onAdReward");
            }
        });

        //设置开屏广告结束后要跳转的activity全类名
        //GameMainActivity.class.getName() MainActivity代指主界面activity。请修改为真正的主activity。
        ECSplashMgr.getInstance().setSplashEntryClsNm(GameMainActivity.class.getName());


        //展示开屏广告
        showSplash();

    }
    private void showSplash() {
        //广告位id,展示广告
        //请填写真正的广告位id
        //AdConstant是用来管理广告位id的。该类可以在demo里找到来使用。
        ECSplashMgr.getInstance().showSplash(AdConstant.SPLASH_ID);
    }
}
```

####3.3.2开屏接入方式二：原生activity中调用闪屏接口展示闪屏

示例如下:
```java
public  class GameSplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		//activity初始化
        ECSplashMgr.getInstance().onCreate(this, savedInstanceState);


        //展示开屏广告之前,先创建
        ECSplashMgr.getInstance().createSplash(new IECAdListener() {
            @Override
            public void onAdShow() {
                Log.i(TAG, "splash onAdShow");
            }

            @Override
            public void onAdFailed(ECAdError error) {
                Log.i(TAG, "splash onAdFailed: " + error.toString());
            }

            @Override
            public void onAdReady() {
                Log.i(TAG, "splash onAdReady");
            }

            @Override
            public void onAdClick() {
                Log.i(TAG, "splash onAdClick");
            }

            @Override
            public void onAdDismissed() {
                Log.i(TAG, "splash onAdDismissed");
            }

            @Override
            public void onAdReward() {
                Log.i(TAG, "splash onAdReward");
            }
        });

        //设置开屏广告结束后要跳转的activity全类名
        //GameMainActivity.class.getName() MainActivity代指主界面activity。请修改为真正的主activity。
        ECSplashMgr.getInstance().setSplashEntryClsNm(GameMainActivity.class.getName());


        //展示开屏广告
        showSplash();

    }

    private void showSplash() {
        //广告位id,展示广告
        //请填写真正的广告位id
        ECSplashMgr.getInstance().showSplash(AdConstant.SPLASH_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ECSplashMgr.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ECSplashMgr.getInstance().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ECSplashMgr.getInstance().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ECSplashMgr.getInstance().onDestroy();
    }

   @Override
    public void onStart() {
       ECSplashMgr.getInstance().onStart();
    }

    @Override
    public void onRestart() {
       ECSplashMgr.getInstance().onRestart();
    }

    @Override
    public void onNewIntent(Intent intent) {
       ECSplashMgr.getInstance().onNewIntent(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
       ECSplashMgr.getInstance().onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
       ECSplashMgr.getInstance().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ECSplashMgr.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ECSplashMgr.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean var = ECSplashMgr.getInstance().onKeyDown(keyCode, event);
        if (!var) {
            return super.onKeyDown(keyCode, event);
        }
        return var;
    }
}
```


###3.4 广告接口

 ECAd类

| 方法名 | 说明 |
| ---- | ---- |
| public ECAd(Activity activity, IECAdListener listener, ECAdType adType) |广告加载类构造函数,传入activity，广告侦听器，广告类型 |
| public void showAd(String posId) | 传入广告位id， 展示对应广告类型 |
| public void loadAd(String posId) |  传入广告位id，加载视频广告。用于视频类型（FUllVIDEO，REWARDVIDEO）广告的预加载|
| public void loadAndShowAd(String posId,Map<String,String> param) | 此接口只用于激励视频类型，用于服务端发奖励， 传入广告位id，服务端的发奖励参数(在3.6节说明Map参数)|
| public boolean isReady() |  判断视频广告是否加载完成。|
| public void setVisibility(boolean) | 设置广告是否可见，默认为true，true为显示。false为隐藏 。用于feed，infeed和banner类型的广告 |
| public void onResume()|当activity在界面上展示时，广告处理相应逻辑|
| public void onPause() |当activity失去焦点暂停时，广告处理对应逻辑|
| public void onStop()|当activity停止时，广告进行处理相应逻辑|
| public void onDestroy()|当activity被销毁时，清理广告，并回收广告相应资源|
| public void onStart()|当activity开始时，广告进行处理相应逻辑|
| public void onRestart()|当activity重新开始时，广告进行处理相应逻辑|
| public void onNewIntent()|当activity接受新的intent，广告进行处理相应逻辑|
| public void onSaveInstanceState()|保存activity状态，广告进行处理相应逻辑|
| public void onConfigurationChanged()|当Configuration发生变化，广告进行处理相应逻辑

####3.4.1 广告类型说明
| 广告类型 | 说明 |
| ---- | ---- |
| ECAdType.BANNER | 横幅  |
| ECAdType.INTERSTITIAL | 插屏 |
| ECAdType.INFEED | 贴片 |
| ECAdType.FEED | 信息流 |
| ECAdType.REWARDVIDEO | 激励视频 |
| ECAdType.FULLVIDEO | 全屏视频 |

###3.5 广告回调接口说明
```java
    public interface IECAdListener {
        void onAdShow(); //广告展示时回调
        void onAdFailed(ECAdError error); //失败回调
        void onAdReady();//广告加载准备好回调
        void onAdClick();//点击广告回调
        void onAdDismissed();//关闭广告回调
        void onAdReward();//广告奖励回调，用于视频广告在观看完广告后回调接入方发起奖励给用户
    }

```


### 3.6 激励视频服务器发奖励接口说明（使用loadAndShowAd接口才接入）
```java
		Map<String,String> gameParams = new HashMap<String,String>();
        gameParams.put(GameParam.REWARD_NAME.getValue(),"五十金币");//奖励名称
        gameParams.put(GameParam.REWARD_AMOUNT.getValue(),"2");//奖励数量
        gameParams.put(GameParam.USER_ID.getValue(),"my_useid");//用户id
        gameParams.put(GameParam.EXTRA.getValue(),"{\"reward_id\":\"8234899234823948394723\"}");//游戏扩展参数，使用json格式传输，包含与服务器约定的参数，请自定义。
```


```
注意:
1.示例中的生命周期回调函数请务必调用
2.广告接口请在主activity里使用。
```

广告接口示例如下：
```java


public class GameMainActivity extends Activity {

    public class AdConstant {
        //请填写真正的广告位id
        //开屏广告位
        public static final String SPLASH_ID = "";
        //插屏广告位
        public static final String INTERSTITIAL_ID = "";
        //banner广告位
        public static final String BANNER_ID = "";
        //激励视频广告位
        public static final String REWARDVIDEO_ID = "";
        //全屏视频广告位
        public static final String FULLVIDEO_ID = "";
        //信息流广告位
        public static final String FEED_ID = "";
        //贴片广告位
        public static final String INFEED_ID = "";
    }


    private static final String TAG = GameMainActivity.class.getSimpleName();
    public static GameMainActivity mGameMainActivity;

    //banner（横幅）广告
    private ECAd bannerAd;
    private boolean mBannerIsShow;

    //插屏广告
    private ECAd interstitialAd;

    //激励视频广告
    private ECAd rewardVideoAd;
    private static long mRewardVideoTime = 0;//记录激励视频当前时间

    //全屏视频广告
    private ECAd fullVideoAd;
    private static long mFullVideoTime = 0;//记录全屏视频当前时间

    //信息流广告
    private ECAd feedAd;
    private boolean mFeedIsShow;

    //贴片广告
    private ECAd infeedAd;
    private boolean mInfeedIsShow;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameMainActivity = this;

        //初始化广告实例
        initAd();

    }

    //创建全部广告类型的广告实例
    public void initAd() {
        //banner广告
        if (bannerAd == null) {
            bannerAd = new ECAd(this, new IECAdListener() {
                @Override
                public void onAdShow() {
                    Log.i(TAG, "banner onAdShow");
                }

                @Override
                public void onAdFailed(ECAdError error) {
                    Log.i(TAG, "banner onAdFailed: " + error.toString());
                }

                @Override
                public void onAdReady() {
                    Log.i(TAG, "banner onAdReady");
                    //调用show接口广告加载完成后，根据mBannerIsShow，设置显示或隐藏
                    setBannerShow(mBannerIsShow);
                }

                @Override
                public void onAdClick() {
                    Log.i(TAG, "banner onAdClick");
                }

                @Override
                public void onAdDismissed() {
                    Log.i(TAG, "banner onAdDismissed");
                }

                @Override
                public void onAdReward() {
                    Log.i(TAG, "banner onAdReward");
                }
            }, ECAdType.BANNER);
        }

        //插屏广告
        if (interstitialAd == null) {
            interstitialAd = new ECAd(this, new IECAdListener() {
                @Override
                public void onAdShow() {
                    Log.i(TAG, "interstitial onAdShow");
                }

                @Override
                public void onAdFailed(ECAdError error) {
                    Log.i(TAG, "interstitial onAdFailed: " + error.toString());
                }

                @Override
                public void onAdReady() {
                    Log.i(TAG, "interstitial onAdReady");
                }

                @Override
                public void onAdClick() {
                    Log.i(TAG, "interstitial onAdClick");
                }

                @Override
                public void onAdDismissed() {
                    Log.i(TAG, "interstitial onAdDismissed");
                }

                @Override
                public void onAdReward() {
                    Log.i(TAG, "interstitial onAdReward");
                }
            }, ECAdType.INTERSTITIAL);
        }


        //激励视频广告
        if (rewardVideoAd == null) {
            rewardVideoAd = new ECAd(this, new IECAdListener() {
                @Override
                public void onAdShow() {
                    Log.i(TAG, "rewardVideo onAdShow");
                }

                @Override
                public void onAdFailed(ECAdError error) { //不能在失败回调里调用loadAd，不然很可能会造成死循环。
                    Log.i(TAG, "rewardVideo onAdFailed: " + error.toString());
                }

                @Override
                public void onAdReady() { //视频已经准备好了
                    Log.i(TAG, "rewardVideo onAdReady");
                }

                @Override
                public void onAdClick() {
                    Log.i(TAG, "rewardVideo onAdClick");
                }

                @Override
                public void onAdDismissed() {
                    Log.i(TAG, "rewardVideo onAdDismissed");

                    //关闭广告后重新加载广告
                    if (null != rewardVideoAd) {
                        rewardVideoAd.loadAd(AdConstant.REWARDVIDEO_ID);
                    }

                }

                @Override
                public void onAdReward() {
                    //奖励视频这里发奖励，不要做任何判断，直接在这回调发奖励
                    Log.i(TAG, "rewardVideo onAdReward");
                }
            }, ECAdType.REWARDVIDEO);
        }

        //全屏视频广告
        if (fullVideoAd == null) {
            fullVideoAd = new ECAd(this, new IECAdListener() {
                @Override
                public void onAdShow() {
                    Log.i(TAG, "fullVideo onAdShow");
                }

                @Override
                public void onAdFailed(ECAdError error) { //不能在失败回调里调用loadAd，不然很可能会造成死循环。
                    Log.i(TAG, "fullVideo onAdFailed: " + error.toString());
                }

                @Override
                public void onAdReady() { //视频已经准备好了
                    Log.i(TAG, "fullVideo onAdReady");
                }

                @Override
                public void onAdClick() {
                    Log.i(TAG, "fullVideo onAdClick");
                }

                @Override
                public void onAdDismissed() {
                    Log.i(TAG, "fullVideo onAdDismissed");

                    //关闭广告后重新加载广告
                    if (null != fullVideoAd) {
                        fullVideoAd.loadAd(AdConstant.FULLVIDEO_ID);
                    }

                }

                @Override
                public void onAdReward() {
                    //奖励视频这里发奖励，不要做任何判断，直接在这回调发奖励
                    Log.i(TAG, "rewardVideo onAdReward");
                }
            }, ECAdType.FULLVIDEO);
        }


        //信息流广告
        if (feedAd == null) {
            feedAd = new ECAd(this, new IECAdListener() {
                @Override
                public void onAdShow() {
                    Log.i(TAG, "feed onAdShow");
                }

                @Override
                public void onAdFailed(ECAdError error) {
                    Log.i(TAG, "feed onAdFailed: " + error.toString());

                }

                @Override
                public void onAdReady() {
                    Log.i(TAG, "feed onAdReady");

                    //调用show接口广告加载完成后，根据mFeedIsShow，设置显示或隐藏
                    setFeedShow(mFeedIsShow);
                }

                @Override
                public void onAdClick() {
                    Log.i(TAG, "feed onAdClick");
                }

                @Override
                public void onAdDismissed() {
                    Log.i(TAG, "feed onAdDismissed");
                }

                @Override
                public void onAdReward() {
                    Log.i(TAG, "feed onAdReward");
                }
            }, ECAdType.FEED);
        }


        //贴片广告
        if (infeedAd == null) {
            infeedAd = new ECAd(this, new IECAdListener() {
                @Override
                public void onAdShow() {
                    Log.i(TAG, "infeed onAdShow");
                }

                @Override
                public void onAdFailed(ECAdError error) {
                    Log.i(TAG, "infeed onAdFailed: " + error.toString());
                }

                @Override
                public void onAdReady() {
                    Log.i(TAG, "infeed onAdReady");
                    //调用show接口广告加载完成后，根据mInfeedIsShow，设置显示或隐藏
                    setInfeedShow(mInfeedIsShow);
                }

                @Override
                public void onAdClick() {
                    Log.i(TAG, "infeed onAdClick");
                }

                @Override
                public void onAdDismissed() {
                    Log.i(TAG, "infeed onAdDismissed");
                }

                @Override
                public void onAdReward() {
                    Log.i(TAG, "infeed onAdReward");
                }
            }, ECAdType.INFEED);
        }

    }


    //加载展示信息流广告接口 ，每次showAd调用都会重新加载广告
    public static void showFeedAd() {
        Log.i(TAG, "展示feed广告---->");
        if (null != mGameMainActivity.feedAd) {
            mGameMainActivity.feedAd.showAd(AdConstant.FEED_ID);
        }
    }

    //设置是否显示信息流广告接口
    public static void setFeedShow(boolean isShow) {
        mGameMainActivity.mFeedIsShow = isShow;
        if (null != mGameMainActivity.feedAd) {
            Log.i(TAG, "设置feed广告Visibility：" + isShow);
            mGameMainActivity.feedAd.setVisibility(isShow);//控制feed隐藏显示。
        }
    }


    //加载全屏视频接口，游戏加载完成后只用调用一次
    public static void loadFullVideoAd() {
        //建议接入方在恰当的时机，在游戏层控制调用加载，比如：游戏开始时，进入有视频广告的界面等，按接入方实际情况调用。
        if (null != mGameMainActivity.fullVideoAd) {
            Log.i(TAG, "加载全屏视频广告---->");
            mGameMainActivity.fullVideoAd.loadAd(AdConstant.FULLVIDEO_ID);
        }
    }

    //展示全屏视频广告接口
    public static void showFullVideoAd() {
        if (null != mGameMainActivity.fullVideoAd) {
            if (mGameMainActivity.fullVideoAd.isReady()) { //判断是否加载完毕
                Log.i(TAG, "展示全屏视频广告...");
                mGameMainActivity.fullVideoAd.showAd(AdConstant.FULLVIDEO_ID);//显示视频广告
            } else {
                //接入方也可以在这加载视频广告。但是要限制用户不能频繁点击，加载之间要有一定的时间间隔，视视频大小和网络情况大概在3s~5s。
                Log.i(TAG, "full video ad is not ready");

                long curTime = System.currentTimeMillis();
                boolean isLoad = (0 == mFullVideoTime || curTime - mFullVideoTime > 3000);//3s后可以再加载
                if (isLoad) {//这个是安卓层的防止频繁点击处理。如果接入方在游戏层做了类似的处理，可以将这里的限制注释。
                    mFullVideoTime = curTime;
                    mGameMainActivity.fullVideoAd.loadAd(AdConstant.FULLVIDEO_ID);
                }
            }
        }
    }


    //加载激励视频广告接口，游戏界面加载完成后只用调用一次
    public static void loadRewardVideoAd() {
        //建议接入方在恰当的时机，在游戏层控制调用加载，比如：游戏开始时，进入有视频广告的界面等，按接入方实际情况调用。
        if (null != mGameMainActivity.rewardVideoAd) {
            Log.i(TAG, "加载激励视频广告---->");
            mGameMainActivity.rewardVideoAd.loadAd(AdConstant.REWARDVIDEO_ID);
        }
    }

    //展示激励视频广告接口
    public static void showRewardVideoAd() {
        if (null != mGameMainActivity.rewardVideoAd) {
            if (mGameMainActivity.rewardVideoAd.isReady()) { //判断是否加载完毕
                Log.i(TAG, "展示激励视频广告...");
                mGameMainActivity.rewardVideoAd.showAd(AdConstant.REWARDVIDEO_ID);//显示视频广告
            } else {
                //接入方也可以在这加载视频广告。但是要限制用户不能频繁点击，加载之间要有一定的时间间隔，视视频大小和网络情况大概在3s~5s。
                Log.i(TAG, "reward video ad is not ready");

                long curTime = System.currentTimeMillis();
                boolean isLoad = (0 == mRewardVideoTime || curTime - mRewardVideoTime > 3000);//3s后可以再加载
                if (isLoad) {//这个是安卓层的防止频繁点击处理。如果接入方在游戏层做了类似的处理，可以将这里的限制注释。
                    mRewardVideoTime = curTime;
                    mGameMainActivity.rewardVideoAd.loadAd(AdConstant.REWARDVIDEO_ID);
                }
            }
        }
    }


    //加载展示插屏广告接口
    public static void showInterstitialAd() {
        if (null != mGameMainActivity.interstitialAd) {
            Log.i(TAG, "展示插屏---->");
            mGameMainActivity.interstitialAd.showAd(AdConstant.INTERSTITIAL_ID);
        }
    }


    //加载展示banner广告接口，banner会30s自动刷新
    public static void showBanner() {
        if (null != mGameMainActivity.bannerAd) {
            Log.i(TAG, "展示banner---->");
            mGameMainActivity.bannerAd.showAd(AdConstant.BANNER_ID);
        }
    }

    //因为banner的显示需要网络请求，设置隐藏显示可能无效，所以可以在banner的onReady回调里根据mBannerIsShow设置是否显示
    //设置是否显示banner接口
    public static void setBannerShow(boolean isShow) {
        mGameMainActivity.mBannerIsShow = isShow;
        if (null != mGameMainActivity.bannerAd) {
            Log.i(TAG, "设置banner广告Visibility：" + isShow);
            mGameMainActivity.bannerAd.setVisibility(isShow);
        }
    }

    //加载展示贴片广告接口，每次调用刷新广告
    public static void showInfeedAd() {
        if (null != mGameMainActivity.infeedAd) {
            Log.i(TAG, "展示贴片广告---->");
            mGameMainActivity.infeedAd.showAd(AdConstant.INFEED_ID);
        }
    }

    //设置是否显示的贴片广告接口
    public static void setInfeedShow(boolean isShow) {
        mGameMainActivity.mInfeedIsShow = isShow;
        if (null != mGameMainActivity.infeedAd) {
            Log.i(TAG, "设置infeed广告Visibility：" + isShow);
            mGameMainActivity.infeedAd.setVisibility(isShow);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (bannerAd != null) {
            bannerAd.onStart();
        }
        if (interstitialAd != null) {
            interstitialAd.onStart();
        }
        if (rewardVideoAd != null) {
            rewardVideoAd.onStart();
        }
        if (fullVideoAd != null) {
            fullVideoAd.onStart();
        }
        if (feedAd != null) {
            feedAd.onStart();
        }
        if (infeedAd != null) {
            infeedAd.onStart();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (bannerAd != null) {
            bannerAd.onRestart();
        }
        if (interstitialAd != null) {
            interstitialAd.onRestart();
        }
        if (rewardVideoAd != null) {
            rewardVideoAd.onRestart();
        }
        if (fullVideoAd != null) {
            fullVideoAd.onRestart();
        }
        if (feedAd != null) {
            feedAd.onRestart();
        }
        if (infeedAd != null) {
            infeedAd.onRestart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bannerAd != null) {
            bannerAd.onResume();
        }
        if (interstitialAd != null) {
            interstitialAd.onResume();
        }
        if (rewardVideoAd != null) {
            rewardVideoAd.onResume();
        }
        if (fullVideoAd != null) {
            fullVideoAd.onResume();
        }
        if (feedAd != null) {
            feedAd.onResume();
        }
        if (infeedAd != null) {
            infeedAd.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bannerAd != null) {
            bannerAd.onPause();
        }
        if (interstitialAd != null) {
            interstitialAd.onPause();
        }
        if (rewardVideoAd != null) {
            rewardVideoAd.onPause();
        }
        if (fullVideoAd != null) {
            fullVideoAd.onPause();
        }
        if (feedAd != null) {
            feedAd.onPause();
        }
        if (infeedAd != null) {
            infeedAd.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bannerAd != null) {
            bannerAd.onStop();
        }
        if (interstitialAd != null) {
            interstitialAd.onStop();
        }
        if (rewardVideoAd != null) {
            rewardVideoAd.onStop();
        }
        if (fullVideoAd != null) {
            fullVideoAd.onStop();
        }
        if (feedAd != null) {
            feedAd.onStop();
        }
        if (infeedAd != null) {
            infeedAd.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        if (bannerAd != null) {
            bannerAd.onDestroy();
        }
        if (interstitialAd != null) {
            interstitialAd.onDestroy();
        }
        if (rewardVideoAd != null) {
            rewardVideoAd.onDestroy();
        }
        if (fullVideoAd != null) {
            fullVideoAd.onDestroy();
        }
        if (feedAd != null) {
            feedAd.onDestroy();
        }
        if (infeedAd != null) {
            infeedAd.onDestroy();
        }
        super.onDestroy();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (bannerAd != null) {
            bannerAd.onNewIntent(intent);
        }
        if (interstitialAd != null) {
            interstitialAd.onNewIntent(intent);
        }
        if (rewardVideoAd != null) {
            rewardVideoAd.onNewIntent(intent);
        }
        if (fullVideoAd != null) {
            fullVideoAd.onNewIntent(intent);
        }
        if (feedAd != null) {
            feedAd.onNewIntent(intent);
        }
        if (infeedAd != null) {
            infeedAd.onNewIntent(intent);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bannerAd != null) {
            bannerAd.onSaveInstanceState(outState);
        }
        if (interstitialAd != null) {
            interstitialAd.onSaveInstanceState(outState);
        }
        if (rewardVideoAd != null) {
            rewardVideoAd.onSaveInstanceState(outState);
        }
        if (fullVideoAd != null) {
            fullVideoAd.onSaveInstanceState(outState);
        }
        if (feedAd != null) {
            feedAd.onSaveInstanceState(outState);
        }
        if (infeedAd != null) {
            infeedAd.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (bannerAd != null) {
            bannerAd.onConfigurationChanged(newConfig);
        }
        if (interstitialAd != null) {
            interstitialAd.onConfigurationChanged(newConfig);
        }
        if (rewardVideoAd != null) {
            rewardVideoAd.onConfigurationChanged(newConfig);
        }
        if (fullVideoAd != null) {
            fullVideoAd.onConfigurationChanged(newConfig);
        }
        if (feedAd != null) {
            feedAd.onConfigurationChanged(newConfig);
        }
        if (infeedAd != null) {
            infeedAd.onConfigurationChanged(newConfig);
        }
    }

}
```

###4 退出接口（必接）

```
在主activity添加以下方法即可。
```

```java
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                // SDK退出接口（必接）
                Log.i(TAG, "调用退出接口---->");
                ECUnionSDK.quit(this, new IECQuitResultListener() {

                    @Override
                    public void onQuit() {
                        //cp 在退出前的额外操作...

                        //游戏退出
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

```

###5 登录接口（必接）

```
在主activity添加以下接口使用
```

```java
    //登录接口（必接）
    public static void login() {
        //请在登录完成后加载进入游戏
        ECUnionSDK.login(mGameMainActivity, new IECELoginResultListener() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                Log.i(TAG, "login onSuccess ");
                //返回的userInfo可能为空，部分SDK没登录功能就直接走成功回调。
                String userId = "";
                String userName = "";
                if (null != userInfo) {
                    Log.i(TAG, userInfo.toString());
                    if (!TextUtils.isEmpty(userInfo.getUserId())) {
                        userId = userInfo.getUserId();
                    }
                    if (!TextUtils.isEmpty(userInfo.getUserId())) {
                        userName = userInfo.getUserName();
                    }
                }
                //to do..  开始进入游戏。。
            }

            @Override
            public void onFailure(String errMsg) {
                Log.i(TAG, "login onFailure errMsg=" + errMsg);
            }
        });
    }
```
###6 支付接口（选接）

```
在主activity添加以下接口使用
```

```java
    // 支付接口（选接）
    public static void pay() {
        PayInfo payInfo = new PayInfo();
        payInfo.setCpOrderId("";//接入方的订单号，必填。订单号务必保持唯一
        payInfo.setPayCode("");//计费点，必填

        //客户端的支付回调不可靠，有掉单风险。
        ECUnionSDK.pay(mGameMainActivity, payInfo, new IECEPayResultListener() {

            @Override
            public void onSuccess(PaymentResultInfo paymentResultInfo) {
                Log.i(TAG, "pay onSuccess " + paymentResultInfo.toString());

            }

            @Override
            public void onFailure(String errMsg) {
                Log.i(TAG, "pay onFailure errMsg=" + errMsg);
            }

            @Override
            public void onCancel() {

            }
        });

    }
```

###7  补单接口（如果有支付必接）

```
在主activity添加以下接口使用
```

```java
    //补单接口（如果有支付必接）
    public static void checkMissingOrders() {

        ECUnionSDK.checkMissingOrders(mGameMainActivity, new IMissingRewardResultListener() {

            @Override
            public void onSuccess(PaymentResultInfo paymentResultInfo) {
                Log.i(TAG, "checkMissingOrders onSuccess " + paymentResultInfo.toString());
            }

            @Override
            public void onFailure(String errMsg) {
                Log.i(TAG, "checkMissingOrders onFailure errMsg=" + errMsg);
            }
        });

    }
```
###8  跳转到特定区域接口，比如：OPPO的超休闲专区（如有要求，请接入）

```
在主activity添加以下接口使用
```

```java
    //跳转到特定区域接口，比如：OPPO的超休闲专区（如有要求，请接入）
    public static void jumpSpecialArea() {
        //只需要在跳转的位置调用即可
        ECUnionSDK.jumpSpecialArea(mGameMainActivity);
    }
```


###9  上报用户游戏信息接口（如有要求，请接入）

```
在主activity添加以下接口使用
```

```java
    //上报用户游戏信息接口（如有要求，请接入）
    public static void reportUserGameInfoData() {

//        CP 可在玩家首次成功登陆游戏服务器，并选定游戏角色信息后，调用该接口，上传玩家初始角色
//        信息；为了确保即时性和准确性：
//         1、每次登陆进入游戏之后都要调用接口上报数据；
//         2、当游戏中角色信息任意一个发生变化时要立即调用接口上报数据

        //按游戏实际情况，传入参数
        Map map = new HashMap();
        map.put("roleId", "");//角色id
        map.put("roleName", "");//角色昵称
        map.put("roleLevel", "");//角色等级
        map.put("areaId", "");//区服id
        map.put("areaName", "");//区服名称
        map.put("chapter", "");//关卡章节
        map.put("combatValue", "");//战力值，有战力值的网游必须上传，活动工具需要根据战力值评估
        map.put("pointValue", "");//积分值，捕鱼或棋牌类休闲游戏参与比赛的积分必须上传，如果暂无积分值，游戏可自行设置积分值并上传。
        ECUnionSDK.reportUserGameInfoData(mGameMainActivity, map);
    }
```

###10  自定义计数事件统计接口（选接）

```
在主activity添加以下接口使用。
注意：
自定义事件ID,需要跟后台创建的事件ID一致。
事件ID或者map的key请使用（英文、数字、下划线、中划线、小数点及加号）进行定义，建议加上前缀，防止跟关键字冲突。
```

```java
    public static void onEventObject() {

        //事件ID,建议加上前缀，防止跟关键字冲突，需要跟后台创建的事件ID一致。
        String eventId = "";
        //对当前事件的属性描述，定义为“属性名:属性值”的HashMap“<键-值>对”。
        //如果事件不需要属性，传递null即可。(属性值目前仅支持以下数据类型: String，Integer，Long，Short，Float，Double)
        //key建议加上前缀，防止跟关键字冲突。
        Map<String, Object> map = new HashMap<String, Object>();

        //计数事件统计
        ECUnionSDK.onEventObject(mGameMainActivity, eventId, map);

    }
```


###11  自定义计算事件统计接口（选接）

```
在主activity添加以下接口使用。
注意：
自定义事件ID,需要跟后台创建的事件ID一致。
事件ID或者map的key请使用（英文、数字、下划线、中划线、小数点及加号）进行定义，建议加上前缀，防止跟关键字冲突。
```

```java
    public static void onEventValue() {

        //事件ID，建议加上前缀，防止跟关键字冲突，需要跟后台创建的事件ID一致。
        String eventId = "";
        //为当前事件的属性和取值（Key-Value键值对），可选，键值的数据类型都是string
        //key建议加上前缀，防止跟关键字冲突。
        Map<String, String> map = new HashMap<String, String>();
        //当前事件的数值，取值范围是-2,147,483,648 到 +2,147,483,647 之间的
        //有符号整数，即int 32类型，如果数据超出了该范围，会造成数据丢包，
        //影响数据统计的准确性。
        int du = 1;

        //计算事件统计
        ECUnionSDK.onEventValue(mGameMainActivity, eventId, map, du);

    }

```
