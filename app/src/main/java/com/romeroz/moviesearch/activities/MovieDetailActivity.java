package com.romeroz.moviesearch.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.romeroz.moviesearch.R;
import com.romeroz.moviesearch.Utility;
import com.romeroz.moviesearch.eventbus.MovieDetailsEvent;
import com.romeroz.moviesearch.eventbus.NoInternetEvent;
import com.romeroz.moviesearch.model.Movie;
import com.romeroz.moviesearch.services.MovieService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.realm.Realm;

public class MovieDetailActivity extends AppCompatActivity {

    public static String ARG_MOVIE = "MOVIE";

    // UI
    private Toolbar mToolbar;
    private AppCompatImageButton mFavoriteButton;
    private ImageView mPosterImageView;
    private TextView mTitleTextView;
    private TextView mRatingTextView;
    private TextView mRunningTimeTextView;
    private TextView mGenreTextView;
    private TextView mPlotTextView;
    private TextView mDirectorTextView;
    private TextView mReleasedTextView;
    private TextView mImbdRatingTextView;
    private TextView mMetascoreTextView;
    private TextView mAwardsTextView;
    private TextView mActorsTextView;
    private TextView mWriterTextView;
    private ProgressBar mProgressBar;
    private ScrollView mScrollView;
    private LinearLayout mMainView;

    private Realm mRealm;

    private Movie mMovie;
    private String mImbdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mPosterImageView = (ImageView) findViewById(R.id.poster_image_view);
        mFavoriteButton = (AppCompatImageButton) findViewById(R.id.favorite_button);
        mTitleTextView = (TextView) findViewById(R.id.title_text_view);
        mRatingTextView = (TextView) findViewById(R.id.rating_text_view);
        mRunningTimeTextView = (TextView) findViewById(R.id.running_time_text_view);
        mGenreTextView = (TextView) findViewById(R.id.genre_text_view);
        mPlotTextView = (TextView) findViewById(R.id.plot_text_view);
        mDirectorTextView = (TextView) findViewById(R.id.director_text_view);
        mReleasedTextView = (TextView) findViewById(R.id.released_text_view);
        mImbdRatingTextView = (TextView) findViewById(R.id.imbd_rating_text_view);
        mMetascoreTextView = (TextView) findViewById(R.id.metascore_text_view);
        mAwardsTextView = (TextView) findViewById(R.id.awards_text_view);
        mActorsTextView = (TextView) findViewById(R.id.actors_text_view);
        mWriterTextView = (TextView) findViewById(R.id.writer_text_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);
        mMainView = (LinearLayout) findViewById(R.id.main_linear_layout);

        // Setup Toolbar
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get Realm Instance
        mRealm = Realm.getDefaultInstance();

        // Get extras passed in from previous invoker
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Gson gson = new Gson();
            String movieData = extras.getString(ARG_MOVIE);
            mMovie = gson.fromJson(movieData, Movie.class);
            mImbdID = mMovie.getImdbID();
            getMovie(mImbdID);
        } else {
            throw new IllegalStateException("You must supply a imdbID to load this activity.");
        }

        setupFavoriteButton(mFavoriteButton);
    }

    private void setupFavoriteButton(final AppCompatImageButton appCompatImageButton){
        // Hide button initially until we load data
        appCompatImageButton.setVisibility(View.INVISIBLE);

        appCompatImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.toggleAddingMovieToFavorites(mMovie, appCompatImageButton);
            }
        });
    }

    // Start service to fetch movie data
    private void getMovie(String imbdID){
        // Show progress
        Utility.showProgress(true, MovieDetailActivity.this, mProgressBar, mScrollView);
        // Start IntentService
        MovieService.startActionGetMovie(this, imbdID);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoInternetEvent(NoInternetEvent event){
        // Hide main view
        mMainView.setVisibility(View.GONE);
        Snackbar.make(mScrollView,
                "No internet connection.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        // Hide spinner
        Utility.showProgress(false, this, mProgressBar, mScrollView);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onGetMovieDetailsEvent(MovieDetailsEvent event){
        int result = event.getResult();

        if(result == MovieService.STATUS_OK ){

            Gson gson = new Gson();
            String data = event.getData();
            // Update Movie object with complete data from api call
            mMovie = gson.fromJson(data, Movie.class);

            if(!mMovie.getPoster().isEmpty()) {
                ImageLoader.getInstance().displayImage(mMovie.getPoster(), mPosterImageView);
            }

            mTitleTextView.setText(mMovie.getTitle());
            mRatingTextView.setText(mMovie.getRated());
            mRunningTimeTextView.setText(mMovie.getRuntime());
            mGenreTextView.setText(mMovie.getGenre());
            mPlotTextView.setText(mMovie.getPlot());
            mDirectorTextView.setText(mMovie.getDirector());
            mReleasedTextView.setText(mMovie.getReleased());
            mImbdRatingTextView.setText(mMovie.getImdbRating() + "/10");
            mMetascoreTextView.setText(mMovie.getMetascore() + "/100");
            mAwardsTextView.setText(mMovie.getAwards());
            mActorsTextView.setText(mMovie.getActors());
            mWriterTextView.setText(mMovie.getWriter());

            if(Utility.movieIsFavorite(mImbdID)){
                // Update UI
                mFavoriteButton.setImageResource(R.drawable.ic_star_black_24dp);
            } else {
                // Update UI
                mFavoriteButton.setImageResource(R.drawable.ic_star_border_black_24dp);
            }

            // Show button
            mFavoriteButton.setVisibility(View.VISIBLE);

            // Show main view
            mMainView.setVisibility(View.VISIBLE);

            // Hide spinner
            Utility.showProgress(false, this, mProgressBar, mScrollView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register EventBus to receive events
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        // Un-register EventBus to stop receiving events
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
