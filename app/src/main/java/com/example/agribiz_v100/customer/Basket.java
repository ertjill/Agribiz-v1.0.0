package com.example.agribiz_v100.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
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
import com.example.agribiz_v100.services.BasketManagement;
import com.example.agribiz_v100.validation.AuthValidation;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Basket extends Fragment {
    private static final String TAG = "Basket";
    int itemsCount = 3;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Object> basketItems;
    BasketListAdapter basketListAdapter;
    List<BasketProductItem> items;
    Bundle bundle;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ListView basket_item_list;
    CheckBox select_all_cb;
    LinearLayout empty_basket_ll, mid_ll;
    TextView totalAmount_tv;
    ListenerRegistration registration;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Creating Basket...");

        items = new ArrayList<>();
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
        basketItems = new ArrayList<>();
        basketListAdapter = new BasketListAdapter(getContext());
        empty_basket_ll = view.findViewById(R.id.empty_basket_ll);
        mid_ll = view.findViewById(R.id.mid_ll);

        basket_item_list = view.findViewById(R.id.basket_item_list);
        select_all_cb = view.findViewById(R.id.select_all_cb);
        totalAmount_tv = view.findViewById(R.id.totalAmount_tv);
        select_all_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < basketItems.size(); i++) {
                    if (basketItems.get(i) instanceof BasketProductItem) {
                        ((BasketProductItem) basketItems.get(i)).setChecked(select_all_cb.isChecked());
                    } else
                        ((BasketHeader) basketItems.get(i)).setChecked(select_all_cb.isChecked());
                }
                basketListAdapter.notifyDataSetChanged();
            }
        });
        basket_item_list.setAdapter(basketListAdapter);
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
    public void onPause() {
        super.onPause();
        registration.remove();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resuming Basket...");
        displayBasketItem();
    }

    public void displayBasketItem() {
        Log.d(TAG, "Listen failed.");
        registration = db.collection("basket").document(user.getUid()).collection("products")
                .orderBy("productDateAdded", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        basketItems.clear();
                        if (e != null) {
                            Log.d(TAG, "Listen failed.", e);
                            return;
                        }
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
                                            basketHeader.setFarmName((doc.getData().get("userDisplayName")).toString());
                                            if (tempId[0].equals("") || !tempId[0].equals(doc.getId())) {
                                                basketItems.add(basketHeader);
                                                tempId[0] = doc.getId();
                                            }
                                            basketItems.add(item);
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
        LayoutInflater layoutInflater;
        final int ITEM = 1;
        final int HEADER = 0;


        public BasketListAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return basketItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (basketItems.get(position) instanceof BasketProductItem) {
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
            return basketItems.get(position);
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
                    ImageView delete_ib = convertView.findViewById(R.id.delete_ib);
                    TextView farmer_hub_name = convertView.findViewById(R.id.farmer_hub_name);
                    farmer_hub_name.setText(((BasketHeader) basketItems.get(position)).getFarmName().substring(0,((BasketHeader) basketItems.get(position)).getFarmName().length()-2));
                    CheckBox header_checkbox = convertView.findViewById(R.id.header_checkbox);
                    header_checkbox.setChecked(((BasketHeader) basketItems.get(position)).isChecked());
                    delete_ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle("Basket Header:");
                            alert.setMessage("header");
                            alert.setPositiveButton("Yes", null);
                            alert.show();
                        }
                    });
                    header_checkbox.setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    ((BasketHeader) basketItems.get(position)).setChecked(isChecked);
                                    setCheckAll(isChecked, position);
                                    isCheckAll();
                                }
                            }
                    );
                    return convertView;
                case ITEM:

                    convertView = layoutInflater.inflate(R.layout.basket_list_item, null);
                    ImageView deletei_ib = convertView.findViewById(R.id.delete_ib);
                    deletei_ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle("Delete Basket Item:");
                            alert.setMessage("Are you sure to remove this item on your basket?");
                            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BasketManagement bm =new BasketManagement(getActivity());
                                    bm.deleteFromBasket(((BasketProductItem)basketItems.get(position)).getProductId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                AuthValidation.successToast(getContext(),"Item removed").show();
                                                notifyDataSetChanged();
                                            }else{
                                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                                alert.setTitle("Error");
                                                alert.setMessage(task.getException().getLocalizedMessage());
                                                alert.setPositiveButton("Ok", null);
                                            }
                                        }
                                    });
                                }
                            });
                            alert.setNegativeButton("No",null);
                            alert.show();
                        }
                    });
//                    convertView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//                            alert.setTitle("Basket Item:");
//                            alert.setMessage(((BasketProductItem) basketItems.get(position)).getProductName());
//                            alert.setPositiveButton("Ok", null);
//                            alert.show();
//                        }
//                    });
                    ImageView item_image = convertView.findViewById(R.id.item_image);
                    TextView item_name = convertView.findViewById(R.id.item_name);
                    TextView productBasketQuantity_tv = convertView.findViewById(R.id.productBasketQuantity_tv);
                    TextView item_price = convertView.findViewById(R.id.item_price);
                    TextView item_stocks = convertView.findViewById(R.id.item_stocks);

                    item_name.setText(((BasketProductItem) basketItems.get(position)).getProductName());
                    item_price.setText("Php " + String.format("%.2f", ((BasketProductItem) basketItems.get(position)).getProductPrice()));
                    item_stocks.setText("Stocks: " + ((BasketProductItem) basketItems.get(position)).getProductStocks());
                    CheckBox item_checkbox = convertView.findViewById(R.id.item_checkbox);
                    item_checkbox.setChecked(((BasketProductItem) basketItems.get(position)).isChecked());
                    String sp = ((BasketProductItem) basketItems.get(position)).getProductImage().get(0);
//                    .split("[\\[\\]]")
                    Glide.with(context)
                            .load(sp)
                            .centerCrop()
                            .into(item_image);
                    productBasketQuantity_tv.setText(String.valueOf(((BasketProductItem) basketItems.get(position)).getProductBasketQuantity()));
                    item_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            ((BasketProductItem) basketItems.get(position)).setChecked(isChecked);
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
            for (int i = 0; i < basketItems.size(); i++) {
                if (basketItems.get(i) instanceof BasketProductItem) {
                    BasketProductItem pi = (BasketProductItem) basketItems.get(i);
                    if (pi.isChecked())
                        total += (pi.getProductBasketQuantity() * pi.getProductPrice());
                }

            }
            totalAmount_tv.setText("Total: ₱" + String.format("%.2f", total));
        }

        private void isCheckAll() {
            boolean overAllItemCheckFlag = true;
            for (int i = 0; i < basketItems.size(); i++) {
                if (getItemViewType(i) == ITEM && !((BasketProductItem) basketItems.get(i)).isChecked()) {
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
                for (int x = 0; x < basketItems.size(); x++) {
                    if (getItemViewType(x) == HEADER && ((BasketProductItem) basketItems.get(position)).getProductUserId().equals(((BasketHeader) basketItems.get(x)).getFarmId())) {
                        ((BasketHeader) basketItems.get(x)).setChecked(false);
                        notifyDataSetChanged();
                        break;
                    }
                }
            } else {
                for (int x = 0; x < basketItems.size(); x++) {
                    if (getItemViewType(x) == ITEM && ((BasketProductItem) basketItems.get(position)).getProductUserId().equals(((BasketProductItem) basketItems.get(x)).getProductUserId()) && position != x) {
                        if (!((BasketProductItem) basketItems.get(x)).isChecked()) {
                            allCheckFlag = false;
                            locator = x;
                            break;
                        }
                    }

                }
                if (allCheckFlag)
                    for (int x = 0; x < basketItems.size(); x++) {
                        if (getItemViewType(x) == HEADER && ((BasketHeader) basketItems.get(x)).getFarmId().equals(((BasketProductItem) basketItems.get(position)).getProductUserId())) {
                            ((BasketHeader) basketItems.get(x)).setChecked(true);
                            //basket_item_list.setAdapter(new BasketListAdapter(getContext(), basketItems));
                            notifyDataSetChanged();
                            break;
                        }
                    }

            }
        }

        private void setCheckAll(boolean isChecked, int position) {
            for (int x = 0; x < basketItems.size(); x++) {
                if (getItemViewType(x) == ITEM && ((BasketProductItem) basketItems.get(x)).getProductUserId().equals(((BasketHeader) basketItems.get(position)).getFarmId())) {
                    ((BasketProductItem) basketItems.get(x)).setChecked(isChecked);
                }
            }
            //basket_item_list.setAdapter(new BasketListAdapter(getContext(), basketItems));
            notifyDataSetChanged();
        }
    }

}