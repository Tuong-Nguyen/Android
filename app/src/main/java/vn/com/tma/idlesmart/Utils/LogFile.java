package vn.com.tma.idlesmart.Utils;

import android.content.Context;
import android.util.Log;

import vn.com.tma.idlesmart.AoaMessage;

import static vn.com.tma.idlesmart.AccessoryControl.getUTCdatetimeAsString;


public class LogFile extends LogAbstract {
    /**
     * LogFile constructor
     *
     * @param context
     * @param fileName
     * @param fileNamePath
     * @param tag
     */
    public LogFile(Context context, String fileName, String fileNamePath, String tag) {
        super(context, fileName, fileNamePath, tag);
    }


    /**
     * Writing with string data
     * @param data
     */
    public void write(String data) {
        if (this.outputStream == null) {
            // Open file
            this.openBufferOutPutStream();
        }
        if (this.outputStream != null && !data.trim().isEmpty()) {
            try {
                byte[] ts = getUTCdatetimeAsString().getBytes();
                this.outputStream.write(ts, 0, ts.length);
                this.outputStream.write(' ');
                byte[] bstr = data.getBytes();
                this.outputStream.write(bstr, 0, bstr.length);
                this.outputStream.write('\n');
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
        if (this.outputStream == null) {
            // Open file
            this.openBufferOutPutStream();
        }
        if (this.outputStream != null) {
            try {
                byte[] ts = getUTCdatetimeAsString().getBytes();
                this.outputStream.write(ts, 0, ts.length);
                this.outputStream.write(' ');
                this.outputStream.write(buffer, AoaMessage.START_DATA_POSITION, len);
                this.outputStream.write('\n');
                this.outputStream.flush();
            } catch (Exception e22) {
                Log.w(tag, "IOException writing Datum file - e=", e22);
            }
            // Close file
            this.closeOutputStream();
        }
    }


}
