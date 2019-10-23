package edu.cmu.andrew.workbnb.server.models;


public class User {

    String id = null;
    String username = null;
    String password = null;
    String email = null;
    Integer riderBalance;



    public User(String id, String username, String password, String email, Integer riderBalance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.riderBalance = riderBalance;
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

    public Integer getRiderBalance() { return riderBalance;}
}
