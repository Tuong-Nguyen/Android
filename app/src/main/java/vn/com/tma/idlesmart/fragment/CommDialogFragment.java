package vn.com.tma.idlesmart.fragment;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import vn.com.tma.idlesmart.R;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * Created by ntmhanh on 5/12/2017.
 */

public class CommDialogFragment extends DialogFragment{

    public CommDialogFragment() {
    }

    public static CommDialogFragment newInstance(String commlogstr){
        CommDialogFragment commDialogFragment = newInstance(commlogstr);
        Bundle bundle = new Bundle();
        bundle.putString("commlogstr", commlogstr);
        commDialogFragment.setArguments(bundle);
        return commDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comm_dialog, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        TextView commlogtext = (TextView) view.findViewById(R.id.commlog_Text);
        String commlog = getArguments().getString("commlogstr");
        commlogtext.setText(commlog);
        view.findViewById(R.id.commDoneButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
