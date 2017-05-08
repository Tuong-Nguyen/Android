package vn.com.tma.idlesmart;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import vn.com.tma.idlesmart.Utils.JsonObjectConverter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by ntmhanh on 5/8/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ConvertCscDataBlockObjectToByteArrayInstrumentTest {
    JSONObject jsonObject = new JSONObject();

    @Before
    public void setUp() throws JSONException {
        //jsonObject
        jsonObject.put("addr", 12);
        jsonObject.put("size", "0123456789ABCDEF");
        jsonObject.put("load_image", "0123456789ABCDEF");
    }
    @Test
    public void ConvertCscDataBlockObjectToByteArray_InputJsonObject_returnTheNumberOfCountToCallhexStringToByteArrayIsThree() throws JSONException {
        //Arrange
        JsonObjectConverter mock = Mockito.spy(JsonObjectConverter.class);
        int index = 1;
        //Action
        mock.convertCscDataBlockObjectToByteArray(jsonObject, index);
        //Assert
        verify(mock, times(3)).hexStringToByteArray(anyString());
    }
}
