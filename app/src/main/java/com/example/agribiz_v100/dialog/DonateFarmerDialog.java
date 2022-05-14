package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.validation.AuthValidation;
import com.example.agribiz_v100.validation.BarterValidation;
import com.example.agribiz_v100.validation.DonateValidation;
import com.google.android.material.textfield.TextInputLayout;

public class DonateFarmerDialog {

    Activity activity;
    Fragment fragment;
    Dialog dialog;

    TextInputLayout farmerName_til, amountDonate_til, shortMessage_til;
    Button upload_slip_btn, cancel_btn, donate_btn;

    public DonateFarmerDialog(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
        this.dialog = new Dialog(activity);
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        reset();
        dialog.dismiss();
    }

    public void buildDialog() {
        dialog.setContentView(R.layout.customer_donate_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        farmerName_til = dialog.findViewById(R.id.farmerName_til);
        amountDonate_til = dialog.findViewById(R.id.amountDonate_til);
        shortMessage_til = dialog.findViewById(R.id.shortMessage_til);
        upload_slip_btn = dialog.findViewById(R.id.upload_slip_btn);
        cancel_btn = dialog.findViewById(R.id.cancel_btn);
        donate_btn = dialog.findViewById(R.id.donate_btn);

        cancel_btn.setOnClickListener(v -> {
            dismissDialog();
        });

        donate_btn.setOnClickListener(v -> {
            if (error()) {
                // Make some magic here
            }
        });

        amountDonate_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this blank
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this blank
            }
            @Override
            public void afterTextChanged(Editable e) {
                if (!DonateValidation.validateAmount(e.toString()).isEmpty()) {
                    amountDonate_til.setError(DonateValidation.validateAmount(e.toString()));
                } else {
                    amountDonate_til.setError(null);
                }
            }
        });

        shortMessage_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this blank
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this blank
            }
            @Override
            public void afterTextChanged(Editable e) {
                if (!DonateValidation.validateShortMessage(e.toString()).isEmpty()) {
                    shortMessage_til.setError(DonateValidation.validateShortMessage(e.toString()));
                } else {
                    shortMessage_til.setError(null);
                }
            }
        });
    }

    public void reset() {
        farmerName_til.getEditText().setText("");
        farmerName_til.setError(null);
        farmerName_til.clearFocus();
        amountDonate_til.getEditText().setText("");
        amountDonate_til.setError(null);
        amountDonate_til.clearFocus();
        shortMessage_til.getEditText().setText("");
        shortMessage_til.setError(null);
        shortMessage_til.clearFocus();
    }

    private boolean error() {
        if (TextUtils.isEmpty(amountDonate_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Enter amount to donate").show();
            return false;
        }
        else if (TextUtils.isEmpty(shortMessage_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please enter short message.").show();
            return false;
        }
        return true;
    }
}
