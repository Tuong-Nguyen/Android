package vn.com.tma.idlesmart.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
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
import vn.com.tma.idlesmart.params.UpdateTaskConfig;

/**
 * Created by lnthao on 3/30/2017.
 */
public class UpdateGateway extends AsyncTask<String, Void, Void> {
    private static final String TAG = "IdleSmart.UpdateGateway";
    public Context context;
    public MainActivity mInstance;

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
                        if (len1 == -1) {
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

    /**
     * Build child array
     * @param i
     * @param parentArray
     * @param feature
     * @param jsonObject
     */

    public void buildChildArray(int i, byte[] parentArray, String feature, JSONObject jsonObject) throws JSONException {
        byte[] childArray;
        int j = 0;
            childArray = hexStringToByteArray(jsonObject.getString(feature));
        while (childArray.length > j){
            parentArray[i] = childArray[j];
            j++;
            i++;
        }
    }


    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendCscHeaderToGateway(JSONObject jsonObject) {
        byte[] parentArray = new byte[16767];
        int i = 0;
            if (MainActivity.DebugLog) {
                Log.i("IdleSmart.UpdateGateway", "<sendCSCHeaderToGateway>");
            }
            try {
                parentArray[i] = (byte) (jsonObject.getInt("format") & 255);
                i++;

                String start = "start";
                buildChildArray(i, parentArray,start, jsonObject);

                String size = "size";
                buildChildArray(i, parentArray, size, jsonObject);

                String tra = "tra";
                buildChildArray(i, parentArray, tra, jsonObject);

                String signature = "signature";
                buildChildArray(i, parentArray, signature, jsonObject);

                String crc_1 = "crc_1";
                buildChildArray(i, parentArray, crc_1, jsonObject);

                String crc_2 = "crc_2";
                buildChildArray(i, parentArray, crc_2, jsonObject);

                String crc_3 = "crc_3";
                buildChildArray(i, parentArray, crc_3, jsonObject);

                while (hexStringToByteArray(jsonObject.getString("block_count")).length == 1) {
                    parentArray[i] = 0;
                    i++;
                    String block_count = "block_count";
                    buildChildArray(i, parentArray, block_count, jsonObject);

                    String version = "version";
                    buildChildArray(i, parentArray, version, jsonObject);

                    int j = jsonObject.getString("version").getBytes().length;
                    while (j<10){
                        parentArray[i] = 0;
                        j++;
                        i++;
                    }
                    this.mInstance.accessoryControl.writeCommandBlock(188, i, parentArray );
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendCscDataBlockToGateway(JSONObject jsonObject, int r11) {
        byte[] parentArray = new byte[16767];
        int i = 0;
        if (MainActivity.DebugLog) {
            Log.i("IdleSmart.UpdateGateway", "<sendCSCDataBlockToGateway>");
        }
        try {

            parentArray[i] = 0;
            i++;
            parentArray[i] = (byte) (r11 & 255);

            String addr = "addr";
            buildChildArray(i, parentArray, addr, jsonObject);

            String size = "size";
            buildChildArray(i, parentArray, size, jsonObject);

            String load_image = "load_image";
            buildChildArray(i, parentArray, load_image, jsonObject);

            this.mInstance.accessoryControl.writeCommandBlock(189, i, parentArray);
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
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
