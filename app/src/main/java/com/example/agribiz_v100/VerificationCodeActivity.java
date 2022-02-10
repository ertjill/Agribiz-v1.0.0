package com.example.agribiz_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agribiz_v100.customer.CustomerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerificationCodeActivity extends AppCompatActivity {
    private static final String TAG = "VerificationCode";
    TextView populatePhoneNumber;
    EditText char_code_1,char_code_2,char_code_3,char_code_4,char_code_5,char_code_6;
    Button verificationCode__nextBtn;

    String userPhoneNo;
    String userEmail;
    String userPassword;
    String userName;

    private FirebaseAuth mAuth;
    private String codeBySystem;
    Uri defaultImage ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        char_code_1 = findViewById(R.id.char_code_1);
        char_code_2 = findViewById(R.id.char_code_2);
        char_code_3 = findViewById(R.id.char_code_3);
        char_code_4 = findViewById(R.id.char_code_4);
        char_code_5 = findViewById(R.id.char_code_5);
        char_code_6 = findViewById(R.id.char_code_6);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/agribiz-12cc6.appspot.com/o/profile%2F272229741_475164050669220_5648552245273002941_n.png?alt=media&token=781589bc-71bd-4b66-a647-59c0bff5f9e5");
        httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                defaultImage=uri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        mAuth = FirebaseAuth.getInstance();
        populatePhoneNumber = findViewById(R.id.populatePhoneNumber);
        verificationCode__nextBtn = findViewById(R.id.verificationCode__nextBtn);

        userPhoneNo = getIntent().getStringExtra("userPhoneNo");
        userEmail = getIntent().getStringExtra("userEmail");
        userPassword = getIntent().getStringExtra("userPassword");
        userName = getIntent().getStringExtra("userName");
        sendVerificationOTP(userPhoneNo);
        populatePhoneNumber.setText(userPhoneNo);

        verificationCode__nextBtn.setOnClickListener(view -> {
            String code = char_code_1.getText().toString()+
                    char_code_2.getText().toString()+
                    char_code_3.getText().toString()+
                    char_code_4.getText().toString()+
                    char_code_5.getText().toString()+
                    char_code_6.getText().toString();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(getApplicationContext(), "Input code", Toast.LENGTH_SHORT).show();
            } else {
                verifyCode(code);
            }

        });

    }

    private void sendVerificationOTP(String phoneNumber) {
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
                    Toast.makeText(getApplicationContext(), "Code : " + code, Toast.LENGTH_SHORT).show();
                    if (!TextUtils.isEmpty(code)) {
                        char_code_1.setText(code.charAt(0));
                        char_code_2.setText(code.charAt(1));
                        char_code_3.setText(code.charAt(2));
                        char_code_4.setText(code.charAt(3));
                        char_code_5.setText(code.charAt(4));
                        char_code_6.setText(code.charAt(5));
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(getApplicationContext(), "Verification Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                    builder.setTitle("Sign up failed!?");
//                    builder.setMessage("Creating account cannot be completed!");
//                    AlertDialog ad = builder.create();
//                    ad.show();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        AuthCredential emailCredential = EmailAuthProvider.getCredential(userEmail, userPassword);
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
                                                Log.d(TAG, "linkWithCredential:success");
//                                                final Uri[] profileUri = {null};
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                                String defaultProfileImage = "https://firebasestorage.googleapis.com/v0/b/agribiz-12cc6.appspot.com/o/profile%2F272229741_475164050669220_5648552245273002941_n.png?alt=media&token=781589bc-71bd-4b66-a647-59c0bff5f9e5";

                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(userName + "-c")
                                                        .setPhotoUri(defaultImage)
                                                        .build();

                                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User profile updated.");
//                                                            List<String> imageAsList = Arrays.asList(image);
                                                            Map newUser = new HashMap();
                                                            newUser.put("userId", user.getUid());
                                                            newUser.put("userName", user.getDisplayName());
                                                            newUser.put("userType", "customer");
                                                            newUser.put("userImage", user.getPhotoUrl().toString());
                                                            newUser.put("userEmail", user.getEmail());
                                                            newUser.put("userPhoneNo", user.getPhoneNumber());
                                                            newUser.put("userLocation", "");
                                                            newUser.put("userStatus", "active");

                                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                            db.collection("users").document(user.getUid())
                                                                    .set(newUser)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                            Intent intent = new Intent(VerificationCodeActivity.this, CustomerMainActivity.class);
                                                                            intent.putExtra("user", user);
                                                                            startActivity(intent);
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                            finish();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w(TAG, "Error writing document", e);
                                                                            user.delete()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Log.d(TAG, "User account deleted.");
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });

                                            } else {
                                                Log.d(TAG, "linkWithCredential:failure" + task.getException());
                                                Toast.makeText(getApplicationContext(), "Failed to create account!", Toast.LENGTH_SHORT).show();
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                user.delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User account deleted.");
                                                                }
                                                            }
                                                        });
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