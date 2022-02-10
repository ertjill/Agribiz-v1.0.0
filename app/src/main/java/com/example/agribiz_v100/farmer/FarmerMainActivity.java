package com.example.agribiz_v100.farmer;

import androidx.annotation.NonNull;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class FarmerMainActivity extends AppCompatActivity {
    static String TAG = "FarmerMainActivity";
    ViewPager2 farmerMain_vp;
    BottomNavigationView bottom_navigation;

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