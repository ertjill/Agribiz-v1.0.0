package com.example.agribiz_v100.farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.agribiz_v100.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class BarterProductActivity extends AppCompatActivity {

    MaterialToolbar topAppBar;

    TabLayout barter_tab;
    ViewPager2 barter_product_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barter_product);

        references();

        BarterProductActivity.BarterProductFragmentAdapter barterProductsPager = new BarterProductFragmentAdapter(this);
        barter_product_pager.setAdapter(barterProductsPager);

        barter_product_pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                barter_tab.getTabAt(position).select();
            }
        });

        barter_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                barter_product_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        topAppBar.setNavigationOnClickListener(v -> finish());
    }

    public void references() {
        topAppBar = findViewById(R.id.topAppBar);
        barter_tab = findViewById(R.id.barter_tab);
        barter_product_pager = findViewById(R.id.barter_product_pager);
    }

    private class BarterProductFragmentAdapter extends FragmentStateAdapter {
        public BarterProductFragmentAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new BarterAddItem();
            } else if (position == 1){
                return new BarterRequests();
            } else if (position == 2){
                return new BarterSwap();
            }
            else {
                return new BarterCompleted();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}