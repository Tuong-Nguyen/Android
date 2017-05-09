package vn.com.tma.idlesmart.Utils;

import android.util.Log;

import vn.com.tma.idlesmart.AccessoryControl;
import vn.com.tma.idlesmart.Features;

/**
 * Created by ntmhanh on 5/8/2017.
 */

public class InformationSender {
    private static final String TAG = "IdleSmart.Main";
    public AccessoryControl accessoryControl;

    public InformationSender(AccessoryControl accessoryControl) {
        this.accessoryControl = accessoryControl;
    }

    /**
     * Send Command String
     * @param cmd
     * @param str
     */

    public void sendCmdString(int cmd, String str) {
        Log.i(TAG, "sendCmdString..cmd:" + cmd + " string:" + str);
        byte[] bytestring = new byte[81];
        int i = 0;
        while (i < str.length()) {
            bytestring[i] = (byte) str.charAt(i);
            if (bytestring[i] == 0) {
                break;
            }
            i += 1;
        }
        bytestring[i] = (byte) 0;
        this.accessoryControl.writeCommandBlock(cmd, i, bytestring);
    }

    /**
     * Send Features
     */
    public void sendFeatures() {
        int i;
        Log.i(TAG, "sendFeatures..");
        byte[] bytestring = new byte[202];
        for (i = 0; i < 100; i += 1) {
            bytestring[i * 2] = (byte) (Features.feature_value[i] & 255);
            bytestring[(i * 2) + 1] = (byte) ((Features.feature_value[i] & 65280) >> 8);
        }
        this.accessoryControl.writeCommandBlock(AccessoryControl.APIDATA_FEATURE_VALUES, 200, bytestring);
        for (i = 0; i < 100; i += 1) {
            bytestring[i] = (byte) (Features.feature_status[i] & 255);
        }
        this.accessoryControl.writeCommandBlock(AccessoryControl.APIDATA_FEATURE_CODES, 100, bytestring);
        for (i = 0; i < 5; i += 1) {
            Log.i(TAG, "****** Feature Code[" + i + "]: status=" + ((byte) (Features.feature_status[i] & 255)) + "   value=" + Features.feature_value[i]);
        }
    }

    /**
     * Send fleet
     * @param fleet
     */

    public void sendFleet(String fleet) {
        Log.i(TAG, "sendFleet.." + fleet);
        int i = 0;
        byte[] bytestring = new byte[41];
        if (fleet.length() < 41) {
            i = 0;
            while (i < fleet.length()) {
                bytestring[i] = (byte) fleet.charAt(i);
                if (bytestring[i] == 0) {
                    break;
                }
                i += 1;
            }
        }
        bytestring[i] = (byte) 0;
        this.accessoryControl.writeCommandBlock(AccessoryControl.APIDATA_FLEET, i, bytestring);
    }

    /**
     * Send VIN
     * @param vin
     */
    public void sendVIN(String vin) {
        Log.i(TAG, "sendVIN.." + vin);
        int i = 0;
        byte[] bytestring = new byte[41];
        if (vin.length() < 41) {
            i = 0;
            while (i < vin.length()) {
                bytestring[i] = (byte) vin.charAt(i);
                if (bytestring[i] == 0) {
                    break;
                }
                i += 1;
            }
        }
        bytestring[i] = (byte) 0;
        this.accessoryControl.writeCommandBlock(AccessoryControl.APICMD_VIN, i, bytestring);
    }

    /**
     * write CommandMaintenance Feature
     * @param isEnable
     * @param value
     * @param APIdebug
     */
    public void writeMaintenanceFeatureCommand(boolean isEnable, int value, int APIdebug ){
        byte[] data = new byte[2];
        if (isEnable){
            data[0] = (byte) ((value >> 8) & 255);
            data[1] = (byte) (value & 255);
            this.accessoryControl.writeCommand(APIdebug, data[0], data[1]);
        } else{
            this.accessoryControl.writeCommand(APIdebug, 0, 0);
        }

    }
}
