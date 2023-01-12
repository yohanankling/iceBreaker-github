package com.example.icebreaker.users.model;

import android.app.Activity;
import android.content.Intent;

import com.example.icebreaker.Home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


public class LoginModel {
    private final Activity activity;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;

    public LoginModel(Activity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void setAndInitStatus(String Email, String Password) {
        firebaseAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String UserId = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(UserId);
                        documentReferenceuser.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                DocumentSnapshot document = task2.getResult();
                                if (document.exists()) {
                                    Map<String, Object> userData = document.getData();
                                    userData.replace("status", "online");
                                    documentReferenceuser.set(userData);
                                }
                                Intent intent = new Intent(this.activity, Home.class);
                                this.activity.startActivity(intent);
                                this.activity.finish();
                            }
                        });
                    }
                });    }
}