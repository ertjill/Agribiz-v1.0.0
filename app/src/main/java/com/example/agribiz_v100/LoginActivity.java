package com.example.agribiz_v100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agribiz_v100.controller.AuthController;
import com.example.agribiz_v100.responses.AuthResponse;
import com.example.agribiz_v100.services.AuthManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button login_btn;
    AuthManagement am;
    private FirebaseAuth mAuth;

    EditText email_input, password_et;
    TextView forgot_pass_label;
    private static final String Tag = "LoginActivity";

    @Override
    protected void onStart() {
        super.onStart();
        // If user is already logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // then navigate to corresponding UI based on the user role
        AuthController.loginNavigation(user, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // AuthManagement class object initialization
        am = new AuthManagement(this);
        // FirebaseAuth object initialization
        mAuth = FirebaseAuth.getInstance();
        
        // Fragment UI ID References
        email_input = findViewById(R.id.email_input);
        password_et = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);
        forgot_pass_label = findViewById(R.id.forgot_pass_label);

        // When login button is click
        login_btn.setOnClickListener(view -> {
            // Get inputted values from fields
            String email = email_input.getText().toString();
            String pass = password_et.getText().toString();

            // Stored get validation response
            String validatedEmail = AuthValidation.validateEmail(email);
            String validatedPassword = AuthValidation.validatePassword(pass);

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
                Task<AuthResult> loginTask = am.loginAccount(email, pass);
                // Listens if task is complete
                loginTask.addOnCompleteListener(this, task -> {
                    // Verifies if task is successful
                    if (task.isSuccessful()) {
                        try {
                            // then user is logged in,
                            Log.d(Tag, "signInWithEmail:success"); // for developers
                            // returns currently signed-in user
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Navigate to corresponding UI based on user roles
                            AuthController.loginNavigation(user, LoginActivity.this);
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            if (task.getException() != null) {
                                // If sign in fails, display a message to the user.
                                String errorCode = ((FirebaseAuthException)task.getException()).getErrorCode();
                                Log.d(Tag, AuthResponse.getAuthError(errorCode)); // for developers
                                // Return error message for errorCode
                                Toast.makeText(LoginActivity.this, AuthResponse.getAuthError(errorCode),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Malicious login detected. " +
                                    "Your account has been temporarily put on hold for a day..",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        forgot_pass_label.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class)));
    }

    public void goToSignup(View v) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }
}