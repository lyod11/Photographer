package com.example.liudmula.photographer;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoadImageTask.Listener,
        NetworkRequestTask.RequestListener, NetworkRequestTokenTask.RequestTokenListener {



    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private List<Photo> mPhotos = new ArrayList<>();
    private AuthToken mAuthToken;



    private static final String LIST_PHOTOS_URL = "https://api.unsplash.com/photos/?client_id=40a70581b73ac9754c7e45d1312acf3c176f59c638863ee599c448d649dc85d3";
    private static final String TOKEN_REQUEST_URL = "https://unsplash.com/oauth/token";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // new NetworkRequestTask(this).execute(LIST_PHOTOS_URL);

        mImageView = (ImageView) findViewById(R.id.iv_card_row);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        /*
        При натисненні на FloatingActionButton викликає інтент і перенаправляє
        на сторінку авторизації ансплешу
        Перенарпавляє назад в майн актівіті. Інтент ловлю методом onNewIntent(внизу)
         */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Uri uri = Uri.parse(NetUtils.getLoginUrl());
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
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
    public void onImageLoaded(List<Photo> photos) {
        mPhotos = (ArrayList) photos;

        mImageAdapter = new ImageAdapter((ArrayList)photos);
        mRecyclerView.setAdapter(mImageAdapter);

    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error Loading image", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onRequestSuccessful(List<Photo> photos) {
        new LoadImageTask(this).execute(photos);
    }

    @Override
    public void onRequestError() {
        Toast.makeText(this, "Request error", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if(data != null){

            /*
            витягую код з інтенту
             */
            String code = data.getQueryParameter("code");
            /*викликаю новий асинктаск для нетворкреквесту
            далі всі функції в NetUtils
            */
            new NetworkRequestTokenTask(this).execute(TOKEN_REQUEST_URL, code);

        }

    }

    @Override
    public void onRequestTokenSuccessful(AuthToken token) {
        mAuthToken = token;
    }

    @Override
    public void onRequestTokenError() {

    }
}
