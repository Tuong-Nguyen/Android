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
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by ntmhanh on 4/14/2017.
 */
@RunWith(AndroidJUnit4.class)
public class LogFileInstrumentTest {
    private Context context;

        @Test
    public void deleteFile_deleteExistFile_returnTrue() throws Exception {

            //Arrange
            String fileNamePath = "Logs";
            String filename = "Log2.bin";
            String tag = "test";
           context = InstrumentationRegistry.getTargetContext();
            LogFile logFile = new LogFile(context, filename, fileNamePath, tag);
            logFile.openBufferOutPutStream();

            // Action
            boolean isDelete = logFile.deleteFile(filename);

            // Assert
            assertEquals("File was deleted ", true, isDelete);
        }
    @Test
    public void deleteFile_deleteNotExistFile_returnFalse() throws Exception {

        //Arrange
        String fileNamePath = "LogFile";
        String filename = "logFile.bin";
        String tag = "test";
        context = InstrumentationRegistry.getTargetContext();
        LogFile logFile = new LogFile(context, filename, fileNamePath, tag);
        //logFile.openBufferOutPutStream();

        // Action
        boolean isDelete = logFile.deleteFile(filename);

        // Assert
        assertEquals("File isn't exist", false, isDelete);
    }
    @Test
    public void writeLogFile_inputStringDataIntoANewFile_returnTheDataExistInThatFile() throws IOException {
            //Arrange
            String fileNamePath = "Logs";
            String filename = "Log1.bin";
            String tag = "test";
            context = InstrumentationRegistry.getTargetContext();
            LogFile logFile = new LogFile(context, filename, fileNamePath, tag);
            logFile.deleteFile(filename);// Ensure this is new file
            String input = "This is instrument test";

            // Action
            logFile.write(input);
            String data = logFile.read();
            boolean result = data.matches("(?i).*This is instrument test.*");

            // Assert
            assertTrue(result);
    }
    @Test
    public void writeLogFile_inputStringDataIntoExistFile_returnOldAndNewDataInThatFile() throws IOException {
        //Arrange
        String fileNamePath = "Logs";
        String filename = "Log2.bin";
        String tag = "test";
        String input1 = "This is the first input";
        String input2 = "This is the second input";
        context = InstrumentationRegistry.getTargetContext();

        LogFile logFile = new LogFile(context, filename, fileNamePath, tag);
        logFile.deleteFile(filename);// Ensure this is a new file

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
        logFile.write(input2);
        String data = logFile.read();
        boolean result1 = data.matches("(?i).*This is the first input.*");
        boolean result2 = data.matches("(?i).*This is the second input.*");
        boolean result;
        if (result1 == true && result2 == true){
            result = true;
        } else {
            result = false;
        }
        // Assert
        assertTrue(result);
    }
    @Test
    public void readLogFile_readDataInExistFileHasData_returnTrue() throws IOException {
        //Arrange
        String fileNamePath = "Logs";
        String filename = "Log1.bin";
        String tag = "test";
        context = InstrumentationRegistry.getTargetContext();
        LogFile logFile = new LogFile(context, filename, fileNamePath, tag);
        logFile.deleteFile(filename);// Ensure this is new file
        String input = "This is instrument test";

        // Action
        logFile.write(input);
        String data = logFile.read();
        int size = data.length();
        boolean result;
        if (size > 0){
            result = true;
        } else {
            result = false;
        }
        // Assert
        assertTrue(result);
    }

    @Test
    public void readLogFile_readDataInExistFileEmptyData_return0() throws IOException {
        //Arrange
        String fileNamePath = "Logs";
        String filename = "Log1.bin";
        String tag = "test";
        context = InstrumentationRegistry.getTargetContext();
        LogFile logFile = new LogFile(context, filename, fileNamePath, tag);
        logFile.deleteFile(filename);// Ensure that this is new file
        // Action
        logFile.openBufferOutPutStream(); //Create a new file with empty data
        String data = logFile.read();
        int size = data.length();
        // Assert
        assertEquals(0, size);
    }

    @Test
    public void readLogFile_readDataInNotExistFile_returnNullData(){
        //Arrange
        String fileNamePath = "Logs";
        String filename = "Log1.bin";
        String tag = "test";
        context = InstrumentationRegistry.getTargetContext();
        LogFile logFile = new LogFile(context, filename, fileNamePath, tag);
        logFile.deleteFile(filename);// Ensure that this is a new file
        // Action
        String data;
        data = logFile.read();
        // Assert
        assertNull(data);
    }
}
