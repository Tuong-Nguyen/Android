package vn.com.tma.idlesmart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import vn.com.tma.idlesmart.Utils.BatteryVoltageConverter;
import vn.com.tma.idlesmart.Utils.LogFile;
import vn.com.tma.idlesmart.Utils.PrefUtils;
import vn.com.tma.idlesmart.params.PhoneHomeState;
import vn.com.tma.idlesmart.params.PhoneHomeSyncStatus;
import vn.com.tma.idlesmart.tasks.ServerTask;
import vn.com.tma.idlesmart.tasks.UpdateApp;
import vn.com.tma.idlesmart.tasks.UpdateGateway;

public class httpClient extends Activity {
    private static final int MAINT_JSON_GUID_REQUEST = 1;
    private static final int MAINT_JSON_ACTIVATION = 2;
    private static final int MAINT_JSON_UPDATE = 3;
    private static final int MAINT_JSON_LOG = 4;
    private static final int MAINT_JSON_VERSION = 5;
    private static final int MAINT_JSON_APK_UPDATE = 6;
    private static final int MAINT_JSON_CSC_UPDATE = 7;
    private static final int MAINT_JSON_VERSION_ARRAY = 8;
    private static final int MAINT_JSON_DATUM = 9;
    private static final int MAINT_JSON_ALL = 99;

    public static final int PHONEHOME_TIMEOUT = -3;
    public static final int PHONEHOME_NONETWORK = -2;
    public static final int PHONEHOME_ERROR = -1;

    public static final int PHONEHOME_OK = 1;

    public static final int PHONEHOME_FULL_UPDATE = 0;
    public static final int PHONEHOME_AUTO_UPDATE = 1;
    public static final int PHONEHOME_GATEWAY_UPDATE = 2;
    public static final int PHONEHOME_TABLET_UPDATE = 3;

    public static final int PHONEHOME_IDLE = 0;
    public static final int PHONEHOME_CSC_PENDING = 2;
    public static final int PHONEHOME_PENDING = 3;
    public static final int PHONEHOME_APK_PENDING = 4;
    public static final int PHONEHOME_NONE = 5;

    public static final boolean PHONEHOME_NO_RESCHEDULE = false;
    public static final boolean PHONEHOME_RESCHEDULE = true;

    private static final int PROGRESS_UPDATE_RATE = 100;

    private static final String TAG = "IdleSmart.HTTPClient";
    private static final int TEST_UPDATE_RATE = 1000;

    private static boolean APKupdate_exists = false;
    private static boolean CSCupdate_exists = false;

    public static boolean PhoneHomePending = false;

    private static Context context;
    static final Handler phonehomeHandler;
    private static boolean phonehome_reschedule;
    private static int phonehome_state;
    private static int phonehome_update_level;
    private static int progressUpdateRate;
    private static int result;

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
            boolean z = false;
            int status;
            switch (httpClient.phonehome_state) {
                case PhoneHomeState.IDLE /*0*/:
                    httpClient.result = PhoneHomeSyncStatus.PENDING;
                    MainActivity.SyncLast_Status = PhoneHomeSyncStatus.PENDING;
                    MainActivity.SyncLast = Calendar.getInstance();
                    httpClient.this.mInstance.UpdateConnectivityStatus();
                    httpClient.this.dialog = new ProgressDialog(httpClient.this.mInstance);
                    httpClient.this.dialog.setIndeterminate(true);
                    httpClient.this.dialog.setCancelable(true);
                    httpClient.this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    httpClient.this.dialog.setTitle("Idle Smart Refresh");
                    httpClient.this.dialog.setMessage("Connecting to server");
                    httpClient.this.dialog.show();
                    httpClient.phonehome_state = PhoneHomeState.CONNECT;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.CONNECT /*1*/:
                    if (httpClient.this.isConnected()) {
                        httpClient.this.dialog.setMessage("Stopping Gateway");
                        httpClient.phonehome_state = PhoneHomeState.FREEZE_GATEWAY;
                    } else {
                        httpClient.this.dialog.setMessage("No Network Connection");
                        httpClient.result = PhoneHomeSyncStatus.NONE_NETWORK;
                        httpClient.phonehome_state = PhoneHomeState.ERROR;
                    }
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.FREEZE_GATEWAY /*2*/:
                    if (MainActivity.aMaintEnable[MainActivity.MaintenanceFeature.VIEW_SERVER_COMMUNICATION]) {
                        httpClient.this.mInstance.openCommDialog();
                    }
                    Log.w(httpClient.TAG, "send APICMD_FREEZE(true)..");
                    httpClient.this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_FREEZE, 0, 1);
                    httpClient.this.dialog.setMessage("Identifying/Activating Vehicle");
                    httpClient.phonehome_state = PhoneHomeState.ACTIVATE;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.ACTIVATE /*3*/:
                    Log.i(httpClient.TAG, "       setJsonGateway..");
                    httpClient.this.setJsonGateway();
                    Log.i(httpClient.TAG, "       Activation..");
                    httpClient.this.PerformActivationTask();
                    if (httpClient.phonehome_update_level == PhoneHomeState.FREEZE_GATEWAY || httpClient.phonehome_update_level == PhoneHomeState.ACTIVATE) {
                        httpClient.this.dialog.setMessage("Checking for software updates");
                        httpClient.phonehome_state = PhoneHomeState.VERSION;
                    } else {
                        httpClient.this.dialog.setMessage("Updating Settings with Fleet Dashboard");
                        httpClient.phonehome_state = PhoneHomeState.UPDATE;
                    }
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.UPDATE /*4*/:
                    Log.i(httpClient.TAG, "       Update..");
                    httpClient.this.PerformUpdateTask();
                    httpClient.this.dialog.setMessage("Sending Vehicle Logs");
                    httpClient.phonehome_state = PhoneHomeState.LOG;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.LOG /*5*/:
                    Log.i(httpClient.TAG, "       Log..");
                    httpClient.this.PerformLogTask();
                    httpClient.phonehome_state = PhoneHomeState.LOG_STATUS;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.VERSION /*6*/:
                    Log.i(httpClient.TAG, "       Version..");
                    httpClient.this.PerformVersionTask();
                    httpClient.APKupdate_exists = httpClient.this.APKUpdateExist() == 1;
                    Log.i(httpClient.TAG, "       APKUpdateExist? " + (httpClient.APKupdate_exists ? "true" : "false"));
                    if (httpClient.this.CSCUpdateExist() == 1) {
                        z = true;
                    }
                    httpClient.CSCupdate_exists = z;
                    Log.i(httpClient.TAG, "       CSCUpdateExist? " + (httpClient.CSCupdate_exists ? "true" : "false"));
                    if (httpClient.phonehome_update_level == PhoneHomeState.FREEZE_GATEWAY) {
                        if (httpClient.CSCupdate_exists) {
                            httpClient.this.dialog.setMessage("Updating Gateway firmware");
                            httpClient.phonehome_state = PhoneHomeState.CSC_AUTO_UPDATE;
                        } else {
                            httpClient.result = PhoneHomeSyncStatus.NONE;
                            httpClient.phonehome_state = PhoneHomeState.DONE;
                        }
                    } else if (httpClient.phonehome_update_level == PhoneHomeState.ACTIVATE) {
                        if (httpClient.APKupdate_exists) {
                            httpClient.this.dialog.setMessage("Updating Tablet Application");
                            httpClient.phonehome_state = PhoneHomeState.APK_UPDATE;
                        } else {
                            httpClient.result = PhoneHomeSyncStatus.NONE;
                            httpClient.phonehome_state = PhoneHomeState.DONE;
                        }
                    } else if (httpClient.phonehome_update_level == PhoneHomeState.CONNECT) {
                        if (httpClient.CSCupdate_exists) {
                            httpClient.this.dialog.setMessage("Updating Gateway firmware");
                            httpClient.phonehome_state = PhoneHomeState.CSC_UPDATE;
                        } else {
                            if (httpClient.APKupdate_exists) {
                                httpClient.result = PhoneHomeSyncStatus.APK_PENDING;
                            } else {
                                httpClient.result = PhoneHomeSyncStatus.OK;
                            }
                            httpClient.phonehome_state = PhoneHomeState.DONE;
                        }
                    } else if (httpClient.APKupdate_exists) {
                        httpClient.this.dialog.setMessage("Updating Tablet Application");
                        httpClient.phonehome_state = PhoneHomeState.APK_UPDATE;
                    } else if (httpClient.CSCupdate_exists) {
                        httpClient.this.dialog.setMessage("Updating Gateway firmware");
                        httpClient.phonehome_state = PhoneHomeState.CSC_UPDATE;
                    } else {
                        httpClient.result = PhoneHomeSyncStatus.OK;
                        httpClient.phonehome_state = PhoneHomeState.DONE;
                    }
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.APK_UPDATE /*7*/:
                    httpClient.result = PhoneHomeSyncStatus.OK;
                    if (httpClient.APKupdate_exists) {
                        if (httpClient.CSCupdate_exists) {
                            Log.w(httpClient.TAG, "Send APICMD_DL(2)..(delayed CSC update)");
                            httpClient.this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_DL, 0, 1);
                        }
                        status = httpClient.this.PerformAPKUpdate();
                        if (status == 0) {
                            httpClient.result = PhoneHomeSyncStatus.OK;
                        } else if (status == 1) {
                            if (MainActivity.PackageUpdatePending) {
                                httpClient.result = PhoneHomeSyncStatus.APK_PENDING;
                            }
                        } else if (status < 0) {
                            httpClient.result = PhoneHomeSyncStatus.ERROR;
                        }
                        if (httpClient.result == PhoneHomeSyncStatus.OK && httpClient.CSCupdate_exists) {
                            httpClient.result = PhoneHomeSyncStatus.GATEWAY_UPDATE;
                        }
                        httpClient.phonehome_state = PhoneHomeState.DONE;
                    }
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                    break;
                case PhoneHomeState.CSC_UPDATE /*8*/:
                case PhoneHomeState.CSC_AUTO_UPDATE /*9*/:
                    if (httpClient.CSCupdate_exists) {
                        status = httpClient.this.PerformCSCUpdate();
                        if (status == 0) {
                            httpClient.result = PhoneHomeSyncStatus.NONE;
                        } else if (status < 0) {
                            httpClient.result = PhoneHomeSyncStatus.ERROR;
                        } else {
                            httpClient.result = PhoneHomeSyncStatus.OK;
                        }
                    } else {
                        httpClient.result = PhoneHomeSyncStatus.NONE;
                    }
                    httpClient.phonehome_state = PhoneHomeState.DONE;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.LOG_STATUS /*15*/:
                    httpClient.this.dialog.setMessage("Sending Collected Data");
                    httpClient.phonehome_state = PhoneHomeState.DATUM;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.DATUM /*20*/:
                    Log.i(httpClient.TAG, "       Datum..");
                    httpClient.this.PerformDatumTask();
                    httpClient.phonehome_state = PhoneHomeState.DATUM_STATUS;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.DATUM_STATUS /*25*/:
                    httpClient.this.dialog.setMessage("Checking for software updates");
                    httpClient.phonehome_state = PhoneHomeState.VERSION;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, (long) httpClient.progressUpdateRate);
                	break;
				case PhoneHomeState.DONE /*90*/:
                    break;
                case PhoneHomeState.ERROR /*99*/:
                    MainActivity.SyncLast_Status = httpClient.result;
                    httpClient.this.mInstance.UpdateConnectivityStatus();
                    String resched_msg = httpClient.phonehome_reschedule == httpClient.PHONEHOME_RESCHEDULE ? "\n\nRefresh has been rescheduled" : "";
                    switch (httpClient.result) {
                        case PhoneHomeSyncStatus.TIMEOUT /*-3*/:
                            httpClient.this.dialog.setMessage("Server dashboard not responding" + resched_msg);
                            break;
                        case PhoneHomeSyncStatus.NONE_NETWORK /*-2*/:
                            httpClient.this.dialog.setMessage("Cannot connect to network" + resched_msg);
                            break;
                        case PhoneHomeSyncStatus.ERROR /*-1*/:
                            httpClient.this.dialog.setMessage("Errors encountered during refresh" + resched_msg);
                            break;
                        case PhoneHomeState.CONNECT /*1*/:
                            MainActivity.SyncLast = Calendar.getInstance();
                            httpClient com_idlesmarter_aoa_httpClient = httpClient.this;
                            int access$100 = httpClient.result;
                            com_idlesmarter_aoa_httpClient.sendSyncLast(access$100, MainActivity.SyncLast);
                            httpClient.this.dialog.setMessage("Refresh completed");
                            break;
                        case PhoneHomeState.FREEZE_GATEWAY /*2*/:
                            httpClient.this.dialog.setMessage("Gateway update is in progress");
                            break;
                        case PhoneHomeState.ACTIVATE /*3*/:
                            httpClient.this.dialog.setMessage("Refresh is in progress");
                            break;
                        case PhoneHomeState.UPDATE /*4*/:
                            httpClient.this.dialog.setMessage("Tablet update is in progress");
                            break;
                        case PhoneHomeState.LOG /*5*/:
                            httpClient.this.dialog.setMessage("No updates found");
                            break;
                    }
                    httpClient.PhoneHomePending = false;
                    if (httpClient.result != PhoneHomeSyncStatus.APK_PENDING) {
                        Log.w(httpClient.TAG, "send APICMD_FREEZE(false)..");
                        httpClient.this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_FREEZE, 0, 0);
                    }
                    if (httpClient.phonehome_reschedule == httpClient.PHONEHOME_RESCHEDULE) {
                        httpClient.this.mInstance.ReschedulePhoneHome(60);
                    }
                    httpClient.phonehome_state = PhoneHomeState.CLEANUP;
                    httpClient.phonehomeHandler.postDelayed(httpClient.this.phonehomeRunnable, 3000);
                	break;
				case PhoneHomeState.CLEANUP /*100*/:
                    httpClient.this.dialog.dismiss();
                    httpClient.phonehome_state = PhoneHomeState.IDLE;
                    httpClient.phonehomeHandler.removeCallbacks(httpClient.this.phonehomeRunnable);
                    httpClient.PhoneHomePending = false;
                    if (httpClient.result == PhoneHomeSyncStatus.APK_PENDING) {
                        httpClient.this.mInstance.exit();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public httpClient(MainActivity act) {
        this.httpStatus = 0;
        this.ENABLE_APK_UPDATE = true;
        this.ENABLE_CSC_UPDATE = true;
        this.ENABLE_APK_RETROGRADE_VERSION = true;
        this.ENABLE_CSC_RETROGRADE_VERSION = true;
        this.LOG_RECORDS_BLOCK_MAX = Params.PARAM_MAX;
        this.DATUM_RECORDS_BLOCK_MAX = Params.PARAM_MAX;
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
        this.httpStatus = 0;
        this.ENABLE_APK_UPDATE = true;
        this.ENABLE_CSC_UPDATE = true;
        this.ENABLE_APK_RETROGRADE_VERSION = true;
        this.ENABLE_CSC_RETROGRADE_VERSION = true;
        this.LOG_RECORDS_BLOCK_MAX = Params.PARAM_MAX; // 25, assume
        this.DATUM_RECORDS_BLOCK_MAX = Params.PARAM_MAX; // 25, assume
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
        String line = "";
        String result = "";
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
        if (flag == PhoneHomeState.CONNECT || flag == MainActivity.aMaintValue[MainActivity.MaintenanceFeature.VIEW_SERVER_COMMUNICATION] || MainActivity.aMaintValue[MainActivity.MaintenanceFeature.VIEW_SERVER_COMMUNICATION] == PhoneHomeState.ERROR) {
            this.mInstance.CommLogStr(str);
        }
    }

    static {
        PhoneHomePending = false;
        phonehome_update_level = 0;
        phonehomeHandler = new Handler();
        progressUpdateRate = PROGRESS_UPDATE_RATE;
        phonehome_state = PhoneHomeState.IDLE;
        APKupdate_exists = false;
        CSCupdate_exists = false;
        // Assume <result> is an "enum" PhoneHomeSyncStatus
        result = PhoneHomeSyncStatus.NONE;
    }

    /**
     *
     * @param update_level
     *  1 - Auto
     *  2 - Gateway
     *  3 - Tablet
     *  Other - All
     * @param reschedule
     */
    public void PhoneHome(int update_level, boolean reschedule) {
        Log.i(TAG, "======> PhoneHome request <======");
        phonehome_update_level = update_level;
        phonehome_reschedule = reschedule;
        String str = TAG;
        StringBuilder append = new StringBuilder().append("     Update Level: ");
        String str2 = update_level == 1 ? "AUTO" : update_level == 3 ? "TABLET" : update_level == 2 ? "GATEWAY" : "FULL";
        Log.i(str, append.append(str2).toString());
        phonehome_state = PhoneHomeState.IDLE;
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
            CommLog(PhoneHomeState.CONNECT, "jsonGateway " + this.jsonGateway.toString(1));
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
        CommLog(PhoneHomeState.LOG, "VersionTask");
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
            CommLog(PhoneHomeState.LOG, "servertask.get - finished");
            if (response.isEmpty()) {
                Log.e(TAG, "ERROR: versionTaskResponse is empty");
                CommLog(PhoneHomeState.LOG, "ERROR: versionTaskResponse is empty");
                return 1;
            }
            this.jsonVersion = new JSONObject(response);
            Log.i(TAG, "Versions:" + this.jsonVersion.toString());
            CommLog(PhoneHomeState.CSC_UPDATE, "Versions:" + this.jsonVersion.toString());
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
                CommLog(PhoneHomeState.LOG, "jsonResponse = " + this.jsonVersion.toString(1));
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
        CommLog(PhoneHomeState.FREEZE_GATEWAY, "ActivationTask");
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.accumulate("vin", this.jsonGateway.getString("vin"));
            jsonRequest.accumulate("activation_phrase", Integer.valueOf(this.jsonGateway.getInt("activation_phrase")));
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonActivationRequest:" + jsonRequest.toString(1));
            }
            CommLog(PhoneHomeState.FREEZE_GATEWAY, "jsonActivationRequest:" + jsonRequest.toString(1));
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
            CommLog(PhoneHomeState.FREEZE_GATEWAY, "servertask.get - finished");
            if (response.isEmpty()) {
                Log.e(TAG, "ERROR: activationTaskResponse is empty");
                CommLog(PhoneHomeState.FREEZE_GATEWAY, "ERROR: activationTaskResponse is empty");
                return 1;
            }
            this.jsonActivation = new JSONObject(response);
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonActivationResponse=" + this.jsonActivation.toString(1));
            }
            CommLog(PhoneHomeState.FREEZE_GATEWAY, "jsonActivationResponse=" + this.jsonActivation.toString(1));
            responseCode = this.jsonActivation.getInt("code");
            if (responseCode == 10) {
                this.jsonGateway.put("guid", this.jsonActivation.getInt("guid"));
                this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_ACTIVATE, 0, 2);
                Log.i(TAG, "APICMD_ACTIVATE = 2");
                this.NewTruckActivation = this.jsonActivation.getString("route_type").equals("create_truck");
                if (!this.jsonActivation.has("fleet_name") || this.jsonActivation.isNull("fleet_name")) {
                    MainActivity.Gateway_Fleet = "";
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
        // TODO new BatteryVoltageConverter object;
        BatteryVoltageConverter batteryVoltageConverter = new BatteryVoltageConverter();

        CommLog(PhoneHomeState.ACTIVATE, "UpdateTask");
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
                this.jsonDeviceNode.accumulate("BATTERYMONITOR_VOLTAGE", batteryVoltageConverter.batteryMilliVoltToString(MainActivity.aParam[Params.PARAM_VoltageSetPoint]));
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
            CommLog(PhoneHomeState.ACTIVATE, "jsonUpdateRequest:" + jsonRequest.toString(1));
            ServerTask servertask = new ServerTask();
            servertask.setContext(this.mInstance.getApplicationContext());
            Log.i(TAG, "updateTask:servertask.execute..");
            String[] strArr = new String[2];
            strArr[0] = "http://" + MainActivity.APIroute + "/api/truck/update";
            strArr[1] = jsonRequest.toString();
            servertask.execute(strArr);
            String response = (String) servertask.get(60, TimeUnit.SECONDS);
            Log.i(TAG, "updateTask:servertask.get response=" + response);
            CommLog(PhoneHomeState.ACTIVATE, "servertask.get - finished");
            if (response.isEmpty()) {
                Log.e(TAG, "ERROR: updateTaskResponse is empty");
                CommLog(PhoneHomeState.ACTIVATE, "ERROR: updateTaskResponse is empty");
                return 1;
            }
            this.jsonUpdate = new JSONObject(response);
            responseCode = this.jsonUpdate.getInt("code");
            if (MainActivity.DebugLog) {
                Log.i(TAG, "jsonUpdateResponse:" + this.jsonUpdate.toString(1));
            }
            CommLog(PhoneHomeState.ACTIVATE, "jsonUpdateResponse:" + this.jsonUpdate.toString(1));
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
                int battmv = batteryVoltageConverter.batteryStringToMilliVolt(jsonServerNode.getString("BATTERYMONITOR_VOLTAGE"));
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
        CommLog(PhoneHomeState.UPDATE, "LogTask");
        LogFile logFile = new LogFile(context, LogFile.LOGNAME, LogFile.LOGPATH, TAG);
        logFile.write("Start Upload");
        String logInStr = null;
        try {
            logInStr = logFile.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = new ByteArrayInputStream(logInStr.getBytes());

        BufferedReader logIn = new BufferedReader(new InputStreamReader(inputStream));

        // TODO original while (logIn != null)
        if (logIn != null) {
            JSONArray jsonLog = convertLogToJsonArray(logIn, PhoneHomeState.DATUM_STATUS);
            if (jsonLog == null) {
                return responseCode;
            }
            try {
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.accumulate("vin", this.jsonGateway.getString("vin"));
                jsonRequest.accumulate("guid", Integer.valueOf(jsonGateway.getInt("guid")));
                JSONObject jsonDevice = new JSONObject();
                jsonDevice.accumulate("serial", Integer.valueOf(jsonGateway.getInt("serial")));
                jsonRequest.accumulate("device", jsonDevice);
                jsonRequest.accumulate("log", jsonLog);
                if (MainActivity.DebugLog) {
                    Log.i(TAG, "jsonLogRequest:" + jsonRequest.toString(1));
                }
                CommLog(PhoneHomeState.UPDATE, "jsonLogRequest:" + jsonRequest.toString(1));
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
                CommLog(PhoneHomeState.UPDATE, "servertask.get - finished");
                if (response.isEmpty()) {
                    responseCode = 1;
                    Log.e(TAG, "ERROR: logTaskResponse is empty");
                    CommLog(PhoneHomeState.UPDATE, "ERROR: logTaskResponse is empty");
                } else {
                    JSONObject jsonResponse = new JSONObject(response);
                    responseCode = jsonResponse.getInt("code");
                    if (MainActivity.DebugLog) {
                        Log.i(TAG, "jsonLogResponse:" + jsonResponse.toString(1));
                    }
                    CommLog(PhoneHomeState.UPDATE, "jsonLogResponse:" + jsonResponse.toString(1));
                    if (responseCode != 10) {
                        Log.e(TAG, "*** Server Error Code: " + Integer.toString(responseCode));
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        logFile.deleteFile(LogFile.LOGNAME);
        logFile.write("End Upload");
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
        LogFile logFile = new LogFile(context, LogFile.DATUMNAME, LogFile.LOGPATH, TAG);
        CommLog(PhoneHomeState.CSC_AUTO_UPDATE, "DatumTask");
        String datumStr="";
        try {
            datumStr = logFile.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = new ByteArrayInputStream(datumStr.getBytes());

        BufferedReader datumIn = new BufferedReader(new InputStreamReader(inputStream));

        // TODO original while (logIn != null)
        if (datumIn != null) {
            JSONArray jsonDatum = convertDatumToJsonArray(datumIn, PhoneHomeState.DATUM_STATUS);
            if (jsonDatum == null) {
                return responseCode;
            }
            try {
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.accumulate("guid", Integer.valueOf(this.jsonGateway.getInt("guid")));
                jsonRequest.accumulate("datum", jsonDatum);
                if (MainActivity.DebugLog) {
                    Log.i(TAG, "jsonDatumRequest:" + jsonRequest.toString(1));
                }
                CommLog(PhoneHomeState.CSC_UPDATE, "jsonDatumRequest:" + jsonRequest.toString(1));
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
                    CommLog(PhoneHomeState.CSC_AUTO_UPDATE, "ERROR: datumTaskResponse is empty");
                } else {
                    JSONObject jsonResponse = new JSONObject(response);
                    responseCode = jsonResponse.getInt("code");
                    if (MainActivity.DebugLog) {
                        Log.i(TAG, "jsonDatumResponse:" + jsonResponse.toString(1));
                    }
                    CommLog(PhoneHomeState.CSC_AUTO_UPDATE, "jsonDatumResponse:" + jsonResponse.toString(1));
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
        logFile.deleteFile(LogFile.DATUMNAME);
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
     * Check there is APK update or not
     * @return
     * 0 if no update
     * 1 if has update
     * -1 if there is an exception
     */
    public int APKUpdateExist() {
        Log.i(TAG, "<<APKUpdateExist>>");
        CommLog(PhoneHomeState.VERSION, "APKUpdateExist");
        try {
            String version = this.mInstance.getPackageManager().getPackageInfo(this.mInstance.getPackageName(), 0).versionName;
            Log.i(TAG, "   current APK version: " + version);
            CommLog(PhoneHomeState.VERSION, "   current APK version: " + version);
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
                    CommLog(PhoneHomeState.VERSION, "   server version: " + server_version);
                    PrefUtils.setServerUpdateVersion(server_version, this.mInstance.getApplicationContext());
                    // if current apk version is up to date
                    if (version.equals(server_version)) {
                        return 0;
                    }
                    return 1;
                }
                PrefUtils.setServerUpdateVersion("", this.mInstance.getApplicationContext());
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
        CommLog(PhoneHomeState.VERSION, "PerformAPKUpdate");
        try {
            String version = this.mInstance.getPackageManager().getPackageInfo(this.mInstance.getPackageName(), 0).versionName;
            Log.i(TAG, "APKUpdate::current APK version: " + version);
            CommLog(PhoneHomeState.VERSION, "current APK version: " + version);
            if (!APKupdate_exists) {
                return 0;
            }
            PrefUtils.setApkUpdateState(1, this.mInstance.getApplicationContext());
            Log.i(TAG, "-----> New Application code exists, Update the APK <-----");
            try {
                Log.w(TAG, "Send APICMD_DISCONNECT to Gateway..");
                this.mInstance.accessoryControl.writeCommand(AccessoryControl.APICMD_DISCONNECT, 0, 0);
                String APKlink = this.jsonApkVersion.getString("link_small");
                UpdateApp updateApp = new UpdateApp();
                updateApp.setContext(this.mInstance.getApplicationContext());
                Log.i(TAG, "APKUpdate:updateApp.execute..");
                String[] strArr = { APKlink };
                updateApp.execute(strArr);
                updateApp.get(60, TimeUnit.SECONDS);
                Log.i(TAG, "APKUpdate::updateApp.get - finished");
                CommLog(PhoneHomeState.VERSION, "updateApp.get - finished");
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
        CommLog(PhoneHomeState.APK_UPDATE, "CSCUpdateExist");
        Log.i(TAG, "   current CSC version: " + MainActivity.Gateway_FWversion);
        CommLog(PhoneHomeState.APK_UPDATE, "   current CSC version: " + MainActivity.Gateway_FWversion);
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
                CommLog(PhoneHomeState.APK_UPDATE, "   server version: " + server_version);
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
        CommLog(PhoneHomeState.APK_UPDATE, "PerformCSCUpdate");
        Log.i(TAG, "current CSC version: " + MainActivity.Gateway_FWversion);
        CommLog(PhoneHomeState.APK_UPDATE, "current CSC version: " + MainActivity.Gateway_FWversion);
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
            CommLog(PhoneHomeState.APK_UPDATE, "updateGateway.get - finished");
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
