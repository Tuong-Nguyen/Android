package vn.com.tma.idlesmart;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import vn.com.tma.idlesmart.Utils.SendToGateway;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by ntmhanh on 5/3/2017.
 */
@RunWith(AndroidJUnit4.class)
public class sendCscheaderToGatewayInstrumentTest {
    JSONObject jsonObject = new JSONObject();

    @Before
    public void setUp() throws JSONException {
        //Arrange
        jsonObject.put("format", 12);
        jsonObject.put("start", "0123456789ABCDEF");
        jsonObject.put("size", "0123456789ABCDEF");
        jsonObject.put("tra", "0123456789ABCDEF");
        jsonObject.put("signature", "0123456789ABCDEF");
        jsonObject.put("crc_1", "0123456789ABCDEF");
        jsonObject.put("crc_2", "0123456789ABCDEF");
        jsonObject.put("crc_3", "0123456789ABCDEF");
        jsonObject.put("block_count", "0123456789ABCDEF");
        jsonObject.put("version", "0123456789ABCDEF");
    }
    @Test
    public void sendCscHeaderToGateway_InputJsonObject_return() throws JSONException {
        //Arrange
         SendToGateway mock = Mockito.spy(SendToGateway.class);
        byte[] parentArray = new byte[16767];

        int index = mock.sendCscHeaderToGateway(jsonObject, parentArray);

        verify(mock, times(9)).hexStringToByteArray(anyString());

    }

}

