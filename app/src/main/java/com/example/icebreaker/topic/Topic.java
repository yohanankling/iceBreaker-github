package com.example.icebreaker.topic;

public class Topic {
    String Title;
    long Members;
    String MemberUid;

    public Topic(String title, long members, String membersUid) {
        Title = title;
        Members = members;
        MemberUid = membersUid;
    }

    public Topic() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public long getMembers() {
        return Members;
    }

    public void setMembers(long members) {
        Members = members;
    }

    public String getMembersUid() {
        return MemberUid;
    }

    public void setMembersUid(String membersUid) {
        MemberUid = membersUid;
    }
}
