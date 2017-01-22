package com.example.liudmula.photographer;

import com.example.liudmula.photographer.interfaces.AuthenticationApi;
import com.example.liudmula.photographer.models.AuthToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liudmula on 20.01.17.
 */

public class AuthService {
    private Call call;

    public interface OnRequestTokenListener {
        void onRequestTokenSuccessful(Call<AuthToken> call, Response<AuthToken> response);
        void onRequestTokenError(Call<AuthToken> call);
    }



    public void requestAccessToken(String code, final OnRequestTokenListener listener){
        Call<AuthToken> accessToken = buildApi()
                .getAccessToken(
                        NetUtils.APPLICATION_ID,
                        NetUtils.SECRET_ID,
                        NetUtils.CALLBACK_URL,
                        code,
                        "authorization_code");
        accessToken.enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                if(listener != null){
                    listener.onRequestTokenSuccessful(call, response);
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {
                if(listener != null){
                    listener.onRequestTokenError(call);
                }
            }
        });

        call = accessToken;
    }

    public static AuthService getService(){
        return new AuthService();
    }


    private AuthenticationApi buildApi (){
        return new Retrofit.Builder()
                .baseUrl(NetUtils.UNSPLASH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthenticationApi.class);
    }


}
