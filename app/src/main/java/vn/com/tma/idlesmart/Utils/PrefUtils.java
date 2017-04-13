package vn.com.tma.idlesmart.Utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import vn.com.tma.idlesmart.MainActivity;

public class PrefUtils {
    public static final String PREF_APK_UPDATE = "pref_apk_update";
    public static final int PREF_APK_UPDATE_IDLE = 0;
    public static final int PREF_APK_UPDATE_RUNNING = 1;
    private static final String PREF_KIOSK_MODE = "pref_kiosk_mode";
    private static final String PREF_SERVER_UPDATE_VERSION = "pref_server_update_version";
    private static final String TAG = "IdleSmart.PrefUtils";

    public static boolean isKioskModeActive(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_KIOSK_MODE, false);
    }

    public static void setKioskModeActive(boolean active, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_KIOSK_MODE, active).commit();
        if (!MainActivity.DebugLog) {
            return;
        }
        if (active) {
            Log.i(TAG, "Entering KIOSK_MODE");
        } else {
            Log.i(TAG, "Exit KIOSK_MODE");
        }
    }

    public static int getApkUpdateState(Context context) {
        int result = PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_APK_UPDATE, PREF_APK_UPDATE_IDLE);
        if (MainActivity.DebugLog) {
            Log.i(TAG, "get PREF_APK_UPDATE:" + result + "=>" + (result == PREF_APK_UPDATE_RUNNING ? "RUNNING" : "IDLE"));
        }
        return result;
    }

    public static void setApkUpdateState(int state, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_APK_UPDATE, state).commit();
        if (MainActivity.DebugLog) {
            Log.i(TAG, "set PREF_APK_UPDATE_STATE=" + state + "=>" + (state == PREF_APK_UPDATE_RUNNING ? "RUNNING" : "IDLE"));
        }
    }

    public static String getServerUpdateVersion(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SERVER_UPDATE_VERSION, "").trim();
    }

    public static void setServerUpdateVersion(String version, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SERVER_UPDATE_VERSION, version).commit();
        if (MainActivity.DebugLog) {
            Log.i(TAG, "SERVER_UPDATE_VERSION = " + version);
        }
    }
}
