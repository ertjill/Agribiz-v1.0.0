package com.example.agribiz_v100.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.agribiz_v100.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfile extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView profile_iv;
    TextView userName_et, email_et,number_et, password_et, con_password_et;
    RadioGroup radioYesNo;
    MaterialToolbar topAppBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        topAppBar=findViewById(R.id.topAppBar);
        profile_iv=findViewById(R.id.profile_iv);
        userName_et = findViewById(R.id.userName_et);
        number_et=findViewById(R.id.number_et);
        email_et=findViewById(R.id.email_et);
        password_et=findViewById(R.id.password_et);
        con_password_et =findViewById(R.id.con_password_et);
        radioYesNo = findViewById(R.id.radioYesNo);

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



    }
}