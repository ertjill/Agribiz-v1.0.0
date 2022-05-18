package com.example.agribiz_v100.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.BarterModel;

import java.util.List;

public class NotOpenBarterAdapter extends BaseAdapter {

    List<BarterModel> barteredItems;
    Context context;

    public NotOpenBarterAdapter(List<BarterModel> barteredItems, Context context) {
        this.barteredItems = barteredItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return barteredItems.size();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.barter_list_item_layout,null);


        ImageView cutomer_image_iv, barterImage_iv;
        TextView product_tv, barterCondition_tv,barterDesc_tv,barterQuantity_tv,user_name,address_tv;
        Button cancel_btn,swap_btn;

        cutomer_image_iv = convertView.findViewById(R.id.cutomer_image_iv);
        barterImage_iv = convertView.findViewById(R.id.barterImage_iv);
        product_tv = convertView.findViewById(R.id.product_tv);
        barterCondition_tv = convertView.findViewById(R.id.barterCondition_tv);
        barterDesc_tv = convertView.findViewById(R.id.barterDesc_tv);
        barterQuantity_tv = convertView.findViewById(R.id.barterQuantity_tv);
        user_name = convertView.findViewById(R.id.user_name);
        address_tv = convertView.findViewById(R.id.address_tv);
        cancel_btn = convertView.findViewById(R.id.cancel_btn);
        swap_btn = convertView.findViewById(R.id.swap_btn);



        return convertView;
    }
}
