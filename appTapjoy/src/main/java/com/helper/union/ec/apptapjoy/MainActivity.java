package com.helper.union.ec.apptapjoy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;

import java.util.Hashtable;

public class MainActivity extends Activity {

    private String TAG = "@@@@@@";

    TJPlacement rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init();
        Hashtable connectFlags = new Hashtable();
        connectFlags.put("TJC_OPTION_ENABLE_LOGGING", "true");
        Tapjoy.setDebugEnabled(true);
        Tapjoy.connect(this.getApplicationContext(), "Wmu1PDRVTK-TblXw-2LjkwECmMKxK4BhcmlEHPbW7YYdDJNqWJpVFXUVigt4", connectFlags, new TJConnectListener(){

            @Override
            public void onConnectSuccess() {
                Log.i(TAG,"connect success");
            }

            @Override
            public void onConnectFailure() {
                Log.i(TAG,"connect failure");
            }
        });
    }

    //session start
    @Override
    protected void onStart() {
        super.onStart();
        Tapjoy.onActivityStart(this);
    }

    //session end
    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(this);
        super.onStop();
    }

    public void onClick(View view){
        String str = "";
        switch (view.getId()){
            case R.id.test1:
                str = "test1";
                rv = Tapjoy.getPlacement("rv_p", new TJPlacementListener() {
                    @Override
                    public void onRequestSuccess(TJPlacement tjPlacement) {

                    }

                    @Override
                    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {

                    }

                    @Override
                    public void onContentReady(TJPlacement tjPlacement) {

                    }

                    @Override
                    public void onContentShow(TJPlacement tjPlacement) {

                    }

                    @Override
                    public void onContentDismiss(TJPlacement tjPlacement) {

                    }

                    @Override
                    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {

                    }

                    @Override
                    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {

                    }

                    @Override
                    public void onClick(TJPlacement tjPlacement) {

                    }
                });
                break;
            case R.id.test2:
                str = "test2";
                if(Tapjoy.isConnected()) {
                    rv.requestContent();
                } else {
                    Log.d(TAG, "Tapjoy SDK must finish connecting before requesting content.");
                }
                if(rv.isContentReady()) {
                    rv.showContent();
                }
                else {
                    //handle situation where there is no content to show, or it has not yet downloaded.
                }
                break;
            case R.id.test3:
                str = "test3";

                break;
            case R.id.test4:
                str = "test4";

                break;
        }
        Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();
    }
}
