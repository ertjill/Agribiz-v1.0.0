package com.example.agribiz_v100;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.agribiz_v100.adapter.MessageAdapter;
import com.example.agribiz_v100.entities.ChatModel;
import com.example.agribiz_v100.services.AuthManagement;
import com.example.agribiz_v100.services.ChatManagement;
import com.example.agribiz_v100.services.ProfileManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    LinearLayoutManager linearLayoutManager;
    RecyclerView msgRecyclerView;
    List<ChatModel> chatModelList;
    MessageAdapter messageAdapter;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    DocumentSnapshot last;
    ListenerRegistration registration;
    String anotherUserId;
    EditText message_et;
    ImageButton send_ib;
    MaterialToolbar topAppBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        message_et =findViewById(R.id.message_et);
        send_ib = findViewById(R.id.send_ib);
        msgRecyclerView = (RecyclerView) findViewById(R.id.message_rv);
        linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        chatModelList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, chatModelList);
        msgRecyclerView.setAdapter(messageAdapter);

        if(getIntent().getStringExtra("userId")!=null) {

            anotherUserId = getIntent().getStringExtra("userId");

            topAppBar = findViewById(R.id.topAppBar);

            topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            send_ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send_ib.setEnabled(false);
                    String msg =message_et.getText().toString();
                    if(!msg.isEmpty()){
                        Date dateNow = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                        ChatModel chatModel = new ChatModel();

                        chatModel.setChatId(user.getUid()+simpleDateFormat.format(dateNow));
                        chatModel.setChatMessage(msg);
                        chatModel.setChatSeen(false);
                        chatModel.setChatSenderUserId(user.getUid());
                        chatModel.setChatDate(new Timestamp(dateNow));
                        chatModelList.add(chatModel);
                        ChatManagement.sendMessage(ChatActivity.this,user.getUid(),anotherUserId,chatModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
//                                        AuthValidation.successToast(ChatActivity.this,"Message Sent").show();
                                        message_et.setText("");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("ids",e.getLocalizedMessage());
                                        e.getStackTrace();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        send_ib.setEnabled(true);
                                    }
                                });
                    }
                }
            });
            ProfileManagement.getUserProfile(anotherUserId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String name = documentSnapshot.getString("userDisplayName");
                    topAppBar.setTitle(name.substring(0,name.length()-2));
                }
            });

            ChatManagement.tagMessagesSeen(user.getUid(),anotherUserId);

        }
    }

    public void getMessages(){
        registration=ChatManagement.getConversation(user.getUid(),anotherUserId,last).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                chatModelList.clear();
                if(!value.isEmpty()){
//                    last=value.getDocuments().get(value.getDocuments().size()-1);
                    for (DocumentSnapshot ds:value){
                        chatModelList.add(ds.toObject(ChatModel.class));
                    }
                    messageAdapter.notifyDataSetChanged();
                    msgRecyclerView.scrollToPosition(chatModelList.size()-1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMessages();
        AuthManagement.setUserStatus(user.getUid(),"active");
    }

    @Override
    protected void onPause() {
        super.onPause();
        registration.remove();
        AuthManagement.setUserStatus(user.getUid(),"inactive");
    }
}