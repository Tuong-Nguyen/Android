package vn.com.tma.idlesmart;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connectivity {
    public static NetworkInfo getNetworkInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected();
    }

    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null && info.isConnected() && info.getType() == 1) {
            return true;
        }
        return false;
    }

    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected() && info.getType() == 0;
    }

    public static boolean isConnectedFast(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype());
    }

    public static boolean isConnectionFast(int type, int subType) {
        if (type == 1) {
            return true;
        }
        if (type != 0) {
            return false;
        }
        switch (subType) {
            case httpClient.PHONEHOME_OK /*1*/:
                return false;
            case httpClient.PHONEHOME_GATEWAY_UPDATE /*2*/:
                return false;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
            case httpClient.PHONEHOME_NONE /*5*/:
            case Params.PasswordType /*6*/:
            case Params.PARAM_VoltageSetPoint /*8*/:
            case Params.PARAM_EngineRunTime /*9*/:
            case Params.PARAM_IdealCoolantTemp /*10*/:
            case Params.PARAM_TemperatureSetPoint /*12*/:
            case Params.PARAM_HoursBetweenStart /*13*/:
            case Params.PARAM_DimTabletScreen /*14*/:
            case Params.PARAM_AudibleSound /*15*/:
                return true;
            case httpClient.PHONEHOME_APK_PENDING /*4*/:
                return false;
            case Params.ProcessType /*7*/:
                return false;
            case Params.PARAM_MinCoolantTemp /*11*/:
                return false;
            default:
                return false;
        }
    }
}
