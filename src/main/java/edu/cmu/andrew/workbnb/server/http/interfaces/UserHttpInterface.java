package edu.cmu.andrew.workbnb.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.http.exceptions.HttpBadRequestException;
import edu.cmu.andrew.workbnb.server.http.responses.AppResponse;
import edu.cmu.andrew.workbnb.server.models.User;
import edu.cmu.andrew.workbnb.server.managers.UserManager;
import edu.cmu.andrew.workbnb.server.utils.*;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("/users")

public class UserHttpInterface extends HttpInterface{

    private ObjectWriter ow;
    private MongoCollection<Document> userCollection = null;

    public UserHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postUsers(Object request){

        try{
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            User newUser = new User(
                    null,
                    json.getString("username"),
                    json.getString("email")
            );

            newUser.setPassword(json.getString("password"));
            UserManager.getInstance().createUser(newUser);
            return new AppResponse("Insert Successful");

        } catch (Exception e){
            throw handleException("POST users", e);
        }

    }

    @GET
    @Path("/{userId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleUser(@Context HttpHeaders headers, @PathParam("userId") String userId){

        try{
            AppLogger.info("Got an API call");
            User user = UserManager.getInstance().getUserById(userId);

            if(user != null)
                return new AppResponse(user);
            else
                throw new HttpBadRequestException(0, "Problem with getting user");
        }catch (Exception e){
            throw handleException("GET /users/{userId}", e);
        }
    }
}
