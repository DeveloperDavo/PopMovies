package com.example.android.popularmoviesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;
import com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

/**
 * Created by David on 22/08/16.
 */
public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();

    private static final String SORT_ORDER_PREFERENCE = "sort_order";
    private static final String SELECTION_PREFERENCE = "selection";
    private static final String SELECTION_ARG_PREFERENCE = "selection_arg";
    private static final String POSITION_PREFERENCE = "position";

    public static final String SORT_BY_POPULARITY_DESC = MovieEntry.COLUMN_POPULARITY + " DESC";
    public static final String SORT_BY_RATING_DESC = MovieEntry.COLUMN_RATING + " DESC";

    public static void setSelection(Context context, String selection) {
        setPreferenceText(context, selection, R.string.selection_key, SELECTION_PREFERENCE);
    }

    public static String getSelection(Context context) {
        return getPreferenceText(context, SELECTION_PREFERENCE, null, R.string.selection_key);
    }

    public static void setSelectionArg(Context context, String selectionArg) {
        setPreferenceText(context, selectionArg, R.string.selection_arg_key,
                SELECTION_ARG_PREFERENCE);
    }

    public static String getSelectionArg(Context context) {
        return getPreferenceText(context, SELECTION_ARG_PREFERENCE, null,
                R.string.selection_arg_key);
    }

    public static void setSortOrder(Context context, String sortOrder) {
        setPreferenceText(context, sortOrder, R.string.sort_order_key, SORT_ORDER_PREFERENCE);
    }

    public static String getSortOrder(Context context) {
        return getPreferenceText(context, SORT_ORDER_PREFERENCE,
                SORT_BY_POPULARITY_DESC, R.string.sort_order_key);
    }

    public static void setPosition(Context context, int position) {
        setPreferenceText(context, Integer.toString(position),
                R.string.position_key, POSITION_PREFERENCE);
    }

    public static String getPosition(Context context) {
        return getPreferenceText(context, POSITION_PREFERENCE,
                Integer.toString(-1), R.string.position_key);
    }

    static String formatRating(Context context, double ratingFromDb) {
        int formatId = R.string.format_user_rating;
        return String.format(context.getString(formatId), ratingFromDb);
    }

    static String getPosterPathFrom(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_POSTER);
        return cursor.getString(columnIndex);
    }

    static String getVideoKeyFrom(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(VideoEntry.COLUMN_VIDEO_KEY);
        return cursor.getString(columnIndex);
    }

    public static Cursor querySingleMovieUri(Context context, long movieRowId) {
        final Uri uri = MovieEntry.buildMovieUri(movieRowId);
        final String[] projection = null;
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = null;

        return context.getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @NonNull
    static Cursor getSingleMovieCursorAndMoveToPosition(Context context, int position, long movieRowId) {
        final Cursor cursor = Utility.querySingleMovieUri(context, movieRowId);
        cursor.moveToPosition(position);
        return cursor;
    }

    static boolean isVideosView(Cursor cursor) {
        return VideoEntry.COLUMN_VIDEO_ID.equals(getColumnNameOf12thColumn(cursor));
    }

    static boolean isReviewsView(Cursor cursor) {
        return ReviewEntry.COLUMN_REVIEW_ID.equals(getColumnNameOf12thColumn(cursor));
    }

    /* Helper methods */

    private static void setPreferenceText(
            Context context, String text, int key, String preference) {
        SharedPreferences settings = context.getSharedPreferences(
                preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(context.getString(key), text);
        editor.commit();
    }

    private static String getPreferenceText(
            Context context, String preference, String defValue, int key) {
        SharedPreferences settings = context.getSharedPreferences(
                preference, Context.MODE_PRIVATE);
        return settings.getString(context.getString(key), defValue);
    }

    // The 12th column is the first column that differs between reviews and videos
    private static String getColumnNameOf12thColumn(Cursor cursor) {
        final int columnIndex = 11;
        return cursor.getColumnName(columnIndex);
    }



}
