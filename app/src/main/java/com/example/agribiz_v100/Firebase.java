package com.example.agribiz_v100;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.customer.CustomerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

import javax.annotation.Nullable;

public class Firebase implements Serializable {

    private static final String TAG = "Firebase";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    SparseArray<ProductItem> productItems;
    SparseArray<ProductItem> basketProductItems;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GetProductCallback productCallback;

    public void setProductCallback(GetProductCallback productCallback) {
        this.productCallback = productCallback;
    }

    public Firebase() {
        getProductItems();
    }

//    public SparseArray<ProductItem> getProductItems() {
//        return this.productItems;
//    }

    public void getProductItems() {
        synchronized (this) {
            db.collection("products")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                SparseArray<ProductItem> items = new SparseArray<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ProductItem item = new ProductItem(document);

                                    db.collection("users")
                                            .whereEqualTo("userId", document.getData().get("productFarmId"))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
//                                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                                        item.setProductFarmName(doc.getData().get("username").toString());
                                                        item.setProductFarmImage(doc.getData().get("userImage").toString());
                                                        items.append(items.size(), item);
                                                        Log.d(TAG, item.getProductFarmName() + ", " + item.getProductFarmImage() + " = " + items.size());

                                                        Log.d(TAG, items.size() + "");
                                                        productCallback.getProduct(items);
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }
    }

    public interface GetProductCallback {
        SparseArray<ProductItem> getProduct(SparseArray<ProductItem> items);
    }

}
