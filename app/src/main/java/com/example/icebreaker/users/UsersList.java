package com.example.icebreaker.users;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UsersList extends AppCompatActivity {

    private ListView userListView;
    private ArrayList<String> users = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;    private ImageButton backBtn;
    private ArrayList<String> usersId = new ArrayList<>();
    User user = new User("", "", "", "", "", "", true, true, true);


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
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        firebaseFirestore = FirebaseFirestore.getInstance();
        userListView = findViewById(R.id.UserListListView);
    }

    private void backBtn() {
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UsersList.this, Home.class);
            startActivity(intent);
        });
    }

    private void getUsersList() {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String Toshow = dataSnapshot.child("Email").getValue().toString();
//                                   + " | Area : " + dataSnapshot.child("Area").getValue().toString();
                        users.add(Toshow);
                        usersId.add(dataSnapshot.getKey());

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
                user.setId(usersId.get(position));
                getUserInfo();
                // TODO :: check how to change object by reference and not by value
            }

            private void getUserInfo() {
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user.setEmail(snapshot.child(user.getId()).child("Email").getValue(String.class));
                        user.setName(snapshot.child(user.getId()).child("Name").getValue(String.class));
                        user.setGender(snapshot.child(user.getId()).child("Gender").getValue(String.class));

                        AlertDialog.Builder builder = new AlertDialog.Builder(UsersList.this);
                        final View PopUpStatus = getLayoutInflater().inflate(R.layout.popupstatus, null);
                        ImageButton backBtn = PopUpStatus.findViewById(R.id.back);
                        ImageView male = PopUpStatus.findViewById(R.id.MaleUserPic);
                        ImageView female = PopUpStatus.findViewById(R.id.FemaleUserPic);
                        TextView Details = PopUpStatus.findViewById(R.id.Details);
                        Details.setText("user details");
                        TextView name = PopUpStatus.findViewById(R.id.Name);
                        TextView mail = PopUpStatus.findViewById(R.id.Mail);
                        TextView area = PopUpStatus.findViewById(R.id.Area);
                        TextView Game = PopUpStatus.findViewById(R.id.Game);
                        TextView SocialClass = PopUpStatus.findViewById(R.id.SocialClass);
                        backBtn.setOnClickListener(v -> {
                            Intent intent = new Intent(UsersList.this, UsersList.class);
                            startActivity(intent);
                        });
                        if (user.getGender().equals("Male")) {
                            male.setVisibility(View.VISIBLE);
                        } else female.setVisibility(View.VISIBLE);
                        name.setText(user.getName());
                        mail.setText("mail: " + user.getEmail());
                        area.setText("area: " + user.getArea());
                        if (user.isGameAvailable()) {
                            Game.setText("game: " + "available");
                        } else Game.setText("game: " + "not available");
                        if (!user.haveAdminAccess()) {
                            SocialClass.setText("access: user");
                        } else SocialClass.setText("access: admin");
                        builder.setView(PopUpStatus);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        status("online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()){
            status("offline");
        }
    }

    private void status(String status) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

}