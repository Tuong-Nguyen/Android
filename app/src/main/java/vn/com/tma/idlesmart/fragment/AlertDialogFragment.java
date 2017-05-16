package vn.com.tma.idlesmart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.com.tma.idlesmart.R;
import vn.com.tma.idlesmart.listener.AlertDialogFragmentListener;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * Created by ntmhanh on 5/12/2017.
 */

public class AlertDialogFragment extends DialogFragment {

    public AlertDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alert_dialog, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(FEATURE_NO_TITLE);
        String faultMessage = this.getArguments().getString("faultMessage");
        String faultDesc = this.getArguments().getString("faultDesc");
        final int faultId = this.getArguments().getInt("faultId");
        ((TextView) view.findViewById(R.id.alertName)).setText(faultMessage);
        ((TextView) view.findViewById(R.id.alertDescription)).setText(faultDesc);
        view.findViewById(R.id.alertRefreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogFragmentListener alertDialogListener = (AlertDialogFragmentListener) getActivity();
                alertDialogListener.onFreshListener(faultId);
                dismiss();
            }
        });

    }
}
