package com.example.icebreaker.users;

public class Topic {
    public String Creator;
    public String Name;
    public int Members;

    public Topic() {
    }

    public Topic(String creator, String name, int members) {
        Creator = creator;
        Name = name;
        Members = members;
    }

    public String getCreator() {
        return Creator;
    }

    public void setCreator(String creator) {
        Creator = creator;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getMembers() {
        return Members;
    }

    public void setMembers(int members) {
        Members = members;
    }
}

