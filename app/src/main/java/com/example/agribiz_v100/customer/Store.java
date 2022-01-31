package com.example.agribiz_v100.customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Store extends Fragment {

    HorizontalScrollView farmersHub_hv;
    LinearLayout farmersHub_ll;
    GridLayout topProduct_gl;
    ListView item_may_like_lv;
    TextView viewAll_tv;
    String[] productName = {"Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato"};
    String[] productUnit = {"kg", "kg", "kg", "kg", "kg", "kg", "kg", "kg", "kg", "kg"};
    String[] productPrice = {"87.00", "87.00", "87.00", "87.00", "87.00", "87.00", "87.00", "87.00", "87.00", "87.00"};
    int[] image = {R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product};

    String[] item_may_like = {"Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato"};

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
            startActivity(new Intent(this.getContext(), ViewAllProductsActivity.class));
        });

        setupTopProduce();
        setupFarmerHubs();
        setupItemsYouLike();
        return view;
    }

    private void setupItemsYouLike() {
        ItemMayLikeAdapter itemMayLikeAdapter = new ItemMayLikeAdapter(getContext(), item_may_like);
        item_may_like_lv.setAdapter(itemMayLikeAdapter);
    }

    public void setupTopProduce() {
        LayoutInflater i = getLayoutInflater();
        for (int n = 0; n < 10; n++) {
            View v = i.inflate(R.layout.product_item_card, null);
            ImageView product_iv = v.findViewById(R.id.product_iv);
            TextView product_name_tv = v.findViewById(R.id.product_name_tv);
            TextView productUnit_tv = v.findViewById(R.id.productUnit_tv);
            TextView productPrice_tv = v.findViewById(R.id.productPrice_tv);
            int finalN = n;
            product_iv.setOnClickListener(v1 -> {
                Toast.makeText(getContext(), "Product " + finalN, Toast.LENGTH_SHORT).show();
            });
            product_iv.setImageResource(R.drawable.sample_product);
            product_name_tv.setText("Sweet Tomato");
            productUnit_tv.setText("per kg");
            productPrice_tv.setText("Php 87:00");
            topProduct_gl.addView(v);
        }

    }

    public void setupFarmerHubs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereNotEqualTo("userType", "customer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LayoutInflater i = getLayoutInflater();
                                    View v = i.inflate(R.layout.fragment_farmer_hub_card, null);
                                    TextView farmers = v.findViewById(R.id.farmer);
                                    ImageView farmerProfile = v.findViewById(R.id.farmerProfile);
                                    Glide.with(getContext())
                                            .load(document.getData().get("userImage"))
                                            .into(farmerProfile);
                                    farmers.setText(document.getData().get("username").toString());
                                    farmers.setOnClickListener(v1 -> {
                                        Toast.makeText(getContext(), document.getData().get("userName").toString(), Toast.LENGTH_SHORT).show();
                                    });
                                    farmersHub_ll.addView(v);
                                    Log.d("FirebaseHelper",document.getData().get("username").toString());
                            }

                        } else {
                            Log.d("FirebaseHelper", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

}