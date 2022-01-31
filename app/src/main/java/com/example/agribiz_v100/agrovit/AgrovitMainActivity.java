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

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.farmer.AgriHelp;
import com.example.agribiz_v100.farmer.FarmerMainActivity;
import com.example.agribiz_v100.farmer.FarmerProfile;
import com.example.agribiz_v100.farmer.Finance;
import com.example.agribiz_v100.farmer.Product;
import com.example.agribiz_v100.farmer.Shipment;
import com.google.android.material.tabs.TabLayout;

public class AgrovitMainActivity extends AppCompatActivity {
    ViewPager2 customerMain_vp;
    TabLayout customer_tab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agrovit_main);
        //view pager setup
        AgrovitMainActivity.ViewPagerAdapter adpater = new AgrovitMainActivity.ViewPagerAdapter(this);
        customerMain_vp = findViewById(R.id.customerMain_vp);
        customerMain_vp.setUserInputEnabled(false);
        customerMain_vp.setAdapter(adpater);


        //tab setup
        customer_tab = findViewById(R.id.customer_tab);

        customerMain_vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //super.onPageSelected(position);

                selectedTab(position);
                Log.d("Tag",position+"");
                //customer_tab.selectTab(customer_tab.getTabAt(position));

            }

        });


        customer_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("Tag",tab.getPosition()+"");
                customerMain_vp.setCurrentItem(tab.getPosition());
//               for(int i = 0;i<5;i++){
//                    if(i==customer_tab.getSelectedTabPosition()){
//                       customer_tab.getTabAt(i).getIcon().setColorFilter(getResources().getColor(R.color.yellow_orange), PorterDuff.Mode.SRC_IN);
//                }
//                   else
//                       customer_tab.getTabAt(i).getIcon().setColorFilter(getResources().getColor(R.color.army_green), PorterDuff.Mode.SRC_IN);
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    @SuppressLint("ResourceAsColor")
    private void selectedTab(int position) {
        customer_tab.selectTab(customer_tab.getTabAt(position));
        for (int i = 0; i < 4; i++) {
            if (i == position) {
//                customer_tab.getTabAt(i).getIcon().setTint(R.color.yellow_orange);
                customer_tab.getTabAt(i).getIcon().setColorFilter(getResources().getColor(R.color.yellow_orange), PorterDuff.Mode.SRC_IN);
            } else
                customer_tab.getTabAt(i).getIcon().setColorFilter(getResources().getColor(R.color.army_green), PorterDuff.Mode.SRC_IN);
//                customer_tab.getTabAt(i).getIcon().setTint(R.color.army_green);
        }
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