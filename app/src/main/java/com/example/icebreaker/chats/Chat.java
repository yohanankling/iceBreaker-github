package com.example.icebreaker.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icebreaker.CostumBaseadapter;
import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.example.icebreaker.users.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    private EditText message;
    private Button send;
    private TextView chatTitle;
    private ImageButton backBtn;
    private ListView chatListView;
    private ArrayAdapter arrayAdapter;
    private ArrayList <String> messages = new ArrayList<>();
    private DatabaseReference databaseReference;
    private int senderUid = 0, senderName = 1, recieverUid = 2, recieverName = 3;
    public User sender = new User("", "", "", "", "", "", true, false, true);
    public User reciver = new User("", "", "", "", "", "", true, false, true);
    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initFields();
        initUsers();
        chatTitle.setText(" " + reciver.getName());
        initMessages();
        sendBtn();
        backBtn();
    }

    private void initFields() {
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        backBtn = findViewById(R.id.back);
        chatListView = findViewById(R.id.chatList);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        chatTitle = findViewById(R.id.chatTitle);
        data = getIntent().getStringArrayExtra("data");
        arrayAdapter = new ArrayAdapter(Chat.this, android.R.layout.simple_list_item_1, messages);
        chatListView.setAdapter(arrayAdapter);
    }

    private void initUsers() {
        sender.setId(data[senderUid]);
        sender.setName(data[senderName]);
        reciver.setId(data[recieverUid]);
        reciver.setName(data[recieverName]);
    }

    private void initMessages() {
        arrayAdapter.clear();
        databaseReference.child("messages").child(sender.getId()).child(reciver.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String message = dataSnapshot.getValue().toString();
                        messages.add(message);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }
            //
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendBtn() {
        send.setOnClickListener(v -> {
            if(message.getText().toString().isEmpty()){
                Toast.makeText(Chat.this, "can't send empty message..", Toast.LENGTH_SHORT).show();
                return;
            }
            String messageData = message.getText().toString();

            databaseReference.child("messages").child(sender.getId()).child(reciver.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 1;
                    if (snapshot.exists()){
                        count = (int) snapshot.getChildrenCount() +1;
                    }
                    databaseReference.child("messages").child(sender.getId()).child(reciver.getId()).child(String.valueOf(count)).setValue(sender.getName() + " : " + messageData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            message.setText("");
                        }
                    }).addOnFailureListener(e -> Toast.makeText(Chat.this, "Error in sending message: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    databaseReference.child("messages").child(reciver.getId()).child(sender.getId()).child(String.valueOf(count)).setValue(sender.getName() + " : " + messageData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            message.setText("");
                        }
                    }).addOnFailureListener(e -> Toast.makeText(Chat.this, "Error in sending message: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });
        initMessages();
    }

    private void backBtn(){
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Chat.this, Home.class);
            startActivity(intent);
        });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        setStatus("offline");
//    }
//
//
}