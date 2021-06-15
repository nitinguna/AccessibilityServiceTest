package jp.co.smj.sample.accessibilityservicetest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    private int deviceId = -1;
    private InputManagerWrapper inputManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         handler = new Handler();
        inputManager = new InputManagerWrapper();
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonSetting).setOnClickListener(this::buttonSetting_onClick);
        findViewById(R.id.button1).setTag(1);
        findViewById(R.id.button1).setOnClickListener(this::button_onClick);
        findViewById(R.id.button2).setTag(2);
        findViewById(R.id.button2).setOnClickListener(this::button_onClick);
        findViewById(R.id.button3).setTag(3);
        findViewById(R.id.button3).setOnClickListener(this::button_onClick);
        findViewById(R.id.button4).setTag(4);
        findViewById(R.id.button4).setOnClickListener(this::button_onClick);
        findViewById(R.id.button5).setTag(5);
        findViewById(R.id.button5).setOnClickListener(this::button_onClick);
        findViewById(R.id.button6).setTag(6);
        findViewById(R.id.button6).setOnClickListener(this::button_onClick);
    }

    private void button_onClick(View view) {
        if (view == null || view.getTag() == null) return;
        int tag = (int) view.getTag();
        BaseInputConnection mInputConnection = new BaseInputConnection(
                findViewById(R.id.button_bar_1), true);
        KeyEvent kd, ku;
        switch (tag) {
            case 1:
                kd = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER);
                ku = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_POWER);
                mInputConnection.sendKeyEvent(kd);
                mInputConnection.sendKeyEvent(ku);
                break;
            case 2:
                kd = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME);
                ku = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME);
                mInputConnection.sendKeyEvent(kd);
                mInputConnection.sendKeyEvent(ku);
                break;
            case 3:
                kd = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
                ku = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK);
                mInputConnection.sendKeyEvent(kd);
                mInputConnection.sendKeyEvent(ku);
                break;
            case 4:
                kd = new KeyEvent(KeyEvent.ACTION_DOWN, 103);
                ku = new KeyEvent(KeyEvent.ACTION_UP, 103);
                mInputConnection.sendKeyEvent(kd);
                mInputConnection.sendKeyEvent(ku);
                break;
            case 5:
                //simulateKey(KeyEvent.KEYCODE_VOLUME_UP);
                keyPress(KeyEvent.KEYCODE_VOLUME_UP, 0);
                /*kd = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP);
                ku = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_UP);
                mInputConnection.sendKeyEvent(kd);
                mInputConnection.sendKeyEvent(ku);*/
                break;
            case 6:
                //simulateKey(KeyEvent.KEYCODE_VOLUME_DOWN);
                keyPress(KeyEvent.KEYCODE_VOLUME_DOWN, 0);
                /*kd = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_DOWN);
                ku = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_DOWN);
                mInputConnection.sendKeyEvent(kd);
                mInputConnection.sendKeyEvent(ku);*/
                break;
        }
    }



    private  void keyPress(int keyCode, int metaState) {
        keyDown(keyCode, metaState);
        keyUp(keyCode, metaState);
    }

    private void keyDown(int keyCode, int metaState) {
        long time = SystemClock.uptimeMillis();
        handler.post( () -> inputManager.injectKeyEvent(new KeyEvent(
                time,
                time,
                KeyEvent.ACTION_DOWN,
                keyCode,
                0,
                metaState,
                deviceId,
                0,
                KeyEvent.FLAG_FROM_SYSTEM,
                InputDevice.SOURCE_KEYBOARD
        )));
    }

    private void keyUp(int keyCode, int metaState) {
        long time = SystemClock.uptimeMillis();
        handler.post( ()-> inputManager.injectKeyEvent(new KeyEvent(
                time,
                time,
                KeyEvent.ACTION_UP,
                keyCode,
                0,
                metaState,
                deviceId,
                0,
                KeyEvent.FLAG_FROM_SYSTEM,
                InputDevice.SOURCE_KEYBOARD
        )));
    }

    public void startService() {
        Intent serviceIntent = new Intent(getApplicationContext(), KeyAccessibilityService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        getApplicationContext().startService(serviceIntent);
    }
    private void buttonSetting_onClick(View view) {

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        startService();
    }

    public static void simulateKey(final int KeyCode) {

        new Thread() {
            @Override
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    Log.e("Exception when sendKeyDownUpSync", e.toString());
                }
            }

        }.start();
    }

}