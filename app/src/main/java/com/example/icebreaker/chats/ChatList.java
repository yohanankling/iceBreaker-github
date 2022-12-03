package com.example.icebreaker.chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.icebreaker.CostumBaseadapter;
import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatList extends AppCompatActivity {

    private ListView userListView;
    private ArrayList<String> usersName = new ArrayList<>();
    private ArrayList<String> usersId = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ImageButton backBtn;
    private int senderUid = 0, senderName = 1, recieverUid = 2, recieverName = 3;
    private String [] PutExtraData = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        initFields();
        backBtn();
        getUsersList();
        setChatChoose();
    }

    private void initFields() {
        backBtn = findViewById(R.id.back);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userListView = findViewById(R.id.UserListListView);
    }

    private void backBtn(){
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ChatList.this, Home.class);
            startActivity(intent);
        });
    }

    private void getUsersList() {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (!dataSnapshot.child("Email").getValue().toString().equals(mAuth.getCurrentUser().getEmail())){
                            String name  = dataSnapshot.child("Name").getValue().toString();
//                                   + " | Area : " + dataSnapshot.child("Area").getValue().toString();
                            usersName.add(name);
                            usersId.add(dataSnapshot.getKey());
                        }
                        else {
                            PutExtraData[senderUid] = dataSnapshot.getKey();
                            PutExtraData[senderName] = dataSnapshot.child("Name").getValue().toString();
                        }
                    }
                    CostumBaseadapter costumBaseadapter = new CostumBaseadapter(ChatList.this, usersName);
                    userListView.setAdapter(costumBaseadapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatList.this, "faild to load users..try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setChatChoose() {
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChatList.this, Chat.class);
                PutExtraData [recieverUid] = usersId.get(position);
                PutExtraData [recieverName] = usersName.get(position);
                intent.putExtra("data", PutExtraData);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        setStatus("offline");
    }

}