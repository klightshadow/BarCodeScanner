package com.miiixer.barcodescanner;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Vibrator vibrator;
    private TextView showInfo;
    private Button startScanner;
    private BarcodeView barcodeView;
    private ViewfinderView viewFinder;
    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(Result result) {
            if(result.getText() != null) {
                vibrator.vibrate(300);
                showInfo.setText(result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            for (ResultPoint point : resultPoints) {
                viewFinder.addPossibleResultPoint(point);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        barcodeView = (BarcodeView) findViewById(R.id.bcv_barcodeView);
        barcodeView.decodeContinuous(barcodeCallback);

        viewFinder = (ViewfinderView) findViewById(R.id.zxing_viewfinder_view);
        viewFinder.setCameraPreview(barcodeView);

        showInfo = (TextView) findViewById(R.id.tv_showInfo);
        startScanner = (Button) findViewById(R.id.btn_startScanner);

        startScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.initiateScan(IntentIntegrator.ONE_D_CODE_TYPES);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult != null) {
            String result = intentResult.getContents();
            if(result != null) {
                vibrator.vibrate(300);
                showInfo.setText(intentResult.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d("MainActivity", "Configuration changed");

    }

    private void orientationChanged() {
        barcodeView.pause();
        barcodeView.resume();
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(barcodeCallback);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                return true;
            // Use volume up/down to turn on light
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                barcodeView.setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                barcodeView.setTorch(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
