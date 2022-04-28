package com.example.agribiz_v100.farmer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.dialog.RequestAssistanceDialog;

public class AgriHelp extends Fragment {

    Button requestAssistance_btn;
    RequestAssistanceDialog requestAssistanceDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agri_help, container, false);
        requestAssistance_btn = view.findViewById(R.id.requestAssistance_btn);

        requestAssistanceDialog = new RequestAssistanceDialog(getActivity(), this);
        requestAssistanceDialog.buildDialog();

        requestAssistance_btn.setOnClickListener(view1 -> {
            requestAssistanceDialog.showDialog();
        });

        // Inflate the layout for this fragment
        return view;
    }
}