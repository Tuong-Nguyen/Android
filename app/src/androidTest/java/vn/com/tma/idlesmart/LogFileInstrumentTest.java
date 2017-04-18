package vn.com.tma.idlesmart;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import vn.com.tma.idlesmart.Utils.LogFile;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by ntmhanh on 4/14/2017.
 */
@RunWith(AndroidJUnit4.class)
public class LogFileInstrumentTest {
    private Context context;

        @Test
    public void deleteFile_CreateANewFileAndDeleteThisFile_CheckExistThisFileReturnFail() throws Exception {

            //Arrange
            String fileNamePath = "Logs";
            String filename = "Log.bin";
            String tag = "test";
           context = InstrumentationRegistry.getTargetContext();
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileNamePath);
                if (path.exists()) {
                    Log.i(tag, filename + " directory already exists");
                } else if (path.mkdirs()) {
                    Log.i(tag, filename + " directory created");
                } else {
                    Log.i(tag, "ERROR: Cannot create " + filename + " directory");
                }
                try {
                   BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(path, filename), true));
                    Log.i(tag,  filename + " file opened");
                } catch (Exception e) {
                    Log.w(tag, "IOException creating"+ filename + " file - ioe=", e);
                }
            } else {
                Log.w(tag, "Error opening" + filename + " file - SDCard is not mounted");
            }

            LogFile logFile = new LogFile(context, filename, fileNamePath, tag);

            // Action
            boolean isDelete = logFile.deleteFile(filename);

            // Assert
            assertEquals("File was deleted ", true, isDelete);
        }
    @Test
    public void readwrite_inputStringData_returnDataExistInThatFile() throws IOException {
        //Arrange
        String fileNamePath = "Logs";
        String filename = "Log1.bin";
        String tag = "test";
        context = InstrumentationRegistry.getTargetContext();
        LogFile logFile = new LogFile(context, filename, fileNamePath, tag);

        // Action
        logFile.deleteFile(filename);
        logFile.write("This is instrument test!");
        String data = "";
        data = logFile.readString();

        // Assert
        assertNotEquals("", data);


    }

}
