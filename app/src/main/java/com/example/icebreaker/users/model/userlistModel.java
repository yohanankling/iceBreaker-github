package com.example.icebreaker.users.model;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.icebreaker.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;


public class userlistModel {
    private Activity activity;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public userlistModel(Activity activity) {
        this.activity = activity;
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
