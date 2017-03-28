package vn.com.tma.idlesmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class OnBootReceiver extends BroadcastReceiver {
    private static final String TAG = "IdleSmart.OnBootRcvr";
    private static final String TargetPackageName = "com.idlesmarter.aoa";

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "------------------------------------------------------------------------");
        Log.i(TAG, "OnBoot Received");
        Log.i(TAG, "Start IdleSmart Activity..");
        Intent myIntent = new Intent(context, MainActivity.class);
        myIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
        Log.i(TAG, "   IdleSmart has been started");
        Log.i(TAG, "------------------------------------------------------------------------");
    }
}
