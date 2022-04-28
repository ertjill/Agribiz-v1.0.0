package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.validation.ProductValidation;
import com.example.agribiz_v100.validation.RequestAssistanceValidation;
import com.google.android.material.textfield.TextInputLayout;

public class RequestAssistanceDialog {

    Activity activity;
    Fragment fragment;
    Dialog dialog;

    RadioGroup radioGroup_assistanceType;
    Button cancel_btn, submitRequest_btn;
    TextInputLayout assistanceDescription_til;

    public RequestAssistanceDialog(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.dialog = new Dialog(activity);
        this.fragment = fragment;
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        reset();
        dialog.dismiss();
    }

    public void buildDialog() {
        dialog.setContentView(R.layout.farmer_request_assistance_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        cancel_btn = dialog.findViewById(R.id.cancel_btn);
        submitRequest_btn = dialog.findViewById(R.id.submitRequest_btn);
        radioGroup_assistanceType = dialog.findViewById(R.id.radioGroup_assistanceType);
        assistanceDescription_til = dialog.findViewById(R.id.assistanceDescription_til);

        assistanceDescription_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                if (!RequestAssistanceValidation.validateRequestDescription(cs.toString()).isEmpty()) {
                    assistanceDescription_til.setError(RequestAssistanceValidation.validateRequestDescription(cs.toString()));
                } else {
                    assistanceDescription_til.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Leave this block empty
            }
        });

        cancel_btn.setOnClickListener(v -> {
            dismissDialog();
        });

        submitRequest_btn.setOnClickListener(v -> {
            // to be continued...
        });
    }

    public void reset() {
        radioGroup_assistanceType.clearCheck();
        assistanceDescription_til.getEditText().setText("");
        assistanceDescription_til.setError(null);
        assistanceDescription_til.clearFocus();
    }
}
