package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.exceptions.AppException;
import edu.cmu.andrew.workbnb.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.workbnb.server.models.Listing;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListingManager extends Manager{

    public static ListingManager _self;
    private MongoCollection<Document> listingCollection;

    public ListingManager(){

        this.listingCollection = MongoPool.getInstance().getCollection("listings");
    }

    public static ListingManager getInstance(){
        if (_self == null){
            _self = new ListingManager();
        }
        return _self;
    }

    public void createListing(Listing listing) throws AppException {

        try{
            JSONObject json = new JSONObject(listing);

            Document newDoc = new Document()
                    .append("landlordId", listing.getLandlordId())
                    .append("address", listing.getAddress())
                    .append("type", listing.getType())
                    .append("images", listing.getImages())
                    .append("price", listing.getPrice())
                    .append("availability", listing.getAvailability())
                    .append("details", listing.getDetails());
            if (newDoc != null)
                listingCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new listing");

        }catch(Exception e){
            throw handleException("Create Listing", e);
        }

    }

    public void updateListing(Listing listing) throws AppException {
        try {


            Bson filter = new Document("_id", new ObjectId(listing.getId()));
            Bson newValue = new Document()
                    .append("landlordId", listing.getLandlordId())
                    .append("address", listing.getAddress())
                    .append("type", listing.getType())
                    .append("images",listing.getImages())
                    .append("price",listing.getPrice())
                    .append("availability",listing.getAvailability())
                    .append("details",listing.getDetails());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                listingCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update listing details");

        } catch(Exception e) {
            throw handleException("Update Listing", e);
        }
    }

    public void deleteListing(String listingId) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(listingId));
            listingCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete Listing", e);
        }
    }

    public ArrayList<Listing> getListingList() throws AppException {
        try{
            ArrayList<Listing> listingList = new ArrayList<>();
            FindIterable<Document> listingDocs = listingCollection.find();
            for(Document listingDoc: listingDocs) {
                Listing listing = new Listing(
                        listingDoc.getObjectId("_id").toString(),
                        listingDoc.getString("landlordId"),
                        listingDoc.getString("address"),
                        listingDoc.getString("type"),
                        listingDoc.getString("images"),
                        listingDoc.getDouble("price"),
                        listingDoc.getString("availability"),
                        listingDoc.getString("details")
                );
                listing.setId(listingDoc.getObjectId("_id").toString());
                listingList.add(listing);
            }
            return new ArrayList<>(listingList);
        } catch(Exception e){
            throw handleException("Get Listing List", e);
        }
    }

    public ArrayList<Listing> getListingListSorted(String sortby) throws AppException {
        try{
            ArrayList<Listing> listingList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put(sortby, 1);
            FindIterable<Document> listingDocs = listingCollection.find().sort(sortParams);
            for(Document listingDoc: listingDocs) {
                Listing listing = new Listing(
                        listingDoc.getObjectId("_id").toString(),
                        listingDoc.getString("landlordId"),
                        listingDoc.getString("address"),
                        listingDoc.getString("type"),
                        listingDoc.getString("images"),
                        listingDoc.getDouble("price"),
                        listingDoc.getString("availability"),
                        listingDoc.getString("details")
                );
                listingList.add(listing);
            }
            return new ArrayList<>(listingList);
        } catch(Exception e){
            throw handleException("Get Listing List", e);
        }
    }

    public ArrayList<Listing> getListingListPaginated(Integer offset, Integer count) throws AppException {
        try{
            ArrayList<Listing> listingList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put("riderBalance", 1);
            FindIterable<Document> listingDocs = listingCollection.find().sort(sortParams).skip(offset).limit(count);
            for(Document listingDoc: listingDocs) {
                Listing listing = new Listing(
                        listingDoc.getObjectId("_id").toString(),
                        listingDoc.getString("landlordId"),
                        listingDoc.getString("address"),
                        listingDoc.getString("type"),
                        listingDoc.getString("images"),
                        listingDoc.getDouble("price"),
                        listingDoc.getString("availability"),
                        listingDoc.getString("details")
                );
                listingList.add(listing);
            }
            return new ArrayList<>(listingList);
        } catch(Exception e){
            throw handleException("Get Listing List", e);
        }
    }

    public ArrayList<Listing> getListingById(String listingId) throws AppException {
        try{
            ArrayList<Listing> listingList = new ArrayList<>();
            FindIterable<Document> listingDocs = listingCollection.find();
            for(Document listingDoc: listingDocs) {
                if(listingDoc.getObjectId("_id").toString().equals(listingId)) {
                    Listing listing = new Listing(
                            listingDoc.getObjectId("_id").toString(),
                            listingDoc.getString("landlordId"),
                            listingDoc.getString("address"),
                            listingDoc.getString("type"),
                            listingDoc.getString("images"),
                            listingDoc.getDouble("price"),
                            listingDoc.getString("availability"),
                            listingDoc.getString("details")
                    );
                    listingList.add(listing);
                }
            }
            return new ArrayList<>(listingList);
        } catch(Exception e){
            throw handleException("Get Listing List", e);
        }
    }

}
