package vn.com.tma.idlesmart.Utils;

/**
 * Created by ntmhanh on 5/8/2017.
 */

public class TimeConverter {

    public String time2MinsSecsStr(int time) {
        int mins = time / 60;
        int secs = time - (mins * 60);
        if (secs < 10) {
            return Integer.toString(mins) + ":0" + Integer.toString(secs);
        }
        return Integer.toString(mins) + ":" + Integer.toString(secs);
    }
}
