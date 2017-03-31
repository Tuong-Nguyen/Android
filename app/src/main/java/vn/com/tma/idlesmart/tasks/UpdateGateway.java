package vn.com.tma.idlesmart.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import vn.com.tma.idlesmart.MainActivity;
import vn.com.tma.idlesmart.httpClient;
import vn.com.tma.idlesmart.params.UpdateTaskConfig;

/**
 * Created by lnthao on 3/30/2017.
 */
public class UpdateGateway extends AsyncTask<String, Void, Void> {
    private static final String TAG = "IdleSmart.UpdateGateway";
    private Context context;

    public void setContext(Context contextf) {
        this.context = contextf;
    }

    protected Void doInBackground(String... arg0) {
        byte[] buffer = new byte[16384];
        Log.i(TAG, "=====> UpdateGateway thread running in bkgnd");
        // TODO: Extract to LogUtil class
        //httpClient.CommLog(httpClient.STATE_APKUPDATE, "  dib: UpdateGateway thread running in bkgnd");
        try {
            String CSClink = arg0[0];
            URL url = new URL("http://" + MainActivity.APIroute + CSClink);
            Log.i(TAG, "url=" + url);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST");
            c.setDoOutput(true);
            c.connect();
            String lfn = CSClink;
            for (int i = lfn.length() - 1; i >= 0; i -= 1) {
                if (lfn.charAt(i) == '/') {
                    lfn = lfn.substring(i + 1);
                    break;
                }
            }
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), UpdateTaskConfig.CSC_PATH);
                if (!path.exists()) {
                    if (!path.mkdirs()) {
                        Log.i(TAG, "ERROR: Cannot create CSCupdates directory");
                        // TODO: Extract to LogUtil class
                        //httpClient.CommLog(httpClient.STATE_APKUPDATE, "ERROR: Cannot create CSCupdates directory");
                    }
                }
                File outputFile = new File(path, lfn);
                try {
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    outputFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    InputStream is = c.getInputStream();
                    while (true) {
                        int len1 = is.read(buffer);
                        if (len1 == httpClient.PHONEHOME_ERROR) {
                            break;
                        }
                        fos.write(buffer, 0, len1);
                    }
                    fos.close();
                    is.close();
                    Log.i(TAG, lfn + " download complete");
                    // TODO: Extract to LogUtil class
                    //httpClient.CommLog(httpClient.STATE_APKUPDATE, lfn + " download complete");
                    parseCSC(path + "/" + lfn);
                } catch (IOException e) {
                    Log.e(TAG, "=====> UpdateGateway thread - IOException");
                    e.printStackTrace();
                    Log.i(TAG, "=====> UpdateGateway thread is done");
                    return null;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "=====> UpdateGateway thread - IOException");
            e.printStackTrace();
            Log.i(TAG, "=====> UpdateGateway thread is done");
            return null;
        }
        Log.i(TAG, "=====> UpdateGateway thread is done");
        return null;
    }

    private void parseCSC(String file) {
        FileInputStream is = null;
        String TAG = "IdleSmart.UpdateGateway";
        try {
            Log.i("IdleSmart.UpdateGateway", "     ParseCSC and send to Gateway..");
            // TODO: Extract to LogUtil class
            //CommLog(STATE_APKUPDATE, "     ParseCSC and send to Gateway..");
            is = new FileInputStream(file);
            FileChannel fc = is.getChannel();
            String jsonStr = Charset.defaultCharset().decode(fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())).toString();
            is.close();
            JSONObject jsonCsc = new JSONObject(jsonStr);
            sendCscHeaderToGateway(jsonCsc);
            Log.i("IdleSmart.UpdateGateway", "<sendCSCDataBlocks ToGateway>");
            for (int i = 0; i < jsonCsc.getInt("block_count"); i += 1) {
                sendCscDataBlockToGateway(jsonCsc.getJSONObject("block_" + Integer.toString(i)), i);
            }
            sendCscTRAToGateway(jsonCsc);
            MainActivity.gateway_restarting = true;
        } catch (Exception e) {
            Log.e("IdleSmart.UpdateGateway", "     ParseCSC IOException");
            e.printStackTrace();
        } catch (Throwable th) {
            if(is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i("IdleSmart.UpdateGateway", "     CSC has been sent to Gateway");
        // TODO: Extract to LogUtil class
        //CommLog(STATE_APKUPDATE, "     CSC has been sent to Gateway");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendCscHeaderToGateway(JSONObject r14) {
        /*
        r13 = this;
        r0 = "IdleSmart.UpdateGateway";
        r11 = 16767; // 0x417f float:2.3496E-41 double:8.284E-320;
        r1 = new byte[r11];
        r6 = 0;
        r11 = com.idlesmarter.aoa.MainActivity.DebugLog;
        if (r11 == 0) goto L_0x0012;
    L_0x000b:
        r11 = "IdleSmart.UpdateGateway";
        r12 = "<sendCSCHeaderToGateway>";
        android.util.Log.i(r11, r12);
    L_0x0012:
        r11 = "format";
        r4 = r14.getInt(r11);	 Catch:{ JSONException -> 0x0120 }
        r7 = r6 + 1;
        r11 = r4 & 255;
        r11 = (byte) r11;
        r1[r6] = r11;	 Catch:{ JSONException -> 0x0125 }
        r11 = "start";
        r11 = r14.getString(r11);	 Catch:{ JSONException -> 0x0125 }
        r2 = hexStringToByteArray(r11);	 Catch:{ JSONException -> 0x0125 }
        r5 = 0;
    L_0x002a:
        r11 = r2.length;	 Catch:{ JSONException -> 0x0125 }
        if (r5 >= r11) goto L_0x0037;
    L_0x002d:
        r6 = r7 + 1;
        r11 = r2[r5];	 Catch:{ JSONException -> 0x0120 }
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
        r5 = r5 + 1;
        r7 = r6;
        goto L_0x002a;
    L_0x0037:
        r11 = "size";
        r11 = r14.getString(r11);	 Catch:{ JSONException -> 0x0125 }
        r2 = hexStringToByteArray(r11);	 Catch:{ JSONException -> 0x0125 }
        r5 = 0;
    L_0x0042:
        r11 = r2.length;	 Catch:{ JSONException -> 0x0125 }
        if (r5 >= r11) goto L_0x004f;
    L_0x0045:
        r6 = r7 + 1;
        r11 = r2[r5];	 Catch:{ JSONException -> 0x0120 }
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
        r5 = r5 + 1;
        r7 = r6;
        goto L_0x0042;
    L_0x004f:
        r11 = "tra";
        r11 = r14.getString(r11);	 Catch:{ JSONException -> 0x0125 }
        r2 = hexStringToByteArray(r11);	 Catch:{ JSONException -> 0x0125 }
        r5 = 0;
    L_0x005a:
        r11 = r2.length;	 Catch:{ JSONException -> 0x0125 }
        if (r5 >= r11) goto L_0x0067;
    L_0x005d:
        r6 = r7 + 1;
        r11 = r2[r5];	 Catch:{ JSONException -> 0x0120 }
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
        r5 = r5 + 1;
        r7 = r6;
        goto L_0x005a;
    L_0x0067:
        r11 = "signature";
        r8 = r14.getJSONObject(r11);	 Catch:{ JSONException -> 0x0125 }
        r11 = "crc_0";
        r11 = r8.getString(r11);	 Catch:{ JSONException -> 0x0125 }
        r2 = hexStringToByteArray(r11);	 Catch:{ JSONException -> 0x0125 }
        r5 = 0;
    L_0x0078:
        r11 = r2.length;	 Catch:{ JSONException -> 0x0125 }
        if (r5 >= r11) goto L_0x0085;
    L_0x007b:
        r6 = r7 + 1;
        r11 = r2[r5];	 Catch:{ JSONException -> 0x0120 }
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
        r5 = r5 + 1;
        r7 = r6;
        goto L_0x0078;
    L_0x0085:
        r11 = "crc_1";
        r11 = r8.getString(r11);	 Catch:{ JSONException -> 0x0125 }
        r2 = hexStringToByteArray(r11);	 Catch:{ JSONException -> 0x0125 }
        r5 = 0;
    L_0x0090:
        r11 = r2.length;	 Catch:{ JSONException -> 0x0125 }
        if (r5 >= r11) goto L_0x009d;
    L_0x0093:
        r6 = r7 + 1;
        r11 = r2[r5];	 Catch:{ JSONException -> 0x0120 }
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
        r5 = r5 + 1;
        r7 = r6;
        goto L_0x0090;
    L_0x009d:
        r11 = "crc_2";
        r11 = r8.getString(r11);	 Catch:{ JSONException -> 0x0125 }
        r2 = hexStringToByteArray(r11);	 Catch:{ JSONException -> 0x0125 }
        r5 = 0;
    L_0x00a8:
        r11 = r2.length;	 Catch:{ JSONException -> 0x0125 }
        if (r5 >= r11) goto L_0x00b5;
    L_0x00ab:
        r6 = r7 + 1;
        r11 = r2[r5];	 Catch:{ JSONException -> 0x0120 }
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
        r5 = r5 + 1;
        r7 = r6;
        goto L_0x00a8;
    L_0x00b5:
        r11 = "crc_3";
        r11 = r8.getString(r11);	 Catch:{ JSONException -> 0x0125 }
        r2 = hexStringToByteArray(r11);	 Catch:{ JSONException -> 0x0125 }
        r5 = 0;
    L_0x00c0:
        r11 = r2.length;	 Catch:{ JSONException -> 0x0125 }
        if (r5 >= r11) goto L_0x00cd;
    L_0x00c3:
        r6 = r7 + 1;
        r11 = r2[r5];	 Catch:{ JSONException -> 0x0120 }
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
        r5 = r5 + 1;
        r7 = r6;
        goto L_0x00c0;
    L_0x00cd:
        r11 = "block_count";
        r11 = r14.getString(r11);	 Catch:{ JSONException -> 0x0125 }
        r2 = hexStringToByteArray(r11);	 Catch:{ JSONException -> 0x0125 }
        r11 = r2.length;	 Catch:{ JSONException -> 0x0125 }
        r12 = 1;
        if (r11 != r12) goto L_0x0128;
    L_0x00db:
        r6 = r7 + 1;
        r11 = 0;
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
    L_0x00e0:
        r5 = 0;
        r7 = r6;
    L_0x00e2:
        r11 = r2.length;	 Catch:{ JSONException -> 0x0125 }
        if (r5 >= r11) goto L_0x00ef;
    L_0x00e5:
        r6 = r7 + 1;
        r11 = r2[r5];	 Catch:{ JSONException -> 0x0120 }
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
        r5 = r5 + 1;
        r7 = r6;
        goto L_0x00e2;
    L_0x00ef:
        r11 = "version";
        r9 = r14.getString(r11);	 Catch:{ JSONException -> 0x0125 }
        r10 = r9.getBytes();	 Catch:{ JSONException -> 0x0125 }
        r5 = 0;
    L_0x00fa:
        r11 = r10.length;	 Catch:{ JSONException -> 0x0125 }
        if (r5 >= r11) goto L_0x0107;
    L_0x00fd:
        r6 = r7 + 1;
        r11 = r10[r5];	 Catch:{ JSONException -> 0x0120 }
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
        r5 = r5 + 1;
        r7 = r6;
        goto L_0x00fa;
    L_0x0107:
        r5 = r10.length;	 Catch:{ JSONException -> 0x0125 }
    L_0x0108:
        r11 = 10;
        if (r5 >= r11) goto L_0x0115;
    L_0x010c:
        r6 = r7 + 1;
        r11 = 0;
        r1[r7] = r11;	 Catch:{ JSONException -> 0x0120 }
        r5 = r5 + 1;
        r7 = r6;
        goto L_0x0108;
    L_0x0115:
        r11 = r13.mInstance;	 Catch:{ JSONException -> 0x0125 }
        r11 = r11.accessoryControl;	 Catch:{ JSONException -> 0x0125 }
        r12 = 188; // 0xbc float:2.63E-43 double:9.3E-322;
        r11.writeCommandBlock(r12, r7, r1);	 Catch:{ JSONException -> 0x0125 }
        r6 = r7;
    L_0x011f:
        return;
    L_0x0120:
        r3 = move-exception;
    L_0x0121:
        r3.printStackTrace();
        goto L_0x011f;
    L_0x0125:
        r3 = move-exception;
        r6 = r7;
        goto L_0x0121;
    L_0x0128:
        r6 = r7;
        goto L_0x00e0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.idlesmarter.aoa.httpClient.sendCscHeaderToGateway(org.json.JSONObject):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendCscDataBlockToGateway(JSONObject r10, int r11) {
        /*
        r9 = this;
        r0 = "IdleSmart.UpdateGateway";
        r7 = 16767; // 0x417f float:2.3496E-41 double:8.284E-320;
        r1 = new byte[r7];
        r5 = 0;
        r7 = com.idlesmarter.aoa.MainActivity.DebugLog;
        if (r7 == 0) goto L_0x0012;
    L_0x000b:
        r7 = "IdleSmart.UpdateGateway";
        r8 = "<sendCSCDataBlockToGateway>";
        android.util.Log.i(r7, r8);
    L_0x0012:
        r6 = r5 + 1;
        r7 = 0;
        r1[r5] = r7;
        r5 = r6 + 1;
        r7 = r11 & 255;
        r7 = (byte) r7;
        r1[r6] = r7;
        r7 = "addr";
        r7 = r10.getString(r7);	 Catch:{ JSONException -> 0x0076 }
        r2 = hexStringToByteArray(r7);	 Catch:{ JSONException -> 0x0076 }
        r4 = 0;
        r6 = r5;
    L_0x002a:
        r7 = r2.length;	 Catch:{ JSONException -> 0x007b }
        if (r4 >= r7) goto L_0x0037;
    L_0x002d:
        r5 = r6 + 1;
        r7 = r2[r4];	 Catch:{ JSONException -> 0x0076 }
        r1[r6] = r7;	 Catch:{ JSONException -> 0x0076 }
        r4 = r4 + 1;
        r6 = r5;
        goto L_0x002a;
    L_0x0037:
        r7 = "size";
        r7 = r10.getString(r7);	 Catch:{ JSONException -> 0x007b }
        r2 = hexStringToByteArray(r7);	 Catch:{ JSONException -> 0x007b }
        r4 = 0;
    L_0x0042:
        r7 = r2.length;	 Catch:{ JSONException -> 0x007b }
        if (r4 >= r7) goto L_0x004f;
    L_0x0045:
        r5 = r6 + 1;
        r7 = r2[r4];	 Catch:{ JSONException -> 0x0076 }
        r1[r6] = r7;	 Catch:{ JSONException -> 0x0076 }
        r4 = r4 + 1;
        r6 = r5;
        goto L_0x0042;
    L_0x004f:
        r7 = "load_image";
        r7 = r10.getString(r7);	 Catch:{ JSONException -> 0x007b }
        r2 = hexStringToByteArray(r7);	 Catch:{ JSONException -> 0x007b }
        r4 = 0;
    L_0x005a:
        r7 = r2.length;	 Catch:{ JSONException -> 0x007b }
        if (r4 >= r7) goto L_0x0067;
    L_0x005d:
        r5 = r6 + 1;
        r7 = r2[r4];	 Catch:{ JSONException -> 0x0076 }
        r1[r6] = r7;	 Catch:{ JSONException -> 0x0076 }
        r4 = r4 + 1;
        r6 = r5;
        goto L_0x005a;
    L_0x0067:
        r7 = r9.mInstance;	 Catch:{ JSONException -> 0x007b }
        r7 = r7.accessoryControl;	 Catch:{ JSONException -> 0x007b }
        r8 = 189; // 0xbd float:2.65E-43 double:9.34E-322;
        r7.writeCommandBlock(r8, r6, r1);	 Catch:{ JSONException -> 0x007b }
        r7 = com.idlesmarter.aoa.MainActivity.DebugLog;	 Catch:{ JSONException -> 0x007b }
        if (r7 == 0) goto L_0x0074;
    L_0x0074:
        r5 = r6;
    L_0x0075:
        return;
    L_0x0076:
        r3 = move-exception;
    L_0x0077:
        r3.printStackTrace();
        goto L_0x0075;
    L_0x007b:
        r3 = move-exception;
        r5 = r6;
        goto L_0x0077;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.idlesmarter.aoa.httpClient.sendCscDataBlockToGateway(org.json.JSONObject, int):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendCscTRAToGateway(JSONObject r10) {
        /*
        r9 = this;
        r0 = "IdleSmart.UpdateGateway";
        r7 = 16767; // 0x417f float:2.3496E-41 double:8.284E-320;
        r1 = new byte[r7];
        r5 = 0;
        r7 = com.idlesmarter.aoa.MainActivity.DebugLog;
        if (r7 == 0) goto L_0x0012;
    L_0x000b:
        r7 = "IdleSmart.UpdateGateway";
        r8 = "<sendCSCTRAToGateway>";
        android.util.Log.i(r7, r8);
    L_0x0012:
        r7 = "tra";
        r7 = r10.getString(r7);	 Catch:{ JSONException -> 0x0039 }
        r2 = hexStringToByteArray(r7);	 Catch:{ JSONException -> 0x0039 }
        r4 = 0;
        r6 = r5;
    L_0x001e:
        r7 = r2.length;	 Catch:{ JSONException -> 0x003e }
        if (r4 >= r7) goto L_0x002b;
    L_0x0021:
        r5 = r6 + 1;
        r7 = r2[r4];	 Catch:{ JSONException -> 0x0039 }
        r1[r6] = r7;	 Catch:{ JSONException -> 0x0039 }
        r4 = r4 + 1;
        r6 = r5;
        goto L_0x001e;
    L_0x002b:
        r7 = 1;
        com.idlesmarter.aoa.MainActivity.GatewayUpdatePending = r7;	 Catch:{ JSONException -> 0x003e }
        r7 = r9.mInstance;	 Catch:{ JSONException -> 0x003e }
        r7 = r7.accessoryControl;	 Catch:{ JSONException -> 0x003e }
        r8 = 191; // 0xbf float:2.68E-43 double:9.44E-322;
        r7.writeCommandBlock(r8, r6, r1);	 Catch:{ JSONException -> 0x003e }
        r5 = r6;
    L_0x0038:
        return;
    L_0x0039:
        r3 = move-exception;
    L_0x003a:
        r3.printStackTrace();
        goto L_0x0038;
    L_0x003e:
        r3 = move-exception;
        r5 = r6;
        goto L_0x003a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.idlesmarter.aoa.httpClient.sendCscTRAToGateway(org.json.JSONObject):void");
    }


    public static byte[] hexStringToByteArray(String str) {
        String TAG = "IdleSmart.UpdateGateway";
        try {
            String s;
            if (str.charAt(0) == '0' && str.charAt(1) == 'x') {
                s = str.substring(2);
            } else {
                s = str;
            }
            int len = s.length();
            byte[] bArr = new byte[(len / 2)];
            for (int i = 0; i < len; i += 2) {
                bArr[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
            }
            return bArr;
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }
}
