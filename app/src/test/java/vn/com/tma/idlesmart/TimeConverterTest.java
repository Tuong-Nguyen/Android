package vn.com.tma.idlesmart;

import org.junit.Test;

import vn.com.tma.idlesmart.Utils.TimeConverter;

import static junit.framework.Assert.assertEquals;

/**
 * Created by ntmhanh on 5/8/2017.
 */

public class TimeConverterTest {

    @Test
    public void time2MinsSecsStr_Input560SecsWithSurplusOfSecsSmallerThan10_Return9Mins_0Secs(){
        TimeConverter timeConverter = new TimeConverter();
        String time = timeConverter.time2MinsSecsStr(540);
        assertEquals("9:00", time);
    }
    @Test
    public void time2MinsSecsStr_Input560SecsWithSurplusOfSecsBiggerThan10_Return9Mins_0Secs(){
        TimeConverter timeConverter = new TimeConverter();
        String time = timeConverter.time2MinsSecsStr(560);
        assertEquals("9:20", time);
    }

}
