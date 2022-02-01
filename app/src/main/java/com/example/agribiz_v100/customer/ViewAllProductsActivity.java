package com.example.agribiz_v100.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.Map;

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
    SparseArray<ProductItem> productItems;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    Bundle bundle;

    //                        if(productItems.isEmpty()){
//                            no_product_ll.setVisibility(View.VISIBLE);
//                            farmer_product_gv.setVisibility(View.GONE);
//                        }
//                        else{
//                            no_product_ll.setVisibility(View.GONE);
//                            farmer_product_gv.setVisibility(View.VISIBLE);
//                        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_products);
        productItems = new SparseArray<>();
        if (getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
            productItems = bundle.getSparseParcelableArray("productItems");
            user = bundle.getParcelable("user");
            Log.d(TAG,user.getDisplayName().toString());
        }
//        db.collection("products")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            final int[] i = {0};
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                //Log.d(TAG, document.getId() + " => " + document.getData());
//                                ProductItem item = new ProductItem(document);
//
//                                db.collection("users").document(document.getData().get("productFarmId").toString())
//                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            DocumentSnapshot doc = task.getResult();
//
//                                            if (doc.exists()) {
//                                                //Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
//                                                item.setProductFarmImage(doc.getData().get("userImage").toString());
//                                                item.setProductFarmName(doc.getData().get("username").toString());
//                                            } else {
//                                                Log.d(TAG, "No such document");
//                                            }
//                                            productItems.append(i[0]++,item);
//                                            productGridAdapter.notifyDataSetChanged();
//                                        } else {
//                                            Log.d(TAG, "get failed with ", task.getException());
//                                        }
//                                    }
//                                });
//                            }
//
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });

        productGridAdapter = new ProductGridAdapter(getApplicationContext(), productItems);
        viewAll_gv = findViewById(R.id.viewAll_gv);
        viewAll_gv.setAdapter(productGridAdapter);
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

        viewAll_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewAllProductsActivity.this, ProductView.class);
                intent.putExtra("item", productItems.get(position));
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
    }
}