package com.example.agribiz_v100;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.agribiz_v100.customer.BasketHeader;
import com.example.agribiz_v100.customer.BasketProductItem;
import com.example.agribiz_v100.customer.ProductGridAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper implements Parcelable {
    static String TAG = "FirebaseHelper";
    Source source = Source.CACHE;
    FirebaseHelperCallback firebaseHelperCallback;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SparseArray<Object> basketProductItems;
    private SparseArray<ProductItem> products;
    private SparseArray<ProductItem> topProducts;
    public SparseArray<ProductItem> topProductsItems;

    public static void createAccount(Map newUser) {
        Log.d(TAG,"I'm here ate firebasehelper");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(newUser.get("userId").toString())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public SparseArray<ProductItem> getTopProductsItems() {
        firebaseHelperCallback.getToProducst(topProductsItems);
        return topProductsItems;
    }

    public void getTopProducts() {
        db.collection("products").limit(6)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final int[] i = {0};
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int size = task.getResult().size();
                                Log.d(TAG, "TASK SIZE: " + size);
                                ProductItem item = new ProductItem(document);
                                db.collection("users").document(document.getData().get("productFarmId").toString())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            if (doc.exists()) {
                                                item.setProductFarmImage(doc.getData().get("userImage").toString());
                                                item.setProductFarmName(doc.getData().get("username").toString());
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                            topProductsItems.append(i[0]++, item);
                                            Log.d(TAG, topProductsItems.size() + " : topProductsItems size");
//                                            firebaseHelperCallback.getToProducst(topProductsItems);
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
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

//    public synchronized boolean getProductItems() {
//        firebaseHelperCallback.getProducts(products, topProducts);
//        return true;
//    }

    public static final Creator<FirebaseHelper> CREATOR = new Creator<FirebaseHelper>() {
        @Override
        public FirebaseHelper createFromParcel(Parcel in) {
            return new FirebaseHelper(in);
        }

        @Override
        public FirebaseHelper[] newArray(int size) {
            return new FirebaseHelper[size];
        }
    };

    public void getBasketProductItems() {
        firebaseHelperCallback.getProductFromBasket(basketProductItems);
    }

    public void setBasketProductItems(SparseArray<Object> basketProductItems) {
        this.basketProductItems = basketProductItems;
    }

    public void addProductObject(int key, Object o) {
        this.basketProductItems.append(key, o);
    }

    public void addProductItem(int key, ProductItem o) {
        this.products.append(key, o);
    }

    public FirebaseHelper() {
        this.basketProductItems = new SparseArray<>();
    }

    public FirebaseHelper(FirebaseHelperCallback firebaseHelperCallback) {
        this.firebaseHelperCallback = firebaseHelperCallback;
        this.basketProductItems = new SparseArray<>();
//        this.products = new SparseArray<>();
//        this.topProducts = new SparseArray<>();
        this.topProductsItems = new SparseArray<>();

    }


    public void setProducts(SparseArray<ProductItem> products) {
        this.products = products;
    }

    protected FirebaseHelper(Parcel in) {

    }

//    public void getProducts() {
//        db.collection("products")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            final int[] i = {0};
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                //Log.d(TAG, document.getId() + " => " + document.getData());
//                                ProductItem item = new ProductItem(document);
//
//                                db.collection("users").document(document.getData().get("productFarmId").toString())
//                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            DocumentSnapshot doc = task.getResult();
//                                            if (doc.exists()) {
//                                                //Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
//                                                item.setProductFarmImage(doc.getData().get("userImage").toString());
//                                                item.setProductFarmName(doc.getData().get("username").toString());
//                                            } else {
//                                                Log.d(TAG, "No such document");
//                                            }
//                                            addProductItem(i[0], item);
//                                            if (i[0] < 6)
//                                                topProducts.append(i[0], item);
//                                            i[0]++;
////                                            firebaseHelperCallback.getProducts(products, topProducts);
//                                        } else {
//                                            Log.d(TAG, "get failed with ", task.getException());
//                                        }
//                                    }
//                                });
//                            }
//
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//        Log.d(TAG, "1");
//    }


    public void getBasketProduct(FirebaseUser user) {
        db.collection("users").document(user.getUid()).collection("basket")
                .orderBy("productFarmId", Query.Direction.ASCENDING)
                .orderBy("productDateAdded", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final int[] i = {0};
                            final String[] tempId = {""};
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BasketProductItem item = new BasketProductItem(document);
                                BasketHeader basketHeader = new BasketHeader();
                                //Log.d(TAG, document.getId() + " => " + document.getData().get("productFarmId"));
                                db.collection("users")
                                        .document(document.getData().get("productFarmId").toString())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            if (doc.exists()) {
                                                basketHeader.setFarmId(doc.getId());
                                                basketHeader.setFarmName((doc.getData().get("username")).toString());
                                                if (tempId[0].equals("") || !tempId[0].equals(doc.getId())) {
                                                    addProductObject(i[0]++, basketHeader);
                                                    tempId[0] = doc.getId();
                                                }
                                                addProductObject(i[0]++, item);
                                                Log.d(TAG, "DocumentSnapshot data: " + basketProductItems.size());
                                                firebaseHelperCallback.getProductFromBasket(basketProductItems);
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Log.e(TAG, task.getException().getLocalizedMessage());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }


    public interface FirebaseHelperCallback {
        void isProductAddedToBasket(boolean added);

        void getProductFromBasket(SparseArray<Object> productFromBasket);

        void getProducts(SparseArray<ProductItem> products, SparseArray<ProductItem> topProducst);

        void getToProducst(SparseArray<ProductItem> topProducts);
    }
}
