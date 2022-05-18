package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.services.AppManagement;
import com.example.agribiz_v100.services.BarterManagement;
import com.example.agribiz_v100.services.StorageManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.example.agribiz_v100.validation.BarterValidation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddBarterDialog {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ActivityResultLauncher<Intent> selectFromGallery;
    BarterModel barterModel;

    Activity activity;
    Fragment fragment;
    Dialog dialog;

    AddBarterDialogCallback addBarterDialogCallback;
    AddBarterDialog.ImageViewPagerAdapter imageViewPagerAdapter;

    List<String> arrayOfImages;
    List<Integer> imagesProgress;
    List<String> units;

    ViewPager2 imageSlider;
    TextView count_done_tv;
    ImageView barter_image_iv;
    AutoCompleteTextView itemCondition_at;
    AutoCompleteTextView itemUnit_at;
    Button cancel_btn, addItem_btn, add_image_btn;

    LinearLayout blank_photo_ll;
    RelativeLayout loder_rl;
    TextInputLayout itemName_til, itemCondition_til, itemQuantity_til, description_til;

    private static String[] itemCondition = {"New Harvest", "Excess Harvest - Excellent", "Excess Harvest - Great", "Excess Harvest - Good", " Excess Harvest - Okay"};
    private String proposeBarterID;

    public AddBarterDialog(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.dialog = new Dialog(activity);
        this.fragment = fragment;
        this.arrayOfImages = new ArrayList<>();
        this.units = new ArrayList<>();
        this.imageViewPagerAdapter = new ImageViewPagerAdapter();
        AppManagement.getSettings()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            for(Object object:(List<Object>) (task.getResult().get("units"))){
                                units.add(object.toString());
                            }
                        }else{
                            Toast.makeText(activity, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public AddBarterDialog(Activity activity, Fragment fragment, String proposeBarterID) {
        this.activity = activity;
        this.dialog = new Dialog(activity);
        this.fragment = fragment;
        this.arrayOfImages = new ArrayList<>();
        this.units = new ArrayList<>();
        this.proposeBarterID = proposeBarterID;
        this.imageViewPagerAdapter = new ImageViewPagerAdapter();

        AppManagement.getSettings()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            for(Object object:(List<Object>) (task.getResult().get("units"))){
                                units.add(object.toString());
                            }
                        }else{
                            Toast.makeText(activity, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public interface AddBarterDialogCallback {
        void addUploadImagesListener(List<Integer> progress);
    }

    public void addProgressListener(AddBarterDialog.AddBarterDialogCallback addBarterDialogCallBack){
        this.addBarterDialogCallback = addBarterDialogCallBack;
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        reset();
        dialog.dismiss();
    }

    public void buildDialog() {
        dialog.setContentView(R.layout.farmer_add_barter_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        // Button references
        cancel_btn = dialog.findViewById(R.id.cancel_btn);
        addItem_btn = dialog.findViewById(R.id.addItem_btn);
        add_image_btn = dialog.findViewById(R.id.add_image_btn);
        // ImageView
        barter_image_iv = dialog.findViewById(R.id.barter_image_iv);
        // AutoCompleteView references
        itemCondition_at = dialog.findViewById(R.id.itemCondition_at);
        itemUnit_at = dialog.findViewById(R.id.itemUnit_at);
        // TextInputLayouts references
        itemName_til = dialog.findViewById(R.id.itemName_til);
        itemCondition_til = dialog.findViewById(R.id.itemCondition_til);
        itemQuantity_til = dialog.findViewById(R.id.itemQuantity_til);
        description_til = dialog.findViewById(R.id.description_til);
        // LinearLayout
        blank_photo_ll = dialog.findViewById(R.id.blank_photo_ll);
        // Relative
        loder_rl = dialog.findViewById(R.id.loder_rl);
        // TextView
        count_done_tv = dialog.findViewById(R.id.count_done_tv);
        // ViewPager2
        imageSlider = dialog.findViewById(R.id.imageSlider);
        imageSlider.setAdapter(imageViewPagerAdapter);
        // DropDown adapter
        ArrayAdapter<String> itemUnitAdapter = new ArrayAdapter<>(activity.getBaseContext(), R.layout.dropdown_item, units);
        ArrayAdapter<String> itemConditionAdapter = new ArrayAdapter<>(activity.getBaseContext(), R.layout.dropdown_item, itemCondition);
        itemCondition_at.setAdapter(itemConditionAdapter);
        itemUnit_at.setAdapter(itemUnitAdapter);

        // Progress
        CircularProgressIndicator add_product_progress = dialog.findViewById(R.id.add_product_progress);

        barter_image_iv.setOnClickListener(v -> {
            Log.d("BarterManagement", "Barter image on click");
            addFromGallery();
        });

        add_image_btn.setOnClickListener(v -> {
            Log.d("BarterManagement", "Clicking additional upload image...");
            addFromGallery();
        });

        itemName_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void afterTextChanged(Editable e) {
                if (!BarterValidation.validateItemName(e.toString()).isEmpty()) {
                    itemName_til.setError(BarterValidation.validateItemName(e.toString()));
                } else {
                    itemName_til.setError(null);
                }
            }
        });

        itemCondition_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void afterTextChanged(Editable ed) {
                if (!BarterValidation.validateCondition(ed.toString()).isEmpty()) {
                    itemCondition_til.setError(BarterValidation.validateCondition(ed.toString()));
                } else {
                    itemCondition_til.setError(null);
                }
            }
        });

        itemQuantity_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void afterTextChanged(Editable edi) {
                if (!BarterValidation.validateQuantity(edi.toString()).isEmpty()) {
                    itemQuantity_til.setError(BarterValidation.validateQuantity(edi.toString()));
                } else {
                    itemQuantity_til.setError(null);
                }
            }
        });

        description_til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this block empty
            }

            @Override
            public void afterTextChanged(Editable edit) {
                if (!BarterValidation.validateDesc(edit.toString()).isEmpty()) {
                    description_til.setError(BarterValidation.validateDesc(edit.toString()));
                } else {
                    description_til.setError(null);
                }
            }
        });

        addProgressListener(new AddBarterDialogCallback() {
            @Override
            public void addUploadImagesListener(List<Integer> progress) {
                int total = 0;
                for (int i : progress) {
                    total += i;
                }
                count_done_tv.setText((total / (100 * arrayOfImages.size())) * 100 + "");
                add_product_progress.setProgressCompat((total / (100 * arrayOfImages.size())) * 100,true);
            }
        });

        addItem_btn.setOnClickListener(v -> {
            if (error()) {
                loder_rl.setVisibility(View.VISIBLE);
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                barterModel = new BarterModel();
                Date dateNow = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                Timestamp timestamp = new Timestamp(dateNow);

                String userType = String.valueOf(user.getDisplayName().charAt(user.getDisplayName().length()-1));

                if (userType.equals("f")) {
                    barterModel.setBarterId(user.getUid() + simpleDateFormat.format(dateNow));
                } else {
                    barterModel.setBarterId(proposeBarterID);
                }

                barterModel.setBarterUserId(user.getUid());
                barterModel.setBarterName(itemName_til.getEditText().getText().toString());
                barterModel.setBarterCondition(itemCondition_til.getEditText().getText().toString());
                barterModel.setBarterQuantity(Integer.parseInt(itemQuantity_til.getEditText().getText().toString()));
                barterModel.setBarterUnit(itemUnit_at.getText().toString());
                barterModel.setBarterDescription(description_til.getEditText().getText().toString());
                barterModel.setBarterStatus("Open");
                barterModel.setBarterType(userType);
                barterModel.setBarterDateUploaded(timestamp);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                imagesProgress = new ArrayList<>();
                List<String> uploadedImages = new ArrayList<>();


                for (int k = 0 ; k < arrayOfImages.size(); k++) {
                    int finalI = k;
                    StorageReference storeRef = storage.getReference().child("barters/" + k);
                    StorageManagement.uploadBarterImages(storeRef, arrayOfImages.get(k)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            Log.d("BarterManagement", "Upload is " + progress + "% done");

                            imagesProgress.add(finalI, (int) progress);
                            addBarterDialogCallback.addUploadImagesListener(imagesProgress);
                        }

                    }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return storeRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task1) {
                            if (task1.isSuccessful()) {
                                String downloadUri = task1.getResult().toString();
                                Log.d("BarterManagement", "uploaded : " + downloadUri);
                                uploadedImages.add(downloadUri);
                                if (finalI == arrayOfImages.size()-1) {
                                    barterModel.setBarterImage(uploadedImages);
                                    barterModel.setBarterStatus("Open");
                                    BarterManagement.addBarterItem(barterModel, activity).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loder_rl.setVisibility(View.GONE);
                                            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            if (task.isSuccessful()) {
                                                AuthValidation.successToast(activity, "Successfully added barter item").show();
                                                reset();
                                                dismissDialog();
                                            } else {
                                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                                alert.setTitle("Failed Adding Barter Item");
                                                alert.setMessage(task.getException().getLocalizedMessage());
                                                alert.setCancelable(false);
                                                alert.setPositiveButton("Ok",null);
                                                alert.show();
                                                Log.d("BarterManagement", task.getException().getLocalizedMessage());
                                            }
                                        }
                                    });
                                }
                            } else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                alert.setTitle("Failed to upload images");
                                alert.setMessage(task1.getException().getMessage());
                                alert.setCancelable(false);
                                alert.setPositiveButton("Ok",null);
                                alert.show();
                            }
                        }
                    });
                }

                Log.d("BarterManagement", "0" + barterModel.getBarterUserId());
                Log.d("BarterManagement", "1" + barterModel.getBarterId());
                Log.d("BarterManagement", "2" + barterModel.getBarterName());
                Log.d("BarterManagement", "3" + barterModel.getBarterCondition());
                Log.d("BarterManagement", "4" + barterModel.getBarterQuantity());
                Log.d("BarterManagement", "5" + barterModel.getBarterDescription());
                Log.d("BarterManagement", "6" + barterModel.getBarterDateUploaded());

            }
        });

        cancel_btn.setOnClickListener(v -> dismissDialog());

        selectFromGallery = fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData().getClipData() != null) {
                        int i = result.getData().getClipData().getItemCount();
                        for (int n = 0; n < i; n++) {
                            if (arrayOfImages.size() < 3) {
                                Uri imageUri = result.getData().getClipData().getItemAt(n).getUri();
                                arrayOfImages.add(n, imageUri.toString());
                            }
                            else {
                                AuthValidation.failedToast(activity, "You reached the maximum upload.").show();
                                break;
                            }
                        }

                        Log.d("BarterManagement", "Checking images...");

                        if (arrayOfImages.size() > 0) {
                            blank_photo_ll.setVisibility(View.GONE);
                            imageSlider.setVisibility(View.VISIBLE);
                            add_image_btn.setVisibility(View.VISIBLE);
                            imageViewPagerAdapter.notifyDataSetChanged();
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
                        } else {
                            blank_photo_ll.setVisibility(View.VISIBLE);
                            add_image_btn.setVisibility(View.GONE);
                        }
                    }
                } else {

                }
            }
        });
    }

    public void reset() {
        itemName_til.getEditText().setText("");
        itemName_til.setError(null);
        itemName_til.clearFocus();
        itemCondition_til.getEditText().setText("");
        itemCondition_til.setError(null);
        itemCondition_til.clearFocus();
        itemQuantity_til.getEditText().setText("");
        itemQuantity_til.setError(null);
        itemQuantity_til.clearFocus();
        description_til.getEditText().setText("");
        description_til.setError(null);
        description_til.clearFocus();
        arrayOfImages = new ArrayList<>();
        imageViewPagerAdapter.notifyDataSetChanged();
        add_image_btn.setVisibility(View.GONE);
        blank_photo_ll.setVisibility(View.VISIBLE);
        imageSlider.setVisibility(View.GONE);
    }

    private boolean error() {
        if (TextUtils.isEmpty(itemName_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please specify item name").show();
            return false;
        } else if (TextUtils.isEmpty(itemCondition_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please select item condition").show();
            return false;
        } else if (TextUtils.isEmpty(itemQuantity_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please specify item quantity").show();
            return false;
        }
        else if (TextUtils.isEmpty(itemUnit_at.getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please specify item unit").show();
            return false;
        }
        else if (TextUtils.isEmpty(description_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please enter item description").show();
            return false;
        } else if (arrayOfImages.size() < 1) {
            AuthValidation.failedToast(dialog.getContext(), "Please upload item photo.").show();
            return false;
        }
        return true;
    }

    public void addFromGallery() {
        if (arrayOfImages.size() < 3) {
            Log.d("BarterManagement", "Inside add from gallery method");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (intent.resolveActivity(activity.getApplicationContext().getPackageManager()) != null) {
                Log.d("BarterManagement", "About to select image...");
                selectFromGallery.launch(Intent.createChooser(intent, "Select Image(s)"));
            } else {
                Log.d("BarterManagement", "Cannot select image...");
                AuthValidation.failedToast(activity, "Unable to use gallery").show();
            }
        }
        else {
            AuthValidation.failedToast(activity, "Upload photos must not exceed to 3 images.").show();
        }
    }

    public class ImageViewPagerAdapter extends RecyclerView.Adapter<AddBarterDialog.ImageViewPagerAdapter.SlideViewHolder> {

        public ImageViewPagerAdapter() {
        }

        @NonNull
        @Override
        public AddBarterDialog.ImageViewPagerAdapter.SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new AddBarterDialog.ImageViewPagerAdapter
                    .SlideViewHolder(LayoutInflater
                    .from(activity.getApplicationContext()).inflate(R.layout.image_slide, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull AddBarterDialog.ImageViewPagerAdapter.SlideViewHolder holder, int position) {
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

}
