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
                    if (MainActivity.aMaintEnable[MainActivity.MaintenanceFeature.VIEW_SERVER_COMMUNICATION]) {
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
        this.ENABLE_APK_UPDATE = true;
        this.ENABLE_CSC_UPDATE = true;
        this.ENABLE_APK_RETROGRADE_VERSION = true;
        this.ENABLE_CSC_RETROGRADE_VERSION = true;
        this.LOG_RECORDS_BLOCK_MAX = Params.PARAM_MAX;
        this.DATUM_RECORDS_BLOCK_MAX = Params.PARAM_MAX;
        // TODO: correct assigned values: STATE_IDLE, STATE_CONNECT, STATE_FREEZE_GATEWAY
        this.APIRESPONSE_BAD = STATE_IDLE;
        this.APIRESPONSE_BADHTTP = STATE_CONNECT;
        this.APIRESPONSE_INACTIVE = STATE_FREEZE_GATEWAY;
        this.APIRESPONSE_OK = 10;
        this.ActivationFindTruck = "find_truck";
        this.ActivationCreateTruck = "create_truck";
        this.NewTruckActivation = false;
        this.deviceAPKversion = 0;
        this.deviceCSCversion = 0;
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
        this.ENABLE_APK_UPDATE = true;
        this.ENABLE_CSC_UPDATE = true;
        this.ENABLE_APK_RETROGRADE_VERSION = true;
        this.ENABLE_CSC_RETROGRADE_VERSION = true;
        this.LOG_RECORDS_BLOCK_MAX = Params.PARAM_MAX; // 25, assume
        this.DATUM_RECORDS_BLOCK_MAX = Params.PARAM_MAX; // 25, assume
        // TODO: correct assigned values: STATE_IDLE, STATE_CONNECT, STATE_FREEZE_GATEWAY
        this.APIRESPONSE_BAD = STATE_IDLE;
        this.APIRESPONSE_BADHTTP = STATE_CONNECT;
        this.APIRESPONSE_INACTIVE = STATE_FREEZE_GATEWAY;
        this.APIRESPONSE_OK = 10;
        this.ActivationFindTruck = "find_truck";
        this.ActivationCreateTruck = "create_truck";
        this.NewTruckActivation = false;
        this.deviceAPKversion = 0;
        this.deviceCSCversion = 0;
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
            return false;
        }
        Log.i(TAG, "Connected to Network");
        return true;
    }

    public void CommLog(int flag, String str) {
        if (!MainActivity.aMaintEnable[MainActivity.MaintenanceFeature.VIEW_SERVER_COMMUNICATION]) {
            return;
        }
        if (flag == STATE_CONNECT || flag == MainActivity.aMaintValue[MainActivity.MaintenanceFeature.VIEW_SERVER_COMMUNICATION] || MainActivity.aMaintValue[MainActivity.MaintenanceFeature.VIEW_SERVER_COMMUNICATION] == STATE_ERROR) {
            this.mInstance.CommLogStr(str);
        }
    }

    static {
        PhoneHomePending = false;
        phonehome_update_level = 0;
        phonehomeHandler = new Handler();
        progressUpdateRate = PROGRESS_UPDATE_RATE;
        phonehome_state = STATE_IDLE;
        APKupdate_exists = false;
        CSCupdate_exists = false;
        result = PHONEHOME_NONE; // 5, assume
    }

    public void PhoneHome(int update_level, boolean reschedule) {
        Log.i(TAG, "======> PhoneHome request <======");
        phonehome_update_level = update_level;
        phonehome_reschedule = reschedule;
        String str = TAG;
        StringBuilder append = new StringBuilder().append("     Update Level: ");
        String str2 = update_level == 1 ? "AUTO" : update_level == 3 ? "TABLET" : update_level == 2 ? "GATEWAY" : "FULL";
        Log.i(str, append.append(str2).toString());
        phonehome_state = STATE_IDLE;
        PhoneHomePending = true;
        phonehomeHandler.removeCallbacks(this.phonehomeRunnable);
        if (MainActivity.test_mode) {
            progressUpdateRate = TEST_UPDATE_RATE;
        } else {
            progressUpdateRate = PROGRESS_UPDATE_RATE;
        }
        phonehomeHandler.postDelayed(this.phonehomeRunnable, (long) progressUpdateRate);
    }

    /**
     * Write last sync time to accessory
     * @param status
     * @param synclast
     */
    public void sendSyncLast(int status, Calendar synclast) {
        Log.i(TAG, "sendSyncLast.." + status + synclast);
        byte[] bytestring = new byte[8];
        bytestring[0] = (byte) (status & 255);
        bytestring[1] = (byte) ((synclast.get(Calendar.YEAR) >> 8) & 255);
        bytestring[2] = (byte) (synclast.get(Calendar.YEAR) & 255);
        bytestring[3] = (byte) synclast.get(Calendar.MONTH); // 2, assume MONTH
        bytestring[4] = (byte) synclast.get(Calendar.DATE); // 5, assume DATE
        bytestring[5] = (byte) synclast.get(Calendar.HOUR_OF_DAY);
        bytestring[6] = (byte) synclast.get(Calendar.MINUTE);
        bytestring[7] = (byte) synclast.get(Calendar.SECOND);
        AccessoryControl accessoryControl = this.mInstance.accessoryControl;
        accessoryControl.writeCommandBlock(AccessoryControl.APIDATA_SYNC_LAST, bytestring.length, bytestring);
    }

    public void setJsonGateway() {
        try {
            this.jsonGateway = new JSONObject();
            this.jsonGateway.accumulate("vin", MainActivity.Gateway_VIN);
            this.jsonGateway.accumulate("activation_phrase", Integer.valueOf(MainActivity.ActivationCode));
            this.jsonGateway.accumulate("guid", Integer.valueOf(0));
            this.jsonGateway.accumulate("serial", "1234");
            Log.i(TAG, "jsonGateway " + this.jsonGateway.toString(1));
            CommLog(STATE_CONNECT, "jsonGateway " + this.jsonGateway.toString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calling ServerTask to get apk and csc recent version
     * @return
     * -1 if error
     * 1 if response is empty
     * 10 if success
     * others: server error code
     * Default API link: [POST] http://api.idlesmart.com/api/version
     */
    public int PerformVersionTask() {
        int responseCode = -1;
        Log.i(TAG, "VersionTask");
        CommLog(STATE_LOG, "VersionTask");
        try {
            JSONObject jsonRequest = new JSONObject();
            ServerTask servertask = new ServerTask();
            servertask.setContext(this.mInstance.getApplicationContext());
            Log.i(TAG, "versionTask:servertask.execute..");
            String[] strArr = new String[2];
            strArr[0] = "http://" + MainActivity.APIroute + "/api/version";
            strArr[1] = jsonRequest.toString();
            servertask.execute(strArr);
            Log.i(TAG, "versionTask:servertask.get..");
            String response = (String) servertask.get(60, TimeUnit.SECONDS);
            Log.i(TAG, "versionTask:servertask.get response=" + response);
            CommLog(STATE_LOG, "servertask.get - finished");
            if (response.isEmpty()) {
                Log.e(TAG, "ERROR: versionTaskResponse is empty");
                CommLog(STATE_LOG, "ERROR: versionTaskResponse is empty");
                return 1;
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
            } else {
                Log.e(TAG, "*** Server Error Code: " + Integer.toString(responseCode));
                CommLog(STATE_LOG, "jsonResponse = " + this.jsonVersion.toString(1));
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
        return responseCode;
    }

    /**
     * Calling ServerTask to activate the truck
     * @return
     * -1 if error
     * 1 if response is empty
     * 10 if success
     * others: server error code
     * Default API link: [POST] http://api.idlesmart.com/api/truck/activate
     */
    public int PerformActivationTask() {
        int responseCode = -1;
        Log.i(TAG, "ActivationTask");
        CommLog(STATE_FREEZE_GATEWAY, "ActivationTask");
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.accumulate("vin", this.jsonGateway.getString("vin"));
            jsonRequest.accumulate("activation_phrase", Integer.valueOf(this.jsonGateway.getInt("activation_phrase")));
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonActivationRequest:" + jsonRequest.toString(1));
            }
            CommLog(STATE_FREEZE_GATEWAY, "jsonActivationRequest:" + jsonRequest.toString(1));
            ServerTask servertask = new ServerTask();
            servertask.setContext(this.mInstance.getApplicationContext());
            Log.i(TAG, "activationTask:servertask.execute..");
            String[] strArr = new String[2];
            strArr[0] = "http://" + MainActivity.APIroute + "/api/truck/activate";
            strArr[1] = jsonRequest.toString();
            servertask.execute(strArr);
            if (MainActivity.DebugLog) {
                Log.i(TAG, "activationTask:servertask.get..");
            }
            String response = servertask.get(60, TimeUnit.SECONDS);
            if (MainActivity.DebugLog) {
                Log.i(TAG, "activationTask:servertask.get response=" + response);
            }
            CommLog(STATE_FREEZE_GATEWAY, "servertask.get - finished");
            if (response.isEmpty()) {
                Log.e(TAG, "ERROR: activationTaskResponse is empty");
                CommLog(STATE_FREEZE_GATEWAY, "ERROR: activationTaskResponse is empty");
                return 1;
            }
            this.jsonActivation = new JSONObject(response);
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonActivationResponse=" + this.jsonActivation.toString(1));
            }
            CommLog(STATE_FREEZE_GATEWAY, "jsonActivationResponse=" + this.jsonActivation.toString(1));
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
            } else {
                Log.e(TAG, "*** Server Error Code: " + Integer.toString(responseCode));
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
        return responseCode;
    }

    /**
     * Calling ServerTask to update truck information to server
     * @return
     * -1 if error
     * 1 if response is empty
     * 10 if success
     * others: server error code
     * Default API link: [POST] http://api.idlesmart.com/api/truck/update
     */
    public int PerformUpdateTask() {
        byte[] data = new byte[2];
        int responseCode = -1;
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
                this.jsonDeviceNode.accumulate("DRIVER_CABINCOMFORT_ENABLE", Integer.valueOf(MainActivity.aParam[Params.PARAM_CabinComfort]));
                this.jsonDeviceNode.accumulate("COLDWEATHERGUARD_ENABLE", Integer.valueOf(MainActivity.aParam[Params.PARAM_ColdWeatherGuard]));
                this.jsonDeviceNode.accumulate("BATTERYMONITOR_ENABLE", Integer.valueOf(MainActivity.aParam[Params.PARAM_BatteryProtect]));
                this.jsonDeviceNode.accumulate("DRIVER_CABINCOMFORT_SETPOINT", Integer.valueOf(MainActivity.aParam[Params.PARAM_CabinTargetTemp]));
                this.jsonDeviceNode.accumulate("CABINCOMFORT_RANGE", Integer.valueOf(MainActivity.aParam[Params.PARAM_CabinTempRange]));
                this.jsonDeviceNode.accumulate("CABINCOMFORT_AMBIENT_SETPOINT", Integer.valueOf(MainActivity.aParam[Params.PARAM_OutsideTargetTemp]));
                this.jsonDeviceNode.accumulate("CABINCOMFORT_AMBIENT_RANGE", Integer.valueOf(MainActivity.aParam[Params.PARAM_OutsideTempRange]));
                this.jsonDeviceNode.accumulate("BATTERYMONITOR_VOLTAGE", battmv2Str(MainActivity.aParam[Params.PARAM_VoltageSetPoint]));
                this.jsonDeviceNode.accumulate("BATTERYMONITOR_RUNTIME", Integer.valueOf(MainActivity.aParam[Params.PARAM_EngineRunTime]));
                this.jsonDeviceNode.accumulate("COLDWEATHERGUARD_IDEAL_COOLANT", Integer.valueOf(MainActivity.aParam[Params.PARAM_IdealCoolantTemp]));
                this.jsonDeviceNode.accumulate("COLDWEATHERGUARD_MIN_COOLANT", Integer.valueOf(MainActivity.aParam[Params.PARAM_MinCoolantTemp]));
                this.jsonDeviceNode.accumulate("COLDWEATHERGUARD_START_TEMP", Integer.valueOf(MainActivity.aParam[Params.PARAM_TemperatureSetPoint]));
                this.jsonDeviceNode.accumulate("RESTART_INTERVAL", Integer.valueOf(MainActivity.aParam[Params.PARAM_HoursBetweenStart]));
                this.jsonDeviceNode.accumulate("COMMON_SCREEN_DIM", Integer.valueOf(MainActivity.aParam[Params.PARAM_DimTabletScreen]));
                this.jsonDeviceNode.accumulate("COMMON_IDLERPM", Integer.valueOf(MainActivity.aParam[Params.PARAM_TruckRPMs]));
                this.jsonDeviceNode.accumulate("COMMON_RUNTIME", Integer.valueOf(MainActivity.aParam[Params.PARAM_TruckTimer]));
                this.jsonDeviceNode.accumulate("COMMON_DRIVER_TEMP_RANGE", Integer.valueOf(MainActivity.aParam[Params.PARAM_DriverTempCommon]));
                jsonRequest.accumulate("node_settings", this.jsonDeviceNode);
            }
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonUpdateRequest:" + jsonRequest.toString(1));
            }
            CommLog(STATE_ACTIVATE, "jsonUpdateRequest:" + jsonRequest.toString(1));
            ServerTask servertask = new ServerTask();
            servertask.setContext(this.mInstance.getApplicationContext());
            Log.i(TAG, "updateTask:servertask.execute..");
            String[] strArr = new String[2];
            strArr[0] = "http://" + MainActivity.APIroute + "/api/truck/update";
            strArr[1] = jsonRequest.toString();
            servertask.execute(strArr);
            String response = (String) servertask.get(60, TimeUnit.SECONDS);
            Log.i(TAG, "updateTask:servertask.get response=" + response);
            CommLog(STATE_ACTIVATE, "servertask.get - finished");
            if (response.isEmpty()) {
                Log.e(TAG, "ERROR: updateTaskResponse is empty");
                CommLog(STATE_ACTIVATE, "ERROR: updateTaskResponse is empty");
                return 1;
            }
            this.jsonUpdate = new JSONObject(response);
            responseCode = this.jsonUpdate.getInt("code");
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonUpdateResponse:" + this.jsonUpdate.toString(1));
            }
            CommLog(STATE_ACTIVATE, "jsonUpdateResponse:" + this.jsonUpdate.toString(1));
            if (responseCode == 10) {
                JSONObject jsonServerNode = this.jsonUpdate.getJSONObject("node_settings");
                if (jsonServerNode.getInt("CABINCOMFORT_ENABLE") == 0) {
                    this.mInstance.SaveDownloadedParamValue(Params.PARAM_FleetCabinComfort, 0);
                    this.mInstance.SaveDownloadedParamValue(Params.PARAM_CabinComfort, 0);
                } else {
                    this.mInstance.SaveDownloadedParamValue(Params.PARAM_FleetCabinComfort, 1);
                }
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_ColdWeatherGuard, jsonServerNode.getInt("COLDWEATHERGUARD_ENABLE") != 0 ? 1 : 0);
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_BatteryProtect, jsonServerNode.getInt("BATTERYMONITOR_ENABLE") != 0 ? 1 : 0);
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_FleetCabinTargetTemp, jsonServerNode.getInt("CABINCOMFORT_SETPOINT"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_CabinTempRange, jsonServerNode.getInt("CABINCOMFORT_RANGE"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_OutsideTargetTemp, jsonServerNode.getInt("CABINCOMFORT_AMBIENT_SETPOINT"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_OutsideTempRange, jsonServerNode.getInt("CABINCOMFORT_AMBIENT_RANGE"));
                int battmv = battStr2mv(jsonServerNode.getString("BATTERYMONITOR_VOLTAGE"));
                if (battmv != 0) {
                    this.mInstance.SaveDownloadedParamValue(Params.PARAM_VoltageSetPoint, battmv);
                }
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_EngineRunTime, jsonServerNode.getInt("BATTERYMONITOR_RUNTIME"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_IdealCoolantTemp, jsonServerNode.getInt("COLDWEATHERGUARD_IDEAL_COOLANT"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_MinCoolantTemp, jsonServerNode.getInt("COLDWEATHERGUARD_MIN_COOLANT"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_TemperatureSetPoint, jsonServerNode.getInt("COLDWEATHERGUARD_START_TEMP"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_HoursBetweenStart, jsonServerNode.getInt("COLDWEATHERGUARD_RESTART_INTERVAL"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_DimTabletScreen, jsonServerNode.getInt("COMMON_SCREEN_DIM"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_TruckRPMs, jsonServerNode.getInt("COMMON_IDLERPM"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_TruckTimer, jsonServerNode.getInt("COMMON_RUNTIME"));
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_DriverTempCommon, jsonServerNode.getInt("COMMON_DRIVER_TEMP_RANGE"));
                int temp_passwordenable = jsonServerNode.getInt("COMMON_PROHIBIT_DRIVER_EDITS");
                MainActivity.PasswordEnable = temp_passwordenable != 0 ? true : false;
                this.mInstance.SaveDownloadedParamValue(19, temp_passwordenable);
                if (jsonServerNode.getString("COMMON_DRIVER_UNLOCK_CODE").isEmpty()) {
                    MainActivity.Password = 5555;
                } else {
                    MainActivity.Password = jsonServerNode.getInt("COMMON_DRIVER_UNLOCK_CODE");
                }
                // 20, assume Params.PARAM_Password
                this.mInstance.SaveDownloadedParamValue(Params.PARAM_Password, MainActivity.Password);
                boolean newSync = false;
                if (jsonServerNode.has("COMMON_TIME_TO_LIVE") && !jsonServerNode.isNull("COMMON_TIME_TO_LIVE")) {
                    int newSyncTTL = jsonServerNode.getInt("COMMON_TIME_TO_LIVE");
                    Log.i(TAG, "COMMON_TIME_TO_LIVE = " + newSyncTTL);
                    if (newSyncTTL > 1440) {
                        newSyncTTL = 1440;
                    }
                    if (newSyncTTL != MainActivity.SyncTTL) {
                        Log.i(TAG, "New TTL - we need a recalc");
                        MainActivity.SyncTTL = newSyncTTL;
                        newSync = true;
                        data[0] = (byte) ((newSyncTTL >> 8) & 255);
                        data[1] = (byte) (newSyncTTL & 255);
                        this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_SYNC_TTL, data[0], data[1]);
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
                        newSync = true;
                        data[0] = (byte) ((newSyncStart >> 8) & 255);
                        data[1] = (byte) (newSyncStart & 255);
                        this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_SYNC_START, data[0], data[1]);
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
            } else {
                Log.e(TAG, "*** Server Error Code: " + Integer.toString(responseCode));
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
        return responseCode;
    }

    // TODO: Move to Util class
    public int battStr2mv(String battstr) {
        try {
            return (Integer.parseInt(battstr.substring(0, 2)) * 10) + Integer.parseInt(battstr.substring(3, 4));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // TODO: Move to Util class
    public String battmv2Str(int battvolt) {
        int volts = battvolt / 10;
        String voltstr = Integer.toString(volts);
        return voltstr + "." + Integer.toString(battvolt - (volts * 10));
    }

    /**
     * Calling ServerTask to upload log from external storage to server
     * @return
     * -1 if error
     * 1 if response is empty
     * 10 if success
     * others: server error code
     * Default API link: [POST] http://api.idlesmart.com/api/truck/update
     */
    public int PerformLogTask() {
        int responseCode = -1;
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
            return -1;
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
                    Log.i(TAG, "jsonLogRequest:" + jsonRequest.toString(1));
                }
                CommLog(STATE_UPDATE, "jsonLogRequest:" + jsonRequest.toString(1));
                ServerTask servertask = new ServerTask();
                servertask.setContext(this.mInstance.getApplicationContext());
                Log.i(TAG, "logTask:servertask.execute..");
                String[] strArr = new String[2];
                strArr[0] = "http://" + MainActivity.APIroute + "/api/truck/update";
                strArr[1] = jsonRequest.toString();
                servertask.execute(strArr);
                String response = servertask.get(5, TimeUnit.MINUTES);
                if (MainActivity.DebugLog) {
                    Log.i(TAG, "logTask:servertask.get response=" + response);
                }
                CommLog(STATE_UPDATE, "servertask.get - finished");
                if (response.isEmpty()) {
                    responseCode = 1;
                    Log.e(TAG, "ERROR: logTaskResponse is empty");
                    CommLog(STATE_UPDATE, "ERROR: logTaskResponse is empty");
                } else {
                    JSONObject jsonResponse = new JSONObject(response);
                    responseCode = jsonResponse.getInt("code");
                    if (MainActivity.DebugLog) {
                        Log.i(TAG, "jsonLogResponse:" + jsonResponse.toString(1));
                    }
                    CommLog(STATE_UPDATE, "jsonLogResponse:" + jsonResponse.toString(1));
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

    /**
     * Read log file store in external storage
     * @return BufferedInputStream object
     */
    public BufferedInputStream openLogBufferedInputStream() {
        BufferedInputStream bufferedInputStream = null;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Logs");
            if (path.exists()) {
                try {
                    Log.i(TAG, "Log file opened for Read");
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(path, "Log.bin")));
                } catch (Exception e) {
                    Log.w(TAG, "IOException opening Log file - ioe=", e);
                }
            } else {
                Log.i(TAG, "ERROR: Log file directory does not exist");
            }
        } else {
            Log.w(TAG, "Error opening Log file for Read - SDCard is not mounted");
        }
        return bufferedInputStream;
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

    /**
     * Calling ServerTask to get data from server
     * @return
     * -1 if error
     * 1 if response is empty
     * 10 if success
     * others: server error code
     * Default API link: [POST] http://api.idlesmart.com/api/datacollection
     */
    public int PerformDatumTask() {
        int responseCode = -1;
        Log.i(TAG, "DatumTask");
        CommLog(STATE_CSC_AUTOUPDATE, "DatumTask");
        if (this.mInstance.accessoryControl.datumStream != null) {
            this.mInstance.accessoryControl.closeDatumFile();
        }
        BufferedInputStream datumStream = openDatumBufferedInputStream();
        if (datumStream == null) {
            Log.i(TAG, "Datum file does not exist or is empty");
            CommLog(STATE_CSC_AUTOUPDATE, "Datum file does not exist or is empty");
            return responseCode;
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
                    Log.i(TAG, "jsonDatumRequest:" + jsonRequest.toString(1));
                }
                CommLog(STATE_CSC_AUTOUPDATE, "jsonDatumRequest:" + jsonRequest.toString(1));
                ServerTask servertask = new ServerTask();
                servertask.setContext(this.mInstance.getApplicationContext());
                Log.i(TAG, "datumTask:servertask.execute..");
                String[] strArr = new String[2];
                strArr[0] = "http://" + MainActivity.APIroute + "/api/datacollection";
                strArr[1] = jsonRequest.toString();
                servertask.execute(strArr);
                String response = servertask.get(5, TimeUnit.MINUTES);
                if (response.isEmpty()) {
                    responseCode = 1;
                    Log.e(TAG, "ERROR: datumTaskResponse is empty");
                    CommLog(STATE_CSC_AUTOUPDATE, "ERROR: datumTaskResponse is empty");
                } else {
                    JSONObject jsonResponse = new JSONObject(response);
                    responseCode = jsonResponse.getInt("code");
                    if (MainActivity.DebugLog) {
                        Log.i(TAG, "jsonDatumResponse:" + jsonResponse.toString(1));
                    }
                    CommLog(STATE_CSC_AUTOUPDATE, "jsonDatumResponse:" + jsonResponse.toString(1));
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
        // TODO: Move closeDatumStream, deleteDatumFile, openDatumFile into single class
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

    /**
     * Open datum BufferedInputStream
     * @return BufferedInputStream object
     */
    public BufferedInputStream openDatumBufferedInputStream() {
        BufferedInputStream returnstream = null;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Logs");
            if (path.exists()) {
                File file = new File(path, "Datum.bin");
                if (file.length() == 0) {
                    return null;
                }
                try {
                    Log.i(TAG, "Datum file opened for Read");
                    returnstream = new BufferedInputStream(new FileInputStream(file));
                } catch (Exception e) {
                    Log.w(TAG, "IOException opening Datum file - ioe=", e);
                }
            } else {
                Log.i(TAG, "ERROR: Log file directory does not exist");
            }
        } else {
            Log.w(TAG, "Error opening Datum file for Read - SDCard is not mounted");
        }
        return returnstream;
    }

    /**
     * Close a buffered input stream
     * @param datumStream
     */
    public void closeDatumStream(BufferedInputStream datumStream) {
        if (datumStream != null) {
            try {
                datumStream.close();
            } catch (Exception e) {
                Log.w(TAG, "IOException closing Datum file - e=", e);
            }
        }
    }

    /**
     * Delete data file in Logs
     */
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

    /**
     * Check there is APK update or not
     * @return
     * 0 if no update
     * 1 if has update
     * -1 if there is an exception
     */
    public int APKUpdateExist() {
        Log.i(TAG, "<<APKUpdateExist>>");
        CommLog(STATE_VERSION, "APKUpdateExist");
        try {
            String version = this.mInstance.getPackageManager().getPackageInfo(this.mInstance.getPackageName(), 0).versionName;
            Log.i(TAG, "   current APK version: " + version);
            CommLog(STATE_VERSION, "   current APK version: " + version);
            try {
                if (this.jsonApkVersionStack != null) {
                    this.jsonApkVersion = null;
                    // get first valid apk in stack (latest)
                    for (int i = 0; i < this.jsonApkVersionStack.length(); i += 1) {
                        JSONObject jentry = this.jsonApkVersionStack.getJSONObject(i);
                        String feature_list = jentry.getString("feature_codes");
                        if (feature_list.trim().isEmpty() || Features.ValidateFeatureIdentityList(feature_list)) {
                            this.jsonApkVersion = jentry;
                            break;
                        }
                    }
                } else {
                    // get recent apk
                    this.jsonApkVersion = this.jsonVersion.getJSONObject("recent_apk");
                }
                if (this.jsonApkVersion != null) {
                    String server_version = this.jsonApkVersion.getString("version");
                    Log.i(TAG, "   server version: " + server_version);
                    CommLog(STATE_VERSION, "   server version: " + server_version);
                    PrefUtils.setServerUpdateVersion(server_version, this.mInstance.getApplicationContext());
                    // if current apk version is up to date
                    if (version.equals(server_version)) {
                        return 0;
                    }
                    return 1;
                }
                PrefUtils.setServerUpdateVersion(BuildConfig.FLAVOR, this.mInstance.getApplicationContext());
                return 0;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (NameNotFoundException e2) {
            e2.printStackTrace();
        }
        return -1;
    }

    /**
     * Execute UpdateApp task
     * @return
     * 0 if no update
     * 1 if task finished
     * -1 if there is an exception
     */
    public int PerformAPKUpdate() {
        Log.i(TAG, "PerformAPKUpdate");
        CommLog(STATE_VERSION, "PerformAPKUpdate");
        try {
            String version = this.mInstance.getPackageManager().getPackageInfo(this.mInstance.getPackageName(), 0).versionName;
            Log.i(TAG, "APKUpdate::current APK version: " + version);
            CommLog(STATE_VERSION, "current APK version: " + version);
            if (!APKupdate_exists) {
                return 0;
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
                String[] strArr = { APKlink };
                updateApp.execute(strArr);
                updateApp.get(60, TimeUnit.SECONDS);
                Log.i(TAG, "APKUpdate::updateApp.get - finished");
                CommLog(STATE_VERSION, "updateApp.get - finished");
                return 1;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            } catch (ExecutionException e3) {
                e3.printStackTrace();
            } catch (TimeoutException e4) {
                e4.printStackTrace();
            }
        } catch (NameNotFoundException e5) {
            e5.printStackTrace();
        }
        return -1;
    }

    /**
     * Check there is firmware update or not
     * @return
     * 0 if no update
     * 1 if has update
     * -1 if there is an exception
     */
    public int CSCUpdateExist() {
        Log.i(TAG, "<<CSCUpdateExist>>");
        CommLog(STATE_APKUPDATE, "CSCUpdateExist");
        Log.i(TAG, "   current CSC version: " + MainActivity.Gateway_FWversion);
        CommLog(STATE_APKUPDATE, "   current CSC version: " + MainActivity.Gateway_FWversion);
        try {
            if (this.jsonCscVersionStack != null) {
                this.jsonCscVersion = null;
                // get first valid csc in stack (latest)
                for (int i = 0; i < this.jsonCscVersionStack.length(); i += 1) {
                    JSONObject jentry = this.jsonCscVersionStack.getJSONObject(i);
                    String feature_list = jentry.getString("feature_codes");
                    if (feature_list.trim().isEmpty() || Features.ValidateFeatureIdentityList(feature_list)) {
                        this.jsonCscVersion = jentry;
                        break;
                    }
                }
            } else {
                // get recent csc
                this.jsonCscVersion = this.jsonVersion.getJSONObject("recent_csc");
            }
            if (this.jsonCscVersion != null) {
                String server_version = this.jsonCscVersion.getString("version");
                Log.i(TAG, "   server version: " + server_version);
                CommLog(STATE_APKUPDATE, "   server version: " + server_version);
                // if current firmware version is different to server
                if (!MainActivity.Gateway_FWversion.equals(server_version)) {
                    return 1;
                }
            }
            return 0;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Execute UpdateGateway task
     * @return
     * 0 if no update
     * 1 if task finished
     * -1 if there is an exception
     */
    public int PerformCSCUpdate() {
        Log.i(TAG, "PerformCSCUpdate");
        CommLog(STATE_APKUPDATE, "PerformCSCUpdate");
        Log.i(TAG, "current CSC version: " + MainActivity.Gateway_FWversion);
        CommLog(STATE_APKUPDATE, "current CSC version: " + MainActivity.Gateway_FWversion);
        if (!CSCupdate_exists) {
            return 0;
        }
        Log.i(TAG, "-----> New CSC firmware exists, update the CSC <-----");
        try {
            String CSClink = this.jsonCscVersion.getString("link_small");
            UpdateGateway updateGateway = new UpdateGateway();
            updateGateway.setContext(this.mInstance.getApplicationContext());
            Log.i(TAG, "CSCupdate::updateGateway.execute..");
            String[] strArr = new String[1];
            strArr[0] = CSClink;
            updateGateway.execute(strArr);
            Log.i(TAG, "CSCupdate::updateGateway.get.. <==================================================================");
            updateGateway.get(60, TimeUnit.SECONDS);
            Log.i(TAG, "CSCupdate::updateGateway.get - finished");
            CommLog(STATE_APKUPDATE, "updateGateway.get - finished");
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        } catch (ExecutionException e3) {
            e3.printStackTrace();
        } catch (TimeoutException e4) {
            e4.printStackTrace();
        }
        return -1;
    }
}
