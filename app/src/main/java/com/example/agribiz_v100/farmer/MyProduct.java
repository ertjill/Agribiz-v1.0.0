package com.example.agribiz_v100.farmer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.Verification;
import com.example.agribiz_v100.dialog.AddProductDialog;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.services.ProductManagement;
import com.example.agribiz_v100.services.StorageManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            ImageView product_image_iv = convertView.findViewById(R.id.product_image_iv);
            TextView product_name_unit = convertView.findViewById(R.id.product_name_unit);
            TextView product_stocks = convertView.findViewById(R.id.product_stocks);
            TextView product_sold = convertView.findViewById(R.id.product_sold);
            TextView product_category = convertView.findViewById(R.id.product_category);

            TextView product_price = convertView.findViewById(R.id.product_price);
            ImageButton delete_ib = convertView.findViewById(R.id.delete_ib);
            delete_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialogbox = new AlertDialog.Builder(getActivity());
                    dialogbox.setMessage("Do you really want to delete this products?");
                    dialogbox.setTitle("Delete Product");
                    dialogbox.setCancelable(false);
                    dialogbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
                            ProductManagement.deleteProduct(productList.get(position).getProductId())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                for (int i = 0; i < productList.get(position).getProductImage().size(); i++) {
                                                    int finalI = i;
                                                    StorageManagement.deleteProductImage(productList.get(position).getProductId() + "/" + i)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        if (productList.get(position) != null)
                                                                            if (finalI == productList.get(position).getProductImage().size() - 1) {
                                                                                productList.remove(position);
                                                                                notifyDataSetChanged();
                                                                                AuthValidation.successToast(getContext(), "Product successfully deleted").show();
                                                                            }
                                                                    } else {
                                                                        AuthValidation.failedToast(getContext(), "Failed To delete product").show();
                                                                    }
                                                                }
                                                            });

                                                }

                                            } else {
                                                AuthValidation.failedToast(getContext(), "Failed To delete product").show();
                                            }
                                        }
                                    });

                        }
                    });
                    dialogbox.show();
                }
            });


            Glide.with(context)
                    .load(productList.get(position) == null || productList.get(position).getProductImage() == null || productList.get(position).getProductImage().size() < 1 ? "" : productList.get(position).getProductImage().get(0))
                    .into(product_image_iv);
            product_name_unit.setText(productList.get(position).getProductName() + " (per " + productList.get(position).getProductQuantity() + " " + productList.get(position).getProductUnit() + ")");
            product_stocks.setText("Stocks: " + productList.get(position).getProductStocks());
            product_sold.setText("Sold: " + productList.get(position).getProductSold());
            product_category.setText("Category: " + productList.get(position).getProductCategory());
            product_price.setText("Price: Php " + productList.get(position).getProductPrice());
            return convertView;
        }
    }
}