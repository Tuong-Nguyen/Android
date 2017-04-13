package vn.com.tma.idlesmart;

/**
 * Created by ntmhanh on 4/13/2017.
 */

public class StringAndVoltUtils {

    /**
     * Convert string to voltage
     * @param battstr
     * @return the number of voltage
     */
    public int battStr2mv(String battstr) {
        try {
            return (Integer.parseInt(battstr.substring(0, 2)) * 10) + Integer.parseInt(battstr.substring(3, 4));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Convert voltage to string
     * @param battvolt
     * @return volt string
     */
    public String battmv2Str(int battvolt) {
        int volts = battvolt / 10;
        String voltstr = Integer.toString(volts);
        return voltstr + "." + Integer.toString(battvolt - (volts * 10));
    }
}
