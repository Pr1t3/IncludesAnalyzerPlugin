package com.example;

import java.net.HttpURLConnection;
import java.net.URL;

public class ReferenceChecker {
    public static boolean isReferencePageExists(String includeName) {
        try {
            String cppReferenceLink = "https://en.cppreference.com/w/cpp/header/" + includeName;
            URL url = new URL(cppReferenceLink);
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