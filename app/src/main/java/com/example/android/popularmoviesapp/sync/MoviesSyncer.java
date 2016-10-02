package com.example.android.popularmoviesapp.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmoviesapp.BuildConfig;
import com.example.android.popularmoviesapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

/**
 * Created by David on 25/09/16.
 */
public class MoviesSyncer {
    private static final String LOG_TAG = MoviesSyncer.class.getSimpleName();

    public static void syncTopRatedMovies(Context context) {
        final String source = context.getString(R.string.source_top_rated);
        syncMovies(context, source);
    }

    public static void syncPopularMovies(Context context) {
        final String source = context.getString(R.string.source_popular);
        syncMovies(context, source);
    }

    /**
     * Gets movie data with a http request.
     * Parses data as a JSON string
     * and persists it.
     * publishes the result on the UI.
     */
    private static void syncMovies(Context context, String source) {

        // Declared outside in order to be closed in finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr;

        try {
            // https://www.themoviedb.org/
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(source)
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
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // new line is not necessary, but helps for debugging
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // stream was empty
            }
            movieJsonStr = buffer.toString();
//                Log.d(LOG_TAG, "movieJsonStr: " + movieJsonStr);
            parseAndPersistMovieData(context, movieJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // no movie data found
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
    }

    private static void parseAndPersistMovieData(Context context, String movieJsonStr)
            throws JSONException {

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
            insertOrUpdate(context, movieId, title, posterPath, overview, rating, popularity, release,
                    favorite);
        }
    }

    /**
     * @return movieId upon insert and number of rows updated upon update
     */
    static long insertOrUpdate(Context context,
                               long movieId, String title, String posterPath, String overview,
                               double rating, double popularity, String release, int favorite) {

        long movieRowId = checkIfMovieIdExists(context, movieId);

        if (movieRowId == -1) {
            return insertMovie(context, movieId, title, posterPath, overview, rating, popularity,
                    release, favorite);
        } else {
            return updateMovie(context, movieId, title, posterPath, overview, rating, popularity, release);
        }

    }

    private static long checkIfMovieIdExists(Context context, long movieId) {
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
            int movieIdIndex = movieCursor.getColumnIndex(MovieEntry._ID);
            movieRowId = movieCursor.getLong(movieIdIndex);
        }

        movieCursor.close();
        return movieRowId;
    }

    private static long insertMovie(Context context,
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
        // TODO: move to insertOrUpdate after db has been updated
        syncVideos(context, movieRowId, movieId);
        // TODO: sync reviews

        return movieRowId;
    }

    private static long updateMovie(Context context,
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

    // TODO: is there a better place for this? Does it make sense to put it in FRT?
    // TODO: after updating db, only need movieId
    private static void syncVideos(Context context, long movieKey, long movieId) {
        VideosSyncer.syncVideos(context, movieKey, movieId);
    }

}
