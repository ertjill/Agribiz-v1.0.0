package com.example.agribiz_v100.customer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.agribiz_v100.R;

public class BarterFragment extends Fragment {
    private String title;
    private String status;

    public BarterFragment(String title, String status) {
        this.title = title;
        this.status = status;
    }

    ListView barters_lv;
    LinearLayout no_product_ll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barter, container, false);
        barters_lv= view.findViewById(R.id.barters_lv);
        no_product_ll = view.findViewById(R.id.no_product_ll);



        return view;
    }
}