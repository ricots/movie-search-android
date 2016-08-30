package com.romeroz.moviesearch.fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romeroz.moviesearch.MyApplication;
import com.romeroz.moviesearch.R;
import com.romeroz.moviesearch.Utility;
import com.romeroz.moviesearch.adapters.MoviesAdapter;
import com.romeroz.moviesearch.eventbus.MovieAddedEvent;
import com.romeroz.moviesearch.eventbus.MovieRemovedEvent;
import com.romeroz.moviesearch.eventbus.NoInternetEvent;
import com.romeroz.moviesearch.eventbus.SearchMoviesEvent;
import com.romeroz.moviesearch.model.Movie;
import com.romeroz.moviesearch.model.MovieSearchResponse;
import com.romeroz.moviesearch.services.MovieService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private View mRootView;
    private RecyclerView mMoviesRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private ArrayList<Movie> mMovieList;

    private ProgressBar mProgressBar;

    public SearchFragment() {
        // Required empty public constructor
    }

    // This fragment takes no arguements
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment. Get the rootView and do stuff to it
        mRootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Set up RecyclerView
        mMoviesRecyclerView = (RecyclerView) mRootView.findViewById(R.id.movie_recycler_view);
        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mMoviesRecyclerView.hasFixedSize();
        mMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // If using nested scroll view, set to false to enable smooth scrolling
        mMoviesRecyclerView.setNestedScrollingEnabled(false);

        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);

        // Set up our RecyclerView's Adapter
        mMoviesAdapter = new MoviesAdapter(getActivity());
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        // Did we rotate the screen? If so load data saved in onSavedInstanceState()
        if(savedInstanceState != null){
            Gson gson = new Gson();
            String data = savedInstanceState.getString("itemList");
            // Set type for List
            Type type = new TypeToken<ArrayList<Movie>>(){}.getType();
            mMovieList = gson.fromJson(data, type);

            // Update adapter
            mMoviesAdapter.swapData(mMovieList);
        }

        // Register EventBus to receive events always
        EventBus.getDefault().register(this);

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save our data via Gson
        Gson gson = new Gson();
        outState.putString("itemList", gson.toJson(mMovieList));
    }

    public void updateMovieInAdapter(String imbdID){
        mMoviesAdapter.updateMovieByImbdID(imbdID);
    }

    public void searchForMovie(String movieTitle){
        // Show progress spinner
        Utility.showProgress(true, getActivity(), mProgressBar, mMoviesRecyclerView);
        // Make api call
        MovieService.startActionSearchMovies(MyApplication.getAppContext(), movieTitle);
    }

    /**
     * Receiving MovieAddedEvent event when it happens,
     * Using sticky = true telling the activity please go and get the last MovieAddedEvent
     * that has been posted before (e.g. if we navigated away from activity).
     * See: http://greenrobot.org/eventbus/documentation/configuration/sticky-events/
     * */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSearchMoviesEvent(SearchMoviesEvent event){
        int result = event.getResult();

        if(result == MovieService.STATUS_OK ){

            Gson gson = new Gson();
            String data = event.getData();
            MovieSearchResponse movieSearchResponse = gson.fromJson(data, MovieSearchResponse.class);

            mMovieList = (ArrayList<Movie>) movieSearchResponse.getMovies();
            mMoviesAdapter.swapData(mMovieList);

            // No results
            if(mMovieList == null){
                Snackbar.make(mRootView,
                        "No results found", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

            // Hide spinner
            Utility.showProgress(false, getActivity(), mProgressBar, mMoviesRecyclerView);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMovieAddedEvent(MovieAddedEvent event){
        Movie movie = event.getMovie();
        updateMovieInAdapter(movie.getImdbID());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMovieRemovedEvent(MovieRemovedEvent event){
        String imbdID = event.getImdbID();
        updateMovieInAdapter(imbdID);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoInternetEvent(NoInternetEvent event){
        Snackbar.make(mRootView,
                "No internet connection.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        // Hide spinner
        Utility.showProgress(false, getActivity(), mProgressBar, mMoviesRecyclerView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Un-register EventBus to stop receiving events
        EventBus.getDefault().unregister(this);
    }

}
