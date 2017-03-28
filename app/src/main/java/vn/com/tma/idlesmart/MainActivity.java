package vn.com.tma.idlesmart;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.WindowManager;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;


public class MainActivity extends Activity implements OnClickListener {
    public static String APIroute = null;
    public static int ActivationCode = 0;
    public static boolean ActivationProcessPending = false;
    public static final int BAD_CONNECTIVITY = 2;
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
    public static final int GOOD_CONNECTIVITY = 1;
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
    public static boolean KioskMode = false;
    static final long MONITOR_RATE = 3000;
    public static boolean PackageUpdatePending = false;
    public static int Password = 0;
    public static boolean PasswordEnable = false;
    public static boolean PasswordValid = false;
    private static boolean Restart = false;
    public static boolean ServerConnectivity = false;
    public static Calendar SyncLast = null;
    public static int SyncLast_Status = 0;
    public static int SyncNext = 0;
    public static int SyncStart = 0;
    public static int SyncTTL = 0;
    public static boolean SyncWithServer = false;
    public static boolean SystemActivationFlag = false;
    private static final String TAG = "IdleSmart.Main";
    public static final int UNKNOWN_CONNECTIVITY = 0;
    public static boolean ValidActivationProcess = false;
    public static boolean[] aMaintEnable = null;
    public static int[] aMaintValue = null;
    public static int[] aParam = null;
    private static Dialog commDialog = null;
    private static String commlogstr = null;
    private static TextView commlogtext = null;
    public static boolean demo_mode = false;
    public static final boolean enableKioskMode = true;
    public static boolean gateway_connected;
    public static boolean gateway_restarting;
    public static httpClient httpclient;
    static int monitor_iter;
    static int pValue;
    public static boolean packagemanagernag;
    public static boolean test_mode;
    private final int ActivationCodeFragment;
    private final int ActivationFragment;
    private final int BatteryProtect;
    private final int BatteryProtectFragment;
    private int BatteryProtectMode;
    private final int CabinComfort;
    private final int CabinComfortFragment;
    private int CabinComfortMode;
    private final int ColdWeatherGuard;
    private final int ColdWeatherGuardFragment;
    private int ColdWeatherGuardMode;
    private final int DisabledMode;
    Handler EThandler;
    private Runnable ETrunnable;
    private final int EnabledMode;
    int GatewayMode;
    private final int Idle;
    private final int InstallationFragment;
    private final int None;
    private final int RunMode;
    private final int RunningFragment;
    private boolean ScreenOn;
    private final int SettingsDisableFragment;
    private final int SettingsEnableFragment;
    private final int SettingsEntryMode;
    private final int SettingsMenu1Mode;
    private final int SettingsMenu2Mode;
    private final int SettingsNumericFragment;
    private final int SettingsPWFragment;
    private final int SettingsRefreshFragment;
    private final int SettingsSerialFragment;
    private final int StopMode;
    final Handler USBReconnectHandler;
    private Runnable USBReconnectRunnable;
    private final BroadcastReceiver UsbReceiver;
    private final int VINCodeFragment;
    private final int VerificationFragment;
    private String[] aSystemStatus;
    public AccessoryControl accessoryControl;
    private int activation_step;
    private Dialog alertDialog;
    private final List blockedKeys;
    public Faults faults;
    private int initialScreenBrightness;
    private int initialScreenTimeout;
    private boolean isScreenOn;
    private final int killswitch_active;
    private final int killswitch_poweroff;
    private final int killswitch_poweron;
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
    private Dialog passwordDialog;
    final Handler screentimeoutHandler;
    private Dialog serialDialog;
    private int settings_entrytype;
    public int settings_menu1_index;
    public int settings_menu2_index;
    private int test_mark_counter;
    private int test_mode_counter;
    final Runnable timeoutRunnable;
    private LinearLayout topLayout;
    final Handler verificationHandler;
    final Runnable verificationRunnable;

    /* renamed from: com.idlesmarter.aoa.MainActivity.11 */
    class AnonymousClass11 implements OnClickListener {
        final /* synthetic */ int val$faultId;

        AnonymousClass11(int i) {
            this.val$faultId = i;
        }

        public void onClick(View v) {
            MainActivity.this.accessoryControl.writeCommand(33, MainActivity.UNKNOWN_CONNECTIVITY, this.val$faultId);
            MainActivity.this.alertDialog.dismiss();
            if (MainActivity.this.mTempWakeLock.isHeld()) {
                MainActivity.this.mTempWakeLock.release();
            }
        }
    }

    /* renamed from: com.idlesmarter.aoa.MainActivity.1 */
    class C00011 implements OnSystemUiVisibilityChangeListener {
        C00011() {
        }

        public void onSystemUiVisibilityChange(int visibility) {
            MainActivity.this.hideNavBar();
        }
    }

    /* renamed from: com.idlesmarter.aoa.MainActivity.2 */
    class C00022 implements Runnable {
        C00022() {
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
                    MainActivity.monitor_iter = i + MainActivity.GOOD_CONNECTIVITY;
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

    /* renamed from: com.idlesmarter.aoa.MainActivity.3 */
    class C00033 implements Runnable {
        C00033() {
        }

        public void run() {
            MainActivity.this.hideNavBar();
        }
    }

    /* renamed from: com.idlesmarter.aoa.MainActivity.4 */
    class C00044 implements Runnable {
        C00044() {
        }

        public void run() {
            MainActivity.SyncWithServer = MainActivity.enableKioskMode;
            Log.i(MainActivity.TAG, "--> <TimerTask>: SyncWithServer");
            MainActivity.this.SetNextPhoneHome();
        }
    }

    /* renamed from: com.idlesmarter.aoa.MainActivity.5 */
    class C00055 implements Runnable {
        C00055() {
        }

        public void run() {
            MainActivity.this.updateScreenTimeout();
        }
    }

    /* renamed from: com.idlesmarter.aoa.MainActivity.6 */
    class C00066 implements Runnable {
        C00066() {
        }

        public void run() {
            MainActivity.this.setScreenBrightness(MainActivity.UNKNOWN_CONNECTIVITY);
            MainActivity.this.isScreenOn = false;
        }
    }

    /* renamed from: com.idlesmarter.aoa.MainActivity.7 */
    class C00077 implements OnEditorActionListener {
        C00077() {
        }

        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            byte[] data = new byte[MainActivity.BAD_CONNECTIVITY];
            if (actionId == 6) {
                switch (v.getId()) {
                    case R.id.activationCodeEditText /*2131361797*/:
                        MainActivity.ActivationCode = Integer.valueOf(((EditText) v).getText().toString()).intValue();
                        data[MainActivity.UNKNOWN_CONNECTIVITY] = (byte) ((MainActivity.ActivationCode >> 8) & 255);
                        data[MainActivity.GOOD_CONNECTIVITY] = (byte) (MainActivity.ActivationCode & 255);
                        MainActivity.this.accessoryControl.writeCommand(AccessoryControl.APIDATA_ACTIVATION_CODE, data[MainActivity.UNKNOWN_CONNECTIVITY], data[MainActivity.GOOD_CONNECTIVITY]);
                        Log.i(MainActivity.TAG, "(send) APIDATA_ACTIVATION_CODE=" + MainActivity.ActivationCode);
                        break;
                    case R.id.VINCodeEditText /*2131362092*/:
                        MainActivity.Gateway_VIN = ((EditText) v).getText().toString();
                        MainActivity.this.sendVIN(MainActivity.Gateway_VIN);
                        Log.i(MainActivity.TAG, "(send) APICMD_VIN=" + MainActivity.Gateway_VIN);
                        break;
                }
            }
            return false;
        }
    }

    /* renamed from: com.idlesmarter.aoa.MainActivity.8 */
    class C00088 implements OnClickListener {
        C00088() {
        }

        public void onClick(View v) {
            MainActivity.this.passwordDialog.dismiss();
            MainActivity.PasswordValid = false;
        }
    }

    /* renamed from: com.idlesmarter.aoa.MainActivity.9 */
    class C00099 implements OnClickListener {
        C00099() {
        }

        public void onClick(View v) {
            MainActivity.this.passwordDialog.dismiss();
            int pwtemp = MainActivity.this.toInteger(((EditText) MainActivity.this.passwordDialog.findViewById(R.id.passwordEditText)).getText().toString());
            MainActivity.PasswordValid = pwtemp == MainActivity.Password ? MainActivity.enableKioskMode : false;
            if (MainActivity.test_mode && pwtemp == 8800) {
                MainActivity.PasswordEnable = false;
                MainActivity.PasswordValid = MainActivity.enableKioskMode;
            }
        }
    }

    public class ScreenReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.i(MainActivity.TAG, "==> ScreenReceiver::onReceive");
            String action = intent.getAction();
            if (action.equals("android.intent.action.SCREEN_OFF")) {
                Log.i(MainActivity.TAG, "    ScreenReceiver:Intent=SCREEN_OFF");
                MainActivity.this.setScreenBrightness(MainActivity.UNKNOWN_CONNECTIVITY);
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
            boolean z = MainActivity.enableKioskMode;
            MainActivity mainActivityClass = this.mainActivityClassWeakReference.get();
            if (mainActivityClass != null) {
                String str;
                switch (msg.what) {
                    case Params.PARAM_TruckTimer /*18*/:
                        MainActivity.aParam[14] = msg.arg1;
                        mainActivityClass.setScreenTimeout(msg.arg1);
                    case Params.PARAM_PasswordEnable /*19*/:
                        MainActivity.aParam[15] = msg.arg1;
                        if (msg.arg1 == 0) {
                            z = false;
                        }
                        mainActivityClass.setSoundOn(z);
                    case AccessoryControl.APICMD_ENGINE_IDLE_RPM /*30*/:
                        MainActivity.aParam[16] = msg.arg1;
                    case AccessoryControl.APICMD_ENGINE_RESTART_INTERVAL /*31*/:
                        MainActivity.aParam[9] = msg.arg1;
                    case AccessoryControl.APICMD_AUTO_SHUTOFF_TIMEOUT /*32*/:
                        MainActivity.aParam[7] = msg.arg1;
                    case AccessoryControl.APICMD_CABIN_COMFORT_ENABLE /*40*/:
                        if (msg.arg1 == 0) {
                            mainActivityClass.setFunctionMode(MainActivity.GOOD_CONNECTIVITY, MainActivity.UNKNOWN_CONNECTIVITY);
                        } else {
                            mainActivityClass.setFunctionMode(MainActivity.GOOD_CONNECTIVITY, MainActivity.GOOD_CONNECTIVITY);
                        }
                    case AccessoryControl.VIN_ID_MAX /*41*/:
                        MainActivity.aParam[3] = msg.arg1;
                    case AccessoryControl.APICMD_CABIN_TEMP_RANGE /*42*/:
                        MainActivity.aParam[4] = msg.arg1;
                    case AccessoryControl.APICMD_AMBIENT_TEMP_SETPOINT /*43*/:
                        MainActivity.aParam[5] = msg.arg1;
                    case AccessoryControl.APICMD_AMBIENT_TEMP_RANGE /*44*/:
                        MainActivity.aParam[6] = msg.arg1;
                    case AccessoryControl.APICMD_SYSTEMTIMER /*45*/:
                        MainActivity.aParam[18] = msg.arg1;
                    case AccessoryControl.APICMD_DRIVER_TEMP_COMMON /*48*/:
                        MainActivity.aParam[17] = msg.arg1;
                    case AccessoryControl.APICMD_BATTERY_MONITOR_ENABLE /*50*/:
                        if (msg.arg1 == 0) {
                            mainActivityClass.setFunctionMode(3, MainActivity.UNKNOWN_CONNECTIVITY);
                        } else {
                            mainActivityClass.setFunctionMode(3, MainActivity.GOOD_CONNECTIVITY);
                        }
                    case AccessoryControl.APICMD_BATTERY_MONITOR_VOLTAGE /*51*/:
                        MainActivity.aParam[8] = msg.arg1;
                    case AccessoryControl.APICMD_BATTERY_MONITOR_RUNTIME /*52*/:
                        MainActivity.aParam[9] = msg.arg1;
                    case AccessoryControl.APICMD_SYNC_START /*53*/:
                        mainActivityClass.SetNextPhoneHome();
                    case AccessoryControl.APICMD_SYNC_TTL /*54*/:
                        mainActivityClass.SetNextPhoneHome();
                    case AccessoryControl.APICMD_COLD_WEATHER_GUARD_ENABLE /*55*/:
                        if (msg.arg1 == 0) {
                            mainActivityClass.setFunctionMode(MainActivity.BAD_CONNECTIVITY, MainActivity.UNKNOWN_CONNECTIVITY);
                        } else {
                            mainActivityClass.setFunctionMode(MainActivity.BAD_CONNECTIVITY, MainActivity.GOOD_CONNECTIVITY);
                        }
                    case AccessoryControl.APICMD_COLD_WEATHER_GUARD_START_TEMP /*56*/:
                        MainActivity.aParam[12] = msg.arg1;
                    case AccessoryControl.APICMD_COLD_WEATHER_GUARD_RESTART_INTERVAL /*58*/:
                        MainActivity.aParam[13] = msg.arg1;
                    case AccessoryControl.APICMD_COLD_WEATHER_GUARD_MIN_COOLANT /*59*/:
                        MainActivity.aParam[11] = msg.arg1;
                    case AccessoryControl.APICMD_COLD_WEATHER_GUARD_IDEAL_COOLANT /*60*/:
                        MainActivity.aParam[10] = msg.arg1;
                    case AccessoryControl.APIEVENT_SYNC /*69*/:
                        if (MainActivity.SyncWithServer) {
                            MainActivity.SyncWithServer = false;
                            MainActivity.httpclient.PhoneHome(MainActivity.GOOD_CONNECTIVITY, MainActivity.enableKioskMode);
                        }
                    case AccessoryControl.APIEVENT_CURRENT_MODE /*78*/:
                        Log.i(MainActivity.TAG, "handler::CurrentMode received");
                        mainActivityClass.useGatewayMode(msg.arg1);
                    case AccessoryControl.APIEVENT_ACTIVATED /*80*/:
                        MainActivity.Gateway_Activated = msg.arg1;
                        Log.i(MainActivity.TAG, "APIEVENT_ACTIVATED = " + MainActivity.Gateway_Activated);
                        if (MainActivity.Gateway_Activated == 0) {
                            z = false;
                        }
                        mainActivityClass.InstallAndActivate(z);
                    case AccessoryControl.APIEVENT_REQUPDATE /*81*/:
                        MainActivity.httpclient.PhoneHome(MainActivity.BAD_CONNECTIVITY, false);
                    case AccessoryControl.APIEVENT_SYSTEM_STATUS /*84*/:
                        mainActivityClass.setEngineStatus(mainActivityClass.aSystemStatus[msg.arg1]);
                    case AccessoryControl.APIEVENT_DRIVING_MODE /*88*/:
                        if (msg.arg1 == 0) {
                            mainActivityClass.wakeup();
                            mainActivityClass.back2sleep();
                        }
                    case AccessoryControl.APIEVENT_ALERT /*94*/:
                        mainActivityClass.openAlertDialog(msg.arg1);
                    case AccessoryControl.APIEVENT_CLEARALERT /*95*/:
                        mainActivityClass.closeAlertDialog(msg.arg1);
                    case AccessoryControl.APIEVENT_CLEARALLALERTS /*96*/:
                        mainActivityClass.closeAllAlertDialogs();
                    case AccessoryControl.APIEVENT_WARNING /*97*/:
                        mainActivityClass.openAlertDialog(msg.arg1);
                    case AccessoryControl.APIEVENT_CLEARWARNING /*98*/:
                        mainActivityClass.closeAlertDialog(msg.arg1);
                    case AccessoryControl.APIEVENT_CLEARALLWARNINGS /*99*/:
                        mainActivityClass.closeAllAlertDialogs();
                    case AccessoryControl.APIEVENT_HANDLER_EXCEPTION /*127*/:
                        Log.e(MainActivity.TAG, "USB_HANDLER_EXCEPTION: " + msg.arg1);
                        MainActivity.gateway_connected = false;
                        mainActivityClass.disconnected();
                        mainActivityClass.accessoryControl.close();
                    case AccessoryControl.APIDATA_CABIN_TEMP /*136*/:
                        str = Integer.toString(msg.arg1) + "\u00b0";
                        ((TextView) mainActivityClass.findViewById(R.id.cabinComfortValue)).setText(str);
                        ((TextView) mainActivityClass.findViewById(R.id.ccFragTemperatureValue)).setText(str);
                    case AccessoryControl.APIDATA_AMBIENT_TEMP /*137*/:
                        ((TextView) mainActivityClass.findViewById(R.id.ccFragAmbientTempValue)).setText(Integer.toString(msg.arg1) + "\u00b0");
                    case AccessoryControl.APIDATA_BATTERY_VOLTAGE /*138*/:
                        str = Integer.toString(msg.arg1);
                        ((TextView) mainActivityClass.findViewById(R.id.batteryProtectValue)).setText(str.substring(MainActivity.UNKNOWN_CONNECTIVITY, str.length() - 1) + "." + str.substring(str.length() - 1) + mainActivityClass.params.aParamSfx[8]);
                    case AccessoryControl.APIDATA_SYNC_LAST /*142*/:
                        if (MainActivity.SyncLast_Status == MainActivity.BAD_CONNECTIVITY) {
                            MainActivity.SyncLast_Status = MainActivity.GOOD_CONNECTIVITY;
                            MainActivity.httpclient.sendSyncLast(MainActivity.SyncLast_Status, MainActivity.SyncLast);
                        }
                        mainActivityClass.UpdateConnectivityStatus();
                    case AccessoryControl.APIDATA_TIMEREMAINING /*158*/:
                        if (mainActivityClass.GatewayMode != 3 || msg.arg1 <= 0) {
                            str = mainActivityClass.Time2MinsSecsStr(MainActivity.aParam[9] * 60);
                        } else {
                            str = mainActivityClass.Time2MinsSecsStr(msg.arg1);
                        }
                        ((TextView) mainActivityClass.findViewById(R.id.bpFragTimeRemainingValue)).setText(str);
                    case AccessoryControl.APIDATA_FLEET_CABIN_COMFORT_ENABLE /*159*/:
                        if (msg.arg1 == 0) {
                            MainActivity.aParam[23] = MainActivity.UNKNOWN_CONNECTIVITY;
                        } else {
                            MainActivity.aParam[23] = MainActivity.GOOD_CONNECTIVITY;
                        }
                    case AccessoryControl.APIDATA_FLEET_CABIN_TEMP_SETPOINT /*160*/:
                        MainActivity.aParam[24] = msg.arg1;
                    case AccessoryControl.APICAN_ENGINE_COOLANT_TEMP /*193*/:
                        str = Integer.toString(msg.arg1) + "\u00b0";
                        ((TextView) mainActivityClass.findViewById(R.id.cwgFragTemperatureValue)).setText(str);
                        ((TextView) mainActivityClass.findViewById(R.id.coldWeatherGuardValue)).setText(str);
                    default:
                }
            }
        }
    }

    public MainActivity() {
        this.verificationHandler = new Handler();
        this.screentimeoutHandler = new Handler();
        this.EThandler = new Handler();
        this.USBReconnectHandler = new Handler();
        this.alertDialog = null;
        this.maintDialog = null;
        this.serialDialog = null;
        this.passwordDialog = null;
        this.mTempWakeLock = null;
        this.menus = new Menus();
        this.params = new Params();
        this.faults = new Faults();
        this.test_mode_counter = UNKNOWN_CONNECTIVITY;
        this.maint_mode_counter = UNKNOWN_CONNECTIVITY;
        this.test_mark_counter = UNKNOWN_CONNECTIVITY;
        this.Idle = UNKNOWN_CONNECTIVITY;
        this.CabinComfort = GOOD_CONNECTIVITY;
        this.ColdWeatherGuard = BAD_CONNECTIVITY;
        this.BatteryProtect = 3;
        this.GatewayMode = UNKNOWN_CONNECTIVITY;
        this.RunningFragment = GOOD_CONNECTIVITY;
        this.CabinComfortFragment = BAD_CONNECTIVITY;
        this.ColdWeatherGuardFragment = 3;
        this.BatteryProtectFragment = 4;
        this.DisabledMode = UNKNOWN_CONNECTIVITY;
        this.EnabledMode = GOOD_CONNECTIVITY;
        this.RunMode = BAD_CONNECTIVITY;
        this.StopMode = 3;
        this.CabinComfortMode = UNKNOWN_CONNECTIVITY;
        this.ColdWeatherGuardMode = UNKNOWN_CONNECTIVITY;
        this.BatteryProtectMode = UNKNOWN_CONNECTIVITY;
        this.None = UNKNOWN_CONNECTIVITY;
        this.VerificationFragment = GOOD_CONNECTIVITY;
        this.InstallationFragment = BAD_CONNECTIVITY;
        this.ActivationFragment = 3;
        this.ActivationCodeFragment = 4;
        this.VINCodeFragment = 5;
        this.activation_step = UNKNOWN_CONNECTIVITY;
        this.SettingsMenu1Mode = GOOD_CONNECTIVITY;
        this.SettingsMenu2Mode = BAD_CONNECTIVITY;
        this.SettingsEntryMode = 3;
        this.SettingsEnableFragment = GOOD_CONNECTIVITY;
        this.SettingsDisableFragment = BAD_CONNECTIVITY;
        this.SettingsNumericFragment = 3;
        this.SettingsPWFragment = 4;
        this.SettingsRefreshFragment = 5;
        this.SettingsSerialFragment = 6;
        this.settings_entrytype = UNKNOWN_CONNECTIVITY;
        this.settings_menu1_index = UNKNOWN_CONNECTIVITY;
        this.settings_menu2_index = UNKNOWN_CONNECTIVITY;
        this.killswitch_active = GOOD_CONNECTIVITY;
        this.killswitch_poweroff = BAD_CONNECTIVITY;
        this.killswitch_poweron = 3;
        this.initialScreenBrightness = UNKNOWN_CONNECTIVITY;
        this.initialScreenTimeout = UNKNOWN_CONNECTIVITY;
        this.ScreenOn = enableKioskMode;
        this.USBReconnectRunnable = new C00022();
        Integer[] numArr = new Integer[BAD_CONNECTIVITY];
        numArr[UNKNOWN_CONNECTIVITY] = Integer.valueOf(25);
        numArr[GOOD_CONNECTIVITY] = Integer.valueOf(24);
        this.blockedKeys = new ArrayList(Arrays.asList(numArr));
        this.ETrunnable = new C00044();
        this.isScreenOn = enableKioskMode;
        this.timeoutRunnable = new C00066();
        this.mEditorActionListener = new C00077();
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
        this.kiosk_mode_counter = UNKNOWN_CONNECTIVITY;
        this.aSystemStatus = new String[]{"NOT ACTIVATED", "DISABLED", "DISENGAGED", "VEHICLE NOT READY", "TEMP CHANGE FAULT", "STANDBY MODE", "TEMP INTERLOCK", "INITIATING START", "STARTING ENGINE", "WARMING UP", "ENGINE RUNNING", "RUNNING NORMAL", "CHARGING BATTERY", "RUNNING COLD GUARD", "SHUTTING DOWN", "RESTART DELAY", "DOWNLOADING", "VEHICLE NOT READY - Transmission not in Neutral", "VEHICLE NOT READY - Parking Brake not set", "VEHICLE NOT READY - Hood is Open", "VEHICLE NOT READY - Regen Required", "VEHICLE NOT READY - Ignition Switch is On", "VEHICLE NOT READY - Battery Voltage below 11.0V"};
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
                                    MainActivity.gateway_connected = MainActivity.enableKioskMode;
                                    MainActivity.demo_mode = false;
                                    MainActivity.this.connected();
                                    MainActivity.this.enableDashboard(MainActivity.enableKioskMode);
                                    MainActivity.this.selectRunning(MainActivity.GOOD_CONNECTIVITY);
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
                    MainActivity.gateway_connected = MainActivity.enableKioskMode;
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
        DebugLog = enableKioskMode;
        commDialog = null;
        KioskMode = false;
        Restart = false;
        HasFocus = enableKioskMode;
        SystemActivationFlag = false;
        gateway_connected = false;
        gateway_restarting = false;
        demo_mode = false;
        test_mode = false;
        packagemanagernag = enableKioskMode;
        CurrentStatusBarFlag = false;
        CurrentDashboardFlag = false;
        CurrentDashboardFragment = UNKNOWN_CONNECTIVITY;
        CurrentSettingsFlag = false;
        CurrentGatewayStatus = BuildConfig.FLAVOR;
        CurrentEngineStatus = BuildConfig.FLAVOR;
        CurrentConnectivityStatus = BuildConfig.FLAVOR;
        Password = UNKNOWN_CONNECTIVITY;
        PasswordEnable = false;
        PasswordValid = false;
        aParam = new int[25];
        aMaintEnable = new boolean[10];
        aMaintValue = new int[10];
        Gateway_HWver = UNKNOWN_CONNECTIVITY;
        Gateway_LDRversion = BuildConfig.FLAVOR;
        Gateway_FWversion = BuildConfig.FLAVOR;
        Gateway_APIversion = BuildConfig.FLAVOR;
        Gateway_SerialID = BuildConfig.FLAVOR;
        Gateway_VIN = BuildConfig.FLAVOR;
        Gateway_Fleet = BuildConfig.FLAVOR;
        Gateway_Activated = UNKNOWN_CONNECTIVITY;
        Gateway_Guid = UNKNOWN_CONNECTIVITY;
        ActivationCode = UNKNOWN_CONNECTIVITY;
        ValidActivationProcess = false;
        ActivationProcessPending = false;
        APIroute = DefaultAPIroute;
        SyncWithServer = false;
        commlogtext = null;
        commlogstr = BuildConfig.FLAVOR;
        PackageUpdatePending = false;
        GatewayUpdatePending = false;
        ServerConnectivity = false;
        ConnectivityIndicator = UNKNOWN_CONNECTIVITY;
        SyncStart = 60;
        SyncTTL = 1440;
        SyncNext = UNKNOWN_CONNECTIVITY;
        SyncLast_Status = UNKNOWN_CONNECTIVITY;
        SyncLast = Calendar.getInstance();
        monitor_iter = UNKNOWN_CONNECTIVITY;
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
            KioskMode = enableKioskMode;
            PrefUtils.setKioskModeActive(KioskMode, getApplicationContext());
        }
        if (!Restart) {
            clearMaintInfo();
        }
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new C00011());
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
        this.topLayout.setBackgroundColor(-16777216);
        if (!Restart) {
            Features.initFeatureCodeTable();
            initializeRunningParams();
        }
        IntentFilter filter = new IntentFilter(AccessoryControl.ACTION_USB_PERMISSION);
        filter.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
        filter.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
        Log.i(TAG, "register UsbReceiver..");
        registerReceiver(this.UsbReceiver, filter);
        this.mWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(536870913, "IdleSmartWakeLock");
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
        startScreenHandler(aParam[14]);
        PrefUtils.setApkUpdateState(UNKNOWN_CONNECTIVITY, getApplicationContext());
        Restart = enableKioskMode;
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
        Restart = enableKioskMode;
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
        HasFocus = enableKioskMode;
        if (!this.mWakeLock.isHeld()) {
            this.mWakeLock.acquire();
        }
        resetScreenTimeout(aParam[14]);
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
            enableDashboard(enableKioskMode);
            selectRunning(GOOD_CONNECTIVITY);
            enableSettings(false);
        } else {
            enableSettings(CurrentSettingsFlag);
        }
        selectSettingsMode(UNKNOWN_CONNECTIVITY);
        selectActivationFragment(UNKNOWN_CONNECTIVITY);
        selectKillswitchMode(UNKNOWN_CONNECTIVITY);
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
        this.accessoryControl.writeCommand(5, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
        if (DebugLog) {
            Log.i(TAG, "<<==end OnResume");
        }
    }

    public void connectUSB() {
        Log.i(TAG, "    --> <connectUSB>");
        Log.i(TAG, "      PackageUpdatePending:" + (PackageUpdatePending ? "true" : "false"));
        if (!(PackageUpdatePending || gateway_connected)) {
            Log.w(TAG, "        connectUSB::Not connected - open AccessoryControl and attempt to reconnect to gateway..");
            AccessoryControl.OpenStatus status = this.accessoryControl.open();
            Log.i(TAG, "        connectUSB::status=" + status.toString());
            if (status == AccessoryControl.OpenStatus.CONNECTED) {
                Log.i(TAG, "        connectUSB::we are now connected to gateway");
                gateway_connected = enableKioskMode;
                Log.i(TAG, "        connectUSB::send APICMD_POWERON..");
                this.accessoryControl.writeCommand(20, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
                connected();
                enableDashboard(enableKioskMode);
                selectRunning(GOOD_CONNECTIVITY);
                enableSettings(false);
                Log.i(TAG, "        connectUSB::send APICMD_SYNC.. (Request data from Gateway");
                this.accessoryControl.writeCommand(5, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
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
                monitor_iter = UNKNOWN_CONNECTIVITY;
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
            Log.i(TAG, "   current APK version: " + getPackageManager().getPackageInfo(getPackageName(), UNKNOWN_CONNECTIVITY).versionName);
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
            ((ActivityManager) getSystemService("activity")).moveTaskToFront(getTaskId(), UNKNOWN_CONNECTIVITY);
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
        moveTaskToBack(enableKioskMode);
        Process.killProcess(Process.myPid());
        java.lang.System.exit(UNKNOWN_CONNECTIVITY);
    }

    public void abort() {
        Log.e(TAG, "###################### abort() ##############################");
        AppContext.instance.stopKioskService();
        moveTaskToBack(enableKioskMode);
        Process.killProcess(Process.myPid());
        java.lang.System.exit(GOOD_CONNECTIVITY);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (DebugLog) {
            Log.i(TAG, "==>>onDestroy");
        }
        this.accessoryControl.closeLogFile();
        Log.i(TAG, "Cancel PhoneHome()..");
        CancelPhoneHome();
        Log.w(TAG, "Send APICMD_DISCONNECT to Gateway..");
        this.accessoryControl.writeCommand(BAD_CONNECTIVITY, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
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

    public void onBackPressed() {
        if (!KioskMode) {
            super.onBackPressed();
        }
    }

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

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KioskMode && this.blockedKeys.contains(Integer.valueOf(event.getKeyCode()))) {
            return enableKioskMode;
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        resetScreenTimeout(aParam[14]);
        if (httpclient.dialog != null && httpclient.dialog.isShowing()) {
            httpclient.dialog.cancel();
        }
        return false;
    }

    private void executeDelayed() {
        new Handler().postDelayed(new C00033(), 100);
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
            selectActivationFragment(3);
        } else if (!SystemActivationFlag) {
            SystemActivationFlag = enableKioskMode;
            Log.i(TAG, "VerifyActivation: ACTIVATED");
            enableStatusBar(enableKioskMode);
            enableDashboard(enableKioskMode);
            selectRunning(GOOD_CONNECTIVITY);
        }
    }

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
        Log.i(TAG, "Next PhoneHome scheduled for: " + date.getTime().toString());
        byte[] data = new byte[BAD_CONNECTIVITY];
        SyncNext = (date.get(11) * 60) + date.get(12);
        data[UNKNOWN_CONNECTIVITY] = (byte) ((SyncNext >> 8) & 255);
        data[GOOD_CONNECTIVITY] = (byte) (SyncNext & 255);
        this.accessoryControl.writeCommand(AccessoryControl.APIDATA_SYNC_NEXT, data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
        CancelPhoneHome();
        long delay = date.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        this.EThandler.postDelayed(this.ETrunnable, delay);
        Log.i(TAG, "Next PhoneHome in (msecs):" + Long.toString(delay));
    }

    private Calendar CalcNextPhoneHome() {
        Calendar date = Calendar.getInstance();
        int hour = SyncStart / 60;
        int min = SyncStart - (hour * 60);
        date.set(11, hour);
        date.set(12, min);
        while (date.before(Calendar.getInstance())) {
            date.add(12, SyncTTL);
        }
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(11, UNKNOWN_CONNECTIVITY);
        tomorrow.set(12, UNKNOWN_CONNECTIVITY);
        tomorrow.set(13, UNKNOWN_CONNECTIVITY);
        tomorrow.add(6, GOOD_CONNECTIVITY);
        if (date.before(tomorrow)) {
            return date;
        }
        date = tomorrow;
        date.set(11, hour);
        date.set(12, min);
        return date;
    }

    private void CancelPhoneHome() {
        this.EThandler.removeCallbacks(this.ETrunnable);
    }

    private int getScreenBrightness() {
        int curBrightnessValue = UNKNOWN_CONNECTIVITY;
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
        int curTimeoutValue = UNKNOWN_CONNECTIVITY;
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
        AudioManager aManager = (AudioManager) getSystemService("audio");
        if (enable) {
            aManager.setRingerMode(BAD_CONNECTIVITY);
        } else {
            aManager.setRingerMode(UNKNOWN_CONNECTIVITY);
        }
    }

    private void startScreenHandler(int delay_secs) {
        this.isScreenOn = enableKioskMode;
        this.screentimeoutHandler.postDelayed(new C00055(), (long) (delay_secs * 1000));
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
        PowerManager mPowerManager = (PowerManager) getSystemService("power");
        if (this.mTempWakeLock == null) {
            this.mTempWakeLock = mPowerManager.newWakeLock(805306394, "IdleSmartTempWakeLock");
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
                selectActivationFragment(4);
                findViewById(R.id.activationFragment).setVisibility(View.GONE);
                findViewById(R.id.activationCodeFragment).setVisibility(View.VISIBLE);
            case R.id.activationCodeContinueButton /*2131361798*/:
                selectActivationFragment(5);
                findViewById(R.id.activationCodeFragment).setVisibility(View.GONE);
                findViewById(R.id.VINCodeFragment).setVisibility(View.VISIBLE);
            case R.id.dashboardButton /*2131361812*/:
                this.test_mode_counter = UNKNOWN_CONNECTIVITY;
                this.maint_mode_counter = UNKNOWN_CONNECTIVITY;
                if (SystemActivationFlag || demo_mode) {
                    selectKillswitchMode(UNKNOWN_CONNECTIVITY);
                    enableStatusBar(enableKioskMode);
                    enableDashboard(enableKioskMode);
                    selectRunning(GOOD_CONNECTIVITY);
                    enableSettings(false);
                    selectActivationFragment(UNKNOWN_CONNECTIVITY);
                    PasswordValid = false;
                }
            case R.id.settingsButton /*2131361814*/:
                this.test_mode_counter = UNKNOWN_CONNECTIVITY;
                this.maint_mode_counter = UNKNOWN_CONNECTIVITY;
                if (SystemActivationFlag || demo_mode) {
                    selectKillswitchMode(UNKNOWN_CONNECTIVITY);
                    enableStatusBar(false);
                    enableDashboard(false);
                    selectRunning(UNKNOWN_CONNECTIVITY);
                    enableSettings(enableKioskMode);
                    selectActivationFragment(UNKNOWN_CONNECTIVITY);
                    this.settings_menu1_index = UNKNOWN_CONNECTIVITY;
                    this.settings_menu2_index = UNKNOWN_CONNECTIVITY;
                    selectSettingsMode(GOOD_CONNECTIVITY);
                    PasswordValid = false;
                }
            case R.id.killSwitchButton /*2131361816*/:
                this.test_mode_counter = UNKNOWN_CONNECTIVITY;
                this.maint_mode_counter = UNKNOWN_CONNECTIVITY;
                if (SystemActivationFlag) {
                    selectKillswitchMode(GOOD_CONNECTIVITY);
                }
            case R.id.installDoneButton /*2131361837*/:
                findViewById(R.id.installFragment).setVisibility(View.GONE);
                if (ValidActivationProcess) {
                    this.accessoryControl.writeCommand(15, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
                    Log.i(TAG, "APICMD_ACTIVATE = 1");
                    enableDashboard(enableKioskMode);
                    selectRunning(GOOD_CONNECTIVITY);
                    httpclient.PhoneHome(UNKNOWN_CONNECTIVITY, false);
                    return;
                }
                InstallAndActivate(SystemActivationFlag);
            case R.id.poweronButton /*2131361943*/:
                selectKillswitchMode(UNKNOWN_CONNECTIVITY);
                enableStatusBar(enableKioskMode);
                enableDashboard(enableKioskMode);
                findViewById(R.id.fullScreen).setVisibility(View.VISIBLE);
                this.accessoryControl.writeCommand(20, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
            case R.id.idlesmartButton /*2131361944*/:
                this.test_mark_counter += GOOD_CONNECTIVITY;
                Log.i(TAG, "************************************ TEST MARK: " + this.test_mark_counter);
                if (gateway_connected) {
                    this.accessoryControl.writefmtCANLogStream("TEST MARK: " + Integer.toString(this.test_mark_counter));
                    if (test_mode) {
                        setGatewayStatus("Gateway Connected");
                        test_mode = false;
                        this.test_mode_counter = UNKNOWN_CONNECTIVITY;
                        this.maint_mode_counter = UNKNOWN_CONNECTIVITY;
                        this.accessoryControl.writeCommand(16, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
                        return;
                    }
                    i = this.test_mode_counter + GOOD_CONNECTIVITY;
                    this.test_mode_counter = i;
                    if (i >= 5) {
                        setGatewayStatus("Test Mode");
                        test_mode = enableKioskMode;
                        this.accessoryControl.writeCommand(16, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
                    }
                } else if (demo_mode) {
                    demo_mode = false;
                    setGatewayStatus("Gateway Disconnected");
                    enableDashboard(false);
                    enableStatusBar(false);
                    selectRunning(UNKNOWN_CONNECTIVITY);
                    enableSettings(false);
                    this.test_mode_counter = UNKNOWN_CONNECTIVITY;
                    this.maint_mode_counter = UNKNOWN_CONNECTIVITY;
                } else {
                    i = this.test_mode_counter + GOOD_CONNECTIVITY;
                    this.test_mode_counter = i;
                    if (i >= 5) {
                        demo_mode = enableKioskMode;
                        setGatewayStatus("Demo Mode");
                        enableDashboard(enableKioskMode);
                        enableStatusBar(enableKioskMode);
                        selectRunning(GOOD_CONNECTIVITY);
                        enableSettings(false);
                    }
                }
            case R.id.updateInstallButton /*2131361948*/:
                installUpdate();
            case R.id.maintButton /*2131361949*/:
                i = this.maint_mode_counter + GOOD_CONNECTIVITY;
                this.maint_mode_counter = i;
                if (i >= 3) {
                    openMaintDialog();
                    this.maint_mode_counter = UNKNOWN_CONNECTIVITY;
                }
            case R.id.passwordReturnButton /*2131361954*/:
                PasswordValid = false;
            case R.id.cabinComfortFunction /*2131361959*/:
                PasswordValid = false;
                if (CurrentDashboardFragment == BAD_CONNECTIVITY) {
                    selectRunning(GOOD_CONNECTIVITY);
                } else {
                    selectRunning(BAD_CONNECTIVITY);
                }
            case R.id.coldWeatherGuardFunction /*2131361960*/:
                PasswordValid = false;
                if (CurrentDashboardFragment == 3) {
                    selectRunning(GOOD_CONNECTIVITY);
                } else {
                    selectRunning(3);
                }
            case R.id.batteryProtectFunction /*2131361961*/:
                PasswordValid = false;
                if (CurrentDashboardFragment == 4) {
                    selectRunning(GOOD_CONNECTIVITY);
                } else {
                    selectRunning(4);
                }
            case R.id.cabinComfortEnableButton /*2131361972*/:
            case R.id.ccFragStopButton /*2131361989*/:
                Log.i(TAG, "-->cabinComfortEnableButton");
                if (aParam[23] != 0 || this.CabinComfortMode == BAD_CONNECTIVITY || ValidPassword()) {
                    setFunctionMode(GOOD_CONNECTIVITY, toggleFunctionMode(this.CabinComfortMode));
                    updateFunctionModes();
                    PasswordValid = false;
                }
            case R.id.coldWeatherGuardEnableButton /*2131361979*/:
            case R.id.cwgFragStopButton /*2131361996*/:
                Log.i(TAG, "-->coldWeatherGuardEnableButton");
                if (this.ColdWeatherGuardMode == BAD_CONNECTIVITY || ValidPassword()) {
                    setFunctionMode(BAD_CONNECTIVITY, toggleFunctionMode(this.ColdWeatherGuardMode));
                    updateFunctionModes();
                    PasswordValid = false;
                }
            case R.id.batteryProtectEnableButton /*2131361986*/:
            case R.id.bpFragStopButton /*2131362005*/:
                Log.i(TAG, "-->batteryProtectEnableButton");
                if (this.BatteryProtectMode == BAD_CONNECTIVITY || ValidPassword()) {
                    setFunctionMode(3, toggleFunctionMode(this.BatteryProtectMode));
                    updateFunctionModes();
                    PasswordValid = false;
                }
            case R.id.ccFragTargetTemperatureDecrButton /*2131361991*/:
                if (isCabinTempCommonDecrValid(aParam[3]) || ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 3);
                }
            case R.id.ccFragTargetTemperatureIncrButton /*2131361992*/:
                if (isCabinTempCommonIncrValid(aParam[3]) || ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 3);
                }
            case R.id.cwgFragMinTempDecrButton /*2131361998*/:
            case R.id.cwgFragMinTempIncrButton /*2131361999*/:
                if (ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 11);
                }
            case R.id.cwgFragIdealTempDecrButton /*2131362001*/:
            case R.id.cwgFragIdealTempIncrButton /*2131362002*/:
                if (ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 10);
                }
            case R.id.bpFragSetpointDecrButton /*2131362007*/:
            case R.id.bpFragSetpointIncrButton /*2131362008*/:
                if (ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 8);
                }
            case R.id.bpEngineRuntimeDecrButton /*2131362010*/:
            case R.id.bpEngineRuntimeIncrButton /*2131362011*/:
                if (ValidPassword()) {
                    updateFragmentParamValue(v.getId(), 9);
                }
            case R.id.settingsMenu11 /*2131362014*/:
                selectMenu1Entry(GOOD_CONNECTIVITY);
            case R.id.settingsMenu12 /*2131362017*/:
                selectMenu1Entry(BAD_CONNECTIVITY);
            case R.id.settingsMenu13 /*2131362020*/:
                selectMenu1Entry(3);
            case R.id.settingsMenu14 /*2131362023*/:
                selectMenu1Entry(4);
            case R.id.settingsMenu15 /*2131362026*/:
                selectMenu1Entry(5);
            case R.id.settingsMenu16 /*2131362029*/:
                selectMenu1Entry(6);
            case R.id.settingsMenu17 /*2131362032*/:
                selectMenu1Entry(7);
            case R.id.settingsMenu21 /*2131362039*/:
                selectMenu2Entry(GOOD_CONNECTIVITY);
            case R.id.settingsMenu22 /*2131362042*/:
                selectMenu2Entry(BAD_CONNECTIVITY);
            case R.id.settingsMenu23 /*2131362045*/:
                selectMenu2Entry(3);
            case R.id.settingsMenu24 /*2131362048*/:
                selectMenu2Entry(4);
            case R.id.settingsMenu25 /*2131362051*/:
                selectMenu2Entry(5);
            case R.id.settingsMenu26 /*2131362054*/:
                selectMenu2Entry(6);
            case R.id.settingsMenu27 /*2131362057*/:
                selectMenu2Entry(7);
            case R.id.settingsEntryEnableButton /*2131362065*/:
                if (PasswordEnable && ValidPassword()) {
                    updateParamValue(v.getId(), this.param_id);
                    saveParamValue(this.param_id);
                }
            case R.id.settingsEntryDisableButton /*2131362068*/:
                if (PasswordEnable && ValidPassword()) {
                    updateParamValue(v.getId(), this.param_id);
                    saveParamValue(this.param_id);
                }
            case R.id.settingsEntryDecrementButton /*2131362072*/:
                updateParamValue(v.getId(), this.param_id);
            case R.id.settingsEntryIncrementButton /*2131362073*/:
                updateParamValue(v.getId(), this.param_id);
            case R.id.settingsEntryDoneButton /*2131362074*/:
                saveParamValue(this.param_id);
            case R.id.packagemanagernagEscapeButton /*2131362083*/:
                packagemanagernag = false;
                findViewById(R.id.packagemanagerFragment).setVisibility(View.GONE);
            case R.id.packagemanagerButton /*2131362084*/:
                removeBloatware();
            case R.id.killswitchButton /*2131362086*/:
                selectKillswitchMode(BAD_CONNECTIVITY);
            case R.id.poweroffButton /*2131362088*/:
                this.accessoryControl.writeCommand(22, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
                selectKillswitchMode(3);
                findViewById(R.id.fullScreen).setVisibility(View.GONE);
                this.accessoryControl.writeCommand(21, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
            case R.id.verificationBeginVerificationButton /*2131362090*/:
                selectActivationFragment(BAD_CONNECTIVITY);
                findViewById(R.id.verificationFragment).setVisibility(View.GONE);
                findViewById(R.id.installFragment).setVisibility(View.VISIBLE);
                ValidActivationProcess = false;
                StartVerificationProcess();
            case R.id.VINCodeContinueButton /*2131362093*/:
                selectActivationFragment(GOOD_CONNECTIVITY);
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
        setEngineStatus(BuildConfig.FLAVOR);
        if (this.GatewayMode == GOOD_CONNECTIVITY) {
            setFunctionMode(GOOD_CONNECTIVITY, 3);
        } else if (this.GatewayMode == BAD_CONNECTIVITY) {
            setFunctionMode(BAD_CONNECTIVITY, 3);
        } else if (this.GatewayMode == 3) {
            setFunctionMode(3, 3);
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
        enableStatusBar(enableKioskMode);
        if (!gateway_connected && !demo_mode) {
            findViewById(R.id.dashboardFragment).setVisibility(View.GONE);
        } else if (enable) {
            findViewById(R.id.dashboardFragment).setVisibility(View.VISIBLE);
            selectSettingsMode(UNKNOWN_CONNECTIVITY);
        } else {
            findViewById(R.id.dashboardFragment).setVisibility(View.GONE);
        }
    }

    private void selectRunning(int fragment) {
        CurrentDashboardFragment = fragment;
        findViewById(R.id.runningFragment).setVisibility(View.GONE);
        findViewById(R.id.cabinComfortFragment).setVisibility(View.GONE);
        findViewById(R.id.coldWeatherGuardFragment).setVisibility(View.GONE);
        findViewById(R.id.batteryProtectFragment).setVisibility(View.GONE);
        updateFunctionModes();
        viewFragmentParamValue(GOOD_CONNECTIVITY);
        viewFragmentParamValue(BAD_CONNECTIVITY);
        viewFragmentParamValue(3);
        viewFragmentParamValue(4);
        if (fragment != 0) {
            enableDashboard(enableKioskMode);
            switch (fragment) {
                case GOOD_CONNECTIVITY /*1*/:
                    findViewById(R.id.runningFragment).setVisibility(View.VISIBLE);
                    break;
                case BAD_CONNECTIVITY /*2*/:
                    findViewById(R.id.cabinComfortFragment).setVisibility(View.VISIBLE);
                    break;
                case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                    findViewById(R.id.coldWeatherGuardFragment).setVisibility(View.VISIBLE);
                    break;
                case httpClient.PHONEHOME_APK_PENDING /*4*/:
                    findViewById(R.id.batteryProtectFragment).setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
        enableDashboard(false);
        PasswordValid = false;
    }

    private void selectKillswitchMode(int mode) {
        switch (mode) {
            case UNKNOWN_CONNECTIVITY /*0*/:
                findViewById(R.id.fullScreen).setVisibility(View.VISIBLE);
                findViewById(R.id.killswitchFragment).setVisibility(View.GONE);
                findViewById(R.id.poweroffFragment).setVisibility(View.GONE);
                findViewById(R.id.poweronFragment).setVisibility(View.GONE);
            case GOOD_CONNECTIVITY /*1*/:
                enableStatusBar(false);
                enableDashboard(false);
                enableSettings(false);
                findViewById(R.id.killswitchFragment).setVisibility(View.VISIBLE);
            case BAD_CONNECTIVITY /*2*/:
                enableStatusBar(false);
                enableDashboard(false);
                enableSettings(false);
                findViewById(R.id.killswitchFragment).setVisibility(View.GONE);
                findViewById(R.id.poweroffFragment).setVisibility(View.VISIBLE);
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                enableStatusBar(false);
                enableDashboard(false);
                enableSettings(false);
                findViewById(R.id.fullScreen).setVisibility(View.GONE);
                findViewById(R.id.poweroffFragment).setVisibility(View.GONE);
                findViewById(R.id.poweronFragment).setVisibility(View.VISIBLE);
            default:
        }
    }

    public void openPasswordDialog() {
        if (this.passwordDialog != null && this.passwordDialog.isShowing()) {
            this.passwordDialog.dismiss();
        }
        this.passwordDialog = new Dialog(this);
        this.passwordDialog.requestWindowFeature(GOOD_CONNECTIVITY);
        this.passwordDialog.setContentView(R.layout.password_dialog);
        this.passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ((TextView) this.passwordDialog.findViewById(R.id.passwordEditText)).setText(BuildConfig.FLAVOR);
        this.passwordDialog.findViewById(R.id.passwordReturnButton).setOnClickListener(new C00088());
        this.passwordDialog.findViewById(R.id.passwordContinueButton).setOnClickListener(new C00099());
        this.passwordDialog.show();
    }

    private boolean ValidPassword() {
        if (!PasswordEnable || PasswordValid) {
            return enableKioskMode;
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
        switch (this.CabinComfortMode) {
            case UNKNOWN_CONNECTIVITY /*0*/:
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
            case GOOD_CONNECTIVITY /*1*/:
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
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
            case BAD_CONNECTIVITY /*2*/:
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
        switch (this.ColdWeatherGuardMode) {
            case UNKNOWN_CONNECTIVITY /*0*/:
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
            case GOOD_CONNECTIVITY /*1*/:
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
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
            case BAD_CONNECTIVITY /*2*/:
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
        switch (this.BatteryProtectMode) {
            case UNKNOWN_CONNECTIVITY /*0*/:
                findViewById(R.id.batteryProtectControl).setBackground(getResources().getDrawable(R.color.disabledFunction));
                findViewById(R.id.batteryProtectFragment).setBackground(getResources().getDrawable(R.color.disabledFunction));
                findViewById(R.id.batteryProtectFunctionIndicator).setBackground(getResources().getDrawable(R.color.disabled));
                findViewById(R.id.batteryProtectIndicator).setBackground(getResources().getDrawable(R.drawable.disabled_indicator_shape));
                ((TextView) findViewById(R.id.batteryProtectStatus)).setText("DISABLED");
                bpEnableButton.setBackground(getResources().getDrawable(R.drawable.disabled_button_shape));
                bpEnableButton.setText("ENABLE");
                bpFragStopButton.setBackground(getResources().getDrawable(R.drawable.disabled_button_shape));
                bpFragStopButton.setText("ENABLE");
            case GOOD_CONNECTIVITY /*1*/:
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                findViewById(R.id.batteryProtectControl).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.batteryProtectFragment).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.batteryProtectFunctionIndicator).setBackground(getResources().getDrawable(R.color.enabled));
                findViewById(R.id.batteryProtectIndicator).setBackground(getResources().getDrawable(R.drawable.enabled_indicator_shape));
                ((TextView) findViewById(R.id.batteryProtectStatus)).setText("MONITORING");
                bpEnableButton.setBackground(getResources().getDrawable(R.drawable.enabled_button_shape));
                bpEnableButton.setText("DISABLE");
                bpFragStopButton.setBackground(getResources().getDrawable(R.drawable.enabled_button_shape));
                bpFragStopButton.setText("DISABLE");
            case BAD_CONNECTIVITY /*2*/:
                findViewById(R.id.batteryProtectControl).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.batteryProtectFragment).setBackground(getResources().getDrawable(R.color.enabledFunction));
                findViewById(R.id.batteryProtectFunctionIndicator).setBackground(getResources().getDrawable(R.color.active));
                findViewById(R.id.batteryProtectIndicator).setBackground(getResources().getDrawable(R.drawable.active_indicator_shape));
                ((TextView) findViewById(R.id.batteryProtectStatus)).setText("RUNNING ENGINE");
                bpEnableButton.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
                bpEnableButton.setText("STOP ENGINE");
                bpFragStopButton.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
                bpFragStopButton.setText("STOP ENGINE");
            default:
        }
    }

    private void setFunctionMode(int function, int mode) {
        int i = GOOD_CONNECTIVITY;
        int[] iArr;
        switch (function) {
            case GOOD_CONNECTIVITY /*1*/:
                if (this.GatewayMode != GOOD_CONNECTIVITY) {
                    this.CabinComfortMode = mode;
                } else if (mode == 3) {
                    this.accessoryControl.writeCommand(29, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
                    this.CabinComfortMode = 3;
                }
                iArr = aParam;
                if (this.CabinComfortMode == 0) {
                    i = UNKNOWN_CONNECTIVITY;
                }
                iArr[UNKNOWN_CONNECTIVITY] = i;
                this.accessoryControl.writeCommand(40, UNKNOWN_CONNECTIVITY, aParam[UNKNOWN_CONNECTIVITY] & 255);
                break;
            case BAD_CONNECTIVITY /*2*/:
                int i2;
                if (this.GatewayMode != BAD_CONNECTIVITY) {
                    this.ColdWeatherGuardMode = mode;
                } else if (mode == 3) {
                    this.accessoryControl.writeCommand(29, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
                    this.ColdWeatherGuardMode = 3;
                }
                int[] iArr2 = aParam;
                if (this.ColdWeatherGuardMode == 0) {
                    i2 = UNKNOWN_CONNECTIVITY;
                } else {
                    i2 = GOOD_CONNECTIVITY;
                }
                iArr2[GOOD_CONNECTIVITY] = i2;
                this.accessoryControl.writeCommand(55, UNKNOWN_CONNECTIVITY, aParam[GOOD_CONNECTIVITY] & 255);
                break;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                if (this.GatewayMode != 3) {
                    this.BatteryProtectMode = mode;
                } else if (mode == 3) {
                    this.accessoryControl.writeCommand(29, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
                    this.BatteryProtectMode = 3;
                }
                iArr = aParam;
                if (this.BatteryProtectMode == 0) {
                    i = UNKNOWN_CONNECTIVITY;
                }
                iArr[BAD_CONNECTIVITY] = i;
                this.accessoryControl.writeCommand(50, UNKNOWN_CONNECTIVITY, aParam[BAD_CONNECTIVITY] & 255);
                break;
        }
        updateFunctionModes();
    }

    private int toggleFunctionMode(int mode) {
        switch (mode) {
            case UNKNOWN_CONNECTIVITY /*0*/:
                return GOOD_CONNECTIVITY;
            case GOOD_CONNECTIVITY /*1*/:
                return UNKNOWN_CONNECTIVITY;
            case BAD_CONNECTIVITY /*2*/:
                return 3;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                return GOOD_CONNECTIVITY;
            default:
                return UNKNOWN_CONNECTIVITY;
        }
    }

    private void useGatewayMode(int newmode) {
        switch (this.GatewayMode) {
            case GOOD_CONNECTIVITY /*1*/:
                this.CabinComfortMode = GOOD_CONNECTIVITY;
                break;
            case BAD_CONNECTIVITY /*2*/:
                this.ColdWeatherGuardMode = GOOD_CONNECTIVITY;
                break;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                this.BatteryProtectMode = GOOD_CONNECTIVITY;
                break;
        }
        switch (newmode) {
            case UNKNOWN_CONNECTIVITY /*0*/:
                Log.i(TAG, "     change mode to Idle");
                this.GatewayMode = UNKNOWN_CONNECTIVITY;
                break;
            case GOOD_CONNECTIVITY /*1*/:
                Log.i(TAG, "     change mode to CabinComfortMode");
                this.GatewayMode = GOOD_CONNECTIVITY;
                this.CabinComfortMode = BAD_CONNECTIVITY;
                break;
            case BAD_CONNECTIVITY /*2*/:
                Log.i(TAG, "     change mode to ColdWeatherGuardMode");
                this.GatewayMode = BAD_CONNECTIVITY;
                this.ColdWeatherGuardMode = BAD_CONNECTIVITY;
                break;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                Log.i(TAG, "     change mode to BatteryProtectMode");
                this.GatewayMode = 3;
                this.BatteryProtectMode = BAD_CONNECTIVITY;
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
            selectSettingsEntry(UNKNOWN_CONNECTIVITY);
        } else {
            enableStatusBar(false);
            enableDashboard(false);
            selectActivationFragment(UNKNOWN_CONNECTIVITY);
            enableSettings(enableKioskMode);
        }
        switch (level) {
            case GOOD_CONNECTIVITY /*1*/:
                initMenu1();
                findViewById(R.id.settingsMenu1).setVisibility(View.VISIBLE);
                findViewById(R.id.settingsMenu2).setVisibility(View.GONE);
                selectSettingsEntry(UNKNOWN_CONNECTIVITY);
                this.settings_menu1_index = UNKNOWN_CONNECTIVITY;
                selectMenu1Entry(UNKNOWN_CONNECTIVITY);
            case BAD_CONNECTIVITY /*2*/:
                findViewById(R.id.settingsMenu1).setVisibility(View.VISIBLE);
                findViewById(R.id.settingsMenu2).setVisibility(View.VISIBLE);
                initMenu2();
                selectSettingsEntry(UNKNOWN_CONNECTIVITY);
                this.settings_menu2_index = UNKNOWN_CONNECTIVITY;
                selectMenu2Entry(UNKNOWN_CONNECTIVITY);
                this.settings_entrytype = UNKNOWN_CONNECTIVITY;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                findViewById(R.id.settingsMenu1).setVisibility(View.VISIBLE);
                findViewById(R.id.settingsMenu2).setVisibility(View.VISIBLE);
                viewParamValue();
            default:
                findViewById(R.id.settingsMenu1).setVisibility(View.GONE);
                findViewById(R.id.settingsMenu2).setVisibility(View.GONE);
                selectSettingsEntry(UNKNOWN_CONNECTIVITY);
        }
    }

    private void initMenu1() {
        findViewById(R.id.settingsMenu11).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsMenu11Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        ((TextView) findViewById(R.id.settingsMenu11Item)).setText(this.menus.aMainMenu[UNKNOWN_CONNECTIVITY]);
        findViewById(R.id.settingsMenu12).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsMenu12Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        ((TextView) findViewById(R.id.settingsMenu12Item)).setText(this.menus.aMainMenu[GOOD_CONNECTIVITY]);
        findViewById(R.id.settingsMenu13).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsMenu13Indicator).setBackground(getResources().getDrawable(R.color.menu1Deselected));
        ((TextView) findViewById(R.id.settingsMenu13Item)).setText(this.menus.aMainMenu[BAD_CONNECTIVITY]);
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
        if (Menus.getSubmenuId(this.settings_menu1_index, GOOD_CONNECTIVITY) >= 0) {
            findViewById(R.id.settingsMenu21).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.settingsMenu21Item)).setText(this.menus.getSubmenuName(this.settings_menu1_index, GOOD_CONNECTIVITY));
        } else {
            findViewById(R.id.settingsMenu21).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.settingsMenu22Indicator).setBackground(getResources().getDrawable(R.color.menu2Deselected));
        if (Menus.getSubmenuId(this.settings_menu1_index, BAD_CONNECTIVITY) >= 0) {
            findViewById(R.id.settingsMenu22).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.settingsMenu22Item)).setText(this.menus.getSubmenuName(this.settings_menu1_index, BAD_CONNECTIVITY));
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
            case GOOD_CONNECTIVITY /*1*/:
                findViewById(R.id.settingsMenu11).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu11Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case BAD_CONNECTIVITY /*2*/:
                findViewById(R.id.settingsMenu12).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu12Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                findViewById(R.id.settingsMenu13).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu13Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case httpClient.PHONEHOME_APK_PENDING /*4*/:
                findViewById(R.id.settingsMenu14).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu14Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case httpClient.PHONEHOME_NONE /*5*/:
                findViewById(R.id.settingsMenu15).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu15Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case Params.PasswordType /*6*/:
                findViewById(R.id.settingsMenu16).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu16Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case Params.ProcessType /*7*/:
                findViewById(R.id.settingsMenu17).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu17Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            case Params.PARAM_VoltageSetPoint /*8*/:
                findViewById(R.id.settingsMenu18).setBackground(getResources().getDrawable(R.color.menu1Selected));
                findViewById(R.id.settingsMenu18Indicator).setBackground(getResources().getDrawable(R.color.menu1Active));
                break;
            default:
                return;
        }
        selectSettingsMode(BAD_CONNECTIVITY);
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
            case GOOD_CONNECTIVITY /*1*/:
                findViewById(R.id.settingsMenu21).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu21Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case BAD_CONNECTIVITY /*2*/:
                findViewById(R.id.settingsMenu22).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu22Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                findViewById(R.id.settingsMenu23).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu23Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case httpClient.PHONEHOME_APK_PENDING /*4*/:
                findViewById(R.id.settingsMenu24).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu24Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case httpClient.PHONEHOME_NONE /*5*/:
                findViewById(R.id.settingsMenu25).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu25Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case Params.PasswordType /*6*/:
                findViewById(R.id.settingsMenu26).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu26Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case Params.ProcessType /*7*/:
                findViewById(R.id.settingsMenu27).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu27Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            case Params.PARAM_VoltageSetPoint /*8*/:
                findViewById(R.id.settingsMenu28).setBackground(getResources().getDrawable(R.color.menu2Selected));
                findViewById(R.id.settingsMenu28Indicator).setBackground(getResources().getDrawable(R.color.menu2Active));
                break;
            default:
                return;
        }
        selectSettingsMode(3);
    }

    private void selectSettingsEntry(int datatype) {
        this.settings_entrytype = datatype;
        findViewById(R.id.settingsEntryEnable).setVisibility(View.GONE);
        findViewById(R.id.settingsEntryDisable).setVisibility(View.GONE);
        findViewById(R.id.settingsEntryNumeric).setVisibility(View.GONE);
        findViewById(R.id.settingsEntryPassword).setVisibility(View.GONE);
        findViewById(R.id.settingsEntryRefresh).setVisibility(View.GONE);
        switch (datatype) {
            case GOOD_CONNECTIVITY /*1*/:
                findViewById(R.id.settingsEntryEnable).setVisibility(View.VISIBLE);
                break;
            case BAD_CONNECTIVITY /*2*/:
                findViewById(R.id.settingsEntryDisable).setVisibility(View.VISIBLE);
                break;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                findViewById(R.id.settingsEntryNumeric).setVisibility(View.VISIBLE);
                break;
            case httpClient.PHONEHOME_APK_PENDING /*4*/:
                findViewById(R.id.settingsEntryPassword).setVisibility(View.VISIBLE);
                break;
            case httpClient.PHONEHOME_NONE /*5*/:
                findViewById(R.id.settingsEntryRefresh).setVisibility(View.VISIBLE);
                break;
        }
        PasswordValid = false;
    }

    public void initializeRunningParams() {
        for (int i = UNKNOWN_CONNECTIVITY; i < 25; i += GOOD_CONNECTIVITY) {
            aParam[i] = this.params.aParamDef[i];
        }
    }

    private void viewParamValue() {
        selectSettingsEntry(GOOD_CONNECTIVITY);
        this.param_id = Menus.getSubmenuId(this.settings_menu1_index, this.settings_menu2_index);
        pValue = aParam[this.param_id];
        String pName = this.params.aParamName[this.param_id];
        String pPfx = this.params.aParamPfx[this.param_id];
        String pSfx = this.params.aParamSfx[this.param_id];
        if (this.param_id >= 0) {
            switch (this.params.aParamType[this.param_id]) {
                case GOOD_CONNECTIVITY /*1*/:
                    if (aParam[this.param_id] != 0) {
                        ((TextView) findViewById(R.id.settingsEntryDisableDescription)).setText(pName + " feature is currently Enabled.");
                        selectSettingsEntry(BAD_CONNECTIVITY);
                        return;
                    }
                    ((TextView) findViewById(R.id.settingsEntryEnableDescription)).setText(pName + " feature is currently Disabled.");
                    selectSettingsEntry(GOOD_CONNECTIVITY);
                    return;
                case BAD_CONNECTIVITY /*2*/:
                case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                    ((TextView) findViewById(R.id.settingsEntryDescription)).setText(pName);
                    ((TextView) findViewById(R.id.settingsEntryValue)).setText(pPfx + Integer.toString(aParam[this.param_id]) + pSfx);
                    selectSettingsEntry(3);
                    return;
                case httpClient.PHONEHOME_APK_PENDING /*4*/:
                    ((TextView) findViewById(R.id.settingsEntryDescription)).setText(pName);
                    String str = Integer.toString(aParam[this.param_id]);
                    ((TextView) findViewById(R.id.settingsEntryValue)).setText(str.substring(UNKNOWN_CONNECTIVITY, str.length() - 1) + "." + str.substring(str.length() - 1) + pSfx);
                    selectSettingsEntry(3);
                    return;
                case Params.ProcessType /*7*/:
                    if (this.param_id == 21) {
                        selectSettingsEntry(5);
                        httpclient.PhoneHome(UNKNOWN_CONNECTIVITY, false);
                    } else if (this.param_id == 22) {
                        openSerialDialog();
                    }
                    selectSettingsMode(UNKNOWN_CONNECTIVITY);
                    selectRunning(GOOD_CONNECTIVITY);
                    return;
                default:
                    selectSettingsMode(UNKNOWN_CONNECTIVITY);
                    selectRunning(GOOD_CONNECTIVITY);
                    return;
            }
        }
        selectSettingsMode(UNKNOWN_CONNECTIVITY);
        selectRunning(GOOD_CONNECTIVITY);
    }

    private void updateParamValue(int vId, int pId) {
        String pSfx = this.params.aParamSfx[this.param_id];
        String pPfx = this.params.aParamPfx[this.param_id];
        boolean bypass = false;
        if (pId == 0 && aParam[23] == GOOD_CONNECTIVITY) {
            bypass = enableKioskMode;
        }
        if (pId == 3) {
            if (vId == R.id.settingsEntryIncrementButton && isCabinTempCommonIncrValid(pValue)) {
                bypass = enableKioskMode;
            }
            if (vId == R.id.settingsEntryDecrementButton && isCabinTempCommonDecrValid(pValue)) {
                bypass = enableKioskMode;
            }
        }
        if (bypass || ValidPassword()) {
            String str;
            switch (vId) {
                case R.id.settingsEntryEnableButton /*2131362065*/:
                    if (this.params.aParamType[pId] == GOOD_CONNECTIVITY) {
                        pValue = GOOD_CONNECTIVITY;
                    }
                case R.id.settingsEntryDisableButton /*2131362068*/:
                    if (this.params.aParamType[pId] == GOOD_CONNECTIVITY) {
                        pValue = UNKNOWN_CONNECTIVITY;
                    }
                case R.id.settingsEntryDecrementButton /*2131362072*/:
                    decrValue(pId);
                    if (pId == 18 && pValue < 4) {
                        pValue = UNKNOWN_CONNECTIVITY;
                    }
                    switch (this.params.aParamType[pId]) {
                        case BAD_CONNECTIVITY /*2*/:
                        case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                            ((TextView) findViewById(R.id.settingsEntryValue)).setText(pPfx + Integer.toString(pValue) + pSfx);
                        case httpClient.PHONEHOME_APK_PENDING /*4*/:
                            str = Integer.toString(pValue);
                            ((TextView) findViewById(R.id.settingsEntryValue)).setText(str.substring(UNKNOWN_CONNECTIVITY, str.length() - 1) + "." + str.substring(str.length() - 1) + pSfx);
                        default:
                    }
                case R.id.settingsEntryIncrementButton /*2131362073*/:
                    incrValue(pId);
                    if (pId == 18 && pValue < 4) {
                        pValue = 4;
                    }
                    switch (this.params.aParamType[pId]) {
                        case BAD_CONNECTIVITY /*2*/:
                        case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                            ((TextView) findViewById(R.id.settingsEntryValue)).setText(Integer.toString(pValue) + pSfx);
                        case httpClient.PHONEHOME_APK_PENDING /*4*/:
                            str = Integer.toString(pValue);
                            ((TextView) findViewById(R.id.settingsEntryValue)).setText(str.substring(UNKNOWN_CONNECTIVITY, str.length() - 1) + "." + str.substring(str.length() - 1) + pSfx);
                        default:
                    }
                default:
            }
        }
    }

    private boolean isCabinTempCommonIncrValid(int value) {
        if (this.params.aParamIncr[3] + value <= aParam[24] + aParam[17]) {
            return enableKioskMode;
        }
        return false;
    }

    private boolean isCabinTempCommonDecrValid(int value) {
        if (value - this.params.aParamIncr[3] >= aParam[24] - aParam[17]) {
            return enableKioskMode;
        }
        return false;
    }

    private void incrValue(int paramId) {
        pValue += this.params.aParamIncr[paramId];
        if (pValue > this.params.aParamMax[paramId]) {
            pValue = this.params.aParamMax[paramId];
        }
    }

    private void decrValue(int paramId) {
        pValue -= this.params.aParamIncr[paramId];
        if (pValue < this.params.aParamMin[paramId]) {
            pValue = this.params.aParamMin[paramId];
        }
    }

    public void SaveDownloadedParamValue(int paramId, int value) {
        pValue = value;
        saveParamValue(paramId);
    }

    private void saveParamValue(int paramId) {
        switch (this.params.aParamType[paramId]) {
            case GOOD_CONNECTIVITY /*1*/:
                aParam[paramId] = pValue;
                boolean z;
                switch (paramId) {
                    case UNKNOWN_CONNECTIVITY /*0*/:
                        if (aParam[paramId] != 0) {
                            setFunctionMode(GOOD_CONNECTIVITY, GOOD_CONNECTIVITY);
                        } else {
                            setFunctionMode(GOOD_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
                        }
                        updateFunctionModes();
                        break;
                    case GOOD_CONNECTIVITY /*1*/:
                        if (aParam[paramId] != 0) {
                            setFunctionMode(BAD_CONNECTIVITY, GOOD_CONNECTIVITY);
                        } else {
                            setFunctionMode(BAD_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
                        }
                        updateFunctionModes();
                        break;
                    case BAD_CONNECTIVITY /*2*/:
                        if (aParam[paramId] != 0) {
                            setFunctionMode(3, GOOD_CONNECTIVITY);
                        } else {
                            setFunctionMode(3, UNKNOWN_CONNECTIVITY);
                        }
                        updateFunctionModes();
                        break;
                    case Params.PARAM_AudibleSound /*15*/:
                        if (pValue != 0) {
                            z = enableKioskMode;
                        } else {
                            z = false;
                        }
                        setSoundOn(z);
                        sendParam(paramId);
                        break;
                    case Params.PARAM_PasswordEnable /*19*/:
                        if (pValue != 0) {
                            z = enableKioskMode;
                        } else {
                            z = false;
                        }
                        PasswordEnable = z;
                        sendParam(paramId);
                        break;
                    case Params.PARAM_Password /*20*/:
                        Password = pValue;
                        sendParam(paramId);
                        break;
                    default:
                        sendParam(paramId);
                        break;
                }
            case BAD_CONNECTIVITY /*2*/:
                aParam[paramId] = pValue;
                switch (paramId) {
                    case Params.PARAM_DimTabletScreen /*14*/:
                        setScreenTimeout(pValue);
                        sendParam(paramId);
                        break;
                    default:
                        sendParam(paramId);
                        break;
                }
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
            case httpClient.PHONEHOME_APK_PENDING /*4*/:
                aParam[paramId] = pValue;
                sendParam(paramId);
                break;
        }
        selectSettingsMode(UNKNOWN_CONNECTIVITY);
        selectRunning(GOOD_CONNECTIVITY);
    }

    private void sendParam(int paramId) {
        byte[] data = new byte[BAD_CONNECTIVITY];
        int api = this.params.aParamAPIcmd[paramId];
        if (api != 0) {
            data[UNKNOWN_CONNECTIVITY] = (byte) ((aParam[paramId] >> 8) & 255);
            data[GOOD_CONNECTIVITY] = (byte) (aParam[paramId] & 255);
            this.accessoryControl.writeCommand(api, data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
        }
    }

    private void viewFragmentParamValue(int fragmentId) {
        switch (fragmentId) {
            case BAD_CONNECTIVITY /*2*/:
                ((TextView) findViewById(R.id.ccFragTargetTemperatureValue)).setText(Integer.toString(aParam[3]) + this.params.aParamSfx[3]);
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                ((TextView) findViewById(R.id.cwgFragMinTempValue)).setText(Integer.toString(aParam[11]) + this.params.aParamSfx[11]);
                ((TextView) findViewById(R.id.cwgFragIdealTempValue)).setText(Integer.toString(aParam[10]) + this.params.aParamSfx[10]);
            case httpClient.PHONEHOME_APK_PENDING /*4*/:
                ((TextView) findViewById(R.id.bpFragTimeRemainingValue)).setText(Time2MinsSecsStr(aParam[9] * 60));
                String str = Integer.toString(aParam[8]);
                ((TextView) findViewById(R.id.bpFragSetpointValue)).setText(str.substring(UNKNOWN_CONNECTIVITY, str.length() - 1) + "." + str.substring(str.length() - 1) + this.params.aParamSfx[8]);
                ((TextView) findViewById(R.id.bpEngineRuntimeValue)).setText(Integer.toString(aParam[9]) + this.params.aParamSfx[9]);
            default:
        }
    }

    private String Time2MinsSecsStr(int time) {
        int mins = time / 60;
        int secs = time - (mins * 60);
        if (secs < 10) {
            return Integer.toString(mins) + ":0" + Integer.toString(secs);
        }
        return Integer.toString(mins) + ":" + Integer.toString(secs);
    }

    private void updateFragmentParamValue(int vId, int pId) {
        byte[] data = new byte[BAD_CONNECTIVITY];
        switch (vId) {
            case R.id.ccFragTargetTemperatureDecrButton /*2131361991*/:
                decrParam(pId);
                viewFragmentParamValue(BAD_CONNECTIVITY);
                break;
            case R.id.ccFragTargetTemperatureIncrButton /*2131361992*/:
                incrParam(pId);
                viewFragmentParamValue(BAD_CONNECTIVITY);
                break;
            case R.id.cwgFragMinTempDecrButton /*2131361998*/:
                decrParam(pId);
                viewFragmentParamValue(3);
                break;
            case R.id.cwgFragMinTempIncrButton /*2131361999*/:
                incrParam(pId);
                viewFragmentParamValue(3);
                break;
            case R.id.cwgFragIdealTempDecrButton /*2131362001*/:
                decrParam(pId);
                viewFragmentParamValue(3);
                break;
            case R.id.cwgFragIdealTempIncrButton /*2131362002*/:
                incrParam(pId);
                viewFragmentParamValue(3);
                break;
            case R.id.bpFragSetpointDecrButton /*2131362007*/:
                decrParam(pId);
                viewFragmentParamValue(4);
                break;
            case R.id.bpFragSetpointIncrButton /*2131362008*/:
                incrParam(pId);
                viewFragmentParamValue(4);
                break;
            case R.id.bpEngineRuntimeDecrButton /*2131362010*/:
                decrParam(pId);
                viewFragmentParamValue(4);
                break;
            case R.id.bpEngineRuntimeIncrButton /*2131362011*/:
                incrParam(pId);
                viewFragmentParamValue(4);
                break;
        }
        data[UNKNOWN_CONNECTIVITY] = (byte) ((aParam[pId] >> 8) & 255);
        data[GOOD_CONNECTIVITY] = (byte) (aParam[pId] & 255);
        this.accessoryControl.writeCommand(this.params.aParamAPIcmd[pId], data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
    }

    private void incrParam(int paramId) {
        int[] iArr = aParam;
        iArr[paramId] = iArr[paramId] + this.params.aParamIncr[paramId];
        if (aParam[paramId] > this.params.aParamMax[paramId]) {
            aParam[paramId] = this.params.aParamMax[paramId];
        }
    }

    private void decrParam(int paramId) {
        int[] iArr = aParam;
        iArr[paramId] = iArr[paramId] - this.params.aParamIncr[paramId];
        if (aParam[paramId] < this.params.aParamMin[paramId]) {
            aParam[paramId] = this.params.aParamMin[paramId];
        }
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
                case GOOD_CONNECTIVITY /*1*/:
                    findViewById(R.id.verificationFragment).setVisibility(View.VISIBLE);
                case BAD_CONNECTIVITY /*2*/:
                    findViewById(R.id.installFragment).setVisibility(View.VISIBLE);
                    StartVerificationProcess();
                case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                    findViewById(R.id.activationFragment).setVisibility(View.VISIBLE);
                case httpClient.PHONEHOME_APK_PENDING /*4*/:
                    activationcodeArea = (EditText) findViewById(R.id.activationCodeEditText);
                    activationcodeArea.setText(BuildConfig.FLAVOR);
                    activationcodeArea.setOnEditorActionListener(this.mEditorActionListener);
                case httpClient.PHONEHOME_NONE /*5*/:
                    activationcodeArea = (EditText) findViewById(R.id.VINCodeEditText);
                    activationcodeArea.setText(BuildConfig.FLAVOR);
                    activationcodeArea.setOnEditorActionListener(this.mEditorActionListener);
                default:
            }
        }
    }

    private void StartVerificationProcess() {
        this.activation_step = UNKNOWN_CONNECTIVITY;
        ValidActivationProcess = false;
        ActivationProcessPending = enableKioskMode;
        this.verificationHandler.removeCallbacks(this.verificationRunnable);
        this.verificationHandler.postDelayed(this.verificationRunnable, 100);
    }

    private void nextVerificationStep() {
        int i = this.activation_step;
        this.activation_step = i + GOOD_CONNECTIVITY;
        Button doneButton;
        switch (i) {
            case UNKNOWN_CONNECTIVITY /*0*/:
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
            case GOOD_CONNECTIVITY /*1*/:
                findViewById(R.id.installDetail1Progress).setVisibility(View.VISIBLE);
                if (test_mode) {
                    this.verificationHandler.postDelayed(this.verificationRunnable, 500);
                } else {
                    this.verificationHandler.postDelayed(this.verificationRunnable, 5000);
                }
            case BAD_CONNECTIVITY /*2*/:
                findViewById(R.id.installDetail1Progress).setVisibility(View.INVISIBLE);
                ((CheckBox) findViewById(R.id.installDetail1CheckBox)).setChecked(enableKioskMode);
                findViewById(R.id.installDetail2Progress).setVisibility(View.VISIBLE);
                if (test_mode) {
                    this.verificationHandler.postDelayed(this.verificationRunnable, 500);
                } else {
                    this.verificationHandler.postDelayed(this.verificationRunnable, 2000);
                }
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                findViewById(R.id.installDetail2Progress).setVisibility(View.INVISIBLE);
                ((CheckBox) findViewById(R.id.installDetail2CheckBox)).setChecked(enableKioskMode);
                findViewById(R.id.installDetail3Progress).setVisibility(View.VISIBLE);
                if (test_mode) {
                    this.verificationHandler.postDelayed(this.verificationRunnable, 500);
                    return;
                }
                this.accessoryControl.writeCommand(49, UNKNOWN_CONNECTIVITY, GOOD_CONNECTIVITY);
                Log.i(TAG, "APICMD_GET_VEHICLE_INFO = 1");
                this.verificationHandler.postDelayed(this.verificationRunnable, 15000);
            case httpClient.PHONEHOME_APK_PENDING /*4*/:
                findViewById(R.id.installDetail3Progress).setVisibility(View.INVISIBLE);
                ((CheckBox) findViewById(R.id.installDetail3CheckBox)).setChecked(enableKioskMode);
                findViewById(R.id.installDetail4Progress).setVisibility(View.VISIBLE);
                this.verificationHandler.postDelayed(this.verificationRunnable, 500);
            case httpClient.PHONEHOME_NONE /*5*/:
                findViewById(R.id.installDetail4Progress).setVisibility(View.INVISIBLE);
                ((CheckBox) findViewById(R.id.installDetail4CheckBox)).setChecked(enableKioskMode);
                this.activation_step = UNKNOWN_CONNECTIVITY;
                doneButton = (Button) findViewById(R.id.installDoneButton);
                if (Gateway_VIN.isEmpty()) {
                    doneButton.setText("ERROR! No VIN number");
                } else if (ActivationCode < 1000) {
                    doneButton.setText("ERROR! Invalid Activation Code");
                } else {
                    doneButton.setText("DONE");
                    ValidActivationProcess = enableKioskMode;
                }
                doneButton.setBackground(getResources().getDrawable(R.drawable.enabled_button_shape));
                doneButton.setEnabled(enableKioskMode);
                this.verificationHandler.removeCallbacks(this.verificationRunnable);
                ActivationProcessPending = false;
            default:
        }
    }

    public void openAlertDialog(int faultId) {
        if (faultId != 0 && faultId <= 23) {
            if (this.alertDialog != null && this.alertDialog.isShowing()) {
                this.alertDialog.dismiss();
                this.accessoryControl.writeCommand(33, UNKNOWN_CONNECTIVITY, faultId);
            }
            this.alertDialog = new Dialog(this);
            this.alertDialog.requestWindowFeature(GOOD_CONNECTIVITY);
            this.alertDialog.setContentView(R.layout.alert_dialog);
            HasFocus = enableKioskMode;
            ((TextView) this.alertDialog.findViewById(R.id.alertName)).setText(this.faults.aFaultMessage[faultId]);
            ((TextView) this.alertDialog.findViewById(R.id.alertDescription)).setText(this.faults.aFaultDesc[faultId]);
            this.alertDialog.findViewById(R.id.alertRefreshButton).setOnClickListener(new AnonymousClass11(faultId));
            this.alertDialog.show();
            wakeup();
            if (aParam[15] != 0) {
                alertTone();
            }
            back2sleep();
        }
    }

    public void closeAlertDialog(int faultId) {
        if (faultId == 0) {
            closeAllAlertDialogs();
        } else if (this.alertDialog != null) {
            if (this.alertDialog.isShowing()) {
                this.alertDialog.dismiss();
            }
            back2sleep();
        }
    }

    public void closeAllAlertDialogs() {
        if (this.alertDialog != null) {
            if (this.alertDialog.isShowing()) {
                this.alertDialog.dismiss();
            }
            back2sleep();
        }
    }

    private void alertTone() {
        if (aParam[15] != 0) {
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
        if (this.serialDialog != null && this.serialDialog.isShowing()) {
            this.serialDialog.dismiss();
        }
        this.serialDialog = new Dialog(this);
        this.serialDialog.requestWindowFeature(GOOD_CONNECTIVITY);
        this.serialDialog.setContentView(R.layout.serial_dialog);
        this.serialDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ((TextView) this.serialDialog.findViewById(R.id.serialVIN_Text)).setText(Gateway_VIN);
        ((TextView) this.serialDialog.findViewById(R.id.serialGWSerialID_Text)).setText(Gateway_SerialID);
        ((TextView) this.serialDialog.findViewById(R.id.serialLDRversion_Text)).setText(Gateway_LDRversion);
        ((TextView) this.serialDialog.findViewById(R.id.serialGWversion_Text)).setText(Gateway_FWversion);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), UNKNOWN_CONNECTIVITY);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo != null) {
            ((TextView) this.serialDialog.findViewById(R.id.serialAndroidVersion_Text)).setText(pInfo.versionName);
        }
        this.serialDialog.findViewById(R.id.serialDoneButton).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.serialDialog.dismiss();
            }
        });
        this.serialDialog.show();
    }

    public void openMaintDialog() {
        if (this.maintDialog != null && this.maintDialog.isShowing()) {
            this.maintDialog.dismiss();
        }
        this.kiosk_mode_counter = UNKNOWN_CONNECTIVITY;
        this.maintDialog = new Dialog(this);
        this.maintDialog.requestWindowFeature(GOOD_CONNECTIVITY);
        this.maintDialog.setContentView(R.layout.maint_dialog);
        this.maintDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_1)).setChecked(aMaintEnable[UNKNOWN_CONNECTIVITY]);
        ((EditText) this.maintDialog.findViewById(R.id.maintText_1)).setText(Integer.toString(aMaintValue[UNKNOWN_CONNECTIVITY]));
        ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_2)).setChecked(aMaintEnable[GOOD_CONNECTIVITY]);
        ((EditText) this.maintDialog.findViewById(R.id.maintText_2)).setText(Integer.toString(aMaintValue[GOOD_CONNECTIVITY]));
        ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_3)).setChecked(aMaintEnable[BAD_CONNECTIVITY]);
        ((EditText) this.maintDialog.findViewById(R.id.maintText_3)).setText(Integer.toString(aMaintValue[BAD_CONNECTIVITY]));
        ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_4)).setChecked(aMaintEnable[3]);
        ((EditText) this.maintDialog.findViewById(R.id.maintText_4)).setText(Integer.toString(aMaintValue[3]));
        ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_5)).setChecked(aMaintEnable[4]);
        ((EditText) this.maintDialog.findViewById(R.id.maintText_5)).setText(Integer.toString(aMaintValue[4]));
        ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_6)).setChecked(aMaintEnable[5]);
        ((EditText) this.maintDialog.findViewById(R.id.maintText_6)).setText(Integer.toString(aMaintValue[5]));
        ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_7)).setChecked(aMaintEnable[6]);
        ((EditText) this.maintDialog.findViewById(R.id.maintText_7)).setText(Integer.toString(aMaintValue[6]));
        ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_8)).setChecked(aMaintEnable[7]);
        ((EditText) this.maintDialog.findViewById(R.id.maintText_8)).setText(APIroute);
        ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_9)).setChecked(aMaintEnable[8]);
        ((EditText) this.maintDialog.findViewById(R.id.maintText_9)).setText(Integer.toString(aMaintValue[8]));
        ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_10)).setChecked(aMaintEnable[9]);
        ((EditText) this.maintDialog.findViewById(R.id.maintText_10)).setText(Integer.toString(aMaintValue[9]));
        this.maintDialog.findViewById(R.id.maintDoneButton).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.aMaintEnable[MainActivity.UNKNOWN_CONNECTIVITY] = ((CheckBox) MainActivity.this.maintDialog.findViewById(R.id.maintCheckBox_1)).isChecked();
                MainActivity.aMaintValue[MainActivity.UNKNOWN_CONNECTIVITY] = MainActivity.this.toInteger(((EditText) MainActivity.this.maintDialog.findViewById(R.id.maintText_1)).getText().toString());
                MainActivity.aMaintEnable[MainActivity.GOOD_CONNECTIVITY] = ((CheckBox) MainActivity.this.maintDialog.findViewById(R.id.maintCheckBox_2)).isChecked();
                MainActivity.aMaintValue[MainActivity.GOOD_CONNECTIVITY] = MainActivity.this.toInteger(((EditText) MainActivity.this.maintDialog.findViewById(R.id.maintText_2)).getText().toString());
                MainActivity.aMaintEnable[MainActivity.BAD_CONNECTIVITY] = ((CheckBox) MainActivity.this.maintDialog.findViewById(R.id.maintCheckBox_3)).isChecked();
                MainActivity.aMaintValue[MainActivity.BAD_CONNECTIVITY] = MainActivity.this.toInteger(((EditText) MainActivity.this.maintDialog.findViewById(R.id.maintText_3)).getText().toString());
                MainActivity.aMaintEnable[3] = ((CheckBox) MainActivity.this.maintDialog.findViewById(R.id.maintCheckBox_4)).isChecked();
                MainActivity.aMaintValue[3] = MainActivity.this.toInteger(((EditText) MainActivity.this.maintDialog.findViewById(R.id.maintText_4)).getText().toString());
                MainActivity.aMaintEnable[4] = ((CheckBox) MainActivity.this.maintDialog.findViewById(R.id.maintCheckBox_5)).isChecked();
                MainActivity.aMaintValue[4] = MainActivity.this.toInteger(((EditText) MainActivity.this.maintDialog.findViewById(R.id.maintText_5)).getText().toString());
                MainActivity.aMaintEnable[5] = ((CheckBox) MainActivity.this.maintDialog.findViewById(R.id.maintCheckBox_6)).isChecked();
                MainActivity.aMaintValue[5] = MainActivity.this.toInteger(((EditText) MainActivity.this.maintDialog.findViewById(R.id.maintText_6)).getText().toString());
                MainActivity.aMaintEnable[6] = ((CheckBox) MainActivity.this.maintDialog.findViewById(R.id.maintCheckBox_7)).isChecked();
                MainActivity.aMaintValue[6] = MainActivity.this.toInteger(((EditText) MainActivity.this.maintDialog.findViewById(R.id.maintText_7)).getText().toString());
                MainActivity.aMaintEnable[7] = ((CheckBox) MainActivity.this.maintDialog.findViewById(R.id.maintCheckBox_8)).isChecked();
                MainActivity.aMaintEnable[8] = ((CheckBox) MainActivity.this.maintDialog.findViewById(R.id.maintCheckBox_9)).isChecked();
                MainActivity.aMaintValue[8] = MainActivity.this.toInteger(((EditText) MainActivity.this.maintDialog.findViewById(R.id.maintText_9)).getText().toString());
                MainActivity.aMaintEnable[9] = ((CheckBox) MainActivity.this.maintDialog.findViewById(R.id.maintCheckBox_10)).isChecked();
                MainActivity.aMaintValue[9] = MainActivity.this.toInteger(((EditText) MainActivity.this.maintDialog.findViewById(R.id.maintText_10)).getText().toString());
                MainActivity.this.sendMaintInfo();
                MainActivity.this.maintDialog.dismiss();
            }
        });
        this.maintDialog.findViewById(R.id.maintSuperExitButton).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.w(MainActivity.TAG, "Kiosk SuperExit button pressed");
                MainActivity mainActivity = MainActivity.this;
                int i = mainActivity.kiosk_mode_counter + MainActivity.GOOD_CONNECTIVITY;
                mainActivity.kiosk_mode_counter = i;
                if (i == 3) {
                    boolean z;
                    MainActivity.this.kiosk_mode_counter = MainActivity.UNKNOWN_CONNECTIVITY;
                    Context ctx = MainActivity.this.getApplicationContext();
                    if (PrefUtils.isKioskModeActive(ctx)) {
                        z = false;
                    } else {
                        z = MainActivity.enableKioskMode;
                    }
                    MainActivity.KioskMode = z;
                    PrefUtils.setKioskModeActive(MainActivity.KioskMode, ctx);
                    if (MainActivity.KioskMode) {
                        Toast.makeText(ctx, "Shields are UP.", MainActivity.UNKNOWN_CONNECTIVITY).show();
                    } else {
                        Toast.makeText(ctx, "Shields are down Commander!", MainActivity.UNKNOWN_CONNECTIVITY).show();
                    }
                }
            }
        });
        this.maintDialog.show();
    }

    public void clearMaintInfo() {
        for (int i = UNKNOWN_CONNECTIVITY; i < 10; i += GOOD_CONNECTIVITY) {
            aMaintEnable[i] = false;
            aMaintValue[i] = GOOD_CONNECTIVITY;
        }
        aMaintValue[3] = GOOD_CONNECTIVITY;
        aMaintValue[6] = GOOD_CONNECTIVITY;
        aMaintValue[7] = UNKNOWN_CONNECTIVITY;
        aMaintValue[8] = UNKNOWN_CONNECTIVITY;
        aMaintValue[9] = GOOD_CONNECTIVITY;
    }

    public void sendMaintInfo() {
        byte[] data = new byte[BAD_CONNECTIVITY];
        if (aMaintEnable[UNKNOWN_CONNECTIVITY]) {
            data[UNKNOWN_CONNECTIVITY] = (byte) ((aMaintValue[UNKNOWN_CONNECTIVITY] >> 8) & 255);
            data[GOOD_CONNECTIVITY] = (byte) (aMaintValue[UNKNOWN_CONNECTIVITY] & 255);
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG1, data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
        } else {
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG1, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
        }
        if (aMaintEnable[GOOD_CONNECTIVITY]) {
            data[UNKNOWN_CONNECTIVITY] = (byte) ((aMaintValue[GOOD_CONNECTIVITY] >> 8) & 255);
            data[GOOD_CONNECTIVITY] = (byte) (aMaintValue[GOOD_CONNECTIVITY] & 255);
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG2, data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
        } else {
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG2, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
        }
        if (aMaintEnable[BAD_CONNECTIVITY]) {
            data[UNKNOWN_CONNECTIVITY] = (byte) ((aMaintValue[BAD_CONNECTIVITY] >> 8) & 255);
            data[GOOD_CONNECTIVITY] = (byte) (aMaintValue[BAD_CONNECTIVITY] & 255);
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG3, data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
        } else {
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG3, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
        }
        if (aMaintEnable[3]) {
            data[UNKNOWN_CONNECTIVITY] = (byte) ((aMaintValue[3] >> 8) & 255);
            data[GOOD_CONNECTIVITY] = (byte) (aMaintValue[3] & 255);
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG4, data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
        } else {
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG4, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
        }
        if (aMaintEnable[4]) {
            data[UNKNOWN_CONNECTIVITY] = (byte) ((aMaintValue[4] >> 8) & 255);
            data[GOOD_CONNECTIVITY] = (byte) (aMaintValue[4] & 255);
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG5, data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
        } else {
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG5, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
        }
        if (aMaintEnable[5]) {
            data[UNKNOWN_CONNECTIVITY] = (byte) ((aMaintValue[5] >> 8) & 255);
            data[GOOD_CONNECTIVITY] = (byte) (aMaintValue[5] & 255);
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG6, data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
        } else {
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG6, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
        }
        if (aMaintEnable[6]) {
            data[UNKNOWN_CONNECTIVITY] = (byte) ((aMaintValue[6] >> 8) & 255);
            data[GOOD_CONNECTIVITY] = (byte) (aMaintValue[6] & 255);
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG7, data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
        } else {
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG7, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
        }
        if (aMaintEnable[7]) {
            String ServerRoute = ((EditText) this.maintDialog.findViewById(R.id.maintText_8)).getText().toString();
            if (ServerRoute.trim().isEmpty()) {
                ServerRoute = DefaultAPIroute;
            }
            int length = ServerRoute.trim().length();
            AccessoryControl accessoryControl = this.accessoryControl;
            if (length >= 41) {
                ServerRoute = DefaultAPIroute;
            }
            APIroute = ServerRoute.trim();
            Log.w(TAG, "(send) APIDATA_SERVER_ROUTE=" + APIroute);
            sendCmdString(AccessoryControl.APIDATA_SERVER_ROUTE, APIroute);
            ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_8)).setChecked(false);
            aMaintEnable[7] = false;
        }
        if (aMaintEnable[8]) {
            Gateway_VIN = BuildConfig.FLAVOR;
            sendVIN(Gateway_VIN);
            Log.i(TAG, "(send) APICMD_VIN= " + Gateway_VIN);
            Gateway_Fleet = BuildConfig.FLAVOR;
            sendFleet(Gateway_Fleet);
            Log.i(TAG, "(send) APICMD_Fleet= " + Gateway_Fleet);
            ActivationCode = UNKNOWN_CONNECTIVITY;
            this.accessoryControl.writeCommand(AccessoryControl.APIDATA_ACTIVATION_CODE, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
            Log.i(TAG, "(send) APIDATA_ACTIVATION_CODE= " + ActivationCode);
            this.accessoryControl.writeCommand(15, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
            Log.i(TAG, "(send) APICMD_ACTIVATE= 0");
            ((CheckBox) this.maintDialog.findViewById(R.id.maintCheckBox_9)).setChecked(false);
            aMaintEnable[8] = false;
        }
        if (aMaintEnable[9]) {
            data[UNKNOWN_CONNECTIVITY] = (byte) ((aMaintValue[9] >> 8) & 255);
            data[GOOD_CONNECTIVITY] = (byte) (aMaintValue[9] & 255);
            this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG10, data[UNKNOWN_CONNECTIVITY], data[GOOD_CONNECTIVITY]);
            return;
        }
        this.accessoryControl.writeCommand(AccessoryControl.APIDEBUG10, UNKNOWN_CONNECTIVITY, UNKNOWN_CONNECTIVITY);
    }

    public void sendVIN(String vin) {
        Log.i(TAG, "sendVIN.." + vin);
        int i = UNKNOWN_CONNECTIVITY;
        byte[] bytestring = new byte[41];
        if (vin.length() < 41) {
            i = UNKNOWN_CONNECTIVITY;
            while (i < vin.length()) {
                bytestring[i] = (byte) vin.charAt(i);
                if (bytestring[i] == 0) {
                    break;
                }
                i += GOOD_CONNECTIVITY;
            }
        }
        bytestring[i] = (byte) 0;
        this.accessoryControl.writeCommandBlock(14, i, bytestring);
    }

    public void sendFleet(String fleet) {
        Log.i(TAG, "sendFleet.." + fleet);
        int i = UNKNOWN_CONNECTIVITY;
        byte[] bytestring = new byte[41];
        if (fleet.length() < 41) {
            i = UNKNOWN_CONNECTIVITY;
            while (i < fleet.length()) {
                bytestring[i] = (byte) fleet.charAt(i);
                if (bytestring[i] == 0) {
                    break;
                }
                i += GOOD_CONNECTIVITY;
            }
        }
        bytestring[i] = (byte) 0;
        this.accessoryControl.writeCommandBlock(AccessoryControl.APIDATA_FLEET, i, bytestring);
    }

    public void sendFeatures() {
        int i;
        Log.i(TAG, "sendFeatures..");
        byte[] bytestring = new byte[202];
        for (i = UNKNOWN_CONNECTIVITY; i < 100; i += GOOD_CONNECTIVITY) {
            bytestring[i * BAD_CONNECTIVITY] = (byte) (Features.feature_value[i] & 255);
            bytestring[(i * BAD_CONNECTIVITY) + GOOD_CONNECTIVITY] = (byte) ((Features.feature_value[i] & 65280) >> 8);
        }
        this.accessoryControl.writeCommandBlock(AccessoryControl.APIDATA_FEATURE_VALUES, 200, bytestring);
        for (i = UNKNOWN_CONNECTIVITY; i < 100; i += GOOD_CONNECTIVITY) {
            bytestring[i] = (byte) (Features.feature_status[i] & 255);
        }
        this.accessoryControl.writeCommandBlock(AccessoryControl.APIDATA_FEATURE_CODES, 100, bytestring);
        for (i = UNKNOWN_CONNECTIVITY; i < 5; i += GOOD_CONNECTIVITY) {
            Log.i(TAG, "****** Feature Code[" + i + "]: status=" + ((byte) (Features.feature_status[i] & 255)) + "   value=" + Features.feature_value[i]);
        }
    }

    public boolean isAnyBloatware() {
        Log.i(TAG, "*** Checking for Bloatware...");
        return false;
    }

    public void removeBloatware() {
    }

    private boolean isAppInstalled(String uri) {
        try {
            Log.w(TAG, "    Bloatware found: " + uri + "   Enabled?" + (getPackageManager().getPackageInfo(uri, GOOD_CONNECTIVITY).applicationInfo.enabled ? "Enabled" : "Disabled"));
            return enableKioskMode;
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
        return enableKioskMode;
    }

    public boolean isRefreshAvailable() {
        Boolean UpdateAvail = Boolean.valueOf(isServerUpdateAvailable());
        if (UpdateAvail.booleanValue()) {
            findViewById(R.id.actionButtons).setVisibility(View.GONE);
            findViewById(R.id.updateButtons).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.actionButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.updateButtons).setVisibility(View.GONE);
        }
        return UpdateAvail.booleanValue();
    }

    public boolean isServerUpdateAvailable() {
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), UNKNOWN_CONNECTIVITY).versionName;
            Log.i(TAG, "   current APK version: " + version);
            String server_version = PrefUtils.getServerUpdateVersion(getApplicationContext());
            Log.i(TAG, "   server APK version: [" + server_version + "]");
            if (server_version.isEmpty() || version.compareTo(server_version) >= 0) {
                return false;
            }
            return enableKioskMode;
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

    public void sendCmdString(int cmd, String str) {
        Log.i(TAG, "sendCmdString..cmd:" + cmd + " string:" + str);
        byte[] bytestring = new byte[81];
        int i = UNKNOWN_CONNECTIVITY;
        while (i < str.length()) {
            bytestring[i] = (byte) str.charAt(i);
            if (bytestring[i] == 0) {
                break;
            }
            i += GOOD_CONNECTIVITY;
        }
        bytestring[i] = (byte) 0;
        this.accessoryControl.writeCommandBlock(cmd, i, bytestring);
    }

    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return enableKioskMode;
        } catch (Exception e) {
            return false;
        }
    }

    public int toInteger(String input) {
        if (isInteger(input)) {
            return Integer.valueOf(input).intValue();
        }
        return UNKNOWN_CONNECTIVITY;
    }

    public Handler getUIHandler() {
        return new UIHandler(this);
    }

    public void UpdateConnectivityStatus() {
        switch (SyncLast_Status) {
            case httpClient.PHONEHOME_TIMEOUT /*-3*/:
                setConnectivityStatus("Network Timeout", BAD_CONNECTIVITY);
                break;
            case httpClient.PHONEHOME_NONETWORK /*-2*/:
                setConnectivityStatus("No Network", BAD_CONNECTIVITY);
                break;
            case httpClient.PHONEHOME_ERROR /*-1*/:
                setConnectivityStatus("Network Error", BAD_CONNECTIVITY);
                break;
            case GOOD_CONNECTIVITY /*1*/:
            case httpClient.PHONEHOME_NONE /*5*/:
                setConnectivityStatus("Refresh: " + new SimpleDateFormat("MM-dd hh:mm a").format(SyncLast.getTime()), GOOD_CONNECTIVITY);
                break;
            case BAD_CONNECTIVITY /*2*/:
                setConnectivityStatus("Gateway updating..", GOOD_CONNECTIVITY);
                break;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                setConnectivityStatus("Refreshing..", UNKNOWN_CONNECTIVITY);
                break;
            case httpClient.PHONEHOME_APK_PENDING /*4*/:
                setConnectivityStatus("Tablet updating..", UNKNOWN_CONNECTIVITY);
                break;
        }
        isRefreshAvailable();
    }

    public void openCommDialog() {
        if (commDialog != null && commDialog.isShowing()) {
            commDialog.dismiss();
        }
        commDialog = new Dialog(this);
        commDialog.requestWindowFeature(GOOD_CONNECTIVITY);
        commDialog.setContentView(R.layout.comm_dialog);
        commDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        commlogtext = (TextView) commDialog.findViewById(R.id.commlog_Text);
        commlogstr = BuildConfig.FLAVOR;
        commlogtext.setText(commlogstr);
        commDialog.findViewById(R.id.commDoneButton).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.commDialog.dismiss();
            }
        });
        commDialog.show();
    }

    public void CommLogStr(String str) {
        if (aMaintEnable[9]) {
            commlogstr += str + "\n\r";
            commlogtext.setText(commlogstr);
        }
    }
}
