package edu.cmu.andrew.workbnb.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import edu.cmu.andrew.workbnb.server.http.responses.AppResponse;
import edu.cmu.andrew.workbnb.server.managers.ListingManager;
import edu.cmu.andrew.workbnb.server.models.Listing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/utils")
public class UtilHttpInterface extends HttpInterface {
    private ObjectWriter ow;

    public UtilHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @GET
    @Path("/pricing_suggestion/{listingId}")
    @Produces({MediaType.TEXT_PLAIN})
    public String getPayments(@Context HttpHeaders headers,
                                   @PathParam("listingId") String listingId){
        try {
            Listing listing = ListingManager.getInstance().getListingById(listingId).get(0);
            ArrayList<Listing> allListings = ListingManager.getInstance().getListingList();
            Double suggestedPrice = null;

            if (listing != null &&
                    allListings != null &&
                    allListings.size() > 0) {
                String availability = listing.getAvailability();
                List<Listing> similarListings = allListings.stream()
                        .filter(l -> l.getAvailability().contains(availability)
                                && l.getId() != listingId)
                        .collect(Collectors.toList());
                suggestedPrice = similarListings.stream()
                        .mapToDouble(Listing::getPrice)
                        .average()
                        .getAsDouble();
            }
            return suggestedPrice.toString();
        } catch (Exception ex)
        {
            throw handleException("GET /utils/pricing_suggestion/{listingId}", ex);
        }

    }

}
