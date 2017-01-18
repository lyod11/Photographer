package com.example.liudmula.photographer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by liudmula on 18.01.17.
 */

public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

    private static final String LOG_TAG = LoadImageTask.class.getSimpleName();

    public interface Listener{
        void onImageLoaded(Bitmap bitmap);
        void onError();
    }

    private Listener mListener;

    LoadImageTask(Listener listener){
        mListener = listener;
    }



    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return BitmapFactory.decodeStream((InputStream)new URL(params[0]).getContent());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with stream decoding", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
            mListener.onImageLoaded(bitmap);
        } else {
            mListener.onError();
        }
    }
}
