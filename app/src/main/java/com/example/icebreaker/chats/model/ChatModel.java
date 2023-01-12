package com.example.icebreaker.chats.model;

import android.app.Activity;

import com.example.icebreaker.chats.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatModel {
    private Activity activity;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;

    public ChatModel(Activity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public String getUid() {
        return this.firebaseAuth.getUid();
    }

    public DatabaseReference getDbRef(String SenderUid, String RecieverUid) {
        return firebaseDatabase.getReference().child("chats").child(SenderUid).child(RecieverUid);
    }

    public void sendTextToDb(String SenderUid, String RecieverUid, Message message) {
        firebaseDatabase.getReference().child("chats").child(SenderUid).child(RecieverUid).push()
                .setValue(message).addOnCompleteListener(task -> firebaseDatabase.getReference().
                        child("chats").child(RecieverUid).child(SenderUid).push()
                        .setValue(message).addOnCompleteListener(task1 -> {
                        }));
    }

    public void status(String status) {
        DocumentReference documentReference = this.firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(unused -> {
        });
    }

}
