package com.example.icebreaker.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.icebreaker.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatList extends AppCompatActivity {

    private ImageButton back;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter<FirebaseUser, userDetailes> chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        initFields();
        initChats();
    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerview);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
    }

    public class userDetailes extends RecyclerView.ViewHolder{

        private final TextView username;
        private final TextView status;

        public userDetailes(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.UserName);
            status = itemView.findViewById(R.id.Status);

        }
    }

    private void initChats() {
        Query query = firebaseFirestore.collection("Users").whereNotEqualTo("uid",firebaseAuth.getUid());
        FirestoreRecyclerOptions<FirebaseUser> allusername = new FirestoreRecyclerOptions.Builder<FirebaseUser>().setQuery(query, FirebaseUser.class).build();
        chatAdapter = new FirestoreRecyclerAdapter<FirebaseUser, userDetailes>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull userDetailes userDetailes, int i, @NonNull FirebaseUser FirebaseUser) {
                userDetailes.username.setText(" " + FirebaseUser.Email + " ");
                userDetailes.status.setText(" " + FirebaseUser.getStatus() + " ");
                if (FirebaseUser.getStatus().equals("online")){
                    userDetailes.status.setTextColor(Color.GREEN);
                }
                userDetailes.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(ChatList.this, Chat.class);
                    intent.putExtra("Uid", FirebaseUser.getUid());
                    intent.putExtra("Email", FirebaseUser.getEmail());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public userDetailes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlistview, parent, false);
                return new userDetailes(view);
            }
        };
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatAdapter.startListening();
        status("online");
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (chatAdapter != null){
//            chatAdapter.stopListening();
//            status("offline");
//        }
//    }

    private void status(String status) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(unused -> {
        });
    }
}