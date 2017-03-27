package vn.com.tma.idlesmart;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Dashboard extends Fragment {
    OnDashboardSelectionListener mCallback;

    public interface OnDashboardSelectionListener {
        void onDashboardSelection(int i);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mCallback = (OnDashboardSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDashboardSelectionListener");
        }
    }

    private void onClick(View v) {
        this.mCallback.onDashboardSelection(456);
    }

    private void SendActivity(int id) {
        this.mCallback.onDashboardSelection(id);
    }

    public void updateView(int id) {
        Log.d("Dashboard", "data: " + id);
        SendActivity(id);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(C0010R.layout.dashboard, container, false);
    }
}
