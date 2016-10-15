package com.example.android.popularmoviesapp.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmoviesapp.BuildConfig;
import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Gets reviews with a http request.
 * Parses data as a JSON string on background thread and
 * publishes the result on the UI.
 */
public class FetchReviewsTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    private final Context context;
    private String reviewsJsonStr;

    private long movie_id;
    private long movieKey;

    public FetchReviewsTask(Context context, long movieKey, long movie_id) {
        this.context = context;
        this.movie_id = movie_id;
        this.movieKey = movieKey;
    }

    /* HTTP request on background thread. */
    @Override
    protected Void doInBackground(Void... params) {

        // Declared outside in order to be closed in finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // https://www.themoviedb.org/
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(String.valueOf(movie_id))
                    .appendPath("reviews")
                    .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

//            Log.d(LOG_TAG, "movieKey: " + movieKey);
//            Log.d(LOG_TAG, "reviews url: " + url);

            // create the request to TMDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // read the input stream into a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // stream was empty
                return null;
            }
            reviewsJsonStr = buffer.toString();
            parseAndPersistReviewsData(reviewsJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // no movie data found
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    private void parseAndPersistReviewsData(String reviewsJsonStr) throws JSONException {

        final String MD_RESULTS = "results";
        final String MD_ID = "id";
        final String MD_AUTHOR = "author";
        final String MD_CONTENT = "content";
        final String MD_URL = "url";

        final JSONObject data = new JSONObject(reviewsJsonStr);
        final JSONArray reviews = data.getJSONArray(MD_RESULTS);

        Vector<ContentValues> contentValuesVector = new Vector<>(reviews.length());

        for (int i = 0; i < reviews.length(); i++) {

            // get data from JSON String
            final JSONObject reviewsData = reviews.getJSONObject(i);
            final String reviewId = reviewsData.getString(MD_ID);
            final String author = reviewsData.getString(MD_AUTHOR);
            final String content = reviewsData.getString(MD_CONTENT);
            final String url = reviewsData.getString(MD_URL);

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, movieKey);
            reviewValues.put(ReviewEntry.COLUMN_REVIEW_ID, reviewId);
            reviewValues.put(ReviewEntry.COLUMN_AUTHOR, author);
            reviewValues.put(ReviewEntry.COLUMN_CONTENT, content);
            reviewValues.put(ReviewEntry.COLUMN_URL, url);

            contentValuesVector.add(reviewValues);

            insertOrUpdate(context, movieKey, reviewId, author, content, url);
        }
    }

    /**
     * @return review rowId upon insert and number of rows updated upon update
     */
    static long insertOrUpdate(Context context, long movieKey, String reviewId,
                               String author, String content, String url) {

        long reviewRowId = checkIfReviewIdExists(context, reviewId);

        if (reviewRowId == -1) {
            return insertReview(context, movieKey, reviewId, author, content, url);
        } else {
            return updateReview(context, reviewId, author, content, url);
        }
    }

    private static long checkIfReviewIdExists(Context context, String reviewId) {
        long reviewRowId = -1;

        final String[] projection = {ReviewEntry._ID};
        final String selection = ReviewEntry.COLUMN_REVIEW_ID + " = ?";
        final String[] selectionArgs = {reviewId};

        final Cursor reviewCursor = context.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null); // sortOrder

        assert reviewCursor != null;
        if (reviewCursor.moveToFirst()) {
            int reviewKeyIndex = reviewCursor.getColumnIndex(ReviewEntry._ID);
            reviewRowId = reviewCursor.getLong(reviewKeyIndex);
        }

        reviewCursor.close();
        return reviewRowId;
    }

    private static long insertReview(Context context, long movieKey, String reviewId,
                                     String author, String content, String url) {

        long reviewRowId;
        ContentValues reviewValues = new ContentValues();

        reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, movieKey);
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_ID, reviewId);
        reviewValues.put(ReviewEntry.COLUMN_AUTHOR, author);
        reviewValues.put(ReviewEntry.COLUMN_CONTENT, content);
        reviewValues.put(ReviewEntry.COLUMN_URL, url);

        final Uri insertedUri = context.getContentResolver().insert(
                ReviewEntry.CONTENT_URI, reviewValues);

        reviewRowId = ContentUris.parseId(insertedUri);
        return reviewRowId;
    }

    private static long updateReview(Context context, String reviewId, String author, String content, String url) {

        ContentValues reviewValues = new ContentValues();

        reviewValues.put(ReviewEntry.COLUMN_AUTHOR, author);
        reviewValues.put(ReviewEntry.COLUMN_CONTENT, content);
        reviewValues.put(ReviewEntry.COLUMN_URL, url);

        final String where = ReviewEntry.COLUMN_REVIEW_ID + " = ?";
        final String[] selectionArgs = {reviewId};
        return context.getContentResolver().update(
                ReviewEntry.CONTENT_URI, reviewValues, where, selectionArgs);
    }
}
