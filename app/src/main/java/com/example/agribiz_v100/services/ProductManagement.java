package com.example.agribiz_v100.services;

import android.net.Uri;

import com.example.agribiz_v100.entities.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ProductManagement {

    public static Task<Void> addProduct(ProductModel product){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("products").document(product.getProductId()).set(product);
    }

    public static Task<QuerySnapshot> getProducts(DocumentSnapshot last, String userId ){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(last==null)
            return db.collection("products")
                .whereEqualTo("productUserId", userId)
                .orderBy("productDateUploaded", Query.Direction.DESCENDING)
                .get();
        else
            return db.collection("products")
                    .whereEqualTo("productUserId", userId)
                    .orderBy("productDateUploaded", Query.Direction.DESCENDING)
                    .startAfter(last)
                    .get();
    }

    public static List<String> uploadProductImage(List<String> images,String userId){
        List<String> imageUrls = new ArrayList<>();
        for (String image:images) {
            Uri uri = Uri.parse(image);
            StorageManagement.uploadImage("products",uri,userId+"/"+uri.getLastPathSegment()+"sk")
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageUrls.add(taskSnapshot.getStorage().getDownloadUrl().getResult().toString());
                        }
                    });
        }
        return imageUrls;
    }

    public static Task<Void> updateProduct(FirebaseUser user, ProductModel product){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        product.setProductImage(uploadProductImage(product.getProductImage(), product.getProductUserId()));
        DocumentReference productsRef = db.collection("products").document(user.getUid());
        return productsRef.set(product);
    }

    public static void deleteProduct(ProductModel product){

    }

    public static void getProductSales(){

    }
}
