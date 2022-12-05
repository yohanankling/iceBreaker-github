package com.example.icebreaker.chats;

public class firebasemodel {
    String Uid;
    String Email;
    String Status;

    public firebasemodel(String uid, String email, String status) {
        Uid = uid;
        Email = email;
        Status = status;
    }

    public firebasemodel() {
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
