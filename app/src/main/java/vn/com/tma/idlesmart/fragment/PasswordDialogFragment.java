package vn.com.tma.idlesmart.fragment;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import vn.com.tma.idlesmart.MainActivity;
import vn.com.tma.idlesmart.R;
import vn.com.tma.idlesmart.Utils.IntegerParser;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * Created by ntmhanh on 5/12/2017.
 */

public class PasswordDialogFragment extends DialogFragment {

    public PasswordDialogFragment() {
    }

    public static PasswordDialogFragment newInstance (){
        PasswordDialogFragment passwordDialogFragment = new PasswordDialogFragment();
        return passwordDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.password_dialog, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ((TextView) view.findViewById(R.id.passwordEditText)).setText("");
        view.findViewById(R.id.passwordReturnButton).setOnClickListener(new PasswordReturnListener());
        view.findViewById(R.id.passwordContinueButton).setOnClickListener(new PaswordContinueListener());
    }

    /**
     * Action listener for PasswordReturn button
     */
    class PasswordReturnListener implements View.OnClickListener {
        public void onClick(View v) {
            dismiss();
            MainActivity.PasswordValid = false;
        }
    }
    /**
     * Action listener for PasswordContinue button
     */
    class PaswordContinueListener implements View.OnClickListener {
        public void onClick(View v) {
            dismiss();
            IntegerParser integerParser = new IntegerParser();
            int pwtemp = integerParser.toInteger(((EditText) getView().findViewById(R.id.passwordEditText)).getText().toString());
            MainActivity.PasswordValid = pwtemp == MainActivity.Password ? true : false;
            if (MainActivity.test_mode && pwtemp == 8800) {
                MainActivity.PasswordEnable = false;
                MainActivity.PasswordValid = true;
            }
        }
    }


}
