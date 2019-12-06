package edu.cmu.andrew.workbnb.server.utils;

import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.net.URI;

public class Util {
    public static String getFileContent(String filePath){
        InputStream is = null;
        String line = "";
        try {
            is = new FileInputStream(filePath);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while(line != null){
                sb.append(line).append("\n");
                line = buf.readLine();
            }

            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/api").build();
    }

    public static Boolean execStripePayment(String landlordId, String renterId){
        return true;
    }
}
