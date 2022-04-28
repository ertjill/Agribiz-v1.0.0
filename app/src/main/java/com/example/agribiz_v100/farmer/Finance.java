package com.example.agribiz_v100.farmer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.dialog.AddExpensesDialog;

public class Finance extends Fragment {

    Button addExpense_btn;
    AddExpensesDialog addExpensesDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finance, container, false);
        addExpense_btn = view.findViewById(R.id.addExpense_btn);

        addExpensesDialog = new AddExpensesDialog(getActivity(), this);
        addExpensesDialog.buildDialog();

        addExpense_btn.setOnClickListener(v -> {
            addExpensesDialog.showDialog();
        });

        return view;
    }
}