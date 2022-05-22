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
import com.example.agribiz_v100.adapter.DonationFarmerAdapter;
import com.example.agribiz_v100.dialog.DonateFarmerDialog;
import com.example.agribiz_v100.entities.ChatThreadUserModel;
import com.example.agribiz_v100.entities.FarmerUserModel;
import com.example.agribiz_v100.services.UserManagement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Donate extends Fragment {

//    Button donate_btn;
    TextView not_available2;
    ListView farmer_list_lv;
    List<FarmerUserModel> farmers;
    DonateFarmerDialog donateFarmerDialog;
    DonationFarmerAdapter donationFarmerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donate, container, false);
        not_available2 = view.findViewById(R.id.not_available2);
        farmer_list_lv = view.findViewById(R.id.farmer_list_lv);
        farmer_list_lv.setEmptyView(not_available2);
//        donate_btn = view.findViewById(R.id.donate_btn);
        farmers = new ArrayList<>();
        donationFarmerAdapter = new DonationFarmerAdapter(getContext(), farmers);
        farmer_list_lv.setAdapter(donationFarmerAdapter);

        donateFarmerDialog = new DonateFarmerDialog(getActivity(), this);
        donateFarmerDialog.buildDialog();
//        donate_btn.setOnClickListener(v -> {
//            donateFarmerDialog.showDialog();
//        });
        getFarmers();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getFarmers() {
        UserManagement.getFarmers().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                farmers.clear();
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot ds : queryDocumentSnapshots) {
                        farmers.add(ds.toObject(FarmerUserModel.class));
                    }
                }
                donationFarmerAdapter.notifyDataSetChanged();
            }
        });
    }
}