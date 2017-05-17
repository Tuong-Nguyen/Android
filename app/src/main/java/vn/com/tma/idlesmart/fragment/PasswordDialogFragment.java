package vn.com.tma.idlesmart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import vn.com.tma.idlesmart.R;
import vn.com.tma.idlesmart.Utils.IntegerParser;
import vn.com.tma.idlesmart.listener.PasswordDialogFragmentListener;

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
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ((TextView) view.findViewById(R.id.passwordEditText)).setText("");
        view.findViewById(R.id.passwordReturnButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordDialogFragmentListener passwordDialogFragmentListener = (PasswordDialogFragmentListener)getActivity();
                passwordDialogFragmentListener.onReturnListener();
                dismiss();
            }
        });
        view.findViewById(R.id.passwordContinueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntegerParser integerParser = new IntegerParser();
                int pwtemp = integerParser.toInteger(((EditText) getView().findViewById(R.id.passwordEditText)).getText().toString());
                PasswordDialogFragmentListener passwordDialogFragmentListener = (PasswordDialogFragmentListener)getActivity();
                passwordDialogFragmentListener.onContinueListener(pwtemp);
                dismiss();
            }
        });
    }

}