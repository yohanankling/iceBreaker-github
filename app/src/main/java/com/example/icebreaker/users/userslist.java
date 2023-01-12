package com.example.icebreaker.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.icebreaker.Home;
import com.example.icebreaker.R;
import com.example.icebreaker.chats.*;
import com.example.icebreaker.users.model.userlistModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Map;

// This class represents the user list screen of the app
public class userslist extends AppCompatActivity {

    userlistModel userslistModel = new userlistModel();

    // UI elements
    private ImageButton back;
    private RecyclerView recyclerView;

    // Adapter for displaying the user list in a RecyclerView
    private FirestoreRecyclerAdapter<FirebaseUser, userDetailes> chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Initialize fields
        initFields();

        // Set up the user list
        initChats();

        // Set up the back button
        backBtn();
    }


    // Initialize fields
    private void initFields() {
        recyclerView = findViewById(R.id.recyclerview);
        back = findViewById(R.id.back);
        TextView title = findViewById(R.id.Title);
        title.setText("Users");
        findViewById(R.id.noChats).setVisibility(View.INVISIBLE);
    }

    // Set up the back button to finish the activity
    private void backBtn() {
        back.setOnClickListener(v -> finish());
    }

    // Inner class representing a user in the user list
    public class userDetailes extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView status;

        public userDetailes(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.UserName);
            status = itemView.findViewById(R.id.Status);
        }

        private void bindUser(FirebaseUser user) {
            username.setText(" " + user.getEmail() + " ");
            status.setText(" " + user.getStatus() + " ");

            // If the user is online, set the status text to green
            if (user.getStatus().equals("online")) {
                status.setTextColor(Color.GREEN);
            }
        }
    }

    // Set up the user list using a FirestoreRecyclerAdapter
    private void initChats() {
        // Query the database for users that are not the current user
        Query query = userslistModel.getQuryData();
        // Set up the options for the adapter
        FirestoreRecyclerOptions<FirebaseUser> allusername = new FirestoreRecyclerOptions.Builder<FirebaseUser>().setQuery(query, FirebaseUser.class).build();
        // Create the adapter
        chatAdapter = new FirestoreRecyclerAdapter<FirebaseUser, userDetailes>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull userDetailes userDetailes, int i, @NonNull FirebaseUser FirebaseUser) {
                // Bind the user data to the view holder
                userDetailes.bindUser(FirebaseUser);
                userDetailes.itemView.setOnClickListener(v -> showUserDetails(FirebaseUser.getUid()));
            }

            @NonNull
            @Override
            public userDetailes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Inflate the layout for the view holder
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlistview, parent, false);
                return new userDetailes(view);
            }
        };
        // Set the adapter for the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
    }

    // Show the details of a user when clicked
    private void showUserDetails(String userUId) {
        // Query the database for the user with the given ID
        DocumentReference docRef = userslistModel.getDocRef(userUId);
        docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If the user was found, get their data as a map
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> userData = document.getData();

                            // Extract the relevant fields from the map
                            String email = (String) userData.get("email");
                            String name = (String) userData.get("name");
                            String topic = (String) userData.get("topic");

                            // Create an alert dialog to display the user's details
                            AlertDialog.Builder builder = new AlertDialog.Builder(userslist.this);
                            final View PopUpStatus = getLayoutInflater().inflate(R.layout.status_popup, null);
                            ImageButton backBtn = PopUpStatus.findViewById(R.id.back);
                            ImageView male = PopUpStatus.findViewById(R.id.MaleUserPic);
                            ImageView female = PopUpStatus.findViewById(R.id.FemaleUserPic);
                            TextView Details = PopUpStatus.findViewById(R.id.Details);
                            Details.setText("user details:");
                            TextView nametext = PopUpStatus.findViewById(R.id.Name);
                            TextView emailtext = PopUpStatus.findViewById(R.id.Mail);
                            TextView topictext = PopUpStatus.findViewById(R.id.Topic);
                            Button chat = PopUpStatus.findViewById(R.id.chatwith);
                            chat.setVisibility(View.VISIBLE);
                            TextView SocialClass = PopUpStatus.findViewById(R.id.SocialClass);

                            // Set back button
                            backBtn.setOnClickListener(v -> {
                                Intent intent = new Intent(userslist.this, Home.class);
                                startActivity(intent);
                            });

                            // Set chat button
                            chat.setOnClickListener(v -> {
                                Intent intent = new Intent(userslist.this, Chat.class);
                                intent.putExtra("Uid", userUId);
                                intent.putExtra("Email", userData.get("email").toString());
                                intent.putExtra("MyName", "admin");
                                startActivity(intent);
                            });

                            // Set the user's data in the dialog
                            if (userData.get("gender").toString().equals("Male")) {
                                male.setVisibility(View.VISIBLE);
                            } else female.setVisibility(View.VISIBLE);
                            nametext.setText(name);
                            emailtext.setText("mail: " + email);
                            topictext.setText("topic: " + topic);
                            // TODO: add game
                            // Gametext.setText("gmae: " + game);
                            SocialClass.setText("access: user");
                            builder.setView(PopUpStatus);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatAdapter.startListening();
        userslistModel.status("online");
    }
}