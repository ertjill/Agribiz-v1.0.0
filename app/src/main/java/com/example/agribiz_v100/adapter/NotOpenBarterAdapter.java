package com.example.agribiz_v100.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.services.BarterManagement;
import com.example.agribiz_v100.services.ProfileManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotOpenBarterAdapter extends BaseAdapter {

    List<BarterModel> barterItems;
    Context context;
    LayoutInflater inflater;

    public NotOpenBarterAdapter(Context context, List<BarterModel> barterItems) {
        this.barterItems = barterItems;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return barterItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.barter_list_item_layout, null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ImageView user_image_iv, barterImage_iv;
        Chip barterStatus_chip;
        TextView user_name, item_name_tv, barterCondition_tv, barterDesc_tv, barterQuantity_tv, address_tv;
        Button cancel_btn, swap_btn;

        user_image_iv = view.findViewById(R.id.user_image_iv);
        barterImage_iv = view.findViewById(R.id.barterImage_iv);
        barterStatus_chip = view.findViewById(R.id.barterStatus_chip);
        user_name = view.findViewById(R.id.user_name);
        item_name_tv = view.findViewById(R.id.item_name_tv);
        barterCondition_tv = view.findViewById(R.id.barterCondition_tv);
        barterDesc_tv = view.findViewById(R.id.barterDesc_tv);
        barterQuantity_tv = view.findViewById(R.id.barterQuantity_tv);
        address_tv = view.findViewById(R.id.address_tv);
        cancel_btn = view.findViewById(R.id.cancel_btn);
        swap_btn = view.findViewById(R.id.swap_btn);

        Glide.with(context)
                .load(barterItems.get(pos).getBarterImage().get(0))
                .into(barterImage_iv);
        barterStatus_chip.setText(barterItems.get(pos).getBarterStatus());
        item_name_tv.setText(barterItems.get(pos).getBarterName());
        barterCondition_tv.setText("Item Condition: " + barterItems.get(pos).getBarterCondition());
        barterDesc_tv.setText("Description: " + barterItems.get(pos).getBarterDescription());
        barterQuantity_tv.setText("Quantity: " + barterItems.get(pos).getBarterQuantity() + " " + barterItems.get(pos).getBarterUnit());

        if (barterItems.get(pos).getBarterStatus().equals("Completed")) {
            cancel_btn.setVisibility(View.GONE);
            swap_btn.setVisibility(View.GONE);
        } else if (barterItems.get(pos).getBarterStatus().equals("Swapping") && barterItems.get(pos).getBarterType().equals("f")) {
            swap_btn.setVisibility(View.GONE);
        }else if(barterItems.get(pos).getBarterStatus().equals("Swapping") && barterItems.get(pos).getBarterType().equals("c")){
            swap_btn.setVisibility(View.VISIBLE);
            swap_btn.setText("Swapped");
        }
        else if(barterItems.get(pos).getBarterStatus().equals("Pending")&&barterItems.get(pos).getBarterType().equals("c")){
            swap_btn.setVisibility(View.GONE);
        }

        String userId = barterItems.get(pos).getBarterUserId();
//        db.collection("barters").whereEqualTo("barterMatchId", barterItems.get(pos).getBaterMatchId())
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
//                            if (ds.getString("barterType").equals("c"))
//                            {
//
//                            }
//                        }
//                    }
//                });

        ProfileManagement.getUserProfile(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("userDisplayName");
                String address = "";
                Map<String, String> map = new HashMap<>();
                if (name.charAt(name.length() - 1) == 'c') {
                    List<Object> loc = (List<Object>) documentSnapshot.getData().get("userLocation");
                    map = (Map<String, String>) loc.get(0);
                    address = name.substring(0, name.length() - 2) + " | "
                            + documentSnapshot.getString("userPhoneNumber") + "\n"
                            + map.get("userSpecificAddress") + ", " +
                            map.get("userBarangay") + ", " +
                            map.get("userMunicipality") + ", " +
                            map.get("userProvince") + "\n" +
                            map.get("userRegion") + ", " +
                            map.get("userZipCode");
                    user_name.setText(name.substring(0, name.length() - 2));
                } else {
                    map = (Map<String, String>) documentSnapshot.get("userLocation");
                    address = name.substring(0, name.length() - 2) + " | "
                            + documentSnapshot.getString("userPhoneNumber") + "\n"
                            + map.get("userSpecificAddress") + ", " +
                            map.get("userBarangay") + ", " +
                            map.get("userMunicipality") + ", " +
                            map.get("userProvince") + "\n" +
                            map.get("userRegion") + ", " +
                            map.get("userZipCode");
                    user_name.setText(name.substring(0, name.length() - 2));
                }


                Glide.with(context).load(documentSnapshot.getString("userImage"))
                        .into(user_image_iv);

                address_tv.setText(address);

            }
        });

        cancel_btn.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Cancel Barter Request");
            alert.setMessage("Are you sure to cancel this request?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("barters").whereEqualTo("barterMatchId", barterItems.get(pos).getBarterMatchId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                if (ds.getString("barterType").equals("c"))
                                    BarterManagement.farmerCancelProposedBarter(context, barterItems.get(pos), ds.toObject(BarterModel.class));
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Error");
                            alert.setMessage(e.getLocalizedMessage());
                            alert.setPositiveButton("Ok", null);
                            alert.setCancelable(false);
                            alert.show();

                        }
                    });

                }
            });
            alert.setNegativeButton("No", null);
            alert.setCancelable(false);
            alert.show();

        });
        swap_btn.setOnClickListener(v -> {
            if(barterItems.get(pos).getBarterStatus().equals("Pending"))
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("To Swap");
                alert.setMessage("Are you sure to tag this request to swap?");
                alert.setCancelable(false);
                alert.setNegativeButton("No", null);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("barters").whereEqualTo("barterMatchId", barterItems.get(pos).getBarterMatchId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                Log.d("hr","hihihihi inside onSucess");
                                for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                    Log.d("hr","hihihihi inside loop");
                                    if (ds.getString("barterType").equals("c"))
                                        BarterManagement.swapBarteredItem(context, barterItems.get(pos), ds.toObject(BarterModel.class));
                                }
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("Error");
                                alert.setMessage(e.getLocalizedMessage());
                                alert.setPositiveButton("Ok", null);
                                alert.setCancelable(false);
                                alert.show();

                            }
                        });
                    }
                });
                alert.show();
            }
            else if(barterItems.get(pos).getBarterStatus().equals("Swapping") && barterItems.get(pos).getBarterType().equals("c")){
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Swapped");
                alert.setMessage("Are you sure to tag this request as Swapped?");
                alert.setCancelable(false);
                alert.setNegativeButton("No", null);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("barters").whereEqualTo("barterMatchId", barterItems.get(pos).getBarterMatchId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                    if (ds.getString("barterType").equals("f"))
                                    {
                                        BarterManagement.swappedBarteredItem(context,ds.toObject(BarterModel.class),barterItems.get(pos));
                                    }
                                }
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("Error");
                                alert.setMessage(e.getLocalizedMessage());
                                alert.setPositiveButton("Ok", null);
                                alert.setCancelable(false);
                                alert.show();

                            }
                        });
                    }
                });
                alert.show();
            }
        });

        return view;
    }
}
