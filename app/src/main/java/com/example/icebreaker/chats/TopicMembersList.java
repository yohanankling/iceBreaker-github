package com.example.icebreaker.chats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class TopicMembersList extends AppCompatActivity {

    private ImageButton back;
    private ListView List;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    public String Title;


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
        List = findViewById(R.id.List);
        back.setOnClickListener(v -> finish());
        Title = getIntent().getStringExtra("Title");
        TextView title = findViewById(R.id.Title);
        title.setText(Title);
    }

    private void initChats() {
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Topics").document(Title);
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    userData.remove("Members");
                    userData.remove("Title");
                    for (Map.Entry<String, Object> data : userData.entrySet()) {
                        String uid = data.getKey();
                        String Email = data.getValue().toString();
                        Toast.makeText(this, Email, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        chatAdapter.startListening();
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