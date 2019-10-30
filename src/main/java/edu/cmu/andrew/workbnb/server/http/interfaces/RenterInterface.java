package edu.cmu.andrew.workbnb.server.http.interfaces;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.http.exceptions.HttpBadRequestException;
import edu.cmu.andrew.workbnb.server.http.responses.AppResponse;
import edu.cmu.andrew.workbnb.server.http.utils.PATCH;
import edu.cmu.andrew.workbnb.server.managers.RenterManager;
import edu.cmu.andrew.workbnb.server.models.Renter;
import edu.cmu.andrew.workbnb.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Path("/renters")
public class RenterInterface extends HttpInterface {

        private ObjectWriter ow;
        private MongoCollection<Document> renterCollection = null;

        public RenterInterface() {
            ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        }

        @POST
        @Consumes({MediaType.APPLICATION_JSON})
        @Produces({MediaType.APPLICATION_JSON})
        public AppResponse postRenters(Object request) {

            try {
                JSONObject json = null;
                json = new JSONObject(ow.writeValueAsString(request));

                Renter newRenter = new Renter(
                        null,
                        json.getString("firstName"),
                        json.getString("lastName"),
                        json.getString("email"),
                        json.getString("phoneNumber"),
                        json.getString("industry"),
                        json.getString("bankAccountNumber")
                );
                RenterManager.getInstance().createRenter(newRenter);
                return new AppResponse("Insert Successful");

            } catch (Exception e) {
                throw handleException("POST renters", e);
            }

        }




    //Sorting: http://localhost:8080/api/users?sortby=riderBalance
    //Pagination: http://localhost:8080/api/users?offset=1&count=2
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getRenters(@Context HttpHeaders headers,
                                  @QueryParam("filter") String filter,
                                  @QueryParam("sortby") String sortby,
                                  @DefaultValue("ASC") @QueryParam("direction") String direction,
                                  @QueryParam("offset") Integer offset,
                                  @QueryParam("count") Integer count){
        try{
            AppLogger.info("Got an API call");
            List<Renter> renters = null;
            renters = RenterManager.getInstance().getRenterList();

            if(renters != null) {
                if(filter!= null) {
                    switch (filter){
                           case "Technology":
                            renters = renters.stream()
                                    .filter(str -> str.getIndustry().equals("Technology"))
                                    .collect(Collectors.toList());
                            break;

                            case "Software":
                            renters = renters.stream()
                                    .filter(str -> str.getIndustry().equals("Software"))
                                    .collect(Collectors.toList());
                    }
                }

                if (sortby != null) {
                    Comparator<Renter> lastNameComparator = Comparator.comparing(Renter::getLastName);
                    Comparator<Renter> lastNameComparatorReversed = Comparator.comparing(Renter::getLastName).reversed();

                    switch (sortby){
                        case "lastName":
                            renters = renters.stream()
                                    .sorted(direction.equals("DESC") ? lastNameComparatorReversed : lastNameComparator)
                                    .collect(Collectors.toList());
                            break;
                    }
                }

                if (offset != null && count != null) {
                    renters = renters.stream()
                            .skip(offset)
                            .limit(count)
                            .collect(Collectors.toList());
                }
                return new AppResponse(renters);
            }
            else
                throw new HttpBadRequestException(0, "Problem with getting renters");

        }catch (Exception e){
            throw handleException("GET /renters", e);
        }
    }

    @GET
    @Path("/{renterId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleRenter(@Context HttpHeaders headers, @PathParam("renterId") String renterId){

        try{
            AppLogger.info("Got an API call");
            List<Renter> renters = RenterManager.getInstance().getRenterById(renterId);

            if(renters != null)
                return new AppResponse(renters);
            else
                throw new HttpBadRequestException(0, "Problem with getting renters");
        }catch (Exception e){
            throw handleException("GET /renters/{renterId}", e);
        }
    }

    @PATCH
    @Path("/{renterId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchRenters(Object request, @PathParam("renterId") String renterId){

        JSONObject json = null;

        try{
            json = new JSONObject(ow.writeValueAsString(request));
            Renter renter = new Renter(
                    renterId,
                    json.getString("firstName"),
                    json.getString("lastName"),
                    json.getString("email"),
                    json.getString("phoneNumber"),
                    json.getString("industry"),
                    json.getString("bankAccountNumber")
            );
            renter.setId(renterId);
            RenterManager.getInstance().updateRenter(renter);

        }catch (Exception e){
            throw handleException("PATCH renters/{renterId}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{renterId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteRenters(@PathParam("renterId") String renterId){
        try{
            RenterManager.getInstance().deleteRenter(renterId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE renters/{renterId}", e);
        }
    }

}
