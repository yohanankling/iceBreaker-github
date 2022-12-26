package com.example.icebreaker.topic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icebreaker.MainActivity;
import com.example.icebreaker.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ChooseTopic extends AppCompatActivity {

    private ImageButton Add;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter<Topic, TopicDetails> TopicsAdapter;
    String Name;
    TextView noChats;


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
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseTopic.this, MainActivity.class);
            startActivity(intent);});
        TextView title = findViewById(R.id.Title);
        title.setText("Topics");
        Name = getIntent().getStringExtra("Name");
        ConstraintLayout constraintLayout = findViewById(R.id.Add);
        Add = findViewById(R.id.AddBtn);
        noChats = findViewById(R.id.noChats);
        noChats.setText("no topics added");
        constraintLayout.setVisibility(View.VISIBLE);
    }

    public class TopicDetails extends RecyclerView.ViewHolder {

        private final TextView TopicName;
        private final TextView Members;

        public TopicDetails(@NonNull View itemView) {
            super(itemView);
            TopicName = itemView.findViewById(R.id.TopicName);
            Members = itemView.findViewById(R.id.Members);

        }
    }

    private void initTopics() {
        Query query = firebaseFirestore.collection("Topics");
        FirestoreRecyclerOptions<Topic> topic = new FirestoreRecyclerOptions.Builder<Topic>().setQuery(query, Topic.class).build();
        TopicsAdapter = new FirestoreRecyclerAdapter<Topic, TopicDetails>(topic) {
            @Override
            protected void onBindViewHolder(@NonNull TopicDetails TopicDetails, int i, @NonNull Topic Topic) {
                noChats.setVisibility(View.INVISIBLE);
                TopicDetails.TopicName.setText(" " + Topic.getTitle());
                TopicDetails.Members.setText(" " + Topic.getMembers() +" Friends ");
                TopicDetails.itemView.setOnClickListener(v -> joinTopic(Topic.getTitle(), Topic.getMembers()));
            }

            @NonNull
            @Override
            public TopicDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topiclistview, parent, false);
                return new TopicDetails(view);
            }
        };
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(TopicsAdapter);
    }

    private void joinTopic(String Title, long members) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("to join " + Title + " title?");
        builder.setMessage("");
        builder.setPositiveButton("yes please", (dialog, which) ->
            checkIfAlreadyin(Title, members));
        Toast.makeText(this, Name, Toast.LENGTH_SHORT).show();
        builder.setNegativeButton("no", (dialog, which) ->
                closeOptionsMenu()
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkIfAlreadyin(String title, long members) {
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    String tiltleRegistered = (String) userData
                            .get("topic");
                    if (tiltleRegistered.equals(title)){
                        Toast.makeText(this, "you'r already in that topic..", Toast.LENGTH_SHORT).show();
                    }
                    else if(tiltleRegistered.equals("~null")){
                        registerdToTopic(title,members);
                        changeUserData(title);
                    }
                    else{
                        leaveTitle(tiltleRegistered);
                        registerdToTopic(title,members);
                        changeUserData(title);
                    }
                }
            }
        });
    }

    private void leaveTitle(String tiltleRegistered) {
        DocumentReference documentReference = firebaseFirestore.collection("Topics").document(tiltleRegistered);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> topicData = document.getData();
                    topicData.remove(firebaseAuth.getUid());
                    long members = (long) topicData.get("Members");
                    topicData.replace("Members", members - 1);
                    documentReference.set(topicData);
                    if (members == 1){
                        documentReference.delete();
                    }
                }
            }
        });
    }

    private void registerdToTopic(String Title, long members) {
        DocumentReference documentReference = firebaseFirestore.collection("Topics").document(Title);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    userData.put(firebaseAuth.getUid(),Name);
                    userData.replace("Members", members + 1);
                    documentReference.set(userData);
                }
            }
        });
    }

    private void changeUserData(String Title){
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    String tiltleRegistered = (String) userData
                            .get("topic");
                    if (!tiltleRegistered.equals("~null")){
                        leaveTitle(tiltleRegistered);
                    }
                    userData.replace("topic", Title);
                    documentReferenceuser.set(userData);
                }
            }
        });
    }

    private void AddTopic() {
        Add.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseTopic.this);
            final View addtopic = getLayoutInflater().inflate(R.layout.addtopic, null);
            ImageButton backBtn = addtopic.findViewById(R.id.back);
            Button CancelBtn = addtopic.findViewById(R.id.Cancel);
            EditText Topic = addtopic.findViewById(R.id.Topic);
            Button AddTopic = addtopic.findViewById(R.id.AddTopic);
            AddTopic.setOnClickListener(v12 -> {
                String TopicName = Topic.getText().toString();
                ValidateAndCheckTopic(TopicName);
            });
            builder.setView(addtopic);
            AlertDialog dialog = builder.create();
            CancelBtn.setOnClickListener(v1 ->
                dialog.dismiss());
            backBtn.setOnClickListener(v1 ->
                    dialog.dismiss());
            dialog.show();
        });
    }

    public void ValidateAndCheckTopic(String TopicName) {
        if (TopicName.isEmpty()){
            Toast.makeText(this, "cant fill empty title", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference documentReference = firebaseFirestore.collection("Topics").document(TopicName);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Toast.makeText(ChooseTopic.this, "topic is already exist!\n join or create another one..", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> TopicData = new HashMap<>();
                    TopicData.put("Title", TopicName);
                    TopicData.put(firebaseAuth.getUid(), Name);
                    TopicData.put("Members", 1);
                    documentReference.set(TopicData);
                    Toast.makeText(ChooseTopic.this, "topic added!", Toast.LENGTH_SHORT).show();
                    CurrnetTitle();
                    changeUserData(TopicName);
                    Intent intent = new Intent(ChooseTopic.this, ChooseTopic.class);
                    intent.putExtra("Name", Name);
                    startActivity(intent);
                }
            }
        });
    }

    private void CurrnetTitle() {
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    String tiltleRegistered = (String) userData
                            .get("topic");
                    if (!tiltleRegistered.equals("~null")){
                        leaveTitle(tiltleRegistered);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        TopicsAdapter.startListening();
        status("online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()) {
            status("offline");
        }
    }

    private void status(String status) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(unused -> {
        });
    }
}