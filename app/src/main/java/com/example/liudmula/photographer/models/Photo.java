package com.example.liudmula.photographer.models;

import android.graphics.Bitmap;

/**
 * Created by liudmula on 18.01.17.
 */

public class Photo {
    private String id;
    private Integer likes;
    private boolean liked_by_user;
    private String username;
    private Bitmap profileImage;
    private String profileImageUrl;
    private String imageUrlSmall;
    private Bitmap imageBitmapSmall;

    public Photo(String username, Bitmap bitmap){
        this.username = username;
    }

    public Photo() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public String getImageUrlSmall() {
        return imageUrlSmall;
    }

    public void setImageUrlSmall(String imageUrlSmall) {
        this.imageUrlSmall = imageUrlSmall;
    }

    public Bitmap getImageBitmapSmall() {
        return imageBitmapSmall;
    }

    public void setImageBitmapSmall(Bitmap imageBitmapSmall) {
        this.imageBitmapSmall = imageBitmapSmall;
    }
}
