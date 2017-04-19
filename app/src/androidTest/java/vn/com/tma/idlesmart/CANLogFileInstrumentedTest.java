package vn.com.tma.idlesmart;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import vn.com.tma.idlesmart.Utils.CANLogFile;

import static junit.framework.Assert.assertTrue;

/**
 * Created by ntmhanh on 4/19/2017.
 */

public class CANLogFileInstrumentedTest {
    private Context context;
    @Test
    public void writeCANLogFile_inputStringDataIntoANewFile_returnTheDataExistInThatFile() throws IOException {
        //Arrange
        String fileNamePath = "CANLogs";
        String filename = "CANLog.bin";
        String tag = "test";
        context = InstrumentationRegistry.getTargetContext();
        CANLogFile canLogFile = new CANLogFile(context, filename, fileNamePath, tag);
        canLogFile.deleteFile(filename);// Ensure this is new file
        String input = "This is instrument test";

        // Action
        canLogFile.write(input);
        String data = canLogFile.read();
        boolean result = data.matches("(?i).*This is instrume.*");

        // Assert
        assertTrue(result);
    }
//TODO Continue working
    @Test
    public void writeCANLogFile_inputStringDataIntoExistFile_returnOldAndNewDataInThatFile() throws IOException {
        //Arrange
        String fileNamePath = "CANLogs";
        String filename = "CANLog2.bin";
        String tag = "test";
        String input1 = "This is the first input";
        String input2 = "This is the second input";
        context = InstrumentationRegistry.getTargetContext();

        CANLogFile canLogFile = new CANLogFile(context, filename, fileNamePath, tag);
        canLogFile.deleteFile(filename);// Ensure this is a new file

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileNamePath);
        if (path.exists()) {
            Log.i(tag, filename + " directory already exists");
        } else if (path.mkdirs()) {
            Log.i(tag,filename + " directory created");
        } else {
            Log.i(tag, "ERROR: Cannot create " +filename + " directory");
        }
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(path, filename), true));
            byte[] bytes = input1.getBytes();
            outputStream.write(bytes);
            Log.i(tag, filename + " file opened");
        } catch (Exception e) {
            Log.w(tag, "IOException creating"+ filename + " file - ioe=", e);
        }

        // Action
        canLogFile.write(input2);
        String data = canLogFile.read();
        boolean result1 = data.matches("(?i).*This is the first input.*");
        boolean result2 = data.matches("(?i).*This is the seco.*");
        boolean result;
        if (result1 == true && result2 == true){
            result = true;
        } else {
            result = false;
        }
        // Assert
        assertTrue(result);
    }
}
