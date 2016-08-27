package com.romeroz.moviesearch.eventbus;

import com.romeroz.moviesearch.model.Movie;

public class MovieAddedEvent {
    private Movie movie;

    public MovieAddedEvent(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
