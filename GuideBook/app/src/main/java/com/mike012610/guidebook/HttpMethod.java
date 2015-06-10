package com.mike012610.guidebook;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

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

    public String connect(Map<String, String> params) {

        byte[] data = getRequestData(params, "UTF-8").toString().getBytes();

        String ans;
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)this.url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setFixedLengthStreamingMode(data.length);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(data);
            out.flush();


            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            ans = readStream(in);

            urlConnection.disconnect();
        }
        catch(IOException e) {
            return null;
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


    ///////////////////////////////
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            //stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

}
