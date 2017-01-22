package com.example.liudmula.photographer.interfaces;

import com.example.liudmula.photographer.models.Photo;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

/**
 * Created by liudmula on 22.01.17.
 */

public interface UnlikeService {
    @DELETE ("photos/{id}/like")
    Call<Void> unlikePhoto(@Path("id") String id);
}
