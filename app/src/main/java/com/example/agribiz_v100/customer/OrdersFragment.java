package com.example.agribiz_v100.customer;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.dialog.RateProductDialog;
import com.example.agribiz_v100.entities.OrderProductModel;
import com.example.agribiz_v100.services.OrderManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdersFragment extends Fragment {
    String title;
    String status;
    LinearLayout no_product_ll;
    ListView orders_lv;
    List<OrderProductModel> ordersList;
    OrdersProductAdaper ordersProductAdaper;
    ListenerRegistration registration;
    final String TAG = "OrdersFragment";

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
        ordersProductAdaper = new OrdersProductAdaper();
        orders_lv.setAdapter(ordersProductAdaper);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrders();
    }

    @Override
    public void onPause() {
        super.onPause();
        registration.remove();
    }

    public void getOrders() {
        registration = OrderManagement.getOrders(status).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                ordersList.clear();

                if (value.isEmpty()) {
                    no_product_ll.setVisibility(View.VISIBLE);
                    orders_lv.setVisibility(View.GONE);
                } else {
                    no_product_ll.setVisibility(View.GONE);
                    orders_lv.setVisibility(View.VISIBLE);
                }

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                List<OrderProductModel> orders = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    orders.add(doc.toObject(OrderProductModel.class));
                }
                ordersList = orders;
                ordersProductAdaper.notifyDataSetChanged();
                Log.d(TAG, "Current cites in CA: " + orders);
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
            TextView farmer_tv, status_tv, product_tv, price_tv, quantity_tv, order_tv, total_tv;
            ImageView product_image_iv;
            Button receive_cancel_btn;
            product_image_iv = convertView.findViewById(R.id.product_image_iv);
            farmer_tv = convertView.findViewById(R.id.farmer_tv);
            status_tv = convertView.findViewById(R.id.status_tv);
            product_tv = convertView.findViewById(R.id.product_tv);
            price_tv = convertView.findViewById(R.id.price_tv);
            quantity_tv = convertView.findViewById(R.id.quantity_tv);
            order_tv = convertView.findViewById(R.id.order_tv);
            total_tv = convertView.findViewById(R.id.total_tv);
            receive_cancel_btn = convertView.findViewById(R.id.receive_cancel_btn);
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
                receive_cancel_btn.setVisibility(View.VISIBLE);
                receive_cancel_btn.setText("Cancel");
            } else if (status.equals("shipped")) {
                receive_cancel_btn.setVisibility(View.VISIBLE);
                receive_cancel_btn.setText("Order Received");
            } else if (status.equals("completed")) {
                receive_cancel_btn.setVisibility(View.VISIBLE);
                receive_cancel_btn.setText("Rate");
                if(ordersList.get(position).getProductNoCustomerRate()>0)
                    receive_cancel_btn.setEnabled(false);
                else
                    receive_cancel_btn.setEnabled(true);
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
                                OrderManagement.cancelOrder(getActivity(),ordersList.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                OrderManagement.receivedOrder(getActivity(),ordersList.get(position).getOrderID())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    AuthValidation.successToast(getContext(), "Order Received").show();
                                                }else{
                                                    AuthValidation.failedToast(getContext(), "Failed To Receive Order").show();
                                                }
                                            }
                                        });
                            }
                        });
                        alert.setNegativeButton("No", null);
                        alert.show();
                    }
                    else if (status.equals("completed")) {
                        //RECEIVED
                        RateProductDialog dialog = new RateProductDialog(getActivity(), ordersList.get(position));
                        dialog.showDialog();
                    }
                }
            });

            return convertView;
        }
    }
}