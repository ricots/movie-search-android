package com.romeroz.moviesearch.retrofit;


import com.romeroz.moviesearch.model.Movie;
import com.romeroz.moviesearch.model.MovieSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// For full movie API info see: http://www.omdbapi.com/
public interface MovieInterface {

    /**
     * Search for movies by title from omdbapi.com
     *
     * @param movieTitle Movie title to search for
     * @param type Type of result to return - valid options: movie, series, episode
     * @param page Page number to return - valid options: 1-100
     * @return
     */
    @GET("./") // Note: Use "./" if there is no path to put in
    Call<MovieSearchResponse> getSearchMovies(
            @Query("s") String movieTitle, @Query("type") String type, @Query("page") int page);

    /**
     * Get movie by imbdID
     * @param imbdID ID
     * @return
     */
    @GET("./")
    Call<Movie> getMovie(
            @Query("i") String imbdID);

}
