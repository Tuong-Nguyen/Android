package vn.com.tma.idlesmart.Utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import vn.com.tma.idlesmart.AoaMessage;
import vn.com.tma.idlesmart.MainActivity;

import static vn.com.tma.idlesmart.AccessoryControl.getUTCdatetimeAsString;

/**
 * Created by ntmhanh on 4/13/2017.
 */

public class LogFile {
    private BufferedOutputStream outputStream;
    private String fileName;
    private String fileNamePath;
    private String tag;

    /**
     * LogFile constructor
     * @param fileName
     * @param fileNamePath
     * @param tag
     */
    public LogFile(String fileName, String fileNamePath, String tag) {
        this.fileName = fileName;
        this.fileNamePath = fileNamePath;
        this.tag = tag;
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
     * Open file
     */
    private void open() {
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
    public void deleteFile() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Logs");
            if (path.exists()) {
                try {
                    new File(path, this.fileName).delete();
                    Log.i(tag, "Datum file deleted");
                } catch (Exception e) {
                    Log.w(tag, "IOException deleting Datum file - ioe=", e);
                }
            }
        }
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
