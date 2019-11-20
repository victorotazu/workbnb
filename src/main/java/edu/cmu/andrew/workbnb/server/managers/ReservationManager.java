package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.exceptions.AppException;
import edu.cmu.andrew.workbnb.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.workbnb.server.exceptions.AppUnauthorizedException;
import edu.cmu.andrew.workbnb.server.models.Renter;
import edu.cmu.andrew.workbnb.server.models.Reservation;
import edu.cmu.andrew.workbnb.server.models.Session;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;

public class ReservationManager extends Manager {
    public static ReservationManager _self;
    private MongoCollection<Document> reservationCollection;

    public ReservationManager(){

        this.reservationCollection = MongoPool.getInstance().getCollection("reservations");
    }

    public static ReservationManager getInstance(){
        if (_self == null){
            _self = new ReservationManager();
        }
        return _self;
    }

    public void createReservation(HttpHeaders headers, Reservation reservation) throws AppException {

        try{
            Session session = SessionManager.getInstance().getSessionForToken(headers);

            JSONObject json = new JSONObject(reservation);

            Document newDoc = new Document()
                    .append("renterId", reservation.getRenterId())
                    .append("landlordId", reservation.getLandlordId())
                    .append("listingId", reservation.getListingId())
                    .append("duration", reservation.getDuration())
                    .append("price", reservation.getPrice());
            if (newDoc != null)
                reservationCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new reservation");

        }catch(Exception e){
            throw handleException("Create Reservation", e);
        }
    }

    public ArrayList<Reservation> getReservationsByRenter(String renterId) throws AppException {
        try{
            ArrayList<Reservation> reservationList = new ArrayList<>();
            FindIterable<Document> reservationDocs = reservationCollection.find();
            for(Document reservationDoc: reservationDocs) {
                if (reservationDoc.getString("renterId").equals(renterId)) {
                    Reservation reservation = new Reservation(
                            reservationDoc.getString("renterId").toString(),
                            reservationDoc.getString("landlordId").toString(),
                            reservationDoc.getString("listingId").toString(),
                            reservationDoc.getInteger("duration"),
                            reservationDoc.getDouble("price")
                    );
                    reservation.setId(reservationDoc.getObjectId("_id").toString());
                    reservationList.add(reservation);
                }
            }
            return new ArrayList<>(reservationList);
        } catch(Exception e){
            throw handleException("Get Reservation List by Renter", e);
        }
    }

    public Reservation getReservationsById(String reservationId) throws AppException {
        try {
            Reservation reservation = null;
            FindIterable<Document> reservationDocs = reservationCollection.find();
            for (Document reservationDoc : reservationDocs) {
                if (reservationDoc.getObjectId("_id").toString().equals(reservationId)) {
                    reservation = new Reservation(
                            reservationDoc.getString("renterId"),
                            reservationDoc.getString("landlordId"),
                            reservationDoc.getString("listingId"),
                            reservationDoc.getInteger("duration"),
                            reservationDoc.getDouble("price")
                    );
                    reservation.setId(reservationDoc.getObjectId("_id").toString());
                    break;
                }
            }
            return reservation;
        } catch (Exception e){
            throw handleException("Get Reservation by Id", e);
        }
    }


    public void updateReservation(HttpHeaders headers, String renterId, Reservation reservation) throws AppException {
        try{
            Session session = SessionManager.getInstance().getSessionForToken(headers);
            Renter renter = RenterManager.getInstance().getRenterById(renterId).get(0);

            if (!session.getUserId().equals(renter.getUserId()))
                throw new AppUnauthorizedException(70, "Invalid user id");

            Bson filter = new Document("_id", new ObjectId(reservation.getId()));
            Bson newValue = new Document()
                    .append("renterId", reservation.getRenterId())
                    .append("landlordId", reservation.getLandlordId())
                    .append("listingId", reservation.getListingId())
                    .append("duration", reservation.getDuration())
                    .append("price", reservation.getPrice());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                reservationCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update reservation details");

        } catch(Exception e){
            throw handleException("Patch Reservation by Id", e);
        }
    }

    public void deleteReservation(HttpHeaders headers, String reservationId) throws AppException {
        try {
            Session session = SessionManager.getInstance().getSessionForToken(headers);

            Bson filter = new Document("_id", new ObjectId(reservationId));
            reservationCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete Reservation", e);
        }
    }
}
