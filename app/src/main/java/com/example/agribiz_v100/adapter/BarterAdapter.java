package com.example.agribiz_v100.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarterAdapter extends BaseAdapter {

    List<BarterModel> barterItems;
    Context context;
    LayoutInflater inflater;
    public BarterAdapter(Context context, List<BarterModel> barterItems) {
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

        view = inflater.inflate(R.layout.farmer_barter_item_list_layout, null);

        TextView barterName_tv = view.findViewById(R.id.barterName_tv);
        TextView barterCondition_tv = view.findViewById(R.id.barterCondition_tv);
        TextView barterQuantity_tv = view.findViewById(R.id.barterQuantity_tv);
        TextView barterDesc_tv = view.findViewById(R.id.barterDesc_tv);
        TextView address_tv = view.findViewById(R.id.address_tv);
        Chip barterStatus_chip = view.findViewById(R.id.barterStatus_chip);
        ImageView barterImage_iv = view.findViewById(R.id.barterImage_iv);
        Button remove_btn = view.findViewById(R.id.remove_btn);
        Glide.with(context)
                .load(barterItems.get(pos).getBarterImage() != null && !barterItems.get(pos).getBarterImage().isEmpty() ? barterItems.get(pos).getBarterImage().get(0) : "")
                .into(barterImage_iv);

        if (barterItems.get(pos).getBarterStatus().equals("Pending")) {
            remove_btn.setText("Cancel");
            remove_btn.setBackgroundColor(Color.parseColor("#C4C4C4"));
        }
        else if(barterItems.get(pos).getBarterStatus().equals("Swapping")){
            remove_btn.setText("Swapped");
            remove_btn.setBackgroundColor(Color.parseColor("#F2B705"));
        }

        barterStatus_chip.setText(barterItems.get(pos).getBarterStatus());
        String userId = barterItems.get(pos).getBarterUserId();
        ProfileManagement.getUserProfile(userId)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       if(documentSnapshot.exists()){
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
                            }

                            address_tv.setText(address);
                        }
                    }
                });

        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barterItems.get(pos).getBarterType().equals("c")&&barterItems.get(pos).getBarterStatus().equals("Pending")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Cancel Barter Request");
                    alert.setMessage("Are you sure to cancel this request?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BarterManagement.cancelProposedBarter(context, barterItems.get(pos));
                        }
                    });
                    alert.setNegativeButton("No", null);
                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Remove Item");
                    alert.setMessage("Are you sure you want to remove this item?");
                    alert.setCancelable(false);
                    alert.setNegativeButton("No", null);
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BarterManagement.removeBarteredItem(barterItems.get(pos))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            AuthValidation.successToast(context, "Barter item removed").show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                            alert.setTitle("Fail to remove barter item");
                                            alert.setMessage(e.getLocalizedMessage());
                                            alert.setCancelable(false);
                                            alert.setPositiveButton("Okay", null);
                                            alert.show();
                                        }
                                    });
                        }
                    });
                    alert.show();
                }
            }
        });

        String name = barterItems.get(pos).getBarterName();
        String condition = "Item condition: " + barterItems.get(pos).getBarterCondition();
        String quantity = "Quantity: " + barterItems.get(pos).getBarterQuantity() + " " + barterItems.get(pos).getBarterUnit();
        String desc = "Description: " + barterItems.get(pos).getBarterDescription();

        barterName_tv.setText(name);
        barterCondition_tv.setText(condition);
        barterQuantity_tv.setText(quantity);
        barterDesc_tv.setText(desc);

        return view;
    }
}