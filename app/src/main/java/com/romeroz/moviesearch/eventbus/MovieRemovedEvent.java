package com.romeroz.moviesearch.eventbus;

public class MovieRemovedEvent {
    private String imdbID;

    public MovieRemovedEvent(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }
}
