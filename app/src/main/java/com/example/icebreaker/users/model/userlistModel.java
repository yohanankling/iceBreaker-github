package com.example.icebreaker.users.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class userlistModel {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;

    public userlistModel() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Query getQuryData() {
        return firebaseFirestore.collection("Users").whereNotEqualTo("uid", firebaseAuth.getUid());
    }
    public DocumentReference getDocRef(String userUId) {
        return firebaseFirestore.collection("Users").document(userUId);
    }

    public void status(String status) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(unused -> {
        });
    }
}
