package vn.com.tma.idlesmart;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import vn.com.tma.idlesmart.Utils.ConvertToJsonArray;
import vn.com.tma.idlesmart.Utils.LogFile;
import vn.com.tma.idlesmart.params.PhoneHomeState;

import static junit.framework.Assert.assertEquals;

/**
 * Created by ntmhanh on 4/28/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ConvertLogToJsonArrayInstrumentTest {
    private Context context;
    private LogFile logFile;
    private String fileNamePath = "Logs";
    private String filename = "log.bin";
    private String tag = "Test";

    @Before
    public void setUp() {
        //Arrange
        context = InstrumentationRegistry.getTargetContext();
        logFile = new LogFile(context, filename, fileNamePath, tag);
    }
    @Test
    public void convertLogToJsonArray_readDataFromFileAndConvertToJsonArray_returnDataLikeInput() throws IOException, JSONException {
        //Arrange
        String input = "event\\eventTrigger\\eventComment";
        logFile.deleteFile(filename);// Ensure this is a new file
        logFile.write(input);
        String data = logFile.read();
        // Action
        ConvertToJsonArray convertToJsonArray= new ConvertToJsonArray();
        JSONArray jsonArray = convertToJsonArray.convertLogToJsonArray(data, PhoneHomeState.DATUM_LIMIT_LINE);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String event = jsonObject.getString("event");
        String eventTrigger = jsonObject.getString("event_trigger");
        String eventComment = jsonObject.getString("event_comment");
        //Assert
        assertEquals("event", event);
        assertEquals("eventTrigger", eventTrigger);
        assertEquals("eventComment", eventComment);
    }

}
