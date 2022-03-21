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
import com.example.agribiz_v100.entities.UserModel;
import com.example.agribiz_v100.services.AuthManagement;
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
    private FirebaseAuth mAuth;

    EditText char_code_1,char_code_2,char_code_3,char_code_4,char_code_5,char_code_6;
    String userPhoneNo, userEmail, userPassword, userName;
    Button verificationCode__nextBtn;
    TextView populatePhoneNumber;

    AuthManagement am;
    Uri defaultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        am = new AuthManagement(this);
        UserModel user = new UserModel();

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

        user.setUserDisplayName(userName + "-c");
        user.setUserEmail(userEmail);
        user.setUserIsActive(true);
        user.setUserType("customer");
        user.setUserPhoneNumber(userPhoneNo);
        am.registerAccount(user, userPassword);

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
                am.verifyReceivedCode(code);
            }
        });
    }
}