package com.example.android.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by David on 17/09/16.
 */
public class DetailAdapter extends CursorAdapter {
    private static final String LOG_TAG = DetailAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_MOVIE_DETAILS = 0;
    private static final int VIEW_TYPE_VIDEOS = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public DetailAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_MOVIE_DETAILS : VIEW_TYPE_VIDEOS;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final int viewType = getItemViewType(cursor.getPosition());

        int layoutId = -1;
        if (viewType == VIEW_TYPE_MOVIE_DETAILS) {
            layoutId = R.layout.list_item_movie;
        } else if (viewType == VIEW_TYPE_VIDEOS) {
            layoutId = R.layout.list_item_video;
        }

        final View rootView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        final ViewHolder viewHolder = new ViewHolder(rootView);
        rootView.setTag(viewHolder);
        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final int viewType = getItemViewType(cursor.getPosition());
        if (viewType == VIEW_TYPE_MOVIE_DETAILS) {
            setPoster(viewHolder, context, cursor);
            setMovieTitle(viewHolder, cursor);
            setOverview(viewHolder, cursor);
            setRating(viewHolder, context, cursor);
            setRelease(viewHolder, cursor);
        } else if (viewType == VIEW_TYPE_VIDEOS) {
            setVideoText(viewHolder, cursor);
        }
//        Log.d(LOG_TAG, "bindView cursor: " + DatabaseUtils.dumpCursorToString(cursor));
    }

    private void setPoster(ViewHolder viewHolder, Context context, Cursor cursor) {
        final String posterUrl = cursor.getString(DetailFragment.COL_MOVIE_POSTER_PATH);

        if (posterUrl != null) {
            Picasso.with(context).load(posterUrl).into(viewHolder.posterView);
        }
    }

    private void setMovieTitle(ViewHolder viewHolder, Cursor cursor) {
        final String title = cursor.getString(DetailFragment.COL_MOVIE_TITLE);
        viewHolder.titleView.setText(title);
    }

    private void setOverview(ViewHolder viewHolder, Cursor cursor) {
        final String overview = cursor.getString(DetailFragment.COL_MOVIE_OVERVIEW);
        viewHolder.overviewView.setText(overview);
    }

    private void setRating(ViewHolder viewHolder, Context context, Cursor cursor) {
        final double userRating = cursor.getDouble(DetailFragment.COL_MOVIE_RATING);
        viewHolder.userRatingView.setText(Utility.formatRating(context, userRating));
    }

    private void setRelease(ViewHolder viewHolder, Cursor cursor) {
        final String releaseDate = cursor.getString(DetailFragment.COL_MOVIE_RELEASE);
        viewHolder.releaseDateView.setText(releaseDate.substring(0, 4));
    }

    private void setVideoText(ViewHolder viewHolder, Cursor cursor) {
//        final String videoPosition = cursor.getString(DetailFragment.COL_VIDEO__ID);
        viewHolder.videoTextView.setText("Video");
    }

    /* Used to speed up loading the views within the list view */
    public static class ViewHolder {

        final ImageView posterView;
        final TextView titleView;
        final TextView overviewView;
        final TextView userRatingView;
        final TextView releaseDateView;
        final TextView videoTextView;

        public ViewHolder(View rootView) {
            posterView = (ImageView) rootView.findViewById(R.id.detail_image_view);
            titleView = (TextView) rootView.findViewById(R.id.title);
            overviewView = (TextView) rootView.findViewById(R.id.overview);
            userRatingView = (TextView) rootView.findViewById(R.id.rating);
            releaseDateView = (TextView) rootView.findViewById(R.id.release);
            videoTextView = (TextView) rootView.findViewById(R.id.video_text_view);
        }
    }
}
