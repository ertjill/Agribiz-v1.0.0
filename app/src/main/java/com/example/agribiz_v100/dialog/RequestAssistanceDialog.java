package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.AssistanceModel;
import com.example.agribiz_v100.services.AssistanceManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.example.agribiz_v100.validation.RequestAssistanceValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RequestAssistanceDialog {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    AssistanceModel assistanceModel;

    Activity activity;
    Fragment fragment;
    Dialog dialog;

    final Calendar myCalendar = Calendar.getInstance();

    RadioGroup radioGroup_assistanceType;
    RadioButton financialSupport_rb, borrow_farmEquipments_rb, borrow_money_rb;
    Button cancel_btn, submitRequest_btn;
    TextInputLayout assistanceDescription_til, assistanceAmountEquipment_til, assistanceRepayDate_til;

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
        borrow_farmEquipments_rb = dialog.findViewById(R.id.borrow_farmEquipments_rb);
        borrow_money_rb = dialog.findViewById(R.id.borrow_money_rb);

        assistanceDescription_til = dialog.findViewById(R.id.assistanceDescription_til);
        assistanceAmountEquipment_til = dialog.findViewById(R.id.assistanceAmountEquipment_til);
        assistanceRepayDate_til = dialog.findViewById(R.id.assistanceRepayDate_til);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };
        assistanceRepayDate_til.getEditText().setOnClickListener(v -> {
            new DatePickerDialog(dialog.getContext(), R.style.DialogTheme, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        radioGroup_assistanceType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.financialSupport_rb) {
                    assistanceAmountEquipment_til.setVisibility(View.VISIBLE);
                    assistanceAmountEquipment_til.setHint("Enter amount");
                    assistanceAmountEquipment_til.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                    assistanceAmountEquipment_til.setError(null);
                    assistanceAmountEquipment_til.getEditText().setText("");
                    assistanceAmountEquipment_til.requestFocus();
                    assistanceRepayDate_til.setVisibility(View.GONE);
                }
                if (i == R.id.borrow_money_rb) {
                    assistanceAmountEquipment_til.setVisibility(View.VISIBLE);
                    assistanceAmountEquipment_til.setHint("Amount to borrow");
                    assistanceAmountEquipment_til.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                    assistanceAmountEquipment_til.setError(null);
                    assistanceAmountEquipment_til.getEditText().setText("");
                    assistanceAmountEquipment_til.requestFocus();
                    assistanceRepayDate_til.setVisibility(View.VISIBLE);
                    assistanceRepayDate_til.setHint("Select date to repay");
                }
                if (i == R.id.borrow_farmEquipments_rb) {
                    assistanceAmountEquipment_til.setVisibility(View.VISIBLE);
                    assistanceAmountEquipment_til.setHint("Specify equipment");
                    assistanceAmountEquipment_til.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                    assistanceAmountEquipment_til.setError(null);
                    assistanceAmountEquipment_til.getEditText().setText("");
                    assistanceAmountEquipment_til.requestFocus();
                    assistanceRepayDate_til.setVisibility(View.VISIBLE);
                    assistanceRepayDate_til.setHint("Select return date");
                }
                if (i == -1) {
                    assistanceAmountEquipment_til.setVisibility(View.GONE);
                    assistanceRepayDate_til.setVisibility(View.GONE);
                }
            }
        });

        assistanceAmountEquipment_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                switch(radioGroup_assistanceType.getCheckedRadioButtonId()) {
                    case R.id.financialSupport_rb:
                    case R.id.borrow_money_rb:
                        if (!RequestAssistanceValidation.validateRequestAmount(cs.toString()).isEmpty()) {
                            assistanceAmountEquipment_til.setError(RequestAssistanceValidation.validateRequestAmount(cs.toString()));
                        } else {
                            assistanceAmountEquipment_til.setError(null);
                        }
                        break;
                    case R.id.borrow_farmEquipments_rb:
                        if (!RequestAssistanceValidation.validateRequestEquipment(cs.toString()).isEmpty()) {
                            assistanceAmountEquipment_til.setError(RequestAssistanceValidation.validateRequestEquipment(cs.toString()));
                        } else {
                            assistanceAmountEquipment_til.setError(null);
                        }
                        break;
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
            if (error()) {
                assistanceModel = new AssistanceModel();

                Date dateNow = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                Timestamp timestamp = new Timestamp(dateNow);

                int selectedType = radioGroup_assistanceType.getCheckedRadioButtonId();
                RadioButton selectedAssistanceType = radioGroup_assistanceType.findViewById(selectedType);

                String assistanceType = selectedAssistanceType.getText().toString();
                String assistanceAmountEquipment = assistanceAmountEquipment_til.getEditText().getText().toString();
                String assistanceDesc = assistanceDescription_til.getEditText().getText().toString();
                String assistanceRepay = "";

                if (!borrow_money_rb.isChecked() && !borrow_farmEquipments_rb.isChecked()) {
                    assistanceRepay = "None";
                }
                else {
                    assistanceRepay = assistanceRepayDate_til.getEditText().getText().toString();
                }

                assistanceModel.setAssistanceUserID(user.getUid());
                assistanceModel.setAssistanceID(user.getUid() + simpleDateFormat.format(dateNow));
                assistanceModel.setAssistanceType(assistanceType);
                assistanceModel.setAssistanceAmountEquipment(assistanceAmountEquipment);
                assistanceModel.setAssistanceDescription(assistanceDesc);
                assistanceModel.setAssistanceStatus("Pending");
                assistanceModel.setAssistanceRepayDate(assistanceRepay);
                assistanceModel.setAssistanceDateRequested(timestamp);

                Log.d("data", "0" + assistanceModel.getAssistanceID());
                Log.d("data", "1" + assistanceModel.getAssistanceType());
                Log.d("data", "2" + assistanceModel.getAssistanceAmountEquipment());
                Log.d("data", "3" + assistanceModel.getAssistanceDescription());
                Log.d("data", "4" + assistanceModel.getAssistanceStatus());
                Log.d("data", "4" + assistanceModel.getAssistanceRepayDate());
                Log.d("data", "6" + assistanceModel.getAssistanceDateRequested());

                AssistanceManagement.addRequestAssistance(assistanceModel, activity).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AuthValidation.successToast(activity.getBaseContext(), "Successfully requested assistance").show();
                            reset();
                            dismissDialog();
                        }
                        else {
                            AuthValidation.failedToast(activity.getBaseContext(), task.getException().getLocalizedMessage()).show();
                            Log.d("data",task.getException().getLocalizedMessage());
                            task.getException().printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private boolean error() {
        if (!financialSupport_rb.isChecked() && !borrow_farmEquipments_rb.isChecked() && !borrow_money_rb.isChecked()) {
            AuthValidation.failedToast(dialog.getContext(), "Please select assistant type").show();
            return false;
        }
        else if (TextUtils.isEmpty(assistanceAmountEquipment_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please specify amount or equipment").show();
            return false;
        }
        else if (borrow_money_rb.isChecked() && TextUtils.isEmpty(assistanceRepayDate_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please specify date").show();
            return false;
        }
        else if (TextUtils.isEmpty(assistanceDescription_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please enter request purpose").show();
            return false;
        }
        return true;
    }

    private void updateLabel() {
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        assistanceRepayDate_til.getEditText().setText(dateFormat.format(myCalendar.getTime()));
    }

    private void reset() {
        radioGroup_assistanceType.clearCheck();
        assistanceAmountEquipment_til.getEditText().setText("");
        assistanceAmountEquipment_til.setError(null);
        assistanceAmountEquipment_til.clearFocus();
        assistanceDescription_til.getEditText().setText("");
        assistanceDescription_til.setError(null);
        assistanceDescription_til.clearFocus();
        assistanceRepayDate_til.getEditText().setText("");
        assistanceRepayDate_til.setError(null);
        assistanceRepayDate_til.clearFocus();
    }
}
