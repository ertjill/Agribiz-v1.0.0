package com.example.agribiz_v100.services;

import com.example.agribiz_v100.entities.BarterModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class BarterManagement {

    public static Task<Void> addBarterItem(BarterModel bm) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("barters").document(bm.getBarterId()).set(bm);
    }

    public static Task<QuerySnapshot> getBarterItems(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return db.collection("barters")
            .whereEqualTo("barterUserId", userId)
            .orderBy("barterDateUploaded", Query.Direction.ASCENDING)
            .get();
    }

}
