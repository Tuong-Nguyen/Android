package vn.com.tma.idlesmart.Utils;

import android.content.Context;
import android.util.Log;


public class CANLogFile extends LogUtils {
    /**
     * LogFile constructor
     *
     * @param context
     * @param fileName
     * @param fileNamePath
     * @param tag
     */
    public CANLogFile(Context context, String fileName, String fileNamePath, String tag) {
        super(context, fileName, fileNamePath, tag);
    }

    private String padRight(String str, int size, char padChar) {
        StringBuffer padded = new StringBuffer(str);
        while (padded.length() < size) {
            padded.append(padChar);
        }
        return padded.toString();
    }

    @Override
    public void write(String data) {
        if (this.outputStream == null) {
            // Open file
            this.openBufferOutPutStream();
        } if (this.outputStream != null){
            int paddingCount = 16;
            try {
                int lth = data.length();
                int reccnt = lth / paddingCount;
                if (reccnt * paddingCount != lth) {
                    reccnt += 1;
                    data = padRight(data, (reccnt * paddingCount) - lth, ' ');
                }
                byte[] bytes = data.getBytes();
                for (int irec = 0; irec < reccnt; irec += 1) {
                    this.outputStream.write(bytes, 0, paddingCount);
                }
                this.outputStream.flush();
            } catch (Exception e) {
                Log.w(tag, "IOException writing CANLog file - e=", e);
            }
        }
        // Close file
        this.closeOutputStream();
    }
}
