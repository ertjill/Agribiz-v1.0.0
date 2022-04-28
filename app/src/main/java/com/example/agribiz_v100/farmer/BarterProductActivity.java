package com.example.agribiz_v100.farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.customer.BarterBrowserFragment;
import com.example.agribiz_v100.customer.BarterGoodsActivity;
import com.example.agribiz_v100.customer.BarterReceivedFragment;
import com.example.agribiz_v100.customer.BarterSentFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BarterProductActivity extends AppCompatActivity {

    FragmentStateAdapter barterProductsPager;

    // tab titles
    private final String[] titles = new String[] { "Add Barter", "Barter Requests"};

    MaterialToolbar topAppBar;
    TabLayout tab_layout;
    ViewPager2 barter_product_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barter_product);

        references();

        barterProductsPager = new BarterProductActivity.BarterProductFragmentAdapter(this);
        barter_product_pager.setAdapter(barterProductsPager);

        new TabLayoutMediator(tab_layout, barter_product_pager, (tab, position) -> tab.setText(titles[position])).attach();

        topAppBar.setNavigationOnClickListener(v -> finish());
    }

    public void references() {
        topAppBar = findViewById(R.id.topAppBar);
        tab_layout = findViewById(R.id.tab_layout);
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
                return new BarterProduct();
            } else if (position == 1) {
                return new BarterRequests();
            } else {
                return new BarterProduct();
            }
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }
}