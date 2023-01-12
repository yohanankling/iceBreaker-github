package com.example.icebreaker.topic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.icebreaker.R;
import com.example.icebreaker.chats.FirebaseUser;
import com.example.icebreaker.topic.model.TopicMembersListModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class TopicMembersList extends AppCompatActivity {

    TopicMembersListModel topicMembersListModel = new TopicMembersListModel();

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
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        Title = getIntent().getStringExtra("Title");
        TextView title = findViewById(R.id.Title);
        title.setText(Title);

        recyclerView = findViewById(R.id.recyclerview);
        userArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        topicAdapter = new TopicAdapter(TopicMembersList.this, userArrayList, TopicMembersList.this.getIntent());
        recyclerView.setAdapter(topicAdapter);

    }

    private void initChats() {
        userArrayList.clear();
        DocumentReference documentReferenceuser = topicMembersListModel.getDocRef(Title);
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userArrayList.clear();
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    userData.remove("Members");
                    userData.remove("Title");
                    userData.remove(topicMembersListModel.getUid());
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
        topicMembersListModel.status("online");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        topicMembersListModel.status("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()) {
            topicMembersListModel.status("offline");
        }
    }
}