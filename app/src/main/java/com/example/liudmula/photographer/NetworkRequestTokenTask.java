package com.example.liudmula.photographer;

import android.os.AsyncTask;

import com.example.liudmula.photographer.models.AuthToken;

/**
 * Created by liudmula on 20.01.17.
 */

public class NetworkRequestTokenTask extends AsyncTask<String, Void, AuthToken>{

    public interface RequestTokenListener{
        void onRequestTokenSuccessful(AuthToken token);
        void onRequestTokenError();
    }

    private RequestTokenListener mListener;

    NetworkRequestTokenTask(RequestTokenListener listener){
        mListener = listener;
    }

    @Override
    protected AuthToken doInBackground(String... params) {
        if(params == null){
            return null;
        }
        return NetUtils.getToken(params[0], params[1]);
    }
}
