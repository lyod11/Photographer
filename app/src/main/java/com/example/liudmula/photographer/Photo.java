package com.example.liudmula.photographer;

import android.graphics.Bitmap;

/**
 * Created by liudmula on 18.01.17.
 */

public class Photo {
    private String id;
    private String likes;
    private boolean liked_by_user;
    private String username;
    private String profile_image;
    private String[] urls;
    private Bitmap[] imageBitmaps;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public boolean isLiked_by_user() {
        return liked_by_user;
    }

    public void setLiked_by_user(boolean liked_by_user) {
        this.liked_by_user = liked_by_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public Bitmap[] getImageBitmaps() {
        return imageBitmaps;
    }

    public void setImageBitmaps(Bitmap[] imageBitmaps) {
        this.imageBitmaps = imageBitmaps;
    }
}
