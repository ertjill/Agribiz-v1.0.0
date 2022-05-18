package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.ExpenseModel;
import com.example.agribiz_v100.services.FinanceManagement;
import com.example.agribiz_v100.validation.ExpenseValidation;
import com.example.agribiz_v100.validation.RequestAssistanceValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddExpensesDialog {

    Activity activity;
    Fragment fragment;
    Dialog dialog;
    AddExpenseAddedCallback addExpenseAddedCallback;
    Button cancel_btn, addExpense_btn;
    TextInputLayout expenseName_til, totalCost_til;
    ExpenseModel expenseModel;
    public AddExpensesDialog(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.dialog = new Dialog(activity);
        this.fragment = fragment;
        expenseModel = new ExpenseModel();
    }

    public void showDialog(AddExpenseAddedCallback addExpenseAddedCallback) {
        this.addExpenseAddedCallback=addExpenseAddedCallback;
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
        addExpense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ExpenseValidation.validateExpenseName(expenseName_til.getEditText().getText().toString()).isEmpty()){
                    expenseName_til.getEditText().findFocus();
                }else if(!ExpenseValidation.validateExpenseCost(totalCost_til.getEditText().getText().toString()).isEmpty()){
                    totalCost_til.getEditText().findFocus();
                }
                else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Date dateNow = new Date();
                    expenseModel.setExpenseId(user.getUid()+new SimpleDateFormat("ddMMyyyyHHmmss").format(dateNow));
                    expenseModel.setExpenseUserId(user.getUid());
                    expenseModel.setExpenseName(expenseName_til.getEditText().getText().toString());
                    expenseModel.setExpenseCost(Double.parseDouble(totalCost_til.getEditText().getText().toString()));
                    expenseModel.setExpenseDateAdded(new Timestamp(dateNow));
                    FinanceManagement.addExpenses(activity,expenseModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dismissDialog();
                            if(task.isSuccessful()){
                                addExpenseAddedCallback.setExpenseAddedListener(expenseModel);

                            }
                        }
                    });
                }
            }
        });
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

    public interface AddExpenseAddedCallback{
        void setExpenseAddedListener(ExpenseModel expenseModel);
    }
}
