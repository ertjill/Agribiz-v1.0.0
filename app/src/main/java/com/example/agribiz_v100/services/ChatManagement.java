package com.example.agribiz_v100.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.agribiz_v100.entities.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatManagement {

    private static final String TAG = "ChatManagement";

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

    public static void tagMessagesSeen(String logUserId,String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(logUserId)
                .collection("threads").document(userId)
                .collection("chats").whereEqualTo("chatSeen",false).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Get a new write batch
                        WriteBatch batch = db.batch();
                        for(QueryDocumentSnapshot ds:queryDocumentSnapshots){
//                            DocumentReference chatDocRef = db.collectionGroup("chats").document(ds.getId());
                            DocumentReference drUser1 = ds.getReference();
                            DocumentReference drUser2 =  db.collection("users").document(userId)
                                    .collection("threads").document(logUserId).collection("chats")
                                            .document(ds.getId());
                            batch.update(drUser1,"chatSeen",true);
                            batch.update(drUser2,"chatSeen",true);
//                            batch.update(chatDocRef,"chatSeen",true);
                        }
                        batch.commit();
                    }
                });
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

    public static Task<QuerySnapshot> getUnseenMessage(String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users").document(userId)
                .collection("threads").get();
    }
}
