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

    FragmentStateAdapter barterPager;

    // tab titles
    private final String[] titles = new String[]{ "Browse Items", "Sent Barter", "Received Barter" };

    MaterialToolbar topAppBar;
    TabLayout tab_layout;
    ViewPager2 barter_goods_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barter_goods);

        references();

        barterPager = new BarterPagerFragmentAdapter(this);
        barter_goods_pager.setAdapter(barterPager);

        new TabLayoutMediator(tab_layout, barter_goods_pager, (tab, position) -> tab.setText(titles[position])).attach();

        topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void references() {
        topAppBar = findViewById(R.id.topAppBar);
        tab_layout = findViewById(R.id.tab_layout);
        barter_goods_pager = findViewById(R.id.barter_goods_pager);
    }

    private class BarterPagerFragmentAdapter extends FragmentStateAdapter {
        public BarterPagerFragmentAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new BarterBrowserFragment();
            } else if (position == 1) {
                return new BarterSentFragment();
            } else {
                return new BarterReceivedFragment();
            }
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }
}