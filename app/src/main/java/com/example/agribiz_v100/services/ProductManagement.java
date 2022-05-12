package com.example.agribiz_v100.services;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.entities.ProductModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ProductManagement {

    ManageProductCallback manageProductCallback;
    public static Task<Void> addProduct(ProductModel product) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("products").document(product.getProductId()).set(product);
    }

    public static Task<QuerySnapshot> getProducts(DocumentSnapshot last, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (last == null)
            return db.collection("products")
                    .whereEqualTo("productUserId", userId)
                    .orderBy("productDateUploaded", Query.Direction.ASCENDING)
                    .get();
        else
            return db.collection("products")
                    .whereEqualTo("productUserId", userId)
                    .orderBy("productDateUploaded", Query.Direction.ASCENDING)
                    .startAfter(last)
                    .get();
    }

    public static Task<Void> deleteProduct(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("products").document(id)
                .delete();
    }

    public static void uploadProductImage(List<String> images, String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        List<String> imageUrls = new ArrayList<>();
        int i=0;
        while(i < images.size()){
                Uri uri = Uri.parse(images.get(i));
                StorageReference ref = storageRef.child("products/"+userId+"/"+uri.getLastPathSegment());
                UploadTask uploadTask = ref.putFile(uri);
            int finalI = i;
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            imageUrls.add(downloadUri.toString());
                            if(finalI >=images.size()-1) {
//                                manageProductCallback.addOnSuccessDownloadListener(imageUrls);
                                Log.d("Uploaded", " pic number " + images.size());
                            }
                        } else {
                            // Handle failures
                            Log.d("Uploaded", task.getException().getMessage());
                        }
                    }
                });
            i++;
        }
    }

    public Task<Void> updateProduct(FirebaseUser user, ProductModel product) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        product.setProductImage(uploadProductImage(product.getProductImage(), product.getProductUserId()));
        DocumentReference productsRef = db.collection("products").document(user.getUid());
        return productsRef.set(product);
    }

    public static Query getProducts(DocumentSnapshot last){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (last == null) {
            return db.collection("products").orderBy("productDateUploaded", Query.Direction.DESCENDING).limit(8);

        } else {
            return db.collection("products").orderBy("productDateUploaded", Query.Direction.DESCENDING).startAfter(last).limit(8);
        }

    }
    public static Query searchProducts(DocumentSnapshot last, String search){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        return db.collection("products").orderBy("productName")
//                .startAt(search)
//                .endAt(search + "\uf8ff").limit(30);
        if (last == null) {
//            return db.collection("products").whereIn("productName", array).orderBy("productDateUploaded", Query.Direction.DESCENDING).limit(8);
//            return db.collection("products").whereGreaterThanOrEqualTo("productName", array).orderBy("productDateUploaded", Query.Direction.DESCENDING).limit(8);
            return db.collection("products").orderBy("productName")
                    .startAt(search)
                    .endAt(search + "\uf8ff").limit(8);

        } else {
//            return db.collection("products").whereIn("productName", array).orderBy("productDateUploaded", Query.Direction.DESCENDING).startAfter(last).limit(8);
//            return db.collection("products").whereGreaterThanOrEqualTo("productName", array).orderBy("productDateUploaded", Query.Direction.DESCENDING).startAfter(last).limit(8);
            return db.collection("products").orderBy("productName")
                    .startAfter(last)
                    .endAt(search + "\uf8ff").limit(8);
        }

    }

    public static Query getTopSellingProduct(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("products").orderBy("productSold", Query.Direction.DESCENDING).limit(6);
    }

    public static void getProductSales() {

    }

    public interface ManageProductCallback{
        void addOnSuccessDownloadListener(List<String> imagesUrl);
    }

}
