package com.example.android.popularmoviesapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.android.popularmoviesapp.data.MovieContract;

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
 * Gets movie data with a http request.
 * Parses data as a JSON string on background thread and
 * publishes the result on the UI.
 */
public class FetchMovieTask extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private ArrayAdapter<String> posterAdapter;
    private final Context context;
    private String movieJsonStr;
    private MovieInfoParser movieInfoParser;

    public FetchMovieTask(Context context, ArrayAdapter<String> posterAdapter) {
        this.context = context;
        this.posterAdapter = posterAdapter;
    }

    /**
     * HTTP request on background thread.
     *
     * @param params is either top_rated or popular
     * @return array of poster URLs
     */
    @Override
    protected String[] doInBackground(String... params) {

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

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // no movie data found
            return null;
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

        try {
            return persistMovies(movieJsonStr);
//            movieInfoParser = new MovieInfoParser(movieJsonStr);

            // log posterUrls
//                String[] posterUrls = movieInfoParser.getPosterUrls();
//                for (String posterUrl : posterUrls) {
//                    Log.d(LOG_TAG, "posterUrl: " + posterUrl);
//                }

//            return movieInfoParser.getPosterUrls();
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private String[] persistMovies(String movieJsonStr) throws JSONException {

        final String MD_RESULTS = "results";
        final String MD_ID = "id";
        final String MD_TITLE = "original_title";
        final String MD_POSTER_PATH = "poster_path";
        final String MD_OVERVIEW = "overview";
        final String MD_RATING = "vote_average";
        final String MD_RELEASE = "release_date";
        final String POSTER_URL_BASE = "http://image.tmdb.org/t/p/w185/";

        final JSONObject data = new JSONObject(movieJsonStr);
        final JSONArray movies = data.getJSONArray(MD_RESULTS);

        Vector<ContentValues> contentValuesVector = new Vector<>(movies.length());

        for (int i = 0; i < movies.length(); i++) {

            // get data from JSON String
            final JSONObject movieData = movies.getJSONObject(i);
            final long movieId = movieData.getLong(MD_ID);
            final String title = movieData.getString(MD_TITLE);
            final String posterPath = POSTER_URL_BASE + movieData.getString(MD_POSTER_PATH);
            final String overview = movieData.getString(MD_OVERVIEW);
            final double rating = movieData.getDouble(MD_RATING);
            final String release = movieData.getString(MD_RELEASE);
            final int favorite = 0; // default to false

            addMovie(movieId, title, posterPath, overview, rating, release, favorite);

            // TODO add reviews (later do it only when needed?)
            // TODO only poster_path and movie id is necessary for the main activity

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, release);
            movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favorite);

            contentValuesVector.add(movieValues);

        }

        // add to database
        if (contentValuesVector.size() > 0) {
//            ContentValues[] cvArray = new ContentValues[contentValuesVector.size()];
//            contentValuesVector.toArray(cvArray);
//            context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }

        return getPosterUrls(contentValuesVector);
    }

    private String[] getPosterUrls(Vector<ContentValues> contentValuesVector) {
        String[] posterUrls = new String[contentValuesVector.size()];
        for (int i = 0; i < contentValuesVector.size(); i++) {
            final ContentValues movieValues = contentValuesVector.elementAt(i);
            posterUrls[i] = movieValues.getAsString(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        }
        return posterUrls;
    }

    /**
     * Updates the UI after using AsyncTask.
     *
     * @param result returned from AsyncTask
     */
    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            posterAdapter.clear();
            for (String posterUrl : result) {
                posterAdapter.add(MovieInfoParser.POSTER_URL_BASE + posterUrl);
            }
        }
    }

    public long addMovie(long movieId, String title, String posterPath, String overview,
                         double rating, String release, int favorite) {
        long movieRowId;

        // check if movie_id already exists
        Cursor movieCursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI, // uri
                new String[]{MovieContract.MovieEntry._ID}, // projection
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", // selection
                new String[]{Long.toString(movieId)}, // selectionArgs
                null); // sortOrder

        if (movieCursor.moveToFirst()) {
            int locationIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            movieRowId = movieCursor.getLong(locationIdIndex);
        } else {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, release);
            movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favorite);

            // insert movieValues into db
            Uri insertedUri = context.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI, movieValues);

            // extract movieRowId from URI
            movieRowId = ContentUris.parseId(insertedUri);
        }

        movieCursor.close();
        return movieRowId;
    }
}
