package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

public class BarterManagement {

    //add barter item
    public static Task<Void> addBarterItem(BarterModel bm, Activity activity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userDocRef = db.collection("users").document(bm.getBarterUserId());
        final DocumentReference bartersDocRef = db.collection("barters").document(bm.getBarterId());

        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userDocRef);

                int userBarteredCount = userSnapshot.get("userBarteredCount") != null ? Integer.parseInt(userSnapshot.get("userBarteredCount").toString())+1 : 1;

                if (userBarteredCount <= 3) {

                    transaction.update(userDocRef, "userBarteredCount", userBarteredCount);
                    transaction.set(bartersDocRef, bm);

                } else {
                    throw new FirebaseFirestoreException("Barter items exceeds limit, complete present barters first.", FirebaseFirestoreException.Code.ABORTED);
                }

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("BarterManagement", "Successfully added barter item");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("BarterManagement", "Failed to add barter item");
            }
        });
    }

//propose barter item
    public static Task<Void> proposeBarterItem(String farmerBarteredId,BarterModel bm, Activity activity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userDocRef = db.collection("users").document(bm.getBarterUserId());
        final DocumentReference bartersDocRef = db.collection("barters").document(bm.getBarterId());
        final DocumentReference farmerBarterDocRef = db.collection("barters").document(farmerBarteredId);

        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userDocRef);
                int userBarteredCount = userSnapshot.get("userBarteredCount") != null ? Integer.parseInt(userSnapshot.get("userBarteredCount").toString())+1 : 1;

                if (userBarteredCount <= 3) {
                    transaction.update(userDocRef, "userBarteredCount", userBarteredCount);
                    transaction.set(bartersDocRef, bm);
                    transaction.update(farmerBarterDocRef,"barterStatus","Pending","barterMatchId",bm.getBarterMatchId());

                } else {
                    throw new FirebaseFirestoreException("Barter items exceeds limit, complete present barters first.", FirebaseFirestoreException.Code.ABORTED);
                }

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("BarterManagement", "Successfully added barter item");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("BarterManagement", "Failed to add barter item");
            }
        });
    }
//get propose barter
    public static Query getProsedBarterItems(String field,String id,String type, String status){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("barters")
                .whereEqualTo(field,id)
                .whereEqualTo("barterType", type)
                .whereEqualTo("barterStatus", status)
                .orderBy("barterDateUploaded", Query.Direction.DESCENDING);
    }

    //get bartered item using id
    public static DocumentReference getBarteredItem(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("barters").document(id);
    }

    //get bartered item by type and status
    public static Query getBarteredItemsByType(String type,String status){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("barters")
                .whereEqualTo("barterType", type)
                .whereEqualTo("barterStatus", status)
                .orderBy("barterDateUploaded", Query.Direction.DESCENDING);
    }

    //get barters items of user by status
    public static Query getBarteredItems(String userId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("barters").whereEqualTo("barterUserId", userId)
                .whereEqualTo("barterStatus", status)
                .orderBy("barterDateUploaded", Query.Direction.DESCENDING);
    }

    //tag items to swap
    public static void swapBarteredItem(Context context, BarterModel bm,BarterModel customerBm){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Tagging as to swap, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference farmerBarterDocRef= db.collection("barters").document(bm.getBarterId());
        DocumentReference customerBarterDocRef= db.collection("barters").document(customerBm.getBarterId());
        db.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.update(farmerBarterDocRef,"barterStatus","Swapping");
                transaction.update(customerBarterDocRef,"barterStatus","Swapping");
                return null;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Error");
                alert.setMessage(e.getLocalizedMessage());
                alert.setPositiveButton("Ok", null);
                alert.show();

            }
        }).addOnSuccessListener(new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                AuthValidation.successToast(context,"Successfully tag as to swap.").show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                progressDialog.dismiss();
            }
        });
    }
//tag as done swapping or swap swapped
    public static void swappedBarteredItem(Context context, BarterModel bm,BarterModel customerBm){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Tagging as to swap, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference farmerBarterDocRef= db.collection("barters").document(bm.getBarterId());
        DocumentReference customerBarterDocRef= db.collection("barters").document(customerBm.getBarterId());
        DocumentReference farmerDocRef= db.collection("users").document(bm.getBarterUserId());
        DocumentReference customerDocRef= db.collection("users").document(customerBm.getBarterUserId());
        db.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                DocumentSnapshot farmerDocSnapshot = transaction.get(farmerDocRef);
                long farmerBarterCount=farmerDocSnapshot.getLong("userBarteredCount")-1;
                DocumentSnapshot customerDocSnapshot = transaction.get(customerDocRef);
                long customerBarterCount =customerDocSnapshot.getLong("userBarteredCount")-1;

                transaction.update(farmerBarterDocRef,"barterStatus","Completed");
                transaction.update(customerBarterDocRef,"barterStatus","Completed");
                transaction.update(farmerDocRef,"userBarteredCount",farmerBarterCount);
                transaction.update(customerDocRef,"userBarteredCount",customerBarterCount);

                return null;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Error");
                alert.setMessage(e.getLocalizedMessage());
                alert.setPositiveButton("Ok", null);
                alert.show();

            }
        }).addOnSuccessListener(new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                AuthValidation.successToast(context,"Successfully tag as swapped.").show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                progressDialog.dismiss();
            }
        });
    }

//REMOVE BARTERED ITEM
    public static Task<Void> removeBarteredItem(BarterModel bm){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference barterDocRef= db.collection("barters").document(bm.getBarterId());
        DocumentReference userDocRef= db.collection("users").document(bm.getBarterUserId());
        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userDocRef);
                long count = userSnapshot.getLong("userBarteredCount")-1;
                transaction.update(userDocRef,"userBarteredCount",count);
                transaction.delete(barterDocRef);
                return null;
            }
        });
    }


        //CANCEL THE PROPOSAL ON FARMERS END
    public static Task<Void> farmerCancelProposedBarter(Context context, BarterModel bm,BarterModel customerBm){

        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Cancelling barter request, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        DocumentReference customerBarterDocRef= db.collection("barters").document(customerBm.getBarterId());
        DocumentReference farmerBarterDocRef= db.collection("barters").document(bm.getBarterId());
        DocumentReference customerDocRef= db.collection("users").document(customerBm.getBarterUserId());

        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot customerSnapshot = transaction.get(customerDocRef);
                long count = customerSnapshot.getLong("userBarteredCount")-1;

                DocumentSnapshot farmerBasketSnapshot = transaction.get(farmerBarterDocRef);
                String farmerId = farmerBasketSnapshot.getString("barterUserId");
                DocumentReference farmerDocRef= db.collection("users").document(farmerId);
                DocumentSnapshot farmerUserSnapshot = transaction.get(farmerDocRef);
                transaction.update(farmerBarterDocRef,"barterStatus","Open","barterMatchId","");
                transaction.update(customerDocRef,"userBarteredCount",count);
                transaction.delete(customerBarterDocRef);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                AuthValidation.successToast(context,"Barter request cancelled successfully").show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Error");
                alert.setMessage(e.getLocalizedMessage());
                alert.setPositiveButton("Ok", null);
                alert.show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }

    public static Task<QuerySnapshot> getTwoItemsBartered(String matchId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("barters").whereEqualTo("barterMatchId",matchId).get();
    }

    public static Task<Void> cancelProposedBarter(Context context, BarterModel bm){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Cancelling barter request, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        DocumentReference customerBarterDocRef= db.collection("barters").document(bm.getBarterId());
        DocumentReference farmerBarterDocRef= db.collection("barters").document(bm.getBarterMatchId());
        DocumentReference customerDocRef= db.collection("users").document(bm.getBarterUserId());

        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot customerSnapshot = transaction.get(customerDocRef);
                long count = customerSnapshot.getLong("userBarteredCount")-1;



                DocumentSnapshot farmerBasketSnapshot = transaction.get(farmerBarterDocRef);
                String farmerId = farmerBasketSnapshot.getString("barterUserId");
                DocumentReference farmerDocRef= db.collection("users").document(farmerId);
                DocumentSnapshot farmerUserSnapshot = transaction.get(farmerDocRef);
                transaction.update(farmerBarterDocRef,"barterStatus","Open","barterMatchId","");
                transaction.update(customerDocRef,"userBarteredCount",count);
                transaction.delete(customerBarterDocRef);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                AuthValidation.successToast(context,"Barter request cancelled successfully").show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Error");
                alert.setMessage(e.getLocalizedMessage());
                alert.setPositiveButton("Ok", null);
                alert.show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }



}
