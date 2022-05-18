package com.example.agribiz_v100.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.agribiz_v100.entities.ExpenseModel;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

public class FinanceManagement {
    public static Task<Void> addExpenses(Activity activity, ExpenseModel expenseModel) {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Uploading Image, please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef =db.collection("users").document(expenseModel.getExpenseUserId());
        DocumentReference expenseDocRef =db.collection("users").document(expenseModel.getExpenseUserId()).collection("expenses").document(expenseModel.getExpenseId());
        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                double totalExpenses = snapshot.getDouble("userExpenses")!=null?snapshot.getDouble("userExpenses")+expenseModel.getExpenseCost():expenseModel.getExpenseCost();
                transaction.update(docRef, "userExpenses",totalExpenses);
                transaction.set(expenseDocRef,expenseModel);
                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "Transaction success!");
                        progressDialog.dismiss();
                        AuthValidation.successToast(activity, "Expenses Added").show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Transaction failure.", e);
                        progressDialog.dismiss();
                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                        alert.setTitle("Failed to Add Expenses:");
                        alert.setMessage(e.getLocalizedMessage());
                        alert.setCancelable(false);
                        alert.setPositiveButton("Ok", null);
                        alert.show();
                    }
                });
    }

    public static Task<QuerySnapshot> getExpenses(DocumentSnapshot last) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (last != null)
            return db.collection("users").document(user.getUid()).collection("expenses")
                    .orderBy("expenseDateAdded", Query.Direction.ASCENDING)
                    .startAfter(last)
                    .get();
        else
            return db.collection("users").document(user.getUid()).collection("expenses")
                    .orderBy("expenseDateAdded", Query.Direction.ASCENDING)
                    .get();

    }

    public static Task<Void> deleteExpense(Activity activity, ExpenseModel expenseModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef =db.collection("users").document(expenseModel.getExpenseUserId());
        DocumentReference expenseDocRef =db.collection("users").document(expenseModel.getExpenseUserId()).collection("expenses").document(expenseModel.getExpenseId());
        return db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        double totalExpenses = snapshot.getDouble("userExpenses")!=null?snapshot.getDouble("userExpenses")-expenseModel.getExpenseCost():0;
                        transaction.update(docRef, "userExpenses",totalExpenses);
                        transaction.delete(expenseDocRef);
                        // Success
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AuthValidation.successToast(activity, "Expenses Removed").show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                        alert.setTitle("Failed to Remove Expenses:");
                        alert.setMessage(e.getLocalizedMessage());
                        alert.setCancelable(false);
                        alert.setPositiveButton("Ok", null);
                        alert.show();
                    }
                });
    }
    public static DocumentReference getTotalExpenses(String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users").document(userId);
    }
    public static DocumentReference getNetIncome(String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users").document(userId);
    }
    public static Task<DocumentSnapshot> getTotalRevenue(String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users")
                .document(userId).get();
    }
    public static Task<Object> updateExpense(Activity activity, ExpenseModel expenseModel,double previousExpense){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Updating Expense, please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users")
                .document(expenseModel.getExpenseUserId());
        DocumentReference expensesDocRef = db.collection("users")
                .document(expenseModel.getExpenseUserId()).collection("expenses").document(expenseModel.getExpenseId());
        return db.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userDocRef);
                transaction.update(expensesDocRef,"expenseName",expenseModel.getExpenseName(),
                        "expenseCost",expenseModel.getExpenseCost());
                double totalExpenses = (userSnapshot.getDouble("userExpenses")-previousExpense)+expenseModel.getExpenseCost();
                transaction.update(userDocRef,"userExpenses",totalExpenses);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                progressDialog.dismiss();
                AuthValidation.successToast(activity,"Updated Expense Successfully.").show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Failed to Update Expenses:");
                alert.setMessage(e.getLocalizedMessage());
                alert.setCancelable(false);
                alert.setPositiveButton("Ok", null);
                alert.show();
            }
        });

    }
}
