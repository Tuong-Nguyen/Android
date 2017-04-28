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
public class ConvertToJsonArrayInstrumentTest {
    private Context context;
    private LogFile logFile;
    private String fileNamePath = "Datums";
    private String filename = "Datum.bin";
    private String tag = "Test";

    @Before
    public void setUp() {
        //Arrange
        context = InstrumentationRegistry.getTargetContext();
        logFile = new LogFile(context, filename, fileNamePath, tag);
    }

    @Test
    public void convertDatumToJsonArray_readDataFromFileAndConvertToJsonArray_returnDataLikeInput() throws IOException, JSONException {
        //Arrange
        String input = "datumname\\datumValue";
        logFile.deleteFile(filename);// Ensure this is a new file
        logFile.write(input);
        String data = logFile.read();
        // Action
        ConvertToJsonArray convertToJsonArray= new ConvertToJsonArray();
        JSONArray jsonArray = convertToJsonArray.convertDatumToJsonArray(data, PhoneHomeState.DATUM_MAX_LINE);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String datumName = jsonObject.getString("datum_name");
        String datumValue = jsonObject.getString("datum_value");
        //Assert
        assertEquals("datumName", datumName);
        assertEquals("datumValue", datumValue);
    }

    @Test
    public void convertDatumToJsonArray_readDataFromFileHasManyLineAndConvertToJsonArray_returnTheNumberOfObjectLikeInput() throws IOException, JSONException {
        //Arrange
        String input1 = "datumName_1\\datumValue_1";
        String input2 = "datumName_2\\datumValue_2";
        logFile.deleteFile(filename);// Ensure this is a new file
        logFile.write(input1);
        logFile.write(input2);
        String data = logFile.read();
        // Action
        ConvertToJsonArray convertToJsonArray= new ConvertToJsonArray();
        JSONArray jsonArray = convertToJsonArray.convertDatumToJsonArray(data, PhoneHomeState.DATUM_MAX_LINE);
        int number = jsonArray.length();
        //Assert
        assertEquals(2, number);
    }

}