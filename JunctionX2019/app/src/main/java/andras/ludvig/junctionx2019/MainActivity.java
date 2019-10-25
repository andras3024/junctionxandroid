package andras.ludvig.junctionx2019;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends Activity {
    // Class variables and constants
    private WebView myWebView;
    public static final String EXTRA_MESSAGE = "Server address";
    static final int REQUEST_TAKE_PHOTO  = 1;
    String currentPhotoPath;
    private String JWTaccesstoken,authcred;
    private String POSTUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWebview();
        String url = "https://www.google.hu/";
        myWebView.loadUrl(url);
    }

    public void setWebview(){
        // Webview settings
        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setAllowFileAccess(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.clearCache(true);
    }

    // Redirect new url requests to WebViewClient
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.v("URL","Redirecting to" + url);
            //Map<String, String> headers = new HashMap<String, String>();
           // headers.put("Authorization", authcred);
            //view.loadUrl(url,headers);
            view.loadUrl(url);
            return false;
        }
    }

    // Start a photo capture with built in camera app
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //Log.e("Failed", "Unable to create Image File", ex);
                Toast.makeText(getApplicationContext(), "Error while creating picture file.", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    // Create an image file for photo storing
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    // Upload image to webserver, after photo is taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user captures an Image
        Log.v("PHOTO","Activity result");
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case REQUEST_TAKE_PHOTO:
                    Log.v("PHOTO",currentPhotoPath);
                    uploadToServer(currentPhotoPath);
                    break;
            }
    }

    // Upload image to server through POST method
    private void uploadToServer(String filePath) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadAPIs uploadAPIs = retrofit.create(UploadAPIs.class);
        // Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);
        // Create request body with text description and text media type
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        // Call POST method with given arguments
        Call call = uploadAPIs.uploadImage(POSTUrl,part, description,authcred);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("URL","response.raw().request().url();"+response.raw().request().url());
                HttpUrl httpurl = response.raw().request().url();
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", authcred);
                myWebView.loadUrl(httpurl.url().toString(),headers);
            }
            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
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
