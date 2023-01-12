package com.example.icebreaker.users.model;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.icebreaker.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterModel {
    private final Activity activity;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;

    public RegisterModel(Activity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void ValidEmailOnDatabase(String Email, String Password, String Name, boolean MaleClicked) {
        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StoreToDatabase(Name, Email, Password, MaleClicked);
                Toast.makeText(this.activity, "registered successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this.activity, MainActivity.class);
                firebaseAuth.signOut();
                this.activity.startActivity(intent);
                this.activity.finish();
            } else
                Toast.makeText(this.activity, "oh! please try again..", Toast.LENGTH_SHORT).show();}).
                addOnFailureListener(e ->
                        Toast.makeText(this.activity, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()).
                addOnCanceledListener(() ->
                        Toast.makeText(this.activity, "canceled, try again!", Toast.LENGTH_SHORT).show());
    }

    private void StoreToDatabase(String Name, String Email, String Password, boolean MaleClicked) {
        String UserId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(UserId);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", UserId);
                userData.put("email", Email);
                userData.put("name", Name);
                userData.put("password", Password);
                userData.put("status", "offline");
                if(MaleClicked){
                    userData.put("gender", "Male");
                } else userData.put("gender", "Female");
                userData.put("topic", "~null");
                documentReference.set(userData);
            }
        });
    }
}