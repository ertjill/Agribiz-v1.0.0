package com.example.agribiz_v100.customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.BasketProductModel;
import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.entities.OrderProductModel;
import com.example.agribiz_v100.services.BasketManagement;
import com.example.agribiz_v100.services.OrderManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.example.agribiz_v100.validation.ProductValidation;
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
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Basket extends Fragment {
    private static final String TAG = "Basket";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Object> basketItems;
    BasketListAdapter basketListAdapter;
    List<BasketProductModel> items;
    Bundle bundle;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ListView basket_item_list;
    CheckBox select_all_cb;
    LinearLayout empty_basket_ll, mid_ll;
    TextView totalAmount_tv;
    ListenerRegistration registration;
    Button checkout_btn;
    Map<String, Object> location;
    List<String> listAdd;
    List<Map<String, Object>> listLocation;
    AutoCompleteTextView address_act;

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
        checkout_btn = view.findViewById(R.id.checkout_btn);
        basket_item_list = view.findViewById(R.id.basket_item_list);
        select_all_cb = view.findViewById(R.id.select_all_cb);
        totalAmount_tv = view.findViewById(R.id.totalAmount_tv);
        address_act = view.findViewById(R.id.address_act);
        listAdd = new ArrayList<>();
        listLocation = new ArrayList<>();

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        List<Object> loc = (List<Object>) document.getData().get("userLocation");
                        for (Object l : loc) {
                            Log.d(TAG, l.toString());
                            Map<String, Object> adds = (Map<String, Object>) l;
                            listAdd.add("Name: "+adds.get("userFullName").toString()+
                                    "\nPhone Number: "+adds.get("userPhoneNumber").toString()+
                                    "\nAddress: "+adds.get("userBarangay").toString()+", "+adds.get("userMunicipality").toString()+", "+adds.get("userProvince").toString()+
                                    "\nSpecific Address: "+adds.get("userSpecificAddress").toString());

                            listLocation.add(adds);
                        }
                        ArrayAdapter<String> addAdapter = new ArrayAdapter<String>(getContext(), R.layout.address_dropdown_item_layout, listAdd);
                        address_act.setAdapter(addAdapter);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        address_act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                location = listLocation.get(position);
            }
        });
        checkout_btn.setOnClickListener(v -> {
            items.clear();
            String itemNames = "";
            double total = 0;
            for (Object i : basketItems) {
                if (i instanceof BasketProductModel && ((BasketProductModel) i).isChecked()) {
                    items.add((BasketProductModel) i);
                    itemNames += ((BasketProductModel) i).getProductName() + " = ₱" + (((BasketProductModel) i).getProductPrice() * ((BasketProductModel) i).getProductBasketQuantity()) + " \n";
                    total += (((BasketProductModel) i).getProductPrice() * ((BasketProductModel) i).getProductBasketQuantity());
                }
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Create Order");
            if (location == null) {
                alert.setMessage("Select select address");
                alert.setPositiveButton("Ok",null);
            } else if(items.size()<=0){
                alert.setMessage("Select a product to checkout");
                alert.setPositiveButton("Ok",null);
            }else {
                alert.setMessage("Are you sure you want to proceed this order with " + items.size() + " number of Items : \n" + itemNames + "=====================\nTotal = ₱ " + total);
                alert.setCancelable(false);
                alert.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items.size() > 0 && location != null) {
                            ProgressDialog progressDialog;
                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Processing your order, please wait...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            OrderManagement.createOrder(getActivity(), items, location)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Log.d(TAG, "Transaction success");
                                            AuthValidation.successToast(getContext(), "Order placed successfully").show();
                                            displayBasketItem();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Transaction failure", e);
                                            progressDialog.dismiss();
                                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                            alert.setTitle("Failed to make an order:");
                                            alert.setMessage(e.getLocalizedMessage());
                                            alert.setCancelable(false);
                                            alert.setPositiveButton("Ok", null);
                                            alert.show();

                                        }
                                    });
                        }
                    }
                });
                alert.setNegativeButton("Cancel", null);
            }
            alert.show();
        });

        select_all_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < basketItems.size(); i++) {
                    if (basketItems.get(i) instanceof BasketProductModel) {
                        ((BasketProductModel) basketItems.get(i)).setChecked(select_all_cb.isChecked());
                    } else
                        ((BasketHeader) basketItems.get(i)).setChecked(select_all_cb.isChecked());
                }
                basketListAdapter.notifyDataSetChanged();
            }
        });
        basket_item_list.setAdapter(basketListAdapter);
        basket_item_list.setEmptyView(empty_basket_ll);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Starting Basket...");

    }

    @Override
    public void onPause() {
        super.onPause();
//        registration.remove();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resuming Basket...");
        displayBasketItem();
    }

    public void displayBasketItem() {
        Log.d(TAG, "Listen failed.");
//        registration = db.collection("basket").document(user.getUid()).collection("products")
//                .orderBy("productDateAdded", Query.Direction.DESCENDING)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
//                        basketItems.clear();
//                        basketListAdapter.notifyDataSetChanged();
////                        if (value.isEmpty()) {
////                            empty_basket_ll.setVisibility(View.VISIBLE);
////                            mid_ll.setVisibility(View.GONE);
////                        } else {
////                            empty_basket_ll.setVisibility(View.GONE);
////                            mid_ll.setVisibility(View.VISIBLE);
////                        }
//                        if (e != null) {
//                            Log.d(TAG, "Listen failed.", e);
//                            return;
//                        }
//                        final String[] tempId = {""};
//
//                        for (QueryDocumentSnapshot document : value) {
//                            BasketProductModel item = document.toObject(BasketProductModel.class);
//                            BasketHeader basketHeader = new BasketHeader();
//                            //Log.d(TAG, document.getId() + " => " + document.getData().get("productUserId"));
//                            db.collection("users")
//                                    .document(document.getData().get("productUserId").toString())
//                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                            if (task.isSuccessful()) {
//                                                DocumentSnapshot doc = task.getResult();
//                                                if (doc.exists()) {
//                                                    basketHeader.setFarmId(doc.getId());
//                                                    basketHeader.setFarmName((doc.getData().get("userDisplayName")).toString());
//                                                    if (tempId[0].equals("") || !tempId[0].equals(doc.getId())) {
//                                                        basketItems.add(basketHeader);
//                                                        tempId[0] = doc.getId();
//                                                    }
//                                                    basketItems.add(item);
//                                                    Log.d(TAG, "DocumentSnapshot data: " + basketItems.size());
//                                                } else {
//                                                    Log.d(TAG, "No such document");
//                                                }
//                                                basketListAdapter.notifyDataSetChanged();
//                                            } else {
//                                                Log.d(TAG, "get failed with ", task.getException());
//                                            }
//                                        }
//                                    });
//                        }
//                    }
//                });
        db.collection("basket").document(user.getUid()).collection("products")
                .orderBy("productDateAdded", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            basketItems.clear();
                            basketListAdapter.notifyDataSetChanged();
//                        if (value.isEmpty()) {
//                            empty_basket_ll.setVisibility(View.VISIBLE);
//                            mid_ll.setVisibility(View.GONE);
//                        } else {
//                            empty_basket_ll.setVisibility(View.GONE);
//                            mid_ll.setVisibility(View.VISIBLE);
//                        }
                            if (task.getException() != null) {
                                Log.d(TAG, "Listen failed.", task.getException());
                                return;
                            }
                            final String[] tempId = {""};

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BasketProductModel item = document.toObject(BasketProductModel.class);
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
                                                        Log.d(TAG, "DocumentSnapshot data: " + basketItems.size());
                                                    } else {
                                                        Log.d(TAG, "No such document");
                                                    }
                                                    basketListAdapter.notifyDataSetChanged();
                                                } else {
                                                    Log.d(TAG, "get failed with ", task.getException());
                                                }
                                            }
                                        });
                            }
                        }
                        else{

                        }
                    }
                });


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
            if (basketItems.get(position) instanceof BasketProductModel) {
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
                    farmer_hub_name.setText(((BasketHeader) basketItems.get(position)).getFarmName().substring(0, ((BasketHeader) basketItems.get(position)).getFarmName().length() - 2));
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
                    LinearLayout delete_cancel_ll = convertView.findViewById(R.id.delete_cancel_ll);
                    ImageButton cancel_ib = convertView.findViewById(R.id.cancel_ib);
                    cancel_ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delete_cancel_ll.setVisibility(View.GONE);
                        }
                    });
                    convertView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            delete_cancel_ll.setVisibility(View.VISIBLE);

                            return false;
                        }
                    });
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
                                    BasketManagement bm = new BasketManagement(getActivity());
                                    bm.deleteFromBasket(((BasketProductModel) basketItems.get(position)).getProductId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                AuthValidation.successToast(getContext(), "Item removed").show();
                                                displayBasketItem();
                                            } else {
                                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                                alert.setTitle("Error");
                                                alert.setMessage(task.getException().getLocalizedMessage());
                                                alert.setPositiveButton("Ok", null);
                                            }
                                        }
                                    });
                                }
                            });
                            alert.setNegativeButton("No", null);
                            alert.show();
                        }
                    });
//                    convertView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//                            alert.setTitle("Basket Item:");
//                            alert.setMessage(((BasketProductModel) basketItems.get(position)).getProductName());
//                            alert.setPositiveButton("Ok", null);
//                            alert.show();
//                        }
//                    });
                    ImageView item_image = convertView.findViewById(R.id.item_image);
                    TextView item_name = convertView.findViewById(R.id.item_name);
                    TextView productBasketQuantity_tv = convertView.findViewById(R.id.productBasketQuantity_tv);

//
                    TextView item_price = convertView.findViewById(R.id.item_price);
                    TextView item_stocks = convertView.findViewById(R.id.item_stocks);
                    TextView minus_tv = convertView.findViewById(R.id.minus_tv);
                    minus_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DocumentReference basketDocRef = db.collection("basket").document(user.getUid()).collection("products").document(((BasketProductModel) basketItems.get(position)).getProductId());
                            DocumentReference prodDocRef = db.collection("products").document(((BasketProductModel) basketItems.get(position)).getProductId());

                            db.runTransaction(new Transaction.Function<Void>() {
                                        @Override
                                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot basketSnapshot = transaction.get(basketDocRef);
                                            int count = Integer.parseInt(basketSnapshot.get("productBasketQuantity").toString()) - 1;
                                            if (count > 0) {
                                                transaction.update(basketDocRef, "productBasketQuantity", count);
                                                return null;
                                            } else {
                                                throw new FirebaseFirestoreException("Quantity must not less or equal to Zero!",
                                                        FirebaseFirestoreException.Code.ABORTED);
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void result) {
                                            Log.d(TAG, "Transaction success: " + result);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Transaction failure.", e);
                                            AlertDialog.Builder alert = new AlertDialog.Builder((getContext()));
                                            alert.setTitle("Failed!");
                                            alert.setMessage("Quantity must not less or equal to Zero!");
                                            alert.setCancelable(false);
                                            alert.setPositiveButton("Ok", null);
                                            alert.show();
                                        }
                                    });
                        }
                    });
                    TextView add_tv = convertView.findViewById(R.id.add_tv);
                    add_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DocumentReference basketDocRef = db.collection("basket").document(user.getUid()).collection("products").document(((BasketProductModel) basketItems.get(position)).getProductId());
                            DocumentReference prodDocRef = db.collection("products").document(((BasketProductModel) basketItems.get(position)).getProductId());

                            db.runTransaction(new Transaction.Function<Void>() {
                                        @Override
                                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot basketSnapshot = transaction.get(basketDocRef);
                                            DocumentSnapshot prodSnapshot = transaction.get(prodDocRef);
                                            int count = Integer.parseInt(basketSnapshot.get("productBasketQuantity").toString()) + 1;
                                            int stocks = Integer.parseInt(prodSnapshot.get("productStocks").toString());
                                            if (stocks >= count) {
                                                transaction.update(basketDocRef, "productBasketQuantity", count);
                                                return null;
                                            } else {
                                                throw new FirebaseFirestoreException("Insufficient Product Stocks.",
                                                        FirebaseFirestoreException.Code.ABORTED);
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void result) {
                                            Log.d(TAG, "Transaction success: " + result);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Transaction failure.", e);
                                            AlertDialog.Builder alert = new AlertDialog.Builder((getContext()));
                                            alert.setTitle("Failed!");
                                            alert.setMessage("Insufficient Product Stocks.");
                                            alert.setCancelable(false);
                                            alert.setPositiveButton("Ok", null);
                                            alert.show();
                                        }
                                    });
                        }
                    });
                    item_name.setText(((BasketProductModel) basketItems.get(position)).getProductName());
                    item_price.setText("Php " + String.format("%.2f", ((BasketProductModel) basketItems.get(position)).getProductPrice()));
                    item_stocks.setText("Stocks: " + ((BasketProductModel) basketItems.get(position)).getProductStocks());
                    CheckBox item_checkbox = convertView.findViewById(R.id.item_checkbox);
                    item_checkbox.setChecked(((BasketProductModel) basketItems.get(position)).isChecked());
                    String sp = ((BasketProductModel) basketItems.get(position)).getProductImage().get(0);

                    Glide.with(context)
                            .load(sp)
                            .centerCrop()
                            .into(item_image);

                    productBasketQuantity_tv.setText(((BasketProductModel) basketItems.get(position)).getProductBasketQuantity() + "");
                    productBasketQuantity_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog dialog = new Dialog(getContext());
                            dialog.setContentView(R.layout.quantity_dialog_layout);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCancelable(false);
                            EditText qty_et = dialog.findViewById(R.id.qty_et);
                            qty_et.setText(((BasketProductModel) basketItems.get(position)).getProductBasketQuantity() + "");
                            Button cancel_btn = dialog.findViewById(R.id.cancel_btn);
                            cancel_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            Button update_qty_btn = dialog.findViewById(R.id.update_qty_btn);
                            update_qty_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                   String quantity = qty_et.getText().toString();
                                    if(!ProductValidation.validateQuantity(quantity).equals("")){
                                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                        alert.setTitle("Failed!");
                                        alert.setMessage(ProductValidation.validateQuantity(quantity));
                                        alert.setCancelable(false);
                                        alert.setPositiveButton("Ok",null);
                                        alert.show();
                                    }
                                    else{
                                        ProgressDialog progressDialog;
                                        progressDialog = new ProgressDialog(getContext());
                                        progressDialog.setMessage("Updating Prduct Qty, please wait!");
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();
                                        DocumentReference prodDocRef = db.collection("products").document(((BasketProductModel) basketItems.get(position)).getProductId());
                                        DocumentReference basketDocRef = db.collection("basket").document(user.getUid()).collection("products").document(((BasketProductModel) basketItems.get(position)).getProductId());
                                        db.runTransaction(new Transaction.Function<Void>() {
                                                    @Override
                                                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                        DocumentSnapshot prodSnapshot = transaction.get(prodDocRef);
                                                        int stocks = Integer.parseInt(prodSnapshot.get("productStocks").toString());
                                                        if (stocks >= Integer.parseInt(quantity) && Integer.parseInt(quantity)>0) {
                                                            transaction.update(basketDocRef, "productBasketQuantity",Integer.parseInt(quantity) );
                                                            return null;
                                                        } else {
                                                            throw new FirebaseFirestoreException("Product Stocks cannot handle Quantity",
                                                                    FirebaseFirestoreException.Code.ABORTED);
                                                        }
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void result) {
                                                        Log.d(TAG, "Transaction success: " + result);
                                                        dialog.dismiss();
                                                        progressDialog.dismiss();
                                                        AuthValidation.successToast(getContext(),"Successfully Updated Quantity").show();
                                                        displayBasketItem();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Transaction failure.", e);
                                                        progressDialog.dismiss();
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                                        alert.setTitle("Failed!");
                                                        alert.setMessage(e.getLocalizedMessage());
                                                        alert.setCancelable(false);
                                                        alert.setPositiveButton("Ok",null);
                                                        alert.show();
                                                    }
                                                });
                                    }
                                }
                            });
                            dialog.show();
                        }
                    });
                    item_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            ((BasketProductModel) basketItems.get(position)).setChecked(isChecked);
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
                if (basketItems.get(i) instanceof BasketProductModel) {
                    BasketProductModel pi = (BasketProductModel) basketItems.get(i);
                    if (pi.isChecked())
                        total += (pi.getProductBasketQuantity() * pi.getProductPrice());
                }

            }
            totalAmount_tv.setText("Total: ₱" + String.format("%.2f", total));
        }

        private void isCheckAll() {
            boolean overAllItemCheckFlag = true;
            for (int i = 0; i < basketItems.size(); i++) {
                if (getItemViewType(i) == ITEM && !((BasketProductModel) basketItems.get(i)).isChecked()) {
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
                    if (getItemViewType(x) == HEADER && ((BasketProductModel) basketItems.get(position)).getProductUserId().equals(((BasketHeader) basketItems.get(x)).getFarmId())) {
                        ((BasketHeader) basketItems.get(x)).setChecked(false);
                        notifyDataSetChanged();
                        break;
                    }
                }
            } else {
                for (int x = 0; x < basketItems.size(); x++) {
                    if (getItemViewType(x) == ITEM && ((BasketProductModel) basketItems.get(position)).getProductUserId().equals(((BasketProductModel) basketItems.get(x)).getProductUserId()) && position != x) {
                        if (!((BasketProductModel) basketItems.get(x)).isChecked()) {
                            allCheckFlag = false;
                            locator = x;
                            break;
                        }
                    }

                }
                if (allCheckFlag)
                    for (int x = 0; x < basketItems.size(); x++) {
                        if (getItemViewType(x) == HEADER && ((BasketHeader) basketItems.get(x)).getFarmId().equals(((BasketProductModel) basketItems.get(position)).getProductUserId())) {
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
                if (getItemViewType(x) == ITEM && ((BasketProductModel) basketItems.get(x)).getProductUserId().equals(((BasketHeader) basketItems.get(position)).getFarmId())) {
                    ((BasketProductModel) basketItems.get(x)).setChecked(isChecked);
                }
            }
            //basket_item_list.setAdapter(new BasketListAdapter(getContext(), basketItems));
            notifyDataSetChanged();
        }
    }

}