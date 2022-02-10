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
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
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
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.agribiz_v100.FirebaseHelper;
import com.example.agribiz_v100.OnboardingAdapter;
import com.example.agribiz_v100.ProductItem;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.Verification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MyProduct extends Fragment {
    String TAG = "MyProduct";
    ListView farmer_product_lv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    SparseArray<ProductItem> productItems;
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
    List<Uri> arrayOfImages;
    ImageViewPagerAdapter imageViewPagerAdapter;
    ViewPager2 imageSlider;
    LinearLayout blank_photo_ll;
    LinearProgressIndicator add_product_progress;
    SwipeRefreshLayout refreshLayout;
    DocumentSnapshot last = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        successAddProductToast = new Toast(getContext());
        arrayOfImages = new ArrayList<>();
        LayoutInflater inflater1 = getLayoutInflater();
        View successToast = inflater1.inflate(R.layout.success_toast, null);
        successAddProductToast.setView(successToast);
        successAddProductToast.setDuration(Toast.LENGTH_LONG);
        successAddProductToast.setGravity(Gravity.CENTER, 0, 0);
        Toast.makeText(getActivity(), "Successfully added produce", Toast.LENGTH_SHORT).show();
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_product, container, false);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayMyProducts();
                refreshLayout.setRefreshing(false);
            }
        });
        addProductDialog = new Dialog(getContext());
        addProductPhotoDialog = new Dialog(getContext());
        farmer_product_lv = view.findViewById(R.id.farmer_product_lv);
        no_product_ll = view.findViewById(R.id.no_product_ll);
        add_product_ib = view.findViewById(R.id.add_product_ib);
        productItems = new SparseArray<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        displayMyProducts();

        selectFromGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    product_image_iv.setImageURI(result.getData().getData());
//                }
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData().getClipData() != null) {
                        int i = result.getData().getClipData().getItemCount();
                        for (int n = 0; n < i; n++) {
                            Uri imageUri = result.getData().getClipData().getItemAt(n).getUri();
                            arrayOfImages.add(imageUri);

                        }
                        Log.d(TAG, "aDD mORE");

                        if (arrayOfImages.size() > 0) {
                            blank_photo_ll.setVisibility(View.GONE);
                            imageSlider.setVisibility(View.VISIBLE);
                            imageViewPagerAdapter.notifyDataSetChanged();

                        } else
                            blank_photo_ll.setVisibility(View.VISIBLE);
                    } else {
                        Uri imageUri = result.getData().getData();
                        arrayOfImages.add(imageUri);
                        if (arrayOfImages.size() > 0) {
                            blank_photo_ll.setVisibility(View.GONE);
                            imageSlider.setVisibility(View.VISIBLE);
                            imageViewPagerAdapter.notifyDataSetChanged();

                        } else
                            blank_photo_ll.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Unable to add this images!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectFromCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    Bundle bundle = result.getData().getExtras();
//                    Bitmap bitmap = (Bitmap) bundle.get("data");
//                    product_image_iv.setImageBitmap(bitmap);
//                }

                if (result.getResultCode() == Activity.RESULT_OK && result.getData().getClipData() != null) {
                    int i = result.getData().getClipData().getItemCount();
                    for (int n = 0; n < i; n++) {
                        Uri imageUri = result.getData().getClipData().getItemAt(n).getUri();
                        arrayOfImages.add(imageUri);
                    }
//                    imageViewPagerAdapter = new ImageViewPagerAdapter(getContext(), arrayOfImages);
//                    imageSlider.setAdapter(imageViewPagerAdapter);
                }
            }
        });

        add_product_ib.setOnClickListener(v -> {

            String category[] = {"Fruits", "Vegetables", "Livestocks", "Poultry", "Fertilizer"};
            String unit[] = {"kg", "g", "ml", "pcs", "L"};
            addProductDialog.setContentView(R.layout.farmer_add_new_product_dialog);
            addProductDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            addProductDialog.setCancelable(false);
            Button cancel_btn = addProductDialog.findViewById(R.id.cancel_btn);
            product_image_iv = addProductDialog.findViewById(R.id.product_image_iv);
            imageSlider = addProductDialog.findViewById(R.id.imageSlider);
            add_product_progress = addProductDialog.findViewById(R.id.add_product_progress);
            TextInputLayout productName_til = addProductDialog.findViewById(R.id.productName_til);
            TextInputLayout productDescription_til = addProductDialog.findViewById(R.id.productDescription_til);
            TextInputLayout productCategory_til = addProductDialog.findViewById(R.id.productCategory_til);
            TextInputLayout productStocks_til = addProductDialog.findViewById(R.id.productStocks_til);
            TextInputLayout productPrice_til = addProductDialog.findViewById(R.id.productPrice_til);
            TextInputLayout productQuantity_til = addProductDialog.findViewById(R.id.productQuantity_til);
            TextInputLayout productUnit_til = addProductDialog.findViewById(R.id.productUnit_til);
            AutoCompleteTextView productCategory_at = addProductDialog.findViewById(R.id.productCategory_at);
            AutoCompleteTextView productUnit_at = addProductDialog.findViewById(R.id.productUnit_at);
            Button add_product_btn = addProductDialog.findViewById(R.id.add_product_btn);
            blank_photo_ll = addProductDialog.findViewById(R.id.blank_photo_ll);
            imageViewPagerAdapter = new ImageViewPagerAdapter();
            imageSlider.setAdapter(imageViewPagerAdapter);

            add_product_btn.setOnClickListener(v13 -> {
                if (Verification.verifyPorductName(productName_til) &&
                        Verification.verifyPorductDescription(productDescription_til) &&
                        Verification.verifyPorductCategory(productCategory_til, productCategory_at.getText().toString()) &&
                        Verification.verifyPorductStocks(productStocks_til) &&
                        Verification.verifyPorductPrice(productPrice_til) &&
                        Verification.verifyPorductQuantity(productQuantity_til) &&
                        Verification.verifyPorductUnit(productUnit_til, productUnit_at.getText().toString())
                ) {
//                    product_image_iv.setDrawingCacheEnabled(true);
//                    product_image_iv.buildDrawingCache();
//                    Bitmap bitmap = ((BitmapDrawable) product_image_iv.getDrawable()).getBitmap();
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    byte[] data = baos.toByteArray();
                    String productName = productName_til.getEditText().getText().toString(),
                            productDescription = productDescription_til.getEditText().getText().toString(),
                            productCategory = productCategory_at.getText().toString(),
                            productStocks = productStocks_til.getEditText().getText().toString(),
                            productPrice = productPrice_til.getEditText().getText().toString(),
                            productQuantity = productQuantity_til.getEditText().getText().toString(),
                            productUnit = productUnit_at.getText().toString();
//                    ArrayList<Object> itemSpecs = new ArrayList<>();
//                    itemSpecs.add(new String(productName));
//                    itemSpecs.add(new String(productDescription));
//                    itemSpecs.add(new String(productCategory));
//                    itemSpecs.add(new Integer(Integer.parseInt(productStocks)));
//                    itemSpecs.add(new Double(Double.parseDouble(productPrice)));
//                    itemSpecs.add(new Integer(Integer.parseInt(productQuantity)));
//                    itemSpecs.add(new String(productUnit));
//                    itemSpecs.add(new String(user.getUid()));
                    Map<String, Object> product = new HashMap<>();
                    Date date = new Date();
                    Timestamp productDateUploaded = new Timestamp(date);
//                    List productImage = Arrays.asList(arrayOfImages);
                    product.put("productUserId", user.getUid());
                    product.put("productName", productName);
                    product.put("productDescription", productDescription);
//                    product.put("productImage", productImage);
                    product.put("productCategory", productCategory);
                    product.put("productPrice", Double.parseDouble(productPrice));
                    product.put("productUnit", productUnit);
                    product.put("productQuantity", Integer.parseInt(productQuantity));
                    product.put("productStocks", Integer.parseInt(productStocks));
                    product.put("productSold", 0);
                    product.put("productRating", 0);
                    product.put("productNoCustomerRate", 0);
                    product.put("productDateUploaded", productDateUploaded);

//                    if (FirebaseHelper.addProduct(getContext(), itemSpecs, data)) {
//                        addProductDialog.dismiss();
//                        successAddProductToast.show();
//                    }

                    uploadProduct(product);
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
                    addProductDialog.dismiss();
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
            ImageButton close_ib = addProductPhotoDialog.findViewById(R.id.close_ib);
            close_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addProductPhotoDialog.dismiss();
                }
            });
            camera_ib.setOnClickListener(v1 -> {
                addFromCamera();
                addProductPhotoDialog.dismiss();
            });
            gallery_ib.setOnClickListener(v12 -> {
                addFromGallery();
                addProductPhotoDialog.dismiss();
            });


        });
        farmerProductAdapter = new FarmerProductAdapter(getContext(), productItems);
        farmer_product_lv.setAdapter(farmerProductAdapter);
        return view;
    }

    private void uploadProduct(Map<String, Object> product) {
        add_product_progress.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StorageReference storageRef = storage.getReference();
        Date date = new Date();
        List<String> productImage = new ArrayList();
        for (int i = 0; i < arrayOfImages.size(); i++) {
            int count = i;
            StorageReference imagesRef = storageRef.child("products/" + user.getUid() + "/" + arrayOfImages.get(i).getLastPathSegment() + date);
            UploadTask uploadTask = imagesRef.putFile(arrayOfImages.get(i));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            productImage.add(uri.toString());
                            if (count == arrayOfImages.size() - 1) {
//                               List img = Arrays.asList(productImage);
                                product.put("productImage", productImage);
                                db.collection("products")
                                        .add(product)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                addProductDialog.dismiss();
                                                successAddProductToast.show();
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Some image failed to upload!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    add_product_progress.setProgressCompat(progress, true);
                    Log.d(TAG, "Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Upload is paused");
                }
            });
        }


    }

    public void displayMyProducts() {
        refreshLayout.setRefreshing(true);
        if (last == null) {
            db.collection("products")
                    .whereEqualTo("productUserId", user.getUid())
                    .orderBy("productDateUploaded", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                final int[] i = {0};
                                if (task.getResult().size() > 0) {
                                    no_product_ll.setVisibility(View.GONE);
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    last = task.getResult().getDocuments().get(task.getResult().getDocuments().size()-1);
                                } else {
                                    no_product_ll.setVisibility(View.VISIBLE);
                                    refreshLayout.setVisibility(View.GONE);
                                }
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    ProductItem item = new ProductItem(document);
                                    productItems.append((i[0]++), item);
                                }
                                farmerProductAdapter.notifyDataSetChanged();
                                refreshLayout.setRefreshing(false);
//                                farmerProductAdapter = new FarmerProductAdapter(getContext(), productItems);
//                                farmer_product_lv.setAdapter(farmerProductAdapter);
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else
        {
            db.collection("products")
                    .whereEqualTo("productUserId", user.getUid())
                    .orderBy("productDateAdded", Query.Direction.DESCENDING)
                    .startAfter(last)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                final int[] i = {0};
                                if(task.getResult().size()>0){
                                    no_product_ll.setVisibility(View.GONE);
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    last = task.getResult().getDocuments().get(task.getResult().getDocuments().size()-1);
                                }
                                else {
                                    no_product_ll.setVisibility(View.VISIBLE);
                                    refreshLayout.setVisibility(View.GONE);
                                }
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    ProductItem item = new ProductItem(document);
                                    productItems.append((i[0]++), item);
                                }
                                farmerProductAdapter.notifyDataSetChanged();
                                refreshLayout.setRefreshing(false);
//                                farmerProductAdapter = new FarmerProductAdapter(getContext(), productItems);
//                                farmer_product_lv.setAdapter(farmerProductAdapter);
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

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
        Intent intent = new Intent();
        intent.setType("image/*");
//        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            selectFromGallery.launch(Intent.createChooser(intent, "Select Image(s)"));
        } else {
            Toast.makeText(getActivity(), "There is no app to support this action", Toast.LENGTH_SHORT).show();
        }
    }

    public class FarmerProductAdapter extends BaseAdapter {

        Context context;
        SparseArray<ProductItem> productList;
        LayoutInflater layoutInflater;

        public FarmerProductAdapter(Context context, SparseArray<ProductItem> productList) {
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
                    .load(productList.get(position).getProductImage().get(0))
                    .into(product_image_iv);
            product_name_unit.setText(productList.get(position).getProductName() + " (per " + productList.get(position).getProductQuantity() + " " + productList.get(position).getProductUnit() + ")");
            product_stocks.setText("Stocks: " + productList.get(position).getProductStocks());
            product_sold.setText("Sold: " + productList.get(position).getProductSold());
            product_category.setText("Category: " + productList.get(position).getProductCategory());
            product_price.setText("Price: Php " + productList.get(position).getProductPrice());
            return convertView;
        }
    }

    public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.SlideViewHolder> {

        public ImageViewPagerAdapter() {
        }

        @NonNull
        @Override
        public ImageViewPagerAdapter.SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new ImageViewPagerAdapter.SlideViewHolder(
                    LayoutInflater.from(getContext()).inflate(
                            R.layout.image_slide, parent, false
                    )
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewPagerAdapter.SlideViewHolder holder, int position) {
            Glide.with(getContext())
                    .load(arrayOfImages.get(position))
                    .into(holder.product_image_iv);
            holder.indicator_tv.setText((position + 1) + "/" + arrayOfImages.size());
            holder.remove_image_ib.setOnClickListener(v -> {
                arrayOfImages.remove(position);
                notifyDataSetChanged();
                if (arrayOfImages.size() < 1) {
                    blank_photo_ll.setVisibility(View.VISIBLE);
                    imageSlider.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayOfImages.size();
        }

        public class SlideViewHolder extends RecyclerView.ViewHolder {
            ImageView product_image_iv;
            TextView indicator_tv;
            ImageButton remove_image_ib;

            public SlideViewHolder(@NonNull View itemView) {
                super(itemView);
                product_image_iv = itemView.findViewById(R.id.product_image_iv);
                indicator_tv = itemView.findViewById(R.id.indicator_tv);
                remove_image_ib = itemView.findViewById(R.id.remove_image_ib);
            }
        }
    }


}