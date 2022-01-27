package com.example.agribiz_v100.customer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.agribiz_v100.R;


public class Donate extends Fragment {

    TextView money_given_tv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donate, container, false);
        money_given_tv=view.findViewById(R.id.money_given_tv);
        return view ;
    }
}