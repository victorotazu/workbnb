package edu.cmu.andrew.workbnb.server.http.interfaces;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.http.exceptions.HttpBadRequestException;
import edu.cmu.andrew.workbnb.server.http.responses.AppResponse;
import edu.cmu.andrew.workbnb.server.http.utils.PATCH;
import edu.cmu.andrew.workbnb.server.managers.FavoriteManager;
import edu.cmu.andrew.workbnb.server.managers.RatingManager;
import edu.cmu.andrew.workbnb.server.managers.RenterManager;
import edu.cmu.andrew.workbnb.server.managers.ReservationManager;
import edu.cmu.andrew.workbnb.server.models.Favorite;
import edu.cmu.andrew.workbnb.server.models.Rating;
import edu.cmu.andrew.workbnb.server.models.Renter;
import edu.cmu.andrew.workbnb.server.models.Reservation;
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
        public AppResponse postRenters(@Context HttpHeaders headers, Object request) {

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
                        json.getString("bankAccountNumber"),
                        json.getString("userId")
                );

                RenterManager.getInstance().createRenter(headers, newRenter);
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
    public AppResponse patchRenters(@Context HttpHeaders headers, Object request, @PathParam("renterId") String renterId){

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
                    json.getString("bankAccountNumber"),
                    json.getString("userId")
            );
            renter.setId(renterId);
            renter.setUserId(RenterManager.getInstance().getRenterById(renterId).get(0).getUserId());

            RenterManager.getInstance().updateRenter(headers, renter);

        }catch (Exception e){
            throw handleException("PATCH renters/{renterId}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{renterId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteRenters(@Context HttpHeaders headers, @PathParam("renterId") String renterId){
        try{
            RenterManager.getInstance().deleteRenter(headers,renterId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE renters/{renterId}", e);
        }
    }
    // Reservations
    @POST
    @Path("/{renterId}/reservations")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse createReservation(@Context HttpHeaders headers, @PathParam("renterId") String renterId, Object request){
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Reservation newReservation = new Reservation(
                    renterId,
                    json.getString("landlordId"),
                    json.getString("listingId"),
                    json.getInt("duration"),
                    json.getDouble("price")
            );

            ReservationManager.getInstance().createReservation(headers, newReservation);
            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST reservations", e);
        }
    }

    @GET
    @Path("/{renterId}/reservations")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse getReservations(@Context HttpHeaders headers, @PathParam("renterId") String renterId){
        try{
            AppLogger.info("Got an API call");
            List<Reservation> reservations = ReservationManager.getInstance().getReservationsByRenter(renterId);

            if(reservations != null)
                return new AppResponse(reservations);
            else
                throw new HttpBadRequestException(0, "Problem with getting reservations");
        }catch (Exception e){
            throw handleException("GET /renters/{renterId}/reservations", e);
        }
    }

    @GET
    @Path("/{renterId}/reservations/{reservationId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse getReservationById(@Context HttpHeaders headers,
                                          @PathParam("renterId") String renterId,
                                          @PathParam("reservationId") String reservationId){
        try{
            AppLogger.info("Got an API call");
            Reservation reservation = ReservationManager.getInstance().
                    getReservationsById(reservationId);

            if(reservation != null)
                return new AppResponse(reservation);
            else
                throw new HttpBadRequestException(0, "Problem with getting reservation");
        }catch (Exception e){
            throw handleException("GET /renters/{renterId}/reservations/{reservationId}", e);
        }
    }

    @PATCH
    @Path("/{renterId}/reservations/{reservationId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchReservations(@Context HttpHeaders headers,
                                         Object request,
                                         @PathParam("renterId") String renterId,
                                         @PathParam("renterId") String reservationId){

        JSONObject json = null;

        try{
            json = new JSONObject(ow.writeValueAsString(request));
            Reservation newReservation = new Reservation(
                    renterId,
                    json.getString("landlordId"),
                    json.getString("listingId"),
                    json.getInt("duration"),
                    json.getDouble("price")
            );
            newReservation.setId(reservationId);

            ReservationManager.getInstance().updateReservation(headers, renterId, newReservation);

        }catch (Exception e){
            throw handleException("PATCH renters/{renterId}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{renterId}/reservations/{reservationId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteReservation(@Context HttpHeaders headers,
                                         @PathParam("renterId") String renterId,
                                         @PathParam("reservationId") String reservationId){
        try{
            ReservationManager.getInstance().deleteReservation(headers,reservationId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE renters/{renterId}/reservation/{reservationId}", e);
        }
    }

    @POST
    @Path("/{renterId}/ratings")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postListings(@Context HttpHeaders headers, @PathParam("renterId") String landlordId, Object request) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Rating newRating = new Rating(
                    null,
                    json.getString("renterId"),
                    json.getString("listingId"),
                    json.getString("stars"),
                    json.getString("userId")
            );
            RatingManager.getInstance().createRating(headers,newRating);
            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST Ratings", e);
        }



    }

    @GET
    @Path("{renterId}/ratings/{ratingId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleRating(@Context HttpHeaders headers, @PathParam("ratingId") String ratingId, @PathParam("renterId") String renterId){

        try{
            AppLogger.info("Got an API call");
            List<Rating> ratings = RatingManager.getInstance().getRatingById(headers, ratingId);

            if(ratings != null)
                return new AppResponse(ratings);
            else
                throw new HttpBadRequestException(0, "Problem with getting ratings");
        }catch (Exception e){
            throw handleException("GET /ratings/{ratingId}", e);
        }
    }

    @PATCH
    @Path("{renterId}/ratings/{ratingId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchListings(@Context HttpHeaders headers,Object request, @PathParam("ratingId") String ratingId, @PathParam("renterId") String renterId){

        JSONObject json = null;

        try{
            json = new JSONObject(ow.writeValueAsString(request));
            Rating rating = new Rating(
                    ratingId,
                    json.getString("renterId"),
                    json.getString("listingId"),
                    json.getString("stars"),
                    json.getString("userId")
            );
            rating.setId(ratingId);
            RatingManager.getInstance().updateRating(headers, rating);

        }catch (Exception e){
            throw handleException("PATCH ratings/{ratingId}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("{renterId}/ratings/{ratingId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteRatings(@Context HttpHeaders headers,@PathParam("ratingId") String ratingId, @PathParam("renterId") String renterId){
        try{
            RatingManager.getInstance().deleteRating(headers, ratingId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE ratings/{ratingId}", e);
        }
    }

    @POST
    @Path("/{renterId}/favorites")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postFavorites(@Context HttpHeaders headers, @PathParam("renterId") String renterId, Object request) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Favorite newFavorite = new Favorite(
                    null,
                    json.getString("renterId"),
                    json.getString("listingId"),
                    json.getBoolean("favoriteWs")
            );
            FavoriteManager.getInstance().createFavorite(headers, newFavorite);
            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST Favorites", e);
        }

    }

    @GET
    @Path("/{renterId}/favorites")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getListings(@Context HttpHeaders headers,
                                   @QueryParam("filter") String filter,
                                   @QueryParam("sortby") String sortby,
                                   @DefaultValue("ASC") @QueryParam("direction") String direction,
                                   @QueryParam("offset") Integer offset,
                                   @QueryParam("count") Integer count){
        try{
            AppLogger.info("Got an API call");
            List<Favorite> favorites = null;
            favorites = FavoriteManager.getInstance().getFavoriteList(headers);

            if(favorites != null) {
                if(filter!= null) {
                    switch (filter){
                        case "Yes":
                            favorites = favorites.stream()
                                    .filter(str -> str.getFavoriteWs().equals("Yes"))
                                    .collect(Collectors.toList());
                            break;

                        case "No":
                            favorites = favorites.stream()
                                    .filter(str -> str.getFavoriteWs().equals("No"))
                                    .collect(Collectors.toList());
                    }
                }

                if (sortby != null) {
                    Comparator<Favorite> priceComparator = Comparator.comparing(Favorite::getFavoriteWs);
                    Comparator<Favorite> priceComparatorReversed = Comparator.comparing(Favorite::getFavoriteWs).reversed();

                    switch (sortby){
                        case "favoriteWs":
                            favorites = favorites.stream()
                                    .sorted(direction.equals("DESC") ? priceComparatorReversed : priceComparator)
                                    .collect(Collectors.toList());
                            break;
                    }
                }

                if (offset != null && count != null) {
                    favorites = favorites.stream()
                            .skip(offset)
                            .limit(count)
                            .collect(Collectors.toList());
                }
                return new AppResponse(favorites);
            }
            else
                throw new HttpBadRequestException(0, "Problem with getting favorites");

        }catch (Exception e){
            throw handleException("GET /favorites", e);
        }
    }

    @GET
    @Path("{renterId}/favorites/{favoriteId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleListing(@Context HttpHeaders headers, @PathParam("favoriteId") String favoriteId, @PathParam("renterId") String renterId){

        try{
            AppLogger.info("Got an API call");
            List<Favorite> favorites = FavoriteManager.getInstance().getFavoriteById(headers, favoriteId);

            if(favorites != null)
                return new AppResponse(favorites);
            else
                throw new HttpBadRequestException(0, "Problem with getting favorites");
        }catch (Exception e){
            throw handleException("GET /favorites/{favoriteId}", e);
        }
    }

    @PATCH
    @Path("{renterId}/favorites/{favoriteId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchFavorites(@Context HttpHeaders headers,Object request, @PathParam("favoriteId") String favoriteId, @PathParam("renterId") String renterId){

        JSONObject json = null;

        try{
            json = new JSONObject(ow.writeValueAsString(request));
            Favorite favorite = new Favorite(
                    favoriteId,
                    json.getString("renterId"),
                    json.getString("listingId"),
                    json.getBoolean("favoriteWs")
            );
            favorite.setId(favoriteId);
            FavoriteManager.getInstance().updateFavorite(headers, favorite);

        }catch (Exception e){
            throw handleException("PATCH listings/{listingId}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("{renterId}/favorites/{favoriteId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteFavorites(@Context HttpHeaders headers, @PathParam("favoriteId") String favoriteId, @PathParam("renterId") String renterId){
        try{
            FavoriteManager.getInstance().deleteFavorite(headers, favoriteId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE favorites/{favoriteId}", e);
        }
    }

}
