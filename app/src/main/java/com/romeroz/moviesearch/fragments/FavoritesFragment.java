package com.romeroz.moviesearch.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romeroz.moviesearch.R;
import com.romeroz.moviesearch.adapters.MoviesAdapter;
import com.romeroz.moviesearch.model.Movie;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class FavoritesFragment extends Fragment {

    private View mRootView;
    private RecyclerView mMoviesRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private ArrayList<Movie> mMovieList;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        mRootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Set up RecyclerView
        mMoviesRecyclerView = (RecyclerView) mRootView.findViewById(R.id.movie_recycler_view);
        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mMoviesRecyclerView.hasFixedSize();
        mMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // If using nested scroll view, set to false to enable smooth scrolling
        mMoviesRecyclerView.setNestedScrollingEnabled(false);

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

        loadFavorites();

        return mRootView;
    }

    private void loadFavorites(){
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction(); // must begin

        List<Movie> list =  mRealm.where(Movie.class).findAll();
        mMovieList = new ArrayList<>(list);

        mRealm.commitTransaction(); // must commit

        mMoviesAdapter.swapData(mMovieList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        // Save our data via Gson
        Gson gson = new Gson();
        outState.putString("itemList", gson.toJson(mMovieList));

        // Call super last
        super.onSaveInstanceState(outState);
    }

}
