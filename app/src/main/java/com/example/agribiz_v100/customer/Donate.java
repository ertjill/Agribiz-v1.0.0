package com.example.agribiz_v100.customer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.dialog.DonateFarmerDialog;


public class Donate extends Fragment {

    Button donate_btn;
    TextView not_available2;
    ListView farmer_list_lv;

    DonateFarmerDialog donateFarmerDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donate, container, false);
        not_available2 = view.findViewById(R.id.not_available2);
        farmer_list_lv = view.findViewById(R.id.farmer_list_lv);
        farmer_list_lv.setEmptyView(not_available2);

        donate_btn = view.findViewById(R.id.donate_btn);

        donateFarmerDialog =  new DonateFarmerDialog(getActivity(), this);
        donateFarmerDialog.buildDialog();

        donate_btn.setOnClickListener(v -> {
            donateFarmerDialog.showDialog();
        });

        return view ;
    }
}