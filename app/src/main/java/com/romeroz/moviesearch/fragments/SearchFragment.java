package com.romeroz.moviesearch.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.romeroz.moviesearch.R;
import com.romeroz.moviesearch.Utility;
import com.romeroz.moviesearch.adapters.MoviesAdapter;
import com.romeroz.moviesearch.model.Movie;
import com.romeroz.moviesearch.model.MovieSearchResponse;
import com.romeroz.moviesearch.services.MovieService;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private View mMainView;
    private RecyclerView mMoviesRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private ArrayList<Movie> mMovieList;

    private ProgressBar mProgressBar;
    private BroadcastReceiver mBroadcastReceiver;

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
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Set up RecyclerView
        mMoviesRecyclerView = (RecyclerView) rootView.findViewById(R.id.pokedex_recycler_view);
        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mMoviesRecyclerView.hasFixedSize();
        mMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // If using nested scroll view, set to false to enable smooth scrolling
        mMoviesRecyclerView.setNestedScrollingEnabled(false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress);

        // Set up our RecyclerView's Adapter
        mMoviesAdapter = new MoviesAdapter(getActivity());
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        // TODO: progress bar isnt showing...
        // Main View (used to show/hide spinner)
        mMainView = (View) rootView.findViewById(R.id.root_view);
        // Show progress spinner
        Utility.showProgress(true, getActivity(), mProgressBar, mMainView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Roman", "SearchFragment.onResume");
        // Register the BroadcastReceiver to receive broadcasts.
        IntentFilter filter = new IntentFilter();
        filter.addAction(MovieService.ACTION_SEARCH_MOVIES);
        mBroadcastReceiver = new MovieSearchReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Roman", "SearchFragment.onPause");

        // Unregister BroadcastReceiver. It is possible it wasn't registered so wrap in try/catch
        try {
            // Unregister the broadcast receivers on pause because we do not want to receive new info
            // when an instance of the activity/fragment is in the background!
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void searchForMovie(String movieTitle){
        // Show progress spinner
        Utility.showProgress(true, getActivity(), mProgressBar, mMainView);
        // Make api call
        MovieService.startActionSearchMovies(getActivity(), movieTitle);
    }

    // When you receive data back from the IntentService network call, handle it.
    public class MovieSearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();

            int result = extras.getInt(MovieService.RESULT);

            if(result == MovieService.STATUS_OK ){

                Gson gson = new Gson();
                String data = extras.getString(MovieService.DATA);
                MovieSearchResponse movieSearchResponse = gson.fromJson(data, MovieSearchResponse.class);

                mMovieList = (ArrayList<Movie>) movieSearchResponse.getMovies();
                mMoviesAdapter.swapData(mMovieList);

                // No results
                if(mMovieList == null){
                    Snackbar.make(mMainView,
                            "No results found", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

                // Hide spinner
                Utility.showProgress(false, getActivity(), mProgressBar, mMainView);

            }

        }
    }


}
