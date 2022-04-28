package com.example.agribiz_v100.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.entities.ProductModel;

public class BarterProductAdapter extends BaseAdapter {

    Context context;
    SparseArray<BarterModel> barterItems;
    LayoutInflater layoutInflater;

    public BarterProductAdapter(Context context, SparseArray<BarterModel> barterItems) {
        this.context = context;
        this.barterItems = barterItems;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return barterItems.size();
    }

    @Override
    public BarterModel getItem(int i) {
        return barterItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View barterListView, ViewGroup viewGroup) {

        TextView barterName_tv;
        TextView barterCondition_tv;
        TextView barterQuantity_tv;
        TextView barterDesc_tv;

        barterListView = layoutInflater.inflate(R.layout.farmer_barter_product_list, null);

        barterName_tv = barterListView.findViewById(R.id.barterName_tv);
        barterCondition_tv = barterListView.findViewById(R.id.barterCondition_tv);
        barterQuantity_tv = barterListView.findViewById(R.id.barterQuantity_tv);
        barterDesc_tv = barterListView.findViewById(R.id.barterDesc_tv);

        String condition = "Item condition: " + barterItems.get(i).getBarterCondition();
        String quantity = "Quantity: " + barterItems.get(i).getBarterQuantity();
        String desc = "Description: " + barterItems.get(i).getBarterDescription();

        barterName_tv.setText(barterItems.get(i).getBarterName());
        barterCondition_tv.setText(condition);
        barterQuantity_tv.setText(quantity);
        barterDesc_tv.setText(desc);

        return barterListView;
    }
}
