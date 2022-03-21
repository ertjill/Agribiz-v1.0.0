package com.example.agribiz_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agribiz_v100.services.AuthManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText rp_email_input;
    TextView email_sent_txt;
    Button reset_pass_btn, okay_btn;
    LinearLayout input_email_linear, sent_email_linear;
    MaterialToolbar topAppBar;
    CircularProgressIndicator circular_progress_btn;
    private static final String TAG = "ResetPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        // References
        rp_email_input = findViewById(R.id.rp_email_input);
        reset_pass_btn = findViewById(R.id.reset_pass_btn);
        okay_btn = findViewById(R.id.okay_btn);
        input_email_linear = findViewById(R.id.input_email_linear);
        sent_email_linear = findViewById(R.id.sent_email_linear);
        email_sent_txt = findViewById(R.id.email_sent_txt);
        topAppBar = findViewById(R.id.topAppBar);
        circular_progress_btn = findViewById(R.id.circular_progress_btn);

        reset_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = rp_email_input.getText().toString();
                // Check if email is valid
                if (AuthValidation.isValidEmail(email)) {
                    reset_pass_btn.setVisibility(View.GONE);
                    circular_progress_btn.setVisibility(View.VISIBLE);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG,"Sending email to reset password...");
                    AuthManagement.resetPassword(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG,"Email sent");
                                input_email_linear.setVisibility(View.GONE);
                                sent_email_linear.setVisibility(View.VISIBLE);

                                String text = "<font color=#5E3819>A verification email has been sent to this email address </font><br/><font color=#365902>"
                                        + email +"</font><br/><font color=#5E3819>please check.</font>";
                                email_sent_txt.setText(Html.fromHtml(text));
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, "Cannot reset password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    String validateEmail = AuthValidation.validateEmail(email);
                    Toast.makeText(ResetPasswordActivity.this, validateEmail, Toast.LENGTH_LONG).show();
                }
            }
        });

        okay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

    }
}