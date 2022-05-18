package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.agribiz_v100.entities.BarterModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

public class BarterManagement {

    public static Task<Void> addBarterItem(BarterModel bm, Activity activity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userDocRef = db.collection("users").document(bm.getBarterUserId());
        final DocumentReference bartersDocRef = db.collection("barters").document(bm.getBarterId());
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Processing your request...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userDocRef);

                int userBarteredCount = userSnapshot.get("userBarteredCount") != null ? Integer.parseInt(userSnapshot.get("userBarteredCount").toString()) : 1;

                if (userBarteredCount <= 3) {

                    transaction.update(userDocRef, "userBarteredCount", userBarteredCount);
                    transaction.set(bartersDocRef, bm);

                } else {
                    throw new FirebaseFirestoreException("Barter items exceeds limit, Complete present barters first.", FirebaseFirestoreException.Code.ABORTED);
                }

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("BarterManagement", "Successfully added barter item");

                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("BarterManagement", "Failed to add barter item");
                progressDialog.dismiss();
            }
        });
    }

    public static Query getBarteredItems(String type){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("barters").whereEqualTo("barterType", type);
    }

    public static Query getBarteredItems(String userId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("barters").whereEqualTo("barterUserId", userId)
                .whereEqualTo("barterStatus", status)
                .orderBy("barterDateUploaded", Query.Direction.DESCENDING);
    }

    public static Task<Void> removeBarteredItem(String barterId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("barters").document(barterId)
                .delete();
    }

    public static Query getBarter(String status) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("gth", user.getUid());
        return db.collection("barters")
                .whereEqualTo("barterUserID", user.getUid())
                .whereEqualTo("barterStatus", status)
                .orderBy("barterDateUploaded", Query.Direction.DESCENDING);
    }

}
