package com.example.icebreaker.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.icebreaker.R;
import com.example.icebreaker.chats.model.ChatListModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ChatList extends AppCompatActivity {

    ChatListModel chatListModel = new ChatListModel();

    RecyclerView recyclerView;
    usersAdapter usersAdapter;
    ArrayList<FirebaseUser> usersArrayList;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        initFields();
        initChats();
    }

    private void initFields() {
        recyclerView = findViewById(R.id.recyclerview);
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        usersArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        usersAdapter = new usersAdapter(ChatList.this, usersArrayList, ChatList.this.getIntent());
        recyclerView.setAdapter(usersAdapter);
    }

    private void initChats() {
        usersArrayList.clear();
        DatabaseReference databaseReference = chatListModel.getDbRef();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    findViewById(R.id.noChats).setVisibility(View.INVISIBLE);
                    DocumentReference documentReference = chatListModel.getFsRef(dataSnapshot);
                    documentReference.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> userData = document.getData();
                                FirebaseUser firebaseUser = new FirebaseUser(dataSnapshot.getKey() , userData.get("name").toString(), userData.get("status").toString());
                                usersArrayList.add(firebaseUser);
                                usersAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        usersAdapter.notifyDataSetChanged();
        chatListModel.status("online");
    }
}