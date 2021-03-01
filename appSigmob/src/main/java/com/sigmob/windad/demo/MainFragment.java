package com.sigmob.windad.demo;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pb.cdsbkncdd.R;
import com.sigmob.windad.WindAdError;
import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.fullscreenvideo.WindFullScreenAdRequest;
import com.sigmob.windad.fullscreenvideo.WindFullScreenVideoAd;
import com.sigmob.windad.fullscreenvideo.WindFullScreenVideoAdListener;
import com.sigmob.windad.rewardedVideo.WindRewardAdRequest;
import com.sigmob.windad.rewardedVideo.WindRewardInfo;
import com.sigmob.windad.rewardedVideo.WindRewardedVideoAd;
import com.sigmob.windad.rewardedVideo.WindRewardedVideoAdListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class MainFragment extends Fragment implements WindRewardedVideoAdListener, WindFullScreenVideoAdListener {


    private TextView logTextView;
    private WindRewardAdRequest rewardAdRequest;
    private String[] mlogs;
    private WindFullScreenAdRequest fullScreenAdRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.main_fragment, container, false);


        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initSDK();

        View view = getView();
        Button configurationBtn = view.findViewById(R.id.configuration_button);
        configurationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configuration();
            }
        });

        logTextView = view.findViewById(R.id.logView);

        Button deviceId = view.findViewById(R.id.deviceId_button);
        deviceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeviceId();
            }
        });

        Button playRewardAdBtn = view.findViewById(R.id.playRewardAd);
        Button loadRewardAdBtn = view.findViewById(R.id.loadRewardAd);


        loadRewardAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewardAdRequest();
            }
        });
        playRewardAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRewardAd();
            }
        });
        Button loadFullScreenAdBtn = view.findViewById(R.id.loadFullScreenAd);

        Button playFullScreenAdBtn = view.findViewById(R.id.playFullScreenAd);


        loadFullScreenAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreenAdRequest();
            }
        });
        playFullScreenAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playFullScreenAd();
            }
        });


        //fix longclick crash with textview
        logTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        logTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        WindRewardedVideoAd windRewardedVideoAd = WindRewardedVideoAd.sharedInstance();


        windRewardedVideoAd.setWindRewardedVideoAdListener(this);

        WindFullScreenVideoAd windFullScreenVideoAd = WindFullScreenVideoAd.sharedInstance();
        windFullScreenVideoAd.setWindFullScreenVideoAdListener(this);

        if (mlogs != null && mlogs.length > 0) {
            for (int i = 0; i < mlogs.length; i++) {
                logMessage(mlogs[i]);
            }
        }
    }


    public void setLogs(String[] logs) {
        mlogs = logs;
    }


    private void rewardAdRequest() {

        WindRewardedVideoAd windRewardedVideoAd = WindRewardedVideoAd.sharedInstance();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setting", 0);

        String placementId = sharedPreferences.getString(Constants.CONF_REWARD_PLACEMENTID, Constants.reward_placement_id);
        String userId = sharedPreferences.getString(Constants.CONF_USERID, "");

        rewardAdRequest = new WindRewardAdRequest(placementId, userId, null);

        windRewardedVideoAd.loadAd(getActivity(), rewardAdRequest);

    }

    private void fullScreenAdRequest() {

        WindFullScreenVideoAd windFullScreenVideoAd = WindFullScreenVideoAd.sharedInstance();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setting", 0);

        String placementId = sharedPreferences.getString(Constants.CONF_FULLSCREEN_PLACEMENTID, Constants.fullScreen_placement_id);
        String userId = sharedPreferences.getString(Constants.CONF_USERID, "");

        fullScreenAdRequest = new WindFullScreenAdRequest(placementId, userId, null);


        windFullScreenVideoAd.loadAd(getActivity(), fullScreenAdRequest);

    }


    private void showDeviceId() {
        Log.d("lance","showDeviceId");
        DeviceIdFragment deviceIdFragment = new DeviceIdFragment();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, deviceIdFragment);
        transaction.addToBackStack("deviceId");
        transaction.commit();

    }

    private void configuration() {

        ConfigurationFragment configurationFragment = new ConfigurationFragment();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, configurationFragment);
        transaction.addToBackStack("configuration");
        transaction.commit();
    }

    private void initSDK() {
        WindAds ads = WindAds.sharedAds();

        //enable or disable debug log
        ads.setDebugEnable(false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setting", 0);
        String appId = sharedPreferences.getString(Constants.CONF_APPID, Constants.app_id);
        String appKey = sharedPreferences.getString(Constants.CONF_APPKEY, Constants.app_key);

        ads.startWithOptions(getActivity(), new WindAdOptions(appId, appKey));

    }


    private void playRewardAd() {
        WindRewardedVideoAd windRewardedVideoAd = WindRewardedVideoAd.sharedInstance();

        try {
            if (windRewardedVideoAd.isReady(rewardAdRequest.getPlacementId())) {
                windRewardedVideoAd.show(getActivity(), rewardAdRequest);
            } else {
                logMessage("Ad not Ready [ " + rewardAdRequest.getPlacementId() + " ]");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logMessage("Exception error" + e.getMessage());
        }
    }

    private void playFullScreenAd() {
        WindFullScreenVideoAd windFullScreenVideoAd = WindFullScreenVideoAd.sharedInstance();

        try {
            if (windFullScreenVideoAd.isReady(fullScreenAdRequest.getPlacementId())) {
                windFullScreenVideoAd.show(getActivity(), fullScreenAdRequest);
            } else {
                logMessage("Ad not Ready [ " + fullScreenAdRequest.getPlacementId() + " ]");

            }
        } catch (Throwable e) {
            e.printStackTrace();
            logMessage("Exception error" + e.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        WindAds ads = WindAds.sharedAds();

    }

    private static SimpleDateFormat dateFormat = null;

    private static SimpleDateFormat getDateTimeFormat() {

        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss SSS", Locale.CHINA);
        }
        return dateFormat;
    }

    private void logMessage(String message) {
        Date date = new Date();
        logTextView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }


    @Override
    public void onVideoAdPreLoadSuccess(final String placementId) {
        Toast.makeText(getActivity(), "onVideoAdPreLoadSuccess", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onVideoAdPreLoadSuccess [ " + placementId + " ]");
            }
        });
    }


    @Override
    public void onVideoAdPreLoadFail(final String placementId) {
        Toast.makeText(getActivity(), "onVideoAdPreLoadFail", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onVideoAdPreLoadFail [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onVideoAdLoadSuccess(final String placementId) {
        Toast.makeText(getActivity(), "onVideoAdLoadSuccess", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onVideoAdLoadSuccess [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onVideoAdPlayStart(final String placementId) {
        Toast.makeText(getActivity(), "onVideoAdPlayStart", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onVideoAdPlayStart [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onVideoAdPlayEnd(final String placementId) {
        Toast.makeText(getActivity(), "onVideoAdPlayEnd", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onVideoAdPlayEnd [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onVideoAdClicked(final String placementId) {
        Toast.makeText(getActivity(), "onVideoAdClicked", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onVideoAdClicked [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onVideoAdClosed(final WindRewardInfo info, final String placementId) {
        Toast.makeText(getActivity(), "onVideoAdClosed", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onVideoAdClosed() called with: info = [" + info + "], placementId = [" + placementId + "]");
            }
        });

    }

    @Override
    public void onVideoAdLoadError(final WindAdError error, final String placementId) {
        Toast.makeText(getActivity(), "onVideoAdLoadError error: [" + error + "]", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onVideoAdLoadError() called with: error = [" + error + "], placementId = [" + placementId + "]");
            }
        });
    }

    @Override
    public void onVideoAdPlayError(final WindAdError error, final String placementId) {
        Toast.makeText(getActivity(), "onVideoAdPlayError error: [" + error + "]", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onVideoAdPlayError() called with: error = [" + error + "], placementId = [" + placementId + "]");
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        WindRewardedVideoAd.sharedInstance().setWindRewardedVideoAdListener(null);

    }

    @Override
    public void onFullScreenVideoAdLoadSuccess(final String placementId) {
        Toast.makeText(getActivity(), "onFullScreenVideoAdLoadSuccess", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onFullScreenVideoAdLoadSuccess [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onFullScreenVideoAdPreLoadSuccess(final String placementId) {
        Toast.makeText(getActivity(), "onFullScreenVideoAdPreLoadSuccess", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onFullScreenVideoAdPreLoadSuccess [ " + placementId + " ]");
            }
        });

    }

    @Override
    public void onFullScreenVideoAdPreLoadFail(final String placementId) {
        Toast.makeText(getActivity(), "onFullScreenVideoAdPreLoadFail", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onFullScreenVideoAdPreLoadFail [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onFullScreenVideoAdPlayStart(final String placementId) {
        Toast.makeText(getActivity(), "onFullScreenVideoAdPlayStart", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onFullScreenVideoAdPlayStart [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onFullScreenVideoAdPlayEnd(final String placementId) {
        Toast.makeText(getActivity(), "onFullScreenVideoAdPlayEnd", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onFullScreenVideoAdPlayEnd [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onFullScreenVideoAdClicked(final String placementId) {
        Toast.makeText(getActivity(), "onFullScreenVideoAdClicked", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onFullScreenVideoAdClicked [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onFullScreenVideoAdClosed(final String placementId) {
        Toast.makeText(getActivity(), "onFullScreenVideoAdClosed", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onFullScreenVideoAdClosed [ " + placementId + " ]");
            }
        });
    }

    @Override
    public void onFullScreenVideoAdLoadError(final WindAdError error, final String placementId) {
        Toast.makeText(getActivity(), "onFullScreenVideoAdLoadError error: [" + error + "]", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onFullScreenVideoAdLoadError() called with: error = [" + error + "], placementId = [" + placementId + "]");
            }
        });
    }

    @Override
    public void onFullScreenVideoAdPlayError(final WindAdError error, final String placementId) {
        Toast.makeText(getActivity(), "onFullScreenVideoAdPlayError error: [" + error + "]", Toast.LENGTH_SHORT).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logMessage("onFullScreenVideoAdPlayError() called with: error = [" + error + "], placementId = [" + placementId + "]");
            }
        });
    }
}
