package vn.com.tma.idlesmart.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;


public abstract class LogFileAbstract {
    public static final String LOGNAME= "Log.bin";
    public static final String LOGPATH= "Logs";
    public static final String DATUMNAME= "Datum.bin";
    public static final String CANLOGNAME= "CANLog.bin";
    public static final String CANLOGPATH= "CANLogs";

    public BufferedOutputStream outputStream = null;
    public BufferedInputStream inputStream = null;
    public String fileName = null;
    public String fileNamePath = null;
    public String tag = null;
    Context context = null;


    /**
     * LogFile constructor
     * @param fileName
     * @param fileNamePath
     * @param tag
     */
    public LogFileAbstract(Context context, String fileName, String fileNamePath, String tag) {
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
    }

    /**
     * Writing with array data
     * @param buffer
     * @param len
     */
    public void write(byte[] buffer, int len) {
    }

    /**
     * Read InputStream
     * @return
     */

    public String read() throws IOException {
        if (this.inputStream == null) {
            this.openBufferedInputStream();
        }
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
        String line = bufferedReader.readLine();
        while(line != null){
            inputStringBuilder.append(line);
            line = bufferedReader.readLine();
        }
        this.closeInputStream();
        return inputStringBuilder.toString();
    }



    /**
     * Open file
     */
    public void openBufferOutPutStream() {
        if (this.outputStream == null) {
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
                    this.outputStream = new BufferedOutputStream(new FileOutputStream(new File(path, this.fileName), true));
                    Log.i(tag,  this.fileName + " file opened");
                } catch (Exception e) {
                    Log.w(tag, "IOException creating"+ this.fileName + " file - ioe=", e);
                }
            } else {
                Log.w(tag, "Error opening" + this.fileName + " file - SDCard is not mounted");
            }
        }
    }
    /**
     * Read file store in external storage
     */
    public void openBufferedInputStream() {
        if (this.inputStream == null) {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), this.fileNamePath);
                if (path.exists()) {
                    try {
                        Log.i(TAG, "Log file opened for Read");
                        this.inputStream = new BufferedInputStream(new FileInputStream(new File(path, this.fileName)));
                    } catch (Exception e) {
                        Log.w(TAG, "IOException opening Log file - ioe=", e);
                    }
                } else {
                    Log.i(TAG, "ERROR: Log file directory does not exist");
                }
            } else {
                Log.w(TAG, "Error opening Log file for Read - SDCard is not mounted");
            }
        }
    }

    /**
     * Close file
     */

    public void closeOutputStream() {
        if (this.outputStream != null) {
            //this.write("\\\\IdleSmart log stop");
            try {
                this.outputStream.flush();
                this.outputStream.close();
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
     */
    public void closeInputStream() {
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (Exception e) {
                Log.w(tag, "IOException closing Datum file - e=", e);
            }
        }
    }
}
