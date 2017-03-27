package vn.com.tma.idlesmart;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public static final int APICMD_ACCESSORY = 23;
    public static final int APICMD_ACTIVATE = 15;
    public static final int APICMD_ALERT_ACK = 33;
    public static final int APICMD_ALERT_CLEARALL = 34;
    public static final int APICMD_AMBIENT_TEMP_RANGE = 44;
    public static final int APICMD_AMBIENT_TEMP_SETPOINT = 43;
    public static final int APICMD_API_VERSION = 10;
    public static final int APICMD_AUDIBLE = 19;
    public static final int APICMD_AUTODIM = 18;
    public static final int APICMD_AUTO_SHUTOFF_TIMEOUT = 32;
    public static final int APICMD_BASE = 0;
    public static final int APICMD_BATTERY_MONITOR_ENABLE = 50;
    public static final int APICMD_BATTERY_MONITOR_RUNTIME = 52;
    public static final int APICMD_BATTERY_MONITOR_VOLTAGE = 51;
    public static final int APICMD_CABIN_COMFORT_ENABLE = 40;
    public static final int APICMD_CABIN_TEMP_RANGE = 42;
    public static final int APICMD_CABIN_TEMP_SETPOINT = 41;
    public static final int APICMD_COLD_WEATHER_GUARD_ENABLE = 55;
    public static final int APICMD_COLD_WEATHER_GUARD_IDEAL_COOLANT = 60;
    public static final int APICMD_COLD_WEATHER_GUARD_MIN_COOLANT = 59;
    public static final int APICMD_COLD_WEATHER_GUARD_RESTART_INTERVAL = 58;
    public static final int APICMD_COLD_WEATHER_GUARD_START_TEMP = 56;
    public static final int APICMD_CONNECT = 1;
    public static final int APICMD_DATETIME = 17;
    public static final int APICMD_DISCONNECT = 2;
    public static final int APICMD_DL = 13;
    public static final int APICMD_DRIVER_TEMP_COMMON = 48;
    public static final int APICMD_ENGINESTARTONCABINTEMPCHANGE = 46;
    public static final int APICMD_ENGINE_IDLE_RPM = 30;
    public static final int APICMD_ENGINE_OFF = 22;
    public static final int APICMD_ENGINE_RESTART_INTERVAL = 31;
    public static final int APICMD_EXT_RELAY = 26;
    public static final int APICMD_FREEZE = 37;
    public static final int APICMD_FW_VERSION = 9;
    public static final int APICMD_GET_VEHICLE_INFO = 49;
    public static final int APICMD_GUID = 38;
    public static final int APICMD_HW_MODEL = 7;
    public static final int APICMD_IGNITION = 24;
    public static final int APICMD_OS_VERSION = 8;
    public static final int APICMD_PASSWORD = 28;
    public static final int APICMD_PASSWORD_ENABLE = 27;
    public static final int APICMD_PING = 3;
    public static final int APICMD_POWEROFF = 21;
    public static final int APICMD_POWERON = 20;
    public static final int APICMD_QUERY = 11;
    public static final int APICMD_REGISTER = 39;
    public static final int APICMD_RESET = 4;
    public static final int APICMD_START = 25;
    public static final int APICMD_STATUS = 6;
    public static final int APICMD_STOP = 29;
    public static final int APICMD_SYNC = 5;
    public static final int APICMD_SYNC_START = 53;
    public static final int APICMD_SYNC_TTL = 54;
    public static final int APICMD_SYSTEMTIMER = 45;
    public static final int APICMD_TESTMODE = 16;
    public static final int APICMD_UL = 12;
    public static final int APICMD_UNCOMFORTABLERESTARTINTERVAL = 47;
    public static final int APICMD_VIN = 14;
    public static final int APICMD_WARNING_ACK = 35;
    public static final int APICMD_WARNING_CLEARALL = 36;
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
                while (pos < AccessoryControl.USB_CLOSE_EXCEPTION) {
                    int rdlth;
                    try {
                        rdlth = this.inputStream.read(buffer, pos, AccessoryControl.USB_CLOSE_EXCEPTION - pos);
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
                    int reclth = AccessoryControl.USB_CLOSE_EXCEPTION + (((buffer[AccessoryControl.APICMD_BASE] & 255) << AccessoryControl.SYNC_LAST_MAX) + (buffer[AccessoryControl.USB_OPEN_EXCEPTION] & 255));
                    while (pos < reclth) {
                        try {
                            rdlth = this.inputStream.read(buffer, pos, reclth - pos);
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
                        int len = numRead - AccessoryControl.USB_CLOSE_EXCEPTION;
                        int resp = buffer[AccessoryControl.USB_CLOSE_EXCEPTION] & 255;
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
                                if (len >= AccessoryControl.USB_CLOSE_EXCEPTION) {
                                    m = Message.obtain(AccessoryControl.this.handler, resp);
                                    m.arg1 = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]);
                                    AccessoryControl.this.handler.sendMessage(m);
                                }
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + AccessoryControl.USB_CLOSE_EXCEPTION;
                                break;
                            case AccessoryControl.APICMD_PASSWORD_ENABLE /*27*/:
                                MainActivity.PasswordEnable = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]) != 0;
                                Log.i(TAG, "(Recv)APICMD_PASSWORD_ENABLE = " + MainActivity.PasswordEnable);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APICMD_PASSWORD /*28*/:
                                MainActivity.Password = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]);
                                Log.i(TAG, "(Recv)APICMD_PASSWORD = " + MainActivity.Password);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APICMD_GUID /*38*/:
                                MainActivity.Gateway_Guid = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]);
                                Log.i(TAG, "(Recv)APICMD_GUID = " + MainActivity.Gateway_Guid);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APICMD_SYNC_START /*53*/:
                                MainActivity.SyncStart = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]);
                                Log.i(TAG, "(Recv)APIDATA_SYNC_START= " + MainActivity.SyncStart);
                                m = Message.obtain(AccessoryControl.this.handler, resp);
                                m.arg1 = AccessoryControl.APICMD_BASE;
                                AccessoryControl.this.handler.sendMessage(m);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APICMD_SYNC_TTL /*54*/:
                                MainActivity.SyncTTL = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]);
                                Log.i(TAG, "(Recv)APIDATA_SYNC_TTL= " + MainActivity.SyncTTL);
                                m = Message.obtain(AccessoryControl.this.handler, resp);
                                m.arg1 = AccessoryControl.APICMD_BASE;
                                AccessoryControl.this.handler.sendMessage(m);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIEVENT_SYNC /*69*/:
                                if (len >= AccessoryControl.USB_CLOSE_EXCEPTION) {
                                    m = Message.obtain(AccessoryControl.this.handler, resp);
                                    m.arg1 = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]);
                                    AccessoryControl.this.handler.sendMessage(m);
                                }
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIEVENT_HW_VERSION /*72*/:
                                MainActivity.Gateway_HWver = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]);
                                Log.i(TAG, "(Recv)APIEVENT_HW_VERSION = " + MainActivity.Gateway_HWver);
                                if (MainActivity.Gateway_LDRversion.isEmpty()) {
                                    MainActivity.Gateway_LDRversion = "1.0.1";
                                }
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + AccessoryControl.USB_CLOSE_EXCEPTION;
                                break;
                            case AccessoryControl.APIEVENT_FW_VERSION /*73*/:
                                MainActivity.GatewayUpdatePending = false;
                                MainActivity.Gateway_FWversion = new String(buffer, AccessoryControl.USB_READ_EXCEPTION, len - 3);
                                Log.i(TAG, "(Recv)APIEVENT_FW_VERSION = " + MainActivity.Gateway_FWversion);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIEVENT_API_VERSION /*74*/:
                                MainActivity.Gateway_APIversion = new String(buffer, AccessoryControl.USB_READ_EXCEPTION, len - 3);
                                Log.i(TAG, "(Recv)APIEVENT_API_VERSION = " + MainActivity.Gateway_APIversion);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIEVENT_CANLOG /*77*/:
                                if (!MainActivity.aMaintEnable[AccessoryControl.APICMD_BASE]) {
                                    break;
                                }
                                if (AccessoryControl.this.canStream == null) {
                                    break;
                                }
                                try {
                                    AccessoryControl.this.canStream.write(buffer, AccessoryControl.USB_READ_EXCEPTION, len - 1);
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
                                if (len >= AccessoryControl.USB_CLOSE_EXCEPTION) {
                                    m = Message.obtain(AccessoryControl.this.handler, resp);
                                    m.arg1 = AccessoryControl.this.toInt((byte) 0, buffer[AccessoryControl.USB_READ_EXCEPTION]);
                                    AccessoryControl.this.handler.sendMessage(m);
                                }
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + AccessoryControl.USB_CLOSE_EXCEPTION;
                                break;
                            case AccessoryControl.APIEVENT_GATEWAY_SERIALID /*79*/:
                                MainActivity.Gateway_SerialID = new String(buffer, AccessoryControl.USB_READ_EXCEPTION, len - 3);
                                Log.i(TAG, "(Recv)APIEVENT_GATEWAY_SERIALID = " + MainActivity.Gateway_SerialID);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIEVENT_ACTIVATED /*80*/:
                                if (len >= AccessoryControl.USB_CLOSE_EXCEPTION) {
                                    m = Message.obtain(AccessoryControl.this.handler, resp);
                                    m.arg1 = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]);
                                    AccessoryControl.this.handler.sendMessage(m);
                                }
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIEVENT_LDR_VERSION /*82*/:
                                MainActivity.Gateway_LDRversion = new String(buffer, AccessoryControl.USB_READ_EXCEPTION, len - 3);
                                Log.i(TAG, "(Recv)APIEVENT_LDR_VERSION = " + MainActivity.Gateway_LDRversion);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIEVENT_LOG /*90*/:
                                if (AccessoryControl.this.logStream == null) {
                                    break;
                                }
                                try {
                                    ts = AccessoryControl.getUTCdatetimeAsString().getBytes();
                                    AccessoryControl.this.logStream.write(ts, AccessoryControl.APICMD_BASE, ts.length);
                                    AccessoryControl.this.logStream.write(AccessoryControl.APICMD_AUTO_SHUTOFF_TIMEOUT);
                                    AccessoryControl.this.logStream.write(buffer, AccessoryControl.USB_READ_EXCEPTION, (len - 1) - 2);
                                    AccessoryControl.this.logStream.write(AccessoryControl.APICMD_DL);
                                    AccessoryControl.this.logStream.write(AccessoryControl.APICMD_API_VERSION);
                                    AccessoryControl.this.logStream.flush();
                                    break;
                                } catch (Exception e2) {
                                    Log.w(TAG, "IOException writing Log file - e=", e2);
                                    break;
                                }
                            case AccessoryControl.APIEVENT_DATUM /*91*/:
                                if (AccessoryControl.this.datumStream == null) {
                                    break;
                                }
                                try {
                                    ts = AccessoryControl.getUTCdatetimeAsString().getBytes();
                                    AccessoryControl.this.datumStream.write(ts, AccessoryControl.APICMD_BASE, ts.length);
                                    AccessoryControl.this.datumStream.write(AccessoryControl.APICMD_AUTO_SHUTOFF_TIMEOUT);
                                    AccessoryControl.this.datumStream.write(buffer, AccessoryControl.USB_READ_EXCEPTION, len - 3);
                                    AccessoryControl.this.datumStream.write(AccessoryControl.APICMD_DL);
                                    AccessoryControl.this.datumStream.write(AccessoryControl.APICMD_API_VERSION);
                                    AccessoryControl.this.datumStream.flush();
                                    break;
                                } catch (Exception e22) {
                                    Log.w(TAG, "IOException writing Datum file - e=", e22);
                                    break;
                                }
                            case AccessoryControl.APIEVENT_HANDLER_DISCONNECT /*125*/:
                                this.done = true;
                                break;
                            case AccessoryControl.APIDATA_SYNC_LAST /*142*/:
                                Log.i(TAG, "(Recv)APIDATA_SYNC_LAST...");
                                if (len >= AccessoryControl.APICMD_FW_VERSION) {
                                    MainActivity.SyncLast_Status = buffer[AccessoryControl.USB_READ_EXCEPTION];
                                    MainActivity.SyncLast.set(AccessoryControl.USB_OPEN_EXCEPTION, AccessoryControl.this.toInt(buffer[AccessoryControl.USB_WRITE_EXCEPTION], buffer[AccessoryControl.APICMD_SYNC]));
                                    MainActivity.SyncLast.set(AccessoryControl.USB_CLOSE_EXCEPTION, AccessoryControl.this.toInt((byte) 0, buffer[AccessoryControl.APICMD_STATUS]));
                                    MainActivity.SyncLast.set(AccessoryControl.APICMD_SYNC, AccessoryControl.this.toInt((byte) 0, buffer[AccessoryControl.APICMD_HW_MODEL]));
                                    MainActivity.SyncLast.set(AccessoryControl.APICMD_QUERY, AccessoryControl.this.toInt((byte) 0, buffer[AccessoryControl.SYNC_LAST_MAX]));
                                    MainActivity.SyncLast.set(AccessoryControl.APICMD_UL, AccessoryControl.this.toInt((byte) 0, buffer[AccessoryControl.APICMD_FW_VERSION]));
                                    MainActivity.SyncLast.set(AccessoryControl.APICMD_DL, AccessoryControl.this.toInt((byte) 0, buffer[AccessoryControl.APICMD_API_VERSION]));
                                    m = Message.obtain(AccessoryControl.this.handler, resp);
                                    m.arg1 = AccessoryControl.APICMD_BASE;
                                    AccessoryControl.this.handler.sendMessage(m);
                                }
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIDATA_SYNC_NEXT /*143*/:
                                MainActivity.SyncNext = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]);
                                Log.i(TAG, "(Recv)APIDATA_SYNC_NEXT= " + MainActivity.SyncNext);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIDATA_VIN /*168*/:
                                MainActivity.Gateway_VIN = new String(buffer, AccessoryControl.USB_READ_EXCEPTION, len - 3);
                                Log.i(TAG, "(Recv)APIDATA_VIN = " + MainActivity.Gateway_VIN);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIDATA_ACTIVATION_CODE /*169*/:
                                MainActivity.ActivationCode = AccessoryControl.this.toInt(buffer[AccessoryControl.USB_READ_EXCEPTION], buffer[AccessoryControl.USB_WRITE_EXCEPTION]);
                                Log.i(TAG, "(Recv)APIDATA_ACTIVATION_CODE = " + MainActivity.ActivationCode);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIDATA_FLEET /*170*/:
                                MainActivity.Gateway_Fleet = new String(buffer, AccessoryControl.USB_READ_EXCEPTION, len - 3);
                                Log.i(TAG, "(Recv)APIDATA_FLEET = " + MainActivity.Gateway_Fleet);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIDATA_SERVER_ROUTE /*180*/:
                                MainActivity.APIroute = new String(buffer, AccessoryControl.USB_READ_EXCEPTION, len - 3);
                                MainActivity.APIroute = MainActivity.APIroute.trim();
                                if (MainActivity.APIroute.trim().isEmpty()) {
                                    MainActivity.APIroute = MainActivity.DefaultAPIroute;
                                }
                                Log.i(TAG, "(Recv)APIDATA_SERVER_ROUTE = " + MainActivity.APIroute);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIDATA_FEATURE_CODES /*181*/:
                                Log.i(TAG, "(Recv)APIDATA_FEATURE_CODES...");
                                for (i = AccessoryControl.APICMD_BASE; i < 100; i += AccessoryControl.USB_OPEN_EXCEPTION) {
                                    if (i < len - 1) {
                                        Features.feature_status[i] = buffer[i + AccessoryControl.USB_READ_EXCEPTION];
                                    } else {
                                        Features.feature_status[i] = (byte) 0;
                                    }
                                }
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            case AccessoryControl.APIDATA_FEATURE_VALUES /*182*/:
                                Log.i(TAG, "(Recv)APIDATA_FEATURE_VALUES...");
                                for (i = AccessoryControl.APICMD_BASE; i < 100; i += AccessoryControl.USB_OPEN_EXCEPTION) {
                                    if (i * AccessoryControl.USB_CLOSE_EXCEPTION < len - 1) {
                                        Features.feature_value[i] = AccessoryControl.this.toInt(buffer[(i * AccessoryControl.USB_CLOSE_EXCEPTION) + AccessoryControl.USB_READ_EXCEPTION], buffer[(i * AccessoryControl.USB_CLOSE_EXCEPTION) + AccessoryControl.USB_WRITE_EXCEPTION]);
                                    } else {
                                        Features.feature_value[i] = AccessoryControl.APICMD_BASE;
                                    }
                                }
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
                                break;
                            default:
                                Log.w(TAG, "Unknown command: " + resp);
                                pos = AccessoryControl.USB_CLOSE_EXCEPTION + len;
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
        this.usbManager = UsbManager.getInstance(context);
    }

    public OpenStatus open() {
        Log.i(TAG, "   --> AccessoryControl.open..");
        if (this.isOpen) {
            Log.i(TAG, "   <-- AccessoryControl.open");
            return OpenStatus.CONNECTED;
        }
        Log.i(TAG, "   get USBAccessoryList..");
        UsbAccessory[] accList = this.usbManager.getAccessoryList();
        if (accList == null) {
            Log.e(TAG, "   accList is NULL");
        } else {
            Log.i(TAG, "   accList=" + accList.toString());
        }
        if (accList == null || accList.length <= 0) {
            Log.e(TAG, "   *** Error: Open: Accessory mode not available ***");
        } else {
            MainActivity.demo_mode = false;
            if (this.usbManager.hasPermission(accList[APICMD_BASE])) {
                Log.i(TAG, "   Open: we have Permission to use the USB");
                Log.i(TAG, "      -->Call to open device: " + accList[APICMD_BASE].toString());
                OpenStatus status = open(accList[APICMD_BASE]);
                Log.i(TAG, "      <--status of open request: " + status.toString());
                Log.i(TAG, "   <-- AccessoryControl.open");
                return status;
            }
            Log.i(TAG, "   Open: we do NOT have Permission to use the USB");
            if (this.permissionRequested) {
                Log.e(TAG, "   Open: USB premission has been requested, but not [yet] granted");
                Log.e(TAG, "   ******* ERROR: Open: known bug in Android - AOA Receiver not terminating properly.");
            } else {
                PendingIntent permissionIntent = PendingIntent.getBroadcast(this.context, APICMD_BASE, new Intent(ACTION_USB_PERMISSION), APICMD_BASE);
                Log.i(TAG, "   Open: Requesting USB permission..");
                this.usbManager.requestPermission(accList[APICMD_BASE], permissionIntent);
                this.permissionRequested = true;
                Log.i(TAG, "   <-- AccessoryControl.open");
                return OpenStatus.REQUESTING_PERMISSION;
            }
        }
        Log.i(TAG, "   <-- AccessoryControl.open");
        return OpenStatus.NO_ACCESSORY;
    }

    public OpenStatus open(UsbAccessory accessory) {
        Log.i(TAG, "   ==>AccessoryControl.open(accessory)..");
        if (this.isOpen) {
            Log.i(TAG, "   already open");
            Log.i(TAG, "   <== AccessoryControl.open(accessory)");
            return OpenStatus.CONNECTED;
        }
        openLogFile();
        openDatumFile();
        openCANLogFile();
        Log.i(TAG, "   USB device is: " + accessory.getManufacturer() + " " + accessory.getModel());
        if (ACC_MANUF.equals(accessory.getManufacturer()) && ACC_MODEL.equals(accessory.getModel())) {
            Log.i(TAG, "   USB device is known and supported");
            Log.i(TAG, "   Open the USB connection..");
            this.parcelFileDescriptor = this.usbManager.openAccessory(accessory);
            if (this.parcelFileDescriptor != null) {
                this.accOutputStream = new FileOutputStream(this.parcelFileDescriptor.getFileDescriptor());
                Log.d(TAG, "accOutputStream=" + this.accOutputStream.toString());
                this.accInputStream = new FileInputStream(this.parcelFileDescriptor.getFileDescriptor());
                Log.d(TAG, "accInputStream=" + this.accInputStream.toString());
                this.isOpen = true;
                this.usbreader = new UsbReader(new BufferedInputStream(this.accInputStream, 16384));
                new Thread(this.usbreader).start();
                Log.i(TAG, "   ---> Thread(receiver).start()..");
                writeLogString("Gateway Connected");
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
            writeLogString("   Gateway Disconnected");
            closeDatumFile();
            closeLogFile();
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

    public void writeCommand(int cmd, int hiVal, int loVal) {
        byte[] buffer = new byte[APICMD_SYNC];
        if (this.isOpen) {
            Log.i(TAG, "AccessoryControl::writeCommand: " + Integer.toString(cmd) + "  isOpen? true");
            buffer[APICMD_BASE] = (byte) 0;
            buffer[USB_OPEN_EXCEPTION] = (byte) 3;
            buffer[USB_CLOSE_EXCEPTION] = (byte) (cmd & 255);
            buffer[USB_READ_EXCEPTION] = (byte) (hiVal & 255);
            buffer[USB_WRITE_EXCEPTION] = (byte) (loVal & 255);
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

    public void writeCommandBlock(int cmd, int lth, byte[] datablk) {
        byte[] buffer = new byte[(lth + USB_READ_EXCEPTION)];
        if (this.isOpen) {
            Log.i(TAG, "AccessoryControl::writeCommand: " + Integer.toString(cmd) + "  isOpen? true");
            int reclen = lth + USB_OPEN_EXCEPTION;
            buffer[APICMD_BASE] = (byte) ((reclen >> SYNC_LAST_MAX) & 255);
            buffer[USB_OPEN_EXCEPTION] = (byte) (reclen & 255);
            buffer[USB_CLOSE_EXCEPTION] = (byte) (cmd & 255);
            for (int i = APICMD_BASE; i < lth; i += USB_OPEN_EXCEPTION) {
                buffer[i + USB_READ_EXCEPTION] = datablk[i];
            }
            try {
                synchronized (this.accOutputStream) {
                    this.accOutputStream.write(buffer, APICMD_BASE, lth + USB_READ_EXCEPTION);
                }
                return;
            } catch (IOException ioe) {
                Log.w(TAG, "IOException writing a USB data block - ioe=", ioe);
                USBException(USB_WRITE_EXCEPTION);
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

    public void openLogFile() {
        if (this.logStream == null) {
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
                    this.logStream = new BufferedOutputStream(new FileOutputStream(new File(path, "Log.bin"), true));
                    Log.i(TAG, "Log file opened");
                } catch (Exception e) {
                    Log.w(TAG, "IOException creating Log file - ioe=", e);
                }
            } else {
                Log.w(TAG, "Error opening Log file - SDCard is not mounted");
            }
        }
        if (this.logStream != null) {
            writeLogString("\\\\IdleSmart log start");
        }
    }

    public void writeLogString(String logstring) {
        if (this.logStream != null && !logstring.trim().isEmpty()) {
            try {
                byte[] ts = getUTCdatetimeAsString().getBytes();
                this.logStream.write(ts, APICMD_BASE, ts.length);
                this.logStream.write(APICMD_AUTO_SHUTOFF_TIMEOUT);
                byte[] bstr = logstring.getBytes();
                this.logStream.write(bstr, APICMD_BASE, bstr.length);
                this.logStream.write(APICMD_DL);
                this.logStream.write(APICMD_API_VERSION);
                this.logStream.flush();
            } catch (Exception e) {
                Log.w(TAG, "IOException writing Log file - e=", e);
            }
        }
    }

    public void closeLogFile() {
        if (this.logStream != null) {
            writeLogString("\\\\IdleSmart log stop");
            try {
                this.logStream.flush();
                this.logStream.close();
                this.logStream = null;
            } catch (Exception e) {
                Log.w(TAG, "IOException closing Log file - e=", e);
            }
        }
    }

    public void openDatumFile() {
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
    }

    public void writeDatumString(String datumstring) {
        if (this.datumStream != null && !datumstring.trim().isEmpty()) {
            try {
                byte[] ts = getUTCdatetimeAsString().getBytes();
                this.datumStream.write(ts, APICMD_BASE, ts.length);
                this.datumStream.write(APICMD_AUTO_SHUTOFF_TIMEOUT);
                byte[] bstr = datumstring.getBytes();
                this.datumStream.write(bstr, APICMD_BASE, bstr.length);
                this.datumStream.write(APICMD_DL);
                this.datumStream.write(APICMD_API_VERSION);
                this.datumStream.flush();
            } catch (Exception e) {
                Log.w(TAG, "IOException writing Datum file - e=", e);
            }
        }
    }

    public void closeDatumFile() {
        if (this.datumStream != null) {
            try {
                this.datumStream.flush();
                this.datumStream.close();
                this.datumStream = null;
            } catch (Exception e) {
                Log.w(TAG, "IOException closing Datum file - e=", e);
            }
        }
    }

    public void writefmtCANLogStream(String str) {
        if (MainActivity.aMaintEnable[APICMD_BASE] && str != null && this.canStream != null) {
            try {
                int lth = str.length();
                int reccnt = lth / APICMD_TESTMODE;
                if (reccnt * APICMD_TESTMODE != lth) {
                    reccnt += USB_OPEN_EXCEPTION;
                    str = padRight(str, (reccnt * APICMD_TESTMODE) - lth, ' ');
                }
                byte[] bytes = str.getBytes();
                for (int irec = APICMD_BASE; irec < reccnt; irec += USB_OPEN_EXCEPTION) {
                    this.canStream.write(bytes, APICMD_BASE, APICMD_TESTMODE);
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
                this.canStream.write("================".getBytes(), APICMD_BASE, APICMD_TESTMODE);
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
        return ((((hi & 255) << SYNC_LAST_MAX) | (lo & 255)) << APICMD_TESTMODE) >> APICMD_TESTMODE;
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
