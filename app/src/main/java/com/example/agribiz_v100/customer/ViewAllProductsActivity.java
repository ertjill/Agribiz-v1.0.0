package com.example.agribiz_v100.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.paging.PagedList;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.services.ProductManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ViewAllProductsActivity extends AppCompatActivity {
    String TAG = "ViewAllProductsActivity";
    LinearLayout no_product_ll;
    RecyclerView viewAll_rv;
    ViewAllProductAdapter viewAllProductAdapter;
    MaterialToolbar topAppBar;
    SparseArray<ProductItem> productItems;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    Bundle bundle;
    View ChildView;
    List<String> search;
    int RecyclerViewItemPosition;
    private FirebaseFirestore firestore;
    ProductGridAdapter productGridAdapter;
    boolean isLoading = false;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText searchProduct_et;
    boolean searching = false;
    TextInputLayout search_tl;
    int count = 0;
    DocumentSnapshot last = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ViewAllProductsActivity");
        setContentView(R.layout.activity_view_all_products);
        firestore = FirebaseFirestore.getInstance();
        no_product_ll = findViewById(R.id.no_product_ll);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        searchProduct_et = findViewById(R.id.searchProduct_et);
        search_tl = findViewById(R.id.search_tl);
        search = new ArrayList<>();
        search_tl.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searching)
                {
                    searchProduct_et.setText("");
                    searching = false;
                    count = 0;
                    productItems.clear();
                    viewAllProductAdapter.notifyDataSetChanged();
                    last = null;
                    loadProduct();
                }
            }
        });
        searchProduct_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searching = true;
                    last = null;
                    count = 0;
                    productItems.clear();
                    viewAllProductAdapter.notifyDataSetChanged();
                    loadProduct();
                    return true;
                }
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        productItems = new SparseArray<>();
        viewAll_rv = findViewById(R.id.viewAll_rv);
        viewAll_rv.setHasFixedSize(true);

        if (getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
//            productItems = bundle.getSparseParcelableArray("productItems");
            user = bundle.getParcelable("user");
            Log.d(TAG, user.getDisplayName().toString() + "  :  " + productItems.size());
        }
        productGridAdapter = new ProductGridAdapter(getApplicationContext(), productItems);

        viewAllProductAdapter = new ViewAllProductAdapter(this);
        loadProduct();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        viewAll_rv.setLayoutManager(gridLayoutManager);
        viewAll_rv.setAdapter(viewAllProductAdapter);

        viewAll_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) viewAll_rv.getLayoutManager();
                int totalItem = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (totalItem < (lastVisibleItem + 2)) {
                    if (!isLoading) {
                        isLoading = true;
                        if (!searching)
                            loadProduct();
                    }
                }
            }
        });
        viewAll_rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                ChildView = rv.findChildViewUnder(e.getX(), e.getY());
                if (ChildView != null && gestureDetector.onTouchEvent(e)) {
                    RecyclerViewItemPosition = rv.getChildAdapterPosition(ChildView);
                    Intent intent = new Intent(ViewAllProductsActivity.this, ProductView.class);
                    intent.putExtra("item", productItems.get(RecyclerViewItemPosition));
                    intent.putExtra("user", user);
                    startActivity(intent);
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClassName(getApplicationContext(), "com.example.agribiz_v100.customer.CustomerMainActivity");
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClassName(getApplicationContext(), "com.example.agribiz_v100.customer.CustomerMainActivity");
        startActivity(intent);
    }

    public void loadProduct() {
        swipeRefreshLayout.setRefreshing(true);
//        if (last == null) {
        if (!searching) {
            ProductManagement.getProducts(last)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            // ...
                            for (QueryDocumentSnapshot dc : documentSnapshots) {
                                ProductItem i = new ProductItem(dc);
                                productItems.append(count++, i);
                            }

                            // Get the last visible document
                            if (documentSnapshots.getDocuments().size() > 0) {
                                last = documentSnapshots.getDocuments()
                                        .get((documentSnapshots.size() - 1));

                            } else {
                                Toast.makeText(getApplicationContext(), "No Products to load.", Toast.LENGTH_SHORT).show();
                            }
                            if (productItems.size() > 0) {
                                no_product_ll.setVisibility(View.GONE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                            } else {
                                no_product_ll.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setVisibility(View.GONE);
                            }
                            productGridAdapter.notifyDataSetChanged();
                            isLoading = false;
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
        } else {
            ProductManagement.searchProducts(last, searchProduct_et.getText().toString()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            // ...
                            for (QueryDocumentSnapshot dc : documentSnapshots) {
                                ProductItem i = new ProductItem(dc);
                                productItems.append(count++, i);
                            }

                            // Get the last visible document
                            if (documentSnapshots.getDocuments().size() > 0) {
                                last = documentSnapshots.getDocuments()
                                        .get((documentSnapshots.size() - 1));

                            } else {
                                Toast.makeText(getApplicationContext(), "No Products to load.", Toast.LENGTH_SHORT).show();
                            }
                            if (productItems.size() > 0) {
                                no_product_ll.setVisibility(View.GONE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                            } else {
                                no_product_ll.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setVisibility(View.GONE);
                            }
                            productGridAdapter.notifyDataSetChanged();
                            isLoading = false;
                            swipeRefreshLayout.setRefreshing(false);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(ViewAllProductsActivity.this);
                            alert.setTitle("Alert");
                            alert.setMessage(e.getMessage());
                            alert.setPositiveButton("Ok", null);
                            alert.show();
                            isLoading = false;
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop...");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause...");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume...");

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, "onPostCreate...");
    }


    class ViewAllProductAdapter extends RecyclerView.Adapter<ViewAllProductAdapter.ViewHolder> {
        LayoutInflater inflater;

        public ViewAllProductAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.product_item_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Glide.with(inflater.getContext())
                    .load(productItems.get(position).getProductImage().get(0))
                    .into(holder.imageView);
            holder.productName.setText(productItems.get(position).getProductName());
            holder.productUnit.setText("(per " + productItems.get(position).getProductQuantity() + " " + productItems.get(position).getProductUnit() + ")");
            holder.productPrice.setText("Php " + productItems.get(position).getProductPrice());
        }

        @Override
        public int getItemCount() {
            return productItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView productName;
            TextView productUnit;
            TextView productPrice;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.product_iv);
                productName = itemView.findViewById(R.id.product_name_tv);
                productUnit = itemView.findViewById(R.id.productUnit_tv);
                productPrice = itemView.findViewById(R.id.productPrice_tv);


            }
        }
    }

}