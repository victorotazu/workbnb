package edu.cmu.andrew.workbnb.server.managers;


import edu.cmu.andrew.workbnb.server.exceptions.*;
import edu.cmu.andrew.workbnb.server.models.Session;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.models.User;
import edu.cmu.andrew.workbnb.server.utils.APPCrypt;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.List;

public class SessionManager {

    private static SessionManager self;
    private ObjectWriter ow;
    private MongoCollection<Document> userCollection;
    public static HashMap<String,Session> SessionMap = new HashMap<String, Session>();

    private SessionManager() {
        this.userCollection = MongoPool.getInstance().getCollection("users");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public static SessionManager getInstance(){
        if (self == null)
            self = new SessionManager();
        return self;
    }

    public Session create(Object request) throws AppException {

        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            if (!json.has("email"))
                throw new AppBadRequestException(55, "missing email");
            if (!json.has("password"))
                throw new AppBadRequestException(55, "missing password");
            BasicDBObject query = new BasicDBObject();

            query.put("email", json.getString("email"));
            //query.put("password", APPCrypt.encrypt(json.getString("password")));
            query.put("password", json.getString("password"));

            Document item = userCollection.find(query).first();
            if (item == null) {
                throw new AppNotFoundException(0, "No user found matching credentials");
            }

            User user = convertDocumentToUser(item);

            user.setId(item.getObjectId("_id").toString());
            Session sessionVal = new Session(user);
            SessionMap.put(sessionVal.token,sessionVal);
            return sessionVal;
        }
        catch (JsonProcessingException e) {
            throw new AppBadRequestException(33, e.getMessage());
        }
        catch (AppBadRequestException e) {
            throw e;
        }
        catch (AppNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AppInternalServerException(0, e.getMessage());
        }

    }

    private User convertDocumentToUser(Document item) {
        User user = new User( item.getObjectId("_id").toString(),
                item.getString("username"),
                item.getString("email")
        );
        user.setPassword(item.getString("password"));
        return user;
    }

    public Session getSessionForToken(HttpHeaders headers) throws Exception{
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new AppUnauthorizedException(70,"No Authorization Headers");
        String token = authHeaders.get(0);

        if(SessionManager.getInstance().SessionMap.containsKey(token))
            return SessionManager.getInstance().SessionMap.get(token);
        else
            throw new AppUnauthorizedException(70,"Invalid Token");

    }
}
