package vn.com.tma.idlesmart;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import vn.com.tma.idlesmart.Utils.LogFile;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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
    public void readwrite_inputStringData_returnDataExistInThatFile() throws IOException {
            //Arrange
            String fileNamePath = "Logs";
            String filename = "Log1.bin";
            String tag = "test";
            context = InstrumentationRegistry.getTargetContext();
            LogFile logFile = new LogFile(context, filename, fileNamePath, tag);

            // Action
            logFile.deleteFile(filename);
            String input = "This is instrument test";
            logFile.write(input);
            String data = logFile.read();
            boolean result = data.matches("(?i).*This is instrument test.*");

            // Assert
            assertTrue(result);
    }

}
