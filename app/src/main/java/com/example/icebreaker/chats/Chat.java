package com.example.icebreaker.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.example.icebreaker.users.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private EditText message;
    private Button send;
    private TextView chatTitle;
    private ImageButton backBtn;
    private ListView chatListView;
    private ArrayAdapter arrayAdapter;
    private ArrayList <String> messages = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String myUid;
    public User sender = new User("", "", "", "", "", "", true, false, true);
    public User reciver = new User("", "", "", "", "", "", true, false, true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initFields();
        initUsers();
        chatTitle.setText(reciver.getName());
        sendBtn();
        backBtn();
    }

    private void initFields() {
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        backBtn = findViewById(R.id.back);
        chatListView = findViewById(R.id.chatList);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        myUid = mAuth.getCurrentUser().getUid();
        chatTitle = findViewById(R.id.chatTitle);
    }

    private void initUsers() {
        sender.setId(myUid);
        reciver.setId(getIntent().getStringExtra("FriendUid"));
//        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                sender.setEmail(snapshot.child(sender.getId()).child("Email").getValue(String.class));
//                sender.setName(snapshot.child(sender.getId()).child("Name").getValue(String.class));
//                sender.setGender(snapshot.child(sender.getId()).child("Gender").getValue(String.class));
//                reciver.setEmail(snapshot.child(reciver.getId()).child("Email").getValue(String.class));
//                reciver.setName(snapshot.child(reciver.getId()).child("Name").getValue(String.class));
//                reciver.setGender(snapshot.child(reciver.getId()).child("Gender").getValue(String.class));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
    }

    private void sendBtn() {
        send.setOnClickListener(v -> {
//            if(message.getText().toString().isEmpty()){
//                Toast.makeText(Chat.this, "can't send empty message..", Toast.LENGTH_SHORT).show();
//            }
//            Map<String, String> messageData = new HashMap<>();
////            messageData.put("sender", Email);
//            String FriendEmail = getIntent().getStringExtra("Email");
//            messageData.put("receiver", FriendEmail);
//            messageData.put("message", message.getText().toString());
//
//            databaseReference.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    int count;
//                    if (snapshot.exists()){
//                        count = (int) snapshot.getChildrenCount() +1;
//                    } else count = 1;
//                    databaseReference.child("messages").child(String.valueOf(count)).setValue(messageData).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()){
//                            message.setText("");
//                        }
//                    }).addOnFailureListener(e -> Toast.makeText(Chat.this, "Error in sending message: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//        });
//        databaseReference.child("chats").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        if (dataSnapshot.child("sender").getValue().toString().equals(Email) || dataSnapshot.child("reciever").getValue().toString().equals(Email)){
//                            String message = dataSnapshot.child("message").getValue().toString();
//                            if (!dataSnapshot.child("sender").getValue().toString().equals(Email)){
//                                message = "> " + message;
//                            }
//                            messages.add(message);
//                        }
//                    }
//                    arrayAdapter = new ArrayAdapter(Chat.this, android.R.layout.simple_list_item_1, messages);
//                    chatListView.setAdapter(arrayAdapter);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
        });
    }

    private void backBtn(){
        backBtn.setOnClickListener(v -> {
            Toast.makeText(this, "jjjjj", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Chat.this, Home.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        setStatus("offline");
    }


}