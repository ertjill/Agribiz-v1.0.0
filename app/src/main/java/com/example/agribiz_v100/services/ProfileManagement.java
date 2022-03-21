package com.example.agribiz_v100.services;

import android.util.Log;

import com.example.agribiz_v100.entities.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileManagement {

    FirebaseFirestore db;
    private static final String TAG = "ProfileManagement";

    public ProfileManagement() {
        db = FirebaseFirestore.getInstance();
    }

    public void createAccountProfile(UserModel user) {

        db.collection("users").document(user.getUserID())
        .set(user)
        .addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot successfully written!");
            // Intent intent = new Intent(activity, CustomerMainActivity.class);
            // intent.putExtra("user", user);
            // activity.startActivity(intent);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // activity.finish();
        })
        .addOnFailureListener(e -> {
            Log.w(TAG, "User is deleted", e);

        });

    }
}
