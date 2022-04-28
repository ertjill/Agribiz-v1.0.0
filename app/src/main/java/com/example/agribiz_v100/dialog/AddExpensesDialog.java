package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.validation.ExpenseValidation;
import com.example.agribiz_v100.validation.RequestAssistanceValidation;
import com.google.android.material.textfield.TextInputLayout;

public class AddExpensesDialog {

    Activity activity;
    Fragment fragment;
    Dialog dialog;

    Button cancel_btn, addExpense_btn;
    TextInputLayout expenseName_til, totalCost_til;

    public AddExpensesDialog(Activity activity, Fragment fragment) {
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
        dialog.setContentView(R.layout.farmer_add_expenses_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        cancel_btn = dialog.findViewById(R.id.cancel_btn);
        addExpense_btn = dialog.findViewById(R.id.addExpense_btn);

        expenseName_til = dialog.findViewById(R.id.expenseName_til);
        totalCost_til = dialog.findViewById(R.id.totalCost_til);

        expenseName_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                if (!ExpenseValidation.validateExpenseName(cs.toString()).isEmpty()) {
                    expenseName_til.setError(ExpenseValidation.validateExpenseName(cs.toString()));
                } else {
                    expenseName_til.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Leave this block empty
            }
        });

        totalCost_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                if (!ExpenseValidation.validateExpenseCost(cs.toString()).isEmpty()) {
                    totalCost_til.setError(ExpenseValidation.validateExpenseCost(cs.toString()));
                } else {
                    totalCost_til.setError(null);
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
    }

    public void reset() {
        expenseName_til.getEditText().setText("");
        expenseName_til.setError(null);
        totalCost_til.getEditText().setText("");
        totalCost_til.setError(null);
    }
}
