package com.example.icebreaker.users;

public class User {
    public String Id;
    public String Email;
    public String Name;
    public String Gender;
    public String Topic;
    public String LastSeen;
    public boolean Online;
    public boolean AdminAccess;
    public boolean GameAvailable;


    public User(String Id, String Email, String Name, String Gender, String Topic, String LastSeen, boolean online, boolean AdminAccess, boolean GameAvailable) {
        this.Id = Id;
        this.Name = Name;
        this.Email = Email;
        this.Gender = Gender;
        this.Topic = Topic;
        this.LastSeen = LastSeen;
        this.Online = online;
        this.AdminAccess = AdminAccess;
        this.GameAvailable = GameAvailable;
    }

    public String getId() {
        return Id;
    }

    public String getName() { return Name;}

    public String getLastSeen() { return LastSeen;}

    public String getEmail() {
        return Email;
    }

    public String getGender() {
        return Gender;
    }

    public String getTopic() {
        return Topic;
    }

    public boolean getOnline() {
        return Online;
    }

    public boolean isGameAvailable() {
        return GameAvailable;
    }

    public boolean haveAdminAccess() {
        return AdminAccess;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public void setLastSeen(String lastSeen) { this.LastSeen = lastSeen;}

    public void setTopic(String topic) {
        this.Topic = topic;
    }

    public void setGameAvailable(boolean gameAvailable) {
        this.GameAvailable = gameAvailable;
    }

    public void setOnline(boolean online) {
        this.Online = online;
    }

    public void setAdminAccess(boolean adminAccess) {
        this.AdminAccess = adminAccess;
    }
}
