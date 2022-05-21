package com.example.agribiz_v100;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.customer.ProductView;
import com.example.agribiz_v100.customer.Profile;
import com.example.agribiz_v100.dialog.AddBarterDialog;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.services.AppManagement;
import com.example.agribiz_v100.services.BarterManagement;
import com.example.agribiz_v100.services.ProfileManagement;
import com.example.agribiz_v100.services.StorageManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.example.agribiz_v100.validation.BarterValidation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BarteredItemViewActivity extends AppCompatActivity {

    BarterModel barterModel;
    ViewPager2 imageSlider;
    TextView item_name_tv, farmer_name_tv, condition_tv, quantity_tv, propose_barter_tv, description_tv;
    ImageView farmer_image_iv;
    ImageViewPagerAdapter imageViewPagerAdapter;
    MaterialToolbar topAppBar;

    Dialog proposeBarterDialog;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String barterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bartered_item_view);

        topAppBar = findViewById(R.id.topAppBar);
        imageViewPagerAdapter = new ImageViewPagerAdapter();
        imageSlider = findViewById(R.id.imageSlider);
        farmer_image_iv = findViewById(R.id.farmer_image_iv);
        item_name_tv = findViewById(R.id.item_name_tv);
        farmer_name_tv = findViewById(R.id.farmer_name_tv);
        condition_tv = findViewById(R.id.condition_tv);
        quantity_tv = findViewById(R.id.quantity_tv);
        propose_barter_tv = findViewById(R.id.propose_barter_tv);
        description_tv = findViewById(R.id.description_tv);
        buildProseDialog();
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getStringExtra("barterId") != null) {
            String barterId = getIntent().getStringExtra("barterId");
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Preparing item, Please wait...");
            progressDialog.show();
            BarterManagement.getBarteredItem(barterId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            barterModel = documentSnapshot.toObject(BarterModel.class);
                            BarteredItemViewActivity.this.barterId = barterModel.getBarterId();

                            ProfileManagement.getUserProfile(barterModel.getBarterUserId())
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            farmer_name_tv.setText(documentSnapshot.getString("userDisplayName").substring(0, documentSnapshot.getString("userDisplayName").length() - 2));
                                            Glide.with(getApplicationContext())
                                                    .load(documentSnapshot.getString("userImage"))
                                                    .into(farmer_image_iv);
                                        }
                                    });
                            item_name_tv.setText(barterModel.getBarterName());
                            condition_tv.setText(barterModel.getBarterCondition());
                            quantity_tv.setText(barterModel.getBarterQuantity() + " " + barterModel.getBarterUnit());
                            imageSlider.setAdapter(imageViewPagerAdapter);
                            description_tv.setText(barterModel.getBarterDescription());
                            propose_barter_tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    proposeBarterDialog.show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(BarteredItemViewActivity.this);
                            alert.setCancelable(false);
                            alert.setTitle("Error");
                            alert.setMessage(e.getLocalizedMessage());
                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface proposeBarterDialog, int which) {
                                    finish();
                                }
                            });
                            alert.show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            progressDialog.dismiss();
                        }
                    });
        } else {
            //finish();
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Error");
            alert.setMessage("Item cannot be found");
            alert.setCancelable(false);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface proposeBarterDialog, int which) {
                    finish();
                }
            });
            alert.show();
        }


    }


    AddBarterDialogCallback addBarterDialogCallback;

    ActivityResultLauncher<Intent> selectFromGallery;

    List<String> arrayOfImages;
    List<Integer> imagesProgress;
    List<String> units;

    ViewPager2 imageSlider1;
    TextView count_done_tv;
    ImageView barter_image_iv;
    AutoCompleteTextView itemCondition_at;
    AutoCompleteTextView itemUnit_at;
    Button cancel_btn, addItem_btn, add_image_btn;

    ImageViewPagerAdapter1 imageViewPagerAdapter1;
    LinearLayout blank_photo_ll;
    RelativeLayout loder_rl;
    TextInputLayout itemName_til, itemCondition_til, itemQuantity_til, description_til;
    private static String[] itemCondition = {"New Harvest", "Excess Harvest - Excellent", "Excess Harvest - Great", "Excess Harvest - Good", " Excess Harvest - Okay"};

    public interface AddBarterDialogCallback {
        void addUploadImagesListener(List<Integer> progress);
    }

    public void addProgressListener(AddBarterDialogCallback addBarterDialogCallBack) {
        this.addBarterDialogCallback = addBarterDialogCallBack;
    }

    public void buildProseDialog() {
        proposeBarterDialog = new Dialog(this);
        proposeBarterDialog.setCancelable(false);
        proposeBarterDialog.setContentView(R.layout.farmer_add_barter_dialog);
        proposeBarterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        arrayOfImages = new ArrayList<>();
        units = new ArrayList<>();
        imageViewPagerAdapter1 = new ImageViewPagerAdapter1();
        AppManagement.getSettings()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (Object object : (List<Object>) (task.getResult().get("units"))) {
                                units.add(object.toString());
                            }
                        } else {
                            Toast.makeText(BarteredItemViewActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // Button references
        cancel_btn = proposeBarterDialog.findViewById(R.id.cancel_btn);
        addItem_btn = proposeBarterDialog.findViewById(R.id.addItem_btn);
        add_image_btn = proposeBarterDialog.findViewById(R.id.add_image_btn);
        // ImageView
        barter_image_iv = proposeBarterDialog.findViewById(R.id.barter_image_iv);
        // AutoCompleteView references
        itemCondition_at = proposeBarterDialog.findViewById(R.id.itemCondition_at);
        itemUnit_at = proposeBarterDialog.findViewById(R.id.itemUnit_at);
        // TextInputLayouts references
        itemName_til = proposeBarterDialog.findViewById(R.id.itemName_til);
        itemCondition_til = proposeBarterDialog.findViewById(R.id.itemCondition_til);
        itemQuantity_til = proposeBarterDialog.findViewById(R.id.itemQuantity_til);
        description_til = proposeBarterDialog.findViewById(R.id.description_til);
        // LinearLayout
        blank_photo_ll = proposeBarterDialog.findViewById(R.id.blank_photo_ll);
        // Relative
        loder_rl = proposeBarterDialog.findViewById(R.id.loder_rl);
        // TextView
        count_done_tv = proposeBarterDialog.findViewById(R.id.count_done_tv);
        // ViewPager2
        imageSlider1 = proposeBarterDialog.findViewById(R.id.imageSlider);
        imageSlider1.setAdapter(imageViewPagerAdapter1);
        // DropDown adapter
        ArrayAdapter<String> itemUnitAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, units);
        ArrayAdapter<String> itemConditionAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, itemCondition);
        itemCondition_at.setAdapter(itemConditionAdapter);
        itemUnit_at.setAdapter(itemUnitAdapter);

        // Progress
        CircularProgressIndicator add_product_progress = proposeBarterDialog.findViewById(R.id.add_product_progress);

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
                double t = (total != 0 ? total / (100 * arrayOfImages.size()) : .01) * 100;
                count_done_tv.setText((int) t + "");
                add_product_progress.setProgressCompat((int) t, true);
            }
        });

        addItem_btn.setOnClickListener(v -> {
            if (error()) {
                loder_rl.setVisibility(View.VISIBLE);
                proposeBarterDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//                barterModel = new BarterModel();
                Date dateNow = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                Timestamp timestamp = new Timestamp(dateNow);

                String userType = String.valueOf(user.getDisplayName().charAt(user.getDisplayName().length() - 1));

                barterModel.setBarterId(user.getUid() + simpleDateFormat.format(dateNow));
                barterModel.setBarterUserId(user.getUid());
                barterModel.setBarterName(itemName_til.getEditText().getText().toString());
                barterModel.setBarterCondition(itemCondition_til.getEditText().getText().toString());
                barterModel.setBarterQuantity(Integer.parseInt(itemQuantity_til.getEditText().getText().toString()));
                barterModel.setBarterUnit(itemUnit_at.getText().toString());
                barterModel.setBarterDescription(description_til.getEditText().getText().toString());
                barterModel.setBarterType(userType);
                barterModel.setBarterDateUploaded(timestamp);
                barterModel.setBarterStatus("Pending");
                barterModel.setBarterMatchId(barterId);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                imagesProgress = new ArrayList<>();
                List<String> uploadedImages = new ArrayList<>();


                for (int k = 0; k < arrayOfImages.size(); k++) {
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
                                if (finalI == arrayOfImages.size() - 1) {
                                    barterModel.setBarterImage(uploadedImages);
                                    BarterManagement.proposeBarterItem(barterId,barterModel, BarteredItemViewActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loder_rl.setVisibility(View.GONE);
                                            proposeBarterDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            if (task.isSuccessful()) {
                                                AuthValidation.successToast(BarteredItemViewActivity.this, "Successfully added barter item").show();
                                                reset();
                                                proposeBarterDialog.dismiss();
                                                finish();
                                            } else {
                                                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(BarteredItemViewActivity.this);
                                                alert.setTitle("Failed Adding Barter Item");
                                                alert.setMessage(task.getException().getLocalizedMessage());
                                                alert.setCancelable(false);
                                                alert.setPositiveButton("Ok", null);
                                                alert.show();
                                                Log.d("BarterManagement", task.getException().getLocalizedMessage());
                                            }
                                        }
                                    });
                                }
                            } else {
                                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(BarteredItemViewActivity.this);
                                alert.setTitle("Failed to upload images");
                                alert.setMessage(task1.getException().getMessage());
                                alert.setCancelable(false);
                                alert.setPositiveButton("Ok", null);
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
        cancel_btn.setOnClickListener(v -> proposeBarterDialog.dismiss());

        selectFromGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData().getClipData() != null) {
                        int i = result.getData().getClipData().getItemCount();
                        for (int n = 0; n < i; n++) {
                            if (arrayOfImages.size() < 3) {
                                Uri imageUri = result.getData().getClipData().getItemAt(n).getUri();
                                arrayOfImages.add(n, imageUri.toString());
                            } else {
                                AuthValidation.failedToast(BarteredItemViewActivity.this, "You reached the maximum upload.").show();
                                break;
                            }
                        }
                        Log.d("BarterManagement", "Checking images...");

                        if (arrayOfImages.size() > 0) {
                            blank_photo_ll.setVisibility(View.GONE);
                            imageSlider1.setVisibility(View.VISIBLE);
                            add_image_btn.setVisibility(View.VISIBLE);
                            imageViewPagerAdapter1.notifyDataSetChanged();
                        } else {
                            blank_photo_ll.setVisibility(View.VISIBLE);
                            add_image_btn.setVisibility(View.GONE);
                        }
                    } else {
                        Uri imageUri = result.getData().getData();
                        arrayOfImages.add(imageUri.toString());
                        if (arrayOfImages.size() > 0) {
                            blank_photo_ll.setVisibility(View.GONE);
                            imageSlider1.setVisibility(View.VISIBLE);
                            imageViewPagerAdapter1.notifyDataSetChanged();
                            add_image_btn.setVisibility(View.VISIBLE);
                        } else {
                            blank_photo_ll.setVisibility(View.VISIBLE);
                            add_image_btn.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    private boolean error() {
        if (TextUtils.isEmpty(itemName_til.getEditText().getText())) {
            AuthValidation.failedToast(proposeBarterDialog.getContext(), "Please specify item name").show();
            return false;
        } else if (TextUtils.isEmpty(itemCondition_til.getEditText().getText())) {
            AuthValidation.failedToast(proposeBarterDialog.getContext(), "Please select item condition").show();
            return false;
        } else if (TextUtils.isEmpty(itemQuantity_til.getEditText().getText())) {
            AuthValidation.failedToast(proposeBarterDialog.getContext(), "Please specify item quantity").show();
            return false;
        } else if (TextUtils.isEmpty(itemUnit_at.getText())) {
            AuthValidation.failedToast(proposeBarterDialog.getContext(), "Please specify item unit").show();
            return false;
        } else if (TextUtils.isEmpty(description_til.getEditText().getText())) {
            AuthValidation.failedToast(proposeBarterDialog.getContext(), "Please enter item description").show();
            return false;
        } else if (arrayOfImages.size() < 1) {
            AuthValidation.failedToast(proposeBarterDialog.getContext(), "Please upload item photo.").show();
            return false;
        }
        return true;
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
        imageViewPagerAdapter1.notifyDataSetChanged();
        add_image_btn.setVisibility(View.GONE);
        blank_photo_ll.setVisibility(View.VISIBLE);
        imageSlider.setVisibility(View.GONE);
    }

    public void addFromGallery() {
        if (arrayOfImages.size() < 3) {
            Log.d("BarterManagement", "Inside add from gallery method");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (intent.resolveActivity(this.getApplicationContext().getPackageManager()) != null) {
                Log.d("BarterManagement", "About to select image...");
                selectFromGallery.launch(Intent.createChooser(intent, "Select Image(s)"));
            } else {
                Log.d("BarterManagement", "Cannot select image...");
                AuthValidation.failedToast(this, "Unable to use gallery").show();
            }
        } else {
            AuthValidation.failedToast(this, "Upload photos must not exceed to 3 images.").show();
        }
    }

    public class ImageViewPagerAdapter extends RecyclerView.Adapter<BarteredItemViewActivity.ImageViewPagerAdapter.SlideViewHolder> {

        @NonNull
        @Override
        public BarteredItemViewActivity.ImageViewPagerAdapter.SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BarteredItemViewActivity.ImageViewPagerAdapter.SlideViewHolder(
                    LayoutInflater.from(getApplicationContext()).inflate(
                            R.layout.product_view_image_slide, parent, false
                    )
            );
        }

        @Override
        public void onBindViewHolder(@NonNull BarteredItemViewActivity.ImageViewPagerAdapter.SlideViewHolder holder, int position) {
            Glide.with(getApplicationContext())
                    .load(barterModel.getBarterImage().get(position))
                    .into(holder.product_image_iv);
            holder.indicator_tv.setText((position + 1) + "/" + barterModel.getBarterImage().size());
        }

        @Override
        public int getItemCount() {
            return barterModel.getBarterImage().size();
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

    public class ImageViewPagerAdapter1 extends RecyclerView.Adapter<ImageViewPagerAdapter1.SlideViewHolder> {

        public ImageViewPagerAdapter1() {
        }

        @NonNull
        @Override
        public ImageViewPagerAdapter1.SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new ImageViewPagerAdapter1
                    .SlideViewHolder(LayoutInflater
                    .from(getApplicationContext()).inflate(R.layout.image_slide, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewPagerAdapter1.SlideViewHolder holder, int position) {
            Glide.with(getApplicationContext().getApplicationContext())
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