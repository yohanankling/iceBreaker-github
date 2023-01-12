package com.example.icebreaker.topic.model;

import android.app.Activity;
import android.content.Intent;

import com.example.icebreaker.Home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


public class TopicMembersListModel {
    private Activity activity;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public TopicMembersListModel(Activity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public String getUid() {
        return firebaseAuth.getUid();
    }

    public DocumentReference getDocRef(String Title) {
        return firebaseFirestore.collection("Topics").document(Title);
    }

    public void status(String status) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(unused -> {
        });
    }
}