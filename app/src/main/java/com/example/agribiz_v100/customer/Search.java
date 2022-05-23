package com.example.agribiz_v100.customer;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.services.AppManagement;
import com.example.agribiz_v100.services.ProductManagement;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class Search extends Fragment {

    EditText searchProduct_et;
    GridView viewAll_gv;
    ProductGridAdapter productGridAdapter;
    SparseArray<ProductModel> productItems;
    DocumentSnapshot last;
    ChipGroup chipGroup;
    int count = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchProduct_et=view.findViewById(R.id.searchProduct_et);
        viewAll_gv=view.findViewById(R.id.viewAll_gv);
        productItems = new SparseArray<>();
        productGridAdapter = new ProductGridAdapter(getContext(),productItems);
        viewAll_gv.setAdapter(productGridAdapter);
        chipGroup = view.findViewById(R.id.filter_category_cg);
        AppManagement.getSettings().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                for (Object category : (List<Object>) (documentSnapshot.get("productCategory"))) {
                    Chip chip = new Chip(getContext());
                    chip.setCheckable(true);
                    chip.setEnabled(true);
                    chip.setText(category.toString());
                    chipGroup.addView(chip);
                }

            }
        });
        searchProduct_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == 66){
                    last = null;
                    getSearchProduct();
                    searchProduct_et.clearFocus();
                }
                return false;
            }
        });
        return view;
    }
   public void getSearchProduct(){
       productItems.clear();
       productGridAdapter.notifyDataSetChanged();
       ProductManagement.searchProducts(last, searchProduct_et.getText().toString()).get()
               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot documentSnapshots) {
                       // ...
                       for (QueryDocumentSnapshot dc : documentSnapshots) {
                           ProductModel i = dc.toObject(ProductModel.class);
                           productItems.append(count++, i);
                       }

                       // Get the last visible document
                       if (documentSnapshots.getDocuments().size() > 0) {
                           last = documentSnapshots.getDocuments()
                                   .get((documentSnapshots.size() - 1));

                       } else {
                           Toast.makeText(getContext(), "No products to be displayed", Toast.LENGTH_SHORT).show();
                       }

                       productGridAdapter.notifyDataSetChanged();
                   }

               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                       alert.setTitle("Alert");
                       alert.setMessage(e.getMessage());
                       alert.setPositiveButton("Okay", null);
                       alert.show();
                   }
               });
   }
}