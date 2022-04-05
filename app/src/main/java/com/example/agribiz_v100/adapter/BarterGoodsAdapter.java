package com.example.agribiz_v100.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.agribiz_v100.customer.BarterReceivedFragment;
import com.example.agribiz_v100.customer.BarterSentFragment;

public class BarterGoodsAdapter extends FragmentStateAdapter {


    public BarterGoodsAdapter(@NonNull FragmentManager fragmentActivity, @NonNull Lifecycle lifecycle) {
        super(fragmentActivity, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0 ) {
            return new BarterSentFragment();
        }
        return new BarterReceivedFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
