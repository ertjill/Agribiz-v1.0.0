package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.entities.BasketProductModel;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Date;

public class BasketManagement {
    Activity activity;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "BasketManagement";

    public BasketManagement(Activity activity) {
        this.activity = activity;
    }

    public Task<Void> addToBasket(String productId, int qty) {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Adding product to basket...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference prodDocRef = db.collection("products").document(productId);
        final DocumentReference basketProdRef = db.collection("basket").document(user.getUid()).collection("products").document(productId);
        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(prodDocRef);
                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                long stocks = snapshot.getLong("productStocks");
                BasketProductModel basketProductModel = snapshot.toObject(BasketProductModel.class);
                basketProductModel.setCustomerId(user.getUid());
                basketProductModel.setProductBasketQuantity(qty);
                basketProductModel.setProductDateAdded(new Timestamp(new Date()));
                if ((int) basketProductModel.getProductBasketQuantity() <= stocks) {

                    transaction.set(basketProdRef, basketProductModel);

                } else {
                    throw new FirebaseFirestoreException("Insufficient product stocks ",
                            FirebaseFirestoreException.Code.ABORTED);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Log.d(TAG, "Transaction success!");
                AuthValidation.successToast(activity, "Product added to basket").show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.w(TAG, "Transaction failure.", e);
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Adding Product to Basket");
                alert.setMessage(e.getMessage());
                alert.setPositiveButton("Okay", null);
                alert.show();
            }
        });
    }

    public Task<Void> deleteFromBasket(String id) {
        return db.collection("basket").document(user.getUid()).collection("products").document(id).delete();
    }

    public static CollectionReference getBaskitItems(String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("basket").document(userId).collection("products");
    }
}
