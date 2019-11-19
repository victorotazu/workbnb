package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.exceptions.AppException;
import edu.cmu.andrew.workbnb.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.workbnb.server.models.User;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserManager extends Manager {
    public static UserManager _self;
    private MongoCollection<Document> userCollection;


    public UserManager() {
        this.userCollection = MongoPool.getInstance().getCollection("users");
    }

    public static UserManager getInstance(){
        if (_self == null)
            _self = new UserManager();
        return _self;
    }

    public void createUser(User user) throws AppException {
        try{
            JSONObject json = new JSONObject(user);

            Document newDoc = new Document()
                    .append("username", user.getUsername())
                    .append("email", user.getEmail())
                    .append("password", user.getPassword());

            if (newDoc != null)
                userCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new user");

        }catch(Exception e){
            throw handleException("Create user", e);
        }
    }

    public User getUserById(String userId) throws AppException {
        try{
            FindIterable<Document> userDocs = userCollection.find();
            for(Document userDoc: userDocs) {
                if(userDoc.getObjectId("_id").toString().equals(userId)) {
                    User user = new User(
                            userDoc.getObjectId("_id").toString(),
                            userDoc.getString("username"),
                            userDoc.getString("email")
                    );
                    return user;
                }
            }
            return null;
        } catch(Exception e){
            throw handleException("Get user", e);
        }
    }
}
