package vn.com.tma.idlesmart;

import org.junit.Test;

import vn.com.tma.idlesmart.Utils.ConvertJsonObjectToByteArray;

import static org.junit.Assert.assertEquals;

/**
 * Created by ntmhanh on 5/8/2017.
 */

public class HexStringToByteArrayTest {
    @Test
    public void hexStringToByteArray_InputStringHexWithout0x_ReturnArrayByteWith26Byte(){
        //Arrange
        String hex = "1a";
        ConvertJsonObjectToByteArray convertJsonObjectToByteArray = new ConvertJsonObjectToByteArray();
        byte[] byteTest = new byte[1];
        byteTest[0] = 26;
        //Action
        byte[] returnArray = convertJsonObjectToByteArray.hexStringToByteArray(hex);
        //Assert
        assertEquals(returnArray.length, byteTest.length);
        assertEquals(returnArray[0], byteTest[0]);
    }
    @Test
    public void hexStringToByteArray_InputStringHexWith0x_ReturnArrayByteWith26Byte(){
        //Arrange
        String hex = "0x1a";
        ConvertJsonObjectToByteArray convertJsonObjectToByteArray = new ConvertJsonObjectToByteArray();
        byte[] byteTest = new byte[1];
        byteTest[0] = 26;
        //Action
        byte[] returnArray = convertJsonObjectToByteArray.hexStringToByteArray(hex);
        //Assert
        assertEquals(returnArray.length, byteTest.length);
        assertEquals(returnArray[0], byteTest[0]);
    }
}