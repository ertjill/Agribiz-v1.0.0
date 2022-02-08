package com.example.agribiz_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.agribiz_v100.agrovit.AgrovitMainActivity;
import com.example.agribiz_v100.customer.CustomerMainActivity;
import com.example.agribiz_v100.farmer.FarmerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    EditText email_input, password;
    Button login_btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().equals("")) {
                char c = user.getDisplayName().charAt(user.getDisplayName().length() - 1);
                if (c == 'c') {
                    startActivity(new Intent(getBaseContext(), CustomerMainActivity.class));
                    finish();
                } else if (c == 'f') {
                    startActivity(new Intent(getBaseContext(), FarmerMainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getBaseContext(), AgrovitMainActivity.class));
                    finish();
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        email_input = findViewById(R.id.email_input);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);

        login_btn.setOnClickListener(view -> {

            String email = email_input.getText().toString();
            String pass = password.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getBaseContext(), "Input Phone number!", Toast.LENGTH_SHORT).show();
            } else if (!Verification.verifyEmail(email)) {
                Toast.makeText(getBaseContext(), "Invalid Email address!", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pass)) {
                Toast.makeText(getBaseContext(), "Input password!", Toast.LENGTH_SHORT).show();
            } else if (!Verification.verifyPassword(pass)) {
                Toast.makeText(getBaseContext(), "Password must at least 8 characters with at least one digit, special character, uppercase and lower case letter!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Log in", Toast.LENGTH_SHORT).show();
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user.getDisplayName() != null) {
                                        char c = user.getDisplayName().charAt(user.getDisplayName().length() - 1);
                                        if (c == 'c') {
                                            Intent intent = new Intent(getBaseContext(), CustomerMainActivity.class);
                                            intent.putExtra("user",user);
                                            startActivity(intent);
                                            finish();
                                        } else if(c=='f') {
                                            startActivity(new Intent(getBaseContext(), FarmerMainActivity.class));
                                            finish();
                                        }else{
                                            startActivity(new Intent(getBaseContext(), AgrovitMainActivity.class));
                                            finish();
                                        }
                                    } else {
                                        Intent intent =new Intent(getBaseContext(), AgrovitMainActivity.class);
                                        intent.putExtra("user",user);
                                        startActivity(intent);
                                        finish();
                                    }

                                    Log.d("Login", "signInWithEmail:success" + user.getDisplayName());
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Login", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }
                            }
                        });
            }
        });

    }

    public void goToSignup(View v) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToResetPassword(View v) {
        //Intent intent = new Intent(this, ResetPasswordActivity.class);
        //startActivity(intent);
    }


}