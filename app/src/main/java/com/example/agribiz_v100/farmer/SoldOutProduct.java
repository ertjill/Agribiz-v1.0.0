package com.example.agribiz_v100.farmer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.dialog.AddProductDialog;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.services.ProductManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SoldOutProduct extends Fragment {
    final static String TAG = "SoldOutProduct";
    ListView farmer_product_lv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<ProductModel> productItems;
    SoldOutProduct.FarmerProductAdapter farmerProductAdapter;
    LinearLayout no_product_ll;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentSnapshot last = null;
    AddProductDialog addProductDialog;
    ListenerRegistration registration;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_sold_out_product, container, false);
        View view = inflater.inflate(R.layout.fragment_sold_out_product, container, false);
        addProductDialog = new AddProductDialog(getActivity(), this);

        productItems = new ArrayList<>();
        farmerProductAdapter = new SoldOutProduct.FarmerProductAdapter(getContext());

        addProductDialog.buildDialog();

        farmer_product_lv = view.findViewById(R.id.farmer_product_lv);
        farmer_product_lv.setAdapter(farmerProductAdapter);

        no_product_ll = view.findViewById(R.id.no_product_ll);
        addProductDialog.createListener(new AddProductDialog.AddProductDialogCallback() {
            @Override
            public void addOnDocumentAddedListener(boolean isAdded) {
                Log.d("tag", "hereee");
                displayMyProducts();
            }
        });
        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
        registration.remove();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayMyProducts();
    }
    public void displayMyProducts() {
        Log.d("tag", "hereee displayMyProducts");
        registration = ProductManagement.getSoldOutProducts(user.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        productItems.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            productItems.add(doc.toObject(ProductModel.class));
                        }

                        farmerProductAdapter.notifyDataSetChanged();
                        last = value.getDocuments().get(value.size() - 1);

                    }
                });
    }

    public class FarmerProductAdapter extends BaseAdapter {

        Context context;
        LayoutInflater layoutInflater;

        public FarmerProductAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return productItems.size();
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
                    .load(productItems.get(position).getProductImage().size() < 1 ? "" : productItems.get(position).getProductImage().get(0))
                    .into(product_image_iv);
            product_name_unit.setText(new StringBuilder().append(productItems.get(position).getProductName()).append(" (per ").append(productItems.get(position).getProductQuantity()).append(" ").append(productItems.get(position).getProductUnit()).append(")").toString());
            product_stocks.setText(new StringBuilder().append("Stocks: ").append(productItems.get(position).getProductStocks()).toString());
            product_sold.setText(new StringBuilder().append("Sold: ").append(productItems.get(position).getProductSold()).toString());
            product_category.setText(new StringBuilder().append("Category: ").append(productItems.get(position).getProductCategory()).toString());
            product_price.setText(new StringBuilder().append("Price: Php ").append(productItems.get(position).getProductPrice()).toString());

            delete_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Deleter Product");
                    alert.setMessage("Are you sure to delete dis product?");
                    alert.setNegativeButton("No", null);
                    alert.setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference productRef = db.collection("products").document(productItems.get(position).getProductId());
                        String id = productItems.get(position).getProductId();
                        ProductManagement.deleteProduct(id)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            AuthValidation.successToast(getContext(),"Successfully deleted a product").show();
                                            productItems.remove(position);
                                            notifyDataSetChanged();
                                        } else {
                                            task.getException().getStackTrace();
                                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                            alert.setTitle("Delete Product:");
                                            alert.setMessage(task.getException().getMessage());
                                            alert.setPositiveButton("Yes", null);
                                            alert.show();
                                        }
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