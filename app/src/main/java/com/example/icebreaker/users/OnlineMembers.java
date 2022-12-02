package com.example.icebreaker.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.icebreaker.CostumBaseadapter;
import com.example.icebreaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OnlineMembers extends AppCompatActivity {

    private ListView userListView;
    private ArrayList<String> users = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_members);
        initFields();
        getUsersList();
        setChatChoose();
    }

    private void initFields() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userListView = findViewById(R.id.UserListListView);
    }

    private void getUsersList() {
        databaseReference.child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (!dataSnapshot.child("Email").getValue().toString().equals(mAuth.getCurrentUser().getEmail())){
                            String Toshow  = dataSnapshot.child("Email").getValue().toString();
//                                   + " | Area : " + dataSnapshot.child("Area").getValue().toString();
                            users.add(Toshow);
                        }
                    }
                    CostumBaseadapter costumBaseadapter = new CostumBaseadapter(com.example.icebreaker.users.OnlineMembers.this, users);
                    userListView.setAdapter(costumBaseadapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(com.example.icebreaker.users.OnlineMembers.this, "faild to load users..try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setChatChoose() {
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(com.example.icebreaker.users.OnlineMembers.this, com.example.icebreaker.chats.Chat.class);
                intent.putExtra("Email", users.get(position));
                startActivity(intent);
            }
        });
    }
}