package com.example.agribiz_v100.farmer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.Verification;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class MyProduct extends Fragment {
    String TAG = "MyProduct";
    ListView farmer_product_lv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<FarmerProductItem> productItems;
    FarmerProductAdapter farmerProductAdapter;
    LinearLayout no_product_ll;
    ImageButton add_product_ib;
    Dialog addProductDialog;
    Dialog addProductPhotoDialog;
    ImageView product_image_iv;
    ActivityResultLauncher<Intent> selectFromGallery, selectFromCamera;
    ImageView real_product_image;
    FirebaseUser user;
    Toast successAddProductToast;
    @Override
    public void onStart() {
        super.onStart();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       successAddProductToast = new Toast(getContext());
        LayoutInflater inflater1=getLayoutInflater();
        View successToast = inflater1.inflate(R.layout.success_toast,null);
        successAddProductToast.setView(successToast);
        successAddProductToast.setDuration(Toast.LENGTH_LONG);
        successAddProductToast.setGravity(Gravity.CENTER,0,0);
        Toast.makeText(getActivity(), "Successfully added produce", Toast.LENGTH_SHORT).show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_product, container, false);
        addProductDialog = new Dialog(getContext());
        addProductPhotoDialog = new Dialog(getContext());
        farmer_product_lv = view.findViewById(R.id.farmer_product_lv);
        no_product_ll = view.findViewById(R.id.no_product_ll);
        add_product_ib = view.findViewById(R.id.add_product_ib);
        productItems = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        selectFromGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    product_image_iv.setImageURI(result.getData().getData());
                }
            }
        });

        selectFromCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    product_image_iv.setImageBitmap(bitmap);
                }
            }
        });

        db.collection("products")
                .whereEqualTo("productFarmId", user.getUid().toString())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                productItems.add(new FarmerProductItem(dc.getDocument().getId(),
                                        dc.getDocument().getData().get("productFarmId").toString(),
                                        dc.getDocument().getData().get("productName").toString(),
                                        Double.parseDouble(dc.getDocument().getData().get("productPrice").toString()),
                                        dc.getDocument().getData().get("productUnit").toString(),
                                        dc.getDocument().getData().get("productImage").toString(),
                                        Integer.parseInt(dc.getDocument().getData().get("productStocks").toString()),
                                        dc.getDocument().getData().get("productDescription").toString(),
                                        dc.getDocument().getData().get("productCategory").toString(),
                                        Integer.parseInt(dc.getDocument().getData().get("productQuantity").toString())));
                            }
                        }
                        if (productItems.isEmpty()) {
                            no_product_ll.setVisibility(View.VISIBLE);
                            farmer_product_lv.setVisibility(View.GONE);
                        } else {
                            no_product_ll.setVisibility(View.GONE);
                            farmer_product_lv.setVisibility(View.VISIBLE);
                        }
                        farmerProductAdapter.notifyDataSetChanged();
                    }
                });
        farmerProductAdapter = new FarmerProductAdapter(getContext(), productItems);
        farmer_product_lv.setAdapter(farmerProductAdapter);

        add_product_ib.setOnClickListener(v -> {

            String category[] = {"Fruits", "Vegetables", "Livestocks", "Poultry", "Fertilizer"};
            String unit[] = {"kg", "g", "ml", "pcs", "L"};
            addProductDialog.setContentView(R.layout.farmer_add_new_product_dialog);
            addProductDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            addProductDialog.setCancelable(false);
            TextView add_product_pic_tv = addProductDialog.findViewById(R.id.add_product_pic_tv);
            Button cancel_btn = addProductDialog.findViewById(R.id.cancel_btn);

            product_image_iv = addProductDialog.findViewById(R.id.product_image_iv);

            TextInputLayout productName_til=addProductDialog.findViewById(R.id.productName_til);
            TextInputLayout productDescription_til=addProductDialog.findViewById(R.id.productDescription_til);
            TextInputLayout productCategory_til=addProductDialog.findViewById(R.id.productCategory_til);
            TextInputLayout productStocks_til=addProductDialog.findViewById(R.id.productStocks_til);
            TextInputLayout productPrice_til=addProductDialog.findViewById(R.id.productPrice_til);
            TextInputLayout productQuantity_til=addProductDialog.findViewById(R.id.productQuantity_til);
            TextInputLayout productUnit_til=addProductDialog.findViewById(R.id.productUnit_til);
            AutoCompleteTextView productCategory_at = addProductDialog.findViewById(R.id.productCategory_at);
            AutoCompleteTextView productUnit_at = addProductDialog.findViewById(R.id.productUnit_at);
            Button add_product_btn = addProductDialog.findViewById(R.id.add_product_btn);
            add_product_btn.setOnClickListener(v13 -> {
                if(Verification.verifyPorductName(productName_til) &&
                        Verification.verifyPorductDescription(productDescription_til) &&
                        Verification.verifyPorductCategory(productCategory_til,productCategory_at.getText().toString()) &&
                        Verification.verifyPorductStocks(productStocks_til) &&
                        Verification.verifyPorductPrice(productPrice_til) &&
                        Verification.verifyPorductQuantity(productQuantity_til) &&
                        Verification.verifyPorductUnit(productUnit_til,productUnit_at.getText().toString())
                ){
                    product_image_iv.setDrawingCacheEnabled(true);
                    product_image_iv.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) product_image_iv.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    String productName=productName_til.getEditText().getText().toString(),
                            productDescription=productDescription_til.getEditText().getText().toString(),
                            productCategory=productCategory_at.getText().toString(),
                            productStocks=productStocks_til.getEditText().getText().toString(),
                            productPrice=productPrice_til.getEditText().getText().toString(),
                            productQuantity=productQuantity_til.getEditText().getText().toString(),
                            productUnit=productUnit_at.getText().toString();
                    ArrayList<Object> itemSpecs = new ArrayList<>();
                    itemSpecs.add(new String(productName));
                    itemSpecs.add(new String(productDescription));
                    itemSpecs.add(new String(productCategory));
                    itemSpecs.add(new Integer(Integer.parseInt(productStocks)));
                    itemSpecs.add(new Double(Double.parseDouble(productPrice)));
                    itemSpecs.add(new Integer(Integer.parseInt(productQuantity)));
                    itemSpecs.add(new String(productUnit));
                    itemSpecs.add(new String(user.getUid()));
                    if(FirebaseHelper.addProduct(getContext(),itemSpecs,data)){
                        addProductDialog.dismiss();
                        successAddProductToast.show();
                    }
                }

            });

            ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_item, category);
            productCategory_at.setAdapter(productCategoryAdapter);
            productCategory_at.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getContext(), "Selected : " + item, Toast.LENGTH_SHORT).show();
                }
            });

            ArrayAdapter<String> productUnitAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_item, unit);
            productUnit_at.setAdapter(productUnitAdapter);
            productUnit_at.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getContext(), "Selected : " + item, Toast.LENGTH_SHORT).show();
                }
            });

            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addProductDialog.cancel();
                }
            });
            product_image_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addProductPhotoDialog.show();
                }
            });
            addProductDialog.show();

            addProductPhotoDialog.setContentView(R.layout.add_product_picture_dialog);
            addProductPhotoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            addProductPhotoDialog.setCancelable(false);
            ImageButton camera_ib = addProductPhotoDialog.findViewById(R.id.camera_ib);
            ImageButton gallery_ib = addProductPhotoDialog.findViewById(R.id.gallery_ib);
            camera_ib.setOnClickListener(v1 -> {
                addFromCamera();
                add_product_pic_tv.setVisibility(View.GONE);
                addProductPhotoDialog.dismiss();
            });
            gallery_ib.setOnClickListener(v12 -> {
                addFromGallery();
                add_product_pic_tv.setVisibility(View.GONE);
                addProductPhotoDialog.dismiss();
            });


        });

        return view;
    }

    public void addFromCamera() {
//        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            selectFromCamera.launch(intent);

        } else {
            Toast.makeText(getActivity(), "There is no app to support this action", Toast.LENGTH_SHORT).show();
        }
    }

    public void addFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setType("image/*");
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            selectFromGallery.launch(intent);
        } else {
            Toast.makeText(getActivity(), "There is no app to support this action", Toast.LENGTH_SHORT).show();
        }
    }

    public class FarmerProductAdapter extends BaseAdapter {

        Context context;
        ArrayList<FarmerProductItem> productList;
        LayoutInflater layoutInflater;

        public FarmerProductAdapter(Context context, ArrayList<FarmerProductItem> productList) {
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


            Glide.with(context)
                    .load(productList.get(position).getProductImage())
                    .into(product_image_iv);
            product_name_unit.setText(productList.get(position).getProductName() + " (per "+productList.get(position).getProductQuantity()+" " + productList.get(position).getProductUnit()+")");
            product_stocks.setText("Stocks: " + productList.get(position).getProductStocks());
            product_sold.setText("Sold: " + productList.get(position).getProductSold());
            product_category.setText("Category: " + productList.get(position).getProductCategory());
            product_price.setText("Price: Php " + productList.get(position).getProductPrice());
            return convertView;
        }
    }

}