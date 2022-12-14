package com.example.icebreaker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.icebreaker.admin.*;
import com.example.icebreaker.contactUs.Contact;
import com.example.icebreaker.gameZone.TicTacToe;
import com.example.icebreaker.users.*;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    private Button Status, TopicChooser, TopicMembers, Broadcast, PlayWith, chat, ContactUs, UserList, RemoveUser, Disconnect;
    private FirebaseAuth firebaseAuth;
    public DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private User user;
    String MyTopic;

    public class OtherUser {
        private String Uid;
        private String Email;

        public OtherUser(String Uid, String Email) {
            this.Uid = Uid;
            this.Email = Email;
        }
    }

    private OtherUser otherUser = new OtherUser("", "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initFields();
//        if (user.haveAdminAccess()) {
        initAdminFunc();
//        }
        StatusButton();
        TopicChooserButton();
        TopicMembersButton();
        BroadcastButton();
        PlayWithButton();
        ChatsButton();
        ContactUsBtn();
        DisconnectButton();
    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Status = findViewById(R.id.Status);
        TopicChooser = findViewById(R.id.TopicChooser);
        TopicMembers = findViewById(R.id.TopicMembers);
        Broadcast = findViewById(R.id.Broadcast);
        PlayWith = findViewById(R.id.PlayWith);
        chat = findViewById(R.id.Chats);
        ContactUs = findViewById(R.id.userslist);
        Disconnect = findViewById(R.id.Disconnect);

        user = new User("", "", "", "", "", "", true, false, true);
        user.setOnline(true);
        user.setId(firebaseAuth.getCurrentUser().getUid());
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user.setEmail(snapshot.child(user.getId()).child("Email").getValue(String.class));
                user.setName(snapshot.child(user.getId()).child("Name").getValue(String.class));
                user.setGender(snapshot.child(user.getId()).child("Gender").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        if (!user.getOnline()) {
            Intent intent = new Intent(Home.this, MainActivity.class);
            status("offline");
            startActivity(intent);
        }
        if (user.getEmail().equals("admin@gmail.com")) {
            user.setAdminAccess(true);
        }
    }

    private void initAdminFunc() {
        UserList = findViewById(R.id.UserList);
        UserList.setVisibility(View.VISIBLE);
        UserList.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, userslist.class);
            startActivity(intent);
        });
        RemoveUser = findViewById(R.id.RemoveUser);
        RemoveUser.setVisibility(View.VISIBLE);
        RemoveUser.setOnClickListener(v -> removeUser());
    }

    private void removeUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        final View removeuser = getLayoutInflater().inflate(R.layout.remove_user, null);
        ImageButton backBtn = removeuser.findViewById(R.id.back);
        EditText MailToRemove = removeuser.findViewById(R.id.MailToRemove);
        Button Remove = removeuser.findViewById(R.id.remove);
        Remove.setOnClickListener(v12 -> {
            otherUser.Email = MailToRemove.getText().toString();

            databaseReference.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean validate = false;
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String Email = dataSnapshot.child("Email").getValue().toString();
                            if (Email.equals(otherUser.Email)) {
                                otherUser.Uid = dataSnapshot.getKey();
                                validate = true;
                            }
                        }
                        if (!validate) {
                            Toast.makeText(Home.this, "mail not found", Toast.LENGTH_SHORT).show();
                        } else {
                            DocumentReference documentReference = firebaseFirestore.collection("Banned").document(otherUser.Uid);
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", otherUser.Email);
                            documentReference.set(userData);
                            DocumentReference users = firebaseFirestore.collection("Users").document(otherUser.Uid);
                            users.delete();
                            Toast.makeText(Home.this, otherUser.Email + " will banned soon", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Home.this, "faild to get UId..try again", Toast.LENGTH_SHORT).show();
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
        DocumentReference documentReference = firebaseFirestore.collection("Banned").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener((value, error) -> {
            if (value.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("its appear you are banned");
                builder.setMessage("if you have an objection you can contact us on the mail :\n ourIceBreaker@gmail.com");
                builder.setPositiveButton("ok", (dialog, which) ->
                {
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

    @SuppressLint("SetTextI18n")
    private void StatusButton() {
        Status.setOnClickListener(view -> {
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
                    if (user.haveAdminAccess()) {
                        SocialClass.setText("access: user");
                    } else SocialClass.setText("access: admin");
                    builder.setView(PopUpStatus);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        );
    }

    private void TopicChooserButton() {
        TopicChooser.setOnClickListener(view -> {
            // TODO: add topic platform
            Intent intent = new Intent(Home.this, TopicMembers.class);
            intent.putExtra("Email", user.getEmail());
            startActivity(intent);
        });
    }

    private void TopicMembersButton() {
        TopicMembers.setOnClickListener(view -> {
            DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
            documentReferenceuser.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> userData = document.getData();
                        String Title = (String) userData.get("topic");
                        Intent intent = new Intent(Home.this, TopicMembersList.class);
                        intent.putExtra("Title", Title);
                        startActivity(intent);
                    }
                }
            });
        });
    }

    private void BroadcastButton() {
        Broadcast.setOnClickListener(view -> {
            //TODO: set message platform to broadcast
        });
    }

    private void PlayWithButton() {
        PlayWith.setOnClickListener(view -> {
            //TODO: set playwith X O platform
            Intent intent = new Intent(Home.this, TicTacToe.class);
            intent.putExtra("Email", user.getEmail());
            startActivity(intent);
        });
    }

    private void ChatsButton() {
        chat.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, ChatList.class);
            startActivity(intent);
        });
    }

    private void ContactUsBtn() {
        ContactUs.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, Contact.class);
            startActivity(intent);
        });
    }

    private void DisconnectButton() {
        Disconnect.setOnClickListener(view ->
                DisconnectDialog());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.isFinishing()) {
            status("offline");
        }
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
        documentReference.update("status", status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

}