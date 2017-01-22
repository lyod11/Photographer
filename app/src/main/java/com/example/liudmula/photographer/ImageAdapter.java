package com.example.liudmula.photographer;


import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liudmula.photographer.interfaces.LikeService;
import com.example.liudmula.photographer.interfaces.UnlikeService;
import com.example.liudmula.photographer.models.Photo;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by liudmula on 18.01.17.
 */

/*
 * For POST and DELETE requests was used Retrofit2 library
 */


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<Photo> mPhotos;
    private String mTokenId;
    private Snackbar mSnackbar;
    private MainActivity instance;



    public ImageAdapter(List<Photo> photos, MainActivity instance){
        mPhotos = photos;
        this.instance = instance;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_image_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageAdapter.ViewHolder holder, final int pos) {

        final int position = pos;
        holder.mIvRow.setImageBitmap(mPhotos.get(pos).getImageBitmapSmall());
        holder.mTvUsernameRow.setText(mPhotos.get(pos).getUsername());
        holder.mTvLikesRow.setText(mPhotos.get(pos).getLikes().toString());

        if(mPhotos.get(pos).isLiked_by_user()){
            holder.mImgBtnLike.setImageResource(R.drawable.ic_heart_red);
        }

        /*
         * Listener for liking photo
         */

        holder.mImgBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                 * user must be authorized for liking photos
                 */
                if(mTokenId == null){
                    mSnackbar = Snackbar.make(v, "Для даної дії потрібна авторизація", Snackbar.LENGTH_LONG);
                    mSnackbar.setAction("Увійти", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            instance.startLoginIntent();
                        }
                    });
                    mSnackbar.show();
                } else {

                    if (mPhotos.get(position).isLiked_by_user() == false) {

                        //POST like request

                        LikeService likeService = createRetrofit().create(LikeService.class);
                        final Call<Photo> call = likeService.likePhoto(mPhotos.get(pos).getId()); //photo id like a parameter
                        call.enqueue(new Callback<Photo>() {
                            @Override
                            public void onResponse(Call<Photo> call, Response<Photo> response) {
                                //if request is successful - update recycler item
                                if(response.body()!=null) {
                                    mPhotos.get(position).setLiked_by_user(true);
                                    mPhotos.get(position).setLikes(mPhotos.get(position).getLikes() + 1);
                                    holder.mTvLikesRow.setText(mPhotos.get(pos).getLikes().toString());
                                    holder.mImgBtnLike.setImageResource(R.drawable.ic_heart_red);
                                }
                            }

                            @Override
                            public void onFailure(Call<Photo> call, Throwable t) {
                                Log.e("TAG", "Fail");
                            }
                        });



                    } else {

                        //POST unlike request

                        UnlikeService unlikeService = createRetrofit().create(UnlikeService.class);
                        final Call<Void> call = unlikeService.unlikePhoto(mPhotos.get(pos).getId());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                //if request is successful - update recycler item
                                if(response.body()!=null) {
                                    mPhotos.get(position).setLiked_by_user(false);
                                    mPhotos.get(position).setLikes(mPhotos.get(position).getLikes() - 1);
                                    holder.mTvLikesRow.setText(mPhotos.get(pos).getLikes().toString());
                                    holder.mImgBtnLike.setImageResource(R.drawable.ic_heart_white);
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("TAG", "Fail");
                            }
                        });
                    }
                }
            }
        });
    }

    public void setTokenId(String tokenId){
        mTokenId = tokenId;
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mIvRow;
        private TextView mTvUsernameRow;
        private TextView mTvLikesRow;
        private ImageButton mImgBtnLike;

        public ViewHolder(View itemView) {
            super(itemView);

            mIvRow = (ImageView)itemView.findViewById(R.id.iv_card_row);
            mTvUsernameRow = (TextView)itemView.findViewById(R.id.tv_card_username);
            mTvLikesRow = (TextView)itemView.findViewById(R.id.tv_card_likes);
            mImgBtnLike = (ImageButton)itemView.findViewById(R.id.btn_image_card_like);
        }
    }

    public void renewPhotos(List<Photo> newList){
        mPhotos.clear();
        mPhotos.addAll(newList);
        notifyDataSetChanged();
    }



    private Retrofit createRetrofit(){
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer "+ mTokenId).build();
                        return chain.proceed(request);
                    }
                }).build();


        return new Retrofit.Builder()
                        .baseUrl(NetUtils.API_UNSPLASH_URL)
                        .client(httpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
    }
}
