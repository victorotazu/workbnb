package edu.cmu.andrew.workbnb.server.http.interfaces;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.http.exceptions.HttpBadRequestException;
import edu.cmu.andrew.workbnb.server.http.responses.AppResponse;
import edu.cmu.andrew.workbnb.server.http.utils.PATCH;
import edu.cmu.andrew.workbnb.server.managers.LandlordManager;
import edu.cmu.andrew.workbnb.server.managers.UserManager;
import edu.cmu.andrew.workbnb.server.models.Landlord;
import edu.cmu.andrew.workbnb.server.models.User;
import edu.cmu.andrew.workbnb.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Path("/landlords")
public class LandlordHttpInterface extends HttpInterface {
    private ObjectWriter ow;
    private MongoCollection<Document> landlordCollection = null;

    public LandlordHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postLandlords(Object request) {
        try{
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Landlord newLandlord = new Landlord(
                    json.getString("firstName"),
                    json.getString("lastName"),
                    json.getString("phoneNumber"),
                    json.getBoolean("subleaseAuth"),
                    json.getString("bankAccountNumber")
            );
            LandlordManager.getInstance().createLandlord(newLandlord);
            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST landlords", e);
        }
    }

    //Sorting: http://localhost:8080/api/users?sortby=riderBalance
    //Pagination: http://localhost:8080/api/users?offset=1&count=2
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getLandlords(@Context HttpHeaders headers,
                                    @QueryParam("filter") String filter,
                                    @QueryParam("sortby") String sortby,
                                    @DefaultValue("ASC") @QueryParam("direction") String direction,
                                    @QueryParam("offset") Integer offset,
                                    @QueryParam("count") Integer count){
        try{
            AppLogger.info("Got an API call");
            List<Landlord> landlords = null;
            landlords = LandlordManager.getInstance().getLandlordList();

            if(landlords != null) {
                if (filter != null){
                    switch (filter){
                        case "enabled":
                            landlords = landlords.stream()
                                    .filter(l -> l.getSubLeaseAuth().equals(Boolean.TRUE))
                                    .collect(Collectors.toList());
                    }
                }

                if (sortby != null) {
                    Comparator<Landlord> lastNameComparator = Comparator.comparing(Landlord::getLastName);
                    Comparator<Landlord> lastNameComparatorReversed = Comparator.comparing(Landlord::getLastName).reversed();
                    // Can add other sortby fields
                    switch (sortby){
                        case "lastName":
                            landlords = landlords.stream()
                                    .sorted(direction.equals("DESC") ? lastNameComparatorReversed : lastNameComparator)
                                    .collect(Collectors.toList());
                            break;
                    }
                }

                if(offset != null && count != null) {
                    landlords = landlords.stream()
                            .skip(offset)
                            .limit(count)
                            .collect(Collectors.toList());
                }


                return new AppResponse(landlords);
            }
            else
                throw new HttpBadRequestException(0, "Problem with getting landlords");
        }catch (Exception e){
            throw handleException("GET /landlords", e);
        }
    }

    @GET
    @Path("/{landlordId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleLandlord(@Context HttpHeaders headers, @PathParam("landlordId") String landlordId){

        try{
            AppLogger.info("Got an API call");
            ArrayList<Landlord> landlords = LandlordManager.getInstance().getLandlordById(landlordId);

            if(landlords != null)
                return new AppResponse(landlords);
            else
                throw new HttpBadRequestException(0, "Problem with getting landlords");
        }catch (Exception e){
            throw handleException("GET /landlords/{landlordId}", e);
        }
    }

    @PATCH
    @Path("/{landlordId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchLandlords(Object request, @PathParam("landlordId") String landlordId){

        JSONObject json = null;

        try{
            json = new JSONObject(ow.writeValueAsString(request));
            Landlord landlord = new Landlord(
                    json.getString("firstName"),
                    json.getString("lastName"),
                    json.getString("phoneNumber"),
                    json.getBoolean("subleaseAuth"),
                    json.getString("bankAccountNumber")
            );
            landlord.setId(landlordId);
            LandlordManager.getInstance().updateLandlord(landlord);

        }catch (Exception e){
            throw handleException("PATCH landlords/{landlordId}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{landlordId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteLandlords(@PathParam("landlordId") String landlordId){
        try{
            LandlordManager.getInstance().deleteLandlord(landlordId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE landlords/{landlordId}", e);
        }
    }

}
