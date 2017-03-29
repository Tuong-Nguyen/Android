package vn.com.tma.idlesmart;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class AppContext extends Application {
    private static final String TAG = "IdleSmart.AppContext";
    public static AppContext instance;
    private OnScreenOffReceiver onScreenOffReceiver;
    private WakeLock wakeLock;

    public AppContext() {
        this.onScreenOffReceiver = null;
    }

    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "#########################################################################################################################################");
        Log.i(TAG, "==>>onCreate");
        instance = this;
        Log.i(TAG, "KioskMode enabled");
        startKioskService();
        Log.i(TAG, "<<==onCreate");
    }

    private void registerKioskModeScreenOffReceiver() {
        IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.SCREEN_ON");
        this.onScreenOffReceiver = new OnScreenOffReceiver();
        Log.i(TAG, "register onScreenOff receiver..");
        registerReceiver(this.onScreenOffReceiver, filter);
    }

    public WakeLock getWakeLock() {
        if (this.wakeLock == null) {
            this.wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE
                    , "IdleSmartAppContextWakeup");
            Log.i(TAG, "WakeLock acquired? " + this.wakeLock);
        }
        return this.wakeLock;
    }

    public void startKioskService() {
        Log.i(TAG, "start KioskService..");
        registerKioskModeScreenOffReceiver();
        startService(new Intent(this, KioskService.class));
    }

    public void stopKioskService() {
        Log.i(TAG, "stop KioskService..");
        stopService(new Intent(this, KioskService.class));
        Log.i(TAG, "unregister onScreenOff receiver..");
        if (this.onScreenOffReceiver != null) {
            try {
                unregisterReceiver(this.onScreenOffReceiver);
            } catch (IllegalArgumentException e) {
            }
        }
    }
}
