package vn.com.tma.idlesmart.listener;

import java.util.ArrayList;

/**
 * Created by ntmhanh on 5/16/2017.
 */

public interface MaintenanceDialogFragmentListener {
    void onDoneMaintenance(ArrayList<Boolean> aMaintEnable, ArrayList<Integer> aMaintValue);
    void onExitMaintenance();
}
