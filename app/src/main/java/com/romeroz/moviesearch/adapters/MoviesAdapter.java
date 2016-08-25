package com.romeroz.moviesearch.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.romeroz.moviesearch.R;
import com.romeroz.moviesearch.model.Movie;

import java.util.ArrayList;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Movie> mItemArrayList;

    public MoviesAdapter(Context context) {
        this.mContext = context;
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
        viewHolder.mPosterImageView.setImageResource(android.R.color.transparent);

        if(!poster.isEmpty()) {
            // Use Universal Image Loader to load the photo and save it.
            // Even if the signed URL expires, if we use the same URI, in another activity, it will still
            // load the image because we have set disk cacheing on with UIL
            ImageLoader.getInstance().displayImage(poster, viewHolder.mPosterImageView);
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

        public ViewHolder(View view) {
            super(view);

            // Get references to our views
            mCardView = (CardView) view.findViewById(R.id.card_view);
            mPosterImageView = (ImageView) view.findViewById(R.id.poster_image_view);
            mTitleTextView = (TextView) view.findViewById(R.id.title_text_view);
            mYearTextView = (TextView) view.findViewById(R.id.year_text_view);

            mCardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                   /* // Pass pokemon id to activity
                    int id = mItemArrayList.get(getAdapterPosition()).getId();

                    Intent i = new Intent(mContext, DetailActivity.class);
                    i.putExtra(DetailActivity.ARG_POKEMON_ID, id);

                    // For mPokemonImageView transition (see DetailActivity.setupUI())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Remember to set android:transitionName in activity_detail.xml and item_layout.xml
                        mContext.startActivity(i, ActivityOptions.makeSceneTransitionAnimation(
                                (Activity) mContext, mPokemonImageView, mPokemonImageView.getTransitionName()).toBundle());
                    } else {
                        // Code to run on older devices
                        mContext.startActivity(i);
                    }*/

                }
            });

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