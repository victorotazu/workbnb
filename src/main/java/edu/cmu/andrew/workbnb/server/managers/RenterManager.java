package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.exceptions.AppException;
import edu.cmu.andrew.workbnb.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.workbnb.server.models.Renter;
import edu.cmu.andrew.workbnb.server.models.User;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.util.ArrayList;

public class RenterManager extends Manager{
    public static RenterManager _self;
    private MongoCollection<Document> renterCollection;

    public RenterManager(){

        this.renterCollection = MongoPool.getInstance().getCollection("renters");
    }

    public static RenterManager getInstance(){
        if (_self == null){
            _self = new RenterManager();
        }
        return _self;
    }

    public void createRenter(Renter renter) throws AppException {

        try{
            JSONObject json = new JSONObject(renter);

            Document newDoc = new Document()
                    .append("firstName", renter.getFirstName())
                    .append("lastName", renter.getLastName())
                    .append("email",renter.getEmail())
                    .append("phoneNumber",renter.getPhoneNumber())
                    .append("industry",renter.getIndustry())
                    .append("bankAccountNumber",renter.getBankAccountNumber());
            if (newDoc != null)
                renterCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new renter");

        }catch(Exception e){
            throw handleException("Create Renter", e);
        }

    }

    public void updateRenter(Renter renter) throws AppException {
        try {


            Bson filter = new Document("_id", new ObjectId(renter.getId()));
            Bson newValue = new Document()
                    .append("firstName", renter.getFirstName())
                    .append("lastName", renter.getLastName())
                    .append("email",renter.getEmail())
                    .append("phoneNumber",renter.getPhoneNumber())
                    .append("industry",renter.getIndustry())
                    .append("bankAccountNumber",renter.getBankAccountNumber());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                renterCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update renter details");

        } catch(Exception e) {
            throw handleException("Update Renter", e);
        }
    }

    public void deleteRenter(String renterId) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(renterId));
            renterCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete Renter", e);
        }
    }

    public ArrayList<Renter> getRenterList() throws AppException {
        try{
            ArrayList<Renter> renterList = new ArrayList<>();
            FindIterable<Document> renterDocs = renterCollection.find();
            for(Document renterDoc: renterDocs) {
                Renter renter = new Renter(
                        renterDoc.getObjectId("_id").toString(),
                        renterDoc.getString("firstName"),
                        renterDoc.getString("lastName"),
                        renterDoc.getString("email"),
                        renterDoc.getString("phoneNumber"),
                        renterDoc.getString("industry"),
                        renterDoc.getString("bankAccountNumber")
                );
                renter.setId(renterDoc.getObjectId("_id").toString());
                renterList.add(renter);
            }
            return new ArrayList<>(renterList);
        } catch(Exception e){
            throw handleException("Get Renter List", e);
        }
    }

    public ArrayList<Renter> getRenterListSorted(String sortby) throws AppException {
        try{
            ArrayList<Renter> renterList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put(sortby, 1);
            FindIterable<Document> renterDocs = renterCollection.find().sort(sortParams);
            for(Document renterDoc: renterDocs) {
                Renter renter = new Renter(
                        renterDoc.getObjectId("_id").toString(),
                        renterDoc.getString("firstName"),
                        renterDoc.getString("lastName"),
                        renterDoc.getString("email"),
                        renterDoc.getString("phoneNumber"),
                        renterDoc.getString("industry"),
                        renterDoc.getString("bankAccountNumber")
                );
                renterList.add(renter);
            }
            return new ArrayList<>(renterList);
        } catch(Exception e){
            throw handleException("Get Renter List", e);
        }
    }

    public ArrayList<Renter> getRenterListPaginated(Integer offset, Integer count) throws AppException {
        try{
            ArrayList<Renter> renterList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put("riderBalance", 1);
            FindIterable<Document> renterDocs = renterCollection.find().sort(sortParams).skip(offset).limit(count);
            for(Document renterDoc: renterDocs) {
                Renter renter = new Renter(
                        renterDoc.getObjectId("_id").toString(),
                        renterDoc.getString("firstName"),
                        renterDoc.getString("lastName"),
                        renterDoc.getString("email"),
                        renterDoc.getString("phoneNumber"),
                        renterDoc.getString("industry"),
                        renterDoc.getString("bankAccountNumber")
                );
                renterList.add(renter);
            }
            return new ArrayList<>(renterList);
        } catch(Exception e){
            throw handleException("Get Renter List", e);
        }
    }

    public ArrayList<Renter> getRenterById(String renterId) throws AppException {
        try{
            ArrayList<Renter> renterList = new ArrayList<>();
            FindIterable<Document> renterDocs = renterCollection.find();
            for(Document renterDoc: renterDocs) {
                if(renterDoc.getObjectId("_id").toString().equals(renterId)) {
                    Renter renter = new Renter(
                            renterDoc.getObjectId("_id").toString(),
                            renterDoc.getString("firstName"),
                            renterDoc.getString("lastName"),
                            renterDoc.getString("email"),
                            renterDoc.getString("phoneNumber"),
                            renterDoc.getString("industry"),
                            renterDoc.getString("bankAccountNumber")
                    );
                    renterList.add(renter);
                }
            }
            return new ArrayList<>(renterList);
        } catch(Exception e){
            throw handleException("Get Renter List", e);
        }
    }

}
