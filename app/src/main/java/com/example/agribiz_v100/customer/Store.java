package com.example.agribiz_v100.customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agribiz_v100.R;

public class Store extends Fragment {

    HorizontalScrollView farmersHub_hv;
    LinearLayout farmersHub_ll;
    GridLayout topProduct_gl;
    ListView item_may_like_lv;
    TextView viewAll_tv;
    String[] productName = {"Sweet Tomato","Sweet Tomato","Sweet Tomato","Sweet Tomato","Sweet Tomato","Sweet Tomato","Sweet Tomato","Sweet Tomato","Sweet Tomato","Sweet Tomato"};
    String[] productUnit ={"kg","kg","kg","kg","kg","kg","kg","kg","kg","kg"};
    String[] productPrice={"87.00","87.00","87.00","87.00","87.00","87.00","87.00","87.00","87.00","87.00"};
    int[] image = {R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product};

    String[] item_may_like={"Sweet Tomato","Sweet Tomato","Sweet Tomato","Sweet Tomato","Sweet Tomato"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);
//        ProductGridAdapter productGridAdapter = new ProductGridAdapter(getContext(),productName,productUnit,productPrice,image);
//        GridView topProduct_gv = view.findViewById(R.id.topProduce_gv);
//        topProduct_gv.setAdapter(productGridAdapter);

//        topProduct_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), "Product item", Toast.LENGTH_SHORT).show();
//            }
//        });


        farmersHub_hv = view.findViewById(R.id.farmersHub_hv);
        farmersHub_ll = view.findViewById(R.id.farmersHub_ll);
        topProduct_gl = view.findViewById(R.id.topProduct_gl);
        item_may_like_lv = view.findViewById(R.id.item_may_like_lv);
        viewAll_tv = view.findViewById(R.id.viewAll_tv);


        viewAll_tv.setOnClickListener(v -> {
            startActivity(new Intent(this.getContext(),ViewAllProductsActivity.class));
        });

        setupTopProduce();
        setupFarmerHubs();
        setupItemsYouLike();
        return view;
    }

    private void setupItemsYouLike() {
        ItemMayLikeAdapter itemMayLikeAdapter = new ItemMayLikeAdapter(getContext(),item_may_like);
        item_may_like_lv.setAdapter(itemMayLikeAdapter);
    }

    public void setupTopProduce(){
        LayoutInflater i =getLayoutInflater();
        for (int n=0; n < 10; n++) {
            View v= i.inflate(R.layout.product_item_card,null);
            ImageView product_iv = v.findViewById(R.id.product_iv);
            TextView product_name_tv=v.findViewById(R.id.product_name_tv);
            TextView productUnit_tv = v.findViewById(R.id.productUnit_tv);
            TextView productPrice_tv = v.findViewById(R.id.productPrice_tv);
            int finalN = n;
            product_iv.setOnClickListener(v1 -> {
                Toast.makeText(getContext(), "Product "+ finalN, Toast.LENGTH_SHORT).show();
            });
            product_iv.setImageResource(R.drawable.sample_product);
            product_name_tv.setText("Sweet Tomato");
            productUnit_tv.setText("per kg");
            productPrice_tv.setText("Php 87:00");
            topProduct_gl.addView(v);
        }

    }

    public void setupFarmerHubs(){
        LayoutInflater i =getLayoutInflater();
        for (int n=0; n< 50; n++) {
            View v= i.inflate(R.layout.fragment_farmer_hub_card,null);
            TextView farmers = v.findViewById(R.id.farmer);
            farmers.setText("Emerson Benatiro Llever "+n);
            int number = n;
            farmers.setOnClickListener(v1 -> {
                Toast.makeText(getContext(), "Farmer "+ number, Toast.LENGTH_SHORT).show();
            });
            farmersHub_ll.addView(v);
        }
    }


}