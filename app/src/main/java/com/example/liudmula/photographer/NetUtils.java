package com.example.liudmula.photographer;


import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
    private static final String UNSPLASH_URL = "https://unsplash.com/";
    private static final String APPLICATION_ID = "7d700121bb4d745cb38d5a47ec3935a816df74be074a50c35b6a73b61dee0421";
    private static final String SECRET_ID = "838583c96cf08b9d12acb15e6f65c05f6cea78407390599e8c8e62403a10b832";

    private NetUtils() {
    }

    //то для GET, працює добре
    public static List<Photo> fetchPhotoList(String requestedUrl){
        URL url = createUrl(requestedUrl);

        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url); //GET
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making http request", e);
        }

        ArrayList<Photo> photos = extractListPhotos(jsonResponse);
        return photos;
    }


    //От що не працює
    public static AuthToken getToken(String requestedUrl, String code){

        //створюю юрл по заданому стрінгу
        URL url = createUrl(requestedUrl);
        String jsonResponse = null;
        /*
        getEncodedParamTokenQuery - просто формує параметри реквесту вже з кодом(внизу функція)
         */

        String params = NetUtils.getEncodedParamTokenQuery(code);
        try {
            //реквест. отут помилка
            jsonResponse = makeHttpPostRequest(url, params); // POST
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making http request", e);
        }

        AuthToken token = extractTokenInfo(jsonResponse);
        return token;

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

    //то для гет
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

             //GET/POST
            urlConnection.setRequestMethod("GET");
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


    //пост реквест
    private static String makeHttpPostRequest(URL url, String params) throws IOException {
        String jsonResponse = "";

        if (url == null){
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            //коли відкриваю конекшн - помилка 404
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);  //milliseconds
            urlConnection.setConnectTimeout(15000);



            urlConnection.setRequestMethod("POST");

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            //це для задання параметрів(стек оверфлоу драйвен девелопмент)
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(params);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();


            //конект
            urlConnection.connect();


            //if (urlConnection.getResponseCode() == 200){
                inputStream  = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
//            }else{
//                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
//            }

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
                String urlSmall = imageUrls.getString("small");
                photo.setImageUrlSmall(urlSmall);


                photos.add(photo);
            }


        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return photos;
    }

    private static AuthToken extractTokenInfo(String jsoneResponce){
        AuthToken token = new AuthToken();
        JSONObject baseJsonObject = null;
        try {

            baseJsonObject = new JSONObject(jsoneResponce);
            String string = baseJsonObject.getString("access_token");
            token.setAccessToken(string);
            string = baseJsonObject.getString("token_type");
            token.setTokenType(string);
            string = baseJsonObject.getString("scope");
            token.setScope(string);
            int created = baseJsonObject.getInt("created_at");
            token.setCreatedAt(created);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;

    }


    public static String getLoginUrl() {
        return UNSPLASH_URL + "oauth/authorize"
                + "?client_id=" + APPLICATION_ID
                + "&redirect_uri=" + "photographer://auth.callback"
                + "&response_type=" + "code"
                + "&scope=" + "public+write_likes";
    }


    public static String getEncodedParamTokenQuery(String code){
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("client_id", APPLICATION_ID)
                .appendQueryParameter("client_secret", SECRET_ID)
                .appendQueryParameter("redirect_uri", "photographer://auth.callback")
                .appendQueryParameter("code", code)
                .appendQueryParameter("grant_type", "authorization_code");
        return builder.build().getEncodedQuery();
    }

}
