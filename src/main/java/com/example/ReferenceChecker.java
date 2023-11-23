package com.example;

import java.net.HttpURLConnection;
import java.net.URL;

public class ReferenceChecker {
    public static boolean isReferencePageExists(String referenceLink) {
        try {
            URL url = new URL(referenceLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}