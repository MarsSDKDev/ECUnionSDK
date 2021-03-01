package com.helper.union.ec.appvungle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hase.vug.Constants;
import com.pb.mmbwdpmcqll5.R;
import com.vungle.warren.AdConfig;
import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

public class MainActivity extends Activity {

    private String TAG = "@@@@@@";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void onClick(View view){
        String str = "";
        switch (view.getId()){
            case R.id.test1:
                str = "test1";
                loadFv();
                break;
            case R.id.test2:
                str = "test2";
                showFv();
                break;
            case R.id.test3:
                str = "test3";
                loadRv();
                break;
            case R.id.test4:
                str = "test4";
                showRv();
                break;
        }
        Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();
    }

    public void init(){
        Vungle.init(Constants.app_id, getApplicationContext(), new InitCallback() {
            @Override
            public void onSuccess() {
                // Initialization has succeeded and SDK is ready to load an ad or play one if there
                // is one pre-cached already
                Log.i(TAG,"init onSuccess");
            }

            @Override
            public void onError(VungleException e) {
                // Initialization error occurred - e.getLocalizedMessage() contains error message
                Log.i(TAG,"init onError:"+e);
            }

            @Override
            public void onAutoCacheAdAvailable(String placementId) {
                // Callback to notify when an ad becomes available for the cache optimized placement
                // NOTE: This callback works only for the cache optimized placement. Otherwise, please use
                // LoadAdCallback with loadAd API for loading placements.
                Log.i(TAG,"placementId="+placementId);
            }
        });
    }

    public void loadRv(){
        if(!Vungle.isInitialized()){
            return;
        }

        Vungle.loadAd(Constants.reward_placement_id, new LoadAdCallback() {
            @Override
            public void onAdLoad(String placementReferenceId) {
                Log.i(TAG,"load:"+placementReferenceId);
            }

            @Override
            public void onError(String placementReferenceId, VungleException e) {
                // Load ad error occurred - e.getLocalizedMessage() contains error message
                Log.i(TAG,"load error:"+placementReferenceId + " --> " + e.getLocalizedMessage());
            }
        });

    }

    public void loadFv(){
        if(!Vungle.isInitialized()){
            return;
        }

        Vungle.loadAd(Constants.fullScreen_placement_id, new LoadAdCallback() {
            @Override
            public void onAdLoad(String placementReferenceId) {
                Log.i(TAG,"load:"+placementReferenceId);
            }

            @Override
            public void onError(String placementReferenceId, VungleException e) {
                // Load ad error occurred - e.getLocalizedMessage() contains error message
                Log.i(TAG,"load error:"+placementReferenceId + " --> " + e.getLocalizedMessage());
            }
        });

    }

    public void showRv(){
        if (Vungle.canPlayAd(Constants.reward_placement_id)) {
            AdConfig adConfig = new AdConfig();
            // Mute
            // adConfig.setMuted(true);
            // Set Oritation
            // adConfig.setAdOrientation(AdConfig.AUTO_ROTATE) // AdConfig.PORTRAIT, AdConfig.LANDSCAPE

            Vungle.playAd(Constants.reward_placement_id, adConfig, new PlayAdCallback() {
                @Override
                public void onAdStart(String id) {
                    // Ad experience started
                    Log.i(TAG,"start:"+id);
                }

                @Override
                public void onAdEnd(String s, boolean b, boolean b1) {

                }

                @Override
                public void onAdViewed(String id) {
                    // Ad has rendered

                }

                @Override
                public void onAdEnd(String id) {
                    // Ad experience ended
                }

                @Override
                public void onAdClick(String id) {
                    // User clicked on ad
                    Log.i(TAG,"click:"+id);
                }

                @Override
                public void onAdRewarded(String id) {
                    // User earned reward for watching an ad
                    Log.i(TAG,"reward:"+id);
                }

                @Override
                public void onAdLeftApplication(String id) {
                    // User has left app during an ad experience
                }

                @Override
                public void onError(String id, VungleException exception) {
                    // Ad failed to play
                    Log.i(TAG,"error:"+id + " --> " + exception.getLocalizedMessage());
                }
            });
        }

    }

    public void showFv(){
        if (Vungle.canPlayAd(Constants.fullScreen_placement_id)) {
            AdConfig adConfig = new AdConfig();
            // Mute
            // adConfig.setMuted(true);
            // Set Oritation
            // adConfig.setAdOrientation(AdConfig.AUTO_ROTATE) // AdConfig.PORTRAIT, AdConfig.LANDSCAPE

            Vungle.playAd(Constants.fullScreen_placement_id, adConfig, new PlayAdCallback() {
                @Override
                public void onAdStart(String id) {
                    // Ad experience started
                    Log.i(TAG,"start:"+id);
                }

                @Override
                public void onAdEnd(String s, boolean b, boolean b1) {

                }

                @Override
                public void onAdViewed(String id) {
                    // Ad has rendered
                }

                @Override
                public void onAdEnd(String id) {
                    // Ad experience ended
                }

                @Override
                public void onAdClick(String id) {
                    // User clicked on ad
                    Log.i(TAG,"click:"+id);
                }

                @Override
                public void onAdRewarded(String id) {
                    // User earned reward for watching an ad
                    Log.i(TAG,"reward:"+id);
                }

                @Override
                public void onAdLeftApplication(String id) {
                    // User has left app during an ad experience
                }

                @Override
                public void onError(String id, VungleException exception) {
                    // Ad failed to play
                    Log.i(TAG,"error:"+id + " --> " + exception.getLocalizedMessage());
                }
            });
        }

    }

}
