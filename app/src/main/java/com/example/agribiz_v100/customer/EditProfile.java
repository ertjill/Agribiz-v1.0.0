package com.example.agribiz_v100.customer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agribiz_v100.R;
import com.example.agribiz_v100.services.AuthManagement;
import com.example.agribiz_v100.services.ProfileManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;

public class EditProfile extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ActivityResultLauncher<Intent> selectFromGallery;
    Dialog uploadPhotoDialog;

    Uri imageUri;

    ImageView profile_iv;
    ImageButton edit_image_ib;
    Button change_password_btn;
    EditText username_et, email_et,number_et, password_et, con_password_et;
    TextInputLayout username_til, email_til, number_til, password_til, con_password_til;
    LinearLayout change_num_ll, update_num_ll,cancel_update_ll,username_cancel_update_ll;
    RadioGroup radioYesNo;
    MaterialToolbar topAppBar;
    ImageButton email_edit_ib, email_cancel_ib, email_update_ib,username_edit_ib, username_cancel_ib, username_update_ib;

    private AuthManagement am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //
        am = new AuthManagement(this);
        uploadPhotoDialog = new Dialog(this);
        //
        topAppBar=findViewById(R.id.topAppBar);
        //
        profile_iv=findViewById(R.id.profile_iv);
        Glide.with(getApplicationContext())
                .load(user.getPhotoUrl())
                .circleCrop()
                .into(profile_iv);
        username_et = findViewById(R.id.username_et);
        number_et=findViewById(R.id.number_et);
        email_et=findViewById(R.id.email_et);
        password_et=findViewById(R.id.password_et);
        con_password_et =findViewById(R.id.con_password_et);
        radioYesNo = findViewById(R.id.radioYesNo);
        //
        cancel_update_ll = findViewById(R.id.cancel_update_ll);
        username_cancel_update_ll = findViewById(R.id.username_cancel_update_ll);
        //
        username_til = findViewById(R.id.userName_til);
        email_til= findViewById(R.id.email_til);
        number_til = findViewById(R.id.number_til);
        //
        email_edit_ib = findViewById(R.id.email_edit_ib);
        email_cancel_ib = findViewById(R.id.email_cancel_ib);
        email_update_ib = findViewById(R.id.email_update_ib);
        //
        username_edit_ib = findViewById(R.id.username_edit_ib);
        username_cancel_ib = findViewById(R.id.username_cancel_ib);
        username_update_ib = findViewById(R.id.username_update_ib);
        //
        edit_image_ib = findViewById(R.id.edit_image_ib);

        // Change profile photo
        edit_image_ib.setOnClickListener(v -> {
            showUploadDialog();

        });

        email_edit_ib.setOnClickListener(v->{
            cancel_update_ll.setVisibility(View.VISIBLE);
            email_edit_ib.setVisibility(View.GONE);
            email_et.setEnabled(true);
            email_et.requestFocus();
        });
        email_cancel_ib.setOnClickListener(v->{
            cancel_update_ll.setVisibility(View.GONE);
            email_edit_ib.setVisibility(View.VISIBLE);
            email_et.setText(user.getEmail());
            email_til.setError(null);
            email_et.setEnabled(false);
        });
        email_update_ib.setOnClickListener(v->{
            email_update_ib.setEnabled(false);
            String email = email_et.getText().toString();
            if(TextUtils.isEmpty(AuthValidation.validateEmail(email))) {
                am.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            cancel_update_ll.setVisibility(View.GONE);
                            email_edit_ib.setVisibility(View.VISIBLE);
                            email_et.setEnabled(false);
                            email_til.setError(null);
                            AuthValidation.successToast(getApplicationContext(),"Successfully updated email").show();
                        }
                        else {
                            email_til.setError(task.getException().getMessage());
                        }
                        email_update_ib.setEnabled(true);
                    }
                });
            }
            else{
                email_til.setError(AuthValidation.validateEmail(email));
            }
        });

        username_edit_ib.setOnClickListener(v->{
            username_cancel_update_ll.setVisibility(View.VISIBLE);
            username_edit_ib.setVisibility(View.GONE);
            username_et.setEnabled(true);
            username_et.requestFocus();
        });
        username_cancel_ib.setOnClickListener(v->{
            username_cancel_update_ll.setVisibility(View.GONE);
            username_edit_ib.setVisibility(View.VISIBLE);
            username_et.setText(user.getDisplayName().substring(0,user.getDisplayName().length()-2));
            username_til.setError(null);
            username_et.setEnabled(false);
        });
        username_update_ib.setOnClickListener(v->{
            username_update_ib.setEnabled(false);
            String type = user.getDisplayName().substring(user.getDisplayName().length()-1);
            String username = username_et.getText().toString()+"-"+type;
            if(!TextUtils.isEmpty(username)) {
                am.updateUsername(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            username_cancel_update_ll.setVisibility(View.GONE);
                            username_edit_ib.setVisibility(View.VISIBLE);
                            username_et.setEnabled(false);
                            username_til.setError(null);
                            AuthValidation.successToast(getApplicationContext(), "Successfully updated username").show();
                        } else {
                            username_til.setError(task.getException().getMessage());
                        }
                        username_update_ib.setEnabled(true);
                    }
                });
            }else{
                username_til.setError("Enter valid Username");
            }
        });

        username_et.setText(user.getDisplayName().substring(0,user.getDisplayName().length()-2));
        number_et.setText(user.getPhoneNumber());
        email_et.setText(user.getEmail());

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        change_password_btn = findViewById(R.id.change_password_btn);
        password_til = findViewById(R.id.password_til);
        con_password_til = findViewById(R.id.con_password_til);

        radioYesNo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioYes){
                    password_et.setEnabled(true);
                    con_password_et.setEnabled(true);
                    change_password_btn.setVisibility(View.VISIBLE);
                }
                else {
                    password_et.setEnabled(false);
                    con_password_et.setEnabled(false);
                    change_password_btn.setVisibility(View.GONE);
                    password_til.setError(null);
                    con_password_til.setError(null);
                }
            }
        });

        change_password_btn.setOnClickListener(view -> {
            String newPassword = password_et.getText().toString();
            String confirmPassword = con_password_et.getText().toString();

            password_til.setError(null);
            con_password_til.setError(null);

            if(TextUtils.isEmpty(newPassword)) {
                password_til.setError("Required password");
            }
            else if (!newPassword.equals(confirmPassword)) {
                con_password_til.setError("Passwords do not match");
                // AuthValidation.failedToast(getApplicationContext(), "Passwords do not match").show();
            }
            else {
                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EditProfile", "User password updated.");
                            AuthValidation.successToast(getApplicationContext(), "Successfully updated password").show();
                            password_et.setFocusable(false);
                            con_password_et.setFocusable(false);
                            password_til.setError(null);
                            con_password_til.setError(null);
                            radioYesNo.check(R.id.radioNo);
                            // stop here
                        }
                        else {
                            Log.d("EditProfile", "Password not updated.");
                            password_til.setError(task.getException().getMessage());
                            con_password_til.setError(task.getException().getMessage());
                        }
                    }
                });
            }
        });

        number_til.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog verificationDialog = new Dialog(EditProfile.this);
                verificationDialog.setContentView(R.layout.enter_verification_code_dialog);
                verificationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                verificationDialog.setCancelable(false);
                update_num_ll = verificationDialog.findViewById(R.id.update_num_ll);
                change_num_ll = verificationDialog.findViewById(R.id.change_num_ll);
                Button change_btn, update_btn;
                EditText new_mobile_num, char_code_1,char_code_2,char_code_3,char_code_4,char_code_5,char_code_6;
                TextView populatePhoneNumber;

                char_code_1 = verificationDialog.findViewById(R.id.char_code_1);
                char_code_2 = verificationDialog.findViewById(R.id.char_code_2);
                char_code_3 = verificationDialog.findViewById(R.id.char_code_3);
                char_code_4 = verificationDialog.findViewById(R.id.char_code_4);
                char_code_5 = verificationDialog.findViewById(R.id.char_code_5);
                char_code_6 = verificationDialog.findViewById(R.id.char_code_6);

                change_btn = verificationDialog.findViewById(R.id.change_btn);
                new_mobile_num = verificationDialog.findViewById(R.id.new_mobile_num);
                update_btn = verificationDialog.findViewById(R.id.update_btn);
                populatePhoneNumber = verificationDialog.findViewById(R.id.populatePhoneNumber);
                ImageButton close_ib;

                close_ib = verificationDialog.findViewById(R.id.close_ib);
                close_ib.setOnClickListener(v ->{
                    verificationDialog.dismiss();
                });

                change_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String phoneNo = "+63"+new_mobile_num.getText().toString();
                        if(AuthValidation.validatePhoneNumber(phoneNo)){
                            am.verifyPhoneNumber(phoneNo);
                            change_num_ll.setVisibility(View.GONE);
                            update_num_ll.setVisibility(View.VISIBLE);
                            populatePhoneNumber.setText(phoneNo);
                        }
                        else{
                            new_mobile_num.setError("Enter a valid mobile number.");
                        }
                    }
                });

                update_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String code1 = char_code_1.getText().toString();
                        String code2 = char_code_2.getText().toString();
                        String code3 = char_code_3.getText().toString();
                        String code4 = char_code_4.getText().toString();
                        String code5 = char_code_5.getText().toString();
                        String code6 = char_code_6.getText().toString();
                        String code = code1+code2+code3+code4+code5+code6;
                        am.updatePhoneNumber(code).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Log.d("EditProfile", "Updated phone number");
                                    Toast.makeText(EditProfile.this, "You successfully updated your phone number.", Toast.LENGTH_SHORT).show();
                                    verificationDialog.dismiss();
                                }
                                else {
                                    Log.d("EditProfile", "Failed to update phone number");
                                    Toast.makeText(EditProfile.this, "Failed to update phone number.", Toast.LENGTH_SHORT).show();
                                    verificationDialog.dismiss();
                                    change_num_ll.setVisibility(View.VISIBLE);
                                    update_num_ll.setVisibility(View.GONE);
                                    new_mobile_num.requestFocus();
                                }
                            }
                        });
                    }
                });
                verificationDialog.show();
            }
        });
    }

    private void showUploadDialog() {
        Dialog uploadPhotoDialog = new Dialog(this);
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
            // addFromCamera();
            // addProductPhotoDialog.dismiss();
        });
        gallery_ib.setOnClickListener(v12 -> {
            pickFromGallery();
            uploadPhotoDialog.dismiss();
        });
        uploadPhotoDialog.show();
    }

    private void pickFromGallery() {
        CropImage.activity().start(EditProfile.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Log.d("UploadImage", imageUri.toString());
                Glide.with(getApplicationContext())
                        .load(imageUri)
                        .circleCrop()
                        .into(profile_iv);
                ProfileManagement pm=new ProfileManagement();
                pm.updateImage(this, imageUri, user);
            }
        }
    }

}