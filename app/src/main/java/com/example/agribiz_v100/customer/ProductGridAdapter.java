package com.example.agribiz_v100.customer;

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
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.services.ProfileManagement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class ProductGridAdapter extends BaseAdapter {
    Context context;
    SparseArray<ProductModel> product;

    LayoutInflater layoutInflater;

    public ProductGridAdapter(Context context, SparseArray<ProductModel> product) {
        this.context = context;
        this.product = product;
    }

    @Override
    public int getCount() {
        return product.size();
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



        if(layoutInflater==null){
            layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.product_item_card,null);
        }

        ImageView imageView = convertView.findViewById(R.id.product_iv);
        TextView productName = convertView.findViewById(R.id.product_name_tv);
        TextView productUnit = convertView.findViewById(R.id.productUnit_tv);
        TextView productPrice = convertView.findViewById(R.id.productPrice_tv);
        TextView product_location_tv = convertView.findViewById(R.id.product_location_tv);
        ProfileManagement.getUserProfile(product.get(position).getProductUserId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                product_location_tv.setText(((Map<String,String >)documentSnapshot.getData().get("userLocation")).get("userMunicipality"));
            }
        });
        Glide.with(context)
                .load(product.get(position).getProductImage().get(0))
                .into(imageView);
        productName.setText(product.get(position).getProductName());
        productUnit.setText("(per "+product.get(position).getProductQuantity()+" "+product.get(position).getProductUnit()+")");
        productPrice.setText("Php "+product.get(position).getProductPrice());

        return convertView;
    }
}
