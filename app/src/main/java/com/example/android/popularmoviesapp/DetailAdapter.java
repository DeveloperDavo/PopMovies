package com.example.android.popularmoviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;
import com.example.android.popularmoviesapp.data.SingleMovieCursorBuilder;
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
//        Log.d(LOG_TAG, "getViewTypeCount");
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_MOVIE_DETAILS : VIEW_TYPE_VIDEOS;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.d(LOG_TAG, "getView");
        return super.getView(position, convertView, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        Log.d(LOG_TAG, "newView");
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
//        Log.d(LOG_TAG, "bindView");
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final int viewType = getItemViewType(cursor.getPosition());
        if (viewType == VIEW_TYPE_MOVIE_DETAILS) {
            setPoster(viewHolder, context, cursor);
            setMovieTitle(viewHolder, cursor);
            setOverview(viewHolder, cursor);
            setRating(viewHolder, context, cursor);
            setRelease(viewHolder, cursor);
            setButtonViewAndPersistChoice(viewHolder, context, cursor);
        } else if (viewType == VIEW_TYPE_VIDEOS) {
            setVideoText(viewHolder, cursor);
        }
    }

    private void setPoster(ViewHolder viewHolder, Context context, Cursor cursor) {
        Picasso.with(context).load(Utility.getPosterPathFrom(cursor)).into(viewHolder.posterView);
//        viewHolder.posterView.setImageBitmap(Utility.getBitmapFromBlob(cursor));
    }

    private void setMovieTitle(ViewHolder viewHolder, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_TITLE);
        final String title = cursor.getString(columnIndex);
        viewHolder.titleView.setText(title);
    }

    private void setOverview(ViewHolder viewHolder, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW);
        final String overview = cursor.getString(columnIndex);
        viewHolder.overviewView.setText(overview);
    }

    private void setRating(ViewHolder viewHolder, Context context, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_RATING);
        final double userRating = cursor.getDouble(columnIndex);
        viewHolder.userRatingView.setText(Utility.formatRating(context, userRating));
    }

    private void setRelease(ViewHolder viewHolder, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE);
        final String releaseDate = cursor.getString(columnIndex);
        viewHolder.releaseDateView.setText(releaseDate.substring(0, 4));
    }

    private void setVideoText(ViewHolder viewHolder, Cursor cursor) {
        final int videoCount = cursor.getPosition();
        viewHolder.videoTextView.setText("Video " + videoCount);
    }

    private void setButtonViewAndPersistChoice(final ViewHolder viewHolder,
                                               final Context context, final Cursor cursor) {
        final Button favoriteButton = viewHolder.favoriteButton;
        setButtonText(cursor, favoriteButton);
        setButtonColor(context, cursor, favoriteButton);
        updateMovieOnClick(context, cursor, favoriteButton);
    }

    private void setButtonText(Cursor cursor, Button favoriteButton) {
        String buttonText;
//        Log.d(LOG_TAG, "cursorDump: " + DatabaseUtils.dumpCursorToString(cursor));
        if (isFavorite(cursor)) {
            buttonText = "remove from favorites";
        } else {
            buttonText = "add to favorites";
        }
        favoriteButton.setText(buttonText);
    }

    private void setButtonColor(Context context, Cursor cursor, Button favoriteButton) {
        int color;
        if (isFavorite(cursor)) {
            color = R.color.colorButtonAfterClick;
        } else {
            color = R.color.colorAccent;
        }
        final int buttonColor = ContextCompat.getColor(context, color);
        favoriteButton.setBackgroundColor(buttonColor);
    }

    private boolean isFavorite(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_FAVORITE);
        if (columnIndex == -1) {
            throw new IllegalStateException("Favorite column not found");
        }

        final long favorite = cursor.getInt(columnIndex);

        return favorite != 0;
    }

    private void updateMovieOnClick(final Context context, final Cursor cursor, final Button favoriteButton) {
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateFavoritesColumn(context, cursor);
                final Cursor newCursor = buildNewCursorFrom(context, cursor);
                swapCursor(newCursor);
                notifyDataSetChanged();
            }
        });
    }

    private void updateFavoritesColumn(Context context, Cursor cursor) {
//        Log.d(LOG_TAG, "updateFavoritesColumn");

        ContentValues movieValues = new ContentValues();

        if (isFavorite(cursor)) {
            movieValues.put(MovieEntry.COLUMN_FAVORITE, 0);
        } else {
            movieValues.put(MovieEntry.COLUMN_FAVORITE, 1);
        }

        int columnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID);
        final long movieId = cursor.getLong(columnIndex);
        final String where = MovieEntry.COLUMN_MOVIE_ID + " = ?";
        final String[] selectionArgs = {Long.toString(movieId)};
        context.getContentResolver().update(
                MovieEntry.CONTENT_URI, movieValues, where, selectionArgs);
    }

    private Cursor buildNewCursorFrom(final Context context, final Cursor oldCursor) {
        final int columnIndex = oldCursor.getColumnIndex(MovieEntry._ID);
        final long movieKey = oldCursor.getLong(columnIndex);

        // TODO: update selection args to take the movie id (after updating db)
        final Uri uri = MovieEntry.buildMovieUri(movieKey);
        final String[] selectionArgs = {String.valueOf(movieKey)};

        return new SingleMovieCursorBuilder(context).buildCursorForSingleMovie(uri, selectionArgs);
    }

    /* Used to speed up loading the views within the list view */
    public static class ViewHolder {

        ImageView posterView;
        TextView titleView;
        TextView overviewView;
        TextView userRatingView;
        TextView releaseDateView;
        TextView videoTextView;
        Button favoriteButton;

        public ViewHolder(View rootView) {
            posterView = (ImageView) rootView.findViewById(R.id.detail_image_view);
            titleView = (TextView) rootView.findViewById(R.id.title);
            overviewView = (TextView) rootView.findViewById(R.id.overview);
            userRatingView = (TextView) rootView.findViewById(R.id.rating);
            releaseDateView = (TextView) rootView.findViewById(R.id.release);
            videoTextView = (TextView) rootView.findViewById(R.id.video_text_view);
            favoriteButton = (Button) rootView.findViewById(R.id.button_favorite);
        }
    }
}
