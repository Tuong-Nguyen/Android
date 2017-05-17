package vn.com.tma.idlesmart;

import android.app.ActivityManager;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import vn.com.tma.idlesmart.Utils.CANLogFile;
import vn.com.tma.idlesmart.Utils.FileName;
import vn.com.tma.idlesmart.Utils.InformationSender;
import vn.com.tma.idlesmart.Utils.PrefUtils;
import vn.com.tma.idlesmart.Utils.TimeConverter;
import vn.com.tma.idlesmart.fragment.AlertDialogFragment;
import vn.com.tma.idlesmart.fragment.CommDialogFragment;
import vn.com.tma.idlesmart.fragment.MaintenanceDialogFragment;
import vn.com.tma.idlesmart.fragment.PasswordDialogFragment;
import vn.com.tma.idlesmart.fragment.SerialDialogFragment;
import vn.com.tma.idlesmart.listener.AlertDialogFragmentListener;
import vn.com.tma.idlesmart.listener.MaintenanceDialogFragmentListener;
import vn.com.tma.idlesmart.listener.PasswordDialogFragmentListener;
import vn.com.tma.idlesmart.params.PhoneHomeSyncStatus;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;


public class MainActivity extends KioskModeActivity implements OnClickListener, AlertDialogFragmentListener, PasswordDialogFragmentListener, MaintenanceDialogFragmentListener {
    public static String APIroute = null;
    public static int ActivationCode = 0;
    public static boolean ActivationProcessPending = false;
    public static int ConnectivityIndicator = 0;
    private static String CurrentConnectivityStatus = null;
    private static boolean CurrentDashboardFlag = false;
    private static int CurrentDashboardFragment = 0;
    private static String CurrentEngineStatus = null;
    private static String CurrentGatewayStatus = null;
    private static boolean CurrentSettingsFlag = false;
    private static boolean CurrentStatusBarFlag = false;
    public static boolean DebugLog = false;
    public static final String DefaultAPIroute = "api.idlesmart.com";

    public static final int UNKNOWN_CONNECTIVITY = 0;
    public static final int GOOD_CONNECTIVITY = 1;
    public static final int BAD_CONNECTIVITY = 2;

    public static boolean GatewayUpdatePending = false;
    public static String Gateway_APIversion = null;
    public static int Gateway_Activated = 0;
    public static String Gateway_FWversion = null;
    public static String Gateway_Fleet = null;
    public static int Gateway_Guid = 0;
    public static int Gateway_HWver = 0;
    public static String Gateway_LDRversion = null;
    public static String Gateway_SerialID = null;
    public static String Gateway_VIN = null;
    public static boolean HasFocus = false;
    static final long MONITOR_RATE = 3000;
    public static boolean PackageUpdatePending = false;
    public static int Password = 0;
    public static boolean PasswordEnable = false;
    public static boolean PasswordValid = false;
    private static boolean Restart = false;
    public static boolean ServerConnectivity = false;
    public static Calendar SyncLast = null;
    public static int SyncLast_Status = PhoneHomeSyncStatus.IDLE;
    public static int SyncNext = 0;
    public static int SyncStart = 0;
    public static int SyncTTL = 0;
    public static boolean SyncWithServer = false;
    public static boolean SystemActivationFlag = false;
    private static final String TAG = "IdleSmart.Main";

    public static boolean ValidActivationProcess = false;

    public static boolean[] aMaintEnable = null;
    public static int[] aMaintValue = null;
    private static String commlogstr = null;
    private static TextView commlogtext = null;
    public static boolean demo_mode = false;
    public static boolean gateway_connected;
    public static boolean gateway_restarting;
    public static httpClient httpclient;
    static int monitor_iter;
    public static boolean packagemanagernag;
    public static boolean test_mode;
    private int BatteryProtectMode;
    private int CabinComfortMode;
    private int ColdWeatherGuardMode;
    Handler EThandler;
    private Runnable ETrunnable;
    int GatewayMode;
    private boolean ScreenOn;
    final Handler USBReconnectHandler;
    private Runnable USBReconnectRunnable;
    private final BroadcastReceiver UsbReceiver;
    private String[] aSystemStatus;
    public AccessoryControl accessoryControl;
    private int activation_step;
    private Dialog alertDialog;

    public Faults faults;
    private int initialScreenBrightness;
    private int initialScreenTimeout;
    private boolean isScreenOn;
    int kiosk_mode_counter;
    private OnCompletionListener mCompletionListener;
    private OnEditorActionListener mEditorActionListener;
    private MediaPlayer mMediaPlayer;
    private WakeLock mTempWakeLock;
    private WakeLock mWakeLock;
    private Dialog maintDialog;
    private int maint_mode_counter;
    public Menus menus;
    int param_id;
    public Params params;
    final Handler screentimeoutHandler;
    private int settings_entrytype;
    public int settings_menu1_index;
    public int settings_menu2_index;
    private int test_mark_counter;
    private int test_mode_counter;
    final Runnable timeoutRunnable;
    private LinearLayout topLayout;
    final Handler verificationHandler;
    final Runnable verificationRunnable;
    private InformationSender informationSender;
    FragmentTransaction fragmentTransaction;


    /**
     * Define verification steps
     */
    static class ActivationStep {
        static final int NONE = 0; // Not in activation
        static final int VERIFICATION = 1;
        static final int INSTALL = 2;
        static final int ACTIVATION = 3;
        static final int ACTIVATION_CODE = 4;
        static final int VIN_CODE = 5;
    }

    static class Functionality {
        static final int CABIN_COMFORT = 1;
        static final int COLD_WEATHER_GUARD = 2;
        static final int BATTERY_PROTECT = 3;
    }

    /**
     * Index of maintenance features
     */
    public static class MaintenanceFeature {
        static final int LOG_FILE = 0;                             // Log File (J1939 data)
        static final int CLUTCH_OVERRIDE = 1;                      // Clutch Override
        static final int IDLE_TIME_OVERRIDE = 2;                   // Idle Timer Override (1='brake', 2='long brake', 3=spn_1237)
        static final int ENGINE_SPEED_ADJUSTMENTS = 3;             // Engine Speed Adjustments (1=during idleup, 2=while running)
        static final int TIMESTAMP_RPM = 4;                        // Timestamp/RPM logging
        static final int NEUTRAL_SWITCH_DETECTION = 5;             // Neutral switch detection
        static final int RESERVED = 6;                             // Reserved
        static final int SERVER_ROUTE = 7;                         // Server Route
        static final int RESET_VIN_RESTORE_FACTORY_DEFAULTS = 8;   // Reset VIN and Restore Factory Defaults
        static final int VIEW_SERVER_COMMUNICATION = 9;            // View Server Communication (1-8 or 99)
    }

    /**
     * Represent the current status of the application
     */
    private static class KillSwitchMode {
        static final int CONNECTED = 0; // The accessory is monitoring the truck.
        static final int KILL_SWITCH = 1; // Display a confirmation asking if user want to stop monitoring the truck
        static final int KILL_SWITCH_CONFIRMED = 2; // Power Off button is displayed for stopping monitoring the truck
        static final int POWER_OFF = 3; // The accessory do not monitor the truck.
    }

    /**
     * Runnable which check the usb connection and try to connect (if not connected) every 3 seconds
     */
    class UsbConnectionChecker implements Runnable {
        UsbConnectionChecker() {
        }

        public void run() {
            Log.i(MainActivity.TAG, "--> <USBReconnectHandler> - gateway_connected:" + (MainActivity.gateway_connected ? "true" : "false"));
            Log.i(MainActivity.TAG, "      PackageUpdatePending:" + (MainActivity.PackageUpdatePending ? "true" : "false"));
            if (!(MainActivity.PackageUpdatePending || MainActivity.gateway_connected)) {
                Log.d(MainActivity.TAG, "      ############################################## USB is Locked Up ########################################");
                if (!MainActivity.demo_mode) {
                    MainActivity.this.connectUSB();
                }
                if (MainActivity.gateway_connected) {
                    Log.i(MainActivity.TAG, "      +++++++ We Have Reconnected +++++++");
                } else {
                    int i = MainActivity.monitor_iter;
                    MainActivity.monitor_iter = i + 1;
                    if (i >= 3) {
                        Log.d(MainActivity.TAG, "      PostDelayed USBReconnectHandler runnable..");
                        MainActivity.this.USBReconnectHandler.postDelayed(this, MainActivity.MONITOR_RATE);
                    } else {
                        Log.d(MainActivity.TAG, "      PostDelayed USBReconnectHandler runnable..");
                        MainActivity.this.USBReconnectHandler.postDelayed(this, MainActivity.MONITOR_RATE);
                    }
                }
            }
            Log.i(MainActivity.TAG, "<-- <USBReconnectHandler>");
        }
    }

    /**
     * Runnable which set NEXT SYNC TIME to PCB (APIDATA_SYNC_NEXT).
     */
    class SetSyncTimeRunnable implements Runnable {
        public void run() {
            MainActivity.SyncWithServer = true;
            Log.i(MainActivity.TAG, "--> <TimerTask>: SyncWithServer");
            MainActivity.this.SetNextPhoneHome();
        }
    }

    /**
     *  A Runnable which turns the screen off.
     */
    class ScreenOffRunnable implements Runnable {
        ScreenOffRunnable() {
        }

        public void run() {
            MainActivity.this.setScreenBrightness(0);
            MainActivity.this.isScreenOn = false;
        }
    }

    /**
     * ActionListener for ActivityCodeEditText adn VINCodeEditText
     */
    class ActivationCodeVINCodeListener implements OnEditorActionListener {
        ActivationCodeVINCodeListener() {
        }

        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            byte[] data = new byte[2];
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                switch (v.getId()) {
                    case R.id.activationCodeEditText /*2131361797*/:
                        MainActivity.ActivationCode = Integer.valueOf(((EditText) v).getText().toString());
                        data[0] = (byte) ((MainActivity.ActivationCode >> 8) & 255);
                        data[1] = (byte) (MainActivity.ActivationCode & 255);
                        MainActivity.this.accessoryControl.writeCommand(AccessoryControl.APIDATA_ACTIVATION_CODE, data[0], data[1]);
                        Log.i(MainActivity.TAG, "(send) APIDATA_ACTIVATION_CODE=" + MainActivity.ActivationCode);
                        break;
                    case R.id.VINCodeEditText /*2131362092*/:
                        MainActivity.Gateway_VIN = ((EditText) v).getText().toString();
                        informationSender.sendVIN(MainActivity.Gateway_VIN);
                        Log.i(MainActivity.TAG, "(send) APICMD_VIN=" + MainActivity.Gateway_VIN);
                        break;
                }
            }
            return false;
        }
    }


    public class ScreenReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.i(MainActivity.TAG, "==> ScreenReceiver::onReceive");
            String action = intent.getAction();
            if (action.equals("android.intent.action.SCREEN_OFF")) {
                Log.i(MainActivity.TAG, "    ScreenReceiver:Intent=SCREEN_OFF");
                MainActivity.this.setScreenBrightness(0);
            } else if (action.equals("android.intent.action.SCREEN_ON")) {
                Log.i(MainActivity.TAG, "    ScreenReceiver:intent=SCREEN_ON");
                MainActivity.this.setScreenBrightness(MainActivity.this.initialScreenBrightness);
            }
            Log.i(MainActivity.TAG, "<== ScreenReceiver::onReceive");
        }
    }

    private static class UIHandler extends Handler {
        private final WeakReference<MainActivity> mainActivityClassWeakReference;

        public UIHandler(MainActivity myClassInstance) {
            this.mainActivityClassWeakReference = new WeakReference(myClassInstance);
        }

        public void handleMessage(Message msg) {
            boolean z = true;
            MainActivity mainActivityClass = this.mainActivityClassWeakReference.get();
            TimeConverter timeConverter = new TimeConverter();
            if (mainActivityClass != null) {
                String str;
                switch (msg.what) {
                    case Params.PARAM_TruckTimer /*18*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_DimTabletScreen, msg.arg1);
                        mainActivityClass.setScreenTimeout(msg.arg1);
                    	break;
					case Params.PARAM_PasswordEnable /*19*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_AudibleSound,msg.arg1);
                        if (msg.arg1 == 0) {
                            z = false;
                        }
                        mainActivityClass.setSoundOn(z);
                    	break;
					case AccessoryControl.APICMD_ENGINE_IDLE_RPM /*30*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_TruckRPMs, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_ENGINE_RESTART_INTERVAL /*31*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_EngineRunTime,msg.arg1);
                    	break;
					case AccessoryControl.APICMD_AUTO_SHUTOFF_TIMEOUT /*32*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_AutoDisable, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_CABIN_COMFORT_ENABLE /*40*/:
                        if (msg.arg1 == 0) {
                            mainActivityClass.setFunctionMode(Functionality.CABIN_COMFORT, 0);
                        } else {
                            mainActivityClass.setFunctionMode(Functionality.CABIN_COMFORT, 1);
                        }
                    	break;
					case AccessoryControl.VIN_ID_MAX /*41*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_CabinTargetTemp, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_CABIN_TEMP_RANGE /*42*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_CabinTempRange, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_AMBIENT_TEMP_SETPOINT /*43*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_OutsideTargetTemp, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_AMBIENT_TEMP_RANGE /*44*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_OutsideTempRange, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_SYSTEMTIMER /*45*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_TruckTimer, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_DRIVER_TEMP_COMMON /*48*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_DriverTempCommon, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_BATTERY_MONITOR_ENABLE /*50*/:
                        if (msg.arg1 == 0) {
                            mainActivityClass.setFunctionMode(Functionality.BATTERY_PROTECT, 0);
                        } else {
                            mainActivityClass.setFunctionMode(Functionality.BATTERY_PROTECT, 1);
                        }
                    	break;
					case AccessoryControl.APICMD_BATTERY_MONITOR_VOLTAGE /*51*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_VoltageSetPoint, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_BATTERY_MONITOR_RUNTIME /*52*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_EngineRunTime, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_SYNC_START /*53*/:
                        mainActivityClass.SetNextPhoneHome();
                    	break;
					case AccessoryControl.APICMD_SYNC_TTL /*54*/:
                        mainActivityClass.SetNextPhoneHome();
                    	break;
					case AccessoryControl.APICMD_COLD_WEATHER_GUARD_ENABLE /*55*/:
                        if (msg.arg1 == 0) {
                            mainActivityClass.setFunctionMode(Functionality.COLD_WEATHER_GUARD, 0);
                        } else {
                            mainActivityClass.setFunctionMode(Functionality.COLD_WEATHER_GUARD, 1);
                        }
                    	break;
					case AccessoryControl.APICMD_COLD_WEATHER_GUARD_START_TEMP /*56*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_TemperatureSetPoint, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_COLD_WEATHER_GUARD_RESTART_INTERVAL /*58*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_HoursBetweenStart, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_COLD_WEATHER_GUARD_MIN_COOLANT /*59*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_MinCoolantTemp, msg.arg1);
                    	break;
					case AccessoryControl.APICMD_COLD_WEATHER_GUARD_IDEAL_COOLANT /*60*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_IdealCoolantTemp, msg.arg1);
                    	break;
					case AccessoryControl.APIEVENT_SYNC /*69*/:
                        if (MainActivity.SyncWithServer) {
                            MainActivity.SyncWithServer = false;
                            MainActivity.httpclient.PhoneHome(1, true);
                        }
                    	break;
					case AccessoryControl.APIEVENT_CURRENT_MODE /*78*/:
                        Log.i(MainActivity.TAG, "handler::CurrentMode received");
                        mainActivityClass.useGatewayMode(msg.arg1);
                    	break;
					case AccessoryControl.APIEVENT_ACTIVATED /*80*/:
                        MainActivity.Gateway_Activated = msg.arg1;
                        Log.i(MainActivity.TAG, "APIEVENT_ACTIVATED = " + MainActivity.Gateway_Activated);
                        if (MainActivity.Gateway_Activated == 0) {
                            z = false;
                        }
                        mainActivityClass.InstallAndActivate(z);
                    	break;
					case AccessoryControl.APIEVENT_REQUPDATE /*81*/:
                        MainActivity.httpclient.PhoneHome(2, false);
                    	break;
					case AccessoryControl.APIEVENT_SYSTEM_STATUS /*84*/:
                        mainActivityClass.setEngineStatus(mainActivityClass.aSystemStatus[msg.arg1]);
                    	break;
					case AccessoryControl.APIEVENT_DRIVING_MODE /*88*/:
                        if (msg.arg1 == 0) {
                            mainActivityClass.wakeup();
                            mainActivityClass.back2sleep();
                        }
                    	break;
					case AccessoryControl.APIEVENT_ALERT /*94*/:
                        mainActivityClass.openAlertDialog(msg.arg1);
                    	break;
					case AccessoryControl.APIEVENT_CLEARALERT /*95*/:
                        mainActivityClass.closeAlertDialog(msg.arg1);
                    	break;
					case AccessoryControl.APIEVENT_CLEARALLALERTS /*96*/:
                        mainActivityClass.closeAllAlertDialogs();
                    	break;
					case AccessoryControl.APIEVENT_WARNING /*97*/:
                        mainActivityClass.openAlertDialog(msg.arg1);
                    	break;
					case AccessoryControl.APIEVENT_CLEARWARNING /*98*/:
                        mainActivityClass.closeAlertDialog(msg.arg1);
                    	break;
					case AccessoryControl.APIEVENT_CLEARALLWARNINGS /*99*/:
                        mainActivityClass.closeAllAlertDialogs();
                    	break;
					case AccessoryControl.APIEVENT_HANDLER_EXCEPTION /*127*/:
                        Log.e(MainActivity.TAG, "USB_HANDLER_EXCEPTION: " + msg.arg1);
                        MainActivity.gateway_connected = false;
                        mainActivityClass.disconnected();
                        mainActivityClass.accessoryControl.close();
                    	break;
					case AccessoryControl.APIDATA_CABIN_TEMP /*136*/:
                        str = Integer.toString(msg.arg1) + "\u00b0";
                        ((TextView) mainActivityClass.findViewById(R.id.cabinComfortValue)).setText(str);
                        ((TextView) mainActivityClass.findViewById(R.id.ccFragTemperatureValue)).setText(str);
                    	break;
					case AccessoryControl.APIDATA_AMBIENT_TEMP /*137*/:
                        ((TextView) mainActivityClass.findViewById(R.id.ccFragAmbientTempValue)).setText(Integer.toString(msg.arg1) + "\u00b0");
                    	break;
					case AccessoryControl.APIDATA_BATTERY_VOLTAGE /*138*/:
                        str = Integer.toString(msg.arg1);
                        ((TextView) mainActivityClass.findViewById(R.id.batteryProtectValue)).setText(str.substring(0, str.length() - 1) + "." + str.substring(str.length() - 1) + mainActivityClass.params.aParamSfx[8]);
                    	break;
					case AccessoryControl.APIDATA_SYNC_LAST /*142*/:
                        if (MainActivity.SyncLast_Status == PhoneHomeSyncStatus.GATEWAY_UPDATE) {
                            MainActivity.SyncLast_Status = PhoneHomeSyncStatus.OK;
                            MainActivity.httpclient.sendSyncLast(MainActivity.SyncLast_Status, MainActivity.SyncLast);
                        }
                        mainActivityClass.UpdateConnectivityStatus();
                    	break;
					case AccessoryControl.APIDATA_TIMEREMAINING /*158*/:
                        if (mainActivityClass.GatewayMode != GatewayModes.BATTERY_PROTECT || msg.arg1 <= 0) {
                            str = timeConverter.time2MinsSecsStr(mainActivityClass.params.getCurrentParamValue(Params.PARAM_EngineRunTime) * 60);
                        } else {
                            str = timeConverter.time2MinsSecsStr(msg.arg1);
                        }
                        ((TextView) mainActivityClass.findViewById(R.id.bpFragTimeRemainingValue)).setText(str);
                    	break;
					case AccessoryControl.APIDATA_FLEET_CABIN_COMFORT_ENABLE /*159*/:
                        if (msg.arg1 == 0) {
                            mainActivityClass.params.setCurrentParamValue(Params.PARAM_FleetCabinComfort, 0);
                        } else {
                            mainActivityClass.params.setCurrentParamValue(Params.PARAM_FleetCabinComfort, 1);                        }
                    	break;
					case AccessoryControl.APIDATA_FLEET_CABIN_TEMP_SETPOINT /*160*/:
                        mainActivityClass.params.setCurrentParamValue(Params.PARAM_FleetCabinTargetTemp, msg.arg1);
                    	break;
					case AccessoryControl.APICAN_ENGINE_COOLANT_TEMP /*193*/:
                        str = Integer.toString(msg.arg1) + "\u00b0";
                        ((TextView) mainActivityClass.findViewById(R.id.cwgFragTemperatureValue)).setText(str);
                        ((TextView) mainActivityClass.findViewById(R.id.coldWeatherGuardValue)).setText(str);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public MainActivity() {
        super();
        this.verificationHandler = new Handler();
        this.screentimeoutHandler = new Handler();
        this.EThandler = new Handler();
        this.USBReconnectHandler = new Handler();
        this.alertDialog = null;
        this.maintDialog = null;
        this.mTempWakeLock = null;
        this.menus = new Menus();
        this.params = new Params();
        this.faults = new Faults();
        this.test_mode_counter = 0;
        this.maint_mode_counter = 0;
        this.test_mark_counter = 0;
        this.GatewayMode = GatewayModes.IDLE;
        this.CabinComfortMode = Modes.DISABLED;
        this.ColdWeatherGuardMode = Modes.DISABLED;
        this.BatteryProtectMode = Modes.DISABLED;
        this.activation_step = 0;
        this.settings_entrytype = 0;
        this.settings_menu1_index = 0;
        this.settings_menu2_index = 0;
        this.initialScreenBrightness = 0;
        this.initialScreenTimeout = 0;
        this.ScreenOn = true;
        this.USBReconnectRunnable = new UsbConnectionChecker();
        this.ETrunnable = new SetSyncTimeRunnable();
        this.isScreenOn = true;
        this.timeoutRunnable = new ScreenOffRunnable();
        this.mEditorActionListener = new ActivationCodeVINCodeListener();
        this.informationSender = new InformationSender(accessoryControl);
        this.fragmentTransaction = getSupportFragmentManager().beginTransaction();
        this.verificationRunnable = new Runnable() {
            public void run() {
                MainActivity.this.nextVerificationStep();
            }
        };
        this.mMediaPlayer = null;
        this.mCompletionListener = new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                Log.i(MainActivity.TAG, "   release");
                MainActivity.this.mMediaPlayer.release();
                MainActivity.this.mMediaPlayer = null;
            }
        };
        this.kiosk_mode_counter = 0;
        this.aSystemStatus = new String[]{"NOT ACTIVATED", "DISABLED", "DISENGAGED", "VEHICLE NOT READY",
                "TEMP CHANGE FAULT", "STANDBY MODE", "TEMP INTERLOCK", "INITIATING START", "STARTING ENGINE",
                "WARMING UP", "ENGINE RUNNING", "RUNNING NORMAL", "CHARGING BATTERY", "RUNNING COLD GUARD",
                "SHUTTING DOWN", "RESTART DELAY", "DOWNLOADING", "VEHICLE NOT READY - Transmission not in Neutral",
                "VEHICLE NOT READY - Parking Brake not set", "VEHICLE NOT READY - Hood is Open",
                "VEHICLE NOT READY - Regen Required", "VEHICLE NOT READY - Ignition Switch is On",
                "VEHICLE NOT READY - Battery Voltage below 11.0V"};

        this.UsbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (AccessoryControl.ACTION_USB_PERMISSION.equals(action)) {
                    Log.i(MainActivity.TAG, "==> UsbReceiver::Recv Intent=ACTION_USB_PERMISSION");
                    synchronized (this) {
                        UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                        if (intent.getBooleanExtra("permission", false)) {
                            Log.i(MainActivity.TAG, "    UsbReceiver::Permission Granted");
                            if (accessory != null) {
                                AccessoryControl.OpenStatus status = MainActivity.this.accessoryControl.open(accessory);
                                if (status == AccessoryControl.OpenStatus.CONNECTED) {
                                    Log.d(MainActivity.TAG, "    UsbReceiver::Gateway is connected");
                                    MainActivity.gateway_connected = true;
                                    MainActivity.demo_mode = false;
                                    MainActivity.this.connected();
                                    MainActivity.this.enableDashboard(true);
                                    MainActivity.this.selectRunning(1);
                                } else {
                                    Log.d(MainActivity.TAG, "   UsbReceiver::Gateway is disconnected");
                                    MainActivity.this.disconnected();
                                    MainActivity.this.showError(status);
                                }
                            }
                        } else {
                            Log.i(MainActivity.TAG, "   UsbReceiver::Permission NOT Granted");
                            MainActivity.this.disconnected();
                            MainActivity.gateway_connected = false;
                            MainActivity.this.enableDashboard(false);
                        }
                    }
                } else if ("android.hardware.usb.action.USB_ACCESSORY_ATTACHED".equals(action)) {
                    Log.d(MainActivity.TAG, "==>UsbReceiver::Recv Intent=ACTION_USB_ACCESSORY_ATTACHED");
                    MainActivity.gateway_connected = true;
                    MainActivity.this.connected();
                } else if ("android.hardware.usb.action.USB_ACCESSORY_DETACHED".equals(action)) {
                    Log.d(MainActivity.TAG, "==>UsbReceiver::Recv Intent=ACTION_USB_ACCESSORY_DETACHED");
                    MainActivity.gateway_connected = false;
                    MainActivity.this.disconnected();
                    MainActivity.this.accessoryControl.close();
                    MainActivity.this.startConnectionMonitoring();
                }
            }
        };
    }

    static {
        DebugLog = true;
        KioskMode = false;
        Restart = false;
        HasFocus = true;
        SystemActivationFlag = false;
        gateway_connected = false;
        gateway_restarting = false;
        demo_mode = false;
        test_mode = false;
        packagemanagernag = true;
        CurrentStatusBarFlag = false;
        CurrentDashboardFlag = false;
        CurrentDashboardFragment = 0;
        CurrentSettingsFlag = false;
        CurrentGatewayStatus = "";
        CurrentEngineStatus = "";
        CurrentConnectivityStatus = "";
        Password = 0;
        PasswordEnable = false;
        PasswordValid = false;
        aMaintEnable = new boolean[10];
        aMaintValue = new int[10];
        Gateway_HWver = 0;
        Gateway_LDRversion = "";
        Gateway_FWversion = "";
        Gateway_APIversion = "";
        Gateway_SerialID = "";
        Gateway_VIN = "";
        Gateway_Fleet = "";
        Gateway_Activated = 0;
        Gateway_Guid = 0;
        ActivationCode = 0;
        ValidActivationProcess = false;
        ActivationProcessPending = false;
        APIroute = DefaultAPIroute;
        SyncWithServer = false;
        commlogtext = null;
        commlogstr = "";
        PackageUpdatePending = false;
        GatewayUpdatePending = false;
        ServerConnectivity = false;
        ConnectivityIndicator = 0;
        SyncStart = 60;
        SyncTTL = 1440;
        SyncNext = 0;
        SyncLast_Status = PhoneHomeSyncStatus.IDLE;
        SyncLast = Calendar.getInstance();
        monitor_iter = 0;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "#########################################################################################################################################");
        Log.i(TAG, "==>>onCreate");
        if (Restart) {
            Log.i(TAG, "   Restart Instantiation");
        } else {
            Log.i(TAG, "   Initial Instantiation");
        }
        if (DebugLog) {
            showRestartParams();
        }
        getWindow().addFlags(FLAG_DISMISS_KEYGUARD);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        setContentView(R.layout.main);
        if (!Restart) {
            KioskMode = true;
            PrefUtils.setKioskModeActive(KioskMode, getApplicationContext());
        }
        if (!Restart) {
            clearMaintInfo();
        }

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener(){
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                MainActivity.this.hideNavBar();
            }
        });

        findViewById(R.id.idlesmartButton).setOnClickListener(this);
        findViewById(R.id.dashboardButton).setOnClickListener(this);
        findViewById(R.id.settingsButton).setOnClickListener(this);
        findViewById(R.id.maintButton).setOnClickListener(this);
        findViewById(R.id.killSwitchButton).setOnClickListener(this);
        findViewById(R.id.cabinComfortFunction).setOnClickListener(this);
        findViewById(R.id.coldWeatherGuardFunction).setOnClickListener(this);
        findViewById(R.id.batteryProtectFunction).setOnClickListener(this);
        findViewById(R.id.cabinComfortEnableButton).setOnClickListener(this);
        findViewById(R.id.coldWeatherGuardEnableButton).setOnClickListener(this);
        findViewById(R.id.batteryProtectEnableButton).setOnClickListener(this);
        findViewById(R.id.ccFragStopButton).setOnClickListener(this);
        findViewById(R.id.cwgFragStopButton).setOnClickListener(this);
        findViewById(R.id.bpFragStopButton).setOnClickListener(this);
        findViewById(R.id.ccFragTargetTemperatureDecrButton).setOnClickListener(this);
        findViewById(R.id.ccFragTargetTemperatureIncrButton).setOnClickListener(this);
        findViewById(R.id.cwgFragMinTempDecrButton).setOnClickListener(this);
        findViewById(R.id.cwgFragMinTempIncrButton).setOnClickListener(this);
        findViewById(R.id.cwgFragIdealTempDecrButton).setOnClickListener(this);
        findViewById(R.id.cwgFragIdealTempIncrButton).setOnClickListener(this);
        findViewById(R.id.bpFragSetpointDecrButton).setOnClickListener(this);
        findViewById(R.id.bpFragSetpointIncrButton).setOnClickListener(this);
        findViewById(R.id.bpEngineRuntimeDecrButton).setOnClickListener(this);
        findViewById(R.id.bpEngineRuntimeIncrButton).setOnClickListener(this);
        findViewById(R.id.settingsMenu11).setOnClickListener(this);
        findViewById(R.id.settingsMenu12).setOnClickListener(this);
        findViewById(R.id.settingsMenu13).setOnClickListener(this);
        findViewById(R.id.settingsMenu14).setOnClickListener(this);
        findViewById(R.id.settingsMenu15).setOnClickListener(this);
        findViewById(R.id.settingsMenu16).setOnClickListener(this);
        findViewById(R.id.settingsMenu17).setOnClickListener(this);
        findViewById(R.id.settingsMenu21).setOnClickListener(this);
        findViewById(R.id.settingsMenu22).setOnClickListener(this);
        findViewById(R.id.settingsMenu23).setOnClickListener(this);
        findViewById(R.id.settingsMenu24).setOnClickListener(this);
        findViewById(R.id.settingsMenu25).setOnClickListener(this);
        findViewById(R.id.settingsMenu26).setOnClickListener(this);
        findViewById(R.id.settingsMenu27).setOnClickListener(this);
        findViewById(R.id.settingsEntryEnableButton).setOnClickListener(this);
        findViewById(R.id.settingsEntryDisableButton).setOnClickListener(this);
        findViewById(R.id.settingsEntryDecrementButton).setOnClickListener(this);
        findViewById(R.id.settingsEntryIncrementButton).setOnClickListener(this);
        findViewById(R.id.settingsEntryDoneButton).setOnClickListener(this);
        findViewById(R.id.settingsPasswordDoneButton).setOnClickListener(this);
        findViewById(R.id.killswitchButton).setOnClickListener(this);
        findViewById(R.id.poweroffButton).setOnClickListener(this);
        findViewById(R.id.poweronButton).setOnClickListener(this);
        findViewById(R.id.packagemanagerButton).setOnClickListener(this);
        findViewById(R.id.updateInstallButton).setOnClickListener(this);
        findViewById(R.id.packagemanagernagEscapeButton).setOnClickListener(this);
        findViewById(R.id.passwordContinueButton).setOnClickListener(this);
        findViewById(R.id.passwordReturnButton).setOnClickListener(this);
        findViewById(R.id.activationEnterCodeButton).setOnClickListener(this);
        findViewById(R.id.activationCodeContinueButton).setOnClickListener(this);
        findViewById(R.id.VINCodeContinueButton).setOnClickListener(this);
        findViewById(R.id.verificationBeginVerificationButton).setOnClickListener(this);
        findViewById(R.id.installDoneButton).setOnClickListener(this);
        this.accessoryControl = new AccessoryControl(this, getUIHandler());
        httpclient = new httpClient(this);
        this.topLayout = (LinearLayout) findViewById(R.id.topLayout);
        this.topLayout.setBackgroundColor(0xff000000);
        if (!Restart) {
            Features.initFeatureCodeTable();
            this.params.initializeRunningParams();
        }
        IntentFilter filter = new IntentFilter(AccessoryControl.ACTION_USB_PERMISSION);
        filter.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
        filter.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
        Log.i(TAG, "register UsbReceiver..");
        registerReceiver(this.UsbReceiver, filter);

        this.mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "IdleSmartWakeLock");
        if (DebugLog) {
            Log.i(TAG, "WakeLock acquired? " + this.mWakeLock);
        }
        if (!Restart) {
            this.initialScreenBrightness = getScreenBrightness();
            if (DebugLog) {
                Log.i(TAG, "initialScreenBrightness = " + this.initialScreenBrightness);
            }
            this.initialScreenTimeout = getScreenTimeout();
            if (DebugLog) {
                Log.i(TAG, "initialScreenTimeout = " + this.initialScreenTimeout);
            }
        }
        startScreenHandler(params.getCurrentParamValue(Params.PARAM_DimTabletScreen));
        PrefUtils.setApkUpdateState(0, getApplicationContext());
        Restart = true;
        Log.i(TAG, "<<==onCreate done");
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (DebugLog) {
            Log.i(TAG, "==>>onConfigurationChanged");
        }
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        if (DebugLog) {
            Log.i(TAG, "<<==onConfigurationChanged");
        }
    }

    protected void onStart() {
        super.onStart();
        if (DebugLog) {
            Log.i(TAG, "==>>onStart");
        }
        if (DebugLog) {
            Log.i(TAG, "<<==onStart");
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (DebugLog) {
            Log.i(TAG, "==>onNewIntent" + intent + intent.getAction());
        }
        if ("android.hardware.usb.action.USB_ACCESSORY_ATTACHED".equals(intent.getAction())) {
            Log.e(TAG, "==>>onNewIntent: " + intent.getAction());
            gateway_restarting = false;
        }
        if (DebugLog) {
            Log.i(TAG, "<<==onNewIntent");
        }
    }

    protected void onRestart() {
        super.onRestart();
        if (DebugLog) {
            Log.i(TAG, "==>>onRestart");
        }
        Restart = true;
        if (DebugLog) {
            Log.i(TAG, "<<==onRestart");
        }
    }

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "==>>onResume");
        if (DebugLog) {
            showRestartParams();
        }
        executeDelayed();
        HasFocus = true;
        if (!this.mWakeLock.isHeld()) {
            this.mWakeLock.acquire();
        }
        resetScreenTimeout(params.getCurrentParamValue(Params.PARAM_DimTabletScreen));
        PasswordValid = false;
        PackageUpdatePending = false;
        GatewayUpdatePending = false;
        enableStatusBar(CurrentStatusBarFlag);
        if (CurrentGatewayStatus.trim().isEmpty()) {
            setGatewayStatus("Gateway Disconnected");
        } else {
            setGatewayStatus(CurrentGatewayStatus);
        }
        setEngineStatus(CurrentEngineStatus);
        setConnectivityStatus(CurrentConnectivityStatus, ConnectivityIndicator);
        enableDashboard(CurrentDashboardFlag);
        selectRunning(CurrentDashboardFragment);
        if (CurrentSettingsFlag) {
            enableDashboard(true);
            selectRunning(1);
            enableSettings(false);
        } else {
            enableSettings(CurrentSettingsFlag);
        }
        selectSettingsMode(0);
        selectActivationFragment(ActivationStep.NONE);

        //TODO Display powerOn button,it was replaced by KillSwitchMode.POWER_OFF - Original: selectKillswitchMode(KillSwitchMode.CONNECTED);
        selectKillswitchMode(KillSwitchMode.POWER_OFF);

        Log.i(TAG, "   onResume::Do we need to update gateway connection...");
        if (gateway_connected) {
            Log.i(TAG, "onResume: we are still connected");
            connected();
        } else {
            connectUSB();
            if (gateway_connected) {
                Log.i(TAG, "onResume: We (re-)Connected the USB!");
            } else {
                Log.d(TAG, "onResume: ##################### NEW INSTANCE:AccessoryControl ###########################");
                this.accessoryControl = new AccessoryControl(this, getUIHandler());
                connectUSB();
                Log.d(TAG, "onResume: ##################### New Instance is running ###########################");
                startConnectionMonitoring();
            }
        }
        UpdateConnectivityStatus();
        StartPhoneHome();
        if (packagemanagernag) {
            if (isAnyBloatware()) {
                Log.i(TAG, "   Bloatware exists - turn on nag screen");
                findViewById(R.id.packagemanagerFragment).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.packagemanagerFragment).setVisibility(View.GONE);
            }
        }
        isRefreshAvailable();
        Log.i(TAG, "        (re-)connectUSB::send APICMD_SYNC.. (Request data from Gateway");
        this.accessoryControl.writeCommand(AccessoryControl.APICMD_SYNC, 0, 1);
        if (DebugLog) {
            Log.i(TAG, "<<==end OnResume");
        }
    }

    public void connectUSB() {
        Log.i(TAG, "    --> <connectUSB>");
        Log.i(TAG, "      PackageUpdatePending:" + (PackageUpdatePending ? "true" : "false"));
        if (!(PackageUpdatePending || gateway_connected)) {
            Log.w(TAG, "        connectUSB::Not connected - openBufferOutPutStream AccessoryControl and attempt to reconnect to gateway..");
            AccessoryControl.OpenStatus status = this.accessoryControl.open();
            Log.i(TAG, "        connectUSB::status=" + status.toString());
            if (status == AccessoryControl.OpenStatus.CONNECTED) {
                Log.i(TAG, "        connectUSB::we are now connected to gateway");
                gateway_connected = true;
                Log.i(TAG, "        connectUSB::send APICMD_POWERON..");
                this.accessoryControl.writeCommand(AccessoryControl.APICMD_POWERON, 0, 1);
                connected();
                enableDashboard(true);
                selectRunning(1);
                enableSettings(false);
                Log.i(TAG, "        connectUSB::send APICMD_SYNC.. (Request data from Gateway");
                this.accessoryControl.writeCommand(AccessoryControl.APICMD_SYNC, 0, 1);
            } else if (status == AccessoryControl.OpenStatus.REQUESTING_PERMISSION) {
                Log.w(TAG, "        connectUSB::Requesting Permission");
                disconnected();
            } else if (status == AccessoryControl.OpenStatus.NO_ACCESSORY) {
                Log.e(TAG, "        connectUSB::*** Error: Cannot connect to Gateway: NO ACCESSORY");
                disconnected();
            } else {
                Log.e(TAG, "        connectUSB::*** Error: Cannot connect to Gateway: unknown cause");
                disconnected();
                showError(status);
            }
        }
        Log.i(TAG, "    <-- <connectUSB>");
    }

    private void startConnectionMonitoring() {
        if (!gateway_connected) {
            Log.d(TAG, "<startConnectionMonitoring>::not connected");
            Log.i(TAG, "   PackageUpdatePending:" + (PackageUpdatePending ? "true" : "false"));
            if (!PackageUpdatePending) {
                Log.d(TAG, "   accessoryControl.appIsClosing()..");
                this.accessoryControl.appIsClosing();
                Log.d(TAG, "   accessoryControl.close()..");
                this.accessoryControl.close();
                gateway_connected = false;
                Log.d(TAG, "   Start USBReconnectHandler runnable..");
                this.USBReconnectHandler.removeCallbacks(this.USBReconnectRunnable);
                monitor_iter = 0;
                this.USBReconnectHandler.postDelayed(this.USBReconnectRunnable, MONITOR_RATE);
            }
        }
    }

    public void showRestartParams() {
        Log.i(TAG, "   Restart:   " + (Restart ? "true" : "false"));
        Log.i(TAG, "   KioskMode: " + (KioskMode ? "true" : "false"));
        Log.i(TAG, "   demo_mode: " + (demo_mode ? "true" : "false"));
        Log.i(TAG, "   PackageUpdatePending:" + (PackageUpdatePending ? "true" : "false"));
        Log.i(TAG, "   GatewayUpdatePending:" + (GatewayUpdatePending ? "true" : "false"));
        try {
            Log.i(TAG, "   current APK version: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            Log.i(TAG, "   server APK version: [" + PrefUtils.getServerUpdateVersion(getApplicationContext()) + "]");
            Log.i(TAG, "   gateway_connected:   " + (gateway_connected ? "true" : "false"));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void onPause() {
        super.onPause();
        Log.i(TAG, "==>>onPause");
        if (DebugLog) {
            showRestartParams();
        }
        if (KioskMode && !PackageUpdatePending && gateway_connected) {
            ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).moveTaskToFront(getTaskId(), 0);
        }
        closeMediaPlayer();
        if (DebugLog) {
            Log.i(TAG, "<<==end OnPause");
        }
    }

    protected void onStop() {
        super.onStop();
        if (DebugLog) {
            Log.i(TAG, "==>>onStop");
        }
        if (this.maintDialog != null) {
            this.maintDialog.dismiss();
        }
        if (DebugLog) {
            Log.i(TAG, "<<==end OnStop");
        }
    }

    public void exit() {
        Log.e(TAG, "###################### exit() ##############################");
        AppContext.instance.stopKioskService();
        finish();
    }

    public void exitApp() {
        Log.e(TAG, "###################### exitApp() ##############################");
        AppContext.instance.stopKioskService();
        moveTaskToBack(true);
        Process.killProcess(Process.myPid());
        java.lang.System.exit(0);
    }

    public void abort() {
        Log.e(TAG, "###################### abort() ##############################");
        AppContext.instance.stopKioskService();
        moveTaskToBack(true);
        Process.killProcess(Process.myPid());
        java.lang.System.exit(1);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (DebugLog) {
            Log.i(TAG, "==>>onDestroy");
        }
        Log.i(TAG, "Cancel PhoneHome()..");
        CancelPhoneHome();
        Log.w(TAG, "Send APICMD_DISCONNECT to Gateway..");
        this.accessoryControl.writeCommand(AccessoryControl.APICMD_DISCONNECT, 0, 0);
        Log.i(TAG, "accessoryControl.appIsClosing()..");
        this.accessoryControl.appIsClosing();
        Log.i(TAG, "accessoryControl.close()..");
        this.accessoryControl.close();
        this.USBReconnectHandler.removeCallbacks(this.USBReconnectRunnable);
        gateway_connected = false;
        Log.i(TAG, "unregister UsbReceiver..");
        try {
            unregisterReceiver(this.UsbReceiver);
        } catch (IllegalArgumentException e) {
        }
        if (this.mWakeLock.isHeld()) {
            Log.i(TAG, "call WakeLock.release()..");
            this.mWakeLock.release();
        }
        AppContext.instance.stopKioskService();
        if (DebugLog) {
            Log.i(TAG, "<<==end OnDestroy");
        }
    }
    /**
     * Kiosk mode: When the window is focused - hide the Android's NavBar
     * @param hasFocus
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        HasFocus = hasFocus;
        if (DebugLog) {
            Log.i(TAG, "==>>OnWindowFocusChanged::hasFocus=" + (hasFocus ? "true" : "false"));
        }
        if (hasFocus) {
            hideNavBar();
        }
        if (DebugLog) {
            Log.i(TAG, "<<== OnWindowFocusChanged");
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        resetScreenTimeout(params.getCurrentParamValue(Params.PARAM_DimTabletScreen));
        if (httpclient.dialog != null && httpclient.dialog.isShowing()) {
            httpclient.dialog.cancel();
        }
        return false;
    }

    /**
     * Hide NavBar after 100ms
     */
    private void executeDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.hideNavBar();
            }
        }, 100);
    }

    private void hideNavBar() {
        if (VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            if (KioskMode) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    public void InstallAndActivate(boolean activate_flag) {
        if (!activate_flag) {
            SystemActivationFlag = false;
            Log.i(TAG, "VerifyActivation: NOT ACTIVATED");
            selectActivationFragment(ActivationStep.ACTIVATION);
        } else if (!SystemActivationFlag) {
            SystemActivationFlag = true;
            Log.i(TAG, "VerifyActivation: ACTIVATED");
            enableStatusBar(true);
            enableDashboard(true);
            selectRunning(1);
        }
    }

    // region PhoneHome - send heart-beat to PCB
    private void StartPhoneHome() {
        this.EThandler.removeCallbacks(this.ETrunnable);
        SetNextPhoneHome();
    }

    public void ReschedulePhoneHome(int minutes) {
        Log.i(TAG, "--> ReschedulePhoneHome for Retry");
        long repeatdelay = (long) ((minutes * 60) * 1000);
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(date.getTimeInMillis() + repeatdelay);
        if (date.before(CalcNextPhoneHome())) {
            Log.i(TAG, "PhoneHome retry rescheduled for: " + date.getTime().toString());
            this.EThandler.removeCallbacks(this.ETrunnable);
            this.EThandler.postDelayed(this.ETrunnable, repeatdelay);
            Log.i(TAG, "Next PhoneHome attempt in (msecs):" + Long.toString(repeatdelay));
        }
    }

    public void SetNextPhoneHome() {
        Calendar date = CalcNextPhoneHome();

        // Send Next Sync time to PCB
        Log.i(TAG, "Next PhoneHome scheduled for: " + date.getTime().toString());
        byte[] data = new byte[2];
        SyncNext = (date.get(Calendar.HOUR_OF_DAY) * 60) + date.get(Calendar.MINUTE);
        data[0] = (byte) ((SyncNext >> 8) & 255);
        data[1] = (byte) (SyncNext & 255);
        this.accessoryControl.writeCommand(AccessoryControl.APIDATA_SYNC_NEXT, data[0], data[1]);

        // Re-add the timer for next time
        CancelPhoneHome();
        long delay = date.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        this.EThandler.postDelayed(this.ETrunnable, delay);
        Log.i(TAG, "Next PhoneHome in (msecs):" + Long.toString(delay));
    }

    private Calendar CalcNextPhoneHome() {
        Calendar date = Calendar.getInstance();
        int hour = SyncStart / 60;
        int min = SyncStart - (hour * 60);
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, min);
        while (date.before(Calendar.getInstance())) {
            date.add(Calendar.MINUTE, SyncTTL);
        }
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        if (date.before(tomorrow)) {
            return date;
        }
        date = tomorrow;
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, min);
        return date;
    }

    private void CancelPhoneHome() {
        this.EThandler.removeCallbacks(this.ETrunnable);
    }
    // endregion

    private int getScreenBrightness() {
        int curBrightnessValue = 0;
        try {
            curBrightnessValue = System.getInt(getContentResolver(), "screen_brightness");
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return curBrightnessValue;
    }

    private void setScreenBrightness(int brightness) {
    }

    private void setScreenOn(boolean enable) {
        if (enable) {
            findViewById(R.id.fullScreen).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.fullScreen).setVisibility(View.GONE);
        }
        this.ScreenOn = enable;
    }

    private int getScreenTimeout() {
        int curTimeoutValue = 0;
        try {
            curTimeoutValue = System.getInt(getContentResolver(), "screen_off_timeout");
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return curTimeoutValue;
    }

    private void setScreenTimeout(int timeout_seconds) {
        System.putInt(getContentResolver(), "screen_off_timeout", timeout_seconds * 1000);
        Log.i(TAG, "Screen Timeout changed - seconds=" + timeout_seconds);
        resetScreenTimeout(timeout_seconds);
    }

    private void setSoundOn(boolean enable) {
        AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (enable) {
            aManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } else {
            aManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
    }

    private void startScreenHandler(int delay_secs) {
        this.isScreenOn = true;

        this.screentimeoutHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.updateScreenTimeout();
            }
        }, (long) (delay_secs * 1000));
    }

    private void updateScreenTimeout() {
        this.screentimeoutHandler.post(this.timeoutRunnable);
    }

    private void resetScreenTimeout(int delay_secs) {
        this.screentimeoutHandler.removeCallbacks(this.timeoutRunnable);
        if (!this.isScreenOn) {
            setScreenBrightness(this.initialScreenBrightness);
        }
        this.screentimeoutHandler.postDelayed(this.timeoutRunnable, (long) (delay_secs * 1000));
    }

    public void wakeup() {
        PowerManager mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (this.mTempWakeLock == null) {
            this.mTempWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "IdleSmartTempWakeLock");
        }
        if (this.mTempWakeLock.isHeld()) {
            this.mTempWakeLock.release();
        }
        this.mTempWakeLock.acquire();
    }

    public void back2sleep() {
        if (this.mTempWakeLock.isHeld()) {
            this.mTempWakeLock.release();
        }
    }

    public void onClick(View v) {
        int i;
        switch (v.getId()) {
            case R.id.activationEnterCodeButton /*2131361795*/:
                selectActivationFragment(ActivationStep.ACTIVATION_CODE);
                findViewById(R.id.activationFragment).setVisibility(View.GONE);
                findViewById(R.id.activationCodeFragment).setVisibility(View.VISIBLE);
            	break;
			case R.id.activationCodeContinueButton /*2131361798*/:
                selectActivationFragment(ActivationStep.VIN_CODE);
                findViewById(R.id.activationCodeFragment).setVisibility(View.GONE);
                findViewById(R.id.VINCodeFragment).setVisibility(View.VISIBLE);
            	break;
			case R.id.dashboardButton /*2131361812*/:
                this.test_mode_counter = 0;
                this.maint_mode_counter = 0;
                // TODO Enable dashboardButton to access dashboard
                SystemActivationFlag =true;
                demo_mode = true;
                if (SystemActivationFlag || demo_mode) {
                    selectKillswitchMode(KillSwitchMode.CONNECTED);
                    enableStatusBar(true);
                    enableDashboard(true);
                    selectRunning(1);
                    enableSettings(false);
                    selectActivationFragment(ActivationStep.NONE);
                    PasswordValid = false;
                }
            	break;
			case R.id.settingsButton /*2131361814*/:
                this.test_mode_counter = 0;
                this.maint_mode_counter = 0;
                if (SystemActivationFlag || demo_mode) {
                    selectKillswitchMode(KillSwitchMode.CONNECTED);
                    enableStatusBar(false);
                    enableDashboard(false);
                    selectRunning(0);
                    enableSettings(true);
                    selectActivationFragment(ActivationStep.NONE);
                    this.settings_menu1_index = 0;
                    this.settings_menu2_index = 0;
                    selectSettingsMode(Functionality.CABIN_COMFORT);
                    PasswordValid = false;
                }
            	break;

            case R.id.killSwitchButton /*2131361816*/:
                this.test_mode_counter = 0;
                this.maint_mode_counter = 0;
                // TODO Enable KillswitchMode button
                SystemActivationFlag =true;
                if (SystemActivationFlag) {
                    selectKillswitchMode(KillSwitchMode.KILL_SWITCH);
                }
            	break;
			case R.id.installDoneButton /*2131361837*/:
                findViewById(R.id.installFragment).setVisibility(View.GONE);
                if (ValidActivationProcess) {
                    this.accessoryControl.writeCommand(AccessoryControl.APICMD_ACTIVATE, 0, 1);
                    Log.i(TAG, "APICMD_ACTIVATE = 1");
                    enableDashboard(true);
                    selectRunning(1);
                    httpclient.PhoneHome(0, false);
                    return;
                }
                InstallAndActivate(SystemActivationFlag);
            	break;
			case R.id.poweronButton /*2131361943*/:
                selectKillswitchMode(KillSwitchMode.CONNECTED);
                enableStatusBar(true);
                enableDashboard(true);
                findViewById(R.id.fullScreen).setVisibility(View.VISIBLE);
                findViewById(R.id.poweronButton).setVisibility(View.GONE);
                this.accessoryControl.writeCommand(AccessoryControl.APICMD_POWERON, 0, 1);
            	break;
			case R.id.idlesmartButton /*2131361944*/:
                this.test_mark_counter += 1;
                Log.i(TAG, "************************************ TEST MARK: " + this.test_mark_counter);
                if (gateway_connected) {
                    CANLogFile canLogFile = new CANLogFile(MainActivity.this, FileName.CANLOGNAME, FileName.CANLOGPATH, TAG);
                    canLogFile.write("TEST MARK: " + Integer.toString(this.test_mark_counter));
                    if (test_mode) {
                        setGatewayStatus("Gateway Connected");
                        test_mode = false;
                        this.test_mode_counter = 0;
                        this.maint_mode_counter = 0;
                        this.accessoryControl.writeCommand(AccessoryControl.APICMD_TESTMODE, 0, 0);
                        return;
                    }
                    i = this.test_mode_counter + 1;
                    this.test_mode_counter = i;
                    if (i >= 5) {
                        setGatewayStatus("Test Mode");
                        test_mode = true;
                        this.accessoryControl.writeCommand(AccessoryControl.APICMD_TESTMODE, 0, 1);
                    }
                } else if (demo_mode) {
                    demo_mode = false;
                    setGatewayStatus("Gateway Disconnected");
                    enableDashboard(false);
                    enableStatusBar(false);
                    selectRunning(0);
                    enableSettings(false);
                    this.test_mode_counter = 0;
                    this.maint_mode_counter = 0;
                } else {
                    i = this.test_mode_counter + 1;
                    this.test_mode_counter = i;
                    if (i >= 5) {
                        demo_mode = true;
                        setGatewayStatus("Demo Mode");
                        enableDashboard(true);
                        enableStatusBar(true);
                        selectRunning(1);
                        enableSettings(false);
                    }
                }
            	break;
			case R.id.updateInstallButton /*2131361948*/:
                installUpdate();
            	break;
			case R.id.maintButton /*2131361949*/:
                i = this.maint_mode_counter + 1;
                this.maint_mode_counter = i;
                if (i >= 3) {
                    openMaintDialog();
                    this.maint_mode_counter = 0;
                }
            	break;
			case R.id.passwordReturnButton /*2131361954*/:
                PasswordValid = false;
            	break;
			case R.id.cabinComfortFunction /*2131361959*/:
                PasswordValid = false;
                if (CurrentDashboardFragment == 2) {
                    selectRunning(1);
                } else {
                    selectRunning(2);
                }
            	break;
			case R.id.coldWeatherGuardFunction /*2131361960*/:
                PasswordValid = false;
                if (CurrentDashboardFragment == 3) {
                    selectRunning(1);
                } else {
                    selectRunning(3);
                }
            	break;
			case R.id.batteryProtectFunction /*2131361961*/:
                PasswordValid = false;
                if (CurrentDashboardFragment == 4) {
                    selectRunning(1);
                } else {
                    selectRunning(4);
                }
            	break;
			case R.id.cabinComfortEnableButton /*2131361972*/:
            	break;
			case R.id.ccFragStopButton /*2131361989*/:
                Log.i(TAG, "-->cabinComfortEnableButton");
                if (params.getCurrentParamValue(Params.PARAM_FleetCabinComfort) != 0 || this.CabinComfortMode == Modes.ENGINE_RUNNING || ValidPassword()) {
                    setFunctionMode(Functionality.CABIN_COMFORT, toggleFunctionMode(this.CabinComfortMode));
                    updateFunctionModes();
                    PasswordValid = false;
                }
            	break;
			case R.id.coldWeatherGuardEnableButton /*2131361979*/:
            	break;
			case R.id.cwgFragStopButton /*2131361996*/:
                Log.i(TAG, "-->coldWeatherGuardEnableButton");
                if (this.ColdWeatherGuardMode == Modes.ENGINE_RUNNING || ValidPassword()) {
                    setFunctionMode(Functionality.COLD_WEATHER_GUARD, toggleFunctionMode(this.ColdWeatherGuardMode));
                    updateFunctionModes();
                    PasswordValid = false;
                }
            	break;
			case R.id.batteryProtectEnableButton /*2131361986*/:
            	break;
			case R.id.bpFragStopButton /*2131362005*/:
                Log.i(TAG, "-->batteryProtectEnableButton");
                if (this.BatteryProtectMode == Modes.ENGINE_RUNNING || ValidPassword()) {
                    setFunctionMode(Functionality.BATTERY_PROTECT, toggleFunctionMode(this.BatteryProtectMode));
                    updateFunctionModes();
                    PasswordValid = false;
                }
            	break;
			case R.id.ccFragTargetTemperatureDecrButton /*2131361991*/:
                if (this.params.isCabinTempCommonDecrValid(this.params.getCurrentParamValue(Params.PARAM_CabinTargetTemp)) || ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 3);
                }
            	break;
			case R.id.ccFragTargetTemperatureIncrButton /*2131361992*/:
                if (this.params.isCabinTempCommonIncrValid(this.params.getCurrentParamValue(Params.PARAM_CabinTargetTemp)) || ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 3);
                }
            	break;
			case R.id.cwgFragMinTempDecrButton /*2131361998*/:
            	break;
			case R.id.cwgFragMinTempIncrButton /*2131361999*/:
                if (ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 11);
                }
            	break;
			case R.id.cwgFragIdealTempDecrButton /*2131362001*/:
            	break;
			case R.id.cwgFragIdealTempIncrButton /*2131362002*/:
                if (ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 10);
                }
            	break;
			case R.id.bpFragSetpointDecrButton /*2131362007*/:
            	break;
			case R.id.bpFragSetpointIncrButton /*2131362008*/:
                if (ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 8);
                }
            	break;
			case R.id.bpEngineRuntimeDecrButton /*2131362010*/:
            	break;
			case R.id.bpEngineRuntimeIncrButton /*2131362011*/:
                if (ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 9);
                }
            	break;
			case R.id.settingsMenu11 /*2131362014*/:
                selectMenu1Entry(1);
            	break;
			case R.id.settingsMenu12 /*2131362017*/:
                selectMenu1Entry(2);
            	break;
			case R.id.settingsMenu13 /*2131362020*/:
                selectMenu1Entry(3);
            	break;
			case R.id.settingsMenu14 /*2131362023*/:
                selectMenu1Entry(4);
            	break;
			case R.id.settingsMenu15 /*2131362026*/:
                selectMenu1Entry(5);
            	break;
			case R.id.settingsMenu16 /*2131362029*/:
                selectMenu1Entry(6);
            	break;
			case R.id.settingsMenu17 /*2131362032*/:
                selectMenu1Entry(7);
            	break;
			case R.id.settingsMenu21 /*2131362039*/:
                selectMenu2Entry(1);
            	break;
			case R.id.settingsMenu22 /*2131362042*/:
                selectMenu2Entry(2);
            	break;
			case R.id.settingsMenu23 /*2131362045*/:
                selectMenu2Entry(3);
            	break;
			case R.id.settingsMenu24 /*2131362048*/:
                selectMenu2Entry(4);
            	break;
			case R.id.settingsMenu25 /*2131362051*/:
                selectMenu2Entry(5);
            	break;
			case R.id.settingsMenu26 /*2131362054*/:
                selectMenu2Entry(6);
            	break;
			case R.id.settingsMenu27 /*2131362057*/:
                selectMenu2Entry(7);
            	break;
			case R.id.settingsEntryEnableButton /*2131362065*/:
                if (PasswordEnable && ValidPassword()) {
                    updateParamValue(v.getId(), this.param_id);
                    saveParamValue(this.param_id);
                }
            	break;
			case R.id.settingsEntryDisableButton /*2131362068*/:
                if (PasswordEnable && ValidPassword()) {
                    updateParamValue(v.getId(), this.param_id);
                    saveParamValue(this.param_id);
                }
            	break;
			case R.id.settingsEntryDecrementButton /*2131362072*/:
                updateParamValue(v.getId(), this.param_id);
            	break;
			case R.id.settingsEntryIncrementButton /*2131362073*/:
                updateParamValue(v.getId(), this.param_id);
            	break;
			case R.id.settingsEntryDoneButton /*2131362074*/:
                saveParamValue(this.param_id);
            	break;
			case R.id.packagemanagernagEscapeButton /*2131362083*/:
                packagemanagernag = false;
                findViewById(R.id.packagemanagerFragment).setVisibility(View.GONE);
            	break;

			case R.id.packagemanagerButton /*2131362084*/:
                removeBloatware();
            	break;
			case R.id.killswitchButton /*2131362086*/:
                selectKillswitchMode(KillSwitchMode.KILL_SWITCH_CONFIRMED);
            	break;
			case R.id.poweroffButton /*2131362088*/:
                this.accessoryControl.writeCommand(AccessoryControl.APICMD_ENGINE_OFF, 0, 1);
                selectKillswitchMode(KillSwitchMode.POWER_OFF);
                findViewById(R.id.fullScreen).setVisibility(View.GONE);
                this.accessoryControl.writeCommand(AccessoryControl.APICMD_POWEROFF, 0, 0);
            	break;
			case R.id.verificationBeginVerificationButton /*2131362090*/:
                selectActivationFragment(ActivationStep.INSTALL);
                findViewById(R.id.verificationFragment).setVisibility(View.GONE);
                findViewById(R.id.installFragment).setVisibility(View.VISIBLE);
                ValidActivationProcess = false;
                StartVerificationProcess();
            	break;
			case R.id.VINCodeContinueButton /*2131362093*/:
                selectActivationFragment(ActivationStep.VERIFICATION);
                findViewById(R.id.VINCodeFragment).setVisibility(View.GONE);
                findViewById(R.id.verificationFragment).setVisibility(View.VISIBLE);
            default:

        }
    }

    private void connected() {
        Log.i(TAG, "connected()");
        if (demo_mode) {
            setGatewayStatus("Demo Mode");
        } else {
            setGatewayStatus("Gateway Connected");
        }
    }

    private void disconnected() {
        if (demo_mode) {
            setGatewayStatus("Demo Mode");
            return;
        }
        if (gateway_restarting) {
            Log.i(TAG, "disconnected() - gateway restarting");
            setGatewayStatus("Gateway Restarting");
        } else {
            Log.i(TAG, "disconnected()");
            setGatewayStatus("Gateway Disconnected");
        }
        setEngineStatus("");
        if (this.GatewayMode == GatewayModes.CABIN_COMFORT) {
            setFunctionMode(Functionality.CABIN_COMFORT, 3);
        } else if (this.GatewayMode == GatewayModes.COLD_WEATHER_GUARD) {
            setFunctionMode(Functionality.COLD_WEATHER_GUARD, 3);
        } else if (this.GatewayMode == GatewayModes.BATTERY_PROTECT) {
            setFunctionMode(Functionality.BATTERY_PROTECT, 3);
        }
        updateFunctionModes();
        enableDashboard(false);
        enableSettings(false);
    }

    private void setGatewayStatus(String str) {
        CurrentGatewayStatus = str;
        ((TextView) findViewById(R.id.gatewayStatus)).setText(str);
        Log.i(TAG, "setGatewayStatus = " + str);
    }

    private void setEngineStatus(String str) {
        CurrentEngineStatus = str;
        ((TextView) findViewById(R.id.engineStatus)).setText(str);
    }

    private void setConnectivityStatus(String str, int indicator) {
        CurrentConnectivityStatus = str;
        ConnectivityIndicator = indicator;
        ((TextView) findViewById(R.id.connectivityStatus)).setText(str);
        if (ConnectivityIndicator == GOOD_CONNECTIVITY) {
            ((TextView) findViewById(R.id.connectivityStatus)).setTextColor(getResources().getColor(R.color.goodConnectivity));
        } else if (ConnectivityIndicator == BAD_CONNECTIVITY) {
            ((TextView) findViewById(R.id.connectivityStatus)).setTextColor(getResources().getColor(R.color.badConnectivity));
        } else {
            ((TextView) findViewById(R.id.connectivityStatus)).setTextColor(getResources().getColor(R.color.unknownConnectivity));
        }
    }

    private void showError(AccessoryControl.OpenStatus status) {
        Log.i(TAG, "showError: status =" + status.toString());
        if (!demo_mode) {
            setGatewayStatus("Cannot connect to Gateway");
        }
    }

    private void enableStatusBar(boolean enable) {
        CurrentStatusBarFlag = enable;
        findViewById(R.id.statusBar).setVisibility(View.VISIBLE);
    }

    private void enableDashboard(boolean enable) {
        CurrentDashboardFlag = enable;
        enableStatusBar(true);
        if (!gateway_connected && !demo_mode) {
            findViewById(R.id.dashboardFragment).setVisibility(View.GONE);
        } else if (enable) {
            findViewById(R.id.dashboardFragment).setVisibility(View.VISIBLE);
            selectSettingsMode(0);
        } else {
            findViewById(R.id.dashboardFragment).setVisibility(View.GONE);
        }
    }

    /**
     * Display fragment on Dashboard
     * @param fragment
     * - 1: RunningFragment - Summary of 3 functionalities
     * - 2: Cabin Comfort - Summary of Cabin comfort
     * - 3: Cold Weather - Summary of Cold Weather
     * - 4: Battery Protect - Summary of Battery Protect
     */
    private void selectRunning(int fragment) {
        CurrentDashboardFragment = fragment;
        findViewById(R.id.runningFragment).setVisibility(View.GONE);
        findViewById(R.id.cabinComfortFragment).setVisibility(View.GONE);
        findViewById(R.id.coldWeatherGuardFragment).setVisibility(View.GONE);
        findViewById(R.id.batteryProtectFragment).setVisibility(View.GONE);
        updateFunctionModes();
        viewFragmentParamValue(1);
        viewFragmentParamValue(2);
        viewFragmentParamValue(3);
        viewFragmentParamValue(4);
        if (fragment != 0) {
            enableDashboard(true);
            switch (fragment) {
                case 1 /*1*/:
                    findViewById(R.id.runningFragment).setVisibility(View.VISIBLE);
                    break;
                case 2 /*2*/:
                    findViewById(R.id.cabinComfortFragment).setVisibility(View.VISIBLE);
                    break;
                case 3 /*3*/:
                    findViewById(R.id.coldWeatherGuardFragment).setVisibility(View.VISIBLE);
                    break;
                case 4 /*4*/:
                    findViewById(R.id.batteryProtectFragment).setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
        // TODO Display each fragment when click on subFragment (cabinComfortFragment, coldWeatherGuardFragment, batteryProtectFragment )
        //enableDashboard(false);
        PasswordValid = false;
    }

    private void selectKillswitchMode(int mode) {
        switch (mode) {
            case KillSwitchMode.CONNECTED /*0*/:
                findViewById(R.id.fullScreen).setVisibility(View.VISIBLE);
                findViewById(R.id.killswitchFragment).setVisibility(View.GONE);
                findViewById(R.id.poweroffFragment).setVisibility(View.GONE);
                findViewById(R.id.poweronFragment).setVisibility(View.GONE);
                break;
            case KillSwitchMode.KILL_SWITCH /*1*/:
                enableStatusBar(false);
                enableDashboard(false);
                enableSettings(false);
                findViewById(R.id.killswitchFragment).setVisibility(View.VISIBLE);
                break;
            case KillSwitchMode.KILL_SWITCH_CONFIRMED /*2*/:
                enableStatusBar(false);
                enableDashboard(false);
                enableSettings(false);
                findViewById(R.id.killswitchFragment).setVisibility(View.GONE);
                findViewById(R.id.poweroffFragment).setVisibility(View.VISIBLE);
                break;
            case KillSwitchMode.POWER_OFF /*3*/:
                enableStatusBar(false);
                enableDashboard(false);
                enableSettings(false);
                findViewById(R.id.fullScreen).setVisibility(View.GONE);
                findViewById(R.id.poweroffFragment).setVisibility(View.GONE);
                findViewById(R.id.poweronFragment).setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    // region Password Dialog for inputting password

    public void openPasswordDialog() {
        PasswordDialogFragment passwordDialogFragment = PasswordDialogFragment.newInstance();
        passwordDialogFragment.show(this.fragmentTransaction, "PasswordDialog");
    }
    @Override
    public void onReturnListener() {
        MainActivity.PasswordValid = false;
    }

    @Override
    public void onContinueListener(int pwtemp) {
        MainActivity.PasswordValid = pwtemp == MainActivity.Password ? true : false;
        if (MainActivity.test_mode && pwtemp == 8800) {
            MainActivity.PasswordEnable = false;
            MainActivity.PasswordValid = true;
        }
    }

    // endregion

    private boolean ValidPassword() {
        if (!PasswordEnable || PasswordValid) {
            return true;
        }
        openPasswordDialog();
        return false;
    }

    private void updateFunctionModes() {
        Button ccEnableButton = (Button) findViewById(R.id.cabinComfortEnableButton);
        Button cwEnableButton = (Button) findViewById(R.id.coldWeatherGuardEnableButton);
        Button bpEnableButton = (Button) findViewById(R.id.batteryProtectEnableButton);
        Button ccFragStopButton = (Button) findViewById(R.id.ccFragStopButton);
        Button cwFragStopButton = (Button) findViewById(R.id.cwgFragStopButton);
        Button bpFragStopButton = (Button) findViewById(R.id.bpFragStopButton);

        setCabinComfortMode(ccEnableButton, ccFragStopButton);
        setColdWeatherGuardMode(cwEnableButton, cwFragStopButton);
        setBatteryProtectMode(bpEnableButton, bpFragStopButton);
    }

    /**
     * Set Battery Protect Mode State
     * @param bpEnableButton
     * @param bpFragStopButton
     */

    private void setBatteryProtectMode(Button bpEnableButton, Button bpFragStopButton) {
        switch (this.BatteryProtectMode) {
            case Modes.DISABLED /*0*/:
                findViewById(R.id.batteryProtectControl).setBackground(getResources().getDrawable(R.color.disabledFunction));
                findViewById(R.id.batteryProtectFragment).setBackground(getResources().getDrawable(R.color.disabledFunction));
                findViewById(R.id.batteryProtectFunctionIndicator).setBackground(getResources().getDrawable(R.color.disabled));
                findViewById(R.id.batteryProtectIndicator).setBackground(getResources().getDrawable(R.drawable.disabled_indicator_shape));
                ((TextView) findViewById(R.id.batteryProtectStatus)).setText("DISABLED");
                bpEnableButton.setBackground(getResources().getDrawable(R.drawable.disabled_button_shape));
                bpEnableButton.setText("ENABLE");
                bpFragStopButton.setBackground(getResources().getDrawable(R.drawable.disabled_button_shape));
                bpFragStopButton.setText("ENABLE");
            	break;
			case Modes.ENGAGED /*1*/:
			case Modes.ENGINE_STOPPED /*3*/:
                findViewById(R.id.batteryProtectControl).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.batteryProtectFragment).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.batteryProtectFunctionIndicator).setBackground(getResources().getDrawable(R.color.enabled));
                findViewById(R.id.batteryProtectIndicator).setBackground(getResources().getDrawable(R.drawable.enabled_indicator_shape));
                ((TextView) findViewById(R.id.batteryProtectStatus)).setText("MONITORING");
                bpEnableButton.setBackground(getResources().getDrawable(R.drawable.enabled_button_shape));
                bpEnableButton.setText("DISABLE");
                bpFragStopButton.setBackground(getResources().getDrawable(R.drawable.enabled_button_shape));
                bpFragStopButton.setText("DISABLE");
            	break;
			case Modes.ENGINE_RUNNING /*2*/:
                findViewById(R.id.batteryProtectControl).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.batteryProtectFragment).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.batteryProtectFunctionIndicator).setBackground(getResources().getDrawable(R.color.active));
                findViewById(R.id.batteryProtectIndicator).setBackground(getResources().getDrawable(R.drawable.active_indicator_shape));
                ((TextView) findViewById(R.id.batteryProtectStatus)).setText("RUNNING ENGINE");
                bpEnableButton.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
                bpEnableButton.setText("STOP ENGINE");
                bpFragStopButton.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
                bpFragStopButton.setText("STOP ENGINE");
                break;
            default:
                break;
        }
    }

    /**
     * Set CabinComfort Mode
     * @param ccEnableButton
     * @param ccFragStopButton
     */

    private void setCabinComfortMode(Button ccEnableButton, Button ccFragStopButton) {

        switch (this.CabinComfortMode) {
            case Modes.DISABLED /*0*/:
                findViewById(R.id.cabinComfortControl).setBackground(getResources().getDrawable(R.color.disabledFunction));
                findViewById(R.id.cabinComfortFragment).setBackground(getResources().getDrawable(R.color.disabledFunction));
                findViewById(R.id.cabinComfortFunctionIndicator).setBackground(getResources().getDrawable(R.color.disabled));
                findViewById(R.id.cabinComfortIndicator).setBackground(getResources().getDrawable(R.drawable.disabled_indicator_shape));
                ((TextView) findViewById(R.id.cabinComfortStatus)).setText("DISABLED");
                ccEnableButton.setBackground(getResources().getDrawable(R.drawable.disabled_button_shape));
                ccEnableButton.setText("ENABLE");
                ccFragStopButton.setBackground(getResources().getDrawable(R.drawable.disabled_button_shape));
                ccFragStopButton.setText("ENABLE");
                break;
            case Modes.ENGAGED /*1*/:
            case Modes.ENGINE_STOPPED/*3*/:
                findViewById(R.id.cabinComfortControl).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.cabinComfortFragment).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.cabinComfortFunctionIndicator).setBackground(getResources().getDrawable(R.color.enabled));
                findViewById(R.id.cabinComfortIndicator).setBackground(getResources().getDrawable(R.drawable.enabled_indicator_shape));
                ((TextView) findViewById(R.id.cabinComfortStatus)).setText("MONITORING");
                ccEnableButton.setBackground(getResources().getDrawable(R.drawable.enabled_button_shape));
                ccEnableButton.setText("DISABLE");
                ccFragStopButton.setBackground(getResources().getDrawable(R.drawable.enabled_button_shape));
                ccFragStopButton.setText("DISABLE");
                break;
            case Modes.ENGINE_RUNNING  /*2*/:
                findViewById(R.id.cabinComfortControl).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.cabinComfortFragment).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.cabinComfortFunctionIndicator).setBackground(getResources().getDrawable(R.color.active));
                findViewById(R.id.cabinComfortIndicator).setBackground(getResources().getDrawable(R.drawable.active_indicator_shape));
                ((TextView) findViewById(R.id.cabinComfortStatus)).setText("RUNNING ENGINE");
                ccEnableButton.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
                ccEnableButton.setText("STOP ENGINE");
                ccFragStopButton.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
                ccFragStopButton.setText("STOP ENGINE");
                break;
        }
    }

    /**
     * Set Cold Weather Guard Mode State
     * @param cwEnableButton
     * @param cwFragStopButton
     */

    private void setColdWeatherGuardMode(Button cwEnableButton, Button cwFragStopButton) {

        switch (this.ColdWeatherGuardMode) {
            case Modes.DISABLED /*0*/:
                findViewById(R.id.coldWeatherGuardControl).setBackground(getResources().getDrawable(R.color.disabledFunction));
                findViewById(R.id.coldWeatherGuardFragment).setBackground(getResources().getDrawable(R.color.disabledFunction));
                findViewById(R.id.coldWeatherGuardFunctionIndicator).setBackground(getResources().getDrawable(R.color.disabled));
                findViewById(R.id.coldWeatherGuardIndicator).setBackground(getResources().getDrawable(R.drawable.disabled_indicator_shape));
                ((TextView) findViewById(R.id.coldWeatherGuardStatus)).setText("DISABLED");
                cwEnableButton.setBackground(getResources().getDrawable(R.drawable.disabled_button_shape));
                cwEnableButton.setText("ENABLE");
                cwFragStopButton.setBackground(getResources().getDrawable(R.drawable.disabled_button_shape));
                cwFragStopButton.setText("ENABLE");
                break;
            case Modes.ENGAGED /*1*/:
            case Modes.ENGINE_STOPPED /*3*/:
                findViewById(R.id.coldWeatherGuardControl).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.coldWeatherGuardFragment).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.coldWeatherGuardFunctionIndicator).setBackground(getResources().getDrawable(R.color.enabled));
                findViewById(R.id.coldWeatherGuardIndicator).setBackground(getResources().getDrawable(R.drawable.enabled_indicator_shape));
                ((TextView) findViewById(R.id.coldWeatherGuardStatus)).setText("MONITORING");
                cwEnableButton.setBackground(getResources().getDrawable(R.drawable.enabled_button_shape));
                cwEnableButton.setText("DISABLE");
                cwFragStopButton.setBackground(getResources().getDrawable(R.drawable.enabled_button_shape));
                cwFragStopButton.setText("DISABLE");
                break;
            case Modes.ENGINE_RUNNING /*2*/:
                findViewById(R.id.coldWeatherGuardControl).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.coldWeatherGuardFragment).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.coldWeatherGuardFunctionIndicator).setBackground(getResources().getDrawable(R.color.active));
                findViewById(R.id.coldWeatherGuardIndicator).setBackground(getResources().getDrawable(R.drawable.active_indicator_shape));
                ((TextView) findViewById(R.id.coldWeatherGuardStatus)).setText("RUNNING ENGINE");
                cwEnableButton.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
                cwEnableButton.setText("STOP ENGINE");
                cwFragStopButton.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
                cwFragStopButton.setText("STOP ENGINE");
                break;
        }
    }

    private void setFunctionMode(int function, int mode) {
        int i = 1;
        int[] iArr;
        switch (function) {
            case Functionality.CABIN_COMFORT /*1*/:
                if (this.GatewayMode != GatewayModes.CABIN_COMFORT) {
                    this.CabinComfortMode = mode;
                } else if (mode == Modes.ENGINE_STOPPED) {
                    this.accessoryControl.writeCommand(AccessoryControl.APICMD_STOP, 0, 1);
                    this.CabinComfortMode = Modes.ENGINE_STOPPED;
                }
                iArr = params.getCurrentParamValues();
                if (this.CabinComfortMode == Modes.DISABLED) {
                    i = 0;
                }
                iArr[Params.PARAM_CabinComfort] = i;
                this.accessoryControl.writeCommand(AccessoryControl.APICMD_CABIN_COMFORT_ENABLE, 0, params.getCurrentParamValue(Params.PARAM_CabinComfort) & 255);
                break;
            case Functionality.COLD_WEATHER_GUARD /*2*/:
                int i2;
                if (this.GatewayMode != GatewayModes.COLD_WEATHER_GUARD) {
                    this.ColdWeatherGuardMode = mode;
                } else if (mode == Modes.ENGINE_STOPPED) {
                    this.accessoryControl.writeCommand(AccessoryControl.APICMD_STOP, 0, 1);
                    this.ColdWeatherGuardMode = Modes.ENGINE_STOPPED;
                }
                int[] iArr2 = params.getCurrentParamValues();
                if (this.ColdWeatherGuardMode == Modes.DISABLED) {
                    i2 = 0;
                } else {
                    i2 = 1;
                }
                iArr2[Params.PARAM_ColdWeatherGuard] = i2;
                this.accessoryControl.writeCommand(AccessoryControl.APICMD_COLD_WEATHER_GUARD_ENABLE, 0, params.getCurrentParamValue(Params.PARAM_ColdWeatherGuard) & 255);
                break;
            case Functionality.BATTERY_PROTECT: /*3*/
                if (this.GatewayMode != GatewayModes.BATTERY_PROTECT) {
                    this.BatteryProtectMode = mode;
                } else if (mode == Modes.ENGINE_STOPPED) {
                    this.accessoryControl.writeCommand(AccessoryControl.APICMD_STOP, 0, 1);
                    this.BatteryProtectMode = Modes.ENGINE_STOPPED;
                }
                iArr = params.getCurrentParamValues();
                if (this.BatteryProtectMode == Modes.DISABLED) {
                    i = 0;
                }
                iArr[Params.PARAM_BatteryProtect] = i;
                this.accessoryControl.writeCommand(AccessoryControl.APICMD_BATTERY_MONITOR_ENABLE, 0, params.getCurrentParamValue(Params.PARAM_BatteryProtect) & 255);
                break;
        }
        updateFunctionModes();
    }

    private int toggleFunctionMode(int mode) {
        switch (mode) {
            case Modes.DISABLED /*0*/:
                return Modes.ENGAGED;
            case Modes.ENGAGED /*1*/:
                return Modes.DISABLED;
            case Modes.ENGINE_RUNNING /*2*/:
                return Modes.ENGINE_STOPPED;
            case Modes.ENGINE_STOPPED /*3*/:
                return Modes.ENGAGED;
            default:
                return Modes.DISABLED;
        }
    }

    private static class GatewayModes {
        static final int IDLE = 0;
        static final int CABIN_COMFORT = 1;
        static final int COLD_WEATHER_GUARD = 2;
        static final int BATTERY_PROTECT = 3;
    }

    /**
     * Define modes for Function
     */
    private static class Modes {
        static final int DISABLED = 0;
        static final int ENGAGED = 1;
        static final int ENGINE_RUNNING = 2;
        static final int ENGINE_STOPPED = 3;
    }

    private void useGatewayMode(int newmode) {
        switch (this.GatewayMode) {
            case GatewayModes.CABIN_COMFORT /*1*/:
                this.CabinComfortMode = Modes.ENGAGED;
                break;
            case GatewayModes.COLD_WEATHER_GUARD /*2*/:
                this.ColdWeatherGuardMode = Modes.ENGAGED;
                break;
            case GatewayModes.BATTERY_PROTECT /*3*/:
                this.BatteryProtectMode = Modes.ENGAGED;
                break;
        }
        switch (newmode) {
            case GatewayModes.IDLE /*0*/:
                Log.i(TAG, "     change mode to Idle");
                this.GatewayMode = GatewayModes.IDLE;
                break;
            case GatewayModes.CABIN_COMFORT /*1*/:
                Log.i(TAG, "     change mode to CabinComfortMode");
                this.GatewayMode = GatewayModes.CABIN_COMFORT;
                this.CabinComfortMode = Modes.ENGINE_RUNNING;
                break;
            case GatewayModes.COLD_WEATHER_GUARD: /*2*/
                Log.i(TAG, "     change mode to ColdWeatherGuardMode");
                this.GatewayMode = GatewayModes.COLD_WEATHER_GUARD;
                this.ColdWeatherGuardMode = Modes.ENGINE_RUNNING;
                break;
            case GatewayModes.BATTERY_PROTECT /*3*/:
                Log.i(TAG, "     change mode to BatteryProtectMode");
                this.GatewayMode = GatewayModes.BATTERY_PROTECT;
                this.BatteryProtectMode = Modes.ENGINE_RUNNING;
                break;
        }
        updateFunctionModes();
    }

    private void enableSettings(boolean enable) {
        CurrentSettingsFlag = enable;
        if (!gateway_connected && !demo_mode) {
            findViewById(R.id.settingsFragment).setVisibility(View.GONE);
        } else if (enable) {
            enableStatusBar(false);
            findViewById(R.id.settingsFragment).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.settingsFragment).setVisibility(View.GONE);
        }
    }

    private void selectSettingsMode(int level) {
        if (level == 0) {
            enableSettings(false);
            selectSettingsEntry(0);
        } else {
            enableStatusBar(false);
            enableDashboard(false);
            selectActivationFragment(ActivationStep.NONE);
            enableSettings(true);
        }
        switch (level) {
            case Functionality.CABIN_COMFORT /*1*/:
                initMenu1();
                findViewById(R.id.settingsMenu1).setVisibility(View.VISIBLE);
                findViewById(R.id.settingsMenu2).setVisibility(View.GONE);
                selectSettingsEntry(0);
                this.settings_menu1_index = 0;
                selectMenu1Entry(0);
            	break;
            case Functionality.COLD_WEATHER_GUARD /*2*/:
                findViewById(R.id.settingsMenu1).setVisibility(View.VISIBLE);
                findViewById(R.id.settingsMenu2).setVisibility(View.VISIBLE);
                initMenu2();
                selectSettingsEntry(0);
                this.settings_menu2_index = 0;
                selectMenu2Entry(0);
                this.settings_entrytype = 0;
            	break;
			case Functionality.BATTERY_PROTECT /*3*/:
                findViewById(R.id.settingsMenu1).setVisibility(View.VISIBLE);
                findViewById(R.id.settingsMenu2).setVisibility(View.VISIBLE);
                viewParamValue();
                break;
            default:
                findViewById(R.id.settingsMenu1).setVisibility(View.GONE);
                findViewById(R.id.settingsMenu2).setVisibility(View.GONE);
                selectSettingsEntry(0);
                break;
        }
    }


    private void initMenu1() {
        findViewById(R.id.settingsMenu11).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsMenu11Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        ((TextView) findViewById(R.id.settingsMenu11Item)).setText(this.menus.aMainMenu[0]);
        findViewById(R.id.settingsMenu12).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsMenu12Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        ((TextView) findViewById(R.id.settingsMenu12Item)).setText(this.menus.aMainMenu[1]);
        findViewById(R.id.settingsMenu13).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsMenu13Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        ((TextView) findViewById(R.id.settingsMenu13Item)).setText(this.menus.aMainMenu[2]);
        findViewById(R.id.settingsMenu14).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsMenu14Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        ((TextView) findViewById(R.id.settingsMenu14Item)).setText(this.menus.aMainMenu[3]);
        findViewById(R.id.settingsMenu15).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsMenu15Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        ((TextView) findViewById(R.id.settingsMenu15Item)).setText(this.menus.aMainMenu[4]);
        findViewById(R.id.settingsMenu16).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsMenu16Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        ((TextView) findViewById(R.id.settingsMenu16Item)).setText(this.menus.aMainMenu[5]);
        findViewById(R.id.settingsMenu17).setVisibility(View.INVISIBLE);
        findViewById(R.id.settingsMenu17Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu18).setVisibility(View.INVISIBLE);
        findViewById(R.id.settingsMenu18Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
    }

    private void initMenu2() {
        findViewById(R.id.settingsMenu21Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        if (Menus.getSubmenuId(this.settings_menu1_index, 1) >= 0) {
            findViewById(R.id.settingsMenu21).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.settingsMenu21Item)).setText(this.menus.getSubmenuName(this.settings_menu1_index, 1));
        } else {
            findViewById(R.id.settingsMenu21).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.settingsMenu22Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        if (Menus.getSubmenuId(this.settings_menu1_index, 2) >= 0) {
            findViewById(R.id.settingsMenu22).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.settingsMenu22Item)).setText(this.menus.getSubmenuName(this.settings_menu1_index, 2));
        } else {
            findViewById(R.id.settingsMenu22).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.settingsMenu23Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        if (Menus.getSubmenuId(this.settings_menu1_index, 3) >= 0) {
            findViewById(R.id.settingsMenu23).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.settingsMenu23Item)).setText(this.menus.getSubmenuName(this.settings_menu1_index, 3));
        } else {
            findViewById(R.id.settingsMenu23).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.settingsMenu24Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        if (Menus.getSubmenuId(this.settings_menu1_index, 4) >= 0) {
            findViewById(R.id.settingsMenu24).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.settingsMenu24Item)).setText(this.menus.getSubmenuName(this.settings_menu1_index, 4));
        } else {
            findViewById(R.id.settingsMenu24).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.settingsMenu25Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        if (Menus.getSubmenuId(this.settings_menu1_index, 5) >= 0) {
            findViewById(R.id.settingsMenu25).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.settingsMenu25Item)).setText(this.menus.getSubmenuName(this.settings_menu1_index, 5));
        } else {
            findViewById(R.id.settingsMenu25).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.settingsMenu26Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        if (Menus.getSubmenuId(this.settings_menu1_index, 6) >= 0) {
            findViewById(R.id.settingsMenu26).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.settingsMenu26Item)).setText(this.menus.getSubmenuName(this.settings_menu1_index, 6));
        } else {
            findViewById(R.id.settingsMenu26).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.settingsMenu27Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        if (Menus.getSubmenuId(this.settings_menu1_index, 7) >= 0) {
            findViewById(R.id.settingsMenu27).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.settingsMenu27Item)).setText(this.menus.getSubmenuName(this.settings_menu1_index, 7));
        } else {
            findViewById(R.id.settingsMenu27).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.settingsMenu28Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        if (Menus.getSubmenuId(this.settings_menu1_index, 8) >= 0) {
            findViewById(R.id.settingsMenu28).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.settingsMenu28Item)).setText(this.menus.getSubmenuName(this.settings_menu1_index, 8));
            return;
        }
        findViewById(R.id.settingsMenu28).setVisibility(View.INVISIBLE);
    }

    /**
     * Highlight selected menu entry
     * @param level
     */
    private void selectMenu1Entry(int level) {
        this.settings_menu1_index = level;
        findViewById(R.id.settingsMenu11).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu11Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu12).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu12Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu13).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu13Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu14).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu14Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu15).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu15Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu16).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu16Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu17).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu17Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu18).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        findViewById(R.id.settingsMenu18Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        switch (level) {
            case 1 /*1*/: // Cabin Comfort
                findViewById(R.id.settingsMenu11).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu11Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case 2 /*2*/: // Battery Protect
                findViewById(R.id.settingsMenu12).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu12Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case 3 /*3*/: // Cold Weather Guard
                findViewById(R.id.settingsMenu13).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu13Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case 4 /*4*/: // Password
                findViewById(R.id.settingsMenu14).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu14Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case 5 /*5*/: // Vehicle
                findViewById(R.id.settingsMenu15).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu15Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case 6 /*6*/: // About Device
                findViewById(R.id.settingsMenu16).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu16Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case 7 /*7*/: // Refresh Device
                findViewById(R.id.settingsMenu17).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu17Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case 8 /*8*/: // Empty
                findViewById(R.id.settingsMenu18).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu18Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            default:
                return;
        }
        selectSettingsMode(Functionality.COLD_WEATHER_GUARD);
    }

    private void selectMenu2Entry(int level) {
        this.settings_menu2_index = level;
        findViewById(R.id.settingsMenu21).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu21Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu22).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu22Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu23).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu23Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu24).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu24Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu25).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu25Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu26).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu26Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu27).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu27Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu28).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        findViewById(R.id.settingsMenu28Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        switch (level) {
            case 1 /*1*/:
                findViewById(R.id.settingsMenu21).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu21Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case 2 /*2*/:
                findViewById(R.id.settingsMenu22).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu22Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case 3 /*3*/:
                findViewById(R.id.settingsMenu23).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu23Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case 4 /*4*/:
                findViewById(R.id.settingsMenu24).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu24Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case 5 /*5*/:
                findViewById(R.id.settingsMenu25).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu25Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case 6 /*6*/:
                findViewById(R.id.settingsMenu26).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu26Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case 7 /*7*/:
                findViewById(R.id.settingsMenu27).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu27Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case 8 /*8*/:
                findViewById(R.id.settingsMenu28).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu28Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            default:
                return;
        }
        selectSettingsMode(Functionality.BATTERY_PROTECT);
    }

    /**
     * Display the UI according to the data type
     * @param datatype
     */
    private void selectSettingsEntry(int datatype) {
        this.settings_entrytype = datatype;
        findViewById(R.id.settingsEntryEnable).setVisibility(View.GONE);
        findViewById(R.id.settingsEntryDisable).setVisibility(View.GONE);
        findViewById(R.id.settingsEntryNumeric).setVisibility(View.GONE);
        findViewById(R.id.settingsEntryPassword).setVisibility(View.GONE);
        findViewById(R.id.settingsEntryRefresh).setVisibility(View.GONE);
        switch (datatype) {
            case 1 /*1*/: // ex: Carbin Comfort is enabled. Display Disable button
                findViewById(R.id.settingsEntryEnable).setVisibility(View.VISIBLE);
                break;
            case 2 /*2*/: // ex: Carbin Comfort is disabled. Display Enable button
                findViewById(R.id.settingsEntryDisable).setVisibility(View.VISIBLE);
                break;
            case 3 /*3*/: // Display a number with + and - button
                findViewById(R.id.settingsEntryNumeric).setVisibility(View.VISIBLE);
                break;
            case 4 /*4*/: // Display change password
                findViewById(R.id.settingsEntryPassword).setVisibility(View.VISIBLE);
                break;
            case 5 /*5*/: // Display refresh
                findViewById(R.id.settingsEntryRefresh).setVisibility(View.VISIBLE);
                break;
        }
        PasswordValid = false;
    }

    private void viewParamValue() {
        selectSettingsEntry(1);
        this.param_id = Menus.getSubmenuId(this.settings_menu1_index, this.settings_menu2_index);
        this.params.setParamValue(this.params.getCurrentParamValue(this.param_id));
        String pName = this.params.aParamName[this.param_id];
        String pPfx = this.params.aParamPfx[this.param_id];
        String pSfx = this.params.aParamSfx[this.param_id];
        if (this.param_id >= 0) {
            switch (this.params.aParamType[this.param_id]) {
                case Params.BooleanType /*1*/:
                    if (this.params.getCurrentParamValue(this.param_id) != 0) {
                        ((TextView) findViewById(R.id.settingsEntryDisableDescription)).setText(pName + " feature is currently Enabled.");
                        selectSettingsEntry(2);
                        return;
                    }
                    ((TextView) findViewById(R.id.settingsEntryEnableDescription)).setText(pName + " feature is currently Disabled.");
                    selectSettingsEntry(1);
                    return;
                case Params.IntegerType /*2*/:
                case Params.TempType /*3*/:
                    ((TextView) findViewById(R.id.settingsEntryDescription)).setText(pName);
                    ((TextView) findViewById(R.id.settingsEntryValue)).setText(pPfx + Integer.toString(this.params.getCurrentParamValue(this.param_id)) + pSfx);
                    selectSettingsEntry(3);
                    return;
                case Params.VoltageType /*4*/:
                    ((TextView) findViewById(R.id.settingsEntryDescription)).setText(pName);
                    String str = Integer.toString(params.getCurrentParamValue(this.param_id));
                    ((TextView) findViewById(R.id.settingsEntryValue)).setText(str.substring(0, str.length() - 1) + "." + str.substring(str.length() - 1) + pSfx);
                    selectSettingsEntry(3);
                    return;
                case Params.ProcessType /*7*/:
                    if (this.param_id == 21) {
                        selectSettingsEntry(5);
                        httpclient.PhoneHome(0, false);
                    } else if (this.param_id == 22) {
                        openSerialDialog();
                    }
                    selectSettingsMode(0);
                    selectRunning(1);
                    return;
                default:
                    selectSettingsMode(0);
                    selectRunning(1);
                    return;
            }
        }
        selectSettingsMode(0);
        selectRunning(1);
    }

    private void updateParamValue(int vId, int pId) {
        String pSfx = this.params.aParamSfx[this.param_id];
        String pPfx = this.params.aParamPfx[this.param_id];
        boolean bypass = false;
        if (pId == 0 && params.getCurrentParamValue(Params.PARAM_FleetCabinComfort) == 1) {
            bypass = true;
        }
        if (pId == 3) {
            if (vId == R.id.settingsEntryIncrementButton && this.params.isCabinTempCommonIncrValid(this.params.getParamValue())) {
                bypass = true;
            }
            if (vId == R.id.settingsEntryDecrementButton && this.params.isCabinTempCommonDecrValid(this.params.getParamValue())) {
                bypass = true;
            }
        }
        if (bypass || ValidPassword()) {
            String str;
            switch (vId) {
                case R.id.settingsEntryEnableButton /*2131362065*/:
                    if (this.params.aParamType[pId] == Params.BooleanType) {
                        this.params.setParamValue(1);
                    }
                	break;
			    case R.id.settingsEntryDisableButton /*2131362068*/:
                    if (this.params.aParamType[pId] == Params.BooleanType) {
                        this.params.setParamValue(0);
                    }
                	break;
			    case R.id.settingsEntryDecrementButton /*2131362072*/:
                    this.params.decrValue(pId);
                    if (pId == Params.PARAM_TruckTimer && this.params.getParamValue() < 4) {
                        this.params.setParamValue(0);
                    }
                    switch (this.params.aParamType[pId]) {
                        case Params.IntegerType /*2*/:
			            case Params.TempType /*3*/:
                            ((TextView) findViewById(R.id.settingsEntryValue)).setText(pPfx + Integer.toString(this.params.getParamValue()) + pSfx);
                        	break;
			            case Params.VoltageType /*4*/:
                            str = Integer.toString(this.params.getParamValue());
                            ((TextView) findViewById(R.id.settingsEntryValue)).setText(str.substring(0, str.length() - 1) + "." + str.substring(str.length() - 1) + pSfx);
                            break;
                        default:
                            break;
                    }
                	break;
			    case R.id.settingsEntryIncrementButton /*2131362073*/:
                    this.params.incrValue(pId);
                    if (pId == Params.PARAM_TruckTimer && this.params.getParamValue() < 4) {
                        this.params.setParamValue(4);
                    }
                    switch (this.params.aParamType[pId]) {
			            case Params.IntegerType /*2*/:
            			case Params.TempType /*3*/:
                            ((TextView) findViewById(R.id.settingsEntryValue)).setText(Integer.toString(this.params.getParamValue()) + pSfx);
                        	break;
			            case Params.VoltageType /*4*/:
                            str = Integer.toString(this.params.getParamValue());
                            ((TextView) findViewById(R.id.settingsEntryValue)).setText(str.substring(0, str.length() - 1) + "." + str.substring(str.length() - 1) + pSfx);
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
    }

    public void SaveDownloadedParamValue(int paramId, int value) {
        this.params.setParamValue(value);
        saveParamValue(paramId);
    }

    private void saveParamValue(int paramId) {
        switch (this.params.aParamType[paramId]) {
            case Params.BooleanType /*1*/:
                this.params.setCurrentParamValue(paramId, this.params.getParamValue());
                boolean z;
                switch (paramId) {
                    case Params.PARAM_CabinComfort /*0*/:
                        if (this.params.getCurrentParamValue(paramId) != 0) {
                            setFunctionMode(Functionality.CABIN_COMFORT, 1);
                        } else {
                            setFunctionMode(Functionality.CABIN_COMFORT, 0);
                        }
                        updateFunctionModes();
                        break;
                    case Params.PARAM_ColdWeatherGuard /*1*/:
                        if (this.params.getCurrentParamValue(paramId) != 0) {
                            setFunctionMode(Functionality.COLD_WEATHER_GUARD, 1);
                        } else {
                            setFunctionMode(Functionality.COLD_WEATHER_GUARD, 0);
                        }
                        updateFunctionModes();
                        break;
                    case Params.PARAM_BatteryProtect /*2*/:
                        if (this.params.getCurrentParamValue(paramId) != 0) {
                            setFunctionMode(Functionality.BATTERY_PROTECT, 1);
                        } else {
                            setFunctionMode(Functionality.BATTERY_PROTECT, 0);
                        }
                        updateFunctionModes();
                        break;
                    case Params.PARAM_AudibleSound /*15*/:
                        if (this.params.getParamValue() != 0) {
                            z = true;
                        } else {
                            z = false;
                        }
                        setSoundOn(z);
                        sendParam(paramId);
                        break;
                    case Params.PARAM_PasswordEnable /*19*/:
                        if (this.params.getParamValue() != 0) {
                            z = true;
                        } else {
                            z = false;
                        }
                        PasswordEnable = z;
                        sendParam(paramId);
                        break;
                    case Params.PARAM_Password /*20*/:
                        Password = this.params.getParamValue();
                        sendParam(paramId);
                        break;
                    default:
                        sendParam(paramId);
                        break;
                }
            case Params.IntegerType /*2*/:
                this.params.setCurrentParamValue(paramId, this.params.getParamValue());
                switch (paramId) {
                    case Params.PARAM_DimTabletScreen /*14*/:
                        setScreenTimeout(this.params.getParamValue());
                        sendParam(paramId);
                        break;
                    default:
                        sendParam(paramId);
                        break;
                }
            case Params.TempType /*3*/:
            case Params.VoltageType /*4*/:
                this.params.setCurrentParamValue(paramId, this.params.getParamValue());
                sendParam(paramId);
                break;
        }
        selectSettingsMode(0);
        selectRunning(1);
    }

    private void sendParam(int paramId) {
        byte[] data = new byte[2];
        int api = this.params.aParamAPIcmd[paramId];
        if (api != 0) {
            data[0] = (byte) ((this.params.getCurrentParamValue(paramId) >> 8) & 255);
            data[1] = (byte) (this.params.getCurrentParamValue(paramId) & 255);
            this.accessoryControl.writeCommand(api, data[0], data[1]);
        }
    }

    private void viewFragmentParamValue(int fragmentId) {
        switch (fragmentId) {
            case 2 /*2*/:
                ((TextView) findViewById(R.id.ccFragTargetTemperatureValue)).setText(Integer.toString(params.getCurrentParamValue(Params.PARAM_CabinTargetTemp)) + this.params.aParamSfx[Params.PARAM_CabinTargetTemp]);
                break;
            case 3 /*3*/:
                ((TextView) findViewById(R.id.cwgFragMinTempValue)).setText(Integer.toString(params.getCurrentParamValue(Params.PARAM_MinCoolantTemp)) + this.params.aParamSfx[Params.PARAM_MinCoolantTemp]);
                ((TextView) findViewById(R.id.cwgFragIdealTempValue)).setText(Integer.toString(params.getCurrentParamValue(Params.PARAM_IdealCoolantTemp)) + this.params.aParamSfx[Params.PARAM_IdealCoolantTemp]);
                break;
            case 4 /*4*/:
                TimeConverter timeConverter = new TimeConverter();
                ((TextView) findViewById(R.id.bpFragTimeRemainingValue)).setText(timeConverter.time2MinsSecsStr(params.getCurrentParamValue(Params.PARAM_EngineRunTime) * 60));
                String str = Integer.toString(params.getCurrentParamValue(Params.PARAM_VoltageSetPoint));
                ((TextView) findViewById(R.id.bpFragSetpointValue)).setText(str.substring(0, str.length() - 1) + "." + str.substring(str.length() - 1) + this.params.aParamSfx[Params.PARAM_VoltageSetPoint]);
                ((TextView) findViewById(R.id.bpEngineRuntimeValue)).setText(Integer.toString(params.getCurrentParamValue(Params.PARAM_EngineRunTime)) + this.params.aParamSfx[Params.PARAM_EngineRunTime]);
                break;
            default:
                break;
        }
    }

    private void updateFragmentParamValue(int vId, int pId) {
        byte[] data = new byte[2];
        switch (vId) {
            case R.id.ccFragTargetTemperatureDecrButton /*2131361991*/:
                params.decrParam(pId);
                viewFragmentParamValue(2);
                break;
            case R.id.ccFragTargetTemperatureIncrButton /*2131361992*/:
                params.incrParam(pId);
                viewFragmentParamValue(2);
                break;
            case R.id.cwgFragMinTempDecrButton /*2131361998*/:
                params.decrParam(pId);
                viewFragmentParamValue(3);
                break;
            case R.id.cwgFragMinTempIncrButton /*2131361999*/:
                params.incrParam(pId);
                viewFragmentParamValue(3);
                break;
            case R.id.cwgFragIdealTempDecrButton /*2131362001*/:
                params.decrParam(pId);
                viewFragmentParamValue(3);
                break;
            case R.id.cwgFragIdealTempIncrButton /*2131362002*/:
                params.incrParam(pId);
                viewFragmentParamValue(3);
                break;
            case R.id.bpFragSetpointDecrButton /*2131362007*/:
                params.decrParam(pId);
                viewFragmentParamValue(4);
                break;
            case R.id.bpFragSetpointIncrButton /*2131362008*/:
                params.incrParam(pId);
                viewFragmentParamValue(4);
                break;
            case R.id.bpEngineRuntimeDecrButton /*2131362010*/:
                params.decrParam(pId);
                viewFragmentParamValue(4);
                break;
            case R.id.bpEngineRuntimeIncrButton /*2131362011*/:
                params.incrParam(pId);
                viewFragmentParamValue(4);
                break;
        }
        data[0] = (byte) ((params.getCurrentParamValue(pId) >> 8) & 255);
        data[1] = (byte) (params.getCurrentParamValue(pId) & 255);
        this.accessoryControl.writeCommand(this.params.aParamAPIcmd[pId], data[0], data[1]);
    }

    private void selectActivationFragment(int fragment) {
        findViewById(R.id.verificationFragment).setVisibility(View.GONE);
        findViewById(R.id.installFragment).setVisibility(View.GONE);
        findViewById(R.id.activationFragment).setVisibility(View.GONE);
        findViewById(R.id.activationCodeFragment).setVisibility(View.GONE);
        findViewById(R.id.VINCodeFragment).setVisibility(View.GONE);
        if (fragment != 0) {
            enableStatusBar(false);
            enableDashboard(false);
            selectRunning(0);
            EditText activationcodeArea;
            switch (fragment) {
                case 1 /*1*/:
                    findViewById(R.id.verificationFragment).setVisibility(View.VISIBLE);
                	break;
				case 2 /*2*/:
                    findViewById(R.id.installFragment).setVisibility(View.VISIBLE);
                    StartVerificationProcess();
                	break;
				case 3 /*3*/:
                    findViewById(R.id.activationFragment).setVisibility(View.VISIBLE);
                	break;
				case 4 /*4*/:
                    activationcodeArea = (EditText) findViewById(R.id.activationCodeEditText);
                    activationcodeArea.setText("");
                    activationcodeArea.setOnEditorActionListener(this.mEditorActionListener);
                	break;
				case 5 /*5*/:
                    activationcodeArea = (EditText) findViewById(R.id.VINCodeEditText);
                    activationcodeArea.setText("");
                    activationcodeArea.setOnEditorActionListener(this.mEditorActionListener);
                    break;
                default:
                    break;
            }
        }
    }

    private void StartVerificationProcess() {
        this.activation_step = 0;
        ValidActivationProcess = false;
        ActivationProcessPending = true;
        this.verificationHandler.removeCallbacks(this.verificationRunnable);
        this.verificationHandler.postDelayed(this.verificationRunnable, 100);
    }

    /**
     * Verify
     */
    private void nextVerificationStep() {
        int i = this.activation_step;
        this.activation_step = i + 1;
        Button doneButton;
        switch (i) {
            case 0 /*0*/:
                ((CheckBox) findViewById(R.id.installDetail1CheckBox)).setChecked(false);
                ((CheckBox) findViewById(R.id.installDetail2CheckBox)).setChecked(false);
                ((CheckBox) findViewById(R.id.installDetail3CheckBox)).setChecked(false);
                ((CheckBox) findViewById(R.id.installDetail4CheckBox)).setChecked(false);
                findViewById(R.id.installDetail1Progress).setVisibility(View.INVISIBLE);
                findViewById(R.id.installDetail2Progress).setVisibility(View.INVISIBLE);
                findViewById(R.id.installDetail3Progress).setVisibility(View.INVISIBLE);
                findViewById(R.id.installDetail4Progress).setVisibility(View.INVISIBLE);
                doneButton = (Button) findViewById(R.id.installDoneButton);
                doneButton.setText("CHECKING");
                doneButton.setBackground(getResources().getDrawable(R.drawable.disabled_button_shape));
                doneButton.setEnabled(false);
                this.verificationHandler.postDelayed(this.verificationRunnable, 100);
            	break;
			case 1 /*1*/:
                findViewById(R.id.installDetail1Progress).setVisibility(View.VISIBLE);
                if (test_mode) {
                    this.verificationHandler.postDelayed(this.verificationRunnable, 500);
                } else {
                    this.verificationHandler.postDelayed(this.verificationRunnable, 5000);
                }
            	break;
			case 2 /*2*/:
                findViewById(R.id.installDetail1Progress).setVisibility(View.INVISIBLE);
                ((CheckBox) findViewById(R.id.installDetail1CheckBox)).setChecked(true);
                findViewById(R.id.installDetail2Progress).setVisibility(View.VISIBLE);
                if (test_mode) {
                    this.verificationHandler.postDelayed(this.verificationRunnable, 500);
                } else {
                    this.verificationHandler.postDelayed(this.verificationRunnable, 2000);
                }
            	break;
			case 3 /*3*/:
                findViewById(R.id.installDetail2Progress).setVisibility(View.INVISIBLE);
                ((CheckBox) findViewById(R.id.installDetail2CheckBox)).setChecked(true);
                findViewById(R.id.installDetail3Progress).setVisibility(View.VISIBLE);
                if (test_mode) {
                    this.verificationHandler.postDelayed(this.verificationRunnable, 500);
                    return;
                }
                this.accessoryControl.writeCommand(AccessoryControl.APICMD_GET_VEHICLE_INFO, 0, 1);
                Log.i(TAG, "APICMD_GET_VEHICLE_INFO = 1");
                this.verificationHandler.postDelayed(this.verificationRunnable, 15000);
            	break;
			case 4 /*4*/:
                findViewById(R.id.installDetail3Progress).setVisibility(View.INVISIBLE);
                ((CheckBox) findViewById(R.id.installDetail3CheckBox)).setChecked(true);
                findViewById(R.id.installDetail4Progress).setVisibility(View.VISIBLE);
                this.verificationHandler.postDelayed(this.verificationRunnable, 500);
            	break;
			case 5 /*5*/:
                findViewById(R.id.installDetail4Progress).setVisibility(View.INVISIBLE);
                ((CheckBox) findViewById(R.id.installDetail4CheckBox)).setChecked(true);
                this.activation_step = 0;
                doneButton = (Button) findViewById(R.id.installDoneButton);
                if (Gateway_VIN.isEmpty()) {
                    doneButton.setText("ERROR! No VIN number");
                } else if (ActivationCode < 1000) {
                    doneButton.setText("ERROR! Invalid Activation Code");
                } else {
                    doneButton.setText("DONE");
                    ValidActivationProcess = true;
                }
                doneButton.setBackground(getResources().getDrawable(R.drawable.enabled_button_shape));
                doneButton.setEnabled(true);
                this.verificationHandler.removeCallbacks(this.verificationRunnable);
                ActivationProcessPending = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void onFreshListener(int faultId) {
        MainActivity.this.accessoryControl.writeCommand(AccessoryControl.APICMD_ALERT_ACK, 0, faultId);
        if (MainActivity.this.mTempWakeLock.isHeld()) {
            MainActivity.this.mTempWakeLock.release();
        }
    }

    public void openAlertDialog(int faultId) {
        if (faultId != 0 && faultId <= 23) {
            HasFocus = true;
            String faultMessage = this.faults.aFaultMessage[faultId];
            String faultDesc = this.faults.aFaultDesc[faultId];

            Bundle bundle = new Bundle();
            bundle.putInt("faultId", faultId);
            bundle.putString("faultMessage", faultMessage);
            bundle.putString("faultDesc", faultDesc);

            AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
            alertDialogFragment.setArguments(bundle);

            alertDialogFragment.show(this.fragmentTransaction, "AlertDialogFragment");
            this.accessoryControl.writeCommand(AccessoryControl.APICMD_ALERT_ACK, 0, faultId);
            wakeup();
            if (params.getCurrentParamValue(Params.PARAM_AudibleSound) != 0) {
                alertTone();
            }
            back2sleep();
        }
    }

    public void closeAlertDialog(int faultId) {
        if (faultId == 0) {
            closeAllAlertDialogs();
        } else {
            Fragment alertDialogFragment = getSupportFragmentManager().findFragmentByTag("AlertDialogFragment");
            if (alertDialogFragment != null) {
                AlertDialogFragment dialogFragment = (AlertDialogFragment) alertDialogFragment;
                dialogFragment.dismiss();
            }
            back2sleep();
        }
    }

    public void closeAllAlertDialogs() {
        Fragment alertDialogFragment = getSupportFragmentManager().findFragmentByTag("AlertDialogFragment");
        if (alertDialogFragment != null) {
            AlertDialogFragment dialogFragment = (AlertDialogFragment) alertDialogFragment;
            dialogFragment.dismiss();
        }
            back2sleep();
    }

    private void alertTone() {
        if (params.getCurrentParamValue(Params.PARAM_AudibleSound) != 0) {
            if (this.mMediaPlayer != null) {
                closeMediaPlayer();
            }
            try {
                MediaPlayer.create(this, R.raw.iridium).start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeMediaPlayer() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
    }

    public void openSerialDialog() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        SerialDialogFragment serialDialogFragment = SerialDialogFragment.newInstance(pInfo);
        serialDialogFragment.show(this.fragmentTransaction, "SerialDialogFragment");
    }

    // region Maintenance Dialog

    public void openMaintDialog() {
        this.kiosk_mode_counter = 0;
        boolean enableLogFile = aMaintEnable[MainActivity.MaintenanceFeature.LOG_FILE];
        String valueLogFile = Integer.toString(aMaintValue[MaintenanceFeature.LOG_FILE]);
        boolean enableClutchOverride = aMaintEnable[MaintenanceFeature.CLUTCH_OVERRIDE];
        String valueClutchOverride = Integer.toString(aMaintValue[MaintenanceFeature.CLUTCH_OVERRIDE]);
        boolean enableIdleTimeOverride = aMaintEnable[MaintenanceFeature.IDLE_TIME_OVERRIDE];
        String valueIdleTimeOverride = Integer.toString(aMaintValue[MaintenanceFeature.IDLE_TIME_OVERRIDE]);
        boolean enableEngineSpeedAdjustments = aMaintEnable[MaintenanceFeature.ENGINE_SPEED_ADJUSTMENTS];
        String valueEngineSpeedAdjustments = Integer.toString(aMaintValue[MaintenanceFeature.ENGINE_SPEED_ADJUSTMENTS]);
        boolean enableTimeStampRMP = aMaintEnable[MaintenanceFeature.TIMESTAMP_RPM];
        String valueTimeStampRMP = Integer.toString(aMaintValue[MaintenanceFeature.TIMESTAMP_RPM]);
        boolean enableNeutralSwitchDetection = aMaintEnable[MaintenanceFeature.NEUTRAL_SWITCH_DETECTION];
        String valueNeutralSwitchDetection = Integer.toString(aMaintValue[MaintenanceFeature.NEUTRAL_SWITCH_DETECTION]);
        boolean enableReserved = aMaintEnable[MaintenanceFeature.RESERVED];
        String valueReserved = Integer.toString(aMaintValue[MaintenanceFeature.RESERVED]);
        boolean enableSeveRoute = aMaintEnable[MaintenanceFeature.SERVER_ROUTE];
        String apiRoute = APIroute;
        boolean enableRestoreFactoryDefaults = aMaintEnable[MaintenanceFeature.RESET_VIN_RESTORE_FACTORY_DEFAULTS];
        String valueRestoreFactoryDefaults = Integer.toString(aMaintValue[MaintenanceFeature.RESET_VIN_RESTORE_FACTORY_DEFAULTS]);
        boolean enableViewServeCommunication = aMaintEnable[MaintenanceFeature.VIEW_SERVER_COMMUNICATION];
        String valueViewServeCommunication = Integer.toString(aMaintValue[MaintenanceFeature.VIEW_SERVER_COMMUNICATION]);
        Bundle bundle =  new Bundle();
        bundle.putBoolean("enableLogFile", enableLogFile);
        bundle.putString("valueLogFile", valueLogFile);
        bundle.putBoolean("enableClutchOverride", enableClutchOverride);
        bundle.putString("valueClutchOverride", valueClutchOverride);
        bundle.putBoolean("enableIdleTimeOverride", enableIdleTimeOverride);
        bundle.putString("valueIdleTimeOverride", valueIdleTimeOverride);
        bundle.putBoolean("enableEngineSpeedAdjustments", enableEngineSpeedAdjustments);
        bundle.putString("valueEngineSpeedAdjustments", valueEngineSpeedAdjustments);
        bundle.putBoolean("enableTimeStampRMP", enableTimeStampRMP);
        bundle.putString("valueTimeStampRMP", valueTimeStampRMP);
        bundle.putBoolean("enableNeutralSwitchDetection", enableNeutralSwitchDetection);
        bundle.putString("valueNeutralSwitchDetection", valueNeutralSwitchDetection);
        bundle.putBoolean("enableReserved", enableReserved);
        bundle.putString("valueReserved", valueReserved);
        bundle.putBoolean("enableSeverRoute", enableSeveRoute);
        bundle.putString("apiRoute", apiRoute);
        bundle.putBoolean("enableRestoreFactoryDefaults", enableRestoreFactoryDefaults);
        bundle.putString("valueRestoreFactoryDefaults", valueRestoreFactoryDefaults);
        bundle.putBoolean("enableViewServeCommunication", enableViewServeCommunication);
        bundle.putString("valueViewServeCommunication", valueViewServeCommunication);
        MaintenanceDialogFragment maintenanceDialogFragment = new MaintenanceDialogFragment();
        maintenanceDialogFragment.setArguments(bundle);
        maintenanceDialogFragment.show(fragmentTransaction, "maintenanceDialogFragment");
    }

    @Override
    public void onExitMaintenance() {
        Log.w(MainActivity.TAG, "Kiosk SuperExit button pressed");
        MainActivity mainActivity = MainActivity.this;
        int i = mainActivity.kiosk_mode_counter + 1;
        mainActivity.kiosk_mode_counter = i;
        if (i == 3) { // Click 3 times for toggling Kiosk mode
            boolean z;
            MainActivity.this.kiosk_mode_counter = 0;
            Context ctx = MainActivity.this.getApplicationContext();
            if (PrefUtils.isKioskModeActive(ctx)) {
                z = false;
            } else {
                z = true;
            }
            MainActivity.KioskMode = z;
            PrefUtils.setKioskModeActive(MainActivity.KioskMode, ctx);
            if (MainActivity.KioskMode) {
                Toast.makeText(ctx, "Shields are UP.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, "Shields are down Commander!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDoneMaintenance(ArrayList<Boolean> aMaintEnable, ArrayList<Integer> aMaintValue) {
        MainActivity.aMaintEnable[MaintenanceFeature.LOG_FILE] = aMaintEnable.get(0);
        MainActivity.aMaintEnable[MaintenanceFeature.CLUTCH_OVERRIDE] = aMaintEnable.get(1);
        MainActivity.aMaintEnable[MaintenanceFeature.IDLE_TIME_OVERRIDE] = aMaintEnable.get(2);
        MainActivity.aMaintEnable[MaintenanceFeature.ENGINE_SPEED_ADJUSTMENTS] = aMaintEnable.get(3);
        MainActivity.aMaintEnable[MaintenanceFeature.TIMESTAMP_RPM] = aMaintEnable.get(4);
        MainActivity.aMaintEnable[MaintenanceFeature.NEUTRAL_SWITCH_DETECTION] = aMaintEnable.get(5);
        MainActivity.aMaintEnable[MaintenanceFeature.RESERVED] = aMaintEnable.get(6);
        MainActivity.aMaintEnable[MaintenanceFeature.SERVER_ROUTE] = aMaintEnable.get(7);
        MainActivity.aMaintEnable[MaintenanceFeature.RESET_VIN_RESTORE_FACTORY_DEFAULTS] = aMaintEnable.get(8);
        MainActivity.aMaintEnable[MaintenanceFeature.VIEW_SERVER_COMMUNICATION] = aMaintEnable.get(9);

        MainActivity.aMaintValue[MaintenanceFeature.LOG_FILE] = aMaintValue.get(0);
        MainActivity.aMaintValue[MaintenanceFeature.CLUTCH_OVERRIDE] = aMaintValue.get(1);
        MainActivity.aMaintValue[MaintenanceFeature.IDLE_TIME_OVERRIDE] = aMaintValue.get(2);
        MainActivity.aMaintValue[MaintenanceFeature.ENGINE_SPEED_ADJUSTMENTS] = aMaintValue.get(3);
        MainActivity.aMaintValue[MaintenanceFeature.TIMESTAMP_RPM] = aMaintValue.get(4);
        MainActivity.aMaintValue[MaintenanceFeature.NEUTRAL_SWITCH_DETECTION] = aMaintValue.get(5);
        MainActivity.aMaintValue[MaintenanceFeature.RESERVED] = aMaintValue.get(6);
        MainActivity.aMaintValue[MaintenanceFeature.RESET_VIN_RESTORE_FACTORY_DEFAULTS] = aMaintValue.get(7);
        MainActivity.aMaintValue[MaintenanceFeature.VIEW_SERVER_COMMUNICATION] = aMaintValue.get(8);

        MainActivity.this.sendMaintInfo();
    }

    public void clearMaintInfo() {
        for (int i = 0; i < 10; i += 1) {
            aMaintEnable[i] = false;
            aMaintValue[i] = 1;
        }

        aMaintValue[MaintenanceFeature.ENGINE_SPEED_ADJUSTMENTS] = 1;
        aMaintValue[MaintenanceFeature.RESERVED] = 1;
        aMaintValue[MaintenanceFeature.SERVER_ROUTE] = 0;
        aMaintValue[MaintenanceFeature.RESET_VIN_RESTORE_FACTORY_DEFAULTS] = 0;
        aMaintValue[MaintenanceFeature.VIEW_SERVER_COMMUNICATION] = 1;
    }

    public void sendMaintInfo() {
        byte[] data = new byte[2];
        // Log File (J1939 data)
       informationSender.writeMaintenanceFeatureCommand(aMaintEnable[MaintenanceFeature.LOG_FILE],aMaintValue[MaintenanceFeature.LOG_FILE], AccessoryControl.APIDEBUG1);

        // Clutch Override
       informationSender.writeMaintenanceFeatureCommand(aMaintEnable[MaintenanceFeature.CLUTCH_OVERRIDE],aMaintValue[MaintenanceFeature.CLUTCH_OVERRIDE], AccessoryControl.APIDEBUG2);

        // Idle Timer Override (1='brake', 2='long brake', 3=spn_1237)
        informationSender.writeMaintenanceFeatureCommand(aMaintEnable[MaintenanceFeature.IDLE_TIME_OVERRIDE],aMaintValue[MaintenanceFeature.IDLE_TIME_OVERRIDE], AccessoryControl.APIDEBUG3);

        // Engine Speed Adjustments (1=during idleup, 2=while running)
        informationSender.writeMaintenanceFeatureCommand(aMaintEnable[MaintenanceFeature.ENGINE_SPEED_ADJUSTMENTS],aMaintValue[MaintenanceFeature.ENGINE_SPEED_ADJUSTMENTS], AccessoryControl.APIDEBUG4);

        // Timestamp/RPM logging
        informationSender.writeMaintenanceFeatureCommand(aMaintEnable[MaintenanceFeature.TIMESTAMP_RPM],aMaintValue[MaintenanceFeature.TIMESTAMP_RPM], AccessoryControl.APIDEBUG5);

        // Neutral switch detection
        informationSender.writeMaintenanceFeatureCommand(aMaintEnable[MaintenanceFeature.NEUTRAL_SWITCH_DETECTION],aMaintValue[MaintenanceFeature.NEUTRAL_SWITCH_DETECTION], AccessoryControl.APIDEBUG6);

        // Reserved
        informationSender.writeMaintenanceFeatureCommand(aMaintEnable[MaintenanceFeature.RESERVED],aMaintValue[MaintenanceFeature.RESERVED], AccessoryControl.APIDEBUG7);

        // Server Route
        if (aMaintEnable[MaintenanceFeature.SERVER_ROUTE]) {
            String ServerRoute = ((EditText) this.maintDialog.findViewById(R.id.maintText_8)).getText().toString();
            if (ServerRoute.trim().isEmpty()) {
                ServerRoute = DefaultAPIroute;
            }
            int length = ServerRoute.trim().length();
            if (length >= 41) {
                ServerRoute = DefaultAPIroute;
            }
            APIroute = ServerRoute.trim();
            Log.w(TAG, "(send) APIDATA_SERVER_ROUTE=" + APIroute);
            informationSender.sendCmdString(AccessoryControl.APIDATA_SERVER_ROUTE, APIroute);
            ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_8)).setChecked(false);
            aMaintEnable[MaintenanceFeature.SERVER_ROUTE] = false;
        }

        // Reset VIN and Restore Factory Defaults
        if (aMaintEnable[MaintenanceFeature.RESET_VIN_RESTORE_FACTORY_DEFAULTS]) {
            Gateway_VIN = "";
            informationSender.sendVIN(Gateway_VIN);
            Log.i(TAG, "(send) APICMD_VIN= " + Gateway_VIN);
            Gateway_Fleet = "";
            informationSender.sendFleet(Gateway_Fleet);
            Log.i(TAG, "(send) APICMD_Fleet= " + Gateway_Fleet);
            ActivationCode = 0;
            this.accessoryControl.writeCommand(AccessoryControl.APIDATA_ACTIVATION_CODE, 0, 0);
            Log.i(TAG, "(send) APIDATA_ACTIVATION_CODE= " + ActivationCode);
            this.accessoryControl.writeCommand(AccessoryControl.APICMD_ACTIVATE, 0, 0);
            Log.i(TAG, "(send) APICMD_ACTIVATE= 0");
            ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_9)).setChecked(false);
            aMaintEnable[MaintenanceFeature.RESET_VIN_RESTORE_FACTORY_DEFAULTS] = false;
        }

        // View Server Communication (1-8 or 99)
        if (aMaintEnable[MaintenanceFeature.VIEW_SERVER_COMMUNICATION]) {
            data[0] = (byte) ((aMaintValue[MaintenanceFeature.VIEW_SERVER_COMMUNICATION] >> 8) & 255);
            data[1] = (byte) (aMaintValue[MaintenanceFeature.VIEW_SERVER_COMMUNICATION] & 255);
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG10, data[0], data[1]);
            return;
        }
        this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG10, 0, 0);
    }
    // endregion

    public boolean isAnyBloatware() {
        Log.i(TAG, "*** Checking for Bloatware...");
        return false;
    }

    public void removeBloatware() {
    }

    private boolean isAppInstalled(String uri) {
        try {
            Log.w(TAG, "    Bloatware found: " + uri + "   Enabled?" + (getPackageManager().getPackageInfo(uri, GET_ACTIVITIES).applicationInfo.enabled ? "Enabled" : "Disabled"));
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private boolean removeApp(String uri) {
        if (isAppInstalled(uri)) {
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.setData(Uri.parse("package:" + uri));
            startActivity(intent);
        }
        return true;
    }

    public boolean isRefreshAvailable() {
        boolean isUpdateAvailable = isServerUpdateAvailable();
        if (isUpdateAvailable) {
            findViewById(R.id.actionButtons).setVisibility(View.GONE);
            findViewById(R.id.updateButtons).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.actionButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.updateButtons).setVisibility(View.GONE);
        }
        return isUpdateAvailable;
    }

    public boolean isServerUpdateAvailable() {
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.i(TAG, "   current APK version: " + version);
            String server_version = PrefUtils.getServerUpdateVersion(getApplicationContext());
            Log.i(TAG, "   server APK version: [" + server_version + "]");
            if (server_version.isEmpty() || version.compareTo(server_version) >= 0) {
                return false;
            }
            return true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void installUpdate() {
        if (isRefreshAvailable()) {
            httpclient.PhoneHome(3, false);
            findViewById(R.id.actionButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.updateButtons).setVisibility(View.GONE);
        }
    }




    public Handler getUIHandler() {
        return new UIHandler(this);
    }

    public void UpdateConnectivityStatus() {
        switch (SyncLast_Status) {
            case PhoneHomeSyncStatus.TIMEOUT /*-3*/:
                setConnectivityStatus("Network Timeout", BAD_CONNECTIVITY);
                break;
            case PhoneHomeSyncStatus.NONE_NETWORK /*-2*/:
                setConnectivityStatus("No Network", BAD_CONNECTIVITY);
                break;
            case PhoneHomeSyncStatus.ERROR /*-1*/:
                setConnectivityStatus("Network Error", BAD_CONNECTIVITY);
                break;
            case PhoneHomeSyncStatus.OK /*1*/:
            case PhoneHomeSyncStatus.NONE /*5*/:
                setConnectivityStatus("Refresh: " + new SimpleDateFormat("MM-dd hh:mm a").format(SyncLast.getTime()), GOOD_CONNECTIVITY);
                break;
            case PhoneHomeSyncStatus.GATEWAY_UPDATE /*2*/:
                setConnectivityStatus("Gateway updating..", GOOD_CONNECTIVITY);
                break;
            case PhoneHomeSyncStatus.PENDING /*3*/:
                setConnectivityStatus("Refreshing..", UNKNOWN_CONNECTIVITY);
                break;
            case PhoneHomeSyncStatus.APK_PENDING /*4*/:
                setConnectivityStatus("Tablet updating..", UNKNOWN_CONNECTIVITY);
                break;
        }
        isRefreshAvailable();
    }

    /**
     * Open a dialog with build flavor
     */
    public void openCommDialog() {
        commlogstr = "";
        CommDialogFragment commDialogFragment = CommDialogFragment.newInstance(commlogstr);
        commDialogFragment.show(this.fragmentTransaction, "CommDialogFragment");
    }

    public void CommLogStr(String str) {
        if (aMaintEnable[MaintenanceFeature.VIEW_SERVER_COMMUNICATION]) {
            commlogstr += str + "\n\r";
            commlogtext.setText(commlogstr);
        }
    }
}
