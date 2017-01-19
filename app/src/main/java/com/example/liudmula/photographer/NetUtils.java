package com.example.liudmula.photographer;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liudmula on 18.01.17.
 */

public class NetUtils {
    private static final String LOG_TAG = NetUtils.class.getSimpleName();

    private NetUtils() {
    }

    public static List<Photo> fetchPhotoList(String requestedUrl){
        URL url = createUrl(requestedUrl);

        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making http request", e);
        }

        ArrayList<Photo> photos = extractListPhotos(jsonResponse);
        return photos;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building url", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null){
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);  //milliseconds
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET"); //??? maybe should be TRANSLATE?
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream  = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON results", e);
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Photo> extractListPhotos(String jsonResponse) {
        ArrayList<Photo> photos = new ArrayList<>();
        JSONArray baseJsonArray = null;

        try {
            baseJsonArray = new JSONArray(jsonResponse);
            for(int i=0; i<baseJsonArray.length()-1; i++) {
                JSONObject currImage = baseJsonArray.getJSONObject(i);

                Photo photo = new Photo();

                String id = currImage.getString("id");
                photo.setId(id);
                int likes = currImage.getInt("likes");
                photo.setLikes(likes);
                boolean liked_by_user = currImage.getBoolean("liked_by_user");
                photo.setLiked_by_user(liked_by_user);

                JSONObject user = currImage.getJSONObject("user");
                String username = user.getString("username");
                photo.setUsername(username);
                JSONObject profileImage = user.getJSONObject("profile_image");
                String profileImageUrl = profileImage.getString("small");
                photo.setProfileImageUrl(profileImageUrl);

                JSONObject imageUrls = currImage.getJSONObject("urls");
                String[] urls = new String[2];
                urls[0] = imageUrls.getString("small");
                urls[1] = imageUrls.getString("full");
                photo.setUrls(urls);

                photos.add(photo);
            }


        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return photos;
    }

}
