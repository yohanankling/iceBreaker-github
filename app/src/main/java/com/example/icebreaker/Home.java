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
import com.example.icebreaker.gameZone.*;
import com.example.icebreaker.topic.*;
import com.example.icebreaker.users.*;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class Home extends AppCompatActivity {

    HomeModel homeModel = new HomeModel(this);

    // Declare variables for UI elements
    private Button TopicChooser;
    private Button TopicMembers;
    private Button ContactUs;
    private ImageButton chat;
    private ImageButton PlayWith;
    private ImageButton Broadcast;

    // Declare User object
    public User user;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile){
            StatusButton();
            return true;
        } else {
            DisconnectDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    // Initialize fields and retrieve user data from Firebase
    private void initFields() {
        // Initialize UI elements
        TopicChooser = findViewById(R.id.TopicChooser);
        TopicMembers = findViewById(R.id.TopicMembers);
        Broadcast = findViewById(R.id.broadcast);
        PlayWith = findViewById(R.id.game);
        chat = findViewById(R.id.chats);
        ContactUs = findViewById(R.id.contactUs);
        initUser();
    }


    public void initUser() {
        user = new User();
        user.setId(homeModel.getUid());
        // Retrieve user data from Firebase
        DocumentReference documentReference = homeModel.reference();
        documentReference.get().addOnCompleteListener(task -> {
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
                    initAdminFunc();
                }
            }
        });
    }

    public void initAdminFunc() {
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
            homeModel.removeUser(removeMail);
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
    }

    private void checkifbanned() {
        homeModel.checkifbanned();
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
            Game.setText("game: " + "available");
        if (!user.getEmail().equals("admin@gmail.com")) {
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
        TopicMembers.setOnClickListener(view ->
                homeModel.TopicMembersButtonPressed(user.getName()));
    }

    // Set OnClickListener for Broadcast button
    private void BroadcastButton() {
        Broadcast.setOnClickListener(view ->
                homeModel.broadcastButtonPressed());
    }

    // Set OnClickListener for Play With button
    private void PlayWithButton() {
        PlayWith.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, receiver.class);
            intent.putExtra("myName", user.getName());
            startActivity(intent);
        });
    }

    // Set OnClickListener for Chats button
    private void ChatsButton() {
        chat.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, ChatList.class);
            intent.putExtra("MyName", user.getName());
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
            homeModel.status("offline");
            homeModel.signOut();
            startActivity(intent);
        });
        builder.setNegativeButton("no", (dialog, which) ->
                Toast.makeText(Home.this, "good to have you back!", Toast.LENGTH_SHORT).show()
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        homeModel.status("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()) {
            homeModel.status("offline");
        }
    }
}