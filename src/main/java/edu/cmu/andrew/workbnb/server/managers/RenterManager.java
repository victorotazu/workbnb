package edu.cmu.andrew.workbnb.server.managers;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class RenterManager extends Manager{
    public static RenterManager _self;
    private MongoCollection<Document> renterCollection;

    public RenterManager(){

    }

    public static RenterManager getInstance(){
        if (_self == null){
            _self = new RenterManager();
        }
        return _self;
    }

}
