package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.exceptions.AppException;
import edu.cmu.andrew.workbnb.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.workbnb.server.models.Favorite;
//import edu.cmu.andrew.workbnb.server.models.Listing;
import edu.cmu.andrew.workbnb.server.models.Session;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;

public class FavoriteManager extends Manager{

    public static FavoriteManager _self;
    private MongoCollection<Document> favoriteCollection;

    public FavoriteManager(){

        this.favoriteCollection = MongoPool.getInstance().getCollection("favorites");
    }

    public static FavoriteManager getInstance(){
        if (_self == null){
            _self = new FavoriteManager();
        }
        return _self;
    }

    public void createFavorite(HttpHeaders headers,Favorite favorite) throws AppException {

        try{

            Session session = SessionManager.getInstance().getSessionForToken(headers);

            JSONObject json = new JSONObject(favorite);

            Document newDoc = new Document()
                    .append("renterId", favorite.getRenterId())
                    .append("listingId", favorite.getListingId())
                    .append("favoriteWs", favorite.getFavoriteWs());
            if (newDoc != null)
                favoriteCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new favorite");

        }catch(Exception e){
            throw handleException("Create Favorite", e);
        }

    }

    public void updateFavorite(HttpHeaders headers, Favorite favorite) throws AppException {
        try {

            Session session = SessionManager.getInstance().getSessionForToken(headers);

            Bson filter = new Document("_id", new ObjectId(favorite.getId()));
            Bson newValue = new Document()
                    .append("renterId", favorite.getRenterId())
                    .append("listingId", favorite.getListingId())
                    .append("favoriteWs", favorite.getFavoriteWs());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                favoriteCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update favorite details");

        } catch(Exception e) {
            throw handleException("Update Favorite", e);
        }
    }

    public void deleteFavorite(HttpHeaders headers, String favoriteId) throws AppException {
        try {

            Session session = SessionManager.getInstance().getSessionForToken(headers);

            Bson filter = new Document("_id", new ObjectId(favoriteId));
            favoriteCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete Favorite", e);
        }
    }

    public ArrayList<Favorite> getFavoriteList(HttpHeaders headers) throws AppException {
        try{

            Session session = SessionManager.getInstance().getSessionForToken(headers);

            ArrayList<Favorite> favoriteList = new ArrayList<>();
            FindIterable<Document> favoriteDocs = favoriteCollection.find();
            for(Document favoriteDoc: favoriteDocs) {
                Favorite favorite = new Favorite(
                        favoriteDoc.getObjectId("_id").toString(),
                        favoriteDoc.getString("renterId"),
                        favoriteDoc.getString("listingId"),
                        favoriteDoc.getBoolean("favoriteWs")
                );
                favorite.setId(favoriteDoc.getObjectId("_id").toString());
                favoriteList.add(favorite);
            }
            return new ArrayList<>(favoriteList);
        } catch(Exception e){
            throw handleException("Get Favorite List", e);
        }
    }

    public ArrayList<Favorite> getFavoriteListSorted(HttpHeaders headers,String sortby) throws AppException {
        try{

            Session session = SessionManager.getInstance().getSessionForToken(headers);

            ArrayList<Favorite> favoriteList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put(sortby, 1);
            FindIterable<Document> favoriteDocs = favoriteCollection.find().sort(sortParams);
            for(Document favoriteDoc: favoriteDocs) {
                Favorite favorite = new Favorite(
                        favoriteDoc.getObjectId("_id").toString(),
                        favoriteDoc.getString("renterId"),
                        favoriteDoc.getString("listingId"),
                        favoriteDoc.getBoolean("favoriteWs")
                );
                favoriteList.add(favorite);
            }
            return new ArrayList<>(favoriteList);
        } catch(Exception e){
            throw handleException("Get Favorite List", e);
        }
    }

    public ArrayList<Favorite> getFavoriteById(HttpHeaders headers, String favoriteId) throws AppException {
        try{

            Session session = SessionManager.getInstance().getSessionForToken(headers);

            ArrayList<Favorite> favoriteList = new ArrayList<>();
            FindIterable<Document> favoriteDocs = favoriteCollection.find();
            for(Document favoriteDoc: favoriteDocs) {
                if(favoriteDoc.getObjectId("_id").toString().equals(favoriteId)) {
                    Favorite favorite = new Favorite(
                            favoriteDoc.getObjectId("_id").toString(),
                            favoriteDoc.getString("renterId"),
                            favoriteDoc.getString("listingId"),
                            favoriteDoc.getBoolean("favoriteWs")
                    );
                    favoriteList.add(favorite);
                }
            }
            return new ArrayList<>(favoriteList);
        } catch(Exception e){
            throw handleException("Get Favorite List", e);
        }
    }

}
