package com.example.agribiz_v100.farmer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.LoginActivity;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.dialog.AddProductDialog;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.services.AuthManagement;
import com.example.agribiz_v100.services.ProductManagement;
import com.example.agribiz_v100.services.StorageManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class MyProduct extends Fragment {
    String TAG = "MyProduct";
    ListView farmer_product_lv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SparseArray<ProductModel> productItems;
    FarmerProductAdapter farmerProductAdapter;
    LinearLayout no_product_ll;
    ImageButton add_product_ib;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentSnapshot last = null;
    AddProductDialog addProductDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_product, container, false);
        addProductDialog = new AddProductDialog(getActivity(), this);
        add_product_ib = view.findViewById(R.id.add_product_ib);

        productItems = new SparseArray<>();
        farmerProductAdapter = new FarmerProductAdapter(getContext(), productItems);

        addProductDialog.buildDialog();

        farmer_product_lv = view.findViewById(R.id.farmer_product_lv);
        farmer_product_lv.setAdapter(farmerProductAdapter);

        displayMyProducts();

        no_product_ll = view.findViewById(R.id.no_product_ll);
        add_product_ib.setOnClickListener(v -> {
            Log.d(TAG, "hELLO BTN");
            addProductDialog.showDialog();
        });
        addProductDialog.createListener(new AddProductDialog.AddProductDialogCallback() {
            @Override
            public void addOnDocumentAddedListener(boolean isAdded) {
                Log.d("tag", "hereee");
                displayMyProducts();
            }
        });
        return view;
    }

    int i = 0;

    public void displayMyProducts() {
        Log.d("tag", "hereee displayMyProducts");
        ProductManagement.getProducts(last, user.getUid())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("tag", last != null ? last.getData().toString() : "null");
                            Log.d("tag", "hereee out");
                            if (task.getResult().getDocuments().size() > 0) {
                                Log.d("tag", "hereee inside");
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    ProductModel item = document.toObject(ProductModel.class);
                                    productItems.append(i++, item);
                                    Log.d("tag", "hereee inside inside");
                                }
                                farmerProductAdapter.notifyDataSetChanged();
                                last = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
                            }

                        }
                    }
                });

    }

    public class FarmerProductAdapter extends BaseAdapter {

        Context context;
        SparseArray<ProductModel> productList;
        LayoutInflater layoutInflater;

        public FarmerProductAdapter(Context context, SparseArray<ProductModel> productList) {
            this.context = context;
            this.productList = productList;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return productList.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            }

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.farmer_product_list_card, null);
            }
            ImageButton delete_ib = convertView.findViewById(R.id.delete_ib);
            ImageView product_image_iv = convertView.findViewById(R.id.product_image_iv);
            TextView product_name_unit = convertView.findViewById(R.id.product_name_unit);
            TextView product_stocks = convertView.findViewById(R.id.product_stocks);
            TextView product_sold = convertView.findViewById(R.id.product_sold);
            TextView product_category = convertView.findViewById(R.id.product_category);

            TextView product_price = convertView.findViewById(R.id.product_price);


            Glide.with(context)
                    .load(productList.get(position).getProductImage().size() < 1 ? "" : productList.get(position).getProductImage().get(0))
                    .into(product_image_iv);
            product_name_unit.setText(new StringBuilder().append(productList.get(position).getProductName()).append(" (per ").append(productList.get(position).getProductQuantity()).append(" ").append(productList.get(position).getProductUnit()).append(")").toString());
            product_stocks.setText(new StringBuilder().append("Stocks: ").append(productList.get(position).getProductStocks()).toString());
            product_sold.setText(new StringBuilder().append("Sold: ").append(productList.get(position).getProductSold()).toString());
            product_category.setText(new StringBuilder().append("Category: ").append(productList.get(position).getProductCategory()).toString());
            product_price.setText(new StringBuilder().append("Price: Php ").append(productList.get(position).getProductPrice()).toString());

            delete_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Deleter Product");
                    alert.setMessage("Are you sure to delete dis product?");
                    alert.setNegativeButton("No", null);
                    alert.setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference productRef = db.collection("products").document(productList.get(position).getProductId());
                        String id = productList.get(position).getProductId();
                        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("products/" + id);
                        listRef.listAll()
                                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        for (StorageReference prefix : listResult.getPrefixes()) {
                                            // All the prefixes under listRef.
                                            // You may call listAll() recursively on them.
                                        }
                                        int i = 0;
                                        for (StorageReference item : listResult.getItems()) {
                                            // All the items under listRef.
                                            Log.d("path", item.getPath());
                                            StorageManagement.deleteFile(item.getPath());
                                            if (i == listResult.getItems().size() - 1) {
                                                ProductManagement.deleteProduct(id)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    productList.remove(position);
                                                                    notifyDataSetChanged();
                                                                }
                                                                else{
                                                                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                                                    alert.setTitle("Deleter Product:");
                                                                    alert.setMessage(task.getException().getMessage());
                                                                    alert.setPositiveButton("Yes",null);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Uh-oh, an error occurred!
                                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                        alert.setTitle("Deleter Product:");
                                        alert.setMessage(e.getMessage());
                                        alert.setPositiveButton("Yes",null);
                                    }
                                });
                    });
                    alert.show();


                }
            });
            return convertView;
        }
    }
}