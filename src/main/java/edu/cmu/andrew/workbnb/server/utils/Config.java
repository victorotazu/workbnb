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


    public static final String database = "workbnbDB";
    public static final int dbPort = 27017;
    public static final String dbHost = "localhost";
    public static String verion = "0.1.1";
    public static String logFile = "C:/Users/avj38/app.log";
    public static String logLevel = "ERROR";
    public static String logName = "AppLog";

    public static final String dataPath = "D:\\DevProjects\\cmu\\workbnb\\data";




}
