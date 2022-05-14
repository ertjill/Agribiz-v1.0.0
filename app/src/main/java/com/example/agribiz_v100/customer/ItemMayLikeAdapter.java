package com.example.agribiz_v100.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.ProductModel;

import java.util.List;

public class ItemMayLikeAdapter extends BaseAdapter {

    Context context;
    List<ProductModel> productList;
    //String[] imageList;
    LayoutInflater layoutInflater;

    public ItemMayLikeAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return productList.size();
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
        item_like_name_tv.setText(productList.get(position).getProductName());
        Glide.with(context)
                .load(productList.get(position).getProductImage().size() < 1 ? R.drawable.sample_product : productList.get(position).getProductImage().get(0))
                .into(item_like_iv);

        return convertView;
    }
}
