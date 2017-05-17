package vn.com.tma.idlesmart.Utils;

/**
 * Created by ntmhanh on 5/8/2017.
 */

public class IntegerParser {
    /**
     * Check Integer of string input
     * @param input
     * @return
     */
    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parse string to integer
     * @param input
     * @return
     */
    public int toInteger(String input) {
        if (isInteger(input)) {
            return Integer.valueOf(input).intValue();
        }
        return 0;
    }
}
