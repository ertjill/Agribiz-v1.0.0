package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.entities.AssistanceModel;
import com.example.agribiz_v100.validation.RequestAssistanceValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

public class AssistanceManagement {

    public static Task<Void> addRequestAssistance(AssistanceModel am, Activity activity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userDocRef = db.collection("users").document(am.getAssistanceUserID());
        final DocumentReference assDocRef = db.collection("assistance").document(am.getAssistanceID());

        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Processing your request...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userDocRef);
                // Store date created value for year validation
                Timestamp dateCreated = snapshot.getTimestamp("userCreatedDate");
                // If dateCreated is not greater than equal to 365 days or is a year
                if (!RequestAssistanceValidation.hasPassedOneYear(dateCreated.toDate())) {
                    // Notify user
                    throw new FirebaseFirestoreException("You must be a year Agribiz subscriber.",
                            FirebaseFirestoreException.Code.ABORTED);
                }
                // OTHERWISE, farmer can request assistance
                // HOWEVER, the amount must not exceed to 15,000 when borrowing money
                else if (am.getAssistanceType().equalsIgnoreCase("Borrow Money") && Integer.parseInt(am.getAssistanceAmountEquipment()) > 15000) {
                    throw new FirebaseFirestoreException("Amount must not exceed to 15,000",
                            FirebaseFirestoreException.Code.ABORTED);
                }
                // AND, amount must not exceed to 5,000 when asking financial support
                else if (am.getAssistanceType().equalsIgnoreCase("Financial Support") && Integer.parseInt(am.getAssistanceAmountEquipment()) > 5000) {
                    throw new FirebaseFirestoreException("Amount must not exceed to 5,000",
                            FirebaseFirestoreException.Code.ABORTED);
                }
                // OTHERWISE, the conditions are met
                else {
                    transaction.set(assDocRef, am);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("assistance", "Transaction success!");
                progressDialog.dismiss();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("assistance", "Transaction failure.", e);
                progressDialog.dismiss();
            }
        });
    }

    public static Query getRequestAssistance(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("assistance")
                .whereEqualTo("assistanceUserID", userId)
                .orderBy("assistanceDateRequested", Query.Direction.DESCENDING);
    }

    public static Task<Void> cancelRequest(String assistanceID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference assDocumentReference = db.collection("assistance").document(assistanceID);

        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(assDocumentReference);
                // IF the request status is APPROVED
                if (snapshot.getString("assistanceStatus").equalsIgnoreCase("Approved")) {
                    // User cannot cancel the request
                    throw new FirebaseFirestoreException("Unable to cancel request",
                            FirebaseFirestoreException.Code.ABORTED);
                }
                // OTHERWISE
                else {
                    transaction.delete(assDocumentReference);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("assistance", "Transaction success!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("assistance", "Transaction failure.", e);
            }
        });
    }
}
