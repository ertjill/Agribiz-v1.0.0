package com.example.agribiz_v100.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.TimeFormatException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.farmer.MyProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class ProductView extends AppCompatActivity implements FirebaseHelper.FirebaseHelperCallback {

    private static final String TAG = "ProductView";
    ProductItem farmerProductItem;
    ImageView product_image_iv;
    ImageView hub_profile_image;
    LinearLayout rating_ll;
    TextView productName, productPrice, productSold, hubName, productStocks, productLocation, add_to_basket_tv;
    MaterialToolbar topAppBar;
    Dialog addProductToBasket;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseHelper firebaseHelper;
    ViewPager2 imageSlider;
    int itemsCount = 0;
    TextView itemCounter;
    ImageViewPagerAdapter imageViewPagerAdapter;
    List<String> images;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        firebaseHelper = new FirebaseHelper(this);
        topAppBar = findViewById(R.id.topAppBar);
        imageSlider = findViewById(R.id.imageSlider);
        imageViewPagerAdapter = new ImageViewPagerAdapter();

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        MenuItem menuItem = topAppBar.getMenu().findItem(R.id.basket_menu);
        if (itemsCount == 0) {
            menuItem.setActionView(null);
        } else {
            menuItem.setActionView(R.layout.badge);
            View vi = menuItem.getActionView();
            itemCounter = vi.findViewById(R.id.badge_tv);
            itemCounter.setText(String.valueOf(itemsCount));
        }
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.basket_menu:

                        break;
                }
                return true;
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
                        Date date =new Date();
                        Timestamp productDateAdded = new Timestamp(date);
                        product.put("productBasketQuantity", Integer.parseInt(product_quantity_tv.getText().toString()));
                        product.put("productDateAdded",productDateAdded);
                        String hubId = product.get("productUserId").toString();
                        String productId = product.get("productId").toString();
//                        Log.d(TAG, product.get("productBasketQuantity").toString() + " " + user.getUid());
//                        firebaseHelper.addProductToBasket(product, productId, user.getUid(), hubId);
//                        CustomerMainActivity cm = (CustomerMainActivity)
                        addToBasket(product);
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
                        if (i < 100) {
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
//            user = getIntent().getParcelableExtra("user");
            productName.setText(farmerProductItem.getProductName() + " ( per " + farmerProductItem.getProductQuantity() + " " + farmerProductItem.getProductUnit() + " )");
            productPrice.setText("Php " + farmerProductItem.getProductPrice());
            productSold.setText(farmerProductItem.getProductSold() + " sold");
            productStocks.setText(farmerProductItem.getProductStocks() + "");
            Glide.with(getApplicationContext())
                    .load(farmerProductItem.getProductImage().get(0))
                    .into(product_image_iv);
            images = farmerProductItem.getProductImage();
            imageSlider.setAdapter(imageViewPagerAdapter);
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

    public void addToBasket(Map<String,Object> product){
        db.collection("users").document(user.getUid())
                .collection("basket").document(product.get("productId").toString())
                .set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    @Override
    public void isProductAddedToBasket(boolean added) {
        if (added)
            Toast.makeText(this, "Successfully added product to basket", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Failed to add product on basket!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void getProductFromBasket(SparseArray<Object> productFromBasket) {

    }

    @Override
    public void getProducts(SparseArray<ProductItem> products, SparseArray<ProductItem> topProducst) {

    }

    @Override
    public void getToProducst(SparseArray<ProductItem> topProducts) {

    }

    public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.SlideViewHolder> {
        LayoutInflater layoutInflater;
        @NonNull
        @Override
        public ImageViewPagerAdapter.SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ImageViewPagerAdapter.SlideViewHolder(
                    LayoutInflater.from(getApplicationContext()).inflate(
                            R.layout.product_view_image_slide, parent, false
                    )
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewPagerAdapter.SlideViewHolder holder, int position) {
            Glide.with(getApplicationContext())
                    .load(images.get(position))
                    .into(holder.product_image_iv);
            holder.indicator_tv.setText((position + 1) + "/" + images.size());
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public class SlideViewHolder extends RecyclerView.ViewHolder {
            ImageView product_image_iv;
            TextView indicator_tv;
            public SlideViewHolder(@NonNull View itemView) {
                super(itemView);
                product_image_iv = itemView.findViewById(R.id.product_image_iv);
                indicator_tv = itemView.findViewById(R.id.indicator_tv);
            }
        }
    }


}