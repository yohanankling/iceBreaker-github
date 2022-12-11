package com.example.icebreaker.users;

public class Topic {
    String Title;
    String Members;
    String MmbersUid;

    public Topic(String title, String members, String mmbersUid) {
        Title = title;
        Members = members;
        MmbersUid = mmbersUid;
    }

    public Topic() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMembers() {
        return Members;
    }

    public void setMembers(String members) {
        Members = members;
    }

    public String getMmbersUid() {
        return MmbersUid;
    }

    public void setMmbersUid(String mmbersUid) {
        MmbersUid = mmbersUid;
    }
}
