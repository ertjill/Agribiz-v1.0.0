package com.example.agribiz_v100.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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

    Activity activity;
    Fragment fragment;
    Dialog dialog;

    Button cancel_btn, addItem_btn;
    AutoCompleteTextView itemCondition_at;
    TextInputLayout itemName_til, itemCondition_til, itemQuantity_til, description_til;

    AddBarterDialogCallback addBarterDialogCallback = null;

    static String[] itemCondition = { "New Harvest", "Excess Harvest - Excellent", "Excess Harvest - Great", "Excess Harvest - Good", " Excess Harvest - Okay" };
    private final boolean[] flag = { false, false, false, false };

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public void createListener(AddBarterDialogCallback addBarterDialogCallback) {
        this.addBarterDialogCallback = addBarterDialogCallback;
    }

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
                    flag[0] = false;
                } else {
                    itemName_til.setError(null);
                    flag[0] = true;
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
                    flag[1] = false;
                } else {
                    itemCondition_til.setError(null);
                    flag[1] = true;
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
                    flag[2] = false;
                } else {
                    itemQuantity_til.setError(null);
                    flag[2] = true;
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
                    flag[3] = false;
                } else {
                    description_til.setError(null);
                    flag[3] = true;
                }
            }
        });

        addItem_btn.setOnClickListener(v -> {
            // add something here
            if (flag[0] && flag[1] && flag[2] && flag[3]) {
                BarterModel barter = new BarterModel();

                Date dateNow = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                Timestamp timestamp = new Timestamp(dateNow);

                barter.setBarterUserId(user.getUid() + simpleDateFormat.format(dateNow));
                barter.setBarterId(user.getUid());
                barter.setBarterName(itemName_til.getEditText().getText().toString());
                barter.setBarterCondition(itemCondition_til.getEditText().getText().toString());
                barter.setBarterQuantity(Integer.parseInt(itemQuantity_til.getEditText().getText().toString()));
                barter.setBarterDescription(description_til.getEditText().getText().toString());
                barter.setBarterDateUploaded(timestamp);

                Log.d("data", "0" + barter.getBarterUserId());
                Log.d("data", "1" + barter.getBarterId());
                Log.d("data", "2" + barter.getBarterName());
                Log.d("data", "3" + barter.getBarterCondition());
                Log.d("data", "4" + barter.getBarterQuantity());
                Log.d("data", "5" + barter.getBarterDescription());
                Log.d("data", "6" + barter.getBarterDateUploaded());

                BarterManagement.addBarterItem(barter).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AuthValidation.successToast(activity.getBaseContext(), "Successfully added barter item").show();

                            FirebaseFirestore db =FirebaseFirestore.getInstance();
                            db.collection("barters").document(barter.getBarterId());
                            addBarterDialogCallback.addOnDocumentAddedListener(true);

                            dismissDialog();
                            reset();
                        } else {
                            AuthValidation.failedToast(activity.getBaseContext(), task.getException().getMessage()).show();
                        }
                    }
                });
            }
        });

        cancel_btn.setOnClickListener(v -> dismissDialog());
    }

    public void reset() {
        // Checks whether the input object reference supplied to it is null
        itemName_til.getEditText().setText("");
        itemName_til.setError(null);
        itemCondition_til.getEditText().setText("");
        itemCondition_til.setError(null);
        itemQuantity_til.getEditText().setText("");
        itemQuantity_til.setError(null);
        description_til.getEditText().setText("");
        description_til.setError(null);
    }

    public interface AddBarterDialogCallback {
        void addOnDocumentAddedListener(boolean isAdded);
    }
}
