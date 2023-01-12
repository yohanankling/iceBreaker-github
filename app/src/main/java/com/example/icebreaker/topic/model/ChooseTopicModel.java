package com.example.icebreaker.topic.model;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.icebreaker.topic.ChooseTopic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ChooseTopicModel {
    private final Activity activity;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;

    public ChooseTopicModel(Activity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Query getQueryData() {
    return firebaseFirestore.collection("Topics");
    }

    public DocumentReference getFirebaseFirestore() {
    return firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
    }

    public void leaveTitle(String tiltleRegistered) {
        DocumentReference documentReference = firebaseFirestore.collection("Topics").document(tiltleRegistered);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> topicData = document.getData();
                    if (topicData.containsKey(firebaseAuth.getUid())){
                        long members = (long) topicData.get("Members");
                        if (members == 1){
                            documentReference.delete();
                        }else {
                            topicData.remove(firebaseAuth.getUid());
                            topicData.replace("Members", members - 1);
                            documentReference.set(topicData);
                        }
                    }
                }
            }
        });
    }

    public void registerdToTopic(String Title, long members, String Name) {
        DocumentReference documentReference = firebaseFirestore.collection("Topics").document(Title);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    userData.put(firebaseAuth.getUid(),Name);
                    userData.replace("Members", members + 1);
                    documentReference.set(userData);
                }
            }
        });
    }

    public void changeUserData(String Title) {
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    String tiltleRegistered = (String) userData
                            .get("topic");
                    if (!tiltleRegistered.equals("~null")) {
                        leaveTitle(tiltleRegistered);
                    }
                    userData.replace("topic", Title);
                    documentReferenceuser.set(userData);
                }
            }
        });
    }

    public void ValidateTopic(String TopicName, String Name) {
        DocumentReference documentReference = firebaseFirestore.collection("Topics").document(TopicName);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Toast.makeText(this.activity, "topic is already exist!\n join or create another one..", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> TopicData = new HashMap<>();
                    TopicData.put("Title", TopicName);
                    TopicData.put(firebaseAuth.getUid(), Name);
                    TopicData.put("Members", 1);
                    documentReference.set(TopicData);
                    Toast.makeText(this.activity, "topic added!", Toast.LENGTH_SHORT).show();
                    CurrnetTitle();
                    changeUserData(TopicName);
                    Intent intent = new Intent(this.activity, ChooseTopic.class);
                    intent.putExtra("Name", Name);
                    this.activity.startActivity(intent);
                }
            }
        });
    }

    private void CurrnetTitle() {
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    String tiltleRegistered = (String) userData
                            .get("topic");
                    if (!tiltleRegistered.equals("~null")){
                        leaveTitle(tiltleRegistered);
                    }
                }
            }
        });
    }

    public void status(String status) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(unused -> {
        });
    }

}