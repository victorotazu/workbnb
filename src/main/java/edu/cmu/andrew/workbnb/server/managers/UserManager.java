package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.mongodb.client.model.Sorts;
import edu.cmu.andrew.workbnb.server.exceptions.AppException;
import edu.cmu.andrew.workbnb.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.workbnb.server.models.User;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import edu.cmu.andrew.workbnb.server.utils.AppLogger;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;


import java.lang.String;
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
                    .append("password", user.getPassword())
                    .append("email",user.getEmail()).append("riderBalance",user.getRiderBalance());
            if (newDoc != null)
                userCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new user");

        }catch(Exception e){
            throw handleException("Create User", e);
        }

    }

    public void updateUser( User user) throws AppException {
        try {


            Bson filter = new Document("_id", new ObjectId(user.getId()));
            Bson newValue = new Document()
                    .append("username", user.getUsername())
                    .append("password", user.getPassword())
                    .append("email",user.getEmail()).append("riderBalance",user.getRiderBalance());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                userCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update user details");

        } catch(Exception e) {
            throw handleException("Update User", e);
        }
    }

    public void deleteUser(String userId) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(userId));
            userCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete User", e);
        }
    }

    public ArrayList<User> getUserList() throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            FindIterable<Document> userDocs = userCollection.find();
            for(Document userDoc: userDocs) {
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("username"),
                        userDoc.getString("password"),
                        userDoc.getString("email"),
                        userDoc.getInteger("riderBalance")
                        );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getUserListSorted(String sortby) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put(sortby, 1);
            FindIterable<Document> userDocs = userCollection.find().sort(sortParams);
            for(Document userDoc: userDocs) {
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("username"),
                        userDoc.getString("password"),
                        userDoc.getString("email"),
                        userDoc.getInteger("riderBalance")
                );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getUserListPaginated(Integer offset, Integer count) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put("riderBalance", 1);
            FindIterable<Document> userDocs = userCollection.find().sort(sortParams).skip(offset).limit(count);
            for(Document userDoc: userDocs) {
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("username"),
                        userDoc.getString("password"),
                        userDoc.getString("email"),
                        userDoc.getInteger("riderBalance")
                );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getUserById(String id) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            FindIterable<Document> userDocs = userCollection.find();
            for(Document userDoc: userDocs) {
                if(userDoc.getObjectId("_id").toString().equals(id)) {
                    User user = new User(
                            userDoc.getObjectId("_id").toString(),
                            userDoc.getString("username"),
                            userDoc.getString("password"),
                            userDoc.getString("email"),
                            userDoc.getInteger("riderBalance")
                    );
                    userList.add(user);
                }
            }
            return new ArrayList<>(userList);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }


}
