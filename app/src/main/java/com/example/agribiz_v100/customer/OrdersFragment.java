package com.example.agribiz_v100.customer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.dialog.RateProductDialog;
import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.entities.OrderProductModel;
import com.example.agribiz_v100.services.OrderManagement;
import com.example.agribiz_v100.services.ProductManagement;
import com.example.agribiz_v100.services.ProfileManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrdersFragment extends Fragment {
    String title;
    String status;
    LinearLayout no_product_ll;
    ListView orders_lv;
    List<OrderProductModel> ordersList;

    ListenerRegistration registration;
    final String TAG = "OrdersFragment";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    OrdersProductAdaper ordersProductAdaper;
    OrdersProductAdaper2 ordersProductAdaper2;

    public OrdersFragment(String title, String status) {
        this.title = title;
        this.status = status;
    }

    public OrdersFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        no_product_ll = view.findViewById(R.id.no_product_ll);
        orders_lv = view.findViewById(R.id.orders_lv);
        ordersList = new ArrayList<>();
        if (user.getDisplayName().charAt(user.getDisplayName().length() - 1) == 'c') {
            ordersProductAdaper = new OrdersProductAdaper();
            orders_lv.setAdapter(ordersProductAdaper);
        } else {
            ordersProductAdaper2 = new OrdersProductAdaper2();
            orders_lv.setAdapter(ordersProductAdaper2);

        }
        orders_lv.setEmptyView(no_product_ll);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resuming" + title);
        getOrders();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Pausing " + title);
        registration.remove();
    }

    public void getOrders() {
        registration = OrderManagement.getOrders(status).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                List<OrderProductModel> orders = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    orders.add(doc.toObject(OrderProductModel.class));
                }
                ordersList = orders;
                if (user.getDisplayName().charAt(user.getDisplayName().length() - 1) == 'c')
                    ordersProductAdaper.notifyDataSetChanged();
                else
                    ordersProductAdaper2.notifyDataSetChanged();
            }
        });
    }

    public class OrdersProductAdaper extends BaseAdapter {
        LayoutInflater inflater;

        @Override
        public int getCount() {
            return ordersList.size();
        }

        public OrdersProductAdaper() {
            this.inflater = LayoutInflater.from(getContext());
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

        }

        @Override
        public OrderProductModel getItem(int position) {
            return ordersList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.orders_layout_item, null);
            TextView farmer_name_tv, status_tv, product_tv, price_tv, quantity_tv, order_tv, total_tv,desc_tv,address_tv;
            ImageView product_image_iv,farmer_image_iv;

            LinearLayout rating_ll = convertView.findViewById(R.id.rating_ll);
            Button receive_cancel_btn;
            desc_tv = convertView.findViewById(R.id.desc_tv);
            desc_tv.setText(ordersList.get(position).getProductDescription());
            address_tv = convertView.findViewById(R.id.address_tv);
            RelativeLayout chat_rl = convertView.findViewById(R.id.chat_rl);

            address_tv.setText(((Map<String,String>)ordersList.get(position).getLocation()).get("userFullName")+" | "+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userPhoneNumber")+"\n"+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userSpecificAddress")+"\n"+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userBarangay")+", "+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userMunicipality")+", "+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userProvince")+", "+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userRegion")+", "+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userZipCode"));

            product_image_iv = convertView.findViewById(R.id.product_image_iv);
            product_image_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            farmer_image_iv=convertView.findViewById(R.id.farmer_image_iv);
            farmer_name_tv = convertView.findViewById(R.id.farmer_name_tv);
            ProfileManagement.getUserProfile(ordersList.get(position).getProductUserId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    Glide.with(getContext())
                            .load(documentSnapshot.getString("userImage"))
                            .into(farmer_image_iv);
                    farmer_name_tv.setText(documentSnapshot.getString("userDisplayName").substring(0,documentSnapshot.getString("userDisplayName").length()-2));
                }
            });
            status_tv = convertView.findViewById(R.id.status_tv);
            product_tv = convertView.findViewById(R.id.product_tv);
            price_tv = convertView.findViewById(R.id.price_tv);
            quantity_tv = convertView.findViewById(R.id.quantity_tv);
            order_tv = convertView.findViewById(R.id.order_tv);
            total_tv = convertView.findViewById(R.id.total_tv);
            receive_cancel_btn = convertView.findViewById(R.id.receive_cancel_btn);
            status_tv.setText(ordersList.get(position).getOrderStatus().toUpperCase(Locale.ROOT));



            for (int i = 0; i < 5; i++) {
                ImageView star = new ImageView(getContext());
                if (i < ordersList.get(position).getProductRating()) {
                    star.setImageResource(R.drawable.ic_baseline_star);
                    rating_ll.addView(star);
                } else {
                    star.setImageResource(R.drawable.ic_baseline_star_outline_24);
                    rating_ll.addView(star);
                }
            }


            Glide.with(getContext())
                    .load(ordersList.get(position).getProductImage().get(0))
                    .into(product_image_iv);
            product_tv.setText(ordersList.get(position).getProductName());
            price_tv.setText("Php " + String.format("%.2f", ordersList.get(position).getProductPrice()));
            quantity_tv.setText("Qty: " + ordersList.get(position).getProductQuantity() + " " + ordersList.get(position).getProductUnit());
            order_tv.setText(ordersList.get(position).getProductBasketQuantity() + " Order");
            total_tv.setText("₱" + String.format("%.2f", (ordersList.get(position).getProductBasketQuantity() * ordersList.get(position).getProductPrice())));

            if (status.equals("pending")) {
                receive_cancel_btn.setVisibility(View.VISIBLE);
                receive_cancel_btn.setBackgroundColor(getResources().getColor(R.color.cancel));
                receive_cancel_btn.setTextColor(getResources().getColor(R.color.white));
                receive_cancel_btn.setText("Cancel");
            } else if (status.equals("shipped")) {
                receive_cancel_btn.setVisibility(View.VISIBLE);
                receive_cancel_btn.setBackgroundColor(getResources().getColor(R.color.yellow_orange));
                receive_cancel_btn.setTextColor(getResources().getColor(R.color.army_green));
                receive_cancel_btn.setText("Order Received");
            } else if (status.equals("completed")) {
                receive_cancel_btn.setVisibility(View.VISIBLE);
                receive_cancel_btn.setBackgroundColor(getResources().getColor(R.color.yellow_orange));
                receive_cancel_btn.setTextColor(getResources().getColor(R.color.army_green));
                receive_cancel_btn.setText("Rate");
                if (ordersList.get(position).isRated())
                    receive_cancel_btn.setVisibility(View.GONE);
                else
                    receive_cancel_btn.setVisibility(View.VISIBLE);
            } else
                receive_cancel_btn.setVisibility(View.GONE);
            receive_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (status.equals("pending")) {
                        //CANCEL
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Cancel Order:");
                        alert.setMessage("Do you really want to cancel this order?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OrderManagement.cancelOrder(getActivity(), ordersList.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void result) {
                                                Log.d(TAG, "Transaction success: ");
                                                AuthValidation.successToast(getContext(), "Order Cancelled").show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Transaction failure.", e);
                                                AuthValidation.failedToast(getContext(), "Failed Cancel Order").show();
                                            }
                                        });
                            }
                        });
                        alert.setNegativeButton("No", null);
                        alert.show();

                    } else if (status.equals("shipped")) {
                        //RECEIVED
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Order Received:");
                        alert.setMessage("Click confirm to Received. \nOnce you confirmed it cannot be undone.");
                        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OrderManagement.receivedOrder(getActivity(), ordersList.get(position));
                            }
                        });
                        alert.setNegativeButton("No", null);
                        alert.show();
                    } else if (status.equals("completed")) {
                        //RECEIVED
                        RateProductDialog dialog = new RateProductDialog(getActivity(), ordersList.get(position));
                        dialog.showDialog();
                    }
                }
            });

            return convertView;
        }
    }

    public class OrdersProductAdaper2 extends BaseAdapter {
        LayoutInflater inflater;

        @Override
        public int getCount() {
            return ordersList.size();
        }

        public OrdersProductAdaper2() {
            this.inflater = LayoutInflater.from(getContext());
        }

        @Override
        public OrderProductModel getItem(int position) {
            return ordersList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.farm_agr_order_item_layout, null);
            TextView description_tv, status_tv, product_tv, price_tv, quantity_tv, order_tv, total_tv, address_tv;
            ImageView product_image_iv;
            LinearLayout rating_ll = convertView.findViewById(R.id.rating_ll);
            TextView not_rated_tv =convertView.findViewById(R.id.not_rated_tv);
            ImageView cutomer_image_iv = convertView.findViewById(R.id.cutomer_image_iv);
            TextView customer_name_tv=convertView.findViewById(R.id.customer_name_tv);
            address_tv=convertView.findViewById(R.id.address_tv);
            RelativeLayout chat_rl = convertView.findViewById(R.id.chat_rl);
            address_tv.setText(((Map<String,String>)ordersList.get(position).getLocation()).get("userFullName")+" | "+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userPhoneNumber")+"\n"+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userSpecificAddress")+"\n"+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userBarangay")+", "+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userMunicipality")+", "+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userProvince")+", "+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userRegion")+", "+
                    ((Map<String,String>)ordersList.get(position).getLocation()).get("userZipCode"));

            ProfileManagement.getUserProfile(ordersList.get(position).getCustomerId())
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Glide.with(getContext())
                                    .load(documentSnapshot.get("userImage").toString())
                                    .into(cutomer_image_iv);

                        }
                    });
            customer_name_tv.setText(((Map<String,Object>)ordersList.get(position).getLocation()).get("userFullName").toString());
            if (ordersList.get(position).isRated()) {
                not_rated_tv.setVisibility(View.GONE);
                for (int i = 0; i < 5; i++) {
                    ImageView star = new ImageView(getContext());

                    if (i < ordersList.get(position).getProductRating()) {
                        star.setImageResource(R.drawable.ic_baseline_star);
                        rating_ll.addView(star);
                    } else {
                        star.setImageResource(R.drawable.ic_baseline_star_outline_24);
                        rating_ll.addView(star);
                    }
                }
            }
            else
                not_rated_tv.setVisibility(View.VISIBLE);
            Button cancel_btn, prepared_shipped_btn;
            product_image_iv = convertView.findViewById(R.id.product_image_iv);
            description_tv = convertView.findViewById(R.id.description_tv);
            status_tv = convertView.findViewById(R.id.status_tv);
            product_tv = convertView.findViewById(R.id.product_tv);
            price_tv = convertView.findViewById(R.id.price_tv);
            quantity_tv = convertView.findViewById(R.id.quantity_tv);
            order_tv = convertView.findViewById(R.id.order_tv);
            total_tv = convertView.findViewById(R.id.total_tv);
            cancel_btn = convertView.findViewById(R.id.cancel_btn);
            prepared_shipped_btn = convertView.findViewById(R.id.prepared_shipped_btn);
            description_tv.setText(ordersList.get(position).getProductDescription());
            status_tv.setText(ordersList.get(position).getOrderStatus().toUpperCase(Locale.ROOT));
            Glide.with(getContext())
                    .load(ordersList.get(position).getProductImage().get(0))
                    .into(product_image_iv);
            product_tv.setText(ordersList.get(position).getProductName());
            price_tv.setText("Php " + String.format("%.2f", ordersList.get(position).getProductPrice()));
            quantity_tv.setText("Qty: " + ordersList.get(position).getProductQuantity() + " " + ordersList.get(position).getProductUnit());
            order_tv.setText(ordersList.get(position).getProductBasketQuantity() + " Order");
            total_tv.setText("₱" + String.format("%.2f", (ordersList.get(position).getProductBasketQuantity() * ordersList.get(position).getProductPrice())));

            if (status.equals("pending")) {
                prepared_shipped_btn.setText("Prepared");
            } else if (status.equals("prepared")) {
                prepared_shipped_btn.setText("Shipped");
            } else if (status.equals("shipped")) {
                prepared_shipped_btn.setVisibility(View.GONE);
            } else {
                cancel_btn.setVisibility(View.GONE);
                prepared_shipped_btn.setVisibility(View.GONE);
            }
            prepared_shipped_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (status.equals("pending")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Prepared");
                        alert.setMessage("Are sure to tag this product as Prepared?");
                        alert.setCancelable(false);
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OrderManagement.updateOrderStatus(getActivity(), ordersList.get(position).getCustomerId(), ordersList.get(position).getOrderID(), "prepared")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                getOrders();
                                            }
                                        });

                            }
                        });
                        alert.setNegativeButton("No", null);
                        alert.show();
                    } else if (status.equals("prepared")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Shipped");
                        alert.setMessage("Are sure to tag this product as Shipped?");
                        alert.setCancelable(false);
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OrderManagement.updateOrderStatus(getActivity(), ordersList.get(position).getCustomerId(), ordersList.get(position).getOrderID(), "shipped")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                getOrders();
                                            }
                                        });

                            }
                        });
                        alert.setNegativeButton("No", null);
                        alert.show();
                    }
                }
            });
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //CANCEL
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Cancel Order:");
                    alert.setMessage("Do you really want to cancel this order?");
                    alert.setCancelable(false);
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OrderManagement.cancelOrder(getActivity(), ordersList.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void result) {
                                            Log.d(TAG, "Transaction success: ");
                                            AuthValidation.successToast(getContext(), "Order Cancelled").show();
                                            getOrders();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Transaction failure.", e);
                                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                            alert.setTitle("Failed!");
                                            alert.setMessage(e.getLocalizedMessage());
                                            alert.setCancelable(false);
                                            alert.setPositiveButton("Ok", null);
                                            alert.show();
                                        }
                                    });
                        }
                    });
                    alert.setNegativeButton("No", null);
                    alert.show();

                }
            });

            return convertView;
        }
    }
}