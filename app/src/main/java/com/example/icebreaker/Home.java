package com.example.icebreaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.icebreaker.chats.*;
import com.example.icebreaker.contactUs.Contact;
import com.example.icebreaker.gameZone.PlayerName;
import com.example.icebreaker.topic.ChooseTopic;
import com.example.icebreaker.topic.TopicMembersList;
import com.example.icebreaker.users.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {

    // Declare variables for UI elements
    private Button TopicChooser;
    private Button TopicMembers;
    private Button ContactUs;
    private ImageButton chat;
    private ImageButton PlayWith;
    private ImageButton Broadcast;

    // Declare Firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;

    // Declare User object
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initFields();
        TopicChooserButton();
        TopicMembersButton();
        BroadcastButton();
        PlayWithButton();
        ChatsButton();
        ContactUsBtn();

        // TODO:: add last message review
        // TODO:: add alert to new message
        // TODO:: add not read yet logo to messages
        // TODO: set X O platform
        // TODO:: fix keyboard resizeable

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                StatusButton();
                return true;
            case R.id.disconnect:
                DisconnectDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    // Initialize fields and retrieve user data from Firebase
    private void initFields() {
        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        TopicChooser = findViewById(R.id.TopicChooser);
        TopicMembers = findViewById(R.id.TopicMembers);
        Broadcast = findViewById(R.id.broadcast);
        PlayWith = findViewById(R.id.game);
        chat = findViewById(R.id.chats);
        ContactUs = findViewById(R.id.contactUs);

        // Initialize User object with default values
        user = new User("", "", "", "", "", "", true, false, true);
        user.setId(firebaseAuth.getCurrentUser().getUid());

        // Retrieve user data from Firebase
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(user.getId());
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    user.setEmail(userData.get("email").toString());
                    user.setName(userData.get("name").toString());
                    user.setGender(userData.get("gender").toString());
                    user.setTopic(userData.get("topic").toString());
                }
                // If user has admin access, initialize admin functions
                if (user.getEmail().equals("admin@gmail.com")) {
                    user.setAdminAccess(true);
                    initAdminFunc();
                }
            }
        });
    }

    private void initAdminFunc() {
        Button userList = findViewById(R.id.UserList);
        userList.setVisibility(View.VISIBLE);
        userList.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, userslist.class);
            startActivity(intent);
        });
        Button removeUser = findViewById(R.id.RemoveUser);
        removeUser.setVisibility(View.VISIBLE);
        removeUser.setOnClickListener(v -> removeUser());
    }

    private void removeUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        final View removeuser = getLayoutInflater().inflate(R.layout.remove_user, null);
        ImageButton backBtn = removeuser.findViewById(R.id.back);
        EditText MailToRemove = removeuser.findViewById(R.id.MailToRemove);
        Button Remove = removeuser.findViewById(R.id.remove);
        Remove.setOnClickListener(v12 -> {
            String removeMail = MailToRemove.getText().toString();
            Query query = firebaseFirestore.collection("Users").whereEqualTo("email", removeMail);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(Home.this, "mail not found", Toast.LENGTH_SHORT).show();
                    } else {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        DocumentSnapshot document = documents.get(0);
                        Map<String, Object> userData = document.getData();
                        String removeUid = userData.get("uid").toString();
                        String removedEmail = userData.get("email").toString();
//                        firebaseFirestore.collection("Users").document(removeUid).delete();
                        DocumentReference documentReference = firebaseFirestore.collection("Banned").document(removeUid);
                        Map<String, Object> BannedData = new HashMap<>();
                        BannedData.put("email", removedEmail);
                        documentReference.set(BannedData).addOnSuccessListener(unused -> {
                        });
                        Toast.makeText(Home.this, removeMail + " will banned soon", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        });

        Button CancelBtn = removeuser.findViewById(R.id.Cancel);
        backBtn.setOnClickListener(v12 -> finish());
        CancelBtn.setOnClickListener(v1 -> finish());
        builder.setView(removeuser);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkifbanned();
//        listenToNewMessages();
    }

    private void checkifbanned() {
        DocumentReference documentReference = firebaseFirestore.collection("Banned").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener((value, error) -> {
            if (value.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("its appear you are banned");
                builder.setMessage("if you have an objection you can contact us on the mail :\nourIceBreaker@gmail.com");
                builder.setPositiveButton("ok", (dialog, which) ->
                {
                    firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).delete();
                    Toast.makeText(Home.this, "bye-bye!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    status("offline");
                    firebaseAuth.signOut();
                    startActivity(intent);
                });
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Home.this.status("online");
            }
        });
    }

    private void listenToNewMessages() {
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(Home.this, "you have got a message!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

        // Set OnClickListener for Status button
    private void StatusButton() {
        // Show dialog to allow user to see their details
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        final View PopUpStatus = getLayoutInflater().inflate(R.layout.status_popup, null);
        ImageButton backBtn = PopUpStatus.findViewById(R.id.back);
        ImageView male = PopUpStatus.findViewById(R.id.MaleUserPic);
        ImageView female = PopUpStatus.findViewById(R.id.FemaleUserPic);
        TextView name = PopUpStatus.findViewById(R.id.Name);
        TextView mail = PopUpStatus.findViewById(R.id.Mail);
        TextView topic = PopUpStatus.findViewById(R.id.Topic);
        TextView Game = PopUpStatus.findViewById(R.id.Game);
        TextView SocialClass = PopUpStatus.findViewById(R.id.SocialClass);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Home.class);
            startActivity(intent);
        });
        if (user.getGender().equals("Male")) {
            male.setVisibility(View.VISIBLE);
        } else female.setVisibility(View.VISIBLE);
        name.setText(user.getName());
        mail.setText("mail: " + user.getEmail());
        topic.setText("topic: " + user.getTopic());
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

    // Set OnClickListener for Topic Chooser button
    private void TopicChooserButton() {
        TopicChooser.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, ChooseTopic.class);
            intent.putExtra("Name", user.getName());
            startActivity(intent);
        });
    }

    // Set OnClickListener for Topic Members button
    private void TopicMembersButton() {
        TopicMembers.setOnClickListener(view -> {
            DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
            documentReferenceuser.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> userData = document.getData();
                        String Title = (String) userData.get("topic");
                        if(!Title.equals("~null")){
                            Intent intent = new Intent(Home.this, TopicMembersList.class);
                            intent.putExtra("Title", Title);
                            startActivity(intent);
                        }
                        else Toast.makeText(this, "must have topic first..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    // Set OnClickListener for Broadcast button
    private void BroadcastButton() {
        Broadcast.setOnClickListener(view -> {
            DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
            documentReferenceuser.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> userData = document.getData();
                        String Title = (String) userData.get("topic");
                        String UserName = (String) userData.get("name");
                        if(!Title.equals("~null")){
                            Intent intent = new Intent(Home.this, Broadcast.class);
                            intent.putExtra("Title", Title);
                            intent.putExtra("Name", UserName);
                            startActivity(intent);
                        }
                        else Toast.makeText(this, "must have topic first..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    // Set OnClickListener for Play With button
    private void PlayWithButton() {
        PlayWith.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, PlayerName.class);
            intent.putExtra("Email", user.getEmail());
            startActivity(intent);
//            Toast.makeText(this, "area under construction..", Toast.LENGTH_SHORT).show();
        });
    }

    // Set OnClickListener for Chats button
    private void ChatsButton() {
        chat.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, ChatList.class);
            startActivity(intent);
        });
    }

    // Set OnClickListener for Contact Us button
    private void ContactUsBtn() {
        ContactUs.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, Contact.class);
            startActivity(intent);
        });
    }

    private void DisconnectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(user.getName() + " ,you sure?");
        builder.setMessage("");
        builder.setPositiveButton("yes please", (dialog, which) ->
        {
            Toast.makeText(Home.this, "bye-bye!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Home.this, MainActivity.class);
            status("offline");
            firebaseAuth.signOut();
            startActivity(intent);
        });
        builder.setNegativeButton("no", (dialog, which) ->
                Toast.makeText(Home.this, "good to have you back!", Toast.LENGTH_SHORT).show()
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void status(String status) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(unused -> {
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()) {
            status("offline");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DisconnectDialog();
    }
}