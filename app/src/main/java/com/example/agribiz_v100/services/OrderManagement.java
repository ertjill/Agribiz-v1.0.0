package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.agribiz_v100.entities.BasketProductModel;
import com.example.agribiz_v100.entities.OrderProductModel;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
//                        DocumentReference ordDocRef = db.collection("orders").document(user.getUid());
//                        Map<String,Object> ord = new HashMap<>();
//                        ord.put("orderCustomerId",user.getUid());
//                        transaction.set(ordDocRef, ord);
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
                            DocumentReference orderDocRef = db.collection("orders").document(item.getOrderID());

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
    public static Task<Object> receivedOrder(Activity activity, OrderProductModel order){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Completing your Order, please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference orderDocRef = db.collection("orders").document(order.getOrderID());
        DocumentReference userDocRef = db.collection("users").document(order.getProductUserId());
        return db.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userDocRef);
                transaction.update(orderDocRef,"orderStatus", "completed");
                double userRevenue = snapshot.getDouble("userRevenue")!=null?snapshot.getDouble("userRevenue")+(order.getProductBasketQuantity()*order.getProductPrice()):order.getProductBasketQuantity()*order.getProductPrice();
                transaction.update(userDocRef,"userRevenue",userRevenue);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                progressDialog.dismiss();
                AuthValidation.successToast(activity, "Order Received").show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Failed to Receive Order:");
                alert.setMessage(e.getLocalizedMessage());
                alert.setCancelable(false);
                alert.setPositiveButton("Ok", null);
                alert.show();
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

        DocumentReference userDocRef = db.collection("orders").document(order.getOrderID());
        DocumentReference prodDocRef = db.collection("products").document(order.getProductId());

        return db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(userDocRef);
                        DocumentSnapshot prodSnapshot = transaction.get(prodDocRef);
                        int stocks = Integer.parseInt( prodSnapshot.get("productStocks").toString())+ order.getProductBasketQuantity();
                        int sold = (Integer.parseInt(prodSnapshot.get("productSold").toString())- order.getProductBasketQuantity());
                        String status = snapshot.get("orderStatus").toString();
                        if (status.equals("pending")||!(user.getDisplayName().charAt(user.getDisplayName().length()-1)=='c')) {
                            transaction.update(prodDocRef,"productStocks",stocks);
                            transaction.update(prodDocRef,"productSold",sold);
                            transaction.update(userDocRef,"orderStatus","cancelled");
                            return null;
                        } else {
                            throw new FirebaseFirestoreException("Cannot cancel order",
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

    public static Task<Void> updateOrderStatus(Activity activity, String customerId, String orderId, String status){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Tagging Order as "+ status +", please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference prodDocRef = db.collection("orders").document(orderId);
        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                if (user.getDisplayName().charAt(user.getDisplayName().length()-1)=='f'&&(status.equals("prepared")||status.equals("shipped"))) {
                    transaction.update(prodDocRef,"orderStatus",status);
                    return null;
                } else {
                    throw new FirebaseFirestoreException("Cannot Tag Order as "+status,
                            FirebaseFirestoreException.Code.ABORTED);
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    AuthValidation.successToast(activity,"Successfully Tag as "+status).show();
                }else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle("Failed!");
                    alert.setMessage(task.getException().getLocalizedMessage());
                    alert.setCancelable(false);
                    alert.setPositiveButton("Ok",null);
                    alert.show();
                }
            }
        });
    }

    public static Query getOrders(String status){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getDisplayName().charAt(user.getDisplayName().length()-1) == 'c')
            return db.collection("orders")
                    .whereEqualTo("customerId",user.getUid())
                    .whereEqualTo("orderStatus",status)
                    .orderBy("orderDate", Query.Direction.DESCENDING);
        else
            return db.collection("orders")
                    .whereEqualTo("productUserId",user.getUid())
                    .whereEqualTo("orderStatus",status)
                    .orderBy("orderDate", Query.Direction.DESCENDING);
    }

}
