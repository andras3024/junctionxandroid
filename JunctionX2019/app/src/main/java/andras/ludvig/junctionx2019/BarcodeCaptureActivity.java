package andras.ludvig.junctionx2019;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class BarcodeCaptureActivity extends Activity {

    private SurfaceView cameraView;
    private TextView barcodeInfo;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    public static final String EXTRA_MESSAGE = "Server address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_capture);

        barcodeInfo = (TextView)findViewById(R.id.code_info);
        preview = (CameraSourcePreview) findViewById(R.id.cameraSourcePreview);

        setupBarcodeDetector();
        setupCameraSource();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }


    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                preview.start(cameraSource);
            } catch (IOException e) {
                cameraSource.release();
                cameraSource = null;
            }
        }
    }


    private void setupBarcodeDetector() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                //.setBarcodeFormats(Barcode.QR_CODE)
                .build();

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            barcodeInfo.setText(R.string.qrcode_done);
                        }
                    });
                    //playBeep()
                    //Log.d("QR",barcodes.valueAt(0).displayValue);
                    sendQRMessage(barcodes.valueAt(0).displayValue);
                }
            }
        });

        if (!barcodeDetector.isOperational()) {
            Log.w("TAG_QR", "Detector dependencies are not yet available.");
        }

    }

    private void setupCameraSource() {
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(15.0f)
                .setRequestedPreviewSize(640, 640)
                .build();
    }

    private void playBeep() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    /** Called when valid QR code is read*/
    public void sendQRMessage(String message) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}

