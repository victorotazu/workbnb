package edu.cmu.andrew.workbnb.server.managers;
import edu.cmu.andrew.workbnb.server.utils.MongoPool;

public class AdminManager extends Manager{
    public static AdminManager _self;

    public AdminManager() {

    }

    public static AdminManager getInstance(){
        if (_self == null)
            _self = new AdminManager();
        return _self;
    }

    public void resetDb(){
        MongoPool.getInstance().reset();
    }
}
