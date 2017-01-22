package com.example.liudmula.photographer.tasks;

import android.os.AsyncTask;

import com.example.liudmula.photographer.NetUtils;
import com.example.liudmula.photographer.models.Photo;

import java.util.List;

/**
 * Created by liudmula on 19.01.17.
 */

public class NetworkRequestTask extends AsyncTask <String, Void, List<Photo>> {

    private static final String LOG_TAG = NetworkRequestTask.class.getSimpleName();

    public interface RequestListener{
        void onRequestSuccessful(List<Photo> photos);
        void onRequestError();
    }

    private RequestListener mListener;

    public NetworkRequestTask(RequestListener listener){
        mListener = listener;
    }


    @Override
    protected List<Photo> doInBackground(String... params) {
        if(params == null){
            return null;
        }
        List<Photo> photos = NetUtils.fetchPhotoList(params[0]);
        return photos;
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        if(photos != null){
            mListener.onRequestSuccessful(photos);
        } else {
            mListener.onRequestError();
        }
    }
}
