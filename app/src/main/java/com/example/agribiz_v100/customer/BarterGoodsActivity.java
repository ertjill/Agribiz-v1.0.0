package com.example.agribiz_v100.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.adapter.BarterGoodsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BarterGoodsActivity extends AppCompatActivity {

    TabLayout tab_layout;
    ViewPager2 barter_goods_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barter_goods);

        tab_layout = findViewById(R.id.tab_layout);
        barter_goods_pager = findViewById(R.id.barter_goods_pager);
    }
}