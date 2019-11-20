package edu.cmu.andrew.workbnb.server.models;

public class User {

    String id = null;
    String username = null;
    String password = null;
    String email = null;

    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public void setId(String id){ this.id = id; }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }
}
