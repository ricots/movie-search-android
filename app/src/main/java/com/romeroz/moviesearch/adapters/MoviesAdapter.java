package com.romeroz.moviesearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.romeroz.moviesearch.R;
import com.romeroz.moviesearch.activities.MovieDetailActivity;
import com.romeroz.moviesearch.eventbus.MovieAddedEvent;
import com.romeroz.moviesearch.eventbus.MovieRemovedEvent;
import com.romeroz.moviesearch.model.Movie;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Movie> mItemArrayList;
    private Realm mRealm;

    public MoviesAdapter(Context context) {
        this.mContext = context;
        // Get Realm Instance
        mRealm = Realm.getDefaultInstance();
     }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        String title = mItemArrayList.get(position).getTitle();
        String year = mItemArrayList.get(position).getYear();
        String imdbID = mItemArrayList.get(position).getImdbID();
        String poster = mItemArrayList.get(position).getPoster();

        viewHolder.mTitleTextView.setText(title);
        viewHolder.mYearTextView.setText(year);
        // Clear the ImageView to not show previous recycled view's image
        viewHolder.mPosterImageView.setImageResource(R.drawable.blank_movie_poster);

        if(!poster.isEmpty()) {
            // Use Universal Image Loader to load the photo and save it.
            // Even if the signed URL expires, if we use the same URI, in another activity, it will still
            // load the image because we have set disk cacheing on with UIL
            ImageLoader.getInstance().displayImage(poster, viewHolder.mPosterImageView);
        }

        if(movieIsFavorite(imdbID)){
            viewHolder.mFavoriteButton.setImageResource(R.drawable.ic_star_black_24dp);
            // Todo: not working to set color
            viewHolder.mFavoriteButton.setSupportBackgroundTintList(
                    ContextCompat.getColorStateList(mContext, R.color.gold));
        } else {
            // Defaults
            viewHolder.mFavoriteButton.setImageResource(R.drawable.ic_star_border_black_24dp);
            // todo not working
            viewHolder.mFavoriteButton.setSupportBackgroundTintList(
                    ContextCompat.getColorStateList(mContext, R.color.grey));
        }
    }

    @Override
    public int getItemCount() {
        if ( null == mItemArrayList) return 0;
        return mItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected CardView mCardView;
        protected ImageView mPosterImageView;
        protected TextView mTitleTextView;
        protected TextView mYearTextView;
        private AppCompatImageButton mFavoriteButton;

        public ViewHolder(View view) {
            super(view);

            // Get references to our views
            mCardView = (CardView) view.findViewById(R.id.card_view);
            mPosterImageView = (ImageView) view.findViewById(R.id.poster_image_view);
            mTitleTextView = (TextView) view.findViewById(R.id.title_text_view);
            mYearTextView = (TextView) view.findViewById(R.id.year_text_view);
            mFavoriteButton = (AppCompatImageButton) view.findViewById(R.id.favorite_button);

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Movie movie = mItemArrayList.get(getAdapterPosition());
                    String imbdId = movie.getImdbID();

                    if(movieIsFavorite(imbdId)){
                        /**
                         * Delete Movie from Realm
                         */
                        mRealm.beginTransaction();
                        // Query object and delete it
                        RealmResults<Movie> result = mRealm.where(Movie.class).equalTo("imdbID",imbdId).findAll();
                        result.deleteAllFromRealm();
                        mRealm.commitTransaction();

                        // Update UI
                        mFavoriteButton.setImageResource(R.drawable.ic_star_border_black_24dp);

                        // Notify listeners
                        EventBus.getDefault().post(new MovieRemovedEvent(imbdId));
                    } else {
                        /**
                         * Add Movie to Realm
                         */
                        mRealm.beginTransaction(); // must begin
                        mRealm.insert(movie);
                        mRealm.commitTransaction(); // must commit

                        // Update UI
                        mFavoriteButton.setImageResource(R.drawable.ic_star_black_24dp);

                        // Notify Listeners
                        EventBus.getDefault().post(new MovieAddedEvent(movie));
                    }

                }
            });

            mCardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Pass movie to Activity
                    Intent i = new Intent(mContext, MovieDetailActivity.class);

                    Movie movie = mItemArrayList.get(getAdapterPosition());
                    Gson gson = new Gson();
                    i.putExtra(MovieDetailActivity.ARG_MOVIE, gson.toJson(movie));

                    mContext.startActivity(i);
                }
            });

        }
    }

    /**
     * Check if movie exists in local realm storage
     * @param imdbID id
     * @return
     */
    private Boolean movieIsFavorite(String imdbID){
        mRealm.beginTransaction(); // must begin

        Movie movie = mRealm.where(Movie.class).equalTo("imdbID", imdbID).findFirst();

        mRealm.commitTransaction(); // must commit

        if(movie != null){
            return true;
        } else {
            return false;
        }
    }

    public void removeMovieByImbdID(String imbdID){
        int position = 0;
        for(Movie movie : mItemArrayList) {
            if(movie.getImdbID().equals(imbdID)) {
                mItemArrayList.remove(movie);
                notifyItemRemoved(position);
            }
            position = position + 1;
        }
    }

    public void swapData(ArrayList<Movie> itemArrayList) {
        this.mItemArrayList = null;
        this.mItemArrayList = itemArrayList;;

        notifyDataSetChanged();
    }

    public void addItem(Movie item) {
        if (item != null){
            mItemArrayList.add(item);
            notifyItemInserted(mItemArrayList.size()-1);
        }
    }

    public void removeItem(int position) {
        mItemArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void changeItem(Movie itemArrayList, int position){
        if (itemArrayList != null){
            mItemArrayList.set(position, itemArrayList);
            notifyItemChanged(position);
        }
    }
}