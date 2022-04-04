package com.example.agribiz_v100.services;

import com.example.agribiz_v100.entities.ProductModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductManagement {

    public static void addProduct(String userId,ProductModel product){
        
    }

    public static void getProducts(){

    }

    public static Task<Void> updateProduct(FirebaseUser user, ProductModel product){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productsRef = db.collection("products").document(user.getUid());
        return productsRef.set(product);
    }

    public static void deleteProduct(ProductModel product){

    }

    public static void getProductSales(){

    }
}
