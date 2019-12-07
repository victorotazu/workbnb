package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.exceptions.AppException;
import edu.cmu.andrew.workbnb.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.workbnb.server.exceptions.AppUnauthorizedException;
import edu.cmu.andrew.workbnb.server.models.Rating;
import edu.cmu.andrew.workbnb.server.models.Renter;
import edu.cmu.andrew.workbnb.server.models.Session;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;


public class RatingManager extends Manager{
    public static edu.cmu.andrew.workbnb.server.managers.RatingManager _self;
    private MongoCollection<Document> ratingCollection;

    public RatingManager(){

        this.ratingCollection = MongoPool.getInstance().getCollection("ratings");
    }

    public static edu.cmu.andrew.workbnb.server.managers.RatingManager getInstance(){
        if (_self == null){
            _self = new edu.cmu.andrew.workbnb.server.managers.RatingManager();
        }
        return _self;
    }

    public void createRating(HttpHeaders headers, Rating rating) throws AppException {

        try{
            Session session = SessionManager.getInstance().getSessionForToken(headers);
            if(!session.getUserId().equals(rating.getUserId()))
                throw new AppUnauthorizedException(70,"Invalid id");

            JSONObject json = new JSONObject(rating);

            Document newDoc = new Document()
                    .append("renterId", rating.getRenterId())
                    .append("listingId",rating.getListingId())
                    .append("stars",rating.getStars()).append("userId",rating.getUserId());
            if (newDoc != null)
                ratingCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new rating");

        }catch(Exception e){
            throw handleException("Create Rating", e);
        }
    }

    public void updateRating(HttpHeaders headers, Rating rating) throws AppException {
        try {
            Session session = SessionManager.getInstance().getSessionForToken(headers);
            if(!session.getUserId().equals(rating.getUserId()))
                throw new AppUnauthorizedException(70,"Invalid id");

            Bson filter = new Document("_id", new ObjectId(rating.getId()));
            Bson newValue = new Document()
                    .append("renterId", rating.getRenterId())
                    .append("listingId",rating.getListingId())
                    .append("stars",rating.getStars());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                ratingCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update rating details");

        } catch(Exception e) {
            throw handleException("Update Rating", e);
        }
    }

    public void deleteRating(HttpHeaders headers, String ratingId) throws AppException {
        try {
            /*Session session = SessionManager.getInstance().getSessionForToken(headers);
            if(!session.getUserId().equals(rating.getUserId()))
                throw new AppUnauthorizedException(70,"Invalid id");*/

            Bson filter = new Document("_id", new ObjectId(ratingId));
            ratingCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete Rating", e);
        }
    }

    public ArrayList<Rating> getRatingList(HttpHeaders headers) throws AppException {
        try{
//            Session session = SessionManager.getInstance().getSessionForToken(headers);
//            if(!session.getUserId().equals(rating.getUserId()))
//                throw new AppUnauthorizedException(70,"Invalid id");

            ArrayList<Rating> ratingList = new ArrayList<>();
            FindIterable<Document> ratingDocs = ratingCollection.find();
            for(Document ratingDoc: ratingDocs) {
                Rating rating = new Rating(
                        ratingDoc.getObjectId("_id").toString(),
                        ratingDoc.getString("renterId"),
                        ratingDoc.getString("listingId"),
                        ratingDoc.getString("stars"),
                        ratingDoc.getString("userId")
                );
                rating.setId(ratingDoc.getObjectId("_id").toString());
                ratingList.add(rating);
            }
            return new ArrayList<>(ratingList);
        } catch(Exception e){
            throw handleException("Get Rating List", e);
        }
    }

    public ArrayList<Rating> getRatingListSorted(HttpHeaders headers,String sortby) throws AppException {
        try{

            //Session session = SessionManager.getInstance().getSessionForToken(headers);

            ArrayList<Rating> ratingList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put(sortby, 1);
            FindIterable<Document> ratingDocs = ratingCollection.find().sort(sortParams);
            for(Document ratingDoc: ratingDocs) {
                Rating rating = new Rating(
                        ratingDoc.getObjectId("_id").toString(),
                        ratingDoc.getString("renterId"),
                        ratingDoc.getString("listingId"),
                        ratingDoc.getString("stars"),
                        ratingDoc.getString("userId")
                );
                ratingList.add(rating);
            }
            return new ArrayList<>(ratingList);
        } catch(Exception e){
            throw handleException("Get Rating List", e);
        }
    }

    public ArrayList<Rating> getRatingListPaginated(HttpHeaders headers,Integer offset, Integer count) throws AppException {
        try{

            //Session session = SessionManager.getInstance().getSessionForToken(headers);

            ArrayList<Rating> ratingList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put("riderBalance", 1);
            FindIterable<Document> ratingDocs = ratingCollection.find().sort(sortParams).skip(offset).limit(count);
            for(Document ratingDoc: ratingDocs) {
                Rating rating = new Rating(
                        ratingDoc.getObjectId("_id").toString(),
                        ratingDoc.getString("renterId"),
                        ratingDoc.getString("listingId"),
                        ratingDoc.getString("stars"),
                        ratingDoc.getString("userId")
                );
                ratingList.add(rating);
            }
            return new ArrayList<>(ratingList);
        } catch(Exception e){
            throw handleException("Get Rating List", e);
        }
    }

    public ArrayList<Rating> getRatingById(HttpHeaders headers, String ratingId) throws AppException {
        try{

            //Session session = SessionManager.getInstance().getSessionForToken(headers);

            ArrayList<Rating> ratingList = new ArrayList<>();
            FindIterable<Document> ratingDocs = ratingCollection.find();
            for(Document ratingDoc: ratingDocs) {
                if(ratingDoc.getObjectId("_id").toString().equals(ratingId)) {
                    Rating rating = new Rating(
                            ratingDoc.getObjectId("_id").toString(),
                            ratingDoc.getString("renterId"),
                            ratingDoc.getString("listingId"),
                            ratingDoc.getString("stars"),
                            ratingDoc.getString("userId")
                    );
                    rating.setUserId(ratingDoc.getString("userId"));
                    ratingList.add(rating);
                }
            }
            return new ArrayList<>(ratingList);
        } catch(Exception e){
            throw handleException("Get Rating List", e);
        }
    }

}
