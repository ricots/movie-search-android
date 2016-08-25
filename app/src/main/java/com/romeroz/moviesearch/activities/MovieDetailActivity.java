package com.romeroz.moviesearch.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.romeroz.moviesearch.R;
import com.romeroz.moviesearch.model.Movie;
import com.romeroz.moviesearch.services.MovieService;

public class MovieDetailActivity extends AppCompatActivity {

    public static String ARG_IMBD_ID = "IMBD_ID";

    private ImageView mPosterImageView;
    private TextView mTitleTextView;

    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mPosterImageView = (ImageView) findViewById(R.id.poster_image_view);
        mTitleTextView = (TextView) findViewById(R.id.title_text_view);

        // Get extras passed in from previous invoker
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String imbdID  = extras.getString(ARG_IMBD_ID);
            getMovie(imbdID);
        } else {
            throw new IllegalStateException("You must supply a imbdID to load this activity");
        }


    }

    // Start service to fetch movie data
    private void getMovie(String imbdID){
        MovieService.startActionGetMovie(this, imbdID);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register the BroadcastReceiver to receive broadcasts.
        IntentFilter filter = new IntentFilter();
        filter.addAction(MovieService.ACTION_GET_MOVIE);
        mBroadcastReceiver = new MovieReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister BroadcastReceiver. It is possible it wasn't registered so wrap in try/catch
        try {
            // Unregister the broadcast receivers on pause because we do not want to receive new info
            // when an instance of the activity/fragment is in the background!
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    // When you receive data back from the IntentService network call, handle it.
    public class MovieReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(MovieService.ACTION_GET_MOVIE)){
                Bundle extras = intent.getExtras();
                handleActionGetMovie(extras);
            }
        }
    }

    private void handleActionGetMovie(Bundle extras){
        int result = extras.getInt(MovieService.RESULT);

        if(result == MovieService.STATUS_OK ){

            Gson gson = new Gson();
            String data = extras.getString(MovieService.DATA);
            Movie movie = gson.fromJson(data, Movie.class);

            if(!movie.getPoster().isEmpty()) {
                ImageLoader.getInstance().displayImage(movie.getPoster(), mPosterImageView);
            }

            mTitleTextView.setText(movie.getTitle());

            // Hide spinner
            //Utility.showProgress(false, getActivity(), mProgressBar, mMainView);

        }

    }


}
