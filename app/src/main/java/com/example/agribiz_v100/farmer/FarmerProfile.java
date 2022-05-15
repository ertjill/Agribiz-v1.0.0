package com.example.agribiz_v100.farmer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.LoginActivity;
import com.example.agribiz_v100.OrdersActivity;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.agrovit.AgrovitMainActivity;
import com.example.agribiz_v100.customer.BarterGoodsActivity;
import com.example.agribiz_v100.customer.EditProfile;
import com.example.agribiz_v100.services.AuthManagement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.microedition.khronos.opengles.GL;

public class FarmerProfile extends Fragment {

    CardView logout_card;
    TextView displayName_tv, farmerMessages_tv, farmerBarter_tv,edit_profile_tv;
    ImageView displayImage_iv;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView to_prepare_ib,to_ship_ib,to_receive_ib,rate_ib;
    @Override
    public void onResume() {
        super.onResume();
        Glide.with(getContext())
                .load(user.getPhotoUrl())
                .into(displayImage_iv);
        displayName_tv.setText(user.getDisplayName().substring(0,user.getDisplayName().length()-2));
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_farmer_profile, container, false);

        logout_card = view.findViewById(R.id.logout_card);
        displayName_tv=view.findViewById(R.id.displayName_tv);
        displayName_tv.setText(user.getDisplayName().substring(0,user.getDisplayName().length()-2));
        displayImage_iv = view.findViewById(R.id.displayImage_iv);
        edit_profile_tv = view.findViewById(R.id.edit_profile_tv);
        farmerMessages_tv = view.findViewById(R.id.farmerMessages_tv);
        farmerBarter_tv = view.findViewById(R.id.farmerBarter_tv);
        to_prepare_ib=view.findViewById(R.id.to_prepare_ib);
        to_prepare_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), OrdersActivity.class);
                intent.putExtra("tab",0);
//                intent.setClassName(getContext(), OrdersActivity.class);
                startActivity(intent);
            }
        });
        to_ship_ib=view.findViewById(R.id.to_ship_ib);
        to_ship_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), OrdersActivity.class);
                intent.putExtra("tab",1);
//                intent.setClassName(getContext(),"com.example.agribiz_v100.OrdersActivity");
                startActivity(intent);
            }
        });
        to_receive_ib=view.findViewById(R.id.to_receive_ib);
        to_receive_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), OrdersActivity.class);
                intent.putExtra("tab",2);
                //intent.setClassName(getContext(),"com.example.agribiz_v100.OrdersActivity");
                startActivity(intent);
            }
        });
        rate_ib=view.findViewById(R.id.rate_ib);
        rate_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), OrdersActivity.class);
                intent.putExtra("tab",3);
                //intent.setClassName(getContext(),"com.example.agribiz_v100.OrdersActivity");
                startActivity(intent);
            }
        });
        Glide.with(getContext())
                .load(user.getPhotoUrl())
                .into(displayImage_iv);

        edit_profile_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FarmerEditProfileActivity.class);
                startActivity(intent);
            }
        });

        farmerMessages_tv.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), FarmerMessagesActivity.class));
        });

        farmerBarter_tv.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), BarterProductActivity.class));
        });

        logout_card.setOnClickListener(vi->{
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Logging out");
            alert.setMessage("Do you want to logout?");
            alert.setNegativeButton("No", null);
            alert.setPositiveButton("Yes", (dialog, which) -> {
                AuthManagement.logoutAccount();
                Intent i = new Intent(getContext(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                getActivity().finishAffinity();
            });
            alert.show();
        });

        return view;
    }
}