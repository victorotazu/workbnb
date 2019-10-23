package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.workbnb.server.exceptions.AppException;
import edu.cmu.andrew.workbnb.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.workbnb.server.models.Landlord;
import edu.cmu.andrew.workbnb.server.models.User;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.util.ArrayList;

public class LandlordManager extends Manager {

    public static LandlordManager _self;
    private MongoCollection<Document> landlordCollection;

    public () {
        this.landlordCollection = MongoPool.getInstance().getCollection("landlords");
    }

    public static LandlordManager getInstance(){
        if (_self == null)
            _self = new LandlordManager();
        return _self;
    }

    public void createLandlord(Landlord landlord) throws AppException {

        try{
            JSONObject json = new JSONObject(landlord);

            Document newDoc = new Document()
                    .append("firstName", landlord.getFirstName())
                    .append("lastName", landlord.getLastName())
                    .append("phoneNumber", landlord.getPhoneNumber())
                    .append("bankAccountNumber", landlord.getBankAccountNumber())
                    .append("subleaseAuth", landlord.getSubLeaseAuth()));
            if (newDoc != null)
                landlordCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new landlord");

        }catch(Exception e){
            throw handleException("Create Landlord", e);
        }
    }

    public void updateLandlord(Landlord landlord) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(landlord.getId()));
            Bson newValue = new Document()
                    .append("firstName", landlord.getFirstName())
                    .append("lastName", landlord.getLastName())
                    .append("phoneNumber", landlord.getPhoneNumber())
                    .append("bankAccountNumber", landlord.getBankAccountNumber())
                    .append("subleaseAuth", landlord.getSubLeaseAuth()));
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                landlordCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update landlord details");

        } catch(Exception e) {
            throw handleException("Update Landlord", e);
        }
    }

    public void deleteLandlord(String landlordId) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(landlordId));
            landlordCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete Landlord", e);
        }
    }

    public ArrayList<Landlord> getLandlordList() throws AppException {
        try{
            ArrayList<Landlord> landlordList = new ArrayList<>();
            FindIterable<Document> landlordDocs = landlordCollection.find();

            for(Document landlordDoc: landlordDocs) {
                Landlord landlord = new Landlord(
                        landlordDoc.getString("firstName"),
                        landlordDoc.getString("lastName"),
                        landlordDoc.getString("phoneNumber"),
                        landlordDoc.getBoolean("subleaseAuth"),
                        landlordDoc.getString("bankAccountNumber")
                );
                landlord.setId(landlordDoc.getString("_id"));
                landlordList.add(landlord);
            }
            return new ArrayList<>(landlordList);
        } catch(Exception e){
            throw handleException("Get Landlord List", e);
        }
    }


    public ArrayList<Landlord> getLandlordById(String landlordId) throws AppException {
        try{
            ArrayList<Landlord> landlordList = new ArrayList<>();
            FindIterable<Document> landlordDocs = landlordCollection.find();
            for(Document landlordDoc: landlordDocs) {
                if(landlordDoc.getObjectId("_id").toString().equals(landlordId)) {
                    Landlord landlord = new Landlord(
                            landlordDoc.getString("firstName"),
                            landlordDoc.getString("lastName"),
                            landlordDoc.getString("phoneNumber"),
                            landlordDoc.getBoolean("subleaseAuth"),
                            landlordDoc.getString("bankAccountNumber")
                    );
                    landlord.setId(landlordDoc.getString("_id"));
                    landlordList.add(landlord);
                }
            }
            return new ArrayList<Landlord>(landlordList);
        } catch(Exception e){
            throw handleException("Get Landlord List", e);
        }
    }
}
