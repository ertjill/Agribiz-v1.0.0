package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.validation.AuthValidation;
import com.example.agribiz_v100.validation.RequestAssistanceValidation;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.List;

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
                DocumentSnapshot snapshot = transaction.get(userDocRef);
                List<BarterModel> barter_items = (List<BarterModel>) snapshot.getData().get("barterId");
                int i = 0;
                if (barter_items != null) {
                    i = barter_items.size();
                }
                if (i <= 3) {
                    Log.d("BarterManagement", "Added barter item");
                    transaction.set(bartersDocRef, bm);
                } else {
                    throw new FirebaseFirestoreException("Barter items exceeds limit",
                            FirebaseFirestoreException.Code.ABORTED);
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

//    public static Task<QuerySnapshot> getBarterItems(DocumentSnapshot last, String userID) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        if (last == null) {
//            return db.collection("barters")
//                    .whereEqualTo("barterUserID", userID)
//                    .orderBy("barterDateUploaded", Query.Direction.ASCENDING)
//                    .get();
//        } else {
//            return db.collection("barters")
//                    .whereEqualTo("barterUserID", userID)
//                    .orderBy("barterDateUploaded", Query.Direction.ASCENDING)
//                    .startAfter(last)
//                    .get();
//        }
//    }

    public static Query getBarter(String status) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return db.collection("barters")
                .whereEqualTo("barterUserID", user.getUid())
                .whereEqualTo("barterStatus", status)
                .orderBy("barterDateUploaded", Query.Direction.DESCENDING);
    }

}
