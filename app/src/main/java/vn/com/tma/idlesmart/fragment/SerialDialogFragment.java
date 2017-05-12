package vn.com.tma.idlesmart.fragment;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import vn.com.tma.idlesmart.R;

import static vn.com.tma.idlesmart.MainActivity.Gateway_FWversion;
import static vn.com.tma.idlesmart.MainActivity.Gateway_LDRversion;
import static vn.com.tma.idlesmart.MainActivity.Gateway_SerialID;
import static vn.com.tma.idlesmart.MainActivity.Gateway_VIN;

/**
 * Created by ntmhanh on 5/12/2017.
 */

public class SerialDialogFragment extends DialogFragment {

    public SerialDialogFragment() {
    }

    public static SerialDialogFragment newInstance(Parcelable parcelable) {
        SerialDialogFragment serialDialogFragment = new SerialDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("PackageInfo",parcelable);
        serialDialogFragment.setArguments(args);
        return serialDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.serial_dialog, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ((TextView) view.findViewById(R.id.serialVIN_Text)).setText(Gateway_VIN);
        ((TextView) view.findViewById(R.id.serialGWSerialID_Text)).setText(Gateway_SerialID);
        ((TextView) view.findViewById(R.id.serialLDRversion_Text)).setText(Gateway_LDRversion);
        ((TextView) view.findViewById(R.id.serialGWversion_Text)).setText(Gateway_FWversion);
        PackageInfo pInfo = getArguments().getParcelable("PackageInfo");
        if (pInfo != null) {
            ((TextView) view.findViewById(R.id.serialAndroidVersion_Text)).setText(pInfo.versionName);
        }
        view.findViewById(R.id.serialDoneButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               dismiss();
            }
        });
    }
}
