package com.example.icebreaker.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icebreaker.Home;
import com.example.icebreaker.MainActivity;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class TopicMembers extends AppCompatActivity {

    private ImageButton Add, back;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
//    private FirestoreRecyclerAdapter<Topic, TopicDetails> TopicsAdapter;
    String Email;
    Boolean addedTopic = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        initFields();
        initTopics();
        AddTopic();
    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerview);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.Title);
        title.setText("Topics");
        Email = getIntent().getStringExtra("Email");
        ConstraintLayout constraintLayout = findViewById(R.id.Add);
        Add = findViewById(R.id.AddBtn);
        constraintLayout.setVisibility(View.VISIBLE);
    }

//        public class TopicDetails extends RecyclerView.ViewHolder{
//
//        private final TextView TopicName;
//        private final TextView Members;
//
//        public TopicDetails(@NonNull View itemView) {
//            super(itemView);
//            TopicName = itemView.findViewById(R.id.TopicName);
//            Members = itemView.findViewById(R.id.Members);
//
//        }
//    }

    private void initTopics() {
//        Query query = firebaseFirestore.collection("Topics");
//        FirestoreRecyclerOptions<Topic> topic = new FirestoreRecyclerOptions.Builder<Topic>().setQuery(query, Topic.class).build();
//        TopicsAdapter = new FirestoreRecyclerAdapter<Topic, TopicDetails>(topic) {
//            @Override
//            protected void onBindViewHolder(@NonNull TopicDetails TopicDetails, int i, @NonNull Topic Topic) {
//                TopicDetails.TopicName.setText(" " + Topic.getName());
//                TopicDetails.Members.setText(" " + Topic.getMembers());
//                TopicDetails.itemView.setOnClickListener(v -> {
//
//                });
//
//                }
//
//            @NonNull
//            @Override
//            public TopicDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topiclistview, parent, false);
//                return new TopicDetails(view);
//            }
//        };
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
//        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(TopicsAdapter);
    }

    private void AddTopic() {
        Add.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TopicMembers.this);
            final View addtopic = getLayoutInflater().inflate(R.layout.addtopic, null);
            ImageButton backBtn = addtopic.findViewById(R.id.back);
            backBtn.setOnClickListener(v1 -> finish());
            Button CancelBtn = addtopic.findViewById(R.id.Cancel);
            CancelBtn.setOnClickListener(v1 -> finish());
            EditText Topic = addtopic.findViewById(R.id.Topic);
            Button AddTopic = addtopic.findViewById(R.id.AddTopic);
            AddTopic.setOnClickListener(v12 -> {
                String TopicName = Topic.getText().toString();
                ValidateAndCheckTopic(TopicName);
            });
            builder.setView(addtopic);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
    public void ValidateAndCheckTopic(String TopicName){
        DocumentReference documentReference = firebaseFirestore.collection("Topics").document(TopicName);
        documentReference.addSnapshotListener((value, error) -> {
            if (value.exists()&&!addedTopic){
                Toast.makeText(TopicMembers.this, "topic is already exist!\n join or create another one..", Toast.LENGTH_SHORT).show();
            } else if (addedTopic){
                Toast.makeText(this, "topic added", Toast.LENGTH_SHORT).show();}
            else{
                addedTopic = true;
                Map<String, Object> userData = new HashMap<>();
                userData.put("creator", Email);
                userData.put("creatorUid", firebaseAuth.getUid());
                documentReference.set(userData);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        TopicsAdapter.startListening();
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