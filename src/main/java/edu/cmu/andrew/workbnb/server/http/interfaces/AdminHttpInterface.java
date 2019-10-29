package edu.cmu.andrew.workbnb.server.http.interfaces;

import edu.cmu.andrew.workbnb.server.managers.AdminManager;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("admin")
public class AdminHttpInterface {

    @GET
    @Path("about")
    @Produces({ MediaType.APPLICATION_JSON})
    public JSONObject getAll() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("version", "0.0.1");
            obj.put("date", "2019-09-23");
        }
        catch(Exception e) {
            System.out.println("Could not set version");
        }
        return obj;
    }

    //Reset
    @POST
    @Path("reset")
    @Produces({ MediaType.TEXT_PLAIN})
    public String reset() {
        AdminManager.getInstance().resetDb();
        return "Data has been reset";

    }
}
