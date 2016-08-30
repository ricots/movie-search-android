package com.romeroz.moviesearch.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestManager {

    private MovieInterface mMovieInterface;

    public MovieInterface getMovieInterface() {
        if (mMovieInterface == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://www.omdbapi.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mMovieInterface = retrofit.create(MovieInterface.class);
        }
        return mMovieInterface;
    }

}
