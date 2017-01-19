package com.example.liudmula.photographer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by liudmula on 18.01.17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<Photo> mPhotos;

    public ImageAdapter(ArrayList<Photo> photos){
        mPhotos = photos;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_image_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int pos) {
        //holder.mIvRow.setImageBitmap(mPhotos.get(pos).getTestBitmap());
        holder.mTvRow.setText(mPhotos.get(pos).getUsername());
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mIvRow;
        private TextView mTvRow;

        public ViewHolder(View itemView) {
            super(itemView);

            mIvRow = (ImageView)itemView.findViewById(R.id.iv_card_row);
            mTvRow = (TextView)itemView.findViewById(R.id.tv_card_row);
        }
    }
}
