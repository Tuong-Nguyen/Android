package vn.com.tma.idlesmart;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.io.IOException;

import vn.com.tma.idlesmart.Utils.CANLogFile;

import static junit.framework.Assert.assertTrue;


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

        // Action
        canLogFile.write(input1);
        canLogFile.write(input2);
        String data = canLogFile.read();
        boolean result1 = data.matches("(?i).*This is the firs.*");
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
