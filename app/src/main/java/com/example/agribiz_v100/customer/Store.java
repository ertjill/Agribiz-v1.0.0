package com.example.agribiz_v100.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Parcelable;
import android.util.SparseArray;

import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.BarteredItemViewActivity;
import com.example.agribiz_v100.ChatActivity;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.services.ProductManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Store extends Fragment {

    private static final String TAG = "Store";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HorizontalScrollView farmersHub_hv;
    LinearLayout farmersHub_ll;
    LinearLayout no_product_ll,top_products_ll;
    ListView item_may_like_lv;
    TextView viewAll_tv, not_available1;
    GridView topProduce_gv;
    String[] item_may_like = {"Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato"};
    SparseArray<ProductModel> productItems, topProducts;
    ProductGridAdapter productGridAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Bundle bundle;
    List<ProductModel> itemLike;
    ListenerRegistration listenerRegistration;
    ItemMayLikeAdapter itemMayLikeAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topProducts = new SparseArray<>();
        itemLike = new ArrayList<>();
        productGridAdapter = new ProductGridAdapter(getContext(), topProducts);
        Log.d(TAG, "Creating Store...");
        if (getArguments() != null) {
            bundle = getArguments();
        } else {
            Log.d(TAG, "No data!");
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        listenerRegistration.remove();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Store is Starting...");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Store is resuming...");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_store, container, false);
            no_product_ll = view.findViewById(R.id.no_product_ll);
            top_products_ll=view.findViewById(R.id.top_products_ll);
            not_available1 = view.findViewById(R.id.not_available1);
            SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        itemMayLikeAdapter = new ItemMayLikeAdapter(getContext(), itemLike);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    displayTopProducts();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            farmersHub_hv = view.findViewById(R.id.farmersHub_hv);
            farmersHub_ll = view.findViewById(R.id.farmersHub_ll);
            topProduce_gv = view.findViewById(R.id.topProduce_gv);
            item_may_like_lv = view.findViewById(R.id.item_may_like_lv);
            viewAll_tv = view.findViewById(R.id.viewAll_tv);
            productGridAdapter = new ProductGridAdapter(getContext(), topProducts);
            topProduce_gv.setAdapter(productGridAdapter);
            topProduce_gv.setEmptyView(no_product_ll);
            topProduce_gv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return event.getAction() == MotionEvent.ACTION_MOVE;
                }
            });

            viewAll_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d(TAG, productItems.size() + "");
//                    Intent i = new Intent(getContext(), ViewAllProductsActivity.class);
//                    i.putExtras(bundle);
//                    startActivity(i);
                    Intent intent=new Intent();
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.setClassName(getContext(),"com.example.agribiz_v100.customer.ViewAllProductsActivity");
                    startActivity(intent);
                }
            });


            topProduce_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getContext(), ProductView.class);
                    intent.putExtra("item", (Parcelable) topProducts.get(position));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            setupFarmerHubs();
            displayTopProducts();
            setupItemsYouLike();
            return view;

    }

    private void setupItemsYouLike() {
        item_may_like_lv.setAdapter(itemMayLikeAdapter);
        item_may_like_lv.setEmptyView(not_available1);
        db.collection("orders").whereEqualTo("customerId",user.getUid())
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                db.collection("products").whereEqualTo("productCategory",document.getData().get("productCategory"))
                                        .limit(5)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        itemLike.add(document.toObject(ProductModel.class));
                                                    }
                                                    itemMayLikeAdapter.notifyDataSetChanged();
                                                }
                                                else{

                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    public void setupFarmerHubs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        listenerRegistration = db.collection("users")
                .whereNotEqualTo("userType", "customer")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        else{
                            Log.w(TAG, "Listen failed.");
                        }
                        farmersHub_ll.removeAllViews();
                        for (QueryDocumentSnapshot document : value) {
                            LayoutInflater i = getLayoutInflater();
                            View v = i.inflate(R.layout.fragment_farmer_hub_card, null);
                            CardView is_active_cv= v.findViewById(R.id.is_active_cv);
                            is_active_cv.setVisibility(document.getData().get("userStatus")!=null?document.getData().get("userStatus").toString().equals("active")?View.VISIBLE:View.GONE:View.GONE);
                            TextView farmers = v.findViewById(R.id.farmer);
                            ImageView farmerProfile = v.findViewById(R.id.farmerProfile);
                            Glide.with(getContext())
                                    .load(document.getData().get("userImage"))
                                    .into(farmerProfile);
                            farmers.setText(document.getData().get("userDisplayName").toString().substring(0,document.getData().get("userDisplayName").toString().length()-2));
                            farmers.setOnClickListener(v1 -> {
                                Toast.makeText(getContext(), document.getData().get("userDisplayName").toString(), Toast.LENGTH_SHORT).show();
                            });
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("ids",document.getString("userID"));
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("userId", document.getString("userID"));
                                    startActivity(intent);
                                }
                            });
                            farmersHub_ll.addView(v);
                            Log.d("FirebaseHelper", document.getData().get("userDisplayName").toString());
                        }
                    }
                });
//        db.collection("users")
//                .whereNotEqualTo("userType", "customer")
//                .whereEqualTo("userStatus","active")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                LayoutInflater i = getLayoutInflater();
//                                View v = i.inflate(R.layout.fragment_farmer_hub_card, null);
//                                TextView farmers = v.findViewById(R.id.farmer);
//                                ImageView farmerProfile = v.findViewById(R.id.farmerProfile);
//                                Glide.with(getContext())
//                                        .load(document.getData().get("userImage"))
//                                        .into(farmerProfile);
//                                farmers.setText(document.getData().get("userDisplayName").toString().substring(0,document.getData().get("userDisplayName").toString().length()-2));
//                                farmers.setOnClickListener(v1 -> {
//                                    Toast.makeText(getContext(), document.getData().get("userDisplayName").toString(), Toast.LENGTH_SHORT).show();
//                                });
//                                farmersHub_ll.addView(v);
//                                Log.d("FirebaseHelper", document.getData().get("userDisplayName").toString());
//                            }
//
//                        } else {
//                            Log.d("FirebaseHelper", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });

    }
    public void displayTopProducts(){
        ProductManagement.getTopSellingProduct()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final int[] i = {0};
                            if(task.getResult().size()<3){
                                ViewGroup.LayoutParams layoutParams = topProduce_gv.getLayoutParams();
                                int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,285,getResources().getDisplayMetrics());
                                layoutParams.height = newWidth;
                                topProduce_gv.getLayoutParams().height=newWidth;
                            }
                            else if(task.getResult().size()<5){
                                ViewGroup.LayoutParams layoutParams = topProduce_gv.getLayoutParams();
                                int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,570,getResources().getDisplayMetrics());
                                layoutParams.height = newWidth;
                                topProduce_gv.getLayoutParams().height=newWidth;
                            }else
                            {
                                ViewGroup.LayoutParams layoutParams = topProduce_gv.getLayoutParams();
                                int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,855,getResources().getDisplayMetrics());
                                layoutParams.height = newWidth;
                                topProduce_gv.setLayoutParams(layoutParams);
                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int size = task.getResult().size();
                                Log.d(TAG, "TASK SIZE: " + size);
                                ProductModel item = document.toObject(ProductModel.class);
                                db.collection("users").document(document.getData().get("productUserId").toString())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();

                                            topProducts.append(i[0]++, item);
                                            productGridAdapter.notifyDataSetChanged();
                                            Log.d(TAG, topProducts.size() + " : topProductsItems size");

                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                            productGridAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            productGridAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

}