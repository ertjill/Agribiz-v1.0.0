package com.example.agribiz_v100.customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.LoginActivity;
import com.example.agribiz_v100.MessagesActivity;
import com.example.agribiz_v100.OrdersActivity;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.services.AuthManagement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends Fragment {

    private static final String TAG = "Profile";
    CardView logout_card;
    TextView displayName_tv, edit_profile_tv,my_address_tv, barter_goods_tv, messages_tv, view_order_history;
    ImageView userImage_iv;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView to_prepare_ib,to_ship_ib,to_receive_ib,rate_ib;
    @Override
    public void onResume() {
        super.onResume();
        Glide.with(getContext())
            .load(user.getPhotoUrl())
            .into(userImage_iv);
        displayName_tv.setText(user.getDisplayName().substring(0,user.getDisplayName().length()-2));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        view_order_history = view.findViewById(R.id.view_order_history);
        my_address_tv = view.findViewById(R.id.my_address_tv);
        displayName_tv = view.findViewById(R.id.displayName_tv);
        userImage_iv = view.findViewById(R.id.userImage_iv);
        edit_profile_tv = view.findViewById(R.id.edit_profile_tv);
        barter_goods_tv = view.findViewById(R.id.barter_goods_tv);
        messages_tv = view.findViewById(R.id.messages_tv);
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

        view_order_history.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), OrdersActivity.class));
        });

        my_address_tv.setOnClickListener(t->{
            startActivity(new Intent(getActivity(), MyAddressesActivity.class));
        });

        if (!user.getDisplayName().equals("")) {
            String[] displayName = user.getDisplayName().split("-");
            displayName_tv.setText(user.getDisplayName().substring(0, user.getDisplayName().length() - 2));
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .into(userImage_iv);
        }

        barter_goods_tv.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               //startActivity(new Intent(getActivity(), MyAddressesActivity.class));
               startActivity(new Intent(getActivity(), BarterGoodsActivity.class));
           }
        });

        messages_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MessagesActivity.class));
            }
        });

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
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Logging out");
                alert.setMessage("Do you want to logout?");
                alert.setNegativeButton("No", null);
                alert.setPositiveButton("Yes", (dialog, which) -> {
                    AuthManagement.logoutAccount(getActivity()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent i = new Intent(getContext(), LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            getActivity().finishAffinity();
                        }
                    });
                });
                alert.show();

            }
        });

        return view;
    }
}