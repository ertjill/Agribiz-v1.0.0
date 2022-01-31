package com.example.agribiz_v100.farmer;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.agribiz_v100.LoginActivity;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.agrovit.AgrovitMainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class FarmerProfile extends Fragment {

    CardView logout_card;
    TextView displayName_tv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_farmer_profile, container, false);
        logout_card = view.findViewById(R.id.logout_card);
        displayName_tv=view.findViewById(R.id.displayName_tv);
        logout_card.setOnClickListener(vi->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });
        return view;
    }
}