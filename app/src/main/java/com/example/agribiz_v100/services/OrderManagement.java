package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.entities.BasketProductModel;
import com.example.agribiz_v100.entities.OrderProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderManagement {

    private static final String TAG = "OrderManagement";

    public static Task<Void> createOrder(Activity activity, List<BasketProductModel> items, Object location){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Processing your Order, please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return db.runTransaction(new Transaction.Function<Void>() {
            List<Integer>stocks = new ArrayList<>();
            List<Integer>sold = new ArrayList<>();
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        List<OrderProductModel> opm = new ArrayList<>();
                        int i=0;
                        for(BasketProductModel item:items){
                            DocumentReference prodDocRef = db.collection("products").document(item.getProductId());
                            DocumentReference userDocRef = db.collection("users").document(user.getUid());
                            DocumentReference basketDocRef = db.collection("basket").document(user.getUid()).collection("products").document(item.getProductId());
                            DocumentSnapshot snapshot = transaction.get(prodDocRef);
                            DocumentSnapshot basketSnapshot = transaction.get(basketDocRef);

                            stocks.add(i ,Integer.parseInt(snapshot.get("productStocks").toString()));
                            sold.add((Integer.parseInt(snapshot.get("productSold").toString())+ item.getProductBasketQuantity()));
                            if(stocks.get(i) < item.getProductBasketQuantity()){
                                throw new FirebaseFirestoreException("Insufficient Product Stocks",
                                        FirebaseFirestoreException.Code.ABORTED);
                            }
                            DocumentSnapshot userSnapshot = transaction.get(userDocRef);
                            opm.add(basketSnapshot.toObject(OrderProductModel.class));
                            i++;
                        }
                        int i1=0;
                        for(OrderProductModel item:opm){
                            Date orderDate = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                            item.setOrderID(user.getUid()+simpleDateFormat.format(orderDate));
                            item.setLocation(location);

                            DocumentReference prodDocRef = db.collection("products").document(item.getProductId());
                            transaction.update(prodDocRef,"productStocks",stocks.get(i1)-item.getProductBasketQuantity());
                            transaction.update(prodDocRef,"productSold",sold.get(i1));

                            DocumentReference basketDocRef = db.collection("basket").document(user.getUid()).collection("products").document(item.getProductId());
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            DocumentReference orderDocRef = db.collection("orders").document(user.getUid())
                                    .collection("products").document(item.getOrderID());

                            item.setOrderDate(new Timestamp(orderDate));
                            transaction.set(orderDocRef,item);
                            transaction.delete(basketDocRef);
                            i1++;
                        }

                        // Success
                        return null;
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }
    public static Task<Void> receivedOrder(Activity activity,String id){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Completing your Order, please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("orders").document(user.getUid()).collection("products").document(id)
                .update("orderStatus", "completed").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                    }
                });
    }
    public static Task<Void> cancelOrder(Activity activity,OrderProductModel order){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Cancelling Order, please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("orders").document(user.getUid())
                .collection("products").document(order.getOrderID());
        DocumentReference prodDocRef = db.collection("products").document(order.getProductId());

        return db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(userDocRef);
                        DocumentSnapshot prodSnapshot = transaction.get(prodDocRef);
                        int stocks = Integer.parseInt( prodSnapshot.get("productStocks").toString())+ order.getProductBasketQuantity();
                        int sold = (Integer.parseInt(prodSnapshot.get("productSold").toString())- order.getProductBasketQuantity());
                        String status = snapshot.get("orderStatus").toString();
                        if (status.equals("pending")) {
                            transaction.update(prodDocRef,"productStocks",stocks);
                            transaction.update(prodDocRef,"productSold",sold);
                            transaction.delete(userDocRef);
                            return null;
                        } else {
                            throw new FirebaseFirestoreException("Population too high",
                                    FirebaseFirestoreException.Code.ABORTED);
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }

    public static Query getOrders(String status){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("orders").document(user.getUid()).collection("products").whereEqualTo("orderStatus",status);
    }
}
