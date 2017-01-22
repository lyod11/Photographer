package com.example.liudmula.photographer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.liudmula.photographer.models.AuthToken;
import com.example.liudmula.photographer.models.Photo;
import com.example.liudmula.photographer.tasks.LoadImageTask;
import com.example.liudmula.photographer.tasks.NetworkRequestTask;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoadImageTask.Listener,
        NetworkRequestTask.RequestListener, AuthService.OnRequestTokenListener {


    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private List<Photo> mPhotos;
    private AuthService mService;   //authentication service
    private String mAccessTokenId;
    private LinearLayoutManager mLayoutManager;
    private final MainActivity mInstance = this;

    boolean loading = true;    //for loading new photo, when scrolled down
    int pastVisibleItems, visibleItemCount, totalItemCount;   //recycler items


    public static final String SORT_BY_LATEST = "latest";
    public static final String SORT_BY_OLDEST = "oldest";
    public static final String SORT_BY_POPULAR = "popular";
    public static final String PHOTOS_IN_PAGE = "10";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * Create asyncTask for random foto network request
         */

        new NetworkRequestTask(this).execute(NetUtils.getRandomPhotosUrl(PHOTOS_IN_PAGE));
        mPhotos = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mImageAdapter = new ImageAdapter(mPhotos, mInstance);
        mRecyclerView.setAdapter(mImageAdapter);

        /*
         * scroll listener for uploading new photo, when scrolled down
         */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy>0){
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if(loading){
                        if ( (visibleItemCount + pastVisibleItems) >= totalItemCount)
                        {
                            loading = false;
                            new NetworkRequestTask(mInstance).execute(NetUtils.getRandomPhotosUrl(PHOTOS_IN_PAGE));

                        }
                    }
               }
            }
        });

        // fab for adding ONE random photo on top of recyclerview

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NetworkRequestTask(mInstance).execute(NetUtils.getRandomPhotosUrl("1"));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_loggin) {
            startLoginIntent();
            return true;

            /*
             * Creating alert dialog for sorting photo
             */

        } if(id == R.id.action_sort) {
            AlertDialog.Builder buildDialog = new AlertDialog.Builder(MainActivity.this);
            buildDialog.setTitle("Сортувати за");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Найновіші");
            arrayAdapter.add("Найстаріші");
            arrayAdapter.add("Найпопулярніші");

            buildDialog.setNegativeButton("Відмінити", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            buildDialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPhotos.clear();
                    switch (which){
                        case 0:
                            new NetworkRequestTask(mInstance).execute(NetUtils.getSortedPhotoListUrl(PHOTOS_IN_PAGE, SORT_BY_LATEST));
                            break;
                        case 1:
                            new NetworkRequestTask(mInstance).execute(NetUtils.getSortedPhotoListUrl(PHOTOS_IN_PAGE, SORT_BY_OLDEST));
                            break;
                        case 2:
                            new NetworkRequestTask(mInstance).execute(NetUtils.getSortedPhotoListUrl(PHOTOS_IN_PAGE, SORT_BY_POPULAR));
                            break;
                    }
                }
            });
            buildDialog.show();
         }
        return super.onOptionsItemSelected(item);
    }


    /*
     * starting login Unsplash web-page
     */
    public void startLoginIntent(){
        Uri uri = Uri.parse(NetUtils.getLoginUrl());
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }


    /*
     * callback from LoadImageTask. It replace old list mPhotos with new one
     * It contains photo bitmap
     */
    @Override
    public void onImageLoaded(List<Photo> photos) {

        mPhotos = photos;
        mImageAdapter.notifyDataSetChanged();
        if(loading == false){   //flag, that new photos were loaded in the scrolled bottom
            loading = true;
        }

    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error Loading image", Toast.LENGTH_LONG).show();
    }

    /*
     * Callback from NetworkRequestTask
     * If contains no photo or scrolled down, add new photo to the list end
     * Execute async task for downloading photos from internet
     */

    @Override
    public void onRequestSuccessful(List<Photo> photos) {
        if(mPhotos.size() == 0 || loading == false) {
            mPhotos.addAll(photos);
        } else {
            mPhotos.addAll(0, photos);
        }
        new LoadImageTask(this).execute(mPhotos);
    }


    @Override
    public void onRequestError() {
        Toast.makeText(this, "Request error", Toast.LENGTH_LONG).show();
    }

    /*
     * Catch redirection intent from Unsplash authorization page
     */

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mService = AuthService.getService();
        Uri data = intent.getData();
        if(data != null){

            String code = data.getQueryParameter("code");
            //triggered service with POST method, that contains requested code
            mService.requestAccessToken(code, this);
        }
    }


    /*
     * Callback from AuthService. Should return authentication token
     */
    @Override
    public void onRequestTokenSuccessful(Call<AuthToken> call, Response<AuthToken> response) {
        if(response.isSuccessful()){
            mAccessTokenId = response.body().getAccess_token();
            mImageAdapter.setTokenId(mAccessTokenId);
            mPhotos.clear();   //reloading photos
            new NetworkRequestTask(this).execute(NetUtils.getRandomPhotosUrl(PHOTOS_IN_PAGE));
        }
    }

    @Override
    public void onRequestTokenError(Call<AuthToken> call) {

    }



}
