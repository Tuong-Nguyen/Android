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
import vn.com.tma.idlesmart.Utils.JsonObjectConverter;
import vn.com.tma.idlesmart.params.UpdateTaskConfig;

import static vn.com.tma.idlesmart.AccessoryControl.APIDATA_FW_HEADER;
import static vn.com.tma.idlesmart.AccessoryControl.APIDATA_FW_TRA;

/**
 * Created by lnthao on 3/30/2017.
 */
public class UpdateGateway extends AsyncTask<String, Void, Void> {
    private static final String TAG = "IdleSmart.UpdateGateway";
    public Context context;
    public MainActivity mInstance;
    private int dataCscHeaderLength;
    private int CscDataBlockLength;
    private int CscTRATLength;

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
        mInstance = new MainActivity();
        String TAG = "IdleSmart.UpdateGateway";
        try {
            Log.i("IdleSmart.UpdateGateway", "     ParseCSC and send to Gateway..");
            // TODO: Extract to LogUtil class
            //CommLog(STATE_APKUPDATE, "     ParseCSC and send to Gateway..");
            is = new FileInputStream(file);
            FileChannel fc = is.getChannel();
            String jsonStr = Charset.defaultCharset().decode(fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())).toString();
            is.close();
            //convert CscHeader Object To ByteArray
            JsonObjectConverter jsonObjectConverter = new JsonObjectConverter();
            JSONObject jsonCsc = new JSONObject(jsonStr);
            byte[] cscHeaderbyteArray = jsonObjectConverter.convertCscHeaderObjectToByteArray(jsonCsc);
            dataCscHeaderLength = cscHeaderbyteArray.length;
            mInstance.accessoryControl.writeCommandBlock(APIDATA_FW_HEADER, dataCscHeaderLength, cscHeaderbyteArray);
            Log.i("IdleSmart.UpdateGateway", "<sendCSCDataBlocks ToGateway>");
            for (int i = 0; i < jsonCsc.getInt("block_count"); i += 1) {
                byte[] cscDataBlockbyteArray = jsonObjectConverter.convertCscDataBlockObjectToByteArray(jsonCsc.getJSONObject("block_" + Integer.toString(i)), i);
                CscDataBlockLength = cscDataBlockbyteArray.length;
                mInstance.accessoryControl.writeCommandBlock(APIDATA_FW_HEADER, CscDataBlockLength, cscHeaderbyteArray);
            };
            byte[] cscTRAbyteArray = jsonObjectConverter.convertCscTRAObjectToByteArray(jsonCsc);
            mInstance.accessoryControl.writeCommandBlock(191,  APIDATA_FW_TRA, cscTRAbyteArray);
            MainActivity.GatewayUpdatePending = true;
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

}
