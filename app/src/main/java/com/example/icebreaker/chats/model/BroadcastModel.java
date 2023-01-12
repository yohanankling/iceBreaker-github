package com.example.icebreaker.chats.model;

import com.example.icebreaker.chats.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BroadcastModel {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseDatabase firebaseDatabase;
    private final FirebaseFirestore firebaseFirestore;

    public BroadcastModel() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public String getUid() {
        return this.firebaseAuth.getUid();
    }

    public DatabaseReference getDbRef(String BroadcastTitle) {
        return firebaseDatabase.getReference().child("broadcast").child(BroadcastTitle);
    }

    public void sendTextToDb(String BroadcastTitle, Message message) {
        firebaseDatabase.getReference().child("broadcast").child(BroadcastTitle).push()
                .setValue(message);
    }

    public void status(String status) {
        DocumentReference documentReference = this.firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(unused -> {
        });
    }

}
