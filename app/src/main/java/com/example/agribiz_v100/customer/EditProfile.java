package com.example.agribiz_v100.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.services.AuthManagement;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfile extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView profile_iv;
    EditText userName_et, email_et,number_et, password_et, con_password_et, enter_code;
    TextView editProfile_save_btn;
    RadioGroup radioYesNo;
    MaterialToolbar topAppBar;
    Button verify_code_btn;

    private AuthManagement am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        am = new AuthManagement(this);

        topAppBar=findViewById(R.id.topAppBar);
        profile_iv=findViewById(R.id.profile_iv);
        userName_et = findViewById(R.id.userName_et);
        number_et=findViewById(R.id.number_et);
        email_et=findViewById(R.id.email_et);
        password_et=findViewById(R.id.password_et);
        con_password_et =findViewById(R.id.con_password_et);
        radioYesNo = findViewById(R.id.radioYesNo);
        // Update button
        editProfile_save_btn = findViewById(R.id.editProfile_save_btn);
        verify_code_btn = findViewById(R.id.verify_code_btn);
        enter_code = findViewById(R.id.enter_code);

        userName_et.setText(user.getDisplayName().substring(0,user.getDisplayName().length()-2));
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
                else{
                    password_et.setEnabled(false);
                    con_password_et.setEnabled(false);
                }
            }
        });

        editProfile_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNumber = "+63" + number_et.getText().toString();
                am.updatePhoneNumber(mobileNumber);
            }
        });

        verify_code_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = enter_code.getText().toString();
                am.phoneNumberAuthentication(code);
                // Stop here...
            }
        });


    }
}