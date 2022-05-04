package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Map;

public class BasketManagement {
    Activity activity;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "BasketManagement";

    public BasketManagement(Activity activity) {
        this.activity = activity;
    }

    public void addToBasket(Map<String, Object> product) {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Adding product to basket...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final DocumentReference prodDocRef = db.collection("products").document(product.get("productId").toString());
        final DocumentReference basketProdRef = db.collection("basket").document(user.getUid()).collection("products").document(product.get("productId").toString());
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(prodDocRef);
                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                long stocks = snapshot.getLong("productStocks");
                if ((int) product.get("productBasketQuantity") <= stocks) {
                    transaction.set(basketProdRef, product);

                } else {
                    throw new FirebaseFirestoreException("Insufficient Products Stocks ",
                            FirebaseFirestoreException.Code.ABORTED);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Log.d(TAG, "Transaction success!");
                AuthValidation.successToast(activity,"Product added to basket").show();

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.w(TAG, "Transaction failure.", e);
                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                        alert.setTitle("Adding Product to Basket:");
                        alert.setMessage(e.getMessage());
                        alert.setPositiveButton("Ok", null);
                        alert.show();
                    }
                });
    }
    public Task<Void> deleteFromBasket(String id){
        return db.collection("basket").document(user.getUid()).collection("products").document(id).delete();
    }
}
