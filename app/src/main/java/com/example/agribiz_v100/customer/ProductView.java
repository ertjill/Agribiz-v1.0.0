package com.example.agribiz_v100.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.farmer.FarmerProductItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductView extends AppCompatActivity {

    FarmerProductItem farmerProductItem;
    ImageView product_image_iv;
    ImageView hub_profile_image;
    LinearLayout rating_ll;
    TextView productName,productPrice,productSold,hubName,productStocks,productLocation;
    Toolbar topAppBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        topAppBar=findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        product_image_iv=findViewById(R.id.product_image_iv);
        hub_profile_image=findViewById(R.id.hub_profile_image);
        productName=findViewById(R.id.productName);
        productPrice=findViewById(R.id.productPrice);
        productSold=findViewById(R.id.productSold);
        hubName=findViewById(R.id.hubName);
        productStocks=findViewById(R.id.productStocks);
        productLocation=findViewById(R.id.productLocation);
        rating_ll=findViewById(R.id.rating_ll);
        if(getIntent().getExtras() != null) {
            farmerProductItem = (FarmerProductItem) getIntent().getSerializableExtra("item");
            productName.setText(farmerProductItem.getProductName()+" ( per "+farmerProductItem.getProductQuantity()+" "+farmerProductItem.getProductUnit()+" )");
            productPrice.setText("Php "+farmerProductItem.getProductPrice());
            productSold.setText(farmerProductItem.getProductSold()+" sold");
            productStocks.setText(farmerProductItem.getProductStocks());
            Log.d("ProductView",farmerProductItem.getProductImage());
            Glide.with(getApplicationContext())
                    .load(farmerProductItem.getProductImage())
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
            for(int i=0;i<5;i++){
                ImageView start= new ImageView(this);

                if(i<farmerProductItem.getProductRating())
                {
                    start.setImageResource(R.drawable.ic_baseline_star);
                    rating_ll.addView(start);
                }else
                {
                    start.setImageResource(R.drawable.ic_baseline_star_outline_24);
                    rating_ll.addView(start);
                }
            }



            Log.d("ProductView", "Number of Item: " + farmerProductItem.getProductFarmProfile());
        }
        Log.d("ProductView","getIntent().getExtras().toString())");
    }
}