package vn.com.tma.idlesmart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.com.tma.idlesmart.tasks.ServerTask;
import vn.com.tma.idlesmart.tasks.UpdateApp;
import vn.com.tma.idlesmart.tasks.UpdateGateway;

public class httpClient extends Activity {
    private static boolean APKupdate_exists = false;
    private static boolean CSCupdate_exists = false;
    private static final int MAINT_JSON_ACTIVATION = 2;
    private static final int MAINT_JSON_ALL = 99;
    private static final int MAINT_JSON_APK_UPDATE = 6;
    private static final int MAINT_JSON_CSC_UPDATE = 7;
    private static final int MAINT_JSON_DATUM = 9;
    private static final int MAINT_JSON_GUID_REQUEST = 1;
    private static final int MAINT_JSON_LOG = 4;
    private static final int MAINT_JSON_UPDATE = 3;
    private static final int MAINT_JSON_VERSION = 5;
    private static final int MAINT_JSON_VERSION_ARRAY = 8;
    public static final int PHONEHOME_APK_PENDING = 4;
    public static final int PHONEHOME_AUTO_UPDATE = 1;
    public static final int PHONEHOME_CSC_PENDING = 2;
    public static final int PHONEHOME_ERROR = -1;
    public static final int PHONEHOME_FULL_UPDATE = 0;
    public static final int PHONEHOME_GATEWAY_UPDATE = 2;
    public static final int PHONEHOME_IDLE = 0;
    public static final int PHONEHOME_NONE = 5;
    public static final int PHONEHOME_NONETWORK = -2;
    public static final boolean PHONEHOME_NO_RESCHEDULE = false;
    public static final int PHONEHOME_OK = 1;
    public static final int PHONEHOME_PENDING = 3;
    public static final boolean PHONEHOME_RESCHEDULE = true;
    public static final int PHONEHOME_TABLET_UPDATE = 3;
    public static final int PHONEHOME_TIMEOUT = -3;
    private static final int PROGRESS_UPDATE_RATE = 100;
    public static boolean PhoneHomePending = false;
    private static final int STATE_ACTIVATE = 3;
    private static final int STATE_APKUPDATE = 7;
    private static final int STATE_CLEANUP = 100;
    private static final int STATE_CONNECT = 1;
    private static final int STATE_CSCUPDATE = 8;
    private static final int STATE_CSC_AUTOUPDATE = 9;
    private static final int STATE_DATUM = 20;
    private static final int STATE_DATUM_STATUS = 25;
    private static final int STATE_DONE = 90;
    private static final int STATE_ERROR = 99;
    private static final int STATE_FREEZE_GATEWAY = 2;
    private static final int STATE_IDLE = 0;
    private static final int STATE_LOG = 5;
    private static final int STATE_LOG_STATUS = 15;
    private static final int STATE_UPDATE = 4;
    private static final int STATE_VERSION = 6;
    private static final String TAG = "IdleSmart.HTTPClient";
    private static final int TEST_UPDATE_RATE = 1000;
    private static Context context;
    static final Handler phonehomeHandler;
    private static boolean phonehome_reschedule;
    private static int phonehome_state;
    private static int phonehome_update_level;
    private static int progressUpdateRate;
    private static int result;
    private final int APIRESPONSE_BAD;
    private final int APIRESPONSE_BADHTTP;
    private final int APIRESPONSE_INACTIVE;
    private final int APIRESPONSE_OK;
    private final String ActivationCreateTruck;
    private final String ActivationFindTruck;
    private final int DATUM_RECORDS_BLOCK_MAX;
    private final boolean ENABLE_APK_RETROGRADE_VERSION;
    private final boolean ENABLE_APK_UPDATE;
    private final boolean ENABLE_CSC_RETROGRADE_VERSION;
    private final boolean ENABLE_CSC_UPDATE;
    private final int LOG_RECORDS_BLOCK_MAX;
    private boolean NewTruckActivation;
    private final String crlf;
    private int deviceAPKversion;
    private int deviceCSCversion;
    public ProgressDialog dialog;
    int httpStatus;
    JSONObject jsonActivation;
    JSONObject jsonApkVersion;
    JSONArray jsonApkVersionStack;
    JSONObject jsonCscVersion;
    JSONArray jsonCscVersionStack;
    JSONObject jsonDeviceNode;
    JSONObject jsonGateway;
    JSONObject jsonUpdate;
    JSONObject jsonVersion;
    private MainActivity mInstance;
    final Runnable phonehomeRunnable;

    /* renamed from: com.idlesmarter.aoa.httpClient.1 */
    class C00111 implements Runnable {
        C00111() {
        }

        public void run() {
            boolean z = httpClient.PHONEHOME_NO_RESCHEDULE;
            int status;
            switch (httpClient.phonehome_state) {
                case httpClient.STATE_IDLE /*0*/:
                    httpClient.result = httpClient.STATE_ACTIVATE;
                    MainActivity.SyncLast_Status = httpClient.STATE_ACTIVATE;
                    MainActivity.SyncLast = Calendar.getInstance();
                    httpClient.this.mInstance.UpdateConnectivityStatus();
                    httpClient.this.dialog = new ProgressDialog(httpClient.this.mInstance);
                    httpClient.this.dialog.setIndeterminate(httpClient.PHONEHOME_RESCHEDULE);
                    httpClient.this.dialog.setCancelable(httpClient.PHONEHOME_RESCHEDULE);
                    httpClient.this.dialog.setProgressStyle(httpClient.STATE_IDLE);
                    httpClient.this.dialog.setTitle("Idle Smart Refresh");
                    httpClient.this.dialog.setMessage("Connecting to server");
                    httpClient.this.dialog.show();
                    httpClient.phonehome_state = httpClient.STATE_CONNECT;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_CONNECT /*1*/:
                    if (httpClient.this.isConnected()) {
                        httpClient.this.dialog.setMessage("Stopping Gateway");
                        httpClient.phonehome_state = httpClient.STATE_FREEZE_GATEWAY;
                    } else {
                        httpClient.this.dialog.setMessage("No Network Connection");
                        httpClient.result = httpClient.PHONEHOME_NONETWORK;
                        httpClient.phonehome_state = httpClient.STATE_ERROR;
                    }
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_FREEZE_GATEWAY /*2*/:
                    if (MainActivity.aMaintEnable[httpClient.STATE_CSC_AUTOUPDATE]) {
                        httpClient.this.mInstance.openCommDialog();
                    }
                    Log.w(httpClient.TAG, "send APICMD_FREEZE(true)..");
                    httpClient.this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_FREEZE, httpClient.STATE_IDLE, httpClient.STATE_CONNECT);
                    httpClient.this.dialog.setMessage("Identifying/Activating Vehicle");
                    httpClient.phonehome_state = httpClient.STATE_ACTIVATE;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_ACTIVATE /*3*/:
                    Log.i(httpClient.TAG, "       setJsonGateway..");
                    httpClient.this.setJsonGateway();
                    Log.i(httpClient.TAG, "       Activation..");
                    httpClient.this.PerformActivationTask();
                    if (httpClient.phonehome_update_level == httpClient.STATE_FREEZE_GATEWAY || httpClient.phonehome_update_level == httpClient.STATE_ACTIVATE) {
                        httpClient.this.dialog.setMessage("Checking for software updates");
                        httpClient.phonehome_state = httpClient.STATE_VERSION;
                    } else {
                        httpClient.this.dialog.setMessage("Updating Settings with Fleet Dashboard");
                        httpClient.phonehome_state = httpClient.STATE_UPDATE;
                    }
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_UPDATE /*4*/:
                    Log.i(httpClient.TAG, "       Update..");
                    httpClient.this.PerformUpdateTask();
                    httpClient.this.dialog.setMessage("Sending Vehicle Logs");
                    httpClient.phonehome_state = httpClient.STATE_LOG;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_LOG /*5*/:
                    Log.i(httpClient.TAG, "       Log..");
                    httpClient.this.PerformLogTask();
                    httpClient.phonehome_state = httpClient.STATE_LOG_STATUS;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_VERSION /*6*/:
                    Log.i(httpClient.TAG, "       Version..");
                    httpClient.this.PerformVersionTask();
                    httpClient.APKupdate_exists = httpClient.this.APKUpdateExist() == httpClient.STATE_CONNECT ? httpClient.PHONEHOME_RESCHEDULE : httpClient.PHONEHOME_NO_RESCHEDULE;
                    Log.i(httpClient.TAG, "       APKUpdateExist? " + (httpClient.APKupdate_exists ? "true" : "false"));
                    if (httpClient.this.CSCUpdateExist() == httpClient.STATE_CONNECT) {
                        z = httpClient.PHONEHOME_RESCHEDULE;
                    }
                    httpClient.CSCupdate_exists = z;
                    Log.i(httpClient.TAG, "       CSCUpdateExist? " + (httpClient.CSCupdate_exists ? "true" : "false"));
                    if (httpClient.phonehome_update_level == httpClient.STATE_FREEZE_GATEWAY) {
                        if (httpClient.CSCupdate_exists) {
                            httpClient.this.dialog.setMessage("Updating Gateway firmware");
                            httpClient.phonehome_state = httpClient.STATE_CSC_AUTOUPDATE;
                        } else {
                            httpClient.result = httpClient.STATE_LOG;
                            httpClient.phonehome_state = httpClient.STATE_DONE;
                        }
                    } else if (httpClient.phonehome_update_level == httpClient.STATE_ACTIVATE) {
                        if (httpClient.APKupdate_exists) {
                            httpClient.this.dialog.setMessage("Updating Tablet Application");
                            httpClient.phonehome_state = httpClient.STATE_APKUPDATE;
                        } else {
                            httpClient.result = httpClient.STATE_LOG;
                            httpClient.phonehome_state = httpClient.STATE_DONE;
                        }
                    } else if (httpClient.phonehome_update_level == httpClient.STATE_CONNECT) {
                        if (httpClient.CSCupdate_exists) {
                            httpClient.this.dialog.setMessage("Updating Gateway firmware");
                            httpClient.phonehome_state = httpClient.STATE_CSCUPDATE;
                        } else {
                            if (httpClient.APKupdate_exists) {
                                httpClient.result = httpClient.STATE_UPDATE;
                            } else {
                                httpClient.result = httpClient.STATE_CONNECT;
                            }
                            httpClient.phonehome_state = httpClient.STATE_DONE;
                        }
                    } else if (httpClient.APKupdate_exists) {
                        httpClient.this.dialog.setMessage("Updating Tablet Application");
                        httpClient.phonehome_state = httpClient.STATE_APKUPDATE;
                    } else if (httpClient.CSCupdate_exists) {
                        httpClient.this.dialog.setMessage("Updating Gateway firmware");
                        httpClient.phonehome_state = httpClient.STATE_CSCUPDATE;
                    } else {
                        httpClient.result = httpClient.STATE_CONNECT;
                        httpClient.phonehome_state = httpClient.STATE_DONE;
                    }
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_APKUPDATE /*7*/:
                    httpClient.result = httpClient.STATE_CONNECT;
                    if (httpClient.APKupdate_exists) {
                        if (httpClient.CSCupdate_exists) {
                            Log.w(httpClient.TAG, "Send APICMD_DL(2)..(delayed CSC update)");
                            httpClient.this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_DL, httpClient.STATE_IDLE, httpClient.STATE_CONNECT);
                        }
                        status = httpClient.this.PerformAPKUpdate();
                        if (status == 0) {
                            httpClient.result = httpClient.STATE_CONNECT;
                        } else if (status == httpClient.STATE_CONNECT) {
                            if (MainActivity.PackageUpdatePending) {
                                httpClient.result = httpClient.STATE_UPDATE;
                            }
                        } else if (status < 0) {
                            httpClient.result = httpClient.PHONEHOME_ERROR;
                        }
                        if (httpClient.result == httpClient.STATE_CONNECT && httpClient.CSCupdate_exists) {
                            httpClient.result = httpClient.STATE_FREEZE_GATEWAY;
                        }
                        httpClient.phonehome_state = httpClient.STATE_DONE;
                    }
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                    break;
                case httpClient.STATE_CSCUPDATE /*8*/:
                case httpClient.STATE_CSC_AUTOUPDATE /*9*/:
                    if (httpClient.CSCupdate_exists) {
                        status = httpClient.this.PerformCSCUpdate();
                        if (status == 0) {
                            httpClient.result = httpClient.STATE_LOG;
                        } else if (status < 0) {
                            httpClient.result = httpClient.PHONEHOME_ERROR;
                        } else {
                            httpClient.result = httpClient.STATE_CONNECT;
                        }
                    } else {
                        httpClient.result = httpClient.STATE_LOG;
                    }
                    httpClient.phonehome_state = httpClient.STATE_DONE;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_LOG_STATUS /*15*/:
                    httpClient.this.dialog.setMessage("Sending Collected Data");
                    httpClient.phonehome_state = httpClient.STATE_DATUM;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_DATUM /*20*/:
                    Log.i(httpClient.TAG, "       Datum..");
                    httpClient.this.PerformDatumTask();
                    httpClient.phonehome_state = httpClient.STATE_DATUM_STATUS;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_DATUM_STATUS /*25*/:
                    httpClient.this.dialog.setMessage("Checking for software updates");
                    httpClient.phonehome_state = httpClient.STATE_VERSION;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case httpClient.STATE_DONE /*90*/:
                    break;
                case httpClient.STATE_ERROR /*99*/:
                    MainActivity.SyncLast_Status = httpClient.result;
                    httpClient.this.mInstance.UpdateConnectivityStatus();
                    String resched_msg = httpClient.phonehome_reschedule == httpClient.PHONEHOME_RESCHEDULE ? "\n\nRefresh has been rescheduled" : BuildConfig.FLAVOR;
                    switch (httpClient.result) {
                        case httpClient.PHONEHOME_TIMEOUT /*-3*/:
                            httpClient.this.dialog.setMessage("Server dashboard not responding" + resched_msg);
                            break;
                        case httpClient.PHONEHOME_NONETWORK /*-2*/:
                            httpClient.this.dialog.setMessage("Cannot connect to network" + resched_msg);
                            break;
                        case httpClient.PHONEHOME_ERROR /*-1*/:
                            httpClient.this.dialog.setMessage("Errors encountered during refresh" + resched_msg);
                            break;
                        case httpClient.STATE_CONNECT /*1*/:
                            MainActivity.SyncLast = Calendar.getInstance();
                            httpClient com_idlesmarter_aoa_httpClient = httpClient.this;
                            int access$100 = httpClient.result;
                            com_idlesmarter_aoa_httpClient.sendSyncLast(access$100, MainActivity.SyncLast);
                            httpClient.this.dialog.setMessage("Refresh completed");
                            break;
                        case httpClient.STATE_FREEZE_GATEWAY /*2*/:
                            httpClient.this.dialog.setMessage("Gateway update is in progress");
                            break;
                        case httpClient.STATE_ACTIVATE /*3*/:
                            httpClient.this.dialog.setMessage("Refresh is in progress");
                            break;
                        case httpClient.STATE_UPDATE /*4*/:
                            httpClient.this.dialog.setMessage("Tablet update is in progress");
                            break;
                        case httpClient.STATE_LOG /*5*/:
                            httpClient.this.dialog.setMessage("No updates found");
                            break;
                    }
                    httpClient.PhoneHomePending = httpClient.PHONEHOME_NO_RESCHEDULE;
                    if (httpClient.result != httpClient.STATE_UPDATE) {
                        Log.w(httpClient.TAG, "send APICMD_FREEZE(false)..");
                        httpClient.this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_FREEZE, httpClient.STATE_IDLE, httpClient.STATE_IDLE);
                    }
                    if (httpClient.phonehome_reschedule == httpClient.PHONEHOME_RESCHEDULE) {
                        httpClient.this.mInstance.ReschedulePhoneHome(60);
                    }
                    httpClient.phonehome_state = httpClient.STATE_CLEANUP;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, 3000);
                	break;
				case httpClient.STATE_CLEANUP /*100*/:
                    httpClient.this.dialog.dismiss();
                    httpClient.phonehome_state = httpClient.STATE_IDLE;
                    httpClient.phonehomeHandler.removeCallbacks(httpClient.this.phonehomeRunnable);
                    httpClient.PhoneHomePending = httpClient.PHONEHOME_NO_RESCHEDULE;
                    if (httpClient.result == httpClient.STATE_UPDATE) {
                        httpClient.this.mInstance.exit();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public httpClient(MainActivity act) {
        this.httpStatus = STATE_IDLE;
        this.ENABLE_APK_UPDATE = PHONEHOME_RESCHEDULE;
        this.ENABLE_CSC_UPDATE = PHONEHOME_RESCHEDULE;
        this.ENABLE_APK_RETROGRADE_VERSION = PHONEHOME_RESCHEDULE;
        this.ENABLE_CSC_RETROGRADE_VERSION = PHONEHOME_RESCHEDULE;
        this.LOG_RECORDS_BLOCK_MAX = STATE_DATUM_STATUS;
        this.DATUM_RECORDS_BLOCK_MAX = STATE_DATUM_STATUS;
        this.APIRESPONSE_BAD = STATE_IDLE;
        this.APIRESPONSE_BADHTTP = STATE_CONNECT;
        this.APIRESPONSE_INACTIVE = STATE_FREEZE_GATEWAY;
        this.APIRESPONSE_OK = 10;
        this.ActivationFindTruck = "find_truck";
        this.ActivationCreateTruck = "create_truck";
        this.NewTruckActivation = PHONEHOME_NO_RESCHEDULE;
        this.deviceAPKversion = STATE_IDLE;
        this.deviceCSCversion = STATE_IDLE;
        this.jsonGateway = null;
        this.jsonVersion = null;
        this.jsonActivation = null;
        this.jsonUpdate = null;
        this.jsonDeviceNode = null;
        this.jsonApkVersionStack = null;
        this.jsonCscVersionStack = null;
        this.jsonApkVersion = null;
        this.jsonCscVersion = null;
        this.crlf = "\n\r";
        this.phonehomeRunnable = new C00111();
        this.mInstance = act;
    }

    public httpClient() {
        this.httpStatus = STATE_IDLE;
        this.ENABLE_APK_UPDATE = PHONEHOME_RESCHEDULE;
        this.ENABLE_CSC_UPDATE = PHONEHOME_RESCHEDULE;
        this.ENABLE_APK_RETROGRADE_VERSION = PHONEHOME_RESCHEDULE;
        this.ENABLE_CSC_RETROGRADE_VERSION = PHONEHOME_RESCHEDULE;
        this.LOG_RECORDS_BLOCK_MAX = STATE_DATUM_STATUS;
        this.DATUM_RECORDS_BLOCK_MAX = STATE_DATUM_STATUS;
        this.APIRESPONSE_BAD = STATE_IDLE;
        this.APIRESPONSE_BADHTTP = STATE_CONNECT;
        this.APIRESPONSE_INACTIVE = STATE_FREEZE_GATEWAY;
        this.APIRESPONSE_OK = 10;
        this.ActivationFindTruck = "find_truck";
        this.ActivationCreateTruck = "create_truck";
        this.NewTruckActivation = PHONEHOME_NO_RESCHEDULE;
        this.deviceAPKversion = STATE_IDLE;
        this.deviceCSCversion = STATE_IDLE;
        this.jsonGateway = null;
        this.jsonVersion = null;
        this.jsonActivation = null;
        this.jsonUpdate = null;
        this.jsonDeviceNode = null;
        this.jsonApkVersionStack = null;
        this.jsonCscVersionStack = null;
        this.jsonApkVersion = null;
        this.jsonCscVersion = null;
        this.crlf = "\n\r";
        this.phonehomeRunnable = new C00111();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = BuildConfig.FLAVOR;
        String result = BuildConfig.FLAVOR;
        while (true) {
            line = bufferedReader.readLine();
            if (line != null) {
                result = result + line;
            } else {
                inputStream.close();
                return result;
            }
        }
    }

    public boolean isConnected() {
        NetworkInfo networkInfo = ((ConnectivityManager) this.mInstance.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Log.e(TAG, "NOT Connected to Network");
            return PHONEHOME_NO_RESCHEDULE;
        }
        Log.i(TAG, "Connected to Network");
        return PHONEHOME_RESCHEDULE;
    }

    public void CommLog(int flag, String str) {
        if (!MainActivity.aMaintEnable[STATE_CSC_AUTOUPDATE]) {
            return;
        }
        if (flag == STATE_CONNECT || flag == MainActivity.aMaintValue[STATE_CSC_AUTOUPDATE] || MainActivity.aMaintValue[STATE_CSC_AUTOUPDATE] == STATE_ERROR) {
            this.mInstance.CommLogStr(str);
        }
    }

    static {
        PhoneHomePending = PHONEHOME_NO_RESCHEDULE;
        phonehome_update_level = STATE_IDLE;
        phonehomeHandler = new Handler();
        progressUpdateRate = STATE_CLEANUP;
        phonehome_state = STATE_IDLE;
        APKupdate_exists = PHONEHOME_NO_RESCHEDULE;
        CSCupdate_exists = PHONEHOME_NO_RESCHEDULE;
        result = STATE_LOG;
    }

    public void PhoneHome(int update_level, boolean reschedule) {
        Log.i(TAG, "======> PhoneHome request <======");
        phonehome_update_level = update_level;
        phonehome_reschedule = reschedule;
        String str = TAG;
        StringBuilder append = new StringBuilder().append("     Update Level: ");
        String str2 = update_level == STATE_CONNECT ? "AUTO" : update_level == STATE_ACTIVATE ? "TABLET" : update_level == STATE_FREEZE_GATEWAY ? "GATEWAY" : "FULL";
        Log.i(str, append.append(str2).toString());
        phonehome_state = STATE_IDLE;
        PhoneHomePending = PHONEHOME_RESCHEDULE;
        phonehomeHandler.removeCallbacks(this.phonehomeRunnable);
        if (MainActivity.test_mode) {
            progressUpdateRate = TEST_UPDATE_RATE;
        } else {
            progressUpdateRate = STATE_CLEANUP;
        }
        phonehomeHandler.postDelayed(this.phonehomeRunnable, (long) progressUpdateRate);
    }

    public void sendSyncLast(int status, Calendar synclast) {
        Log.i(TAG, "sendSyncLast.." + status + synclast);
        byte[] bytestring = new byte[STATE_CSCUPDATE];
        bytestring[STATE_IDLE] = (byte) (status & 255);
        bytestring[STATE_CONNECT] = (byte) ((synclast.get(STATE_CONNECT) >> STATE_CSCUPDATE) & 255);
        bytestring[STATE_FREEZE_GATEWAY] = (byte) (synclast.get(STATE_CONNECT) & 255);
        bytestring[STATE_ACTIVATE] = (byte) synclast.get(STATE_FREEZE_GATEWAY);
        bytestring[STATE_UPDATE] = (byte) synclast.get(STATE_LOG);
        bytestring[STATE_LOG] = (byte) synclast.get(11);
        bytestring[STATE_VERSION] = (byte) synclast.get(12);
        bytestring[STATE_APKUPDATE] = (byte) synclast.get(13);
        AccessoryControl accessoryControl = this.mInstance.accessoryControl;
        AccessoryControl accessoryControl2 = this.mInstance.accessoryControl;
        accessoryControl.writeCommandBlock(AccessoryControl.APIDATA_SYNC_LAST, STATE_CSCUPDATE, bytestring);
    }

    public void setJsonGateway() {
        try {
            this.jsonGateway = new JSONObject();
            this.jsonGateway.accumulate("vin", MainActivity.Gateway_VIN);
            this.jsonGateway.accumulate("activation_phrase", Integer.valueOf(MainActivity.ActivationCode));
            this.jsonGateway.accumulate("guid", Integer.valueOf(STATE_IDLE));
            this.jsonGateway.accumulate("serial", "1234");
            Log.i(TAG, "jsonGateway " + this.jsonGateway.toString(STATE_CONNECT));
            CommLog(STATE_CONNECT, "jsonGateway " + this.jsonGateway.toString(STATE_CONNECT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int PerformVersionTask() {
        int responseCode = PHONEHOME_ERROR;
        Log.i(TAG, "VersionTask");
        CommLog(STATE_LOG, "VersionTask");
        try {
            JSONObject jsonRequest = new JSONObject();
            ServerTask servertask = new ServerTask();
            servertask.setContext(this.mInstance.getApplicationContext());
            Log.i(TAG, "versionTask:servertask.execute..");
            String[] strArr = new String[STATE_FREEZE_GATEWAY];
            strArr[STATE_IDLE] = "http://" + MainActivity.APIroute + "/api/version";
            strArr[STATE_CONNECT] = jsonRequest.toString();
            servertask.execute(strArr);
            Log.i(TAG, "versionTask:servertask.get..");
            String response = (String) servertask.get(60, TimeUnit.SECONDS);
            Log.i(TAG, "versionTask:servertask.get response=" + response);
            CommLog(STATE_LOG, "servertask.get - finished");
            if (response.isEmpty()) {
                Log.e(TAG, "ERROR: versionTaskResponse is empty");
                CommLog(STATE_LOG, "ERROR: versionTaskResponse is empty");
                return STATE_CONNECT;
            }
            this.jsonVersion = new JSONObject(response);
            Log.i(TAG, "Versions:" + this.jsonVersion.toString());
            CommLog(STATE_CSCUPDATE, "Versions:" + this.jsonVersion.toString());
            responseCode = this.jsonVersion.getInt("code");
            if (responseCode == 10) {
                this.jsonApkVersion = this.jsonVersion.getJSONObject("recent_apk");
                this.jsonCscVersion = this.jsonVersion.getJSONObject("recent_csc");
                if (this.jsonVersion.has("apk_versions")) {
                    this.jsonApkVersionStack = this.jsonVersion.getJSONArray("apk_versions");
                } else {
                    this.jsonApkVersionStack = null;
                }
                if (this.jsonVersion.has("csc_versions")) {
                    this.jsonCscVersionStack = this.jsonVersion.getJSONArray("csc_versions");
                    return responseCode;
                }
                this.jsonCscVersionStack = null;
                return responseCode;
            }
            Log.e(TAG, "*** Server Error Code: " + Integer.toString(responseCode));
            CommLog(STATE_LOG, "jsonResponse = " + this.jsonVersion.toString(STATE_CONNECT));
            return responseCode;
        } catch (JSONException e) {
            e.printStackTrace();
            return responseCode;
        } catch (InterruptedException e2) {
            e2.printStackTrace();
            return responseCode;
        } catch (ExecutionException e3) {
            e3.printStackTrace();
            return responseCode;
        } catch (TimeoutException e4) {
            e4.printStackTrace();
            return responseCode;
        }
    }

    public int PerformActivationTask() {
        int responseCode = PHONEHOME_ERROR;
        Log.i(TAG, "ActivationTask");
        CommLog(STATE_FREEZE_GATEWAY, "ActivationTask");
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.accumulate("vin", this.jsonGateway.getString("vin"));
            jsonRequest.accumulate("activation_phrase", Integer.valueOf(this.jsonGateway.getInt("activation_phrase")));
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonActivationRequest:" + jsonRequest.toString(STATE_CONNECT));
            }
            CommLog(STATE_FREEZE_GATEWAY, "jsonActivationRequest:" + jsonRequest.toString(STATE_CONNECT));
            ServerTask servertask = new ServerTask();
            servertask.setContext(this.mInstance.getApplicationContext());
            Log.i(TAG, "activationTask:servertask.execute..");
            String[] strArr = new String[STATE_FREEZE_GATEWAY];
            strArr[STATE_IDLE] = "http://" + MainActivity.APIroute + "/api/truck/activate";
            strArr[STATE_CONNECT] = jsonRequest.toString();
            servertask.execute(strArr);
            if (MainActivity.DebugLog) {
                Log.i(TAG, "activationTask:servertask.get..");
            }
            String response = (String) servertask.get(60, TimeUnit.SECONDS);
            if (MainActivity.DebugLog) {
                Log.i(TAG, "activationTask:servertask.get response=" + response);
            }
            CommLog(STATE_FREEZE_GATEWAY, "servertask.get - finished");
            if (response.isEmpty()) {
                Log.e(TAG, "ERROR: activationTaskResponse is empty");
                CommLog(STATE_FREEZE_GATEWAY, "ERROR: activationTaskResponse is empty");
                return STATE_CONNECT;
            }
            this.jsonActivation = new JSONObject(response);
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonActivationResponse=" + this.jsonActivation.toString(STATE_CONNECT));
            }
            CommLog(STATE_FREEZE_GATEWAY, "jsonActivationResponse=" + this.jsonActivation.toString(STATE_CONNECT));
            responseCode = this.jsonActivation.getInt("code");
            if (responseCode == 10) {
                this.jsonGateway.put("guid", this.jsonActivation.getInt("guid"));
                this.mInstance.accessoryControl.writeCommand(STATE_LOG_STATUS, STATE_IDLE, STATE_FREEZE_GATEWAY);
                Log.i(TAG, "APICMD_ACTIVATE = 2");
                this.NewTruckActivation = this.jsonActivation.getString("route_type").equals("create_truck");
                if (!this.jsonActivation.has("fleet_name") || this.jsonActivation.isNull("fleet_name")) {
                    MainActivity.Gateway_Fleet = BuildConfig.FLAVOR;
                } else {
                    String temp = this.jsonActivation.getString("fleet_name");
                    if (temp.length() < 41) {
                        MainActivity.Gateway_Fleet = temp;
                    }
                }
                this.mInstance.sendFleet(MainActivity.Gateway_Fleet);
                return responseCode;
            }
            Log.e(TAG, "*** Server Error Code: " + Integer.toString(responseCode));
            return responseCode;
        } catch (JSONException e) {
            e.printStackTrace();
            return responseCode;
        } catch (InterruptedException e2) {
            e2.printStackTrace();
            return responseCode;
        } catch (ExecutionException e3) {
            e3.printStackTrace();
            return responseCode;
        } catch (TimeoutException e4) {
            e4.printStackTrace();
            return responseCode;
        }
    }

    public int PerformUpdateTask() {
        byte[] data = new byte[STATE_FREEZE_GATEWAY];
        int responseCode = PHONEHOME_ERROR;
        Log.i(TAG, "UpdateTask");
        CommLog(STATE_ACTIVATE, "UpdateTask");
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.accumulate("vin", this.jsonGateway.getString("vin"));
            jsonRequest.accumulate("guid", Integer.valueOf(this.jsonGateway.getInt("guid")));
            JSONObject jsonDevice = new JSONObject();
            jsonDevice.accumulate("serial", Integer.valueOf(this.jsonGateway.getInt("serial")));
            jsonRequest.accumulate("device", jsonDevice);
            this.jsonDeviceNode = new JSONObject();
            if (!this.NewTruckActivation) {
                this.jsonDeviceNode.accumulate("DRIVER_CABINCOMFORT_ENABLE", Integer.valueOf(MainActivity.aParam[STATE_IDLE]));
                this.jsonDeviceNode.accumulate("COLDWEATHERGUARD_ENABLE", Integer.valueOf(MainActivity.aParam[STATE_CONNECT]));
                this.jsonDeviceNode.accumulate("BATTERYMONITOR_ENABLE", Integer.valueOf(MainActivity.aParam[STATE_FREEZE_GATEWAY]));
                this.jsonDeviceNode.accumulate("DRIVER_CABINCOMFORT_SETPOINT", Integer.valueOf(MainActivity.aParam[STATE_ACTIVATE]));
                this.jsonDeviceNode.accumulate("CABINCOMFORT_RANGE", Integer.valueOf(MainActivity.aParam[STATE_UPDATE]));
                this.jsonDeviceNode.accumulate("CABINCOMFORT_AMBIENT_SETPOINT", Integer.valueOf(MainActivity.aParam[STATE_LOG]));
                this.jsonDeviceNode.accumulate("CABINCOMFORT_AMBIENT_RANGE", Integer.valueOf(MainActivity.aParam[STATE_VERSION]));
                this.jsonDeviceNode.accumulate("BATTERYMONITOR_VOLTAGE", battmv2Str(MainActivity.aParam[STATE_CSCUPDATE]));
                this.jsonDeviceNode.accumulate("BATTERYMONITOR_RUNTIME", Integer.valueOf(MainActivity.aParam[STATE_CSC_AUTOUPDATE]));
                this.jsonDeviceNode.accumulate("COLDWEATHERGUARD_IDEAL_COOLANT", Integer.valueOf(MainActivity.aParam[10]));
                this.jsonDeviceNode.accumulate("COLDWEATHERGUARD_MIN_COOLANT", Integer.valueOf(MainActivity.aParam[11]));
                this.jsonDeviceNode.accumulate("COLDWEATHERGUARD_START_TEMP", Integer.valueOf(MainActivity.aParam[12]));
                this.jsonDeviceNode.accumulate("RESTART_INTERVAL", Integer.valueOf(MainActivity.aParam[13]));
                this.jsonDeviceNode.accumulate("COMMON_SCREEN_DIM", Integer.valueOf(MainActivity.aParam[14]));
                this.jsonDeviceNode.accumulate("COMMON_IDLERPM", Integer.valueOf(MainActivity.aParam[16]));
                this.jsonDeviceNode.accumulate("COMMON_RUNTIME", Integer.valueOf(MainActivity.aParam[18]));
                this.jsonDeviceNode.accumulate("COMMON_DRIVER_TEMP_RANGE", Integer.valueOf(MainActivity.aParam[17]));
                jsonRequest.accumulate("node_settings", this.jsonDeviceNode);
            }
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonUpdateRequest:" + jsonRequest.toString(STATE_CONNECT));
            }
            CommLog(STATE_ACTIVATE, "jsonUpdateRequest:" + jsonRequest.toString(STATE_CONNECT));
            ServerTask servertask = new ServerTask();
            servertask.setContext(this.mInstance.getApplicationContext());
            Log.i(TAG, "updateTask:servertask.execute..");
            String[] strArr = new String[STATE_FREEZE_GATEWAY];
            strArr[STATE_IDLE] = "http://" + MainActivity.APIroute + "/api/truck/update";
            strArr[STATE_CONNECT] = jsonRequest.toString();
            servertask.execute(strArr);
            String response = (String) servertask.get(60, TimeUnit.SECONDS);
            Log.i(TAG, "updateTask:servertask.get response=" + response);
            CommLog(STATE_ACTIVATE, "servertask.get - finished");
            if (response.isEmpty()) {
                Log.e(TAG, "ERROR: updateTaskResponse is empty");
                CommLog(STATE_ACTIVATE, "ERROR: updateTaskResponse is empty");
                return STATE_CONNECT;
            }
            this.jsonUpdate = new JSONObject(response);
            responseCode = this.jsonUpdate.getInt("code");
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonUpdateResponse:" + this.jsonUpdate.toString(STATE_CONNECT));
            }
            CommLog(STATE_ACTIVATE, "jsonUpdateResponse:" + this.jsonUpdate.toString(STATE_CONNECT));
            if (responseCode == 10) {
                JSONObject jsonServerNode = this.jsonUpdate.getJSONObject("node_settings");
                if (jsonServerNode.getInt("CABINCOMFORT_ENABLE") == 0) {
                    this.mInstance.SaveDownloadedParamValue(23, STATE_IDLE);
                    this.mInstance.SaveDownloadedParamValue(STATE_IDLE, STATE_IDLE);
                } else {
                    this.mInstance.SaveDownloadedParamValue(23, STATE_CONNECT);
                }
                this.mInstance.SaveDownloadedParamValue(STATE_CONNECT, jsonServerNode.getInt("COLDWEATHERGUARD_ENABLE") != 0 ? STATE_CONNECT : STATE_IDLE);
                this.mInstance.SaveDownloadedParamValue(STATE_FREEZE_GATEWAY, jsonServerNode.getInt("BATTERYMONITOR_ENABLE") != 0 ? STATE_CONNECT : STATE_IDLE);
                this.mInstance.SaveDownloadedParamValue(24, jsonServerNode.getInt("CABINCOMFORT_SETPOINT"));
                this.mInstance.SaveDownloadedParamValue(STATE_UPDATE, jsonServerNode.getInt("CABINCOMFORT_RANGE"));
                this.mInstance.SaveDownloadedParamValue(STATE_LOG, jsonServerNode.getInt("CABINCOMFORT_AMBIENT_SETPOINT"));
                this.mInstance.SaveDownloadedParamValue(STATE_VERSION, jsonServerNode.getInt("CABINCOMFORT_AMBIENT_RANGE"));
                int battmv = battStr2mv(jsonServerNode.getString("BATTERYMONITOR_VOLTAGE"));
                if (battmv != 0) {
                    this.mInstance.SaveDownloadedParamValue(STATE_CSCUPDATE, battmv);
                }
                this.mInstance.SaveDownloadedParamValue(STATE_CSC_AUTOUPDATE, jsonServerNode.getInt("BATTERYMONITOR_RUNTIME"));
                this.mInstance.SaveDownloadedParamValue(10, jsonServerNode.getInt("COLDWEATHERGUARD_IDEAL_COOLANT"));
                this.mInstance.SaveDownloadedParamValue(11, jsonServerNode.getInt("COLDWEATHERGUARD_MIN_COOLANT"));
                this.mInstance.SaveDownloadedParamValue(12, jsonServerNode.getInt("COLDWEATHERGUARD_START_TEMP"));
                this.mInstance.SaveDownloadedParamValue(13, jsonServerNode.getInt("COLDWEATHERGUARD_RESTART_INTERVAL"));
                this.mInstance.SaveDownloadedParamValue(14, jsonServerNode.getInt("COMMON_SCREEN_DIM"));
                this.mInstance.SaveDownloadedParamValue(16, jsonServerNode.getInt("COMMON_IDLERPM"));
                this.mInstance.SaveDownloadedParamValue(18, jsonServerNode.getInt("COMMON_RUNTIME"));
                this.mInstance.SaveDownloadedParamValue(17, jsonServerNode.getInt("COMMON_DRIVER_TEMP_RANGE"));
                int temp_passwordenable = jsonServerNode.getInt("COMMON_PROHIBIT_DRIVER_EDITS");
                MainActivity.PasswordEnable = temp_passwordenable != 0 ? PHONEHOME_RESCHEDULE : PHONEHOME_NO_RESCHEDULE;
                this.mInstance.SaveDownloadedParamValue(19, temp_passwordenable);
                if (jsonServerNode.getString("COMMON_DRIVER_UNLOCK_CODE").isEmpty()) {
                    MainActivity.Password = 5555;
                } else {
                    MainActivity.Password = jsonServerNode.getInt("COMMON_DRIVER_UNLOCK_CODE");
                }
                this.mInstance.SaveDownloadedParamValue(STATE_DATUM, MainActivity.Password);
                boolean newSync = PHONEHOME_NO_RESCHEDULE;
                if (jsonServerNode.has("COMMON_TIME_TO_LIVE") && !jsonServerNode.isNull("COMMON_TIME_TO_LIVE")) {
                    int newSyncTTL = jsonServerNode.getInt("COMMON_TIME_TO_LIVE");
                    Log.i(TAG, "COMMON_TIME_TO_LIVE = " + newSyncTTL);
                    if (newSyncTTL > 1440) {
                        newSyncTTL = 1440;
                    }
                    if (newSyncTTL != MainActivity.SyncTTL) {
                        Log.i(TAG, "New TTL - we need a recalc");
                        MainActivity.SyncTTL = newSyncTTL;
                        newSync = PHONEHOME_RESCHEDULE;
                        data[STATE_IDLE] = (byte) ((newSyncTTL >> STATE_CSCUPDATE) & 255);
                        data[STATE_CONNECT] = (byte) (newSyncTTL & 255);
                        this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_SYNC_TTL, data[STATE_IDLE], data[STATE_CONNECT]);
                    }
                }
                if (jsonServerNode.has("COMMON_TIME_TO_START") && !jsonServerNode.isNull("COMMON_TIME_TO_START")) {
                    int newSyncStart = jsonServerNode.getInt("COMMON_TIME_TO_START") * 60;
                    Log.i(TAG, "TIME_TO_START = " + newSyncStart);
                    if (newSyncStart >= 1440) {
                        newSyncStart = 60;
                    }
                    if (newSyncStart != MainActivity.SyncStart) {
                        Log.i(TAG, "New TIME_TO_START - we need a recalc");
                        MainActivity.SyncStart = newSyncStart;
                        newSync = PHONEHOME_RESCHEDULE;
                        data[STATE_IDLE] = (byte) ((newSyncStart >> STATE_CSCUPDATE) & 255);
                        data[STATE_CONNECT] = (byte) (newSyncStart & 255);
                        this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_SYNC_START, data[STATE_IDLE], data[STATE_CONNECT]);
                    }
                }
                if (newSync) {
                    this.mInstance.SetNextPhoneHome();
                }
                Features.resetFeatureCodes();
                if (this.jsonUpdate.has("features") && !this.jsonUpdate.isNull("features")) {
                    Features.parseFeatureList(this.jsonUpdate.getString("features"));
                }
                this.mInstance.sendFeatures();
                return responseCode;
            }
            Log.e(TAG, "*** Server Error Code: " + Integer.toString(responseCode));
            return responseCode;
        } catch (JSONException e) {
            e.printStackTrace();
            return responseCode;
        } catch (InterruptedException e2) {
            e2.printStackTrace();
            return responseCode;
        } catch (ExecutionException e3) {
            e3.printStackTrace();
            return responseCode;
        } catch (TimeoutException e4) {
            e4.printStackTrace();
            return responseCode;
        }
    }

    public int battStr2mv(String battstr) {
        try {
            return (Integer.parseInt(battstr.substring(STATE_IDLE, STATE_FREEZE_GATEWAY)) * 10) + Integer.parseInt(battstr.substring(STATE_ACTIVATE, STATE_UPDATE));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return STATE_IDLE;
        }
    }

    public String battmv2Str(int battvolt) {
        int volts = battvolt / 10;
        String voltstr = Integer.toString(volts);
        return voltstr + "." + Integer.toString(battvolt - (volts * 10));
    }

    public int PerformLogTask() {
        int responseCode = PHONEHOME_ERROR;
        Log.i(TAG, "LogTask");
        CommLog(STATE_UPDATE, "LogTask");
        if (this.mInstance.accessoryControl.logStream != null) {
            this.mInstance.accessoryControl.writeLogString("Start Upload");
            this.mInstance.accessoryControl.closeLogFile();
        }
        BufferedInputStream logStream = openLogBufferedInputStream();
        if (logStream == null) {
            Log.i(TAG, "Log file does not exist or is empty");
            CommLog(STATE_UPDATE, "Log file does not exist or is empty");
            return PHONEHOME_ERROR;
        }
        BufferedReader logIn = new BufferedReader(new InputStreamReader(logStream));
        while (logIn != null) {
            JSONArray jsonLog = convertLogToJsonArray(logIn, STATE_DATUM_STATUS);
            if (jsonLog == null) {
                break;
            }
            try {
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.accumulate("vin", this.jsonGateway.getString("vin"));
                jsonRequest.accumulate("guid", Integer.valueOf(this.jsonGateway.getInt("guid")));
                JSONObject jsonDevice = new JSONObject();
                jsonDevice.accumulate("serial", Integer.valueOf(this.jsonGateway.getInt("serial")));
                jsonRequest.accumulate("device", jsonDevice);
                jsonRequest.accumulate("log", jsonLog);
                if (MainActivity.DebugLog) {
                    Log.i(TAG, "jsonLogRequest:" + jsonRequest.toString(STATE_CONNECT));
                }
                CommLog(STATE_UPDATE, "jsonLogRequest:" + jsonRequest.toString(STATE_CONNECT));
                ServerTask servertask = new ServerTask();
                servertask.setContext(this.mInstance.getApplicationContext());
                Log.i(TAG, "logTask:servertask.execute..");
                String[] strArr = new String[STATE_FREEZE_GATEWAY];
                strArr[STATE_IDLE] = "http://" + MainActivity.APIroute + "/api/truck/update";
                strArr[STATE_CONNECT] = jsonRequest.toString();
                servertask.execute(strArr);
                String response = (String) servertask.get(5, TimeUnit.MINUTES);
                if (MainActivity.DebugLog) {
                    Log.i(TAG, "logTask:servertask.get response=" + response);
                }
                CommLog(STATE_UPDATE, "servertask.get - finished");
                if (response.isEmpty()) {
                    responseCode = STATE_CONNECT;
                    Log.e(TAG, "ERROR: logTaskResponse is empty");
                    CommLog(STATE_UPDATE, "ERROR: logTaskResponse is empty");
                } else {
                    JSONObject jsonResponse = new JSONObject(response);
                    responseCode = jsonResponse.getInt("code");
                    if (MainActivity.DebugLog) {
                        Log.i(TAG, "jsonLogResponse:" + jsonResponse.toString(STATE_CONNECT));
                    }
                    CommLog(STATE_UPDATE, "jsonLogResponse:" + jsonResponse.toString(STATE_CONNECT));
                    if (responseCode != 10) {
                        Log.e(TAG, "*** Server Error Code: " + Integer.toString(responseCode));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            } catch (ExecutionException e3) {
                e3.printStackTrace();
            } catch (TimeoutException e4) {
                e4.printStackTrace();
            }
        }
        closeLogStream(logStream);
        deleteLogFile();
        this.mInstance.accessoryControl.openLogFile();
        this.mInstance.accessoryControl.writeLogString("End Upload");
        return responseCode;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JSONArray convertLogToJsonArray(BufferedReader r17, int r18) {
        /*
        r16 = this;
        r7 = 0;
        r11 = 0;
        r8 = r7;
    L_0x0003:
        r0 = r18;
        if (r11 >= r0) goto L_0x009f;
    L_0x0007:
        r9 = r17.readLine();	 Catch:{ Exception -> 0x0097 }
        if (r9 == 0) goto L_0x009d;
    L_0x000d:
        if (r11 != 0) goto L_0x009a;
    L_0x000f:
        r7 = new org.json.JSONArray;	 Catch:{ Exception -> 0x0097 }
        r7.<init>();	 Catch:{ Exception -> 0x0097 }
    L_0x0014:
        r12 = "";
        r3 = "";
        r13 = "";
        r1 = "";
        r14 = " ";
        r4 = r9.indexOf(r14);	 Catch:{ Exception -> 0x008e }
        if (r4 <= 0) goto L_0x0087;
    L_0x0024:
        r14 = 0;
        r12 = r9.substring(r14, r4);	 Catch:{ Exception -> 0x008e }
        r14 = "\\";
        r5 = r9.indexOf(r14);	 Catch:{ Exception -> 0x008e }
        if (r5 <= 0) goto L_0x0078;
    L_0x0031:
        r14 = r4 + 1;
        r3 = r9.substring(r14, r5);	 Catch:{ Exception -> 0x008e }
        r14 = "\\";
        r6 = r9.lastIndexOf(r14);	 Catch:{ Exception -> 0x008e }
        if (r6 <= 0) goto L_0x006b;
    L_0x003f:
        r14 = r5 + 1;
        r13 = r9.substring(r14, r6);	 Catch:{ Exception -> 0x008e }
        r14 = r6 + 1;
        r1 = r9.substring(r14);	 Catch:{ Exception -> 0x008e }
    L_0x004b:
        r10 = new org.json.JSONObject;	 Catch:{ Exception -> 0x008e }
        r10.<init>();	 Catch:{ Exception -> 0x008e }
        r14 = "timestamp";
        r10.put(r14, r12);	 Catch:{ JSONException -> 0x0089 }
        r14 = "event";
        r10.put(r14, r3);	 Catch:{ JSONException -> 0x0089 }
        r14 = "event_trigger";
        r10.put(r14, r13);	 Catch:{ JSONException -> 0x0089 }
        r14 = "event_comment";
        r10.put(r14, r1);	 Catch:{ JSONException -> 0x0089 }
    L_0x0064:
        r7.put(r10);	 Catch:{ Exception -> 0x008e }
    L_0x0067:
        r11 = r11 + 1;
        r8 = r7;
        goto L_0x0003;
    L_0x006b:
        r14 = r9.length();	 Catch:{ Exception -> 0x008e }
        r15 = r5 + 1;
        if (r14 <= r15) goto L_0x004b;
    L_0x0073:
        r13 = r9.substring(r5);	 Catch:{ Exception -> 0x008e }
        goto L_0x004b;
    L_0x0078:
        r14 = r9.length();	 Catch:{ Exception -> 0x008e }
        r15 = r4 + 1;
        if (r14 <= r15) goto L_0x004b;
    L_0x0080:
        r14 = r4 + 1;
        r3 = r9.substring(r14);	 Catch:{ Exception -> 0x008e }
        goto L_0x004b;
    L_0x0087:
        r12 = r9;
        goto L_0x004b;
    L_0x0089:
        r2 = move-exception;
        r2.printStackTrace();	 Catch:{ Exception -> 0x008e }
        goto L_0x0064;
    L_0x008e:
        r2 = move-exception;
    L_0x008f:
        r14 = "IdleSmart.HTTPClient";
        r15 = "IOException reading Log file - e=";
        android.util.Log.w(r14, r15, r2);
    L_0x0096:
        return r7;
    L_0x0097:
        r2 = move-exception;
        r7 = r8;
        goto L_0x008f;
    L_0x009a:
        r7 = r8;
        goto L_0x0014;
    L_0x009d:
        r7 = r8;
        goto L_0x0067;
    L_0x009f:
        r7 = r8;
        goto L_0x0096;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.idlesmarter.aoa.httpClient.convertLogToJsonArray(java.io.BufferedReader, int):org.json.JSONArray");
    }

    public BufferedInputStream openLogBufferedInputStream() {
        Exception e;
        BufferedInputStream bufferedInputStream = null;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Logs");
            if (path.exists()) {
                try {
                    BufferedInputStream returnstream = new BufferedInputStream(new FileInputStream(new File(path, "Log.bin")));
                    try {
                        Log.i(TAG, "Log file opened for Read");
                        return returnstream;
                    } catch (Exception e2) {
                        e = e2;
                        bufferedInputStream = returnstream;
                        Log.w(TAG, "IOException opening Log file - ioe=", e);
                        return bufferedInputStream;
                    }
                } catch (Exception e3) {
                    e = e3;
                    Log.w(TAG, "IOException opening Log file - ioe=", e);
                    return bufferedInputStream;
                }
            }
            Log.i(TAG, "ERROR: Log file directory does not exist");
            return null;
        }
        Log.w(TAG, "Error opening Log file for Read - SDCard is not mounted");
        return null;
    }

    public void closeLogStream(BufferedInputStream logStream) {
        if (logStream != null) {
            try {
                logStream.close();
            } catch (Exception e) {
                Log.w(TAG, "IOException closing Log file - e=", e);
            }
        }
    }

    public void deleteLogFile() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Logs");
            if (path.exists()) {
                try {
                    new File(path, "Log.bin").delete();
                    Log.i(TAG, "Log file deleted");
                } catch (Exception e) {
                    Log.w(TAG, "IOException deleting Log file - ioe=", e);
                }
            }
        }
    }

    public int PerformDatumTask() {
        int responseCode = PHONEHOME_ERROR;
        Log.i(TAG, "DatumTask");
        CommLog(STATE_CSC_AUTOUPDATE, "DatumTask");
        if (this.mInstance.accessoryControl.datumStream != null) {
            this.mInstance.accessoryControl.closeDatumFile();
        }
        BufferedInputStream datumStream = openDatumBufferedInputStream();
        if (datumStream == null) {
            Log.i(TAG, "Datum file does not exist or is empty");
            CommLog(STATE_CSC_AUTOUPDATE, "Datum file does not exist or is empty");
            return PHONEHOME_ERROR;
        }
        BufferedReader datumIn = new BufferedReader(new InputStreamReader(datumStream));
        while (datumIn != null) {
            JSONArray jsonDatum = convertDatumToJsonArray(datumIn, STATE_DATUM_STATUS);
            if (jsonDatum == null) {
                break;
            }
            try {
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.accumulate("guid", Integer.valueOf(this.jsonGateway.getInt("guid")));
                jsonRequest.accumulate("datum", jsonDatum);
                if (MainActivity.DebugLog) {
                    Log.i(TAG, "jsonDatumRequest:" + jsonRequest.toString(STATE_CONNECT));
                }
                CommLog(STATE_CSC_AUTOUPDATE, "jsonDatumRequest:" + jsonRequest.toString(STATE_CONNECT));
                ServerTask servertask = new ServerTask();
                servertask.setContext(this.mInstance.getApplicationContext());
                Log.i(TAG, "datumTask:servertask.execute..");
                String[] strArr = new String[STATE_FREEZE_GATEWAY];
                strArr[STATE_IDLE] = "http://" + MainActivity.APIroute + "/api/datacollection";
                strArr[STATE_CONNECT] = jsonRequest.toString();
                servertask.execute(strArr);
                String response = (String) servertask.get(5, TimeUnit.MINUTES);
                if (response.isEmpty()) {
                    responseCode = STATE_CONNECT;
                    Log.e(TAG, "ERROR: datumTaskResponse is empty");
                    CommLog(STATE_CSC_AUTOUPDATE, "ERROR: datumTaskResponse is empty");
                } else {
                    JSONObject jsonResponse = new JSONObject(response);
                    responseCode = jsonResponse.getInt("code");
                    if (MainActivity.DebugLog) {
                        Log.i(TAG, "jsonDatumResponse:" + jsonResponse.toString(STATE_CONNECT));
                    }
                    CommLog(STATE_CSC_AUTOUPDATE, "jsonDatumResponse:" + jsonResponse.toString(STATE_CONNECT));
                    if (responseCode != 10) {
                        Log.e(TAG, "*** Server Error Code: " + Integer.toString(responseCode));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            } catch (ExecutionException e3) {
                e3.printStackTrace();
            } catch (TimeoutException e4) {
                e4.printStackTrace();
            }
        }
        closeDatumStream(datumStream);
        deleteDatumFile();
        this.mInstance.accessoryControl.openDatumFile();
        return responseCode;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JSONArray convertDatumToJsonArray(BufferedReader r17, int r18) {
        /*
        r16 = this;
        r8 = 0;
        r12 = 0;
        r9 = r8;
    L_0x0003:
        r0 = r18;
        if (r12 >= r0) goto L_0x00a6;
    L_0x0007:
        r10 = r17.readLine();	 Catch:{ Exception -> 0x00a0 }
        if (r10 == 0) goto L_0x009e;
    L_0x000d:
        if (r12 != 0) goto L_0x00a3;
    L_0x000f:
        r8 = new org.json.JSONArray;	 Catch:{ Exception -> 0x00a0 }
        r8.<init>();	 Catch:{ Exception -> 0x00a0 }
    L_0x0014:
        r13 = "";
        r2 = "";
        r3 = "";
        r1 = "";
        r14 = " ";
        r5 = r10.indexOf(r14);	 Catch:{ Exception -> 0x0095 }
        if (r5 <= 0) goto L_0x008e;
    L_0x0024:
        r14 = 0;
        r13 = r10.substring(r14, r5);	 Catch:{ Exception -> 0x0095 }
        r14 = "\\";
        r6 = r10.indexOf(r14);	 Catch:{ Exception -> 0x0095 }
        if (r6 <= 0) goto L_0x007f;
    L_0x0031:
        r14 = r5 + 1;
        r2 = r10.substring(r14, r6);	 Catch:{ Exception -> 0x0095 }
        r14 = "\\";
        r7 = r10.lastIndexOf(r14);	 Catch:{ Exception -> 0x0095 }
        if (r7 <= 0) goto L_0x0072;
    L_0x003f:
        r14 = r6 + 1;
        r3 = r10.substring(r14, r7);	 Catch:{ Exception -> 0x0095 }
        r14 = r7 + 1;
        r1 = r10.substring(r14);	 Catch:{ Exception -> 0x0095 }
    L_0x004b:
        r14 = r2.isEmpty();	 Catch:{ Exception -> 0x0095 }
        if (r14 != 0) goto L_0x006e;
    L_0x0051:
        r14 = r3.isEmpty();	 Catch:{ Exception -> 0x0095 }
        if (r14 != 0) goto L_0x006e;
    L_0x0057:
        r11 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0095 }
        r11.<init>();	 Catch:{ Exception -> 0x0095 }
        r14 = "time_stamp";
        r11.put(r14, r13);	 Catch:{ JSONException -> 0x0090 }
        r14 = "datum_name";
        r11.put(r14, r2);	 Catch:{ JSONException -> 0x0090 }
        r14 = "datum_value";
        r11.put(r14, r3);	 Catch:{ JSONException -> 0x0090 }
    L_0x006b:
        r8.put(r11);	 Catch:{ Exception -> 0x0095 }
    L_0x006e:
        r12 = r12 + 1;
        r9 = r8;
        goto L_0x0003;
    L_0x0072:
        r14 = r10.length();	 Catch:{ Exception -> 0x0095 }
        r15 = r6 + 1;
        if (r14 <= r15) goto L_0x004b;
    L_0x007a:
        r1 = r10.substring(r6);	 Catch:{ Exception -> 0x0095 }
        goto L_0x004b;
    L_0x007f:
        r14 = r10.length();	 Catch:{ Exception -> 0x0095 }
        r15 = r5 + 1;
        if (r14 <= r15) goto L_0x004b;
    L_0x0087:
        r14 = r5 + 1;
        r2 = r10.substring(r14);	 Catch:{ Exception -> 0x0095 }
        goto L_0x004b;
    L_0x008e:
        r13 = r10;
        goto L_0x004b;
    L_0x0090:
        r4 = move-exception;
        r4.printStackTrace();	 Catch:{ Exception -> 0x0095 }
        goto L_0x006b;
    L_0x0095:
        r4 = move-exception;
    L_0x0096:
        r14 = "IdleSmart.HTTPClient";
        r15 = "IOException reading Datum file - e=";
        android.util.Log.w(r14, r15, r4);
    L_0x009d:
        return r8;
    L_0x009e:
        r8 = r9;
        goto L_0x009d;
    L_0x00a0:
        r4 = move-exception;
        r8 = r9;
        goto L_0x0096;
    L_0x00a3:
        r8 = r9;
        goto L_0x0014;
    L_0x00a6:
        r8 = r9;
        goto L_0x009d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.idlesmarter.aoa.httpClient.convertDatumToJsonArray(java.io.BufferedReader, int):org.json.JSONArray");
    }

    public BufferedInputStream openDatumBufferedInputStream() {
        Exception e;
        BufferedInputStream returnstream = null;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Logs");
            if (path.exists()) {
                File file = new File(path, "Datum.bin");
                if (file.length() == 0) {
                    return null;
                }
                try {
                    BufferedInputStream returnstream2 = new BufferedInputStream(new FileInputStream(file));
                    try {
                        Log.i(TAG, "Datum file opened for Read");
                        returnstream = returnstream2;
                    } catch (Exception e2) {
                        e = e2;
                        returnstream = returnstream2;
                        Log.w(TAG, "IOException opening Datum file - ioe=", e);
                        return returnstream;
                    }
                } catch (Exception e3) {
                    e = e3;
                    Log.w(TAG, "IOException opening Datum file - ioe=", e);
                    return returnstream;
                }
            }
            Log.i(TAG, "ERROR: Log file directory does not exist");
        } else {
            Log.w(TAG, "Error opening Datum file for Read - SDCard is not mounted");
        }
        return returnstream;
    }

    public void closeDatumStream(BufferedInputStream datumStream) {
        if (datumStream != null) {
            try {
                datumStream.close();
            } catch (Exception e) {
                Log.w(TAG, "IOException closing Datum file - e=", e);
            }
        }
    }

    public void deleteDatumFile() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Logs");
            if (path.exists()) {
                try {
                    new File(path, "Datum.bin").delete();
                    Log.i(TAG, "Datum file deleted");
                } catch (Exception e) {
                    Log.w(TAG, "IOException deleting Datum file - ioe=", e);
                }
            }
        }
    }

    public int str2int(String str) {
        int num = STATE_IDLE;
        try {
            num = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        return num;
    }

    public int APKUpdateExist() {
        Log.i(TAG, "<<APKUpdateExist>>");
        CommLog(STATE_VERSION, "APKUpdateExist");
        try {
            String version = this.mInstance.getPackageManager().getPackageInfo(this.mInstance.getPackageName(), STATE_IDLE).versionName;
            Log.i(TAG, "   current APK version: " + version);
            CommLog(STATE_VERSION, "   current APK version: " + version);
            try {
                if (this.jsonApkVersionStack != null) {
                    this.jsonApkVersion = null;
                    for (int i = STATE_IDLE; i < this.jsonApkVersionStack.length(); i += STATE_CONNECT) {
                        JSONObject jentry = this.jsonApkVersionStack.getJSONObject(i);
                        String feature_list = jentry.getString("feature_codes");
                        if (feature_list.trim().isEmpty() || Features.ValidateFeatureIdentityList(feature_list)) {
                            this.jsonApkVersion = jentry;
                            break;
                        }
                    }
                } else {
                    this.jsonApkVersion = this.jsonVersion.getJSONObject("recent_apk");
                }
                if (this.jsonApkVersion != null) {
                    String server_version = this.jsonApkVersion.getString("version");
                    Log.i(TAG, "   server version: " + server_version);
                    CommLog(STATE_VERSION, "   server version: " + server_version);
                    PrefUtils.setServerUpdateVersion(server_version, this.mInstance.getApplicationContext());
                    if (version.equals(server_version)) {
                        return STATE_IDLE;
                    }
                    return STATE_CONNECT;
                }
                PrefUtils.setServerUpdateVersion(BuildConfig.FLAVOR, this.mInstance.getApplicationContext());
                return STATE_IDLE;
            } catch (JSONException e) {
                e.printStackTrace();
                return PHONEHOME_ERROR;
            }
        } catch (NameNotFoundException e2) {
            e2.printStackTrace();
            return PHONEHOME_ERROR;
        }
    }

    public int PerformAPKUpdate() {
        Log.i(TAG, "PerformAPKUpdate");
        CommLog(STATE_VERSION, "PerformAPKUpdate");
        try {
            String version = this.mInstance.getPackageManager().getPackageInfo(this.mInstance.getPackageName(), STATE_IDLE).versionName;
            Log.i(TAG, "APKUpdate::current APK version: " + version);
            CommLog(STATE_VERSION, "current APK version: " + version);
            if (!APKupdate_exists) {
                return STATE_IDLE;
            }
            PrefUtils.setApkUpdateState(STATE_CONNECT, this.mInstance.getApplicationContext());
            Log.i(TAG, "-----> New Application code exists, Update the APK <-----");
            try {
                Log.w(TAG, "Send APICMD_DISCONNECT to Gateway..");
                this.mInstance.accessoryControl.writeCommand(STATE_FREEZE_GATEWAY, STATE_IDLE, STATE_IDLE);
                String APKlink = this.jsonApkVersion.getString("link_small");
                UpdateApp updateApp = new UpdateApp();
                updateApp.setContext(this.mInstance.getApplicationContext());
                Log.i(TAG, "APKUpdate:updateApp.execute..");
                String[] strArr = new String[STATE_CONNECT];
                strArr[STATE_IDLE] = APKlink;
                updateApp.execute(strArr);
                updateApp.get(60, TimeUnit.SECONDS);
                Log.i(TAG, "APKUpdate::updateApp.get - finished");
                CommLog(STATE_VERSION, "updateApp.get - finished");
                return STATE_CONNECT;
            } catch (JSONException e) {
                e.printStackTrace();
                return PHONEHOME_ERROR;
            } catch (InterruptedException e2) {
                e2.printStackTrace();
                return PHONEHOME_ERROR;
            } catch (ExecutionException e3) {
                e3.printStackTrace();
                return PHONEHOME_ERROR;
            } catch (TimeoutException e4) {
                e4.printStackTrace();
                return PHONEHOME_ERROR;
            }
        } catch (NameNotFoundException e5) {
            e5.printStackTrace();
            return PHONEHOME_ERROR;
        }
    }

    public int CSCUpdateExist() {
        Log.i(TAG, "<<CSCUpdateExist>>");
        CommLog(STATE_APKUPDATE, "CSCUpdateExist");
        Log.i(TAG, "   current CSC version: " + MainActivity.Gateway_FWversion);
        CommLog(STATE_APKUPDATE, "   current CSC version: " + MainActivity.Gateway_FWversion);
        try {
            if (this.jsonCscVersionStack != null) {
                this.jsonCscVersion = null;
                for (int i = STATE_IDLE; i < this.jsonCscVersionStack.length(); i += STATE_CONNECT) {
                    JSONObject jentry = this.jsonCscVersionStack.getJSONObject(i);
                    String feature_list = jentry.getString("feature_codes");
                    if (feature_list.trim().isEmpty() || Features.ValidateFeatureIdentityList(feature_list)) {
                        this.jsonCscVersion = jentry;
                        break;
                    }
                }
            } else {
                this.jsonCscVersion = this.jsonVersion.getJSONObject("recent_csc");
            }
            if (this.jsonCscVersion != null) {
                String server_version = this.jsonCscVersion.getString("version");
                Log.i(TAG, "   server version: " + server_version);
                CommLog(STATE_APKUPDATE, "   server version: " + server_version);
                if (!MainActivity.Gateway_FWversion.equals(server_version)) {
                    return STATE_CONNECT;
                }
            }
            return STATE_IDLE;
        } catch (JSONException e) {
            e.printStackTrace();
            return PHONEHOME_ERROR;
        }
    }

    public int PerformCSCUpdate() {
        Log.i(TAG, "PerformCSCUpdate");
        CommLog(STATE_APKUPDATE, "PerformCSCUpdate");
        Log.i(TAG, "current CSC version: " + MainActivity.Gateway_FWversion);
        CommLog(STATE_APKUPDATE, "current CSC version: " + MainActivity.Gateway_FWversion);
        if (!CSCupdate_exists) {
            return STATE_IDLE;
        }
        Log.i(TAG, "-----> New CSC firmware exists, update the CSC <-----");
        try {
            String CSClink = this.jsonCscVersion.getString("link_small");
            UpdateGateway updateGateway = new UpdateGateway();
            updateGateway.setContext(this.mInstance.getApplicationContext());
            Log.i(TAG, "CSCupdate::updateGateway.execute..");
            String[] strArr = new String[STATE_CONNECT];
            strArr[STATE_IDLE] = CSClink;
            updateGateway.execute(strArr);
            Log.i(TAG, "CSCupdate::updateGateway.get.. <==================================================================");
            updateGateway.get(60, TimeUnit.SECONDS);
            Log.i(TAG, "CSCupdate::updateGateway.get - finished");
            CommLog(STATE_APKUPDATE, "updateGateway.get - finished");
            return STATE_CONNECT;
        } catch (JSONException e) {
            e.printStackTrace();
            return PHONEHOME_ERROR;
        } catch (InterruptedException e2) {
            e2.printStackTrace();
            return PHONEHOME_ERROR;
        } catch (ExecutionException e3) {
            e3.printStackTrace();
            return PHONEHOME_ERROR;
        } catch (TimeoutException e4) {
            e4.printStackTrace();
            return PHONEHOME_ERROR;
        }
    }
}
