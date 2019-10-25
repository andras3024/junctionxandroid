package andras.ludvig.junctionx2019;

import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    // Class variables and constants
    public static final String EXTRA_MESSAGE = "Server address";
    private String JWTaccesstoken,url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        }

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        JWTaccesstoken = sharedPref.getString("JWTaccesstoken", "");
        url = sharedPref.getString("url", "");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(JWTaccesstoken == ""){
                    startQRcodeActivity();
                }
                else{
                    startWebViewActivity();
                }
            }
        }, 5000);
    }

    public void startQRcodeActivity(){
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        startActivity(intent);
    }

    public void startWebViewActivity(){
        Intent intent = new Intent(this, WebViewActivity.class);
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
