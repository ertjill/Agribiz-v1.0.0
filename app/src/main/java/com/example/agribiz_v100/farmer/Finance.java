package com.example.agribiz_v100.farmer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.dialog.AddExpensesDialog;
import com.example.agribiz_v100.entities.ExpenseModel;
import com.example.agribiz_v100.services.FinanceManagement;
import com.example.agribiz_v100.validation.ExpenseValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Finance extends Fragment {

    Button addExpense_btn;
    AddExpensesDialog addExpensesDialog;
    ListView expences_lv;
    ExpensesAdapter expensesAdapter;
    List<ExpenseModel> expensesList;
    DocumentSnapshot last;
    TextView expenses_amount_tv,totalRevenue_amount_tv,netIncome_amount_tv;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finance, container, false);

        addExpense_btn = view.findViewById(R.id.addExpense_btn);
        expences_lv = view.findViewById(R.id.expences_lv);
        expenses_amount_tv = view.findViewById(R.id.expenses_amount_tv);
        totalRevenue_amount_tv = view.findViewById(R.id.totalRevenue_amount_tv);
        netIncome_amount_tv =view.findViewById(R.id.netIncome_amount_tv);
        addExpensesDialog = new AddExpensesDialog(getActivity(), this);
        addExpensesDialog.buildDialog();
        expensesList = new ArrayList<>();
        expensesAdapter = new ExpensesAdapter(expensesList);
        addExpense_btn.setOnClickListener(v -> {
            addExpensesDialog.showDialog(new AddExpensesDialog.AddExpenseAddedCallback() {
                @Override
                public void setExpenseAddedListener(ExpenseModel expenseModel) {
                    displayExpenses();
                }
            });
        });
        expences_lv.setAdapter(expensesAdapter);
        displayExpenses();
        getTotalRevenue();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public Task<DocumentSnapshot> getTotalExpenses(){
        return FinanceManagement.getTotalExpenses(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        double totalExpenses= documentSnapshot.getDouble("userExpenses")!=null?documentSnapshot.getDouble("userExpenses"):0;
                        expenses_amount_tv.setText("₱ "+String.format("%.2f",totalExpenses));
                    }
                });
    }

    public void getTotalRevenue(){
        FinanceManagement.getTotalRevenue(user.getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        double totalRevenue = documentSnapshot.getDouble("userRevenue")!=null?documentSnapshot.getDouble("userRevenue"):0;
                        totalRevenue_amount_tv.setText("₱ "+String.format("%.2f",totalRevenue));
                    }
                });
    }
    public Task<DocumentSnapshot> getNetIncome(){
        return FinanceManagement.getNetIncome(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        double netIncome = documentSnapshot.getDouble("userRevenue")!=null&& documentSnapshot.getDouble("userExpenses")!=null?documentSnapshot.getDouble("userRevenue")-documentSnapshot.getDouble("userExpenses"):0;
                        netIncome_amount_tv.setText("₱ "+String.format("%.2f",netIncome));
                    }
                });
    }

    public void displayExpenses() {
        FinanceManagement.getExpenses(last)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("here","inside onSuccess");

                            for (DocumentSnapshot doc : task.getResult()) {
                                Log.d("here","inside for");
                                expensesList.add(doc.toObject(ExpenseModel.class));
                            }
                            if (!task.getResult().getDocuments().isEmpty()) {
                                last = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
                                Log.d("here", task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1).get("expenseName").toString());
                            }
                            expensesAdapter.notifyDataSetChanged();
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setTitle("Failed to load expenses");
                            alert.setMessage(task.getException().getLocalizedMessage());
                            alert.setCancelable(false);
                            alert.setPositiveButton("Okay", null);
                            alert.show();
                        }
                    }
                });
    }

    public class ExpensesAdapter extends BaseAdapter {
        List<ExpenseModel> list;

        public ExpensesAdapter() {
        }

        public ExpensesAdapter(List<ExpenseModel> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            getTotalExpenses();
            getNetIncome();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_list_item_layout, null);
            TextView expenses_name_tv, expenses_amount_tv;
            expenses_name_tv = convertView.findViewById(R.id.expenses_name_tv);
            expenses_name_tv.setText(list.get(position).getExpenseName());
            expenses_amount_tv = convertView.findViewById(R.id.expenses_amount_tv);
            expenses_amount_tv.setText(String.format("%.2f", list.get(position).getExpenseCost()));

            ImageButton remove_ib = convertView.findViewById(R.id.remove_ib);
            ImageButton update_ib = convertView.findViewById(R.id.update_ib);
            update_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.farmer_add_expenses_dialog);
                    dialog.setCancelable(false);
                    TextView top_tv =dialog.findViewById(R.id.top_tv);
                    top_tv.setText("Update Expense");
                    TextInputLayout expenseName_til,totalCost_til;
                    expenseName_til = dialog.findViewById(R.id.expenseName_til);
                    expenseName_til.getEditText().setText(expensesList.get(position).getExpenseName());
                    totalCost_til = dialog.findViewById(R.id.totalCost_til);
                    totalCost_til.getEditText().setText(String.format("%.2f",expensesList.get(position).getExpenseCost()));
                    Button cancel_btn,addExpense_btn;
                    cancel_btn = dialog.findViewById(R.id.cancel_btn);
                    addExpense_btn = dialog.findViewById(R.id.addExpense_btn);
                    cancel_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    addExpense_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            expenseName_til.setError("");
                            totalCost_til.setError("");

                            String name = expenseName_til.getEditText().getText().toString();
                            String cost = totalCost_til.getEditText().getText().toString();
                            if(!ExpenseValidation.validateExpenseName(name).isEmpty()){
                                expenseName_til.setError(ExpenseValidation.validateExpenseName(name));
                            }
                            else if(!ExpenseValidation.validateExpenseCost(cost).isEmpty()){
                                totalCost_til.setError(ExpenseValidation.validateExpenseCost(cost));
                            }
                            else{
                                double previousExpense=expensesList.get(position).getExpenseCost();
                                expensesList.get(position).setExpenseName(name);
                                expensesList.get(position).setExpenseCost(Double.parseDouble(cost));
                                FinanceManagement.updateExpense(getActivity(),expensesList.get(position),previousExpense)
                                        .addOnSuccessListener(new OnSuccessListener<Object>() {
                                            @Override
                                            public void onSuccess(Object unused) {
                                                dialog.dismiss();
                                                notifyDataSetChanged();
                                            }
                                        });
                            }
                        }
                    });
                    dialog.show();
                }
            });
            remove_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getActivity(), "Removing", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Remove Expense");
                    alert.setMessage("Are you sure you want to remove this expense?");
                    alert.setCancelable(false);
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressDialog progressDialog;
                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Removing expense, please wait...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            FinanceManagement.deleteExpense(getActivity(),list.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    getTotalExpenses().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            getNetIncome().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    progressDialog.dismiss();
                                                    list.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            });

                                        }
                                    });


                                }
                            });
                        }
                    });
                    alert.setNegativeButton("No",null);
                    alert.show();

                }
            });

            return convertView;
        }
    }
}