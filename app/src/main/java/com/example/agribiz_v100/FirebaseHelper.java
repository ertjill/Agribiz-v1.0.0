package com.example.agribiz_v100;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    FirebaseHelperCallback firebaseHelperCallback;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseHelper() {

    }

    public FirebaseHelper(FirebaseHelperCallback firebaseHelperCallback) {
        this.firebaseHelperCallback = firebaseHelperCallback;
    }

    static String TAG = "FirebaseHelper";

    public void getBasketProduct(FirebaseUser user) {
        db.collection("basket").document(user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //document.getId();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getId());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    public static boolean addProduct(Context C, ArrayList<Object> a, byte[] data) {
        final boolean[] addProductReq = {true};
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Date date = new Date();
        StorageReference imagesRef = storageRef.child("products/" + user.getUid() + date.toString());
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String image = uri.toString();
                        //addProduct(a, image);
                        Map<String, Object> product = new HashMap<>();
                        product.put("productName", a.get(0));
                        product.put("productDescription", a.get(1));
                        product.put("productCategory", a.get(2));
                        product.put("productStocks", a.get(3));
                        product.put("productPrice", a.get(4));
                        product.put("productQuantity", a.get(5));
                        product.put("productUnit", a.get(6));
                        product.put("productImage", image);
                        product.put("productFarmId", a.get(7));

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("products")
                                .add(product)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                        addProductReq[0] = true;
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        addProductReq[0] = false;
                                    }
                                });
                    }
                });
            }
        });
        return addProductReq[0];
    }

    public void addProductToBasket(Map<String, Object> prod, String productId, String UserId, String hubId) {
        synchronized (this) {
            db.collection("users").document(UserId)
                    .collection("basket").document(productId)
                    .set(prod)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            firebaseHelperCallback.isProductAddedToBasket(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            firebaseHelperCallback.isProductAddedToBasket(false);
                        }
                    });
        }
        synchronized (this) {
        }
    }


    public interface FirebaseHelperCallback {
        void isProductAddedToBasket(boolean added);
    }
}
