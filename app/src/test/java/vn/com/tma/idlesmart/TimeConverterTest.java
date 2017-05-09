package vn.com.tma.idlesmart;

import org.junit.Test;

import vn.com.tma.idlesmart.Utils.TimeConverter;

import static junit.framework.Assert.assertEquals;

/**
 * Created by ntmhanh on 5/8/2017.
 */

public class TimeConverterTest {

    @Test
    public void time2MinsSecsStr_Input606Secs_ReturnLikeFormatIs10Mins_06Secs(){
        //Arrange
        TimeConverter timeConverter = new TimeConverter();
        //Action
        String time = timeConverter.time2MinsSecsStr(606);
        //Assert
        assertEquals("10:06", time);
    }
    @Test
    public void time2MinsSecsStr_Input30Secs_ReturnLikeFormatIs0Mins_31Secs(){
        //Arrange
        TimeConverter timeConverter = new TimeConverter();
        //Action
        String time = timeConverter.time2MinsSecsStr(31);
        //Assert
        assertEquals("0:31", time);
    }

    @Test
    public void time2MinsSecsStr_Input541SecsWithSurplusOfSecsSmallerThan10_ReturnLikeFormatIs9Mins_01Secs(){
        //Arrange
        TimeConverter timeConverter = new TimeConverter();
        //Action
        String time = timeConverter.time2MinsSecsStr(541);
        //Assert
        assertEquals("9:01", time);
    }

    @Test
    public void time2MinsSecsStr_Input560SecsWithSurplusOfSecsIs20_Return9Mins_20Secs(){
        //Arrange
        TimeConverter timeConverter = new TimeConverter();
        //Action
        String time = timeConverter.time2MinsSecsStr(560);
        //Assert
        assertEquals("9:20", time);
    }

}
