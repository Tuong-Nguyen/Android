package vn.com.tma.idlesmart.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.com.tma.idlesmart.KioskService;
import vn.com.tma.idlesmart.MainActivity;
import vn.com.tma.idlesmart.httpClient;
import vn.com.tma.idlesmart.params.UpdateTaskConfig;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Class is used to download APK file from the server
 * If the apk file is existed, delete it and store the new one.
 */
public class UpdateApp extends AsyncTask<String, Void, Void> {
    private static final String TAG = "IdleSmart.UpdateApp";
    private Context context;

    public void setContext(Context contextf) {
        this.context = contextf;
    }

    protected Void doInBackground(String... arg0) {
        byte[] buffer = new byte[16384];
        Log.i(TAG, "=====> UpdateApp thread is running in Bkgnd");
        try {
            String APKlink = arg0[UpdateTaskConfig.STATE_IDLE];
            URL url = new URL("http://" + MainActivity.APIroute + APKlink);
            Log.i(TAG, "url=" + url);
            // TODO: Extract to LogUtil class
            //httpClient.CommLog(UpdateTaskConfig.STATE_VERSION, "url=" + url);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST");
            c.setDoOutput(httpClient.PHONEHOME_RESCHEDULE);
            c.connect();
            String lfn = APKlink;
            for (int i = lfn.length() + httpClient.PHONEHOME_ERROR; i >= 0; i += httpClient.PHONEHOME_ERROR) {
                if (lfn.charAt(i) == '/') {
                    lfn = lfn.substring(i + UpdateTaskConfig.STATE_CONNECT);
                    break;
                }
            }
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), UpdateTaskConfig.APK_PATH);
                if (!path.exists()) {
                    if (!path.mkdirs()) {
                        Log.i(TAG, "ERROR: Cannot create APKupdates directory");
                        // TODO: Extract to LogUtil class
                        //httpClient.CommLog(UpdateTaskConfig.STATE_VERSION, "ERROR: Cannot create APKupdates directory");
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
                        fos.write(buffer, UpdateTaskConfig.STATE_IDLE, len1);
                    }
                    fos.close();
                    is.close();
                    Log.i(TAG, "Download complete: " + lfn);
                    // TODO: Extract to LogUtil class
                    //httpClient.CommLog(UpdateTaskConfig.STATE_VERSION, "Download complete: " + lfn);
                    KioskService.restartcount = UpdateTaskConfig.STATE_IDLE;
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setDataAndType(Uri.fromFile(new File(path, lfn)), "application/vnd.android.package-archive");
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.PackageUpdatePending = httpClient.PHONEHOME_RESCHEDULE;
                    Log.i(TAG, "******* startActivity::PackageManager::package-archive");
                    this.context.startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "======> updateApk thread done");
                    return null;
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "======> updateApk thread done");
            return null;
        }
    }
}
