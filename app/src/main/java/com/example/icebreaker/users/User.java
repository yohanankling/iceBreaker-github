package com.example.icebreaker.users;

public class User {
    String Id;
    String Email;
    String Name;
    String Gender;
    String Area;
    String LastSeen;
    boolean Online;
    boolean AdminAccess;
    boolean GameAvailable;


    public User(String Id, String Email, String Name, String Gender, String Area, String LastSeen, boolean online, boolean AdminAccess, boolean GameAvailable) {
        this.Id = Id;
        this.Name = Name;
        this.Email = Email;
        this.Gender = Gender;
        this.Area = Area;
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

    public String getArea() {
        return Area;
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
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public void setLastSeen(String lastSeen) { LastSeen = lastSeen;}

    public void setArea(String area) {
        Area = area;
    }

    public void setGameAvailable(boolean gameAvailable) {
        GameAvailable = gameAvailable;
    }

    public void setOnline(boolean online) {
        Online = online;
    }

    public void setAdminAccess(boolean adminAccess) {
        AdminAccess = adminAccess;
    }
}
