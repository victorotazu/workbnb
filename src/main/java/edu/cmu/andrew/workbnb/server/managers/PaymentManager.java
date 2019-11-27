package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.exceptions.AppException;
import edu.cmu.andrew.workbnb.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.workbnb.server.models.Payment;
import edu.cmu.andrew.workbnb.server.models.Session;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentManager extends Manager {
    public static PaymentManager _self;
    private MongoCollection<Document> paymentCollection;

    public PaymentManager(){
        this.paymentCollection = MongoPool.getInstance().getCollection("payments");
    }

    public static PaymentManager getInstance(){
        if (_self == null){
            _self = new PaymentManager();
        }
        return _self;
    }

    public void createPayment(HttpHeaders headers, Payment payment) throws AppException {

        try{
            Session session = SessionManager.getInstance().getSessionForToken(headers);

            JSONObject json = new JSONObject(payment);

            Document newDoc = new Document()
                    .append("renterId", payment.getRenterId())
                    .append("listingId", payment.getListingId())
                    .append("paymentMethodId", "1")
                    .append("amount", payment.getAmount())
                    .append("createdDate", new Date())
                    .append("lastModifiedDate", new Date());
            if (newDoc != null)
                paymentCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new payment");

        }catch(Exception e){
            throw handleException("Create Payment", e);
        }
    }

    public void updatePayment(HttpHeaders headers, Payment payment) throws AppException {

        try{
            Session session = SessionManager.getInstance().getSessionForToken(headers);

            JSONObject json = new JSONObject(payment);
            Bson filter = new Document("_id", new ObjectId(payment.getId()));
            Document updatedDoc = new Document()
                    .append("renterId", payment.getRenterId())
                    .append("listingId", payment.getListingId())
                    .append("paymentMethodId", payment.getPaymentMethodId())
                    .append("amount", payment.getAmount())
                    .append("createdDate", payment.getCreatedDate())
                    .append("lastModifiedDate", new Date());
            Bson updateOperationDocument = new Document("$set", updatedDoc);
            if (updatedDoc != null)
                paymentCollection.updateOne(filter, updatedDoc);
            else
                throw new AppInternalServerException(0, "Failed to update payment");

        }catch(Exception e){
            throw handleException("Update Payment", e);
        }
    }

    public void deletePayment(HttpHeaders headers, String paymentId) throws AppException {
        try {
            Session session = SessionManager.getInstance().getSessionForToken(headers);

            Bson filter = new Document("_id", new ObjectId(paymentId));
            paymentCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete Payment", e);
        }
    }

    public List<Payment> getPayments() throws AppException {
        List<Payment> paymentList;
        try{
            paymentList = new ArrayList<>();
            FindIterable<Document> paymentDocs = paymentCollection.find();

            for(Document paymentDoc: paymentDocs) {
                Payment payment = new Payment();
                payment.setRenterId(paymentDoc.getString("renterId"));
                payment.setListingId(paymentDoc.getString("listingId"));
                payment.setPaymentMethodId(paymentDoc.getString("paymentMethodId"));
                payment.setAmount(paymentDoc.getDouble("amount"));
                payment.setCreatedDate(paymentDoc.getDate("createdDate"));
                payment.setLastModifiedDate(paymentDoc.getDate("lastModifiedDate"));
                payment.setId(paymentDoc.getObjectId("_id").toString());
                paymentList.add(payment);
            }
            return paymentList;
        }
        catch(Exception e){
            throw handleException("Get Payment List", e);
        }
    }

    public Payment getPaymentById(String paymentId) throws AppException {
        try{
            Payment payment = null;
            FindIterable<Document> paymentDocs = paymentCollection.find();
            for(Document paymentDoc: paymentDocs) {
                if(paymentDoc.getObjectId("_id").toString().equals(paymentId)) {
                    payment = new Payment();
                    payment.setId(paymentDoc.getObjectId("_id").toString());
                    payment.setRenterId(paymentDoc.getString("renterId"));
                    payment.setListingId(paymentDoc.getString("listingId"));
                    payment.setPaymentMethodId(paymentDoc.getString("paymentMethodId"));
                    payment.setAmount(paymentDoc.getDouble("amount"));
                    payment.setCreatedDate(paymentDoc.getDate("createdDate"));
                    payment.setLastModifiedDate(paymentDoc.getDate("lastModifiedDate"));
                    break;
                }
            }
            return payment;
        } catch(Exception e){
            throw handleException("Get Payment", e);
        }
    }
}
