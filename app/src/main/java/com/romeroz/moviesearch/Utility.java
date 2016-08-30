package com.romeroz.moviesearch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.romeroz.moviesearch.eventbus.MovieAddedEvent;
import com.romeroz.moviesearch.eventbus.MovieRemovedEvent;
import com.romeroz.moviesearch.model.Movie;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmResults;

public class Utility {

    /**
     * Check if Movie is a in our local Realm database. Add movie to Rleam if so or remove it otherwise.
     * Then update the favorites button image resource and broadcast event via EventBus.
     *
     * @param movie movie object (only needs to have imbdID)
     * @param appCompatImageButton favorites Button
     */
    public static void toggleAddingMovieToFavorites(Movie movie, AppCompatImageButton appCompatImageButton){
        // Get Realm Instance
        Realm mRealm = Realm.getDefaultInstance();
        String imbdID = movie.getImdbID();

        if(Utility.movieIsFavorite(imbdID)){
            /**
             * Delete Movie from Realm
             */
            mRealm.beginTransaction();
            // Query object and delete it
            RealmResults<Movie> result = mRealm.where(Movie.class).equalTo("imdbID",imbdID).findAll();
            result.deleteAllFromRealm();
            mRealm.commitTransaction();

            // Update UI of button
            appCompatImageButton.setImageResource(R.drawable.ic_star_border_black_24dp);

            // Notify listeners
            EventBus.getDefault().post(new MovieRemovedEvent(imbdID));
        } else {
            /**
             * Add Movie to Realm
             */
            mRealm.beginTransaction(); // must begin
            mRealm.insert(movie);
            mRealm.commitTransaction(); // must commit

            // Update UI of button
            appCompatImageButton.setImageResource(R.drawable.ic_star_black_24dp);

            // Notify Listeners
            EventBus.getDefault().post(new MovieAddedEvent(movie));
        }
    }

    /**
     * Check if movie exists in local realm storage
     * @param imdbID id
     * @return
     */
    public static Boolean movieIsFavorite(String imdbID){
        // Get Realm Instance
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction(); // must begin

        Movie movie = mRealm.where(Movie.class).equalTo("imdbID", imdbID).findFirst();

        mRealm.commitTransaction(); // must commit

        if(movie != null){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Shows the progress UI and hides another view.
     *
     * @param show Whether to show or not show progressView
     * @param context Context
     * @param progressView The local progressView
     * @param toggleView The view you want to hide when progressView is shown
     */
    public static void showProgress(final boolean show, Context context, final ProgressBar progressView, final View toggleView) {
        // Check if context is null
        if (context == null){
            return;
        }

        int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        toggleView.setVisibility(show ? View.GONE : View.VISIBLE);
        toggleView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                toggleView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Hide soft keyboard - Use this to hide keyboard when unsure of the current View
     * Usage: Utility.hideSoftKeyboard(MainActivity.this);
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //F ind the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Check if there is internet access making a simple ping to google.
     * Note: Must be run on background thread.
     * See: http://stackoverflow.com/questions/6493517/detect-if-android-device-has-internet-connection
     *
     * @Param context Context
     */
    public static boolean hasInternetAccess(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                if (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0){
                    Log.d(MyApplication.APP_TAG, "Internet available.");
                    return true;
                }
            } catch (IOException e) {
                Log.e(MyApplication.APP_TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(MyApplication.APP_TAG, "No network available!");
        }
        return false;
    }

    /**
     * Check if there is an available network (e.g. Wifi).
     * Note: only checks for network, does not check if there is internet access.
     *
     * Required Permission:
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     *
     * @Param context Context
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
