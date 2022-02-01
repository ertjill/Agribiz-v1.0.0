package com.example.agribiz_v100.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Parcelable;
import android.util.SparseArray;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Store extends Fragment {


    private static final String TAG = "Store";
    HorizontalScrollView farmersHub_hv;
    LinearLayout farmersHub_ll;
    //    GridLayout topProduct_gl;
    ListView item_may_like_lv;
    TextView viewAll_tv;
    GridView topProduce_gv;
    String[] item_may_like = {"Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato", "Sweet Tomato"};
    SparseArray<ProductItem> productItems, topProducts;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProductGridAdapter productGridAdapter;
    Process one;
    FirebaseUser user;
    Bundle bundle;
    public synchronized void setProducts() {

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productItems = new SparseArray<>();
        topProducts = new SparseArray<>();

        if (getArguments() != null) {
            bundle = getArguments();
            productItems = bundle.getSparseParcelableArray("productItems");
            topProducts = bundle.getSparseParcelableArray("topProducts");
            //productGridAdapter.notifyDataSetChanged();
            user=bundle.getParcelable("user");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        farmersHub_hv = view.findViewById(R.id.farmersHub_hv);
        farmersHub_ll = view.findViewById(R.id.farmersHub_ll);
        topProduce_gv = view.findViewById(R.id.topProduce_gv);
        item_may_like_lv = view.findViewById(R.id.item_may_like_lv);
        viewAll_tv = view.findViewById(R.id.viewAll_tv);

        topProduce_gv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        viewAll_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, productItems.size() + "");
                Intent i = new Intent(getContext(), ViewAllProductsActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSparseParcelableArray("productItems", productItems);

                i.putExtras(bundle);
                startActivity(i);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (productItems.size() > 0)
                    productGridAdapter.notifyDataSetChanged();
                else
                    Toast.makeText(getContext(), "No Internet Access!", Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "wALAY SULOD GIHAPON");
            }
        }, 2000);

        topProduce_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ProductView.class);
                intent.putExtra("item", productItems.get(position));
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        setupTopProduce();
        setupFarmerHubs();
        setupItemsYouLike();

        return view;


//        ProductGridAdapter productGridAdapter = new ProductGridAdapter(getContext(),productName,productUnit,productPrice,image);
//        GridView topProduct_gv = view.findViewById(R.id.topProduce_gv);
//        topProduct_gv.setAdapter(productGridAdapter);

//        topProduct_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), "Product item", Toast.LENGTH_SHORT).show();
//            }
//        });


//        topProduct_gl = view.findViewById(R.id.topProduct_gl);


//        for (int i = 0; i < productItems.size(); i++) {
//            if (i < 10){
//                topProducts.append(i, productItems.get(i));
//            }
//        }


    }

    private void setupItemsYouLike() {
        ItemMayLikeAdapter itemMayLikeAdapter = new ItemMayLikeAdapter(getContext(), item_may_like);
        item_may_like_lv.setAdapter(itemMayLikeAdapter);
    }

    public void setupTopProduce() {
        productGridAdapter = new ProductGridAdapter(getContext(), topProducts);
        topProduce_gv.setAdapter(productGridAdapter);
    }

    public void setupFarmerHubs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereNotEqualTo("userType", "customer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LayoutInflater i = getLayoutInflater();
                                View v = i.inflate(R.layout.fragment_farmer_hub_card, null);
                                TextView farmers = v.findViewById(R.id.farmer);
                                ImageView farmerProfile = v.findViewById(R.id.farmerProfile);
                                Glide.with(getContext())
                                        .load(document.getData().get("userImage"))
                                        .into(farmerProfile);
                                farmers.setText(document.getData().get("username").toString());
                                farmers.setOnClickListener(v1 -> {
                                    Toast.makeText(getContext(), document.getData().get("userName").toString(), Toast.LENGTH_SHORT).show();
                                });
                                farmersHub_ll.addView(v);
                                Log.d("FirebaseHelper", document.getData().get("username").toString());
                            }

                        } else {
                            Log.d("FirebaseHelper", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public class ProductGridAdapter extends BaseAdapter {
        Context context;
        SparseArray<ProductItem> product;

        LayoutInflater layoutInflater;

        public ProductGridAdapter(Context context, SparseArray<ProductItem> product) {
            this.context = context;
            this.product = product;
        }

        @Override
        public int getCount() {
            return product.size();
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
                convertView = layoutInflater.inflate(R.layout.top_product_item_card, null);
            }

            ImageView imageView = convertView.findViewById(R.id.product_iv);
            TextView productName = convertView.findViewById(R.id.product_name_tv);
            TextView productRating = convertView.findViewById(R.id.productRating_tv);
            TextView productPrice = convertView.findViewById(R.id.productPrice_tv);

            Glide.with(context)
                    .load(product.get(position).getProductImage().get(0))
                    .into(imageView);
            productName.setText(product.get(position).getProductName());
            productRating.setText("0");
            productPrice.setText("Php " + product.get(position).getProductPrice());

            return convertView;
        }
    }


}