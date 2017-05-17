package vn.com.tma.idlesmart;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import vn.com.tma.idlesmart.Utils.CANLogFile;

import static junit.framework.Assert.assertTrue;


public class CANLogFileInstrumentedTest {
    private Context context;
    private CANLogFile canLogFile;
    private String fileNamePath = "CANLogs";
    private String filename = "CANLog.bin";
    private String tag = "Test";
    @Before
    public void setUp() {
        //Arrange
        context = InstrumentationRegistry.getTargetContext();
        canLogFile = new CANLogFile(context, filename, fileNamePath, tag);
    }


    @Test
    public void writeCANLogFile_inputStringDataIntoANewFile_returnTheDataExistInThatFile() throws IOException {
        //Arrange
        canLogFile.deleteFile(filename);// Ensure this is new file
        String input = "This is instrument test";

        // Action
        canLogFile.write(input);
        String data = canLogFile.read();
        boolean result = data.matches(".*\\sThis is the first input\\n");

        // Assert
        assertTrue(result);
    }

    @Test
    public void writeCANLogFile_inputStringDataIntoExistFile_returnOldAndNewDataInThatFile() throws IOException {
        //Arrange
        String input1 = "This is the first input";
        String input2 = "This is the second input";
        canLogFile.deleteFile(filename);// Ensure this is a new file

        // Action
        canLogFile.write(input1);
        canLogFile.write(input2);
        String data = canLogFile.read();
        boolean result = data.matches(".*\\sThis is the first input\\n.*\\sThis is the second input\\n");

        // Assert
        assertTrue(result);
    }
}
