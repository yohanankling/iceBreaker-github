package com.example.icebreaker.topic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.icebreaker.R;
import com.example.icebreaker.chats.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class TopicMembersList extends AppCompatActivity {

    private ImageButton back;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    public String Title;




    RecyclerView recyclerView;
    TopicAdapter topicAdapter;
    ArrayList<FirebaseUser> userArrayList;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_topic_list);
        initFields();
        initChats();
    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        Title = getIntent().getStringExtra("Title");
        TextView title = findViewById(R.id.Title);
        title.setText(Title);

        recyclerView = findViewById(R.id.recyclerview);
        userArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        topicAdapter = new TopicAdapter(TopicMembersList.this, userArrayList);
        recyclerView.setAdapter(topicAdapter);

    }

    private void initChats() {
        userArrayList.clear();
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Topics").document(Title);
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userArrayList.clear();
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    userData.remove("Members");
                    userData.remove("Title");
                    userData.remove(firebaseAuth.getUid());
                    for (Map.Entry<String, Object> data : userData.entrySet()) {
                        String uid = data.getKey();
                        String Email = data.getValue().toString();
                        FirebaseUser firebaseUser = new FirebaseUser(uid, Email, "");
                        userArrayList.add(firebaseUser);
                    }
                    topicAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        topicAdapter.notifyDataSetChanged();
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