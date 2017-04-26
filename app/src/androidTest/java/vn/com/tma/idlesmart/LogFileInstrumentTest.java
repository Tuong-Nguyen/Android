package vn.com.tma.idlesmart;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    private LogFile logFile;
    private String fileNamePath = "Logs";
    private String filename = "Log.bin";
    private String tag = "Test";
    @Before
    public void setUp() {
        //Arrange
        context = InstrumentationRegistry.getTargetContext();
        logFile = new LogFile(context, filename, fileNamePath, tag);
    }

    @Test
    public void deleteFile_deleteExistFile_returnTrue() throws Exception {
        //Arrange
         logFile.openBufferOutPutStream();

        // Action
        boolean isDelete = logFile.deleteFile(filename);

        // Assert
        assertEquals("File was deleted ", true, isDelete);
        }
    @Test
    public void deleteFile_deleteNotExistFile_returnFalse() throws Exception {

        // Action
        boolean isDelete = logFile.deleteFile(filename);

        // Assert
        assertEquals("File isn't exist", false, isDelete);
    }
    @Test
    public void writeLogFile_inputStringDataIntoANewFile_returnTheDataExistInThatFile() throws IOException {
            //Arrange
            logFile.deleteFile(filename);// Ensure this is new file
            String input = "This is instrument test";

            // Action
            logFile.write(input);
            String data = logFile.read();
            String dateTime = logFile.dateTime;
            //boolean result = data.matches(time + " " + "(?i).*This is instrument test.*");
            boolean result = data.contains(dateTime + " " + "This is instrument test");

        // Assert
            assertTrue(result);
    }
    @Test
    public void writeLogFile_inputStringDataIntoExistFile_returnOldAndNewDataInThatFile() throws IOException {
        //Arrange
        String input1 = "This is the first input";
        String input2 = "This is the second input";

        logFile.deleteFile(filename);// Ensure this is a new file

        // Action
        logFile.write(input1);
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
        logFile.deleteFile(filename);// Ensure that this is a new file
        // Action
        String data;
        data = logFile.read();
        // Assert
        assertNull(data);
    }
}
