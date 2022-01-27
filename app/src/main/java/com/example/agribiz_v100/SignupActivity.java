package com.example.agribiz_v100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    EditText editText_usernameCustomer, editText_numberCustomer, editText_emailCustomer, editText_passwordCustomer, editText_conPasswordCustomer;
    Button signupCustomer_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        editText_usernameCustomer = findViewById(R.id.editText_usernameCustomer);
        editText_numberCustomer = findViewById(R.id.editText_numberCustomer);
        editText_emailCustomer =findViewById(R.id.editText_emailCustomer);
        editText_passwordCustomer = findViewById(R.id.editText_passwordCustomer);
        editText_conPasswordCustomer = findViewById(R.id.editText_conPasswordCustomer);
        signupCustomer_btn = findViewById(R.id.signupCustomer_btn);

        signupCustomer_btn.setOnClickListener(v -> {
            String username = editText_usernameCustomer.getText().toString();
            String phone = "+63"+editText_numberCustomer.getText().toString().trim();
            String email =editText_emailCustomer.getText().toString().trim();
            String password = editText_passwordCustomer.getText().toString().trim();
            String confirm_password = editText_conPasswordCustomer.getText().toString().trim();

            if(TextUtils.isEmpty(username)){
                Toast.makeText(getApplicationContext(), "Username should not be empty!", Toast.LENGTH_SHORT).show();
            }
            else if(!Verification.verifyUsername(username)){
                Toast.makeText(getApplicationContext(), "Username should be at least 6 characters", Toast.LENGTH_SHORT).show();
            }

            else if(TextUtils.isEmpty(phone)){
                Toast.makeText(getApplicationContext(), "Phone number should not be empty!", Toast.LENGTH_SHORT).show();
            }
            else if(!Verification.verifyPhone(phone)){
                Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(email)){
                Toast.makeText(getApplicationContext(), "Email should not be empty!", Toast.LENGTH_SHORT).show();
            }
            else if(!Verification.verifyEmail(email)){
                Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(password)){
                Toast.makeText(getApplicationContext(), "Password should not be empty!", Toast.LENGTH_SHORT).show();
            }
            else if(!Verification.verifyPassword(password)){
                Toast.makeText(getApplicationContext(), "Password must at least 8 characters with at least one digit, special character[@#$%^&+=()], uppercase and lower case letter!", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(confirm_password) || !Verification.verifyConfirmPassword(password,confirm_password)){
                Toast.makeText(getApplicationContext(), "Confirm password must match with the password!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Create an account", Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(getApplicationContext(), VerificationCodeActivity.class);
                intent.putExtra("phoneNo",phone);
                intent.putExtra("email",email);
                intent.putExtra("password",password);
                intent.putExtra("displayName",username);
                startActivity(intent);
            }
        });
    }
    public void goToLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}