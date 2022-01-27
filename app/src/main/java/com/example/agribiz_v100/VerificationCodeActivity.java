package com.example.agribiz_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agribiz_v100.customer.CustomerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

public class VerificationCodeActivity extends AppCompatActivity {
    TextView populatePhoneNumber;
    EditText enterVerificationCode;
    Button verificationCode__nextBtn;

    String phoneNo;
    String email;
    String password;
    String displayName;

    private FirebaseAuth mAuth;
    private String codeBySystem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        mAuth = FirebaseAuth.getInstance();
        populatePhoneNumber = findViewById(R.id.populatePhoneNumber);
        enterVerificationCode = findViewById(R.id.enterVerificationCode);
        verificationCode__nextBtn = findViewById(R.id.verificationCode__nextBtn);

        phoneNo = getIntent().getStringExtra("phoneNo");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        displayName = getIntent().getStringExtra("displayName");
        sendVerificationOTP(phoneNo);
        populatePhoneNumber.setText(phoneNo);

        verificationCode__nextBtn.setOnClickListener(view->{
            String code  = enterVerificationCode.getText().toString();
            if(TextUtils.isEmpty(code)){
                Toast.makeText(getApplicationContext(), "Input code", Toast.LENGTH_SHORT).show();
            }
            else{
                verifyCode(code);
            }

        });

    }
    private void sendVerificationOTP(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(VerificationCodeActivity.this)              // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    Toast.makeText(getApplicationContext(), "Code : "+ code, Toast.LENGTH_SHORT).show();
                    if(!TextUtils.isEmpty(code)){
                        enterVerificationCode.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(getApplicationContext(), "Verification Failed!"+ e.getMessage(), Toast.LENGTH_SHORT).show();
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                    builder.setTitle("Sign up failed!?");
//                    builder.setMessage("Creating account cannot be completed!");
//                    AlertDialog ad = builder.create();
//                    ad.show();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        AuthCredential emailCredential = EmailAuthProvider.getCredential(email, password);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerificationCodeActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Update UI
                            mAuth.getCurrentUser().linkWithCredential(emailCredential)
                                    .addOnCompleteListener(VerificationCodeActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("SIGN_UP", "linkWithCredential:success");
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(displayName+"-f").build();
                                                user.updateProfile(profileUpdates);
                                                startActivity(new Intent(getApplicationContext(), CustomerMainActivity.class));
                                                finish();
                                            } else {
                                                Log.d("SIGN_UP", "linkWithCredential:failure" + task.getException().getMessage());
                                                Toast.makeText(getApplicationContext(), "Failed to create account!", Toast.LENGTH_SHORT).show();
//                                                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                                                builder.setCancelable(false);
//                                                builder.setTitle("Sign up failed!?");
//                                                builder.setMessage("Creating account cannot be completed!");
//                                                builder.show();
//
//                                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialog, int which) {
//
//                                                    }
//                                                });
                                            }
                                        }
                                    });

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "Verification code entered was invalid!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}