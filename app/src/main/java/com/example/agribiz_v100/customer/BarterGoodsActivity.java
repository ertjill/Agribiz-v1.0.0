package com.example.agribiz_v100.customer;

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
import com.google.android.material.tabs.TabLayoutMediator;

public class BarterGoodsActivity extends AppCompatActivity {

    MaterialToolbar topAppBar;
    TabLayout barterGoods_tab;
    ViewPager2 barter_goods_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barter_goods);

        references();

        BarterGoodsActivity.BarterPagerAdapter bpa = new BarterPagerAdapter(this);
        barter_goods_pager.setAdapter(bpa);
        barter_goods_pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                barterGoods_tab.getTabAt(position).select();
            }
        });
        barterGoods_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                barter_goods_pager.setCurrentItem(tab.getPosition());
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

    private void references() {
        topAppBar = findViewById(R.id.topAppBar);
        barterGoods_tab = findViewById(R.id.barterGoods_tab);
        barter_goods_pager = findViewById(R.id.barter_goods_pager);
    }

    private class BarterPagerAdapter extends FragmentStateAdapter {
        public BarterPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new BarterBrowserFragment();
                case 1:
                    return new BarterFragment("Request", "Pending");
                case 2:
                    return new BarterFragment("Swapping", "Swapping");
                case 3:
                    return new BarterFragment("Completed", "Completed");
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}