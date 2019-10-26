package andras.ludvig.junctionx2019;

import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    // Class variables and constants
    public static final String EXTRA_MESSAGE = "Server address";
    private String JWTaccesstoken,url;
    private String tag = "Junction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(tag,"App started");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        }

        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                "authentication", Context.MODE_PRIVATE);
        JWTaccesstoken = sharedPref.getString("JWTaccesstoken", "");
        url = sharedPref.getString("url", "");
        Log.v(tag,"JWT: " + JWTaccesstoken);
        Log.v(tag,"Url: " + url);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(JWTaccesstoken == ""){
                    startQRcodeActivity();
                }
                else{
                    startWebViewActivity(url + "," + JWTaccesstoken);
                }
            }
        }, 5000);
    }

    public void startQRcodeActivity(){
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        startActivity(intent);
    }

    public void startWebViewActivity(String message){
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }


    // Override BACK button
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}
