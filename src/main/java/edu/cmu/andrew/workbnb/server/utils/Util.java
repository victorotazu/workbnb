package edu.cmu.andrew.workbnb.server.utils;

import java.io.*;

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
}
