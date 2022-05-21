package com.example.agribiz_v100;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.agribiz_v100.adapter.ThreadsListAdapter;
import com.example.agribiz_v100.entities.ChatThreadUserModel;
import com.example.agribiz_v100.entities.UserModel;
import com.example.agribiz_v100.services.ChatManagement;
import com.example.agribiz_v100.services.ProfileManagement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    MaterialToolbar topAppBar;
    ListView threads_lv;
    ThreadsListAdapter threadsListAdapter;
    List<ChatThreadUserModel> users;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        reference();

        threads_lv.setAdapter(threadsListAdapter);
        topAppBar.setNavigationOnClickListener(v -> finish());
        threads_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);
                intent.putExtra("userId", users.get(position).getUserID());
                startActivity(intent);
            }
        });
    }

    public void getThreads() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();
        users.clear();
        ChatManagement.getThreads(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty())
                    for (DocumentSnapshot ds : queryDocumentSnapshots) {
                        ProfileManagement.getUserProfile(ds.getId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                users.add(documentSnapshot.toObject(ChatThreadUserModel.class));
                                threadsListAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getThreads();
    }

    private void reference() {
        users = new ArrayList<>();
        threadsListAdapter = new ThreadsListAdapter(this, users);
        threads_lv = findViewById(R.id.threads_lv);
        topAppBar = findViewById(R.id.topAppBar);
    }
}