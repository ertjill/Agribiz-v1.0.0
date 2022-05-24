package com.example.agribiz_v100.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.services.AppManagement;
import com.example.agribiz_v100.services.ProductManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Search extends Fragment {

    EditText searchProduct_et;
    GridView viewAll_gv;
    ProductGridAdapter productGridAdapter;
    SparseArray<ProductModel> productItems;
    DocumentSnapshot last;
    ChipGroup chipGroup;
    Chip all_chip, fruits_chip, vegetables_chip, livestocks_chips, poultry_chips, animal_chip, fertilizers_chip;
    int count = 0;
    String catergory = "all";
    TextView no_product_tv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchProduct_et = view.findViewById(R.id.searchProduct_et);
        viewAll_gv = view.findViewById(R.id.viewAll_gv);
        productItems = new SparseArray<>();
        productGridAdapter = new ProductGridAdapter(getContext(), productItems);
        viewAll_gv.setAdapter(productGridAdapter);
        chipGroup = view.findViewById(R.id.filter_category_cg);
        all_chip = view.findViewById(R.id.all_chip);
        no_product_tv = view.findViewById(R.id.no_product_tv);
        viewAll_gv.setEmptyView(no_product_tv);
        all_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    catergory="all";
                    fruits_chip.setChecked(false);
                    vegetables_chip.setChecked(false);
                    livestocks_chips.setChecked(false);
                    poultry_chips.setChecked(false);
                    animal_chip.setChecked(false);
                    fertilizers_chip.setChecked(false);
                    getSearchProduct();
                }
            }
        });
        fruits_chip = view.findViewById(R.id.fruits_chip);
        fruits_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    catergory="Fruit";
                    all_chip.setChecked(false);
                    vegetables_chip.setChecked(false);
                    livestocks_chips.setChecked(false);
                    poultry_chips.setChecked(false);
                    animal_chip.setChecked(false);
                    fertilizers_chip.setChecked(false);
                    getSearchProduct();
                }
            }
        });
        vegetables_chip = view.findViewById(R.id.vegetables_chip);
        vegetables_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    catergory="Vegetable";
                    all_chip.setChecked(false);
                    fruits_chip.setChecked(false);
                    livestocks_chips.setChecked(false);
                    poultry_chips.setChecked(false);
                    animal_chip.setChecked(false);
                    fertilizers_chip.setChecked(false);
                    getSearchProduct();
                }
            }
        });
        livestocks_chips = view.findViewById(R.id.livestocks_chips);
        livestocks_chips.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    catergory="Livestock";
                    all_chip.setChecked(false);
                    fruits_chip.setChecked(false);
                    vegetables_chip.setChecked(false);
                    poultry_chips.setChecked(false);
                    animal_chip.setChecked(false);
                    fertilizers_chip.setChecked(false);
                    getSearchProduct();
                }
            }
        });
        poultry_chips = view.findViewById(R.id.poultry_chips);
        poultry_chips.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    catergory="Poultry";
                    all_chip.setChecked(false);
                    fruits_chip.setChecked(false);
                    vegetables_chip.setChecked(false);
                    livestocks_chips.setChecked(false);
                    animal_chip.setChecked(false);
                    fertilizers_chip.setChecked(false);
                    getSearchProduct();
                }
            }
        });
        animal_chip = view.findViewById(R.id.animal_chip);
        animal_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(b){
                   catergory="Animal Feed";
                   all_chip.setChecked(false);
                   fruits_chip.setChecked(false);
                   vegetables_chip.setChecked(false);
                   livestocks_chips.setChecked(false);
                   poultry_chips.setChecked(false);
                   fertilizers_chip.setChecked(false);
                   getSearchProduct();
               }
            }
        });
        fertilizers_chip = view.findViewById(R.id.fertilizers_chip);
        fertilizers_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    catergory="Fertilizer";
                    all_chip.setChecked(false);
                    fruits_chip.setChecked(false);
                    vegetables_chip.setChecked(false);
                    livestocks_chips.setChecked(false);
                    poultry_chips.setChecked(false);
                    animal_chip.setChecked(false);
                    getSearchProduct();
                }
            }
        });
        searchProduct_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == 66) {
                    getSearchProduct();
                    searchProduct_et.clearFocus();
                }
                return false;
            }
        });
        return view;
    }

    public void getSearchProduct() {
        productItems.clear();
        productGridAdapter.notifyDataSetChanged();
        count = 0;
        if(!searchProduct_et.getText().toString().isEmpty())
        {
            ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setMessage("Searching..., Please wait");
            dialog.setCancelable(false);
            dialog.show();

            ProductManagement.searchProductWithCategory(catergory).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot ds : queryDocumentSnapshots) {
                        String productname = ds.getString("productName").toLowerCase(Locale.ROOT);
                        String arrayOfWordsSearch[] = searchProduct_et.getText().toString().split(" ");
                        for (String word : arrayOfWordsSearch) {
                            if (productname.contains(word.toLowerCase(Locale.ROOT))) {
                                productItems.append(count++, ds.toObject(ProductModel.class));
                                productGridAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Error");
                    alert.setMessage(e.getLocalizedMessage());
                    alert.setCancelable(false);
                    alert.setPositiveButton("Ok", null);
                }
            });
        }
        else{
            Toast.makeText(getContext(), "Type to search", Toast.LENGTH_SHORT).show();
        }
//       ProductManagement.searchProducts(last, searchProduct_et.getText().toString()).get()
//               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                   @Override
//                   public void onSuccess(QuerySnapshot documentSnapshots) {
//                       // ...
//                       for (QueryDocumentSnapshot dc : documentSnapshots) {
//                           ProductModel i = dc.toObject(ProductModel.class);
//                           productItems.append(count++, i);
//                       }
//
//                       // Get the last visible document
//                       if (documentSnapshots.getDocuments().size() > 0) {
//                           last = documentSnapshots.getDocuments()
//                                   .get((documentSnapshots.size() - 1));
//
//                       } else {
//                           Toast.makeText(getContext(), "No products to be displayed", Toast.LENGTH_SHORT).show();
//                       }
//
//                       productGridAdapter.notifyDataSetChanged();
//                   }
//
//               }).addOnFailureListener(new OnFailureListener() {
//                   @Override
//                   public void onFailure(@NonNull Exception e) {
//                       AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//                       alert.setTitle("Alert");
//                       alert.setMessage(e.getMessage());
//                       alert.setPositiveButton("Okay", null);
//                       alert.show();
//                   }
//               });
    }
}