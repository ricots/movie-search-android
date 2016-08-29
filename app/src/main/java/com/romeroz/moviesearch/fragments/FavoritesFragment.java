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
import com.romeroz.moviesearch.eventbus.MovieAddedEvent;
import com.romeroz.moviesearch.eventbus.MovieRemovedEvent;
import com.romeroz.moviesearch.model.Movie;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

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

        // Register EventBus to receive events always
        EventBus.getDefault().register(this);

        return mRootView;
    }

    private void loadFavorites(){
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction(); // must begin

        RealmResults<Movie> realmResults =  mRealm.where(Movie.class).findAll();
        mRealm.commitTransaction(); // must commit

        /**
         * Important:
         * Use mRealm.copyFromRealm() to make a copy of results from realm.
         * If you do not, any transactions you do in realm will affect your in-memory arraylists.
         * We do this because we want to make our own pretty animations in our Adapter.
         *
         * Use this if you do not want to make copies:
         * mMovieList = new ArrayList<Movie>(realmResults);
         */
        mMovieList = (ArrayList<Movie>) mRealm.copyFromRealm(realmResults);

        mMoviesAdapter.swapData(mMovieList);
    }

    public void removeMovieFromAdapter(String imbdID){
        mMoviesAdapter.removeMovieByImbdID(imbdID);
    }

    public void addMoveToAdapter(Movie movie){
        mMoviesAdapter.addItem(movie);
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

    /**
     * Receiving MovieAddedEvent event when it happens,
     * Using sticky = true telling the activity please go and get the last MovieAddedEvent
     * that has been posted before (e.g. if we navigated away from activity).
     * See: http://greenrobot.org/eventbus/documentation/configuration/sticky-events/
     * */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMovieAddedEvent(MovieAddedEvent event){
        Movie movie = event.getMovie();
        addMoveToAdapter(movie);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMovieRemovedEvent(MovieRemovedEvent event){
        String imbdID = event.getImdbID();
        removeMovieFromAdapter(imbdID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Un-register EventBus to stop receiving events
        EventBus.getDefault().unregister(this);
    }

}
