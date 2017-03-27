package vn.com.tma.idlesmart;

import android.util.Log;
import java.util.Objects;

public class Features {
    private static final int FEATURE_ALIAS_MAX = 200;
    public static final int FEATURE_CODE_MAX = 100;
    public static final byte FEATURE_DEBUG = (byte) 2;
    public static final byte FEATURE_DISABLE = (byte) 0;
    public static final byte FEATURE_ENABLE = (byte) 1;
    private static final int FEATURE_ENGINE_HOURS = 1;
    private static final int FEATURE_IDLE_TIMER_OVERRIDE = 2;
    private static final int FEATURE_TEST = 0;
    private static final String TAG = "IdleSmart.Features";
    private static final int[] feature_alias_code;
    private static final String[] feature_alias_name;
    private static int feature_count;
    public static byte[] feature_status;
    public static int[] feature_value;

    static {
        feature_status = new byte[FEATURE_CODE_MAX];
        feature_value = new int[FEATURE_CODE_MAX];
        feature_alias_name = new String[FEATURE_ALIAS_MAX];
        feature_alias_code = new int[FEATURE_ALIAS_MAX];
        feature_count = FEATURE_TEST;
    }

    public static void initFeatureCodeTable() {
        Log.i(TAG, "****** initFeatureCodeTable");
        resetFeatureCodes();
        feature_count = FEATURE_TEST;
        addFeature("Test", FEATURE_TEST);
        addFeature("FedEx", FEATURE_ENGINE_HOURS);
        addFeature("EngineHours", FEATURE_ENGINE_HOURS);
        addFeature("IdleTimer", FEATURE_IDLE_TIMER_OVERRIDE);
    }

    public static void resetFeatureCodes() {
        Log.i(TAG, "****** resetFeatureCodes");
        for (int i = FEATURE_TEST; i < FEATURE_CODE_MAX; i += FEATURE_ENGINE_HOURS) {
            feature_status[i] = FEATURE_DISABLE;
            feature_value[i] = FEATURE_TEST;
        }
    }

    private static void addFeature(String feature_name, int feature_code) {
        if (feature_count < FEATURE_ALIAS_MAX) {
            feature_alias_name[feature_count] = feature_name.trim().toUpperCase();
            feature_alias_code[feature_count] = feature_code;
            feature_status[feature_code] = FEATURE_DISABLE;
            feature_value[feature_code] = FEATURE_TEST;
            feature_count += FEATURE_ENGINE_HOURS;
        }
    }

    private static int findFeature(String feature) {
        String temp = feature.trim().toUpperCase();
        if (temp.isEmpty()) {
            return -1;
        }
        for (int i = FEATURE_TEST; i < feature_count; i += FEATURE_ENGINE_HOURS) {
            if (Objects.equals(feature_alias_name[i], temp)) {
                return feature_alias_code[i];
            }
        }
        return -1;
    }

    public static boolean setFeature(String feature_alias_name, int value) {
        int feature_code = findFeature(feature_alias_name);
        if (feature_code < 0) {
            return false;
        }
        feature_status[feature_code] = FEATURE_ENABLE;
        feature_value[feature_code] = 65535 & value;
        return true;
    }

    public static int getFeatureCode(String feature_alias_name) {
        int feature_code = findFeature(feature_alias_name);
        if (feature_code >= 0) {
            return feature_value[feature_code] & 65535;
        }
        return -1;
    }

    public static boolean isFeatureEnabled(String feature_alias_name) {
        int feature_code = findFeature(feature_alias_name);
        if (feature_code < 0) {
            return false;
        }
        if (feature_status[feature_code] == FEATURE_ENABLE) {
            return true;
        }
        return false;
    }

    public static boolean isFeatureCodeEnabled(int feature_number) {
        if (feature_number >= FEATURE_CODE_MAX || feature_status[feature_number] != FEATURE_ENABLE) {
            return false;
        }
        return true;
    }

    public static int FeatureCode(int feature_number) {
        if (feature_number >= FEATURE_CODE_MAX || feature_status[feature_number] != FEATURE_ENGINE_HOURS) {
            return FEATURE_TEST;
        }
        return feature_value[feature_number] & 65535;
    }

    public static void parseFeatureList(String feature_list) {
        Log.i(TAG, "****** feature_list= \"" + feature_list + "\"");
        String temp = feature_list.trim().toUpperCase();
        if (temp.isEmpty()) {
            resetFeatureCodes();
            return;
        }
        int next = FEATURE_TEST;
        while (next < temp.length()) {
            String feature;
            String feature_name;
            int feature_value;
            int delim = feature_list.indexOf(",", next);
            if (delim >= 0) {
                feature = feature_list.substring(next, delim);
                next = delim + FEATURE_ENGINE_HOURS;
            } else {
                feature = feature_list.substring(next);
                next = 9999;
            }
            int equate = feature.indexOf("=");
            if (equate < 0 || equate >= feature.length() - 1) {
                feature_name = feature;
                feature_value = FEATURE_TEST;
            } else {
                feature_name = feature.substring(FEATURE_TEST, equate).trim().toUpperCase();
                try {
                    feature_value = Integer.parseInt(feature.substring(equate + FEATURE_ENGINE_HOURS));
                } catch (NumberFormatException e) {
                    feature_value = FEATURE_TEST;
                }
            }
            if (setFeature(feature_name, feature_value)) {
                Log.i(TAG, "Feature code set: " + feature_name + " = " + feature_value);
            } else {
                Log.i(TAG, "Feature code does not exist: " + feature_name);
            }
        }
    }

    public static boolean ValidateFeatureIdentityList(String feature_list) {
        Log.d(TAG, "****** FeatureIdentityList = " + feature_list);
        String temp = feature_list.trim().toUpperCase();
        if (temp.isEmpty()) {
            resetFeatureCodes();
            return false;
        }
        int next = FEATURE_TEST;
        while (next < temp.length()) {
            String condition;
            int delim = feature_list.indexOf(",", next);
            if (delim == 0) {
                delim = feature_list.indexOf("|", next);
            }
            if (delim >= 0) {
                condition = feature_list.substring(next, delim - next);
                Log.d(TAG, "****** Feature condition = " + condition);
                next = delim + FEATURE_ENGINE_HOURS;
            } else {
                condition = feature_list.substring(next);
                next = 9999;
            }
            if (isExpr(condition)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isExpr(String expr) {
        Log.d(TAG, "******           FeatureExpr = " + expr);
        int oper = expr.indexOf("&");
        if (oper < 0 || oper >= expr.length() - 1) {
            oper = expr.indexOf("=");
            if (oper < 0 || oper >= expr.length() - 1) {
                return isFeatureUsed(expr);
            }
            return isIdentityMatch(expr);
        }
        String expr1 = expr.substring(FEATURE_TEST, oper);
        String expr2 = expr.substring(oper + FEATURE_ENGINE_HOURS, expr.length() - (oper + FEATURE_ENGINE_HOURS));
        Log.d(TAG, "******               Expr1 = " + expr1);
        Log.d(TAG, "******               Expr2 = " + expr2);
        if (isExpr(expr1) && isExpr(expr2)) {
            return true;
        }
        return false;
    }

    private static boolean isFeatureUsed(String feature) {
        if (!isFeatureEnabled(feature)) {
            return false;
        }
        Log.i(TAG, "Feature is in use: " + feature);
        return true;
    }

    private static boolean isIdentityMatch(String identity) {
        int equate = identity.indexOf("=");
        if (equate >= 0 && equate < identity.length() - 1) {
            String id_string = identity.substring(FEATURE_TEST, equate).trim().toUpperCase();
            String value_string = identity.substring(equate + FEATURE_ENGINE_HOURS).trim().toUpperCase();
            if (id_string.equals("FLEET")) {
                if (value_string.equals(MainActivity.Gateway_Fleet.trim().toUpperCase())) {
                    Log.d(TAG, "******               FLEET Matches");
                    return true;
                }
            } else if ((id_string.equals("VEHICLE") || id_string.equals("TRUCK") || id_string.equals("VIN")) && value_string.equals(MainActivity.Gateway_VIN.trim().toUpperCase())) {
                Log.d(TAG, "******               VEHICLE Matches");
                return true;
            }
        }
        Log.d(TAG, "******               NO FLEET or VEHICLE match");
        return false;
    }
}
