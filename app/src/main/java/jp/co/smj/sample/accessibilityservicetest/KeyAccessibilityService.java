package jp.co.smj.sample.accessibilityservicetest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import androidx.core.app.NotificationCompat;

public class KeyAccessibilityService extends AccessibilityService {
    private static final String TAG = KeyAccessibilityService.class.getSimpleName();
    private static final String CHANNEL_ID = "AccessbilityServiceChannel";
    private BroadcastReceiver mBroadcastReceiver;
    public static final String ACTION_APPLICATION_RESTRICTIONS_CHANGED_FROM_VENDING = "android.intent.action.ACTION_APPLICATION_RESTRICTIONS_CHANGED_FROM_VENDING";
    private InputManagerWrapper inputManager;
    private int deviceId = -1;

    @Override
    public void onCreate() {

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        inputManager = new InputManagerWrapper();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // input key events

                int iSelectedItem = intent.getIntExtra("keyitem", -1);
                Log.e(TAG, "Recived broadcast key " +iSelectedItem);
                switch (iSelectedItem){
                    case 1:
                        keyPress(KeyEvent.KEYCODE_POWER, 0);
                        break;
                    case 2:
                        keyPress(KeyEvent.KEYCODE_HOME, 0);
                        break;
                    case 3:
                        keyPress(KeyEvent.KEYCODE_BACK, 0);
                        break;
                    case 4:
                        keyPress(103, 0);
                        break;
                    case 5:
                        keyPress(KeyEvent.KEYCODE_VOLUME_UP, 0);
                        break;
                    case 6:
                        keyPress(KeyEvent.KEYCODE_VOLUME_DOWN, 0);
                        break;
                    default :
                        break;

                }
                //keyPress(KeyEvent.KEYCODE_VOLUME_DOWN, 0);
            }
        };
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED);
        intentFilter.addAction(ACTION_APPLICATION_RESTRICTIONS_CHANGED_FROM_VENDING);
        registerReceiver(mBroadcastReceiver,
                intentFilter);

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Zebra Accessibity Service")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
        stopForeground(true);
        stopSelf();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    @Override
    public void onServiceConnected() {
        Log.d(TAG, "on Service Connected");
        getServiceInfo().flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS | AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }


    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int key = event.getKeyCode();
        Log.d(TAG, String.format("onKeyEvent:keycode=%d action=%d", key, event.getAction()));

        switch(key){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Log.i(TAG, "KEYCODE_VOLUME_DOWN");
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                Log.i(TAG, "KEYCODE_VOLUME_UP");
                break;
        }
        return super.onKeyEvent(event);
    }

    private  void keyPress(int keyCode, int metaState) {
        Log.e(TAG, "keyPress");
        keyDown(keyCode, metaState);
        keyUp(keyCode, metaState);
    }

    private void keyDown(int keyCode, int metaState) {
        long time = SystemClock.uptimeMillis();
        inputManager.injectKeyEvent(new KeyEvent(
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
        ));
    }

    private void keyUp(int keyCode, int metaState) {
        long time = SystemClock.uptimeMillis();
        inputManager.injectKeyEvent(new KeyEvent(
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
        ));

    }

}
