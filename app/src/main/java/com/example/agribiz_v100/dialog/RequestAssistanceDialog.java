package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.customer.AddressActivity;
import com.example.agribiz_v100.entities.AssistanceModel;
import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.services.AssistanceManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.example.agribiz_v100.validation.ProductValidation;
import com.example.agribiz_v100.validation.RequestAssistanceValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RequestAssistanceDialog {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    AssistanceModel assistanceModel;

    Activity activity;
    Fragment fragment;
    Dialog dialog;

    RadioGroup radioGroup_assistanceType;
    RadioButton financialSupport_rb, borrow_money_rb;
    Button cancel_btn, submitRequest_btn;
    TextInputLayout assistanceDescription_til, assistanceAmount_til;

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
        financialSupport_rb = dialog.findViewById(R.id.financialSupport_rb);
        borrow_money_rb = dialog.findViewById(R.id.borrow_money_rb);

        assistanceDescription_til = dialog.findViewById(R.id.assistanceDescription_til);
        assistanceAmount_til = dialog.findViewById(R.id.assistanceAmount_til);

        radioGroup_assistanceType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.financialSupport_rb || i == R.id.borrow_money_rb) {
                    assistanceAmount_til.setVisibility(View.VISIBLE);
                } else {
                    assistanceAmount_til.setVisibility(View.GONE);
                }
            }
        });

        assistanceAmount_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                if (!RequestAssistanceValidation.validateRequestAmount(cs.toString()).isEmpty()) {
                    assistanceAmount_til.setError(RequestAssistanceValidation.validateRequestAmount(cs.toString()));
                } else {
                    assistanceAmount_til.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Leave this block empty
            }
        });

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
            assistanceModel = new AssistanceModel();

            int selectedType = radioGroup_assistanceType.getCheckedRadioButtonId();
            RadioButton selectedAssistanceType = radioGroup_assistanceType.findViewById(selectedType);

            String assistanceType = selectedAssistanceType.getText().toString();
            String assistanceAmount = assistanceAmount_til.getEditText().getText().toString();
            String assistanceDesc = assistanceDescription_til.getEditText().getText().toString();

            assistanceModel.setAssistanceID(user.getUid());
            assistanceModel.setAssistanceType(assistanceType);
            assistanceModel.setAssistanceAmount(assistanceAmount);
            assistanceModel.setAssistanceDescription(assistanceDesc);

            Log.d("data", "0" + assistanceModel.getAssistanceID());
            Log.d("data", "1" + assistanceModel.getAssistanceType());
            Log.d("data", "2" + assistanceModel.getAssistanceAmount());
            Log.d("data", "3" + assistanceModel.getAssistanceDescription());

            AssistanceManagement.addRequestAssistance(assistanceModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        AuthValidation.successToast(activity.getBaseContext(), "Successfully requested assistance").show();
                        reset();
                    }
                    else {
                        AuthValidation.failedToast(activity.getBaseContext(), task.getException().getMessage()).show();
                        Log.d("data",task.getException().getLocalizedMessage());
                        task.getException().printStackTrace();
                    }
                }
            });
        });
    }

    public void reset() {
        radioGroup_assistanceType.clearCheck();
        assistanceDescription_til.getEditText().setText("");
        assistanceDescription_til.setError(null);
        assistanceDescription_til.clearFocus();
    }
}
