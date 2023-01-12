package com.example.icebreaker.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icebreaker.R;
import com.example.icebreaker.chats.model.BroadcastModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Broadcast extends AppCompatActivity {

    BroadcastModel broadcastModel = new BroadcastModel();

    private EditText getMessage;
    private ImageButton back;
    private Button sendBtn;
    private String enteredMessage;
    String BroadcastTitle, SenderUid, SenderName;
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
        initMessages();
        backBtn();
        sendBtn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastModel.status("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()) {
            broadcastModel.status("offline");
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void initFields() {
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
        back = findViewById(R.id.Back);
        getMessage = findViewById(R.id.getMessage);
        TextView status = findViewById(R.id.Status);
        status.setText("(broadcast)");
        status.setTextSize(12);
        recyclerView = findViewById(R.id.recyclerview);
        sendBtn = findViewById(R.id.send);
        TextView name = findViewById(R.id.UserName);
        SenderUid = broadcastModel.getUid();
        BroadcastTitle = getIntent().getStringExtra("Title");
        SenderName = getIntent().getStringExtra("Name");
        name.setText(BroadcastTitle);
        messageArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(Broadcast.this, messageArrayList);
        recyclerView.setAdapter(messagesAdapter);
    }

    private void initMessages() {
        messageArrayList.clear();
        DatabaseReference databaseReference = broadcastModel.getDbRef(BroadcastTitle);
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
                Toast.makeText(Broadcast.this, "enter message first..", Toast.LENGTH_SHORT).show();
            } else {
                currentTime = simpleDateFormat.format(calendar.getTime());
                Message message = new Message(currentTime,SenderName + " : " +  enteredMessage, broadcastModel.getUid());
                broadcastModel.sendTextToDb(BroadcastTitle, message);
                getMessage.setText(null);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
        broadcastModel.status("online");
    }

}