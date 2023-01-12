package com.example.icebreaker.chats.model;

import com.example.icebreaker.chats.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatListModel {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseDatabase firebaseDatabase;
    private final FirebaseFirestore firebaseFirestore;

    public ChatListModel() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public String getUid() {
        return this.firebaseAuth.getUid();
    }

    public DatabaseReference getDbRef() {
        return firebaseDatabase.getReference().child("chats").child(firebaseAuth.getUid());
    }

    public DocumentReference getFsRef(DataSnapshot dataSnapshot) {
        return firebaseFirestore.collection("Users").document(dataSnapshot.getKey());
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
