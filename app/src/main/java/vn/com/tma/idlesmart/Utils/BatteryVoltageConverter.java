package vn.com.tma.idlesmart.Utils;


public class BatteryVoltageConverter {

    /**
     * Convert string to voltage
     * @param batteryString
     * @return the number of voltage
     */
    public int batteryStringToVoltage(String batteryString) {
        try {
            return (Integer.parseInt(batteryString.substring(0, 2)) * 10) + Integer.parseInt(batteryString.substring(3, 4));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Convert voltage to string
     * @param batteryVoltage
     * @return volt string
     */
    public String batteryVoltageToString(int batteryVoltage) {
        int volts = batteryVoltage / 10;
        String voltstr = Integer.toString(volts);
        return voltstr + "." + Integer.toString(batteryVoltage - (volts * 10));
    }
}
