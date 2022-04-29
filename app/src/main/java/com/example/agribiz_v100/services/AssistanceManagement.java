package com.example.agribiz_v100.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.entities.AssistanceModel;
import com.example.agribiz_v100.entities.LocationModel;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.List;

public class AssistanceManagement {

    public static Task<Void> addRequestAssistance(AssistanceModel am) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("assistance").document(am.getAssistanceID()).set(am);
    }

}
