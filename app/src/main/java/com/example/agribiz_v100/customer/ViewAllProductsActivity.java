package com.example.agribiz_v100.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.farmer.FarmerProductItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ViewAllProductsActivity extends AppCompatActivity {
    String TAG = "ViewAllProductsActivity";
    GridView viewAll_gv;
    MaterialToolbar topAppBar;
    String[] productName = {"Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato"};
    String[] productUnit = {"kg", "kg", "kg", "kg", "kg", "kg", "kg", "kg", "kg", "kg"};
    String[] productPrice = {"87.00", "87.00", "87.00", "87.00", "87.00", "87.00", "87.00", "87.00", "87.00", "87.00"};
    int[] image = {R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product, R.drawable.sample_product};
    ProductGridAdapter productGridAdapter;
    ArrayList<ProductItem> productItems;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_products);
        productItems = new ArrayList<>();
        db.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Log.d(TAG, "New city: " + dc.getDocument().getId());
                                productItems.add(new FarmerProductItem(dc.getDocument().getId(),
                                        dc.getDocument().getData().get("productFarmId").toString(),
                                        dc.getDocument().getData().get("productName").toString(),
                                        Double.parseDouble(dc.getDocument().getData().get("productPrice").toString()),
                                        dc.getDocument().getData().get("productUnit").toString(),
                                        dc.getDocument().getData().get("productImage").toString(),
                                        Integer.parseInt(dc.getDocument().getData().get("productStocks").toString()),
                                        dc.getDocument().getData().get("productDescription").toString(),
                                        dc.getDocument().getData().get("productCategory").toString()));
                                Log.d(TAG, "Number of Item: "+productItems.size());

                            }

                        }
//                        if(productItems.isEmpty()){
//                            no_product_ll.setVisibility(View.VISIBLE);
//                            farmer_product_gv.setVisibility(View.GONE);
//                        }
//                        else{
//                            no_product_ll.setVisibility(View.GONE);
//                            farmer_product_gv.setVisibility(View.VISIBLE);
//                        }
                        productGridAdapter.notifyDataSetChanged();
                    }
                });
        productGridAdapter = new ProductGridAdapter(getApplicationContext(), productItems);
        viewAll_gv = findViewById(R.id.viewAll_gv);
        viewAll_gv.setAdapter(productGridAdapter);
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v->{
            finish();
        });

        viewAll_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), "Product item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}