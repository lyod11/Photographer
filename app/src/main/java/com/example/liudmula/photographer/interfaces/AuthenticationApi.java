package com.example.liudmula.photographer.interfaces;


import com.example.liudmula.photographer.models.AuthToken;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by liudmula on 20.01.17.
 */

public interface AuthenticationApi {
    @POST("oauth/token")
    Call<AuthToken> getAccessToken(@Query("client_id") String client_id,
                                   @Query("client_secret") String client_secret,
                                   @Query("redirect_uri") String redirect_uri,
                                   @Query("code") String code,
                                   @Query("grant_type") String grant_type);
}
