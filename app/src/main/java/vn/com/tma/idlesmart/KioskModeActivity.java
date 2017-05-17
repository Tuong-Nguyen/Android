package vn.com.tma.idlesmart;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by ntmhanh on 4/10/2017.
 */

/**
 * Create KioskModeActivity to implement some method of KioskService instead of doing on MainActivity
 */
public class KioskModeActivity extends FragmentActivity{
    public static boolean KioskMode = false;
    private  List blockedKeys;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Integer[] numArr = new Integer[2];
        numArr[0] = KeyEvent.KEYCODE_VOLUME_DOWN;
        numArr[1] = KeyEvent.KEYCODE_VOLUME_UP;
        this.blockedKeys = new ArrayList(Arrays.asList(numArr));
    }

    /**
     * Kioskmode: Disable back button
     */
    @Override
    public void onBackPressed() {
        if (!KioskMode) {
            super.onBackPressed();
        }
    }
    /**
     * KioskMode: disable Volume_Up and Volume_Down button
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KioskMode && this.blockedKeys.contains(event.getKeyCode())) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
