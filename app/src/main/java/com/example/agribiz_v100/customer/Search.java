package com.example.agribiz_v100.customer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.agribiz_v100.R;
public class Search extends Fragment {

    EditText searchProduct_et;
    LinearLayout typing_ll;
    GridView viewAll_gv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchProduct_et=view.findViewById(R.id.searchProduct_et);
        typing_ll = view.findViewById(R.id.typing_ll);
        viewAll_gv=view.findViewById(R.id.viewAll_gv);
        searchProduct_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    typing_ll.setVisibility(View.VISIBLE);
                    viewAll_gv.setVisibility(View.GONE);
                }
                else {
                    typing_ll.setVisibility(View.GONE);
                    viewAll_gv.setVisibility(View.VISIBLE);
                }
            }
        });
        searchProduct_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == 66){
                    searchProduct_et.clearFocus();
                }
                return false;
            }
        });
        return view;
    }
}