package com.mike012610.guidebook;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mike012610 on 2015/6/8.
 */
public class HttpMethod {
    private URL url;
    private String method;


    public HttpMethod(String url,String method) {
        try {
            this.url = new URL(url);
            this.method = method;
        }
        catch (MalformedURLException e)
        {
        }
    }

    public String connect() {
        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        }
        catch(IOException e) {
            return null;
        }
        String ans;
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            ans = readStream(in);
        }
        catch(IOException e) {
            return null;
        }
        finally {
            urlConnection.disconnect();
        }
        return ans;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

}
