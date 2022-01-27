package com.example.agribiz_v100.farmer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.customer.Search;
import com.example.agribiz_v100.customer.Store;
import com.google.android.material.tabs.TabLayout;


public class Shipment extends Fragment {

    ViewPager2 farmer_product_vp;
    TabLayout farmer_product_tab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_shipment, container, false);

        farmer_product_vp = view.findViewById(R.id.farmer_product_vp);
        farmer_product_tab = view.findViewById(R.id.farmer_product_tab);

        Shipment.ViewPagerAdapter viewPagerAdapter = new Shipment.ViewPagerAdapter(getActivity());
        farmer_product_vp.setAdapter(viewPagerAdapter);

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


        return view;
    }
    private class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            if (position == 0) {
                return new MyProduct();
            } else if (position == 1) {
                return new Store();
            } else if (position == 2) {
                return new Store();
            } else {
                return new Search();
            }

        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}