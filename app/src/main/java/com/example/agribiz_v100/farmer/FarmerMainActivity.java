package com.example.agribiz_v100.farmer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.customer.Basket;
import com.example.agribiz_v100.customer.CustomerMainActivity;
import com.example.agribiz_v100.customer.Donate;
import com.example.agribiz_v100.customer.Profile;
import com.example.agribiz_v100.customer.Search;
import com.example.agribiz_v100.customer.Store;
import com.example.agribiz_v100.entities.ChatModel;
import com.example.agribiz_v100.services.AuthManagement;
import com.example.agribiz_v100.services.ChatManagement;
import com.example.agribiz_v100.services.NotificationManagement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class FarmerMainActivity extends AppCompatActivity {
    static String TAG = "FarmerMainActivity";
    ViewPager2 farmerMain_vp;
    BottomNavigationView bottom_navigation;
    int notifications = 0;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private NavigationBarView.OnItemSelectedListener navigationListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.d(TAG, item.getItemId() + "");
            int i = 0;
            switch (item.getItemId()) {
                case R.id.Product:
                    i = 0;
                    break;
                case R.id.Shipment:
                    i = 1;
                    break;
                case R.id.AgriHelp:
                    i = 2;
                    break;
                case R.id.Finance:
                    i = 3;
                    break;
                case R.id.Profile:
                    i = 4;
                    break;
            }
            farmerMain_vp.setCurrentItem(i);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_main);
        NotificationManagement.createNotificationChannel(this);
        //view pager setup
        FarmerMainActivity.ViewPagerAdapter adpater = new FarmerMainActivity.ViewPagerAdapter(this);
        farmerMain_vp = findViewById(R.id.farmerMain_vp);
        farmerMain_vp.setUserInputEnabled(false);
        farmerMain_vp.setAdapter(adpater);
        farmerMain_vp.setCurrentItem(0);
        farmerMain_vp.setOffscreenPageLimit(5);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnItemSelectedListener(navigationListener);

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
                                        if (value !=null) {
                                            for (DocumentChange dc : value.getDocumentChanges()) {
                                                if (!dc.getDocument().getString("chatSenderUserId").equalsIgnoreCase(user.getUid()) && !dc.getDocument().getBoolean("chatSeen"))
                                                    switch (dc.getType()) {
                                                        case ADDED:
                                                            Log.d(TAG, "New Chat: " + dc.getDocument().getData());
                                                            ChatModel chat = dc.getDocument().toObject(ChatModel.class);
                                                            NotificationManagement.getNewMessageNotification(FarmerMainActivity.this, chat, notifications++);
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

    @Override
    protected void onResume() {
        super.onResume();
        getUnseenMessages();
        AuthManagement.setUserStatus(user.getUid(),"active");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AuthManagement.setUserStatus(user.getUid(),"inactive");
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new Product();
                case 1:
                    return new Shipment();
                case 2:
                    return new AgriHelp();
                case 3:
                    return new Finance();
                case 4:
                    return new FarmerProfile();
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