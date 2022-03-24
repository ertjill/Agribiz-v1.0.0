package com.example.agribiz_v100.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.services.AuthManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfile extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView profile_iv;
    EditText username_et, email_et,number_et, password_et, con_password_et, enter_code;
    TextInputLayout username_til, email_til, number_til;
    LinearLayout change_num_ll, update_num_ll,cancel_update_ll,username_cancel_update_ll;
    RadioGroup radioYesNo;
    MaterialToolbar topAppBar;
    ImageButton email_edit_ib, email_cancel_ib, email_update_ib,username_edit_ib, username_cancel_ib, username_update_ib;

    private AuthManagement am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        am = new AuthManagement(this);


        topAppBar=findViewById(R.id.topAppBar);
        profile_iv=findViewById(R.id.profile_iv);
        username_et = findViewById(R.id.username_et);
        number_et=findViewById(R.id.number_et);
        email_et=findViewById(R.id.email_et);
        password_et=findViewById(R.id.password_et);
        con_password_et =findViewById(R.id.con_password_et);
        radioYesNo = findViewById(R.id.radioYesNo);
        cancel_update_ll = findViewById(R.id.cancel_update_ll);
        username_cancel_update_ll = findViewById(R.id.username_cancel_update_ll);
        //
        username_til = findViewById(R.id.userName_til);
        email_til= findViewById(R.id.email_til);
        number_til = findViewById(R.id.number_til);

        email_edit_ib = findViewById(R.id.email_edit_ib);
        email_cancel_ib = findViewById(R.id.email_cancel_ib);
        email_update_ib = findViewById(R.id.email_update_ib);

        username_edit_ib = findViewById(R.id.username_edit_ib);
        username_cancel_ib = findViewById(R.id.username_cancel_ib);
        username_update_ib = findViewById(R.id.username_update_ib);

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
            //stop here...
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

        radioYesNo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioYes){
                    password_et.setEnabled(true);
                    con_password_et.setEnabled(true);
                }
                else {
                    password_et.setEnabled(false);
                    con_password_et.setEnabled(false);
                }
            }
        });

        username_til.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

}