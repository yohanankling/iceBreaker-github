package com.example.icebreaker.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.icebreaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Chat extends AppCompatActivity {

    private EditText message;
    private Button send;
    private ListView chatListView;
    private ArrayAdapter arrayAdapter;
    private ArrayList <String> messages = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initFields();
        sendBtn();
    }
    private void initFields() {
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        chatListView = findViewById(R.id.chatList);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Email = mAuth.getCurrentUser().getEmail();
        String FriendEmail = getIntent().getStringExtra("Email");
        setTitle("Chat with" + FriendEmail);
    }

    private void sendBtn() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getText().toString().isEmpty()){
                    Toast.makeText(Chat.this, "can't send empty message..", Toast.LENGTH_SHORT).show();
                }
                Map<String, String> messageData = new HashMap<>();
                messageData.put("sender", Email);
                String FriendEmail = getIntent().getStringExtra("Email");
                messageData.put("receiver", FriendEmail);
                messageData.put("message", message.getText().toString());

                databaseReference.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count;
                        if (snapshot.exists()){
                            count = (int) snapshot.getChildrenCount() +1;
                        } else count = 1;
                        databaseReference.child("messages").child(String.valueOf(count)).setValue(messageData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    message.setText("");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Chat.this, "Error in sending message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        databaseReference.child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.child("sender").getValue().toString().equals(Email) || dataSnapshot.child("reciever").getValue().toString().equals(Email)){
                            String message = dataSnapshot.child("message").getValue().toString();
                            if (!dataSnapshot.child("sender").getValue().toString().equals(Email)){
                                message = "> " + message;
                            }
                            messages.add(message);
                        }
                    }
                    arrayAdapter = new ArrayAdapter(Chat.this, android.R.layout.simple_list_item_1, messages);
                    chatListView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        setStatus("offline");
    }

    private void setStatus(String state) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String UserId = firebaseAuth.getCurrentUser().getUid();
        String SaveCurrentDate, SaveCurrentTime;
        Calendar date = Calendar.getInstance();
        SimpleDateFormat CurrentDate = new SimpleDateFormat("MMM dd yyyy");
        SaveCurrentDate = CurrentDate.format(date.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat CurrentTime = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime = CurrentTime.format(time.getTime());
        String LastSeen = SaveCurrentDate + "" + SaveCurrentTime;

        if (state.equals("offline")) {
            firebaseAuth.signOut();
            databaseReference.child("online").child(UserId).removeValue();
            databaseReference.child("offline").child(UserId).setValue(LastSeen);
        }
    }

}