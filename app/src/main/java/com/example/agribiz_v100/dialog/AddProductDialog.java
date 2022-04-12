package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.customer.EditProfile;
import com.example.agribiz_v100.entities.ProductModel;
import com.example.agribiz_v100.farmer.MyProduct;
import com.example.agribiz_v100.services.ProductManagement;
import com.example.agribiz_v100.services.ProfileManagement;
import com.example.agribiz_v100.services.StorageManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.example.agribiz_v100.validation.ProductValidation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddProductDialog {

    ActivityResultLauncher<Intent> selectFromGallery, selectFromCamera;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    AddProductDialog.ImageViewPagerAdapter imageViewPagerAdapter;
    LinearLayout blank_photo_ll;
    List<String> arrayOfImages;
    ViewPager2 imageSlider;
    Button add_image_btn;
    Activity activity;
    Fragment fragment;
    RelativeLayout loder_rl;
    AddProductImageCallBack addProductImageCallBack;
    private boolean flag[] = {false, false, false, false, false, false, false, false};
    boolean productNameFlag=false;
    Dialog dialog;
    AddProductDialogCallback addProductDialogCallback = null;
    TextInputLayout productDescription_til;
    TextInputLayout productCategory_til;
    TextInputLayout productQuantity_til;
    TextInputLayout productStocks_til;
    TextInputLayout productPrice_til;
    TextInputLayout productName_til;
    TextInputLayout productUnit_til;
    AutoCompleteTextView productCategory_at;
    AutoCompleteTextView productUnit_at;
    Button add_product_btn;
    Button cancel_btn;
    TextView count_done_tv;

    static String category[] = {"Fruits", "Vegetables", "Livestock", "Poultry", "Fertilizer"};
    static String unit[] = {"kg", "g", "ml", "pcs", "L"};

    public void createListener(AddProductDialogCallback addProductDialogCallback) {
        this.addProductDialogCallback = addProductDialogCallback;
    }

    public AddProductDialog(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.dialog = new Dialog(activity);
        this.fragment = fragment;
        arrayOfImages = new ArrayList<>();
    }
    public void createImageSizeListener(AddProductImageCallBack addProductImageCallBack){
        this.addProductImageCallBack = addProductImageCallBack;
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        reset();
        dialog.dismiss();
    }

    public void buildDialog() {

        dialog.setContentView(R.layout.farmer_add_new_product_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        count_done_tv =dialog.findViewById(R.id.count_done_tv);
        CircularProgressIndicator add_product_progress = dialog.findViewById(R.id.add_product_progress);
        ImageView product_image_iv = dialog.findViewById(R.id.product_image_iv);
        blank_photo_ll = dialog.findViewById(R.id.blank_photo_ll);
        add_image_btn = dialog.findViewById(R.id.add_image_btn);
        imageSlider = dialog.findViewById(R.id.imageSlider);
        loder_rl = dialog.findViewById(R.id.loder_rl);
        productDescription_til = dialog.findViewById(R.id.productDescription_til);
        productCategory_til = dialog.findViewById(R.id.productCategory_til);
        productQuantity_til = dialog.findViewById(R.id.productQuantity_til);
        productStocks_til = dialog.findViewById(R.id.productStocks_til);
        productPrice_til = dialog.findViewById(R.id.productPrice_til);
        productName_til = dialog.findViewById(R.id.productName_til);
        productUnit_til = dialog.findViewById(R.id.productUnit_til);
        productCategory_at = dialog.findViewById(R.id.productCategory_at);
        productUnit_at = dialog.findViewById(R.id.productUnit_at);
        add_product_btn = dialog.findViewById(R.id.add_product_btn);
        cancel_btn = dialog.findViewById(R.id.cancel_btn);

        imageViewPagerAdapter = new AddProductDialog.ImageViewPagerAdapter();
        imageSlider.setAdapter(imageViewPagerAdapter);
        ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<String>(activity.getBaseContext(), R.layout.dropdown_item, category);
        ArrayAdapter<String> productUnitAdapter = new ArrayAdapter<String>(activity.getBaseContext(), R.layout.dropdown_item, unit);
        productCategory_at.setAdapter(productCategoryAdapter);
        productUnit_at.setAdapter(productUnitAdapter);
        createImageSizeListener(new AddProductImageCallBack() {
            @Override
            public void addOnImageSizeListener(int imagesSize) {
                if(imagesSize>0){
                    flag[0]=true;
                }
                else
                    flag[0]=false;
            }
        });
        productName_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ProductValidation.validateProductName(s.toString()).equals("")) {
                    productName_til.setError(ProductValidation.validateProductName(s.toString()));
                    flag[1] = false;
                } else {
                    productName_til.setError(null);

                    flag[1] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        productDescription_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!ProductValidation.validateDesc(s.toString()).isEmpty()){
                    productDescription_til.setError(ProductValidation.validateDesc(s.toString()));

                    flag[2]=false;
                }
                else{
                    productDescription_til.setError(null);
                    flag[2]=true;
                }
            }
        });
        productCategory_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!ProductValidation.validateCategory(s.toString()).isEmpty()){
                    productCategory_til.setError(ProductValidation.validateCategory(s.toString()));
                    flag[3] =false;
                }{
                    productCategory_til.setError(null);
                    flag[3] = true;
                }
            }
        });
        productStocks_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!ProductValidation.validateStocks(s.toString()).isEmpty()){
                    productStocks_til.setError(ProductValidation.validateStocks(s.toString()));
                    flag[4] =false;
                }else {
                    productStocks_til.setError(null);
                    flag[4]=true;
                }
            }
        });
        productPrice_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!ProductValidation.validatePrice(s.toString()).isEmpty())
                {
                    productPrice_til.setError(ProductValidation.validatePrice(s.toString()));
                    flag[5] =false;
                }
                else{
                    productPrice_til.setError(null);
                    flag[5]=true;
                }
            }
        });
        productQuantity_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!ProductValidation.validateQuantity(s.toString()).isEmpty()){
                    productQuantity_til.setError(ProductValidation.validateQuantity(s.toString()));
                    flag[6]=false;
                }
                else{
                    productQuantity_til.setError(null);
                    flag[6]=true;
                }
            }
        });
        productUnit_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!ProductValidation.validateUnit(s.toString()).isEmpty()){
                    productUnit_til.setError(ProductValidation.validateUnit(s.toString()));
                    flag[7] = false;
                }else{
                    productUnit_til.setError(null);
                    flag[7] =true;
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissDialog();
            }
        });

        product_image_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadDialog();
            }
        });
        add_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUploadDialog();
            }
        });

        add_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag[0]&&flag[1]&&flag[2]&&flag[3]&&flag[4]&&flag[5]&&flag[6]&&flag[7]){
                    loder_rl.setVisibility(View.VISIBLE);
                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                ProductModel product = new ProductModel();
                Date dateNow = new Date();
                Log.d("date", dateNow.toString());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                Timestamp timestamp = new Timestamp(dateNow);
                String productName = productName_til.getEditText().getText().toString();
                String productDescription = productDescription_til.getEditText().getText().toString();
                String productCategory = productCategory_til.getEditText().getText().toString();
                String productStocks = productStocks_til.getEditText().getText().toString();
                String productPrice = productPrice_til.getEditText().getText().toString();
                String productQuantity = productQuantity_til.getEditText().getText().toString();
                String productUnit = productUnit_til.getEditText().getText().toString();

                    product.setProductId(user.getUid() + simpleDateFormat.format(dateNow));
                    product.setProductUserId(user.getUid());
                    product.setProductName(productName);
                    product.setProductDescription(productDescription);
                    product.setProductCategory(productCategory);
                    product.setProductStocks(Integer.parseInt(productStocks));
                    product.setProductPrice(Double.parseDouble(productPrice));
                    product.setProductQuantity(Integer.parseInt(productQuantity));
                    product.setProductUnit(productUnit);
                    product.setProductImage(new ArrayList<>());
                    product.setProductDateUploaded(timestamp);

                    Log.d("data", "0" + product.getProductId());
                    Log.d("data", "1" + product.getProductUserId());
                    Log.d("data", "2" + product.getProductName());
                    Log.d("data", "3" + product.getProductDescription());
                    Log.d("data", "4" + product.getProductCategory());
                    Log.d("data", "5" + product.getProductStocks() + "");
                    Log.d("data", "6" + product.getProductPrice() + "");
                    Log.d("data", "7" + product.getProductQuantity() + "");
                    Log.d("data", "8" + product.getProductUnit());
                    Log.d("data", "9" + product.getProductDateUploaded().toString());
                    Log.d("data", "10" + product.getProductImage().size() + "");

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    ProductManagement.addProduct(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int i = 0; i < arrayOfImages.size(); i++) {
                                    Uri file = Uri.parse(arrayOfImages.get(i));
                                    StorageReference ref = storageRef.child("products").child(product.getProductId()).child(String.valueOf(i));
                                    int finalI = i;
                                    StorageManagement.uploadProductImage(ref, file).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                            Log.d("tag", "Upload is " + progress + "% done");
                                            add_product_progress.setProgressCompat( (int)progress,true);
                                            count_done_tv.setText((int)progress+"");
                                        }
                                    }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()) {
                                                throw task.getException();
                                            }
                                            // Continue with the task to get the download URL
                                            return ref.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                String downloadUri = task.getResult().toString();
                                                Log.d("imagess", "uploaded : " + downloadUri);
                                                product.getProductImage().add(downloadUri);
                                                if (finalI == arrayOfImages.size()-1) {

                                                    AuthValidation.successToast(activity.getBaseContext(), "Successfully added product").show();
                                                    FirebaseFirestore db =FirebaseFirestore.getInstance();
                                                    db.collection("products").document(product.getProductId())
                                                            .update("productImage", product.getProductImage());
                                                    loder_rl.setVisibility(View.GONE);
                                                    reset();
                                                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    addProductDialogCallback.addOnDocumentAddedListener(true);

                                                    dismissDialog();
                                                }
                                            } else {
                                                AuthValidation.failedToast(activity.getBaseContext(),"Failed to upload Product Images").show();
                                            }
                                        }
                                    });
                                }

                            } else {
                                AuthValidation.failedToast(activity.getBaseContext(), task.getException().getMessage()).show();
                            }
                        }
                    });



                }
                else{
                    AuthValidation.failedToast(activity.getBaseContext(),"Some are false").show();
                    Log.d("flag",
                            Boolean.toString(flag[0])+" : photo "+
                            Boolean.toString(flag[1])+" : name "+
                            Boolean.toString(flag[2])+" : desc "+
                            Boolean.toString(flag[3])+" : cat "+
                            Boolean.toString(flag[4])+" : stocks "+
                            Boolean.toString(flag[5])+" : price "+
                            Boolean.toString(flag[6])+" : quantity "+
                            Boolean.toString(flag[7])+" : unit ");
                }
            }
        });

        selectFromGallery = fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData().getClipData() != null) {
                        int i = result.getData().getClipData().getItemCount();
                        for (int n = 0; n < i; n++) {
                            Uri imageUri = result.getData().getClipData().getItemAt(n).getUri();
                            arrayOfImages.add(imageUri.toString());

                        }
                        Log.d("GL", "aDD mORE");

                        if (arrayOfImages.size() > 0) {
                            blank_photo_ll.setVisibility(View.GONE);
                            imageSlider.setVisibility(View.VISIBLE);
                            add_image_btn.setVisibility(View.VISIBLE);
                            imageViewPagerAdapter.notifyDataSetChanged();
                            addProductImageCallBack.addOnImageSizeListener(arrayOfImages.size());
                        } else {
                            blank_photo_ll.setVisibility(View.VISIBLE);
                            add_image_btn.setVisibility(View.GONE);
                        }
                    } else {
                        Uri imageUri = result.getData().getData();
                        arrayOfImages.add(imageUri.toString());
                        if (arrayOfImages.size() > 0) {
                            blank_photo_ll.setVisibility(View.GONE);
                            imageSlider.setVisibility(View.VISIBLE);
                            imageViewPagerAdapter.notifyDataSetChanged();
                            add_image_btn.setVisibility(View.VISIBLE);
                            addProductImageCallBack.addOnImageSizeListener(arrayOfImages.size());
                        } else {
                            blank_photo_ll.setVisibility(View.VISIBLE);
                            add_image_btn.setVisibility(View.GONE);
                        }
                    }
                } else {
                }
            }
        });
        selectFromCamera = fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bitmap imBtm = (Bitmap) result.getData().getExtras().get("data");
                    ByteArrayOutputStream byts = new ByteArrayOutputStream();
                    imBtm.compress(Bitmap.CompressFormat.JPEG, 100, byts);
                    String path = MediaStore.Images.Media.insertImage(activity.getBaseContext().getContentResolver(), imBtm, "val" + arrayOfImages.size(), null);

                    Log.d("Camera", path);
                    Uri image = Uri.parse(path);
                    arrayOfImages.add(image.toString());
                    if (arrayOfImages.size() > 0) {
                        blank_photo_ll.setVisibility(View.GONE);
                        imageSlider.setVisibility(View.VISIBLE);
                        imageViewPagerAdapter.notifyDataSetChanged();

                    } else
                        blank_photo_ll.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void reset() {

        productName_til.getEditText().setText("");
        productName_til.setError(null);
        productDescription_til.getEditText().setText("");
        productDescription_til.setError(null);
        productCategory_til.getEditText().setText("");
        productCategory_til.setError(null);
        productStocks_til.getEditText().setText("");
        productStocks_til.setError(null);
        productPrice_til.getEditText().setText("");
        productPrice_til.setError(null);
        productQuantity_til.getEditText().setText("");
        productQuantity_til.setError(null);
        productUnit_til.getEditText().setText("");
        productUnit_til.setError(null);

        arrayOfImages = new ArrayList<>();
        imageViewPagerAdapter.notifyDataSetChanged();
        add_image_btn.setVisibility(View.GONE);
        blank_photo_ll.setVisibility(View.VISIBLE);
        imageSlider.setVisibility(View.GONE);
    }

    private void showUploadDialog() {
        Dialog uploadPhotoDialog = new Dialog(activity);
        uploadPhotoDialog.setContentView(R.layout.upload_picture_dialog);
        uploadPhotoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        uploadPhotoDialog.setCancelable(false);

        ImageButton camera_ib = uploadPhotoDialog.findViewById(R.id.camera_ib);
        ImageButton gallery_ib = uploadPhotoDialog.findViewById(R.id.gallery_ib);
        ImageButton close_ib = uploadPhotoDialog.findViewById(R.id.close_ib);
        TextView upload_tv = uploadPhotoDialog.findViewById(R.id.upload_tv);

        upload_tv.setText("Upload profile photo");

        close_ib.setOnClickListener(v -> {
            uploadPhotoDialog.dismiss();
        });
        camera_ib.setOnClickListener(v1 -> {
            addFromCamera();
            uploadPhotoDialog.dismiss();
        });
        gallery_ib.setOnClickListener(v12 -> {
            addFromGallery();
            uploadPhotoDialog.dismiss();
        });
        uploadPhotoDialog.show();
    }

    public void addFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getBaseContext().getPackageManager()) != null) {
            selectFromCamera.launch(intent);
        } else {
            AuthValidation.failedToast(activity.getBaseContext(), "Unable to use camera").show();
        }
    }

    public void addFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(activity.getApplicationContext().getPackageManager()) != null) {
            selectFromGallery.launch(Intent.createChooser(intent, "Select Image(s)"));
        } else {
            AuthValidation.failedToast(activity.getBaseContext(), "Unable to use gallery").show();
        }
    }

    public class ImageViewPagerAdapter extends RecyclerView.Adapter<AddProductDialog.ImageViewPagerAdapter.SlideViewHolder> {

        public ImageViewPagerAdapter() {
        }

        @NonNull
        @Override
        public AddProductDialog.ImageViewPagerAdapter.SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new AddProductDialog.ImageViewPagerAdapter.SlideViewHolder(
                    LayoutInflater.from(activity.getApplicationContext()).inflate(
                            R.layout.image_slide, parent, false
                    )
            );
        }

        @Override
        public void onBindViewHolder(@NonNull AddProductDialog.ImageViewPagerAdapter.SlideViewHolder holder, int position) {
            Glide.with(activity.getApplicationContext())
                    .load(arrayOfImages.get(position))
                    .into(holder.product_image_iv);
            holder.indicator_tv.setText((position + 1) + "/" + arrayOfImages.size());
            holder.remove_image_ib.setOnClickListener(v -> {
                arrayOfImages.remove(position);
                notifyDataSetChanged();
                if (arrayOfImages.size() < 1) {
                    blank_photo_ll.setVisibility(View.VISIBLE);
                    imageSlider.setVisibility(View.GONE);
                    add_image_btn.setVisibility(View.GONE);
                }
                addProductImageCallBack.addOnImageSizeListener(arrayOfImages.size());
            });
        }

        @Override
        public int getItemCount() {
            return arrayOfImages.size();
        }

        public class SlideViewHolder extends RecyclerView.ViewHolder {
            TextView indicator_tv;
            ImageView product_image_iv;
            ImageButton remove_image_ib;

            public SlideViewHolder(@NonNull View itemView) {
                super(itemView);
                indicator_tv = itemView.findViewById(R.id.indicator_tv);
                remove_image_ib = itemView.findViewById(R.id.remove_image_ib);
                product_image_iv = itemView.findViewById(R.id.product_image_iv);
            }
        }
    }

    public interface AddProductDialogCallback {
        void addOnDocumentAddedListener(boolean isAdded);


    }
    public interface AddProductImageCallBack{
        void addOnImageSizeListener(int imagesSize);
    }

}
