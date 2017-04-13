package vn.com.tma.idlesmart.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by ntmhanh on 4/13/2017.
 */

public class DatumUtils {
    public String TAG;

    public DatumUtils(String tag) {
        this.TAG = tag;
    }

    /**
     * Close a buffered input stream
     *
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
     * Open DatumFile
     * @return FileOutputStream
     */
    public  BufferedOutputStream openDatumFile() {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Logs");
                if (path.exists()) {
                    Log.i(TAG, "Log directory already exists");
                } else if (path.mkdirs()) {
                    Log.i(TAG, "Log directory created");
                } else {
                    Log.i(TAG, "ERROR: Cannot create Log directory");
                }
                try {
                    Log.i(TAG, "Datum file opened");
                    return new BufferedOutputStream(new FileOutputStream(new File(path, "Datum.bin"), true));
                } catch (Exception e) {
                    Log.w(TAG, "IOException creating Datum file - ioe=", e);
                    return null;
                }
            }
            Log.w(TAG, "Error opening Datum file - SDCard is not mounted");
        return null;
    }

    /**
     * Close Datum File
     * @param datumStream
     */
    public void closeDatumFile(BufferedOutputStream datumStream) {
        if (datumStream != null) {
            try {
                datumStream.flush();
                datumStream.close();
                datumStream = null;
            } catch (Exception e) {
                Log.w(TAG, "IOException closing Datum file - e=", e);
            }
        }
    }
}
