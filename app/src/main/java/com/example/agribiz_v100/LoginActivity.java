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

import com.example.agribiz_v100.controller.AuthController;
import com.example.agribiz_v100.responses.AuthResponse;
import com.example.agribiz_v100.services.AuthManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button login_btn;
    AuthManagement am;
    AuthValidation av;
    AuthController ac;
    EditText email_input, password_et;
    private static final String Tag = "LoginActivity";

    @Override
    protected void onStart() {
        super.onStart();
        // If user is already logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // then navigate to corresponding UI based on the user role
        ac.loginNavigation(user, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // AuthManagement class object initialization
        am = new AuthManagement();
        // AuthValidation class object initialization
        av = new AuthValidation();
        // AuthController class object initialization
        ac = new AuthController();
        
        // Fragment UI ID References
        email_input = findViewById(R.id.email_input);
        password_et = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);

        // When login button is click
        login_btn.setOnClickListener(view -> {
            // Get inputted values from fields
            String email = email_input.getText().toString();
            String pass = password_et.getText().toString();

            // Stored get validation response
            String validatedEmail = av.validateEmail(email);
            String validatedPassword = av.validatePassword(pass);

            // Checks if email contains error
            if (!TextUtils.isEmpty(validatedEmail)) {
                // then display error message
                email_input.setError(validatedEmail);
            }
            // Checks if password contains error
            else if (!TextUtils.isEmpty(validatedPassword)) {
                // then display error message
                password_et.setError(validatedPassword);
            }
            else {
                // Holds a task that returns AuthResult, which is a FirebaseAuth object
                Task<AuthResult> loginTask = am.loginAccount(email, pass, this);
                // Listens if task is complete
                loginTask.addOnCompleteListener(this, task -> {
                    // Verifies if task is successful
                    if (task.isSuccessful()) {
                        // then user is logged in,
                        Log.d(Tag, "signInWithEmail:success"); // for developers
                        // returns currently signed-in user
                        FirebaseUser user = task.getResult().getUser();
                        // Navigate to corresponding UI based on user roles
                        ac.loginNavigation(user, LoginActivity.this);
                    } else {
                        try {
                            // stop here
                            // If sign in fails, display a message to the user.
                            String errorCode = ((FirebaseAuthException)task.getException()).getErrorCode();

                            Log.d(Tag, AuthResponse.getAuthError(errorCode)); // for developers
                            Toast.makeText(LoginActivity.this, AuthResponse.getAuthError(errorCode),
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Malicious login detected. " +
                                    "Your account has been temporarily put on hold for a day..",
                                    Toast.LENGTH_SHORT).show();
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
}