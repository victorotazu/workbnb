package edu.cmu.andrew.workbnb.server.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.util.JSON;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MongoPool {

    private static MongoPool mp;
    private static MongoDatabase db;

    private MongoPool() {
        try {
            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
            //build the connection options
            builder.maxConnectionIdleTime(60000);//set the max wait time in (ms)
            builder.socketKeepAlive(true);
            builder.connectTimeout(30000);
            MongoClientOptions opts = builder.build();

            MongoClient mc = new MongoClient(new ServerAddress(Config.dbHost, Config.dbPort));
            this.db = mc.getDatabase("workbnbDB");
            Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        } catch (Exception e) {
            AppLogger.error("From MongoPool creation ",e);
        }
    }

    public static MongoPool getInstance(){
        if(mp == null){
            mp = new MongoPool();
        }
        return mp;
    }

    public MongoCollection<Document> getCollection(String collectionName){
        MongoCollection<Document> collection;
        collection = db.getCollection(collectionName);
        return collection;
    }

    private void deleteCollections(){
        MongoIterable<String> collections = db.listCollectionNames();
        for (String collection:collections) {
            db.getCollection(collection).drop();
        }
    }

    public void reset(){
        // delete all collections
        deleteCollections();
        // create Books collection
        db.createCollection("landlords");
        initializeCollection(Config.dataPath + "\\landlords.json", "landlords");

        // create Borrowers
        db.createCollection("renters");
        initializeCollection(Config.dataPath + "\\renters.json", "renters");
    }

    private void initializeCollection(String sourceJsonFile, String targetCollection){
        MongoCollection<Document> collection = db.getCollection(targetCollection);
        List<BasicDBObject> items = new ArrayList<>();
        // Load data from json file
        String jsonString = Util.getFileContent(sourceJsonFile);
        items = (ArrayList) JSON.parse(jsonString);
        List<Document> documents = new ArrayList<>();
        // Need to convert BasicDBObjects to Documents in order to insert many
        for (BasicDBObject dbObject:items) {
            documents.add(Document.parse(dbObject.toString()));
        }
        collection.insertMany(documents);
    }


}
