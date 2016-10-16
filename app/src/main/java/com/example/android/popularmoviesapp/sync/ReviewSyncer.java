package com.example.android.popularmoviesapp.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by David on 15/10/2016.
 */

public class ReviewSyncer extends AbstractSyncer {
    private static final String LOG_TAG = ReviewSyncer.class.getSimpleName();

    @Override
    protected void parseAndPersist(JSONArray dataArray, int i) throws JSONException {

        final String MD_ID = "id";
        final String MD_AUTHOR = "author";
        final String MD_CONTENT = "content";
        final String MD_URL = "url";

        // get data from JSON String
        final JSONObject reviewsData = dataArray.getJSONObject(i);
        final String reviewId = reviewsData.getString(MD_ID);
        final String author = reviewsData.getString(MD_AUTHOR);
        final String content = reviewsData.getString(MD_CONTENT);
        final String url = reviewsData.getString(MD_URL);

        insertOrUpdate(reviewId, author, content, url);
    }

    private long insertOrUpdate(String reviewId, String author, String content, String url) {

        final Cursor reviewCursor = queryReviewId(reviewId);
        long rowId = getRowIdFrom(reviewCursor);

        if (-1 == rowId) {
            return insert(reviewId, author, content, url);
        } else {
            return update(rowId, reviewId, author, content, url);
        }
    }

    Cursor queryReviewId(String videoId) {

        final String[] projection = null;
        final String selection = ReviewEntry.COLUMN_REVIEW_ID + " = ?";
        final String[] selectionArgs = {videoId};

        return context.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null); // sortOrder
    }

    long getRowIdFrom(Cursor reviewCursor) {
        long rowId = -1;
        if (reviewCursor.moveToFirst()) {
            int videoKeyIndex = reviewCursor.getColumnIndex(ReviewEntry._ID);
            rowId = reviewCursor.getLong(videoKeyIndex);
        }
        return rowId;
    }

    /**
     * @return _ID of video being inserted
     */
    long insert(String reviewId, String author, String content, String url) {

        final ContentValues reviewValues = getContentValuesFrom(reviewId, author, content, url);

        final Uri insertedUri = context.getContentResolver().insert(
                ReviewEntry.CONTENT_URI, reviewValues);

        return ContentUris.parseId(insertedUri);
    }

    /**
     * @return number of videos updated
     */
    int update(long rowId, String reviewId, String author, String content, String url) {

        final ContentValues reviewValues = getContentValuesFrom(reviewId, author, content, url);

        final String where = ReviewEntry._ID + " = ?";
        final String[] selectionArgs = {Long.toString(rowId)};
        return context.getContentResolver().update(
                ReviewEntry.CONTENT_URI, reviewValues, where, selectionArgs);
    }

    @NonNull
    private ContentValues getContentValuesFrom(
            String reviewId, String author, String content, String url) {

        ContentValues videoValues = new ContentValues();

        videoValues.put(ReviewEntry.COLUMN_MOVIE_ROW_ID, movieRowId);
        videoValues.put(ReviewEntry.COLUMN_REVIEW_ID, reviewId);
        videoValues.put(ReviewEntry.COLUMN_AUTHOR, author);
        videoValues.put(ReviewEntry.COLUMN_CONTENT, content);
        videoValues.put(ReviewEntry.COLUMN_URL, url);

        return videoValues;
    }

}
