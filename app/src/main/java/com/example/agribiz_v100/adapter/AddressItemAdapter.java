package com.example.agribiz_v100.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.customer.MyAddressesActivity;
import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.services.ProfileManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddressItemAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    SparseArray<LocationModel> addressItems;
    Activity activity;

    public AddressItemAdapter(Activity activity,Context context, SparseArray<LocationModel> addressItems) {
        this.activity =activity;
        this.context = context;
        this.addressItems = addressItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.address_list_item, null);
        TextView add_fullName_tv = view.findViewById(R.id.add_fullName_tv);
        TextView add_phoneNum_tv = view.findViewById(R.id.add_phoneNum_tv);
        TextView full_address_tv = view.findViewById(R.id.full_address_tv);
        ImageButton delete_address_ib = view.findViewById(R.id.delete_address_ib);

        add_fullName_tv.setText(addressItems.get(i).getUserFullName());
        add_phoneNum_tv.setText(addressItems.get(i).getUserPhoneNumber());
        full_address_tv.setText(new StringBuilder()
                .append(addressItems.get(i).getUserSpecificAddress()).append(", ")
                .append(addressItems.get(i).getUserBarangay()).append(", ")
                .append(addressItems.get(i).getUserMunicipality()).append(", ")
                .append(addressItems.get(i).getUserProvince()).append(", ")
                .append(addressItems.get(i).getUserRegion()).append(", ")
                .append(addressItems.get(i).getUserZipCode()).toString());

        delete_address_ib.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            AlertDialog.Builder dialogbox = new AlertDialog.Builder(activity);
            dialogbox.setMessage("Do you really want to delete this address?");
            dialogbox.setCancelable(true);
            dialogbox.setTitle("Delete Address");
            dialogbox.setCancelable(false);
            dialogbox.setPositiveButton("Yes way", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int s) {
                    Log.d("GetAddress","Yes way");
                    ProfileManagement.deleteAddress(addressItems.get(i), user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        AuthValidation.successToast(context, "Successfully removed address").show();
                                        addressItems.remove(i);
                                        notifyDataSetChanged();
                                    } else {
                                        AuthValidation.failedToast(context, task.getException().getMessage()).show();
                                    }
                                }
                            });
                }
            });
            dialogbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d("GetAddress","No way");
                }
            });
            //AlertDialog xyz = dialogbox.create();
            dialogbox.show();
        });
        return view;
    }

    @Override
    public int getCount() {
        return addressItems.size();
    }

    @Override
    public LocationModel getItem(int i) {
        return addressItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
