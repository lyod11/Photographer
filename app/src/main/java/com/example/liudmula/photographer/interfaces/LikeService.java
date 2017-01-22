package com.example.liudmula.photographer.interfaces;

import com.example.liudmula.photographer.MainActivity;
import com.example.liudmula.photographer.models.Photo;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by liudmula on 22.01.17.
 */

public interface LikeService {
    @POST ("photos/{id}/like")
    Call<Photo> likePhoto(@Path("id") String id);
}
