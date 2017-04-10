package vn.com.tma.idlesmart;

import android.app.Activity;


/**
 * Created by ntmhanh on 4/10/2017.
 */

/**
 * Create KioskModeActivity to implement some method of KioskService instead of doing on MainActivity
 */
public class KioskModeActivity extends Activity{
    public static boolean KioskMode = false;


    /**
     * Kioskmode: Disable back button
     */
    @Override
    public void onBackPressed() {
        if (!KioskMode) {
            super.onBackPressed();
        }
    }

}
