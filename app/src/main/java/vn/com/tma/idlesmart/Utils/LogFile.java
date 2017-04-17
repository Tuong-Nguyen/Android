package vn.com.tma.idlesmart.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import vn.com.tma.idlesmart.AoaMessage;
import vn.com.tma.idlesmart.MainActivity;
import vn.com.tma.idlesmart.params.PhoneHomeState;
import vn.com.tma.idlesmart.tasks.ServerTask;

import static android.content.ContentValues.TAG;
import static vn.com.tma.idlesmart.AccessoryControl.getUTCdatetimeAsString;

/**
 * Created by ntmhanh on 4/13/2017.
 */

public class LogFile {

    public static final String LOGNAME= "Log.bin";
    public static final String LOGPATH= "Logs";
    public static final String DATUMNAME= "Datum.bin";
    public static final String CANLOGNAME= "CANLog.bin";
    public static final String CANLOGPATH= "CANLogs";

    private BufferedOutputStream outputStream;
    private String fileName;
    private String fileNamePath;
    private String tag;
    Context context;
    JSONObject jsonGateway;

    /**
     * LogFile constructor
     * @param fileName
     * @param fileNamePath
     * @param tag
     */
    public LogFile(Context context, String fileName, String fileNamePath, String tag) {
        this.fileName = fileName;
        this.fileNamePath = fileNamePath;
        this.tag = tag;
        this.context = context;
    }

    /**
     * Writing with string data
     * @param data
     */
    public void write(String data) {
        if (outputStream == null) {
            // Open file
            this.open();
        }
        if (outputStream != null && !data.trim().isEmpty()) {
            try {
                byte[] ts = getUTCdatetimeAsString().getBytes();
                this.outputStream.write(ts, 0, ts.length);
                this.outputStream.write(' ');
                byte[] bstr = data.getBytes();
                this.outputStream.write(bstr, 0, bstr.length);
                this.outputStream.write('\n');
                this.outputStream.write('\r');
                this.outputStream.flush();
            } catch (Exception e) {
                Log.w(tag, "IOException writing Log file - e=", e);
            }
        }
        // Close file
        this.closeOutputStream();
    }

    /**
     * Writing with array data
     * @param buffer
     * @param len
     */
    public void write(byte[] buffer, int len) {
        if (outputStream == null) {
            // Open file
            this.open();
        }
        if (outputStream != null) {
            try {
                byte[] ts = getUTCdatetimeAsString().getBytes();
                outputStream.write(ts, 0, ts.length);
                outputStream.write(' ');
                outputStream.write(buffer, AoaMessage.START_DATA_POSITION, len - 3);
                outputStream.write('\n');
                outputStream.write('\r');
                outputStream.flush();
            } catch (Exception e22) {
                Log.w(tag, "IOException writing Datum file - e=", e22);
            }
            // Close file
            this.closeOutputStream();
        }
    }

    /**
     * Read Json data
     * @param jsonGateway
     * @param responseCode
     * @param logStream
     * @return
     */
    public int read(JSONObject jsonGateway, int responseCode, BufferedInputStream logStream) {

        BufferedReader logIn = new BufferedReader(new InputStreamReader(logStream));
        while (logIn != null) {
            JSONArray jsonLog = convertLogToJsonArray(logIn, PhoneHomeState.DATUM_STATUS);
            if (jsonLog == null) {
                break;
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
                    Log.i(tag, "jsonLogRequest:" + jsonRequest.toString(1));
                }
                // TODO Refactor CommLog later
                //CommLog(PhoneHomeState.UPDATE, "jsonLogRequest:" + jsonRequest.toString(1));
                ServerTask servertask = new ServerTask();

                servertask.setContext(this.context);
                Log.i(tag, "logTask:servertask.execute..");
                String[] strArr = new String[2];
                strArr[0] = "http://" + MainActivity.APIroute + "/api/truck/update";
                strArr[1] = jsonRequest.toString();
                servertask.execute(strArr);
                String response = servertask.get(5, TimeUnit.MINUTES);
                if (MainActivity.DebugLog) {
                    Log.i(TAG, "logTask:servertask.get response=" + response);
                }
                // TODO Refactor CommLog later
                //CommLog(PhoneHomeState.UPDATE, "servertask.get - finished");
                if (response.isEmpty()) {
                    responseCode = 1;
                    Log.e(TAG, "ERROR: logTaskResponse is empty");
                    // TODO Refactor CommLog later
                    //CommLog(PhoneHomeState.UPDATE, "ERROR: logTaskResponse is empty");
                } else {
                    JSONObject jsonResponse = new JSONObject(response);
                    responseCode = jsonResponse.getInt("code");
                    if (MainActivity.DebugLog) {
                        Log.i(TAG, "jsonLogResponse:" + jsonResponse.toString(1));
                    }
                    // TODO Refactor CommLog later
                    //CommLog(PhoneHomeState.UPDATE, "jsonLogResponse:" + jsonResponse.toString(1));
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
     * Open file
     */
    public void open() {
        if (outputStream == null) {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), this.fileNamePath);
                if (path.exists()) {
                    Log.i(tag, this.fileName + " directory already exists");
                } else if (path.mkdirs()) {
                    Log.i(tag, this.fileName + " directory created");
                } else {
                    Log.i(tag, "ERROR: Cannot create " + this.fileName + " directory");
                }
                try {
                    outputStream = new BufferedOutputStream(new FileOutputStream(new File(path, this.fileName), true));
                    Log.i(tag,  this.fileName + " file opened");
                } catch (Exception e) {
                    Log.w(tag, "IOException creating"+ this.fileName + " file - ioe=", e);
                }
            } else {
                Log.w(tag, "Error opening" + this.fileName + " file - SDCard is not mounted");
            }
        }
        if (outputStream != null) {
            this.write("\\\\IdleSmart "+ this.fileName +" start");
        }
    }

    /**
     * Close file
     */

    private void closeOutputStream() {
        if (this.outputStream != null) {
            this.write("\\\\IdleSmart log stop");
            try {
                this.outputStream.flush();
                this.outputStream.close();
                this.outputStream = null;
            } catch (Exception e) {
                Log.w(tag, "IOException closing Log file - e=", e);
            }
        }
    }

    /**
     * Delete data file in Logs
     */
    public boolean deleteFile(String fileName) {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), this.fileNamePath);
            if (path.exists()) {
                try {
                    new File(path, fileName).delete();
                    Log.i(tag, "Datum file deleted");
                    return true;
                } catch (Exception e) {
                    Log.w(tag, "IOException deleting Datum file - ioe=", e);
                }
            }
        }
        return false;
    }
    /**
     * Close a buffered input stream
     * @param inputStream
     */
    public void closeInputStream(BufferedInputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception e) {
                Log.w(tag, "IOException closing Datum file - e=", e);
            }
        }
    }
}
