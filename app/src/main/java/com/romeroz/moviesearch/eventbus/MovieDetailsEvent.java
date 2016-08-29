package com.romeroz.moviesearch.eventbus;

public class MovieDetailsEvent {
    String data;
    int result;

    public MovieDetailsEvent(String data, int result) {
        this.data = data;
        this.result = result;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
