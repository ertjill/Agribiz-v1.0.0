package com.example.agribiz_v100.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.example.agribiz_v100.Firebase;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.OnBoard;
import com.example.agribiz_v100.OnBoardSlide;
import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomerMainActivity extends AppCompatActivity implements Firebase.GetProductCallback {

    private static final String TAG = "CustomerMainActivity";
    ViewPager2 customerMain_vp;
    TabLayout customer_tab;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SparseArray<ProductItem> productItems, topProducts;
    SparseArray<BasketProductItem> basketProductItems;
    ViewPagerAdapter adpater;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onStart() {
        super.onStart();
    }

    public SparseArray<ProductItem> getProduct(SparseArray<ProductItem> items) {
        Log.d(TAG, items.size() + "");
        return items;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        basketProductItems = new SparseArray<>();
        user = getIntent().getParcelableExtra("user");
        Log.d(TAG, user.getDisplayName().toString());
        synchronized (this) {
            productItems = new SparseArray<>();
            topProducts = new SparseArray<>();
            customerMain_vp = findViewById(R.id.customerMain_vp);
            Log.d(TAG, "0");
        }
        synchronized (this) {
            db.collection("users").document(user.getUid()).collection("basket")
                    .orderBy("productFarmId")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                final int[] i = {0};
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    BasketProductItem item = new BasketProductItem(document);
                                    Log.d(TAG, document.getId() + " => " + document.getData().get("productFarmId"));
                                    db.collection("users")
                                            .document(document.getData().get("productFarmId").toString())
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();
                                                if (doc.exists()) {
                                                    Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
                                                    basketProductItems.append(++i[0],item);
                                                } else {
                                                    Log.d(TAG, "No such document");
                                                }
                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            db.collection("products")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                final int[] i = {0};
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    ProductItem item = new ProductItem(document);

                                    db.collection("users").document(document.getData().get("productFarmId").toString())
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();
                                                if (doc.exists()) {
                                                    //Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
                                                    item.setProductFarmImage(doc.getData().get("userImage").toString());
                                                    item.setProductFarmName(doc.getData().get("username").toString());
                                                } else {
                                                    Log.d(TAG, "No such document");
                                                }
                                                productItems.append((i[0]), item);
                                                if (i[0] < 9)
                                                    topProducts.append(i[0], item);
                                                i[0]++;
                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                }

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            Log.d(TAG, "1");
        }
        synchronized (this) {
            //view pager setup
            adpater = new ViewPagerAdapter(this);
            customerMain_vp.setAdapter(adpater);


            //tab setup
            customer_tab = findViewById(R.id.customer_tab);

            customerMain_vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    //super.onPageSelected(position);

                    selectedTab(position);
                    Log.d("Tag", position + "");
                    //customer_tab.selectTab(customer_tab.getTabAt(position));

                }

            });


            customer_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Log.d("Tag", tab.getPosition() + "");
                    customerMain_vp.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            Log.d(TAG, "2");
        }

    }

    @SuppressLint("ResourceAsColor")
    private void selectedTab(int position) {
        customer_tab.selectTab(customer_tab.getTabAt(position));
        for (int i = 0; i < 5; i++) {
            if (i == position) {
                customer_tab.getTabAt(i).getIcon().setColorFilter(getResources().getColor(R.color.yellow_orange), PorterDuff.Mode.SRC_IN);
            } else
                customer_tab.getTabAt(i).getIcon().setColorFilter(getResources().getColor(R.color.army_green), PorterDuff.Mode.SRC_IN);
        }
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Bundle bundle = new Bundle();
            bundle.putSparseParcelableArray("productItems", productItems);
            bundle.putSparseParcelableArray("topProducts", topProducts);
            bundle.putSparseParcelableArray("basketProductItems", basketProductItems);
            bundle.putParcelable("user", user);
            if (position == 0) {
                Store store = new Store();
                store.setArguments(bundle);
                return store;
            } else if (position == 1) {
                return new Search();
            } else if (position == 2) {
                return new Donate();
            } else if (position == 3) {
                Basket basket = new Basket();
                basket.setArguments(bundle);
                return basket;
            } else {
                return new Profile();
            }

        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
}