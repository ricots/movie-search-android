package com.romeroz.moviesearch.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.romeroz.moviesearch.MyApplication;
import com.romeroz.moviesearch.eventbus.MovieDetailsEvent;
import com.romeroz.moviesearch.eventbus.SearchMoviesEvent;
import com.romeroz.moviesearch.model.Movie;
import com.romeroz.moviesearch.model.MovieSearchResponse;
import com.romeroz.moviesearch.retrofit.RestManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Response;

/**
 * Database: http://www.omdbapi.com/
 */
public class MovieService extends IntentService {
    public static final String ACTION_SEARCH_MOVIES = "com.romeroz.moviesearch.services.action.SEARCH_MOVIES";
    public static final String ACTION_GET_MOVIE = "com.romeroz.moviesearch.services.action.GET_MOVIE";

    // For this.startActionSearchMovies()
    private static final String EXTRA_PARAM_MOVIE_TITLE = "com.romeroz.moviesearch.services.extra.MOVIE_TITLE";
    // For this.startActionGetMovie()
    private static final String EXTRA_PARAM_IMDB_ID = "com.romeroz.moviesearch.services.extra.IMDB_ID";

    public static final int STATUS_OK = 100;

    public MovieService() {
        super("MovieService");
    }

    public static void startActionSearchMovies(Context context, String movieTitle) {
        Intent intent = new Intent(context, MovieService.class);
        intent.setAction(ACTION_SEARCH_MOVIES);
        intent.putExtra(EXTRA_PARAM_MOVIE_TITLE, movieTitle);
        context.startService(intent);
    }

    public static void startActionGetMovie(Context context, String imdbID) {
        Intent intent = new Intent(context, MovieService.class);
        intent.setAction(ACTION_GET_MOVIE);
        intent.putExtra(EXTRA_PARAM_IMDB_ID, imdbID);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEARCH_MOVIES.equals(action)) {
                final String movieTitle = intent.getStringExtra(EXTRA_PARAM_MOVIE_TITLE);
                handleActionSearchMovies(movieTitle);
            } else if (ACTION_GET_MOVIE.equals(action)) {
                final String imdbID = intent.getStringExtra(EXTRA_PARAM_IMDB_ID);
                handleActionGetMovie(imdbID);
            }
        }
    }

    private void handleActionSearchMovies(String movieTitle) {
        RestManager mManager = new RestManager();
        MovieSearchResponse movieSearchResponse;

        try {
            Response<MovieSearchResponse> response = mManager
                    .getMovieInterface()
                    .getSearchMovies(movieTitle, "movie", 1)
                    .execute();

            // Debugging to make sure everything is working properly
            //Log.d(MyApplication.APP_TAG, "Retrofit2 returned: " + response.isSuccessful());
            //Log.d(MyApplication.APP_TAG, "Retrofit2 returned: " + response.raw());

            if (response.isSuccessful()) {
                movieSearchResponse = response.body();

                Gson gson = new Gson();

                EventBus.getDefault().post(new SearchMoviesEvent(gson.toJson(movieSearchResponse), STATUS_OK));

            } else {
                Log.d(MyApplication.APP_TAG, "Response empty");
            }

        } catch (IOException e) {
            // Handle error
            Log.d(MyApplication.APP_TAG, "ERROR:" + e.toString());
        }
    }

    private void handleActionGetMovie(String imdbID) {
        RestManager mManager = new RestManager();
        Movie movie;

        try {
            Response<Movie> response = mManager
                    .getMovieInterface()
                    .getMovie(imdbID)
                    .execute();

            // Debugging to make sure everything is working properly
            //Log.d(MyApplication.APP_TAG, "Retrofit2 returned: " + response.isSuccessful());
            //Log.d(MyApplication.APP_TAG, "Retrofit2 returned: " + response.raw());

            if (response.isSuccessful()) {
                movie = response.body();

                Gson gson = new Gson();
                EventBus.getDefault().post(new MovieDetailsEvent(gson.toJson(movie), STATUS_OK));

            } else {
                Log.d(MyApplication.APP_TAG, "Response empty");
            }

        } catch (IOException e) {
            // Handle error
            Log.d(MyApplication.APP_TAG, "ERROR:" + e.toString());
        }
    }
}
