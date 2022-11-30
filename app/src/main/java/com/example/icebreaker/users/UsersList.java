package com.example.icebreaker.users;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.icebreaker.CostumBaseadapter;
import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.example.icebreaker.chats.Chat;
import com.example.icebreaker.chats.ChatList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class UsersList extends AppCompatActivity {

    private ListView userListView;
    private ArrayList<String> users = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ImageButton backBtn;
    private User user = new User("", "", "", "", "", "", true, false, true);

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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userListView = findViewById(R.id.UserListListView);
    }

    private void backBtn(){
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UsersList.this, Home.class);
            startActivity(intent);
        });
    }

    private void getUsersList() {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String Toshow  = dataSnapshot.child("Email").getValue().toString();
//                                   + " | Area : " + dataSnapshot.child("Area").getValue().toString();
                        users.add(Toshow);
                    }
                    CostumBaseadapter costumBaseadapter = new CostumBaseadapter(UsersList.this, users);
                    userListView.setAdapter(costumBaseadapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UsersList.this, "faild to load users..try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setChatChoose() {
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UsersList.this, Chat.class);
                intent.putExtra("Email", users.get(position));
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