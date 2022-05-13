package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.entities.BasketProductModel;
import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.entities.OrderProductModel;
import com.example.agribiz_v100.validation.AuthValidation;
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
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderManagement {

    private static final String TAG = "OrderManagement";

    public static Task<Void> createOrder(Activity activity, List<BasketProductModel> items, LocationModel location){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return db.runTransaction(new Transaction.Function<Void>() {
            List<Integer>stocks = new ArrayList<>();
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
                            item.setLocation(location);
                            DocumentReference prodDocRef = db.collection("products").document(item.getProductId());
                            transaction.update(prodDocRef,"productStocks",stocks.get(i1++)-item.getProductBasketQuantity());

                            DocumentReference basketDocRef = db.collection("basket").document(user.getUid()).collection("products").document(item.getProductId());
                            Date orderDate = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");

                            DocumentReference orderDocRef = db.collection("orders").document(user.getUid())
                                    .collection("products").document();
                            item.setOrderID(user.getUid()+simpleDateFormat.format(orderDate));
                            item.setOrderDate(new Timestamp(orderDate));
                            transaction.set(orderDocRef,item);
                            transaction.delete(basketDocRef);
                        }

                        // Success
                        return null;
                    }
                });
    }
}
