package vn.com.tma.idlesmart;

import org.junit.Test;

import vn.com.tma.idlesmart.Utils.BatteryVoltageConverter;

import static junit.framework.Assert.assertEquals;

/**
 * Created by ntmhanh on 4/14/2017.
 */

public class VoltageConverterUnitTest {
    @Test
    public void batteryVoltageToString_8VoltTo0point8String(){
        // Arrange
        int volt = 8;
        BatteryVoltageConverter batteryVoltageConverter = new BatteryVoltageConverter();
        //Action
        String strVolt = batteryVoltageConverter.batteryMilliVoltToString(volt);
        // Assert
        assertEquals("Convert volt to string successfully ", "0.8", strVolt);
    }

    @Test
    public void batteryStringToMilliVolt_StringMilliVolt8000To800MilliVolt(){
        // Arrange
        String strVolt = "8000";
        BatteryVoltageConverter batteryVoltageConverter = new BatteryVoltageConverter();
        //Action
        int volt = batteryVoltageConverter.batteryStringToMilliVolt(strVolt);
        // Assert
        assertEquals("Convert string to volt successfully ", 800, volt);
    }
}
