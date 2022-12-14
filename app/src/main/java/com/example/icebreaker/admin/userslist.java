package com.example.icebreaker.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.example.icebreaker.chats.*;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Map;

public class userslist extends AppCompatActivity {

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
        backBtn();
    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerview);
        back = findViewById(R.id.back);
    }

    private void backBtn() {
        back.setOnClickListener(v -> finish());
    }

    public class userDetailes extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView status;

        public userDetailes(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.UserName);
            status = itemView.findViewById(R.id.Status);

        }
    }

    private void initChats() {
        Query query = firebaseFirestore.collection("Users").whereNotEqualTo("uid", firebaseAuth.getUid());
        FirestoreRecyclerOptions<FirebaseUser> allusername = new FirestoreRecyclerOptions.Builder<FirebaseUser>().setQuery(query, FirebaseUser.class).build();
        chatAdapter = new FirestoreRecyclerAdapter<FirebaseUser, userDetailes>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull userDetailes userDetailes, int i, @NonNull FirebaseUser FirebaseUser) {
                userDetailes.username.setText(" " + FirebaseUser.getEmail() + " ");
                userDetailes.status.setText(" " + FirebaseUser.getStatus() + " ");
                if (FirebaseUser.getStatus().equals("online")) {
                    userDetailes.status.setTextColor(Color.GREEN);
                }
                userDetailes.itemView.setOnClickListener(v -> showUserDetails(FirebaseUser.getUid()));
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


    private void showUserDetails(String userUId) {
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(userUId);
        documentReferenceuser.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(userslist.this);
                            final View PopUpStatus = getLayoutInflater().inflate(R.layout.status_popup, null);
                            ImageButton backBtn = PopUpStatus.findViewById(R.id.back);
                            ImageView male = PopUpStatus.findViewById(R.id.MaleUserPic);
                            ImageView female = PopUpStatus.findViewById(R.id.FemaleUserPic);
                            TextView Details = PopUpStatus.findViewById(R.id.Details);
                            Details.setText("user details:");
                            TextView name = PopUpStatus.findViewById(R.id.Name);
                            TextView mail = PopUpStatus.findViewById(R.id.Mail);
                            TextView topic = PopUpStatus.findViewById(R.id.Topic);
                            TextView Game = PopUpStatus.findViewById(R.id.Game);
                            Button chat = PopUpStatus.findViewById(R.id.chatwith);
                            chat.setVisibility(View.VISIBLE);
                            TextView SocialClass = PopUpStatus.findViewById(R.id.SocialClass);
                            backBtn.setOnClickListener(v -> {
                                Intent intent = new Intent(userslist.this, Home.class);
                                startActivity(intent);
                            });
                            Map<String, Object> userData = document.getData();
                            chat.setOnClickListener(v -> {
                                Intent intent = new Intent(userslist.this, Chat.class);
                                intent.putExtra("Uid", userUId);
                                intent.putExtra("Email", userData.get("email").toString());
                                startActivity(intent);
                            });
                            if (userData.get("gender").toString().equals("Male")) {
                                male.setVisibility(View.VISIBLE);
                            } else female.setVisibility(View.VISIBLE);
                            name.setText(userData.get("name").toString());
                            mail.setText("mail: " + userData.get("email").toString());
                            // TODO: add topic and game
                            topic.setText("topic: " + userData.get("topic").toString());
//                if (snapshot.child(userUId).child("Game").getValue(String.class).equals("Available")) {
//                    Game.setText("game: " + "available");
//                } else Game.setText("game: " + "not available");
                            SocialClass.setText("access: user");
                            builder.setView(PopUpStatus);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }
        );
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