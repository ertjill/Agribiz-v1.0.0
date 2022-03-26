package com.example.agribiz_v100.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.LoginActivity;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.services.AuthManagement;
import com.example.agribiz_v100.services.ProfileManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends Fragment {

    private static final String TAG = "Profile";
    CardView logout_card;
    TextView displayName_tv, edit_profile_tv,my_address_tv;
    ImageView userImage_iv;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    public void onResume() {
        super.onResume();
        Glide.with(getContext())
                .load(user.getPhotoUrl())
                .into(userImage_iv);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        my_address_tv = view.findViewById(R.id.my_address_tv);
        displayName_tv = view.findViewById(R.id.displayName_tv);
        userImage_iv = view.findViewById(R.id.userImage_iv);
        edit_profile_tv = view.findViewById(R.id.edit_profile_tv);

        my_address_tv.setOnClickListener(t->{
            startActivity(new Intent(getContext(),AddressActivity.class));
        });

        if (!user.getDisplayName().equals("")) {
            String[] displayName = user.getDisplayName().split("-");
            displayName_tv.setText(user.getDisplayName().substring(0, user.getDisplayName().length() - 2));
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .into(userImage_iv);
        }

        logout_card = view.findViewById(R.id.logout_card);

        edit_profile_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EditProfile.class);
                startActivity(intent);
            }
        });
        logout_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthManagement.logoutAccount();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }
}