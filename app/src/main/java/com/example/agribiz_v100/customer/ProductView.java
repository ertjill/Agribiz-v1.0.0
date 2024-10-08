package com.example.agribiz_v100.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.agribiz_v100.ChatActivity;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.ChatModel;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.entities.UserModel;
import com.example.agribiz_v100.services.BasketManagement;
import com.example.agribiz_v100.services.NotificationManagement;
import com.example.agribiz_v100.services.ProductManagement;
import com.example.agribiz_v100.services.ProfileManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProductView extends AppCompatActivity {

    private static final String TAG = "ProductView";
    ProductModel farmerProductItem;

    ImageView product_image_iv;
    ImageView hub_profile_image;
    LinearLayout rating_ll;
    TextView productName, productPrice, productSold, hubName, productStocks, productLocation,no_user_rated_tv, add_to_basket_tv,category_tv, buy_now_tv, chat_tv,product_descriptio_tv, count_product_tv;
    MaterialToolbar topAppBar;
    Dialog addProductToBasket, buyNow;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ViewPager2 imageSlider;
    int itemsCount = 0;
    TextView itemCounter;
    ImageViewPagerAdapter imageViewPagerAdapter;
    List<String> images;
    BasketManagement basketManagement;
    UserModel userModel;
    MenuItem menuItem;

    public void getNoBasketItems() {
        itemsCount = 0;
        BasketManagement.getBaskitItems(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot ds : queryDocumentSnapshots) {
                        itemsCount += 1;
                    }
                    if (itemsCount == 0) {
                        menuItem.setActionView(null);
                    } else {
                        menuItem.setActionView(R.layout.badge);
                        View vi = menuItem.getActionView();
                        itemCounter = vi.findViewById(R.id.badge_tv);
                        itemCounter.setText(String.valueOf(itemsCount));
                    }
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        basketManagement = new BasketManagement(this);
        topAppBar = findViewById(R.id.topAppBar);
        imageSlider = findViewById(R.id.imageSlider);
        imageViewPagerAdapter = new ImageViewPagerAdapter();
        chat_tv = findViewById(R.id.chat_tv);
        count_product_tv = findViewById(R.id.count_product_tv);
        product_descriptio_tv = findViewById(R.id.product_descriptio_tv);
        category_tv = findViewById(R.id.category_tv);
        no_user_rated_tv=findViewById(R.id.no_user_rated_tv);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getApplicationContext(), "com.example.agribiz_v100.customer.CustomerMainActivity");
                startActivity(intent);
                finish();
            }
        });

        menuItem = topAppBar.getMenu().findItem(R.id.basket_menu);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                ChatModel cm = new ChatModel();
//                cm.setChatSenderUserId("M7DoQujvc3XHsqA9mowKM2Ws0sc2");
//                cm.setChatDate(new Timestamp(new Date()));
//                cm.setChatMessage("Hello, this is jack the one who bought alot of item from you. May I ask how");
//                NotificationManagement.getNewMessageNotification(ProductView.this, cm, 1);
                return false;
            }
        });
        //add basket number of items
        getNoBasketItems();

        userModel = new UserModel();

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
                        basketManagement.addToBasket(farmerProductItem.getProductId(), Integer.parseInt(product_quantity_tv.getText().toString()))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        addProductToBasket.dismiss();
                                        getNoBasketItems();
                                    }
                                });
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

        buy_now_tv = findViewById(R.id.buy_now_tv);

        buyNow = new Dialog(this);
        buyNow.setContentView(R.layout.buy_now_dialog);
        buyNow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        buyNow.setCancelable(false);

        buy_now_tv.setOnClickListener(v -> {
            TextView increment = buyNow.findViewById(R.id.add_tv);
            TextView decrement = buyNow.findViewById(R.id.minus_tv);
            TextView prodQuantity = buyNow.findViewById(R.id.product_quantity_tv);
            ImageButton close = buyNow.findViewById(R.id.close_ib);
            Button buy_now_btn = buyNow.findViewById(R.id.buy_now_btn);

            close.setOnClickListener(v2 -> buyNow.dismiss());

            buy_now_btn.setOnClickListener(v3 -> {

                Intent i = new Intent(this, BuyNowActivity.class);
                i.putExtra("productId",farmerProductItem.getProductId());
                i.putExtra("productBasketQuantity",Integer.parseInt(prodQuantity.getText().toString()));
                startActivity(i);
            });

            increment.setOnClickListener(v3 -> {
                int i = Integer.parseInt(prodQuantity.getText().toString());
                if (i < 100) {
                    prodQuantity.setText(++i + "");
                }
            });

            decrement.setOnClickListener(v3 -> {
                int i = Integer.parseInt(prodQuantity.getText().toString());
                if (i > 1) {
                    prodQuantity.setText(--i + "");
                }
            });

            // Add some add ons here
            buyNow.show();
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
            chat_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductView.this, ChatActivity.class);
                    intent.putExtra("userId", farmerProductItem.getProductUserId());
                    startActivity(intent);
                }
            });

            ProductManagement.getProductByUser(farmerProductItem.getProductUserId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int count = 0;
                    for(DocumentSnapshot ds:queryDocumentSnapshots){
                        count++;
                    }
                    count_product_tv.setText(count+" Products");
                }
            });

//            user = getIntent().getParcelableExtra("user");
            productName.setText(farmerProductItem.getProductName() + " ( per " + farmerProductItem.getProductQuantity() + " " + farmerProductItem.getProductUnit() + " )");
            productPrice.setText("Php " + farmerProductItem.getProductPrice());
            productSold.setText(farmerProductItem.getProductSold() + " sold");
            productStocks.setText(farmerProductItem.getProductStocks() + "");
            category_tv.setText(farmerProductItem.getProductCategory());
            product_descriptio_tv.setText(farmerProductItem.getProductDescription());
            no_user_rated_tv.setText(" | "+farmerProductItem.getProductNoCustomerRate());
            Glide.with(getApplicationContext())
                    .load(farmerProductItem.getProductImage().get(0))
                    .into(product_image_iv);
            images = farmerProductItem.getProductImage();
            imageSlider.setAdapter(imageViewPagerAdapter);

            //get product user(farmer/agrovit) profile info
            ProfileManagement.getUserProfile(farmerProductItem.getProductUserId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Glide.with(getApplicationContext())
                            .load(documentSnapshot.getString("userImage"))
                            .into(hub_profile_image);
                    productLocation.setText(((Map<String,String>)documentSnapshot.getData().get("userLocation")).get("userMunicipality"));
                }
            });
            // Create a storage reference from our app
//            FirebaseStorage storage = FirebaseStorage.getInstance();
//            StorageReference storageRef = storage.getReference();
            // Get reference to the file
//            StorageReference forestRef = storageRef.child("profile/" + "272229741_475164050669220_5648552245273002941_n.png");
//            forestRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Glide.with(getApplicationContext())
//                            .load(uri.toString())
//                            .into(hub_profile_image);
//
//                }
//            });
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
            DocumentReference docRef = db.collection("users").document(farmerProductItem.getProductUserId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            userModel.setUserDisplayName(document.get("userDisplayName").toString());
                            hubName.setText(userModel.getUserDisplayName().substring(0, userModel.getUserDisplayName().length() - 2));

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        Log.d("ProductView", "getIntent().getExtras().toString())");
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