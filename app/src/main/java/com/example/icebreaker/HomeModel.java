package com.example.icebreaker;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.icebreaker.chats.Broadcast;
import com.example.icebreaker.topic.TopicMembersList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeModel {
    private final Activity activity;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;

    public HomeModel(Activity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public String getUid() {
        return this.firebaseAuth.getUid();
    }

    public DocumentReference reference() {
        return  firebaseFirestore.collection("Users").document(this.getUid());
    }

    public void removeUser(String removeMail) {
        Query query =  firebaseFirestore.collection("Users").whereEqualTo("email", removeMail);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot.isEmpty()) {
                    Toast.makeText(this.activity, "mail not found", Toast.LENGTH_SHORT).show();
                } else {
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    DocumentSnapshot document = documents.get(0);
                    Map<String, Object> userData = document.getData();
                    String removeUid = userData.get("uid").toString();
                    String removedEmail = userData.get("email").toString();
                    this.addToBannedList(removedEmail, removeUid);
                    Toast.makeText(this.activity, removeMail + " will banned soon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addToBannedList(String removeMail, String removeUid) {
        DocumentReference documentReference = firebaseFirestore.collection("Banned").document(removeUid);
        Map<String, Object> BannedData = new HashMap<>();
        BannedData.put("email", removeMail);
        documentReference.set(BannedData).addOnSuccessListener(unused -> {
        });
    }

    public void checkifbanned() {
        DocumentReference documentReference = firebaseFirestore.collection("Banned").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener((value, error) -> {
            if (value.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
                builder.setCancelable(true);
                builder.setTitle("its appear you are banned");
                builder.setMessage("if you have an objection you can contact us on the mail :\nourIceBreaker@gmail.com");
                builder.setPositiveButton("ok", (dialog, which) ->
                {
                    firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).delete();
                    Toast.makeText(this.activity, "bye-bye!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this.activity, MainActivity.class);
                    status("offline");
                    firebaseAuth.signOut();
                    this.activity.startActivity(intent);
                });
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                status("online");
            }
        });
    }

    public void TopicMembersButtonPressed(String name) {
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    String Title = (String) userData.get("topic");
                    if(!Title.equals("~null")){
                        Intent intent = new Intent(this.activity, TopicMembersList.class);
                        intent.putExtra("Title", Title);
                        intent.putExtra("MyName", name);
                        this.activity.startActivity(intent);
                    }
                    else Toast.makeText(this.activity, "must have topic first..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void broadcastButtonPressed() {
        DocumentReference documentReferenceuser = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReferenceuser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    String Title = (String) userData.get("topic");
                    String UserName = (String) userData.get("name");
                    if(!Title.equals("~null")){
                        Intent intent = new Intent(this.activity, Broadcast.class);
                        intent.putExtra("Title", Title);
                        intent.putExtra("Name", UserName);
                        this.activity.startActivity(intent);
                    }
                    else Toast.makeText(this.activity, "must have topic first..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public void status(String status) {
        DocumentReference documentReference = this.firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", status).addOnSuccessListener(unused -> {
        });
    }

}
