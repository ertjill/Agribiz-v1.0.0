package com.example.agribiz_v100.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppManagement {

    public static Task<DocumentSnapshot> getSettings() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("appSettings").document("8AXp5CgroNAxyBpr1Rg6")
                .get();

    }
}
