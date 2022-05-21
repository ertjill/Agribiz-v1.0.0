package com.example.agribiz_v100.services;

import android.app.Activity;
import android.content.Context;

import com.example.agribiz_v100.entities.ChatModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatManagement {

    public static Query getConversation(String userId, String anotherUserId, DocumentSnapshot last) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (last == null)
            return db.collection("users").document(userId)
                    .collection("threads").document(anotherUserId).collection("chats")
                    .orderBy("chatDate", Query.Direction.ASCENDING).limit(20);
        else
            return db.collection("users").document(userId)
                    .collection("threads").document(anotherUserId).collection("chats")
                    .orderBy("chatDate", Query.Direction.ASCENDING)
                    .limit(20)
                    .startAfter(last);
    }

    public static CollectionReference getThreads(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users").document(userId)
                .collection("threads");
    }

    public static Task<Void> sendMessage(Context context, String customerId, String farmerId, ChatModel chatModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Date date = new Date();

        // Get a new write batch
        WriteBatch batch = db.batch();

        DocumentReference customerChat = db.collection("users").document(customerId)
                .collection("threads").document(farmerId)
                .collection("chats").document(chatModel.getChatId());
        batch.set(customerChat, chatModel);

        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("threadId", farmerId);
        customerMap.put("updateDate", new Timestamp(date));
        DocumentReference customerThread = db.collection("users").document(customerId)
                .collection("threads").document(farmerId);
        batch.set(customerThread, customerMap);

        DocumentReference farmerChat = db.collection("users").document(farmerId)
                .collection("threads").document(customerId)
                .collection("chats")
                .document(chatModel.getChatId());
        batch.set(farmerChat, chatModel);

        Map<String, Object> farmerMap = new HashMap<>();
        farmerMap.put("threadId", customerId);
        farmerMap.put("updateDate", new Timestamp(date));
        DocumentReference farmerThread = db.collection("users")
                .document(farmerId)
                .collection("threads").document(customerId);
        batch.set(farmerThread, farmerMap);

        return batch.commit();

    }
}
