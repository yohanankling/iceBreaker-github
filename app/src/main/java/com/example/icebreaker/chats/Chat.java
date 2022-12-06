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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Chat extends AppCompatActivity {

    private EditText getMessage;
    private ImageButton back;
    private Button sendBtn;
    private TextView name, status;
    private String enteredMessage;
    String Recievername, RecieverUid, SenderUid;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    String senderRoom, recieverRoom;
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
    }

    @SuppressLint("SimpleDateFormat")
    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
        back = findViewById(R.id.Back);
        getMessage = findViewById(R.id.getMessage);
        status = findViewById(R.id.Status);
        recyclerView = findViewById(R.id.recyclerview);
        sendBtn = findViewById(R.id.send);
        name = findViewById(R.id.UserName);
        SenderUid = firebaseAuth.getUid();
        RecieverUid = getIntent().getStringExtra("Uid");
        Recievername = getIntent().getStringExtra("Email");
        senderRoom = SenderUid + RecieverUid;
        recieverRoom = RecieverUid + SenderUid;
        name.setText(Recievername);
        messageArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(Chat.this, messageArrayList);
        recyclerView.setAdapter(messagesAdapter);
    }

    private void initStatus(){
        DocumentReference document = FirebaseFirestore.getInstance().collection("Users").document(RecieverUid);
        document.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                String s = documentSnapshot.getString("status");
                status.setText(s);
            }
        }).addOnFailureListener(e -> Toast.makeText(Chat.this,
                "failed to fetch data", Toast.LENGTH_SHORT).show());
    }

    private void initMessages() {
//        messageArrayList.clear();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    messageArrayList.add(message);
                }
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
                Message message = new Message(currentTime, enteredMessage, firebaseAuth.getUid());
                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages").push()
                        .setValue(message).addOnCompleteListener(task -> firebaseDatabase.getReference().
                                child("chats").child(recieverRoom).child("messages").push()
                                .setValue(message).addOnCompleteListener(task1 -> {
                                }));
                getMessage.setText(null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (messagesAdapter != null){
            messagesAdapter.notifyDataSetChanged();
        }
    }
}