package edu.cmu.andrew.workbnb.server.http.interfaces;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.http.exceptions.HttpBadRequestException;
import edu.cmu.andrew.workbnb.server.http.responses.AppResponse;
import edu.cmu.andrew.workbnb.server.http.utils.PATCH;
import edu.cmu.andrew.workbnb.server.models.User;
import edu.cmu.andrew.workbnb.server.managers.UserManager;
import edu.cmu.andrew.workbnb.server.utils.*;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

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

            User newuser = new User(
                    null,
                    json.getString("username"),
                    json.getString("password"),
                    json.getString("email"),
                    json.getInt("riderBalance")
            );
            UserManager.getInstance().createUser(newuser);
            return new AppResponse("Insert Successful");

        }catch (Exception e){
            throw handleException("POST users", e);
        }

    }

    //Sorting: http://localhost:8080/api/users?sortby=riderBalance
    //Pagination: http://localhost:8080/api/users?offset=1&count=2
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getUsers(@Context HttpHeaders headers, @QueryParam("sortby") String sortby, @QueryParam("offset") Integer offset,
                                @QueryParam("count") Integer count){
        try{
            AppLogger.info("Got an API call");
            ArrayList<User> users = null;

            if(sortby != null)
                users = UserManager.getInstance().getUserListSorted(sortby);
            else if(offset != null && count != null)
                users = UserManager.getInstance().getUserListPaginated(offset, count);
            else
                users = UserManager.getInstance().getUserList();

            if(users != null)
                return new AppResponse(users);
            else
                throw new HttpBadRequestException(0, "Problem with getting users");
        }catch (Exception e){
            throw handleException("GET /users", e);
        }
    }


    /*
  //http://server.com/api/users?begin=11&count=10
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getUsersPaginated(@Context HttpHeaders headers){

        try{
            AppLogger.info("Got an API call");
            ArrayList<User> users = UserManager.getInstance().getUserList();

            if(users != null)
                return new AppResponse(users);
            else
                throw new HttpBadRequestException(0, "Problem with getting users");
        }catch (Exception e){
            throw handleException("GET /users", e);
        }
    }*/

    @GET
    @Path("/{userId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleUser(@Context HttpHeaders headers, @PathParam("userId") String userId){

        try{
            AppLogger.info("Got an API call");
            ArrayList<User> users = UserManager.getInstance().getUserById(userId);

            if(users != null)
                return new AppResponse(users);
            else
                throw new HttpBadRequestException(0, "Problem with getting users");
        }catch (Exception e){
            throw handleException("GET /users/{userId}", e);
        }


    }


    @PATCH
    @Path("/{userId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchUsers(Object request, @PathParam("userId") String userId){

        JSONObject json = null;

        try{
            json = new JSONObject(ow.writeValueAsString(request));
            User user = new User(
                    userId,
                    json.getString("username"),
                    json.getString("password"),
                    json.getString("email"),
                    json.getInt("riderBalance")
            );

            UserManager.getInstance().updateUser(user);

        }catch (Exception e){
            throw handleException("PATCH users/{userId}", e);
        }

        return new AppResponse("Update Successful");
    }




    @DELETE
    @Path("/{userId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteUsers(@PathParam("userId") String userId){

        try{
            UserManager.getInstance().deleteUser( userId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE users/{userId}", e);
        }

    }


}
