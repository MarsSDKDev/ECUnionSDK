package com.sigmob.windad.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.pb.cdsbkncdd.R;
import com.tencent.bugly.crashreport.CrashReport;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    //    private Button loadAdBtn, configurationBtn, initSDKBtn, playAdBtn;
//    private TextView logTextView;
    private MainFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createMainFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(checkSelfPermission(READ_PHONE_STATE) != PERMISSION_GRANTED
                    || checkSelfPermission(ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                    || checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED){

                requestPermissions(new String[]{READ_PHONE_STATE,WRITE_EXTERNAL_STORAGE,ACCESS_FINE_LOCATION},PERMISSION_GRANTED);

            }else if (checkSelfPermission(READ_PHONE_STATE) != PERMISSION_GRANTED) {

                requestPermissions(new String[]{READ_PHONE_STATE},PERMISSION_GRANTED);

            }else if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                // TODO: Consider calling
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},PERMISSION_GRANTED);
            }
        }

        CrashReport.initCrashReport(getApplicationContext(), "4c41e5eed0", true);
//        CrashReport.testJavaCrash();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void createMainFragment() {

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.fragment_container) != null && mMainFragment == null) {

            mMainFragment = new MainFragment();
            Intent intent =  getIntent();
            String[] logs  = intent.getStringArrayExtra("logs");

            mMainFragment.setLogs(logs);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mMainFragment).commit();

        }
    }



    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed() called " + getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

}