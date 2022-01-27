package com.example.agribiz_v100.customer;

import android.app.Activity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends Fragment {

    CardView logout_card;
    TextView displayName_tv;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        displayName_tv = view.findViewById(R.id.displayName_tv);
        if(user.getDisplayName()!=null) {
            String[] displayName = user.getDisplayName().split("-");
            displayName_tv.setText(displayName[0]);
        }
        logout_card=view.findViewById(R.id.logout_card);

        logout_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }
}