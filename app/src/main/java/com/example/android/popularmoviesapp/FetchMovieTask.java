package com.example.android.popularmoviesapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;
import com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

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

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

/**
 * Gets movie data with a http request.
 * Parses data as a JSON string on background thread and
 * publishes the result on the UI.
 */
// TODO: delete class and test
public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final Context context;
    private String movieJsonStr;

    public FetchMovieTask(Context context) {
        this.context = context;
    }

    /**
     * HTTP request on background thread.
     *
     * @param params is either top_rated or popular
     * @return nothing
     */
    @Override
    protected Void doInBackground(String... params) {

        // if there is no preference, there is nothing to look up
        if (params.length == 0) {
            return null;
        }

        // Declared outside in order to be closed in finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // https://www.themoviedb.org/
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, "url: " + url);

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
            movieJsonStr = buffer.toString();
//                Log.d(LOG_TAG, "movieJsonStr: " + movieJsonStr);
            parseAndPersistMovieData(movieJsonStr);

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

    private void parseAndPersistMovieData(String movieJsonStr) throws JSONException {

        final String MD_RESULTS = "results";
        final String MD_ID = "id";
        final String MD_TITLE = "original_title";
        final String MD_POSTER_PATH = "poster_path";
        final String MD_OVERVIEW = "overview";
        final String MD_RATING = "vote_average";
        final String MD_POPULARITY = "popularity";
        final String MD_RELEASE = "release_date";
        final String POSTER_URL_BASE = "http://image.tmdb.org/t/p/w185/";

        final JSONObject data = new JSONObject(movieJsonStr);
        final JSONArray movies = data.getJSONArray(MD_RESULTS);

//        Vector<ContentValues> contentValuesVector = new Vector<>(movies.length());

        // TODO
        deleteOldReviewData();
        deleteOldVideoData();

        for (int i = 0; i < movies.length(); i++) {

            // get data from JSON String
            final JSONObject movieData = movies.getJSONObject(i);
            final long movieId = movieData.getLong(MD_ID);
            final String title = movieData.getString(MD_TITLE);
            final String posterPath = POSTER_URL_BASE + movieData.getString(MD_POSTER_PATH);
            final String overview = movieData.getString(MD_OVERVIEW);
            final double rating = movieData.getDouble(MD_RATING);
            final double popularity = movieData.getDouble(MD_POPULARITY);
            final String release = movieData.getString(MD_RELEASE);
            final int favorite = 0; // default to false

            // TODO only poster_path and movie id is necessary for the main activity
            insertOrUpdate(movieId, title, posterPath, overview, rating, popularity, release,
                    favorite);

//            ContentValues movieValues = new ContentValues();
//
//            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
//            movieValues.put(MovieEntry.COLUMN_TITLE, title);
//            movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
//            movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
//            movieValues.put(MovieEntry.COLUMN_RATING, rating);
//            movieValues.put(MovieEntry.COLUMN_RELEASE, release);
//            movieValues.put(MovieEntry.COLUMN_FAVORITE, favorite);
//
//            contentValuesVector.add(movieValues);

        }

        // bulk insert into database
        // TODO not to be used so often in final version
//        bulkInsert(contentValuesVector);
//        final Cursor cursor = context.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);
//        Log.d(LOG_TAG, "movie query: " + DatabaseUtils.dumpCursorToString(cursor));
    }

    private void bulkInsert(Vector<ContentValues> contentValuesVector) {
        if (contentValuesVector.size() > 0) {
            Uri uri = MovieEntry.CONTENT_URI;
            ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(contentValuesArray);
//            int inserted = context.getContentResolver().bulkInsert(uri, contentValuesArray);
//            Log.d(LOG_TAG, "Bulk insert complete. " + inserted + " inserted");
            for (ContentValues contentvalues : contentValuesArray) {
                final Uri movieInsertUri = context.getContentResolver().insert(uri, contentvalues);
//                fetchReviews(movieInsertUri);
//                fetchVideos(movieInsertUri);
            }
        }
    }

    // TODO: is there a better place for this? Does it make sense to put it in FRT?
    // TODO: duplicate code
    private void fetchReviews(long movieKey) {

        final Uri movieUri = MovieEntry.CONTENT_URI;
        final String[] columns = new String[]{MovieEntry.COLUMN_MOVIE_ID};
        // TODO: remove magic number
        final int colMovieId = 0;
        final String selection = MovieEntry.TABLE_NAME + "." + MovieEntry._ID + " = ?";
        final String[] selectionArgs = new String[]{Long.toString(movieKey)};
        final String sortOrder = null;

        final Cursor cursor = context.getContentResolver().query(
                movieUri, columns, selection, selectionArgs, sortOrder
        );
//                Log.d(LOG_TAG, "movie query: " + DatabaseUtils.dumpCursorToString(cursor));
        cursor.moveToFirst();

        long movieId = cursor.getLong(colMovieId);

//        Log.d(LOG_TAG, "fetching reviews");
        (new FetchReviewsTask(context, movieKey, movieId)).execute();
    }

    // TODO: is there a better place for this? Does it make sense to put it in FRT?
    // TODO: duplicate code
    private void fetchVideos(long movieKey) {

        final Uri movieUri = MovieEntry.CONTENT_URI;
        final String[] columns = new String[]{MovieEntry.COLUMN_MOVIE_ID};
        // TODO: remove magic number
        final int colMovieId = 0;
        final String selection = MovieEntry.TABLE_NAME + "." + MovieEntry._ID + " = ?";
        final String[] selectionArgs = new String[]{Long.toString(movieKey)};
        final String sortOrder = null;

        final Cursor cursor = context.getContentResolver().query(
                movieUri, columns, selection, selectionArgs, sortOrder
        );
//                Log.d(LOG_TAG, "movie query: " + DatabaseUtils.dumpCursorToString(cursor));
        cursor.moveToFirst();

        long movieId = cursor.getLong(colMovieId);

//        Log.d(LOG_TAG, "fetching videos");
        (new FetchVideosTask(context, movieKey, movieId)).execute();
    }

    // TODO
    private void deleteOldReviewData() {

        int deleted = context.getContentResolver().delete(
                ReviewEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, deleted + "reviews deleted");

    }

    // TODO
    private void deleteOldVideoData() {

        int deleted = context.getContentResolver().delete(
                VideoEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, deleted + "videos deleted");

    }

    /**
     * @return movieId upon insert and number of rows updated upon update
     */
    long insertOrUpdate(long movieId, String title, String posterPath, String overview,
                               double rating, double popularity, String release, int favorite) {

        long movieRowId = checkIfMovieIdExists(movieId);

        if (movieRowId == -1) {
            return insertMovie(movieId, title, posterPath, overview, rating, popularity,
                    release, favorite);
        } else {
            return updateMovie(movieId, title, posterPath, overview, rating, popularity, release);
        }

    }

    private long checkIfMovieIdExists(long movieId) {
        long movieRowId = -1;

        final String[] projection = {MovieEntry._ID};
        final String selection = MovieEntry.COLUMN_MOVIE_ID + " = ?";
        final String[] selectionArgs = {Long.toString(movieId)};

        final Cursor movieCursor = context.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null); // sortOrder

        if (movieCursor.moveToFirst()) {
            int locationIdIndex = movieCursor.getColumnIndex(MovieEntry._ID);
            movieRowId = movieCursor.getLong(locationIdIndex);
        }

        movieCursor.close();
        return movieRowId;
    }

    // TODO movieRowId is not used
    private long insertMovie(
            long movieId, String title, String posterPath, String overview,
            double rating, double popularity, String release, int favorite) {

        long movieRowId;
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
        movieValues.put(MovieEntry.COLUMN_TITLE, title);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
        movieValues.put(MovieEntry.COLUMN_RATING, rating);
        movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
        movieValues.put(MovieEntry.COLUMN_RELEASE, release);
        movieValues.put(MovieEntry.COLUMN_FAVORITE, favorite);

        final Uri insertedUri = context.getContentResolver().insert(
                MovieEntry.CONTENT_URI, movieValues);

        // extract movieRowId from URI
        movieRowId = ContentUris.parseId(insertedUri);
        return movieRowId;
    }

    private long updateMovie(
            long movieId, String title, String posterPath, String overview,
            double rating, double popularity, String release) {

        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieEntry.COLUMN_TITLE, title);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
        movieValues.put(MovieEntry.COLUMN_RATING, rating);
        movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
        movieValues.put(MovieEntry.COLUMN_RELEASE, release);

        final String where = MovieEntry.COLUMN_MOVIE_ID + " = ?";
        final String[] selectionArgs = {Long.toString(movieId)};
        return context.getContentResolver().update(
                MovieEntry.CONTENT_URI, movieValues, where, selectionArgs);
    }
}
