package edu.cmu.andrew.workbnb.server.models;

import edu.cmu.andrew.workbnb.server.utils.APPCrypt;
import java.util.UUID;

public class Session {

    public  String token = null;
    public String userId = null;
    public String username = null;

    public Session(User user) throws Exception{
        this.userId = user.id;
        //this.token = APPCrypt.encrypt(user.id);
        this.token = UUID.randomUUID().toString();
        this.username = user.username;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}

