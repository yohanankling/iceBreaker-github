package com.example.icebreaker.users;

public class User {
    public String Id;
    public String Email;
    public String Name;
    public String Gender;
    public String Topic;


    public User(String Id, String Email, String Name, String Gender, String Topic) {
        this.Id = Id;
        this.Name = Name;
        this.Email = Email;
        this.Gender = Gender;
        this.Topic = Topic;
    }

    public User() {

    }

    public String getId() {
        return Id;
    }

    public String getName() { return Name;}

    public String getEmail() {
        return Email;
    }

    public String getGender() {
        return Gender;
    }

    public String getTopic() {
        return Topic;
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

    public void setTopic(String topic) {
        this.Topic = topic;
    }


}
