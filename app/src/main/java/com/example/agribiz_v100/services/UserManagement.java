package com.example.agribiz_v100.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserManagement {

    public static Task<QuerySnapshot> getFarmers(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users").whereEqualTo("userType","farmer").get();
    }

}
