package vn.com.tma.idlesmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnUpgradeReceiver extends BroadcastReceiver {
    private static final String TAG = "IdleSmart.OnUpgradeRcvr";
    private static final String TargetPackageName = "com.idlesmarter.aoa";

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "------------------------------------------------------------------------");
        Log.i(TAG, "Intent:" + intent);
        Log.i(TAG, "Action:" + intent.getAction());
        Log.i(TAG, "Data:  " + intent.getData().toString());
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            Log.i(TAG, "ACTION_PACKAGE_REPLACED");
        } else if (intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED")) {
            Log.i(TAG, "ACTION_MY_PACKAGE_REPLACED");
        } else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            Log.i(TAG, "ACTION_PACKAGE_REMOVED");
        } else if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            Log.i(TAG, "ACTION_PACKAGE_ADDED");
        }
        Log.i(TAG, "------------------------------------------------------------------------");
    }
}
