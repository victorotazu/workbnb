package edu.cmu.andrew.workbnb.server.utils;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

public class Config {

    private static Config config;
    private static HashMap<String, Object> currentConfig = new HashMap<>();

    public static final String uri = "mongodb://%s:%s@cluster0-shard-00-00-5n1eq.azure.mongodb.net:27017,cluster0-shard-00-01-5n1eq.azure.mongodb.net:27017,cluster0-shard-00-02-5n1eq.azure.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true&w=majority";
    public static final String database = "workbnbDB";
    public static final int dbPort = 27017;
    public static final String dbHost = "localhost";
    public static String verion = "0.1.1";
    public static String logFile = "C:/Users/avj38/app.log";
    public static String logLevel = "ERROR";
    public static String logName = "AppLog";
    public static String username = "admin";
    public static String password = "";

    public static final String dataPath = "D:\\DevProjects\\cmu\\workbnb\\data";
}
