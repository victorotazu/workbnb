package edu.cmu.andrew.workbnb.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import edu.cmu.andrew.workbnb.server.http.responses.AppResponse;
import edu.cmu.andrew.workbnb.server.managers.PaymentManager;
import edu.cmu.andrew.workbnb.server.models.Payment;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/stripe")
public class StripeHttpInterface {
    private ObjectWriter ow;

    public StripeHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    //http://localhost:8080/api/stripe/pay
    @POST
    @Path("/pay")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse payLandlord(Object request) throws Exception {
        try{
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            String renterId = json.getString("renterId");
            String landlordId = json.getString("landlordId");
            return new AppResponse("Stripe transaction successful");

        } catch (Exception e) {
            throw e;
        }
    }
}
