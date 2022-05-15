package com.example.agribiz_v100;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.example.agribiz_v100.customer.OrdersFragment;
import com.example.agribiz_v100.farmer.MyProduct;
import com.example.agribiz_v100.farmer.Product;
import com.example.agribiz_v100.farmer.SoldOutProduct;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OrdersActivity extends AppCompatActivity {
    ViewPager2 farmer_product_vp;
    TabLayout farmer_product_tab;
    MaterialToolbar topAppBar;
    int tabs;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent1 = getIntent();
        farmer_product_tab.getTabAt(intent1.getIntExtra("tab", 0)).select();
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(parent, name, context, attrs);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        farmer_product_vp = findViewById(R.id.farmer_product_vp);
        farmer_product_tab = (TabLayout) findViewById(R.id.farmer_product_tab);


        OrdersActivity.ViewPagerAdapter viewPagerAdapter = new OrdersActivity.ViewPagerAdapter(OrdersActivity.this);
        farmer_product_vp.setAdapter(viewPagerAdapter);
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent();
            if (user.getDisplayName().charAt(user.getDisplayName().length() - 1) == 'c')
                intent.setClassName(getApplicationContext(), "com.example.agribiz_v100.customer.CustomerMainActivity");
            else if (user.getDisplayName().charAt(user.getDisplayName().length() - 1) == 'f')
                intent.setClassName(getApplicationContext(), "com.example.agribiz_v100.farmer.FarmerMainActivity");
            else if (user.getDisplayName().charAt(user.getDisplayName().length() - 1) == 'a')
                intent.setClassName(getApplicationContext(), "com.example.agribiz_v100.agrovit.AgrovitMainActivity");
            else {
                finish();
            }
            startActivity(intent);
            finish();
        });

        farmer_product_vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                farmer_product_tab.getTabAt(position).select();
            }

        });
        farmer_product_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                farmer_product_vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
            switch (position) {
                case 0:
                    return new OrdersFragment("To Prepare", "pending");
                case 1:
                    return new OrdersFragment("To Ship", "prepared");
                case 2:
                    return new OrdersFragment("To Receive", "shipped");
                case 3:
                    return new OrdersFragment("Completed", "completed");
                case 4:
                    return new OrdersFragment("Cancelled", "cancelled");
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