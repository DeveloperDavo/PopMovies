package com.example.android.popularmoviesapp.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by David on 15/10/2016.
 */

public class ReviewSyncer extends AbstractSyncer {
    private static final String LOG_TAG = ReviewSyncer.class.getSimpleName();

    public ReviewSyncer(Context context, long movieKey, long movieId, String path) {
        super(context, movieKey, movieId, path);
    }

    @Override
    protected void sync() {
        super.sync();
    }

    @Override
    protected void parseAndPersistData(String jsonStr) throws JSONException {
        super.parseAndPersistData(jsonStr);
    }

    @NonNull
    @Override
    protected BufferedReader persistDataFromServer(HttpURLConnection urlConnection) throws IOException, JSONException {
        return super.persistDataFromServer(urlConnection);
    }

    @Override
    protected void disconnectAndClose(HttpURLConnection urlConnection, BufferedReader reader) {
        super.disconnectAndClose(urlConnection, reader);
    }

    @NonNull
    @Override
    protected URL buildUrl() throws MalformedURLException {
        return super.buildUrl();
    }

    @NonNull
    @Override
    protected HttpURLConnection connect(URL url) throws IOException {
        return super.connect(url);
    }

    @Override
    protected void parseAndPersist(JSONArray reviews, int i) throws JSONException {

        final String MD_ID = "id";
        final String MD_AUTHOR = "author";
        final String MD_CONTENT = "content";
        final String MD_URL = "url";

        // get data from JSON String
        final JSONObject reviewsData = reviews.getJSONObject(i);
        final String reviewId = reviewsData.getString(MD_ID);
        final String author = reviewsData.getString(MD_AUTHOR);
        final String content = reviewsData.getString(MD_CONTENT);
        final String url = reviewsData.getString(MD_URL);

        insertOrUpdate(reviewId, author, content, url);
    }

    private long insertOrUpdate(String reviewId, String author, String content, String url) {

        final Cursor reviewCursor = queryReviewId(reviewId);
        long _id = getRowIdFrom(reviewCursor);

        if (-1 == _id) {
            return insert(reviewId, author, content, url);
        } else {
            return update(_id, reviewId, author, content, url);
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
        long _id = -1;
        if (reviewCursor.moveToFirst()) {
            int videoKeyIndex = reviewCursor.getColumnIndex(ReviewEntry._ID);
            _id = reviewCursor.getLong(videoKeyIndex);
        }
        return _id;
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
    int update(long _id, String reviewId, String author, String content, String url) {

        final ContentValues reviewValues = getContentValuesFrom(reviewId, author, content, url);

        final String where = ReviewEntry._ID + " = ?";
        final String[] selectionArgs = {Long.toString(_id)};
        return context.getContentResolver().update(
                ReviewEntry.CONTENT_URI, reviewValues, where, selectionArgs);
    }

    @NonNull
    private ContentValues getContentValuesFrom(
            String reviewId, String author, String content, String url) {

        ContentValues videoValues = new ContentValues();

        videoValues.put(ReviewEntry.COLUMN_MOVIE_KEY, movieKey);
        videoValues.put(ReviewEntry.COLUMN_REVIEW_ID, reviewId);
        videoValues.put(ReviewEntry.COLUMN_AUTHOR, author);
        videoValues.put(ReviewEntry.COLUMN_CONTENT, content);
        videoValues.put(ReviewEntry.COLUMN_URL, url);

        return videoValues;
    }

}
