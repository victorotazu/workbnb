package edu.cmu.andrew.workbnb.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class UtilHttpInterface extends HttpInterface {
    private ObjectWriter ow;
    private MongoCollection<Document> renterCollection = null;

    public utilHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }
}
