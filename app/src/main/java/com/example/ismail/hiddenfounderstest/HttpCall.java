package com.example.ismail.hiddenfounderstest;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by ismail on 02.01.2018.
 * Class for making an http request to a URL and return the result
 */

public class HttpCall {
    private static final String TAG = HttpCall.class.getSimpleName();

    public HttpCall() {
    }

    /**
     * Method tha make the http request to an url
     * @param pUrl he URL to make the request to
     * @return the result of the request as a String
     */
    public String makeServiceCall(String pUrl) {
        String response = null;
        try {
            URL url = new URL(pUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response and convert it to String
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) { //While there is a line in the result
                sb.append(line).append('\n');           //We append it to the StringBuilder
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();//Close the stream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString(); //Returning the result as a String
    }
}
