package edu.cmu.andrew.workbnb.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.http.exceptions.HttpBadRequestException;
import edu.cmu.andrew.workbnb.server.http.responses.AppResponse;
import edu.cmu.andrew.workbnb.server.http.utils.PATCH;
import edu.cmu.andrew.workbnb.server.managers.PaymentManager;
import edu.cmu.andrew.workbnb.server.models.Payment;
import edu.cmu.andrew.workbnb.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentHttpInterface extends  HttpInterface {
    private ObjectWriter ow;
    private MongoCollection<Document> landlordCollection = null;

    public PaymentHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postPayments(@Context HttpHeaders headers, Object request) {
        try{
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Payment newPayment = new Payment();
            newPayment.setRenterId(json.getString("renterId"));
            newPayment.setListingId(json.getString("listingId"));
            newPayment.setPaymentMethodId(json.getString("paymentMethodId"));
            newPayment.setAmount(json.getDouble("amount"));
            PaymentManager.getInstance().createPayment(headers, newPayment);
            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST payments", e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getPayments(@Context HttpHeaders headers,
                                    @QueryParam("filter") String filter,
                                    @QueryParam("sortby") String sortby,
                                    @DefaultValue("ASC") @QueryParam("direction") String direction,
                                    @QueryParam("offset") Integer offset,
                                    @QueryParam("count") Integer count){
        try{
            AppLogger.info("Got an API call");
            List<Payment> payments = null;
            payments = PaymentManager.getInstance().getPayments();

            if(payments != null) {
                if (filter != null){
                    switch (filter){
                        case "today":
                            LocalDateTime todayLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            Date yesterday = Date.from(todayLocal.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
                            payments = payments.stream()
                                    .filter(p -> p.getCreatedDate().after(yesterday))
                                    .collect(Collectors.toList());
                    }
                }

                if (sortby != null) {
                    Comparator<Payment> lastMDComparator = Comparator.comparing(Payment::getLastModifiedDate);
                    Comparator<Payment> lastMDComparatorReversed = Comparator.comparing(Payment::getLastModifiedDate).reversed();
                    // Can add other sortby fields
                    switch (sortby){
                        case "updateDate":
                            payments = payments.stream()
                                    .sorted(direction.equals("DESC") ? lastMDComparatorReversed : lastMDComparator)
                                    .collect(Collectors.toList());
                            break;
                    }
                }

                if(offset != null && count != null) {
                    payments = payments.stream()
                            .skip(offset)
                            .limit(count)
                            .collect(Collectors.toList());
                }

                return new AppResponse(payments);
            }
            else
                throw new HttpBadRequestException(0, "Problem with getting payments");
        }catch (Exception e){
            throw handleException("GET /payments", e);
        }
    }

    @PATCH
    @Path("/{paymentId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchPayments(@Context HttpHeaders headers,
                                     Object request,
                                     @PathParam("paymentId") String paymentId){

        JSONObject json = null;

        try{
            json = new JSONObject(ow.writeValueAsString(request));
            Payment updatedPayment = new Payment();
            updatedPayment.setRenterId(json.getString("renterId"));
            updatedPayment.setListingId(json.getString("listingId"));
            updatedPayment.setPaymentMethodId(json.getString("paymentMethodId"));
            updatedPayment.setAmount(json.getDouble("amount"));
            updatedPayment.setId(paymentId);

            PaymentManager.getInstance().updatePayment(headers, updatedPayment);

        }catch (Exception e){
            throw handleException("PATCH payments/{paymentId}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{paymentId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deletePayments(@Context HttpHeaders headers,
                                       @PathParam("paymentId") String paymentId){
        try{
            PaymentManager.getInstance().deletePayment(headers, paymentId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE payments/{paymentId}", e);
        }
    }


}
