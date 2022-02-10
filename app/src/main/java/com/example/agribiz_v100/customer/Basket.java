package com.example.agribiz_v100.customer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class Basket extends Fragment {
    private static final String TAG = "Basket";
    int itemsCount = 3;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SparseArray<Object> basketItems;
    BasketListAdapter basketListAdapter;
    SparseArray<BasketProductItem> items;
    Bundle bundle;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ListView basket_item_list;
    CheckBox select_all_cb;
    LinearLayout empty_basket_ll, mid_ll;
    TextView totalAmount_tv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Creating Basket...");
        basketItems = new SparseArray<>();
        basketListAdapter = new BasketListAdapter(getContext(), basketItems);
        items = new SparseArray<>();
        if (getArguments() != null) {
            bundle = getArguments();
//            user = bundle.getParcelable("user");
        } else {
            Log.d(TAG, "No data...");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Creating Basket view...");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basket, container, false);

        empty_basket_ll = view.findViewById(R.id.empty_basket_ll);
        mid_ll = view.findViewById(R.id.mid_ll);

        basket_item_list = view.findViewById(R.id.basket_item_list);
        select_all_cb = view.findViewById(R.id.select_all_cb);
        totalAmount_tv = view.findViewById(R.id.totalAmount_tv);
        basket_item_list.setAdapter(basketListAdapter);
        select_all_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < basketItems.size(); i++) {
                    if (basketItems.valueAt(i) instanceof BasketProductItem) {
                        ((BasketProductItem) basketItems.valueAt(i)).setChecked(select_all_cb.isChecked());
                    } else
                        ((BasketHeader) basketItems.valueAt(i)).setChecked(select_all_cb.isChecked());
                }
                basketListAdapter.notifyDataSetChanged();
            }
        });

        basket_item_list.setAdapter(basketListAdapter);
        displayBasketItem();
        return view;
    }

    public void showIfEmpty() {
        if (basketItems.size() > 0) {
            empty_basket_ll.setVisibility(View.GONE);
            mid_ll.setVisibility(View.VISIBLE);
        } else {
            empty_basket_ll.setVisibility(View.VISIBLE);
            mid_ll.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Starting Basket...");
        Log.d(TAG, basketItems.size() + "");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resuming Basket...");
    }

    public void displayBasketItem() {
        db.collection("users").document(user.getUid()).collection("basket")
                .orderBy("productUserId", Query.Direction.ASCENDING)
                .orderBy("productDateAdded", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        final int[] i = {0};
                        final String[] tempId = {""};
                        for (QueryDocumentSnapshot document : value) {
                            BasketProductItem item = new BasketProductItem(document);
                            BasketHeader basketHeader = new BasketHeader();
                            //Log.d(TAG, document.getId() + " => " + document.getData().get("productUserId"));
                            db.collection("users")
                                    .document(document.getData().get("productUserId").toString())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (doc.exists()) {
                                            basketHeader.setFarmId(doc.getId());
                                            basketHeader.setFarmName((doc.getData().get("username")).toString());
                                            if (tempId[0].equals("") || !tempId[0].equals(doc.getId())) {
                                                basketItems.append(i[0]++, basketHeader);
                                                tempId[0] = doc.getId();
                                            }
                                            basketItems.append(i[0]++, item);
                                            basketListAdapter.notifyDataSetChanged();
                                            Log.d(TAG, "DocumentSnapshot data: " + basketItems.size());
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                        }
                        Log.d(TAG, "Current cites in CA: ");
                    }
                });


//        db.collection("users").document(user.getUid()).collection("basket")
//                .orderBy("productUserId", Query.Direction.ASCENDING)
//                .orderBy("productDateAdded", Query.Direction.DESCENDING)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            final int[] i = {0};
//                            final String[] tempId = {""};
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                BasketProductItem item = new BasketProductItem(document);
//                                BasketHeader basketHeader = new BasketHeader();
//                                //Log.d(TAG, document.getId() + " => " + document.getData().get("productUserId"));
//                                db.collection("users")
//                                        .document(document.getData().get("productUserId").toString())
//                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            DocumentSnapshot doc = task.getResult();
//                                            if (doc.exists()) {
//                                                basketHeader.setFarmId(doc.getId());
//                                                basketHeader.setFarmName((doc.getData().get("username")).toString());
//                                                if (tempId[0].equals("") || !tempId[0].equals(doc.getId())) {
//                                                    basketItems.append(i[0]++, basketHeader);
//                                                    tempId[0] = doc.getId();
//                                                }
//                                                basketItems.append(i[0]++, item);
//                                                basketListAdapter.notifyDataSetChanged();
//                                                Log.d(TAG, "DocumentSnapshot data: " + basketItems.size());
//                                            } else {
//                                                Log.d(TAG, "No such document");
//                                            }
//                                        } else {
//                                            Log.d(TAG, "get failed with ", task.getException());
//                                        }
//                                    }
//                                });
//
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                            Log.e(TAG, task.getException().getLocalizedMessage());
//                        }
//                    }
//                });
    }

    public class BasketListAdapter extends BaseAdapter {

        Context context;
        SparseArray<Object> productList;
        LayoutInflater layoutInflater;
        final int ITEM = 1;
        final int HEADER = 0;


        public BasketListAdapter(Context context, SparseArray<Object> productList) {
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
            if (productList.valueAt(position) instanceof BasketProductItem) {
                return ITEM;
            } else
                return HEADER;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            getTotal();
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
                    farmer_hub_name.setText(((BasketHeader) productList.valueAt(position)).getFarmName());
                    CheckBox header_checkbox = convertView.findViewById(R.id.header_checkbox);
                    header_checkbox.setChecked(((BasketHeader) productList.valueAt(position)).isChecked());
                    header_checkbox.setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    ((BasketHeader) productList.valueAt(position)).setChecked(isChecked);
                                    setCheckAll(isChecked, position);
                                    isCheckAll();
                                }
                            }
                    );
                    return convertView;
                case ITEM:
                    convertView = layoutInflater.inflate(R.layout.basket_list_item, null);
                    ImageView item_image = convertView.findViewById(R.id.item_image);
                    TextView item_name = convertView.findViewById(R.id.item_name);
                    TextView productBasketQuantity_tv = convertView.findViewById(R.id.productBasketQuantity_tv);
                    TextView item_price = convertView.findViewById(R.id.item_price);
                    TextView item_stocks = convertView.findViewById(R.id.item_stocks);

                    item_name.setText(((BasketProductItem) productList.valueAt(position)).getProductName());
                    item_price.setText("Php " + String.format("%.2f", ((BasketProductItem) productList.valueAt(position)).getProductPrice()));
                    item_stocks.setText("Stocks: " + ((BasketProductItem) productList.valueAt(position)).getProductStocks());
                    CheckBox item_checkbox = convertView.findViewById(R.id.item_checkbox);
                    item_checkbox.setChecked(((BasketProductItem) productList.valueAt(position)).isChecked());
                    String sp = ((BasketProductItem) productList.valueAt(position)).getProductImage().get(0);
//                    .split("[\\[\\]]")
                    Glide.with(context)
                            .load(sp)
                            .centerCrop()
                            .into(item_image);
                    productBasketQuantity_tv.setText(String.valueOf(((BasketProductItem) productList.valueAt(position)).getProductBasketQuantity()));
                    item_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            ((BasketProductItem) productList.valueAt(position)).setChecked(isChecked);
                            unCheckHeader(isChecked, position);
                            isCheckAll();
                            notifyDataSetChanged();
                        }
                    });
                    return convertView;
                default:
                    return null;
            }


        }

        private void getTotal() {
            double total = 0;
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i) instanceof BasketProductItem) {
                    BasketProductItem pi = (BasketProductItem) productList.get(i);
                    if (pi.isChecked())
                        total += (pi.getProductBasketQuantity() * pi.getProductPrice());
                }

            }
            totalAmount_tv.setText("Total: ₱" + String.format("%.2f", total));
        }

        private void isCheckAll() {
            boolean overAllItemCheckFlag = true;
            for (int i = 0; i < productList.size(); i++) {
                if (getItemViewType(i) == ITEM && !((BasketProductItem) productList.valueAt(i)).isChecked()) {
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
                    if (getItemViewType(x) == HEADER && ((BasketProductItem) productList.valueAt(position)).getProductUserId().equals(((BasketHeader) productList.valueAt(x)).getFarmId())) {
                        ((BasketHeader) productList.valueAt(x)).setChecked(false);
                        notifyDataSetChanged();
                        break;
                    }
                }
            } else {
                for (int x = 0; x < productList.size(); x++) {
                    if (getItemViewType(x) == ITEM && ((BasketProductItem) productList.valueAt(position)).getProductUserId().equals(((BasketProductItem) productList.valueAt(x)).getProductUserId()) && position != x) {
                        if (!((BasketProductItem) productList.valueAt(x)).isChecked()) {
                            allCheckFlag = false;
                            locator = x;
                            break;
                        }
                    }

                }
                if (allCheckFlag)
                    for (int x = 0; x < productList.size(); x++) {
                        if (getItemViewType(x) == HEADER && ((BasketHeader) productList.valueAt(x)).getFarmId().equals(((BasketProductItem) productList.valueAt(position)).getProductUserId())) {
                            ((BasketHeader) productList.valueAt(x)).setChecked(true);
                            //basket_item_list.setAdapter(new BasketListAdapter(getContext(), productList));
                            notifyDataSetChanged();
                            break;
                        }
                    }

            }
        }

        private void setCheckAll(boolean isChecked, int position) {
            for (int x = 0; x < productList.size(); x++) {
                if (getItemViewType(x) == ITEM && ((BasketProductItem) productList.valueAt(x)).getProductUserId().equals(((BasketHeader) productList.valueAt(position)).getFarmId())) {
                    ((BasketProductItem) productList.valueAt(x)).setChecked(isChecked);
                }
            }
            //basket_item_list.setAdapter(new BasketListAdapter(getContext(), productList));
            notifyDataSetChanged();
        }
    }

}