package com.example.sudal.tellingmamapapa;

public class Users {
    String id;
    String email;
    String gender;
    String name;

    public Users(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public Users(String id, String email,String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
