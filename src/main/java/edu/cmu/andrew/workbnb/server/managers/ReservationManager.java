package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.exceptions.AppException;
import edu.cmu.andrew.workbnb.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.workbnb.server.models.Renter;
import edu.cmu.andrew.workbnb.server.models.Reservation;
import edu.cmu.andrew.workbnb.server.models.Session;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;

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
}
