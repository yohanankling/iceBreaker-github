package com.example.icebreaker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.util.Calendar;
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
import com.example.icebreaker.contactUs.Contact;
import com.example.icebreaker.gameZone.PlayZone;
import com.example.icebreaker.users.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class Home extends AppCompatActivity {

    //Todo:: disconnect from online list

    private Button Status, OnlineMember, Broadcast, PlayWith, chat, ContactUs, UserList, RemoveUser, Disconnect;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private User user;

    public class OtherUser{
        private String Uid;
        private String Email;
        public OtherUser(String Uid, String Email){
            this.Uid = Uid;
            this.Email = Email;
        }
    }

    private OtherUser otherUser = new OtherUser("","");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initFields();
//        if (user.haveAdminAccess()) {
        initAdminFunc();
//        }
        StatusButton();
        OnlineButton();
        BroadcastButton();
        PlayWithButton();
        ChatsButton();
        ContactUsBtn();
        DisconnectButton();
    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Status = findViewById(R.id.Status);
        OnlineMember = findViewById(R.id.OnlineMember);
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
            setStatus("offline");
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
            Intent intent = new Intent(Home.this, UsersList.class);
            startActivity(intent);
        });
        RemoveUser = findViewById(R.id.RemoveUser);
        RemoveUser.setVisibility(View.VISIBLE);
        RemoveUser.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            final View removeuser = getLayoutInflater().inflate(R.layout.removeuser, null);
            ImageButton backBtn = removeuser.findViewById(R.id.back);
            EditText MailToRemove = removeuser.findViewById(R.id.MailToRemove);
            Button Remove = removeuser.findViewById(R.id.Remove);
            Remove.setOnClickListener(v12 -> {
                otherUser.Email = MailToRemove.getText().toString();
                getUIdByMail(otherUser.Email);
                if (otherUser.Uid.equals("")){
                    Toast.makeText(this, "mail not found", Toast.LENGTH_SHORT).show();
                }
                else {
                    // TODO:: remove
                }
            });
            Button CancelBtn = removeuser.findViewById(R.id.Cancel);
            backBtn.setOnClickListener(v12 -> {
                Intent intent = new Intent(Home.this, Home.class);
                startActivity(intent);
            });
            CancelBtn.setOnClickListener(v1 -> {
                Intent intent = new Intent(Home.this, Home.class);
                startActivity(intent);
            });
            builder.setView(removeuser);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void getUIdByMail(String removingMail) {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    boolean validMail = false;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String Email  = dataSnapshot.child("Email").getValue().toString();
                        if (removingMail.equals(Email)){
                            otherUser.Uid = dataSnapshot.getKey();
                            validMail = true;
                        }
                    }
                    if (!validMail) otherUser.Uid = "mail not found";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "faild to get UId..try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setStatus("online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setStatus("offline");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.isFinishing()){
            setStatus("offline");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()){
            setStatus("offline");
        }
    }

    @SuppressLint("SetTextI18n")
    private void StatusButton() {
        Status.setOnClickListener(view -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    final View PopUpStatus = getLayoutInflater().inflate(R.layout.popupstatus, null);
                    ImageButton backBtn = PopUpStatus.findViewById(R.id.back);
                    ImageView male = PopUpStatus.findViewById(R.id.MaleUserPic);
                    ImageView female = PopUpStatus.findViewById(R.id.FemaleUserPic);
                    TextView name = PopUpStatus.findViewById(R.id.Name);
                    TextView mail = PopUpStatus.findViewById(R.id.Mail);
                    TextView area = PopUpStatus.findViewById(R.id.Area);
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
                    area.setText("area: " + user.getArea());
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

    private void OnlineButton() {
//        OnlineMember.setOnClickListener(view -> {
//            LoginFirebase.child("online").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot Snapshot : snapshot.getChildren()) {
//                        String Name = Snapshot.getValue(String.class);
//                        OnlineMembers.add(Name);
//                    }
//                    Intent intent = new Intent(Home.this, OnlineMembers.class);
//                    intent.putStringArrayListExtra("arraylist", OnlineMembers);
//                    startActivity(intent);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                }
//
//            });
//
//            //TODO: import online members details from fire base
//        });
    }

    private void BroadcastButton() {
        Broadcast.setOnClickListener(view -> {
            //TODO: set message platform to broadcast
        });
    }

    private void PlayWithButton() {
        PlayWith.setOnClickListener(view -> {
            //TODO: set playwith X O platform
            Intent intent = new Intent(Home.this, PlayZone.class);
            intent.putExtra("Email", user.getEmail());
            startActivity(intent);
        });
    }

    private void ChatsButton() {
        chat.setOnClickListener(view -> {
            //TODO: chats
            Intent intent = new Intent(Home.this, chatActivity.class);
            intent.putExtra("Email", user.getEmail());
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
    public void onBackPressed() {
        super.onBackPressed();
        setStatus("offline");
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
            setStatus("offline");
            startActivity(intent);
        });
        builder.setNegativeButton("no", (dialog, which) ->
                Toast.makeText(Home.this, "good to have you back!", Toast.LENGTH_SHORT).show()
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setStatus(String state) {
        String SaveCurrentDate, SaveCurrentTime;
        Calendar date = Calendar.getInstance();
        SimpleDateFormat CurrentDate = new SimpleDateFormat("MMM dd yyyy");
        SaveCurrentDate = CurrentDate.format(date.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat CurrentTime = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime = CurrentTime.format(time.getTime());

        if (state.equals("offline")) {
            user.setOnline(false);
            firebaseAuth.signOut();
            user.setLastSeen(SaveCurrentDate + " | " + SaveCurrentTime);
            databaseReference.child("online").child(user.getId()).removeValue();
            databaseReference.child("offline").child(user.getId()).setValue(user.getLastSeen());
        } else databaseReference.child("online").child(user.getId());
    }
}