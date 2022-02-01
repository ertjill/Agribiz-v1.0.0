package com.example.agribiz_v100.customer;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agribiz_v100.R;

import java.util.ArrayList;


public class Basket extends Fragment {

    private static final String TAG = "Basket";
    ListView basket_item_list;
    CheckBox select_all_cb;
    ArrayList<Object> list;
    BasketListAdapter basketListAdapter;
    SparseArray<BasketProductItem> items;
    Bundle bundle;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            list = new ArrayList<>();
        if (getArguments() != null) {
            bundle = getArguments();
            items = bundle.getParcelable("basketProductItems");

        }
        for(int i=0;i<items.size();i++){
                    Log.d(TAG,i+"");
                }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },2000);



        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basket, container, false);
        select_all_cb = view.findViewById(R.id.select_all_cb);
        basket_item_list = view.findViewById(R.id.basket_item_list);
        
        
        
//        list.add(new BasketHeader("1102", "Emerson's Farm"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1102"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1102"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1102"));
//
//        list.add(new BasketHeader("1103", "Jack Farm"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1103"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1103"));
//
//        list.add(new BasketHeader("1105", "Skye's Farm"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1105"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1105"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1105"));
//
//        list.add(new BasketHeader("1106", "Venice Farm"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1106"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1106"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1106"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1106"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1106"));
//        list.add(new BasketProduct("Sweet Tomato", 87.00, 1, "1106"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (list.size() > 0)
                    basketListAdapter.notifyDataSetChanged();
                else
                    Toast.makeText(getContext(), "No Internet Access!", Toast.LENGTH_SHORT).show();
                //Log.d(TAG, "wALAY SULOD GIHAPON");
                //Log.d(TAG,items.size()+"");
            }
        }, 5000);
        basketListAdapter = new BasketListAdapter(getContext(), list);
        basket_item_list.setAdapter(basketListAdapter);

        select_all_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof BasketProduct) {
                        ((BasketProduct) list.get(i)).setChecked(select_all_cb.isChecked());
                    } else
                        ((BasketHeader) list.get(i)).setChecked(select_all_cb.isChecked());
                }
                basketListAdapter.notifyDataSetChanged();
                //basket_item_list.setAdapter(basketListAdapter);
            }
        });


        return view;
    }

    public class BasketListAdapter extends BaseAdapter {

        Context context;
        ArrayList<Object> productList;
        LayoutInflater layoutInflater;
        final int ITEM = 1;
        final int HEADER = 0;


        public BasketListAdapter(Context context, ArrayList<Object> productList) {
            this.context = context;
            this.productList = productList;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return productList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (productList.get(position) instanceof BasketProduct) {
                return ITEM;
            } else
                return HEADER;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case HEADER:
                    convertView = layoutInflater.inflate(R.layout.basket_list_item_header, null);
                    TextView farmer_hub_name = convertView.findViewById(R.id.farmer_hub_name);
                    farmer_hub_name.setText(((BasketHeader) productList.get(position)).getFarmName());
                    CheckBox header_checkbox = convertView.findViewById(R.id.header_checkbox);
                    header_checkbox.setChecked(((BasketHeader) productList.get(position)).isChecked());
                    header_checkbox.setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    ((BasketHeader) productList.get(position)).setChecked(isChecked);
                                    setCheckAll(isChecked, position);
                                    isCheckAll();
                                }
                            }
                    );
                    return convertView;
                case ITEM:
                    convertView = layoutInflater.inflate(R.layout.basket_list_item, null);
                    CheckBox item_checkbox = convertView.findViewById(R.id.item_checkbox);
                    item_checkbox.setChecked(((BasketProduct) productList.get(position)).isChecked());
                    item_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            //                                    if(!isChecked){
//                                        for(int x=0;x<productList.size();x++){
//                                            if(getItemViewType(x)==HEADER && ((BasketProduct)productList.get(position)).getFarmerId().equals(((BasketHeader)productList.get(x)).getFarmId())){
//                                                ((BasketHeader)productList.get(x)).setChecked(false);
//                                                basket_item_list.setAdapter(new BasketListAdapter(getContext(), productList));
//                                                Log.d("check","HAHA"+ ((BasketHeader)productList.get(x)).isChecked());
//                                            }
//                                        }
//                                    }
                            ((BasketProduct) productList.get(position)).setChecked(isChecked);
                            unCheckHeader(isChecked, position);
                            isCheckAll();
                        }
                    });
                    return convertView;
                default:
                    return null;
            }


        }

        private void isCheckAll() {
            boolean overAllItemCheckFlag = true;
            for (int i = 0; i < productList.size(); i++) {
                if (getItemViewType(i) == ITEM && !((BasketProduct) productList.get(i)).isChecked()) {
                    overAllItemCheckFlag = false;
                    break;
                }
            }
            select_all_cb.setChecked(overAllItemCheckFlag);
        }

        private void unCheckHeader(boolean isChecked, int position) {
            boolean allCheckFlag = true;

            int locator = 0;
            if (!isChecked) {
                for (int x = 0; x < productList.size(); x++) {
                    if (getItemViewType(x) == HEADER && ((BasketProduct) productList.get(position)).getFarmerId().equals(((BasketHeader) productList.get(x)).getFarmId())) {
//                    allCheckFlag = false;
//                    locator = x;
                        ((BasketHeader) productList.get(x)).setChecked(false);
                        //basket_item_list.setAdapter(new BasketListAdapter(getContext(), productList));
                        notifyDataSetChanged();
                        break;
                    }
                }
            } else {
                for (int x = 0; x < productList.size(); x++) {
                    if (getItemViewType(x) == ITEM && ((BasketProduct) productList.get(position)).getFarmerId().equals(((BasketProduct) productList.get(x)).getFarmerId()) && position != x) {
                        if (!((BasketProduct) productList.get(x)).isChecked()) {
                            allCheckFlag = false;
                            locator = x;
                            break;
                        }
                    }

                }
                if (allCheckFlag)
                    for (int x = 0; x < productList.size(); x++) {
                        if (getItemViewType(x) == HEADER && ((BasketHeader) productList.get(x)).getFarmId().equals(((BasketProduct) productList.get(position)).getFarmerId())) {
                            ((BasketHeader) productList.get(x)).setChecked(true);
                            //basket_item_list.setAdapter(new BasketListAdapter(getContext(), productList));
                            notifyDataSetChanged();
                            break;
                        }
                    }

            }

        }

        private void setCheckAll(boolean isChecked, int position) {
            for (int x = 0; x < productList.size(); x++) {
                if (getItemViewType(x) == ITEM && ((BasketProduct) productList.get(x)).getFarmerId().equals(((BasketHeader) productList.get(position)).getFarmId())) {
                    ((BasketProduct) productList.get(x)).setChecked(isChecked);
                    Log.d("check", ((BasketProduct) productList.get(x)).getName() + " : " + ((BasketProduct) productList.get(x)).isChecked() + " : " + x);
                }
            }
            //basket_item_list.setAdapter(new BasketListAdapter(getContext(), productList));
            notifyDataSetChanged();
            Log.d("check", "" + isChecked);
        }
    }

}