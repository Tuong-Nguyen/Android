package vn.com.tma.idlesmart;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.hardware.usb.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import vn.com.tma.idlesmart.Utils.DatumUtils;
import vn.com.tma.idlesmart.Utils.LogFile;

public class AccessoryControl {
    private static final String ACC_MANUF = "Idle Smart LLC";
    private static final String ACC_MODEL = "Idle Smart Gateway";
    public static final String ACTION_USB_PERMISSION = "com.idlesmarter.aoa.MainActivity.action.USB_PERMISSION";
    public static final int APICAN_BASE = 192;
    public static final int APICAN_BATTERY_VOLTAGE = 197;
    public static final int APICAN_ELECTRICAL_VOLTAGE = 196;
    public static final int APICAN_ENGINE_COOLANT_TEMP = 193;
    public static final int APICAN_ENGINE_OIL_PRESSURE = 195;
    public static final int APICAN_ENGINE_SPEED = 194;

    // region Command ID
    public static final int APICMD_BASE = 0;
    public static final int APICMD_CONNECT = 1;
    public static final int APICMD_DISCONNECT = 2;
    public static final int APICMD_PING = 3;
    public static final int APICMD_RESET = 4;
    public static final int APICMD_SYNC = 5;
    public static final int APICMD_STATUS = 6;
    public static final int APICMD_HW_MODEL = 7;
    public static final int APICMD_OS_VERSION = 8;
    public static final int APICMD_FW_VERSION = 9;
    public static final int APICMD_API_VERSION = 10;
    public static final int APICMD_QUERY = 11;
    public static final int APICMD_UL = 12;
    public static final int APICMD_DL = 13;
    public static final int APICMD_VIN = 14;
    public static final int APICMD_ACTIVATE = 15;
    public static final int APICMD_TESTMODE = 16;
    public static final int APICMD_DATETIME = 17;
    public static final int APICMD_AUTODIM = 18;
    public static final int APICMD_AUDIBLE = 19;
    public static final int APICMD_POWERON = 20;
    public static final int APICMD_POWEROFF = 21;
    public static final int APICMD_ENGINE_OFF = 22;
    public static final int APICMD_ACCESSORY = 23;
    public static final int APICMD_IGNITION = 24;
    public static final int APICMD_START = 25;
    public static final int APICMD_EXT_RELAY = 26;
    public static final int APICMD_PASSWORD_ENABLE = 27;
    public static final int APICMD_PASSWORD = 28;
    public static final int APICMD_STOP = 29;
    public static final int APICMD_ENGINE_IDLE_RPM = 30;
    public static final int APICMD_ENGINE_RESTART_INTERVAL = 31;
    public static final int APICMD_AUTO_SHUTOFF_TIMEOUT = 32;
    public static final int APICMD_ALERT_ACK = 33;
    public static final int APICMD_ALERT_CLEARALL = 34;
    public static final int APICMD_WARNING_ACK = 35;
    public static final int APICMD_WARNING_CLEARALL = 36;
    public static final int APICMD_FREEZE = 37;
    public static final int APICMD_GUID = 38;
    public static final int APICMD_REGISTER = 39;
    public static final int APICMD_CABIN_COMFORT_ENABLE = 40;
    public static final int APICMD_CABIN_TEMP_SETPOINT = 41;
    public static final int APICMD_CABIN_TEMP_RANGE = 42;
    public static final int APICMD_AMBIENT_TEMP_SETPOINT = 43;
    public static final int APICMD_AMBIENT_TEMP_RANGE = 44;
    public static final int APICMD_SYSTEMTIMER = 45;
    public static final int APICMD_ENGINESTARTONCABINTEMPCHANGE = 46;
    public static final int APICMD_UNCOMFORTABLERESTARTINTERVAL = 47;
    public static final int APICMD_DRIVER_TEMP_COMMON = 48;
    public static final int APICMD_GET_VEHICLE_INFO = 49;
    public static final int APICMD_BATTERY_MONITOR_ENABLE = 50;
    public static final int APICMD_BATTERY_MONITOR_VOLTAGE = 51;
    public static final int APICMD_BATTERY_MONITOR_RUNTIME = 52;
    public static final int APICMD_SYNC_START = 53;
    public static final int APICMD_SYNC_TTL = 54;
    public static final int APICMD_COLD_WEATHER_GUARD_ENABLE = 55;
    public static final int APICMD_COLD_WEATHER_GUARD_START_TEMP = 56;

    public static final int APICMD_COLD_WEATHER_GUARD_RESTART_INTERVAL = 58;
    public static final int APICMD_COLD_WEATHER_GUARD_MIN_COOLANT = 59;
    public static final int APICMD_COLD_WEATHER_GUARD_IDEAL_COOLANT = 60;
    // endregion

    public static final int APIDATA_ACCESSORY_FB = 130;
    public static final int APIDATA_ACTIVATION_CODE = 169;
    public static final int APIDATA_AMBIENT_TEMP = 137;
    public static final int APIDATA_BASE = 128;
    public static final int APIDATA_BATTERY_VOLTAGE = 138;
    public static final int APIDATA_CABIN_TEMP = 136;
    public static final int APIDATA_CAN_ERRORCNT = 140;
    public static final int APIDATA_CAN_HARDERRORCNT = 139;
    public static final int APIDATA_DATATIME = 141;
    public static final int APIDATA_DRIVER = 171;
    public static final int APIDATA_EngineCrankAttempts = 152;
    public static final int APIDATA_FEATURE_CODES = 181;
    public static final int APIDATA_FEATURE_VALUES = 182;
    public static final int APIDATA_FLEET = 170;
    public static final int APIDATA_FLEET_CABIN_COMFORT_ENABLE = 159;
    public static final int APIDATA_FLEET_CABIN_TEMP_SETPOINT = 160;
    public static final int APIDATA_FW_CHECKSUM = 190;
    public static final int APIDATA_FW_DATA = 189;
    public static final int APIDATA_FW_HEADER = 188;
    public static final int APIDATA_FW_TRA = 191;
    public static final int APIDATA_IGNITION_FB = 131;
    public static final int APIDATA_LOG_COUNT = 178;
    public static final int APIDATA_LOG_ENTRY = 179;
    public static final int APIDATA_MaxRunTime = 151;
    public static final int APIDATA_SAFETY_SW1 = 133;
    public static final int APIDATA_SAFETY_SW2 = 134;
    public static final int APIDATA_SAFETY_SW3 = 135;
    public static final int APIDATA_SCHEDCOUNTER = 129;
    public static final int APIDATA_SERVER_ROUTE = 180;
    public static final int APIDATA_START_FB = 132;
    public static final int APIDATA_SYNC_LAST = 142;
    public static final int APIDATA_SYNC_NEXT = 143;
    public static final int APIDATA_TIMEREMAINING = 158;
    public static final int APIDATA_VIN = 168;
    public static final int APIDATA_lastBatteryMonitorStartTime = 155;
    public static final int APIDATA_lastColdWeatherGuardStartTime = 156;
    public static final int APIDATA_lastStartTime = 148;
    public static final int APIDATA_lastStopTime = 149;
    public static final int APIDATA_lastTempModeStartTime = 153;
    public static final int APIDATA_lastTimerModeStartTime = 154;
    public static final int APIDATA_lastUncomfortableStartTime = 157;
    public static final int APIDATA_llEngineTimer = 150;
    public static final int APIDATUM = 213;
    public static final int APIDEBUG1 = 203;
    public static final int APIDEBUG10 = 212;
    public static final int APIDEBUG2 = 204;
    public static final int APIDEBUG3 = 205;
    public static final int APIDEBUG4 = 206;
    public static final int APIDEBUG5 = 207;
    public static final int APIDEBUG6 = 208;
    public static final int APIDEBUG7 = 209;
    public static final int APIDEBUG8 = 210;
    public static final int APIDEBUG9 = 211;

    // region Event ID
    public static final int APIEVENT_ACTIVATED = 80;
    public static final int APIEVENT_ALERT = 94;
    public static final int APIEVENT_API_VERSION = 74;
    public static final int APIEVENT_AUTOSHUTOFF = 86;
    public static final int APIEVENT_BASE = 64;
    public static final int APIEVENT_CANBUS_RUNNING = 75;
    public static final int APIEVENT_CANLOG = 77;
    public static final int APIEVENT_CLEARALERT = 95;
    public static final int APIEVENT_CLEARALLALERTS = 96;
    public static final int APIEVENT_CLEARALLWARNINGS = 99;
    public static final int APIEVENT_CLEARWARNING = 98;
    public static final int APIEVENT_CONNECT = 65;
    public static final int APIEVENT_CURRENT_MODE = 78;
    public static final int APIEVENT_DATUM = 91;
    public static final int APIEVENT_DEEPSLEEP = 85;
    public static final int APIEVENT_DISCONNECT = 66;
    public static final int APIEVENT_DRIVING_MODE = 88;
    public static final int APIEVENT_EMULATOR = 70;
    public static final int APIEVENT_ENABLEOPERATION = 87;
    public static final int APIEVENT_FW_VERSION = 73;
    public static final int APIEVENT_GATEWAY_SERIALID = 79;
    public static final int APIEVENT_HANDLER_CONNECT = 124;
    public static final int APIEVENT_HANDLER_DISCONNECT = 125;
    public static final int APIEVENT_HANDLER_EXCEPTION = 127;
    public static final int APIEVENT_HANDLER_RESET = 126;
    public static final int APIEVENT_HW_VERSION = 72;
    public static final int APIEVENT_LAST_STARTEVENT = 89;
    public static final int APIEVENT_LDR_VERSION = 82;
    public static final int APIEVENT_LOG = 90;
    public static final int APIEVENT_PING = 67;
    public static final int APIEVENT_REQUEST_ACTIVATION = 76;
    public static final int APIEVENT_REQUPDATE = 81;
    public static final int APIEVENT_RESET = 68;
    public static final int APIEVENT_STATUS = 71;
    public static final int APIEVENT_SYNC = 69;
    public static final int APIEVENT_SYSTEM_STATUS = 84;
    public static final int APIEVENT_WARNING = 97;
    // endregion

    public static final int FLEET_ID_MAX = 41;
    public static final int SERVER_ROUTE_MAX = 41;
    public static final int SYNC_LAST_MAX = 8;
    private static final String TAG = "IdleSmart.CSAOA";
    public static final int USB_CLOSE_EXCEPTION = 2;
    public static final int USB_OPEN_EXCEPTION = 1;
    public static final int USB_READ_EXCEPTION = 3;
    public static final int USB_WRITE_EXCEPTION = 4;
    static final String UTCDATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final int VIN_ID_MAX = 41;
    public boolean UsbReaderRunning;
    private FileInputStream accInputStream;
    private FileOutputStream accOutputStream;
    private BufferedOutputStream canStream;
    private Context context;
    public BufferedOutputStream datumStream;
    private Handler handler;
    private boolean isOpen;
    public BufferedOutputStream logStream;
    private ParcelFileDescriptor parcelFileDescriptor;
    private boolean permissionRequested;
    private UsbManager usbManager;
    public UsbReader usbreader;
    private String fileName;


    public enum OpenStatus {
        CONNECTED,
        REQUESTING_PERMISSION,
        UNKNOWN_ACCESSORY,
        NO_ACCESSORY,
        NO_PARCEL
    }

    public class UsbReader implements Runnable {
        private static final String TAG = "IdleSmart.UsbReader";
        public boolean done;
        private BufferedInputStream inputStream;

        UsbReader(BufferedInputStream inputStreamParam) {
            this.done = false;
            this.inputStream = inputStreamParam;
        }

        public void run() {
            byte[] buffer = new byte[16384];
            boolean IOError = false;
            Log.i(TAG, "======> Thread(USB Receiver) started");
            AccessoryControl.this.UsbReaderRunning = true;
            while (!this.done) {
                int pos = AccessoryControl.APICMD_BASE;

                // read header (2 bytes)
                while (pos < AoaMessage.HEADER_LENGTH) {
                    int rdlth;
                    try {
                        rdlth = this.inputStream.read(buffer, pos, AoaMessage.HEADER_LENGTH - pos);
                        if (rdlth > 0) {
                            pos += rdlth;
                        }
                    } catch (IOException ioe) {
                        Log.e(TAG, "IOException during read1");
                        ioe.printStackTrace();
                        IOError = true;
                    }
                }

                if (!IOError) {
                    // Get message length = 2 bytes for header + length (represent by byte 0 and byte 1 -> interger value)
                    int reclth = AoaMessage.HEADER_LENGTH + (((buffer[AccessoryControl.APICMD_BASE] & 255) << 8) + (buffer[AccessoryControl.USB_OPEN_EXCEPTION] & 255));
                    while (pos < reclth) {
                        try {
                            int rdlth = this.inputStream.read(buffer, pos, reclth - pos);
                            if (rdlth > 0) {
                                pos += rdlth;
                            }
                        } catch (IOException ioe2) {
                            Log.e(TAG, "IOException during read2");
                            ioe2.printStackTrace();
                            IOError = true;
                        }
                    }

                    int numRead = reclth;
                    if (!IOError) {
                        int len = numRead - AoaMessage.HEADER_LENGTH;
                        int resp = buffer[AoaMessage.COMMAND_POSITION] & 255;
                        Message m;
                        byte[] ts;
                        int i;
                        switch (resp) {
                            case AccessoryControl.USB_CLOSE_EXCEPTION /*2*/:
                                Log.w(TAG, "(Recv)APICMD_DISCONNECT (ACK) from accessory");
                                this.done = true;
                                pos = numRead;
                                break;
                            case AccessoryControl.APICMD_AUTODIM /*18*/:
                            case AccessoryControl.APICMD_AUDIBLE /*19*/:
                            case AccessoryControl.APICMD_ENGINE_IDLE_RPM /*30*/:
                            case AccessoryControl.APICMD_ENGINE_RESTART_INTERVAL /*31*/:
                            case AccessoryControl.APICMD_AUTO_SHUTOFF_TIMEOUT /*32*/:
                            case AccessoryControl.APICMD_CABIN_COMFORT_ENABLE /*40*/:
                            case AccessoryControl.VIN_ID_MAX /*41*/:
                            case AccessoryControl.APICMD_CABIN_TEMP_RANGE /*42*/:
                            case AccessoryControl.APICMD_AMBIENT_TEMP_SETPOINT /*43*/:
                            case AccessoryControl.APICMD_AMBIENT_TEMP_RANGE /*44*/:
                            case AccessoryControl.APICMD_SYSTEMTIMER /*45*/:
                            case AccessoryControl.APICMD_ENGINESTARTONCABINTEMPCHANGE /*46*/:
                            case AccessoryControl.APICMD_UNCOMFORTABLERESTARTINTERVAL /*47*/:
                            case AccessoryControl.APICMD_DRIVER_TEMP_COMMON /*48*/:
                            case AccessoryControl.APICMD_BATTERY_MONITOR_ENABLE /*50*/:
                            case AccessoryControl.APICMD_BATTERY_MONITOR_VOLTAGE /*51*/:
                            case AccessoryControl.APICMD_BATTERY_MONITOR_RUNTIME /*52*/:
                            case AccessoryControl.APICMD_COLD_WEATHER_GUARD_ENABLE /*55*/:
                            case AccessoryControl.APICMD_COLD_WEATHER_GUARD_START_TEMP /*56*/:
                            case AccessoryControl.APICMD_COLD_WEATHER_GUARD_RESTART_INTERVAL /*58*/:
                            case AccessoryControl.APICMD_COLD_WEATHER_GUARD_MIN_COOLANT /*59*/:
                            case AccessoryControl.APICMD_COLD_WEATHER_GUARD_IDEAL_COOLANT /*60*/:
                            case AccessoryControl.APIEVENT_REQUPDATE /*81*/:
                            case AccessoryControl.APIEVENT_SYSTEM_STATUS /*84*/:
                            case AccessoryControl.APIEVENT_DRIVING_MODE /*88*/:
                            case AccessoryControl.APIEVENT_ALERT /*94*/:
                            case AccessoryControl.APIEVENT_CLEARALERT /*95*/:
                            case AccessoryControl.APIEVENT_CLEARALLALERTS /*96*/:
                            case AccessoryControl.APIEVENT_WARNING /*97*/:
                            case AccessoryControl.APIEVENT_CLEARWARNING /*98*/:
                            case AccessoryControl.APIEVENT_CLEARALLWARNINGS /*99*/:
                            case AccessoryControl.APIDATA_CABIN_TEMP /*136*/:
                            case AccessoryControl.APIDATA_AMBIENT_TEMP /*137*/:
                            case AccessoryControl.APIDATA_BATTERY_VOLTAGE /*138*/:
                            case AccessoryControl.APIDATA_TIMEREMAINING /*158*/:
                            case AccessoryControl.APIDATA_FLEET_CABIN_COMFORT_ENABLE /*159*/:
                            case AccessoryControl.APIDATA_FLEET_CABIN_TEMP_SETPOINT /*160*/:
                            case AccessoryControl.APICAN_ENGINE_COOLANT_TEMP /*193*/:
                                if (len >= AoaMessage.HEADER_LENGTH) {
                                    m = Message.obtain(AccessoryControl.this.handler, resp);
                                    m.arg1 = AccessoryControl.this.toInt(buffer[3], buffer[4]);
                                    AccessoryControl.this.handler.sendMessage(m);
                                }
                                break;
                            case AccessoryControl.APICMD_PASSWORD_ENABLE /*27*/:
                                MainActivity.PasswordEnable = AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION], buffer[AoaMessage.START_DATA_POSITION + 1]) != 0;
                                Log.i(TAG, "(Recv)APICMD_PASSWORD_ENABLE = " + MainActivity.PasswordEnable);
                                break;
                            case AccessoryControl.APICMD_PASSWORD /*28*/:
                                MainActivity.Password = AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION], buffer[AoaMessage.START_DATA_POSITION + 1]);
                                Log.i(TAG, "(Recv)APICMD_PASSWORD = " + MainActivity.Password);
                                break;
                            case AccessoryControl.APICMD_GUID /*38*/:
                                MainActivity.Gateway_Guid = AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION], buffer[AoaMessage.START_DATA_POSITION + 1]);
                                Log.i(TAG, "(Recv)APICMD_GUID = " + MainActivity.Gateway_Guid);
                                break;
                            case AccessoryControl.APICMD_SYNC_START /*53*/:
                                MainActivity.SyncStart = AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION], buffer[AoaMessage.START_DATA_POSITION + 1]);
                                Log.i(TAG, "(Recv)APIDATA_SYNC_START= " + MainActivity.SyncStart);
                                m = Message.obtain(AccessoryControl.this.handler, resp);
                                m.arg1 = AccessoryControl.APICMD_BASE;
                                AccessoryControl.this.handler.sendMessage(m);
                                break;
                            case AccessoryControl.APICMD_SYNC_TTL /*54*/:
                                MainActivity.SyncTTL = AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION], buffer[AoaMessage.START_DATA_POSITION + 1]);
                                Log.i(TAG, "(Recv)APIDATA_SYNC_TTL= " + MainActivity.SyncTTL);
                                m = Message.obtain(AccessoryControl.this.handler, resp);
                                m.arg1 = AccessoryControl.APICMD_BASE;
                                AccessoryControl.this.handler.sendMessage(m);
                                break;
                            case AccessoryControl.APIEVENT_SYNC /*69*/:
                                if (len >= AoaMessage.HEADER_LENGTH) {
                                    m = Message.obtain(AccessoryControl.this.handler, resp);
                                    m.arg1 = AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION], buffer[AoaMessage.START_DATA_POSITION + 1]);
                                    AccessoryControl.this.handler.sendMessage(m);
                                }
                                break;
                            case AccessoryControl.APIEVENT_HW_VERSION /*72*/:
                                MainActivity.Gateway_HWver = AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION], buffer[AoaMessage.START_DATA_POSITION + 1]);
                                Log.i(TAG, "(Recv)APIEVENT_HW_VERSION = " + MainActivity.Gateway_HWver);
                                if (MainActivity.Gateway_LDRversion.isEmpty()) {
                                    MainActivity.Gateway_LDRversion = "1.0.1";
                                }
                                break;
                            case AccessoryControl.APIEVENT_FW_VERSION /*73*/:
                                MainActivity.GatewayUpdatePending = false;
                                MainActivity.Gateway_FWversion = new String(buffer, AoaMessage.START_DATA_POSITION, len - 3);
                                Log.i(TAG, "(Recv)APIEVENT_FW_VERSION = " + MainActivity.Gateway_FWversion);
                                break;
                            case AccessoryControl.APIEVENT_API_VERSION /*74*/:
                                MainActivity.Gateway_APIversion = new String(buffer, AoaMessage.START_DATA_POSITION, len - 3);
                                Log.i(TAG, "(Recv)APIEVENT_API_VERSION = " + MainActivity.Gateway_APIversion);
                                break;
                            case AccessoryControl.APIEVENT_CANLOG /*77*/:
                                if (!MainActivity.aMaintEnable[MainActivity.MaintenanceFeature.LOG_FILE]) {
                                    break;
                                }
                                if (AccessoryControl.this.canStream == null) {
                                    break;
                                }
                                try {
                                    AccessoryControl.this.canStream.write(buffer, AoaMessage.START_DATA_POSITION, len - 1);
                                    AccessoryControl.this.canStream.flush();
                                    break;
                                } catch (Exception e) {
                                    Log.w(TAG, "IOException writing CANLog file - e=", e);
                                    break;
                                }
                            case AccessoryControl.APIEVENT_CURRENT_MODE /*78*/:
                            case AccessoryControl.APIDATA_ACCESSORY_FB /*130*/:
                            case AccessoryControl.APIDATA_IGNITION_FB /*131*/:
                            case AccessoryControl.APIDATA_START_FB /*132*/:
                            case AccessoryControl.APIDATA_SAFETY_SW1 /*133*/:
                            case AccessoryControl.APIDATA_SAFETY_SW2 /*134*/:
                            case AccessoryControl.APIDATA_SAFETY_SW3 /*135*/:
                                if (len >= AoaMessage.HEADER_LENGTH) {
                                    m = Message.obtain(AccessoryControl.this.handler, resp);
                                    m.arg1 = AccessoryControl.this.toInt((byte) 0, buffer[AoaMessage.START_DATA_POSITION]);
                                    AccessoryControl.this.handler.sendMessage(m);
                                }
                                break;
                            case AccessoryControl.APIEVENT_GATEWAY_SERIALID /*79*/:
                                MainActivity.Gateway_SerialID = new String(buffer, AoaMessage.START_DATA_POSITION, len - 3);
                                Log.i(TAG, "(Recv)APIEVENT_GATEWAY_SERIALID = " + MainActivity.Gateway_SerialID);
                                break;
                            case AccessoryControl.APIEVENT_ACTIVATED /*80*/:
                                if (len >= AoaMessage.HEADER_LENGTH) {
                                    m = Message.obtain(AccessoryControl.this.handler, resp);
                                    m.arg1 = AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION], buffer[AoaMessage.START_DATA_POSITION + 1]);
                                    AccessoryControl.this.handler.sendMessage(m);
                                }
                                break;
                            case AccessoryControl.APIEVENT_LDR_VERSION /*82*/:
                                MainActivity.Gateway_LDRversion = new String(buffer, AoaMessage.START_DATA_POSITION, len - 3);
                                Log.i(TAG, "(Recv)APIEVENT_LDR_VERSION = " + MainActivity.Gateway_LDRversion);
                                break;
                            case AccessoryControl.APIEVENT_LOG /*90*/:
                                fileName = "Log.bin";
                                LogFile logFile = new LogFile(fileName, TAG);
                                logFile.writeArray(buffer, len);
                                break;
                            case AccessoryControl.APIEVENT_DATUM /*91*/:
                                fileName = "Datum.bin";
                                LogFile datumFile = new LogFile(fileName, TAG);
                                datumFile.writeArray(buffer, len);
                                break;
                            case AccessoryControl.APIEVENT_HANDLER_DISCONNECT /*125*/:
                                this.done = true;
                                break;
                            case AccessoryControl.APIDATA_SYNC_LAST /*142*/:
                                Log.i(TAG, "(Recv)APIDATA_SYNC_LAST...");
                                if (len >= AccessoryControl.APICMD_FW_VERSION) {
                                    MainActivity.SyncLast_Status = buffer[AoaMessage.START_DATA_POSITION];
                                    MainActivity.SyncLast.set(Calendar.YEAR, 10);
                                    MainActivity.SyncLast.set(Calendar.YEAR, AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION + 1], buffer[AoaMessage.START_DATA_POSITION + 2]));
                                    MainActivity.SyncLast.set(Calendar.MONTH, AccessoryControl.this.toInt((byte) 0, buffer[AoaMessage.START_DATA_POSITION + 3]));
                                    MainActivity.SyncLast.set(Calendar.DATE, AccessoryControl.this.toInt((byte) 0, buffer[AoaMessage.START_DATA_POSITION + 4]));
                                    MainActivity.SyncLast.set(Calendar.HOUR_OF_DAY, AccessoryControl.this.toInt((byte) 0, buffer[AoaMessage.START_DATA_POSITION + 5]));
                                    MainActivity.SyncLast.set(Calendar.MINUTE, AccessoryControl.this.toInt((byte) 0, buffer[AoaMessage.START_DATA_POSITION + 6]));
                                    MainActivity.SyncLast.set(Calendar.SECOND, AccessoryControl.this.toInt((byte) 0, buffer[AoaMessage.START_DATA_POSITION + 7]));
                                    m = Message.obtain(AccessoryControl.this.handler, resp);
                                    m.arg1 = AccessoryControl.APICMD_BASE;
                                    AccessoryControl.this.handler.sendMessage(m);
                                }
                                break;
                            case AccessoryControl.APIDATA_SYNC_NEXT /*143*/:
                                MainActivity.SyncNext = AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION], buffer[AoaMessage.START_DATA_POSITION + 1]);
                                Log.i(TAG, "(Recv)APIDATA_SYNC_NEXT= " + MainActivity.SyncNext);
                                break;
                            case AccessoryControl.APIDATA_VIN /*168*/:
                                MainActivity.Gateway_VIN = new String(buffer, AoaMessage.START_DATA_POSITION, len - 3);
                                Log.i(TAG, "(Recv)APIDATA_VIN = " + MainActivity.Gateway_VIN);
                                break;
                            case AccessoryControl.APIDATA_ACTIVATION_CODE /*169*/:
                                MainActivity.ActivationCode = AccessoryControl.this.toInt(buffer[AoaMessage.START_DATA_POSITION], buffer[AoaMessage.START_DATA_POSITION + 1]);
                                Log.i(TAG, "(Recv)APIDATA_ACTIVATION_CODE = " + MainActivity.ActivationCode);
                                break;
                            case AccessoryControl.APIDATA_FLEET /*170*/:
                                MainActivity.Gateway_Fleet = new String(buffer, AoaMessage.START_DATA_POSITION, len - 3);
                                Log.i(TAG, "(Recv)APIDATA_FLEET = " + MainActivity.Gateway_Fleet);
                                break;
                            case AccessoryControl.APIDATA_SERVER_ROUTE /*180*/:
                                MainActivity.APIroute = new String(buffer, AoaMessage.START_DATA_POSITION, len - 3);
                                MainActivity.APIroute = MainActivity.APIroute.trim();
                                if (MainActivity.APIroute.trim().isEmpty()) {
                                    MainActivity.APIroute = MainActivity.DefaultAPIroute;
                                }
                                Log.i(TAG, "(Recv)APIDATA_SERVER_ROUTE = " + MainActivity.APIroute);
                                break;
                            case AccessoryControl.APIDATA_FEATURE_CODES /*181*/: // Each feature takes 3 bytes
                                Log.i(TAG, "(Recv)APIDATA_FEATURE_CODES...");
                                for (i = 0; i < 100; i += 1) {
                                    if (i < len - 1) {
                                        Features.feature_status[i] = buffer[i + 3];
                                    } else {
                                        Features.feature_status[i] = (byte) 0;
                                    }
                                }
                                break;
                            case AccessoryControl.APIDATA_FEATURE_VALUES /*182*/: // Each value takes 2 bytes
                                Log.i(TAG, "(Recv)APIDATA_FEATURE_VALUES...");
                                for (i = 0; i < 100; i += 1) {
                                    if (i * 2 < len - 1) {
                                        Features.feature_value[i] = AccessoryControl.this.toInt(buffer[(i * 2) + 3], buffer[(i * 2) + 4]);
                                    } else {
                                        Features.feature_value[i] = 0;
                                    }
                                }
                                break;
                            default:
                                Log.w(TAG, "Unknown command: " + resp);
                                break;
                        }
                    }
                    this.done = true;
                } else {
                    this.done = true;
                }
            }
            Log.i(TAG, "<======USB Receiver Thread is Exiting");
            AccessoryControl.this.UsbReaderRunning = false;
            if (IOError) {
                AccessoryControl.this.USBException(AccessoryControl.USB_READ_EXCEPTION);
            }
            AccessoryControl.this.usbreader = null;
        }

        public void close() {
            Log.d(TAG, "--> UsbReader.close()..");
            this.done = true;
        }
    }

    private AccessoryControl() {
        this.permissionRequested = false;
        this.isOpen = false;
        this.usbreader = null;
        this.logStream = null;
        this.datumStream = null;
        this.canStream = null;
        this.UsbReaderRunning = false;
    }

    public AccessoryControl(Context context, Handler handler) {
        this();
        this.handler = handler;
        this.context = context;
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public OpenStatus open() {
        Log.i(TAG, "   --> AccessoryControl.open..");
        return ValidConfig.status;
    }

    public OpenStatus open(UsbAccessory accessory) {
        Log.i(TAG, "   ==>AccessoryControl.open(accessory)..");
        if (this.isOpen) {
            Log.i(TAG, "   already open");
            Log.i(TAG, "   <== AccessoryControl.open(accessory)");
            return OpenStatus.CONNECTED;
        }
        // TODO Moved open datum file into writeArray() mehthod in LogFile
        //openDatumFile();
        openCANLogFile();
        Log.i(TAG, "   USB device is: " + accessory.getManufacturer() + " " + accessory.getModel());
        if (ACC_MANUF.equals(accessory.getManufacturer()) && ACC_MODEL.equals(accessory.getModel())) {
            Log.i(TAG, "   USB device is known and supported");
            Log.i(TAG, "   Open the USB connection..");
            this.parcelFileDescriptor = this.usbManager.openAccessory(accessory);
            if (this.parcelFileDescriptor != null) {

                // Open InputStream and OutputStream for communicating with accessory
                this.accOutputStream = new FileOutputStream(this.parcelFileDescriptor.getFileDescriptor());
                Log.d(TAG, "accOutputStream=" + this.accOutputStream.toString());
                this.accInputStream = new FileInputStream(this.parcelFileDescriptor.getFileDescriptor());
                Log.d(TAG, "accInputStream=" + this.accInputStream.toString());
                this.isOpen = true;

                // Start thread for reading from accessory
                this.usbreader = new UsbReader(new BufferedInputStream(this.accInputStream, 16384));
                new Thread(this.usbreader).start();
                Log.i(TAG, "   ---> Thread(receiver).start()..");
                //TODO Use write() in LogFile
                fileName = "Log.bin";
                LogFile logFile = new LogFile(fileName, TAG);
                logFile.write("Gateway Connected");
                MainActivity.demo_mode = false;
                Log.i(TAG, "   Send APICMD_CONNECT to Gateway..");
                writeCommand(USB_OPEN_EXCEPTION, APICMD_BASE, APICMD_BASE);
                Log.i(TAG, "   The USB is now connected");
                Log.i(TAG, "   <== AccessoryControl.open(accessory)");
                return OpenStatus.CONNECTED;
            }
            Log.e(TAG, "   Open Failed: Could not get a ParcelDescriptor");
            Log.i(TAG, "   <== AccessoryControl.open(accessory)");
            return OpenStatus.NO_PARCEL;
        }
        Log.i(TAG, "   Unknown accessory: " + accessory.getManufacturer() + ", " + accessory.getModel());
        Log.i(TAG, "   <== AccessoryControl.open(accessory)");
        return OpenStatus.UNKNOWN_ACCESSORY;
    }

    public void close() {
        Log.i(TAG, "==> AccessoryControl::close()..");
        if (this.isOpen) {
            //TODO Use write() in LogFile
            fileName = "Log.bin";
            LogFile logFile = new LogFile(fileName, TAG);
            logFile.write("   Gateway Disconnected");
            // TODO Moved close datum file into writeArray() mehthod in LogFile
           /* DatumUtils datumUtils = new DatumUtils(TAG);
            datumUtils.closeDatumFile(this.datumStream);*/
            closeCANLogFile();
            this.permissionRequested = false;
            this.isOpen = false;
            try {
                this.accInputStream.close();
                this.accInputStream = null;
                this.accOutputStream.close();
                this.accOutputStream = null;
                this.parcelFileDescriptor.close();
                Log.w(TAG, "   -----> close(USB) <-----");
            } catch (IOException ioe) {
                Log.w(TAG, "   Exception during close - ioe=", ioe);
            }
            Log.i(TAG, "<==close()");
            return;
        }
        Log.i(TAG, "   !isOpen");
        Log.i(TAG, "<==close()");
    }

    public void appIsClosing() {
        Log.i(TAG, "-->appIsClosing()..");
        if (this.isOpen) {
            Log.i(TAG, "   Close the Thread(UsbReader)..");
            if (this.usbreader != null) {
                this.usbreader.close();
            }
            Log.i(TAG, "   Wait up to 5 seconds for UsbReader to terminate..");
            long t = System.currentTimeMillis() + 5000;
            while (this.UsbReaderRunning && System.currentTimeMillis() < t) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    Log.w(TAG, "Exception during closing thread sleep - ie=" + ie);
                }
            }
            if (this.UsbReaderRunning) {
                Log.e(TAG, "   UsbReader is not terminating!");
            }
            Log.i(TAG, "<--appIsClosing()");
        }
    }

    /**
     * Write a command - This is a 5-byte string
     * @param cmd Command id
     * @param hiVal High value
     * @param loVal Low value
     */
    public void writeCommand(int cmd, int hiVal, int loVal) {
        byte[] buffer = new byte[5];
        if (this.isOpen) {
            Log.i(TAG, "AccessoryControl::writeCommand: " + Integer.toString(cmd) + "  isOpen? true");
            buffer[0] = (byte) 0;
            buffer[1] = (byte) 3;
            buffer[2] = (byte) (cmd & 255);
            buffer[3] = (byte) (hiVal & 255);
            buffer[4] = (byte) (loVal & 255);
            try {
                synchronized (this.accOutputStream) {
                    this.accOutputStream.write(buffer);
                }
            } catch (IOException ioe) {
                Log.w(TAG, "IOException writing USB command =" + Integer.toString(cmd) + " hiVal=" + Integer.toString(hiVal) + "  loVal=" + Integer.toString(loVal));
                Log.w(TAG, ioe);
                USBException(USB_WRITE_EXCEPTION);
            }
            Log.i(TAG, "writing USB command =" + Integer.toString(cmd) + " hiVal=" + Integer.toString(hiVal) + "  loVal=" + Integer.toString(loVal));
            return;
        }
        Log.w(TAG, "AccessoryControl::writeCommand: " + Integer.toString(cmd) + "  isOpen? false");
    }

    /**
     * Write command with data to accessory
     * @param cmd
     * @param dataLength
     * @param datablk
     */
    public void writeCommandBlock(int cmd, int dataLength, byte[] datablk) {
        byte[] buffer = new byte[(dataLength + AoaMessage.START_DATA_POSITION)];
        if (this.isOpen) {
            Log.i(TAG, "AccessoryControl::writeCommand: " + Integer.toString(cmd) + "  isOpen? true");
            int reclen = dataLength + 1; // data length + command (1 byte)
            buffer[0] = (byte) ((reclen >> 8) & 255);
            buffer[1] = (byte) (reclen & 255);
            buffer[AoaMessage.COMMAND_POSITION] = (byte) (cmd & 255);
            for (int i = 0; i < dataLength; i += 1) {
                buffer[i + AoaMessage.START_DATA_POSITION] = datablk[i];
            }
            try {
                synchronized (this.accOutputStream) {
                    this.accOutputStream.write(buffer, 0, dataLength + AoaMessage.START_DATA_POSITION);
                }
                return;
            } catch (IOException ioe) {
                Log.w(TAG, "IOException writing a USB data block - ioe=", ioe);
                USBException(4);
                return;
            }
        }
        Log.e(TAG, "AccessoryControl::writeCommand: " + Integer.toString(cmd) + "  isOpen? false");
    }

    public void USBException(int exception_id) {
        Log.e(TAG, "USBException: " + exception_id);
        Message m = Message.obtain(this.handler, APIEVENT_HANDLER_EXCEPTION);
        m.arg1 = exception_id;
        this.handler.sendMessage(m);
    }

    // TODO Moved openDatumFile to DatumUtils

  /*  public void openDatumFile() {
        if (this.datumStream == null) {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Logs");
                if (path.exists()) {
                    Log.i(TAG, "Log directory already exists");
                } else if (path.mkdirs()) {
                    Log.i(TAG, "Log directory created");
                } else {
                    Log.i(TAG, "ERROR: Cannot create Log directory");
                }
                try {
                    this.datumStream = new BufferedOutputStream(new FileOutputStream(new File(path, "Datum.bin"), true));
                    Log.i(TAG, "Datum file opened");
                    return;
                } catch (Exception e) {
                    Log.w(TAG, "IOException creating Datum file - ioe=", e);
                    return;
                }
            }
            Log.w(TAG, "Error opening Datum file - SDCard is not mounted");
        }
    }*/

    // TODO Move closeDatumFile to DatumUtils
  /*  public void closeDatumFile() {
        if (this.datumStream != null) {
            try {
                this.datumStream.flush();
                this.datumStream.close();
                this.datumStream = null;
            } catch (Exception e) {
                Log.w(TAG, "IOException closing Datum file - e=", e);
            }
        }
    }*/

    public void writefmtCANLogStream(String str) {
        if (MainActivity.aMaintEnable[MainActivity.MaintenanceFeature.LOG_FILE] && str != null && this.canStream != null) {
            int paddingCount = 16;
            try {
                int lth = str.length();
                int reccnt = lth / paddingCount;
                if (reccnt * paddingCount != lth) {
                    reccnt += 1;
                    str = padRight(str, (reccnt * paddingCount) - lth, ' ');
                }
                byte[] bytes = str.getBytes();
                for (int irec = 0; irec < reccnt; irec += 1) {
                    this.canStream.write(bytes, 0, paddingCount);
                }
                this.canStream.flush();
            } catch (Exception e) {
                Log.w(TAG, "IOException writing CANLog file - e=", e);
            }
        }
    }

    public void openCANLogFile() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "CANLogs");
            if (path.exists()) {
                Log.i(TAG, "CANLog directory already exists");
            } else if (path.mkdirs()) {
                Log.i(TAG, "CANLog directory created");
            } else {
                Log.i(TAG, "ERROR: Cannot create CANLog directory");
            }
            try {
                this.canStream = new BufferedOutputStream(new FileOutputStream(new File(path, "CANLog.bin"), true));
                Log.i(TAG, "CANLog file opened");
            } catch (Exception e) {
                Log.w(TAG, "IOException creating CANLog file - ioe=", e);
            }
        } else {
            Log.w(TAG, "Error opening CANLog file - SDCard is not mounted");
        }
        if (this.canStream != null) {
            try {
                this.canStream.write("================".getBytes(), 0, 16);
            } catch (Exception e2) {
                Log.w(TAG, "IOException writing CANLog header - ioe=", e2);
            }
        }
    }

    public void closeCANLogFile() {
        if (this.canStream != null) {
            try {
                this.canStream.flush();
                this.canStream.close();
                this.canStream = null;
            } catch (Exception e) {
                Log.w(TAG, "IOException closing CANLog file - e=", e);
            }
        }
    }

    private int toInt(byte hi, byte lo) {
        return ((((hi & 255) << 8) | (lo & 255)) << 16) >> 16;
    }

    private String padRight(String str, int size, char padChar) {
        StringBuffer padded = new StringBuffer(str);
        while (padded.length() < size) {
            padded.append(padChar);
        }
        return padded.toString();
    }

    public static Date getUTCdatetimeAsDate() {
        String utcdatestring = getUTCdatetimeAsString();
        if (utcdatestring == null) {
            return new Date(0);
        }
        return stringDateToDate(utcdatestring);
    }

    public static String getUTCdatetimeAsString() {
        return new SimpleDateFormat(UTCDATEFORMAT).format(new Date());
    }

    public static Date stringDateToDate(String StrDate) {
        Date dateToReturn = null;
        try {
            dateToReturn = new SimpleDateFormat(UTCDATEFORMAT).parse(StrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateToReturn;
    }
}
