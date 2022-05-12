package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.entities.BarterModel;
import com.example.agribiz_v100.services.BarterManagement;
import com.example.agribiz_v100.services.StorageManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.example.agribiz_v100.validation.BarterValidation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class AddBarterDialog {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    BarterModel barterModel;

    Activity activity;
    Fragment fragment;
    Dialog dialog;

    AddBarterDialogCallback addBarterDialogCallback = null;

    Button cancel_btn, addItem_btn;
    ImageView product_image_iv;
    AutoCompleteTextView itemCondition_at;
    TextInputLayout itemName_til, itemCondition_til, itemQuantity_til, description_til;

    private static String[] itemCondition = { "New Harvest", "Excess Harvest - Excellent", "Excess Harvest - Great", "Excess Harvest - Good", " Excess Harvest - Okay" };

    public AddBarterDialog(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.dialog = new Dialog(activity);
        this.fragment = fragment;
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
        // ImageView
        product_image_iv = dialog.findViewById(R.id.product_image_iv);
        // AutoCompleteView references
        itemCondition_at = dialog.findViewById(R.id.itemCondition_at);
        // TextInputLayouts references
        itemName_til = dialog.findViewById(R.id.itemName_til);
        itemCondition_til = dialog.findViewById(R.id.itemCondition_til);
        itemQuantity_til = dialog.findViewById(R.id.itemQuantity_til);
        description_til = dialog.findViewById(R.id.description_til);
        // DropDown adapter
        ArrayAdapter<String> itemConditionAdapter = new ArrayAdapter<>(activity.getBaseContext(), R.layout.dropdown_item, itemCondition);
        itemCondition_at.setAdapter(itemConditionAdapter);

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

        addItem_btn.setOnClickListener(v -> {
            if (error()) {
                barterModel = new BarterModel();

                Date dateNow = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                Timestamp timestamp = new Timestamp(dateNow);

                barterModel.setBarterUserId(user.getUid());
                barterModel.setBarterId(user.getUid() + simpleDateFormat.format(dateNow));
                barterModel.setBarterName(itemName_til.getEditText().getText().toString());
                barterModel.setBarterCondition(itemCondition_til.getEditText().getText().toString());
                barterModel.setBarterQuantity(Integer.parseInt(itemQuantity_til.getEditText().getText().toString()));
                barterModel.setBarterDescription(description_til.getEditText().getText().toString());
                barterModel.setBarterDateUploaded(timestamp);

                Log.d("BarterManagement", "0" + barterModel.getBarterUserId());
                Log.d("BarterManagement", "1" + barterModel.getBarterId());
                Log.d("BarterManagement", "2" + barterModel.getBarterName());
                Log.d("BarterManagement", "3" + barterModel.getBarterCondition());
                Log.d("BarterManagement", "4" + barterModel.getBarterQuantity());
                Log.d("BarterManagement", "5" + barterModel.getBarterDescription());
                Log.d("BarterManagement", "6" + barterModel.getBarterDateUploaded());

                BarterManagement.addBarterItem(barterModel, activity).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AuthValidation.successToast(activity.getBaseContext(), "Successfully added barter item").show();
                            reset();
                            addBarterDialogCallback.addOnDocumentAddedListener(true);
                            dismissDialog();
                        } else {
                            AuthValidation.failedToast(activity.getBaseContext(), task.getException().getLocalizedMessage()).show();
                            Log.d("BarterManagement",task.getException().getLocalizedMessage());
                            task.getException().printStackTrace();
                        }
                    }
                });
            }
        });

        cancel_btn.setOnClickListener(v -> dismissDialog());
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
    }

    private boolean error() {
        if (TextUtils.isEmpty(itemName_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please specify item name").show();
            return false;
        }
        else if (TextUtils.isEmpty(itemCondition_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please select item condition").show();
            return false;
        }
        else if (TextUtils.isEmpty(itemQuantity_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please specify item quantity").show();
            return false;
        }
        else if (TextUtils.isEmpty(description_til.getEditText().getText())) {
            AuthValidation.failedToast(dialog.getContext(), "Please enter item description").show();
            return false;
        }
        return true;
    }

    public interface AddBarterDialogCallback {
        void addOnDocumentAddedListener(boolean isAdded);
    }

    public void createListener(AddBarterDialogCallback addBarterDialogCallback) {
        this.addBarterDialogCallback = addBarterDialogCallback;
    }

}
