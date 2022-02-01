package com.example.agribiz_v100.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProductView extends AppCompatActivity implements FirebaseHelper.FirebaseHelperCallback {

    private static final String TAG = "ProductView";
    ProductItem farmerProductItem;
    ImageView product_image_iv;
    ImageView hub_profile_image;
    LinearLayout rating_ll;
    TextView productName, productPrice, productSold, hubName, productStocks, productLocation, add_to_basket_tv;
    MaterialToolbar topAppBar;
    Dialog addProductToBasket;
    FirebaseUser user;
    FirebaseHelper firebaseHelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        firebaseHelper = new FirebaseHelper(this);
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addProductToBasket = new Dialog(this);
        addProductToBasket.setContentView(R.layout.add_to_basket_dialog);
        addProductToBasket.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addProductToBasket.setCancelable(false);
        add_to_basket_tv = findViewById(R.id.add_to_basket_tv);
        add_to_basket_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton close_ib = addProductToBasket.findViewById(R.id.close_ib);
                ImageView add_basket_product_image_iv = addProductToBasket.findViewById(R.id.add_basket_product_image_iv);
                Glide.with(getApplicationContext())
                        .load(farmerProductItem.getProductImage().get(0))
                        .into(add_basket_product_image_iv);
                TextView add_product_basket_name_unit_per_item = addProductToBasket.findViewById(R.id.add_product_basket_name_unit_per_item);
                add_product_basket_name_unit_per_item.setText(farmerProductItem.getProductName() + " ( per " + farmerProductItem.getProductQuantity() + " " + farmerProductItem.getProductUnit() + " )");
                TextView add_basket_product_price = addProductToBasket.findViewById(R.id.add_basket_product_price);
                add_basket_product_price.setText("Php " + farmerProductItem.getProductPrice());
                TextView add_basket_product_stocks = addProductToBasket.findViewById(R.id.add_basket_product_stocks);
                add_basket_product_stocks.setText("Stocks: " + farmerProductItem.getProductStocks());
                TextView product_quantity_tv = addProductToBasket.findViewById(R.id.product_quantity_tv),
                        minus_tv = addProductToBasket.findViewById(R.id.minus_tv),
                        add_tv = addProductToBasket.findViewById(R.id.add_tv);
                Button add_basket_product_btn = addProductToBasket.findViewById(R.id.add_basket_product_btn);
                add_basket_product_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> product = farmerProductItem.getProductMap();
                        product.put("productBasketQuantity", Integer.parseInt(product_quantity_tv.getText().toString()));
                        String hubId = product.get("productFarmId").toString();
                        String productId = product.get("productId").toString();
                        Log.d(TAG, product.get("productBasketQuantity").toString() + " " + user.getUid());
                        firebaseHelper.addProductToBasket(product, productId, user.getUid(),hubId);
                        addProductToBasket.dismiss();
                    }
                });
                minus_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = Integer.parseInt(product_quantity_tv.getText().toString());
                        if (i > 1) {
                            product_quantity_tv.setText(--i + "");
                        }

                    }
                });
                add_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int i = Integer.parseInt(product_quantity_tv.getText().toString());
                        if (i < 100)
                        {
                            product_quantity_tv.setText(++i + "");
                        }
                    }
                });
                close_ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addProductToBasket.dismiss();
                    }
                });
                addProductToBasket.show();
            }
        });
        product_image_iv = findViewById(R.id.product_image_iv);
        hub_profile_image = findViewById(R.id.hub_profile_image);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productSold = findViewById(R.id.productSold);
        hubName = findViewById(R.id.hubName);
        productStocks = findViewById(R.id.productStocks);
        productLocation = findViewById(R.id.productLocation);
        rating_ll = findViewById(R.id.rating_ll);

        if (getIntent().getExtras() != null) {
            farmerProductItem = getIntent().getParcelableExtra("item");
            user = getIntent().getParcelableExtra("user");
            productName.setText(farmerProductItem.getProductName() + " ( per " + farmerProductItem.getProductQuantity() + " " + farmerProductItem.getProductUnit() + " )");
            productPrice.setText("Php " + farmerProductItem.getProductPrice());
            productSold.setText(farmerProductItem.getProductSold() + " sold");
            productStocks.setText(farmerProductItem.getProductStocks() + "");
            Glide.with(getApplicationContext())
                    .load(farmerProductItem.getProductImage().get(0))
                    .into(product_image_iv);
            // Create a storage reference from our app
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            // Get reference to the file
            StorageReference forestRef = storageRef.child("profile/" + "272229741_475164050669220_5648552245273002941_n.png");
            forestRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext())
                            .load(uri.toString())
                            .into(hub_profile_image);

                }
            });
            for (int i = 0; i < 5; i++) {
                ImageView start = new ImageView(this);

                if (i < farmerProductItem.getProductRating()) {
                    start.setImageResource(R.drawable.ic_baseline_star);
                    rating_ll.addView(start);
                } else {
                    start.setImageResource(R.drawable.ic_baseline_star_outline_24);
                    rating_ll.addView(start);
                }
            }


            Log.d("ProductView", "Number of Item: " + farmerProductItem.getProductFarmImage());
        }
        Log.d("ProductView", "getIntent().getExtras().toString())");
    }

    @Override
    public void isProductAddedToBasket(boolean added) {
        if (added)
            Toast.makeText(this, "Successfully added product to basket", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Failed to add product on basket!", Toast.LENGTH_LONG).show();
    }
}