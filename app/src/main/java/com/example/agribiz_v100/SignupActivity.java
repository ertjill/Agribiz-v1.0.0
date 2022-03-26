package com.example.agribiz_v100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {

    EditText editText_usernameCustomer, editText_numberCustomer, editText_emailCustomer, editText_passwordCustomer, editText_conPasswordCustomer;
    Button signupCustomer_btn;
    TextInputLayout textInputLayout_usernameCustomer,textInputLayout_numberCustomer,textInputLayout_emailCustomer,textInputLayout_password,textInputLayout_conPasswordCustomer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        textInputLayout_usernameCustomer = findViewById(R.id.textInputLayout_usernameCustomer);
        textInputLayout_numberCustomer = findViewById(R.id.textInputLayout_numberCustomer);
        textInputLayout_emailCustomer = findViewById(R.id.textInputLayout_emailCustomer);
        textInputLayout_password = findViewById(R.id.textInputLayout_password);
        textInputLayout_conPasswordCustomer = findViewById(R.id.textInputLayout_conPasswordCustomer);


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

            textInputLayout_usernameCustomer.setError(null);
            textInputLayout_numberCustomer.setError(null);
            textInputLayout_emailCustomer.setError(null);
            textInputLayout_password.setError(null);
            if(TextUtils.isEmpty(username)){
                textInputLayout_usernameCustomer.setError("Username should not be empty!");
                //Toast.makeText(getApplicationContext(), "Username should not be empty!", Toast.LENGTH_SHORT).show();
            }
            else if(!Verification.verifyUsername(username)){
                textInputLayout_usernameCustomer.setError("Username should be at least 6 characters and no white space");
                //Toast.makeText(getApplicationContext(), "Username should be at least 6 characters and no white space", Toast.LENGTH_SHORT).show();
            }

            else if(TextUtils.isEmpty(phone)){

                textInputLayout_numberCustomer.setError("Phone number should not be empty!");
                //Toast.makeText(getApplicationContext(), "Phone number should not be empty!", Toast.LENGTH_SHORT).show();
            }
            else if(!Verification.verifyPhone(phone)){
                textInputLayout_numberCustomer.setError("Invalid phone number");
                //Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(email)){
                textInputLayout_emailCustomer.setError("Email should not be empty!");
                //Toast.makeText(getApplicationContext(), "Email should not be empty!", Toast.LENGTH_SHORT).show();
            }
            else if(!Verification.verifyEmail(email)){
                textInputLayout_emailCustomer.setError("Invalid Email");
                //Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(password)){

                textInputLayout_password.setError("Password should not be empty!");
                //Toast.makeText(getApplicationContext(), "Password should not be empty!", Toast.LENGTH_SHORT).show();
            }
            else if(!Verification.verifyPassword(password)){
                textInputLayout_password.setError("Password must at least 8 characters with at least one digit, uppercase and lower case letter!");
                //Toast.makeText(getApplicationContext(), "Password must at least 8 characters with at least one digit, special character[@#$%^&+=()], uppercase and lower case letter!", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(confirm_password) || !Verification.verifyConfirmPassword(password,confirm_password)){

                textInputLayout_conPasswordCustomer.setError("Confirm password must match with the password!");
                //Toast.makeText(getApplicationContext(), "Confirm password must match with the password!", Toast.LENGTH_SHORT).show();
            }
            else {
                textInputLayout_conPasswordCustomer.setError(null);
                Toast.makeText(getApplicationContext(), "Create an account", Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(getApplicationContext(), VerificationCodeActivity.class);
                intent.putExtra("userPhoneNo",phone);
                intent.putExtra("userEmail",email);
                intent.putExtra("userPassword",password);
                intent.putExtra("userName",username);
                startActivity(intent);
                finish();
            }
        });
    }
    public void goToLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}