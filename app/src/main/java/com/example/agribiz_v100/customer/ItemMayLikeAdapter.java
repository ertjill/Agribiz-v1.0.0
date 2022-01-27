package com.example.agribiz_v100.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.agribiz_v100.R;

public class ItemMayLikeAdapter extends BaseAdapter {

    Context context;
    String[] productList;
    //String[] imageList;
    LayoutInflater layoutInflater;

    public ItemMayLikeAdapter(Context context, String[] productList) {
        this.context = context;
        this.productList = productList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return productList.length;
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
        convertView = layoutInflater.inflate(R.layout.you_might_also_like_item_card,null);
        TextView item_like_name_tv= convertView.findViewById(R.id.item_like_name_tv);
        ImageView item_like_iv= convertView.findViewById(R.id.item_like_iv);
        item_like_name_tv.setText(productList[position]);
        item_like_iv.setImageResource(R.drawable.sample_product);

        return convertView;
    }
}
