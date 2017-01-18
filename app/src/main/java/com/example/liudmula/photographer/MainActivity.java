package com.example.liudmula.photographer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoadImageTask.Listener{

    private ImageView mImageView;
    private Bitmap mBitmapTest;
    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;

    private static final String SIMPLE_IMAGE_URL = "https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        new LoadImageTask(this).execute(SIMPLE_IMAGE_URL);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        //mImageView.setImageBitmap(bitmap);
        mBitmapTest = bitmap;
        mImageView = (ImageView) findViewById(R.id.iv_card_row);

        ArrayList<Photo> photos = new ArrayList<>();

        photos.add(new Photo("username1", mBitmapTest));
        photos.add(new Photo("username2", mBitmapTest));
        photos.add(new Photo("username3", mBitmapTest));
        photos.add(new Photo("username4", mBitmapTest));
        photos.add(new Photo("username5", mBitmapTest));


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mImageAdapter = new ImageAdapter(photos);
        mRecyclerView.setAdapter(mImageAdapter);

    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error Loading image", Toast.LENGTH_LONG).show();
    }
}
