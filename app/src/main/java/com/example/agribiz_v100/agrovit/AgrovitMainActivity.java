package com.example.agribiz_v100.agrovit;

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
import com.example.agribiz_v100.farmer.AgriHelp;
import com.example.agribiz_v100.farmer.FarmerMainActivity;
import com.example.agribiz_v100.farmer.FarmerProfile;
import com.example.agribiz_v100.farmer.Finance;
import com.example.agribiz_v100.farmer.Product;
import com.example.agribiz_v100.farmer.Shipment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class AgrovitMainActivity extends AppCompatActivity {
    static String TAG="AgrovitMainActivity";
    ViewPager2 agrovitMain_vp;
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
                case R.id.Finance:
                    i = 2;
                    break;
                case R.id.Profile:
                    i = 3;
                    break;
            }
            agrovitMain_vp.setCurrentItem(i);
            return true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agrovit_main);
        //view pager setup
        AgrovitMainActivity.ViewPagerAdapter adpater = new AgrovitMainActivity.ViewPagerAdapter(this);
        agrovitMain_vp = findViewById(R.id.agrovitMain_vp);
        agrovitMain_vp.setUserInputEnabled(false);
        agrovitMain_vp.setAdapter(adpater);
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

            if(position==0){
                return new AgrovitProduct();
            }
            else if(position==1){
                return new Shipment();
            }
            else if(position==2){
                return new Finance();
            }
            else {
                return new FarmerProfile();
            }

        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}