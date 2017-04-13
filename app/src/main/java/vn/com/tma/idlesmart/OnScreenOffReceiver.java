package vn.com.tma.idlesmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import vn.com.tma.idlesmart.Utils.PrefUtils;

public class OnScreenOffReceiver extends BroadcastReceiver {
    private static final String TAG = "IdleSmart.OnScreenOff";

    public void onReceive(Context context, Intent intent) {
        AppContext ctx;
        if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
            Log.i(TAG, "Received: ACTION_SCREEN_OFF");
            ctx = (AppContext) context.getApplicationContext();
            if (PrefUtils.isKioskModeActive(ctx)) {
                wakeUpDevice(ctx);
            }
        } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
            Log.i(TAG, "Received: ACTION_SCREEN_ON");
            ctx = (AppContext) context.getApplicationContext();
            if (PrefUtils.isKioskModeActive(ctx)) {
                wakeUpDevice(ctx);
            }
        }
    }

    private void wakeUpDevice(AppContext context) {
        Log.i(TAG, "wakeUpDevice!");
        WakeLock wakeLock = context.getWakeLock();
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        wakeLock.acquire();
    }
}
