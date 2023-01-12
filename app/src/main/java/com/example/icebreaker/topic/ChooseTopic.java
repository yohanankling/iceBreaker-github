package com.example.icebreaker.topic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.example.icebreaker.topic.model.ChooseTopicModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Map;

public class ChooseTopic extends AppCompatActivity {

    ChooseTopicModel chooseTopicModel = new ChooseTopicModel(this);

    private ImageButton Add;
    private RecyclerView recyclerView;
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
        recyclerView = findViewById(R.id.recyclerview);
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseTopic.this, Home.class);
            startActivity(intent);});
        TextView title = findViewById(R.id.Title);
        title.setText("Topics");
        Name = getIntent().getStringExtra("Name");
        Add = findViewById(R.id.AddBtn);
        Add.setVisibility(View.VISIBLE);
        noChats = findViewById(R.id.noChats);
        noChats.setText("no topics added");
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
        Query query = chooseTopicModel.getQueryData();
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
        builder.setNegativeButton("no", (dialog, which) ->
                closeOptionsMenu()
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkIfAlreadyin(String title, long members) {
        DocumentReference documentReferenceuser = chooseTopicModel.getFirebaseFirestore();
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
        chooseTopicModel.leaveTitle(tiltleRegistered);
    }

    private void registerdToTopic(String Title, long members) {
        chooseTopicModel.registerdToTopic(Title, members, Name);
    }

    private void changeUserData(String Title){
        chooseTopicModel.changeUserData(Title);
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
        if (TopicName.length()>15){
            Toast.makeText(this, "too long title! \n up tp 15 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        chooseTopicModel.ValidateTopic(TopicName, Name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TopicsAdapter.startListening();
        chooseTopicModel.status("online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chooseTopicModel.status("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()) {
            chooseTopicModel.status("offline");
        }
    }
}