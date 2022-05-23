package com.example.agribiz_v100.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.agribiz_v100.ChatActivity;
import com.example.agribiz_v100.Firebase;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.NotificationBroadcastReceiver;
import com.example.agribiz_v100.OnBoard;
import com.example.agribiz_v100.OnBoardSlide;
import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.ChatModel;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.services.AuthManagement;
import com.example.agribiz_v100.services.ChatManagement;
import com.example.agribiz_v100.services.NotificationManagement;
import com.example.agribiz_v100.services.ProductManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomerMainActivity extends AppCompatActivity implements Serializable, Firebase.GetProductCallback, FirebaseHelper.FirebaseHelperCallback {
    private static final String TAG = "CustomerMainActivity";
    ViewPager2 customerMain_vp;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SparseArray<ProductItem> topProducts;
    SparseArray<Object> basketProductItems;
    ViewPagerAdapter adpater;
    BottomNavigationView bottom_navigation;
    Bundle bundle = new Bundle();
    FirebaseHelper firebaseHelper;
    int notifications = 0;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart...");

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUnseenMessages();
        AuthManagement.setUserStatus(user.getUid(), "active");
        Log.d(TAG, "onResume..." + basketProductItems.size());
    }

    public void getNotifiedForNewProduct() {
        ProductManagement.getProductsQuery().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {

                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New Chat: " + dc.getDocument().getData());
                                ProductModel productModel = dc.getDocument().toObject(ProductModel.class);
                                Intent intent = new Intent(CustomerMainActivity.this, ProductView.class);
                                intent.putExtra("item",(Parcelable) productModel);
                                NotificationManagement.newProductNotification(CustomerMainActivity.this, "New Product Added", dc.getDocument().getString("productName"),intent);
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified Chat: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed Chat: " + dc.getDocument().getData());
                                break;
                        }

                    }
                }
            }
        });
    }

    public SparseArray<ProductItem> getProduct(SparseArray<ProductItem> items) {
        Log.d(TAG, items.size() + "");
        return items;
    }

    private NavigationBarView.OnItemSelectedListener navigationListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.d(TAG, item.getItemId() + "");
            int i = 0;
            switch (item.getItemId()) {
                case R.id.Store:
                    i = 0;
                    break;
                case R.id.Search:
                    i = 1;
                    break;
                case R.id.Donate:
                    i = 2;
                    break;
                case R.id.Basket:
                    i = 3;
                    break;
                case R.id.Profile:
                    i = 4;
                    break;
            }
            customerMain_vp.setCurrentItem(i);
            return true;
        }
    };

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, "onPostCreate...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AuthManagement.setUserStatus(user.getUid(), "inactive");
        Log.d(TAG, "onPause...");
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        getNotifiedForNewProduct();
        NotificationManagement.createNotificationChannel(this);
        Log.d(TAG, "onCreate...");
        firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.getTopProducts();
        topProducts = firebaseHelper.getTopProductsItems();
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnItemSelectedListener(navigationListener);
        basketProductItems = new SparseArray<>();
//        topProducts = getIntent().getExtras().getSparseParcelableArray("topProducts");
//        user = getIntent().getParcelableExtra("user");
        customerMain_vp = findViewById(R.id.customerMain_vp);
        customerMain_vp.setUserInputEnabled(false);
        Log.d(TAG, "0");

        //view pager setup
        customerMain_vp.setOffscreenPageLimit(5);
        adpater = new ViewPagerAdapter(this);
        customerMain_vp.setAdapter(adpater);

    }


    @Override
    public void isProductAddedToBasket(boolean added) {

    }

    @Override
    public void getProductFromBasket(SparseArray<Object> productFromBasket) {

    }

    @Override
    public void getProducts(SparseArray<ProductItem> products, SparseArray<ProductItem> topProducst) {
        this.topProducts = topProducst;
        Log.d(TAG, topProducst.size() + " products...");

    }

    @Override
    public void getToProducst(SparseArray<ProductItem> topProducts) {

    }

    public void getUnseenMessages() {
        ChatManagement.getUnseenMessage(user.getUid())
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                CollectionReference chats = ds.getReference().collection("chats");
                                chats.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (value != null) {
                                            for (DocumentChange dc : value.getDocumentChanges()) {
                                                if (!dc.getDocument().getString("chatSenderUserId").equalsIgnoreCase(user.getUid()) && !dc.getDocument().getBoolean("chatSeen"))
                                                    switch (dc.getType()) {
                                                        case ADDED:
                                                            Log.d(TAG, "New Chat: " + dc.getDocument().getData());
                                                            ChatModel chat = dc.getDocument().toObject(ChatModel.class);
                                                            NotificationManagement.getNewMessageNotification(CustomerMainActivity.this, chat, notifications++);
                                                            break;
                                                        case MODIFIED:
                                                            Log.d(TAG, "Modified Chat: " + dc.getDocument().getData());
                                                            break;
                                                        case REMOVED:
                                                            Log.d(TAG, "Removed Chat: " + dc.getDocument().getData());
                                                            break;
                                                    }
                                            }

                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            bundle.putSparseParcelableArray("topProducts", topProducts);
            bundle.putParcelable("user", user);

            switch (position) {
                case 0:
                    Store store = new Store();
                    store.setArguments(bundle);
                    return store;
                case 1:
                    return new Search();
                case 2:
                    return new Donate();
                case 3:
                    Basket basket = new Basket();
                    basket.setArguments(bundle);
                    return basket;
                case 4:
                    return new Profile();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
}