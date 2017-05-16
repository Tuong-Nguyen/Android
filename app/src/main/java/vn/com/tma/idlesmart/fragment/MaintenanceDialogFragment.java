package vn.com.tma.idlesmart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;

import vn.com.tma.idlesmart.R;
import vn.com.tma.idlesmart.Utils.IntegerParser;
import vn.com.tma.idlesmart.listener.MaintenanceDialogFragmentListener;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * Created by ntmhanh on 5/16/2017.
 */

public class MaintenanceDialogFragment extends DialogFragment {

    public MaintenanceDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maint_dialog, container);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final IntegerParser integerParser = new IntegerParser();

        this.getDialog().requestWindowFeature(FEATURE_NO_TITLE);
        this.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        boolean enableLogFile = this.getArguments().getBoolean("enableLogFile");
        String valueLogFile = this.getArguments().getString("valueLogFile");
        boolean enableClutchOverride = this.getArguments().getBoolean("enableClutchOverride");
        String valueClutchOverride = this.getArguments().getString("valueClutchOverride");
        boolean enableIdleTimeOverride = this.getArguments().getBoolean("enableIdleTimeOverride");
        String valueIdleTimeOverride = this.getArguments().getString("valueIdleTimeOverride");
        boolean enableEngineSpeedAdjustments = this.getArguments().getBoolean("enableEngineSpeedAdjustments");
        String valueEngineSpeedAdjustments = this.getArguments().getString("valueEngineSpeedAdjustments");
        boolean enableTimeStampRMP = this.getArguments().getBoolean("enableTimeStampRMP");
        String valueTimeStampRMP = this.getArguments().getString("valueTimeStampRMP");
        boolean enableNeutralSwitchDetection = this.getArguments().getBoolean("enableNeutralSwitchDetection");
        String valueNeutralSwitchDetection = this.getArguments().getString("valueNeutralSwitchDetection");
        boolean enableReserved = this.getArguments().getBoolean("enableReserved");
        String valueReserved = this.getArguments().getString("valueReserved");
        boolean enableSeverRoute = this.getArguments().getBoolean("enableSeverRoute");
        String apiRoute = this.getArguments().getString("apiRoute");
        boolean enableRestoreFactoryDefaults = this.getArguments().getBoolean("enableRestoreFactoryDefaults");
        String valueRestoreFactoryDefaults = this.getArguments().getString("valueRestoreFactoryDefaults");
        boolean enableViewServeCommunication = this.getArguments().getBoolean("enableViewServeCommunication");
        String valueViewServeCommunication = this.getArguments().getString("valueViewServeCommunication");


        ((CheckBox) view.findViewById(R.id.maintCheckBox_1)).setChecked(enableLogFile);
        ((EditText) view.findViewById(R.id.maintText_1)).setText(valueLogFile);
        ((CheckBox) view.findViewById(R.id.maintCheckBox_2)).setChecked(enableClutchOverride);
        ((EditText) view.findViewById(R.id.maintText_2)).setText(valueClutchOverride);
        ((CheckBox) view.findViewById(R.id.maintCheckBox_3)).setChecked(enableIdleTimeOverride);
        ((EditText) view.findViewById(R.id.maintText_3)).setText(valueIdleTimeOverride);
        ((CheckBox) view.findViewById(R.id.maintCheckBox_4)).setChecked(enableEngineSpeedAdjustments);
        ((EditText) view.findViewById(R.id.maintText_4)).setText(valueEngineSpeedAdjustments);
        ((CheckBox) view.findViewById(R.id.maintCheckBox_5)).setChecked(enableTimeStampRMP);
        ((EditText) view.findViewById(R.id.maintText_5)).setText(valueTimeStampRMP);
        ((CheckBox) view.findViewById(R.id.maintCheckBox_6)).setChecked(enableNeutralSwitchDetection);
        ((EditText) view.findViewById(R.id.maintText_6)).setText(valueNeutralSwitchDetection);
        ((CheckBox) view.findViewById(R.id.maintCheckBox_7)).setChecked(enableReserved);
        ((EditText) view.findViewById(R.id.maintText_7)).setText(valueReserved);
        ((CheckBox) view.findViewById(R.id.maintCheckBox_8)).setChecked(enableSeverRoute);
        ((EditText) view.findViewById(R.id.maintText_8)).setText(apiRoute);
        ((CheckBox) view.findViewById(R.id.maintCheckBox_9)).setChecked(enableRestoreFactoryDefaults);
        ((EditText) view.findViewById(R.id.maintText_9)).setText(valueRestoreFactoryDefaults);
        ((CheckBox) view.findViewById(R.id.maintCheckBox_10)).setChecked(enableViewServeCommunication);
        ((EditText) view.findViewById(R.id.maintText_10)).setText(valueViewServeCommunication);
        view.findViewById(R.id.maintDoneButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<Boolean> aMaintEnable = new ArrayList<Boolean>();
                aMaintEnable.add(((CheckBox) getView().findViewById(R.id.maintCheckBox_1)).isChecked());
                aMaintEnable.add(((CheckBox) getView().findViewById(R.id.maintCheckBox_2)).isChecked());
                aMaintEnable.add(((CheckBox) getView().findViewById(R.id.maintCheckBox_3)).isChecked());
                aMaintEnable.add(((CheckBox) getView().findViewById(R.id.maintCheckBox_4)).isChecked());
                aMaintEnable.add(((CheckBox) getView().findViewById(R.id.maintCheckBox_5)).isChecked());
                aMaintEnable.add(((CheckBox) getView().findViewById(R.id.maintCheckBox_6)).isChecked());
                aMaintEnable.add(((CheckBox) getView().findViewById(R.id.maintCheckBox_7)).isChecked());
                aMaintEnable.add(((CheckBox) getView().findViewById(R.id.maintCheckBox_8)).isChecked());
                aMaintEnable.add(((CheckBox) getView().findViewById(R.id.maintCheckBox_9)).isChecked());
                aMaintEnable.add(((CheckBox) getView().findViewById(R.id.maintCheckBox_10)).isChecked());

                ArrayList<Integer> aMaintValue = new ArrayList<Integer>();
                aMaintValue.add(integerParser.toInteger(((EditText) view.findViewById(R.id.maintText_1)).getText().toString()));
                aMaintValue.add(integerParser.toInteger(((EditText) view.findViewById(R.id.maintText_2)).getText().toString()));
                aMaintValue.add(integerParser.toInteger(((EditText) view.findViewById(R.id.maintText_3)).getText().toString()));
                aMaintValue.add(integerParser.toInteger(((EditText) view.findViewById(R.id.maintText_4)).getText().toString()));
                aMaintValue.add(integerParser.toInteger(((EditText) view.findViewById(R.id.maintText_5)).getText().toString()));
                aMaintValue.add(integerParser.toInteger(((EditText) view.findViewById(R.id.maintText_6)).getText().toString()));
                aMaintValue.add(integerParser.toInteger(((EditText) view.findViewById(R.id.maintText_7)).getText().toString()));
                aMaintValue.add(integerParser.toInteger(((EditText) view.findViewById(R.id.maintText_9)).getText().toString()));
                aMaintValue.add(integerParser.toInteger(((EditText) view.findViewById(R.id.maintText_10)).getText().toString()));

                MaintenanceDialogFragmentListener maintenanceDialogFragmentListener = (MaintenanceDialogFragmentListener) getActivity();
                maintenanceDialogFragmentListener.onDoneMaintenance(aMaintEnable, aMaintValue);
                dismiss();
            }
        });

        view.findViewById(R.id.maintSuperExitButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MaintenanceDialogFragmentListener maintenanceDialogFragmentListener = (MaintenanceDialogFragmentListener) getActivity();
                maintenanceDialogFragmentListener.onExitMaintenance();
                dismiss();
            }
        });
    }
}
