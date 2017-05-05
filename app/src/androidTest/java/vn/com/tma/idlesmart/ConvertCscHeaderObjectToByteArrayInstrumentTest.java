package vn.com.tma.idlesmart;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import vn.com.tma.idlesmart.Utils.ConvertJsonObjectToByteArray;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by ntmhanh on 5/3/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ConvertCscHeaderObjectToByteArrayInstrumentTest {
    JSONObject jsonObject_1 = new JSONObject();
    JSONObject jsonObject_2 = new JSONObject();

    @Before
    public void setUp() throws JSONException {
        //jsonObject_1
        jsonObject_1.put("format", 12);
        jsonObject_1.put("start", "0123456789ABCDEF");
        jsonObject_1.put("size", "0123456789ABCDEF");
        jsonObject_1.put("tra", "0123456789ABCDEF");
        jsonObject_1.put("signature", "0123456789ABCDEF");
        jsonObject_1.put("crc_1", "0123456789ABCDEF");
        jsonObject_1.put("crc_2", "0123456789ABCDEF");
        jsonObject_1.put("crc_3", "0123456789ABCDEF");
        jsonObject_1.put("block_count", "0123456789ABCDEF");
        jsonObject_1.put("version", "0123456789ABCDEF");
        //jsonObject_2
        jsonObject_2.put("format", 12);
        jsonObject_2.put("start", "0123456789ABCDEF");
        jsonObject_2.put("size", "0123456789ABCDEF");
        jsonObject_2.put("tra", "0123456789ABCDEF");
        jsonObject_2.put("signature", "0123456789ABCDEF");
        jsonObject_2.put("crc_1", "0123456789ABCDEF");
        jsonObject_2.put("crc_2", "0123456789ABCDEF");
        jsonObject_2.put("crc_3", "0123456789ABCDEF");
        jsonObject_2.put("block_count", "EF");
        jsonObject_2.put("version", "0123456789ABCDEF");
    }
    @Test
    public void ConvertCscHeaderObjectToByteArray_InputJsonObjectWithLengthOfblock_countBiggerThanOne_returnTheNumberOfCountToCallhexStringToByteArrayIsEight() throws JSONException {
        //Arrange
         ConvertJsonObjectToByteArray mock = Mockito.spy(ConvertJsonObjectToByteArray.class);
        //Action
        mock.convertCscHeaderObjectToByteArray(jsonObject_1);
        //Assert
        verify(mock, times(8)).hexStringToByteArray(anyString());
    }

    @Test
    public void ConvertCscHeaderObjectToByteArray_InputJsonObjectWithLengthOfblock_countEqualToOne_returnTheNumberOfCountToCallhexStringToByteArrayIsNine() throws JSONException {
        //Arrange
        ConvertJsonObjectToByteArray mock = Mockito.spy(ConvertJsonObjectToByteArray.class);
        //Action
        mock.convertCscHeaderObjectToByteArray(jsonObject_2);
        //Assert
        verify(mock, times(9)).hexStringToByteArray(anyString());
    }

}

