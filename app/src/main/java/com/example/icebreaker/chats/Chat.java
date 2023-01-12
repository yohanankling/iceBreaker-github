package com.example.icebreaker.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icebreaker.R;
import com.example.icebreaker.chats.model.ChatModel;
import com.example.icebreaker.gameZone.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Chat extends AppCompatActivity {

    ChatModel chatModel = new ChatModel(this);

    private EditText getMessage;
    private ImageButton back;
    private ImageButton inviteGame;
    private Button sendBtn;
    private TextView name, status;
    private String enteredMessage;
    String Recievername, RecieverUid, SenderUid, MyName;
    RecyclerView recyclerView;
    String currentTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    MessagesAdapter messagesAdapter;
    ArrayList<Message> messageArrayList;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);
        initFields();
        initStatus();
        initMessages();
        backBtn();
        sendBtn();
        inviteGameBtn();
    }

    @SuppressLint("SimpleDateFormat")
    private void initFields() {
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
        back = findViewById(R.id.Back);
        inviteGame = findViewById(R.id.inviteGame);
        getMessage = findViewById(R.id.getMessage);
        status = findViewById(R.id.Status);
        recyclerView = findViewById(R.id.recyclerview);
        sendBtn = findViewById(R.id.send);
        name = findViewById(R.id.UserName);
        SenderUid = chatModel.getUid();
        RecieverUid = getIntent().getStringExtra("Uid");
        Recievername = getIntent().getStringExtra("Email");
        MyName = getIntent().getStringExtra("MyName");
        name.setText(Recievername);
        messageArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(Chat.this, messageArrayList);
        recyclerView.setAdapter(messagesAdapter);
    }

    private void initStatus(){
        chatModel.status("online");
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(RecieverUid);
        docRef.addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, e) -> {
            if(documentSnapshot.exists()){
                String s = documentSnapshot.getString("status");
                status.setText(s);
            }
        });
    }


        private void initMessages() {
        messageArrayList.clear();
        DatabaseReference databaseReference = chatModel.getDbRef(SenderUid, RecieverUid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    messageArrayList.add(message);
                }
                recyclerView.scrollToPosition(messageArrayList.size() - 1);
                messagesAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void backBtn() {
        back.setOnClickListener(v -> finish());
    }

    private void sendBtn() {
        sendBtn.setOnClickListener(v -> {
            enteredMessage = getMessage.getText().toString();
            if (enteredMessage.isEmpty()) {
                Toast.makeText(Chat.this, "enter message first..", Toast.LENGTH_SHORT).show();
            } else {
                currentTime = simpleDateFormat.format(calendar.getTime());
                Message message = new Message(currentTime, enteredMessage, chatModel.getUid());
                chatModel.sendTextToDb(SenderUid, RecieverUid, message);
                getMessage.setText(null);
            }
        });
    }

    private void inviteGameBtn() {
        inviteGame.setOnClickListener(v -> {
            if(status.getText().equals("offline")){
        Toast.makeText(Chat.this, "cant invite offline member..", Toast.LENGTH_SHORT).show();
    }
        else{
            Intent intent = new Intent(Chat.this, inviter.class);
            intent.putExtra("inviterName", MyName);
            intent.putExtra("opponentName", Recievername);
            intent.putExtra("opponentUid", RecieverUid);
            startActivity(intent);}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
        chatModel.status("online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatModel.status("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()) {
            chatModel.status("offline");
        }
    }
}