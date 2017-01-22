package com.example.liudmula.photographer.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.liudmula.photographer.models.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by liudmula on 18.01.17.
 */

public class LoadImageTask extends AsyncTask<List<Photo>, Void, List<Photo>> {

    private static final String LOG_TAG = LoadImageTask.class.getSimpleName();

    public interface Listener{
        void onImageLoaded(List<Photo> photos);
        void onError();
    }

    private Listener mListener;

    public LoadImageTask(Listener listener){
        mListener = listener;
    }



    @Override
    protected List<Photo> doInBackground(List<Photo>... params) {

        List<Photo> photos = params[0];

        for(int i=0; i<photos.size(); i++){
            Bitmap bitmap;
            try {
//                bitmap = BitmapFactory.decodeStream((InputStream) new URL(photos.get(i).getProfileImageUrl()).getContent());
//                photos.get(i).setProfileImage(bitmap);

                bitmap = BitmapFactory.decodeStream((InputStream) new URL(photos.get(i).getImageUrlSmall()).getContent());
                photos.get(i).setImageBitmapSmall(bitmap);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem with stream decoding", e);
            }
        }
        return photos;
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        if(photos.get(0).getImageBitmapSmall() != null) {
            mListener.onImageLoaded(photos);
        } else {
            mListener.onError();
        }
    }
}
