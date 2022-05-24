package com.example.agribiz_v100.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.User;
import com.example.agribiz_v100.customer.DonationActivity;
import com.example.agribiz_v100.dialog.DonateFarmerDialog;
import com.example.agribiz_v100.entities.ChatThreadUserModel;
import com.example.agribiz_v100.entities.FarmerUserModel;
import com.example.agribiz_v100.validation.AuthValidation;

import java.util.List;

public class DonationFarmerAdapter extends BaseAdapter {
    List<FarmerUserModel> userModels;
    Context context;

    public DonationFarmerAdapter( Context context,List<FarmerUserModel> userModels) {
        this.userModels = userModels;
        this.context = context;
    }

    @Override
    public int getCount() {
        return userModels.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.farmers_item_list_layout,null);
        TextView farmer_display_name_tv = convertView.findViewById(R.id.farmer_display_name_tv);
        TextView user_info_tv = convertView.findViewById(R.id.user_info_tv);
        ImageView farmer_image_iv = convertView.findViewById(R.id.farmer_image_iv);
        Button donate_btn = convertView.findViewById(R.id.donate_btn);
        String name = userModels.get(position).getUserDisplayName();
        farmer_display_name_tv.setText(name.substring(0,name.length()-2));
        Glide.with(context).load(userModels.get(position).getUserImage()).into(farmer_image_iv);
        String info = userModels.get(position).getUserFirstName()+" "+userModels.get(position).getUserLastName()+
                " | "+userModels.get(position).getUserPhoneNumber()+"\n"+
                userModels.get(position).getUserLocation().getUserSpecificAddress()+"\n"+
                userModels.get(position).getUserLocation().getUserBarangay()+", "+
                userModels.get(position).getUserLocation().getUserMunicipality()+", "+
                userModels.get(position).getUserLocation().getUserProvince()+", "+
                userModels.get(position).getUserLocation().getUserZipCode();
        user_info_tv.setText(info);

        donate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Donate");
                alert.setMessage("Do you want to donate this farmer?");
                alert.setCancelable(false);
                alert.setNegativeButton("No",null);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AuthValidation.failedToast(context,"Under development").show();
//                        context.startActivity(new Intent(context,DonationActivity.class));
                    }
                });
                alert.show();
            }
        });

        return convertView;
    }
}
