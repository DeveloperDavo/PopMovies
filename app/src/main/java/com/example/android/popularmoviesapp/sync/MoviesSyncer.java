package com.example.android.popularmoviesapp.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesapp.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

/**
 * Created by David on 16/10/2016.
 */

public class MoviesSyncer extends Syncer {
    private static final String LOG_TAG = MoviesSyncer.class.getSimpleName();

    @NonNull
    @Override
    protected URL buildUrl() throws MalformedURLException {
        // https://www.themoviedb.org/
        final String BASE_URL = "http://api.themoviedb.org/3/movie";
        final String API_PARAM = "api_key";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(source)
                .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        return new URL(builtUri.toString());
    }

    @Override
    protected void parseAndPersist(JSONArray dataArray, int i) throws JSONException {
        final String MD_ID = "id";
        final String MD_TITLE = "original_title";
        final String MD_POSTER_PATH = "poster_path";
        final String MD_OVERVIEW = "overview";
        final String MD_RATING = "vote_average";
        final String MD_POPULARITY = "popularity";
        final String MD_RELEASE = "release_date";

        // get data from JSON String
        final JSONObject movieData = dataArray.getJSONObject(i);
        final long movieId = movieData.getLong(MD_ID);
        final String title = movieData.getString(MD_TITLE);
        final String posterPath = movieData.getString(MD_POSTER_PATH);
        final String overview = movieData.getString(MD_OVERVIEW);
        final double rating = movieData.getDouble(MD_RATING);
        final double popularity = movieData.getDouble(MD_POPULARITY);
        final String release = movieData.getString(MD_RELEASE);
        final int favorite = 0; // default to false

//        final Bitmap bitmap = PosterSyncer.sync(posterPath);
//        final byte[] posterBlob = Utility.convertBitmapIntoBytes(bitmap);
        final String posterUrl = "http://image.tmdb.org/t/p/w185/" + posterPath;

        insertOrUpdate(movieId, title, posterUrl, overview, rating, popularity, release,
                favorite);
    }

    /**
     * @return movieId upon insert and number of rows updated upon update
     */
    long insertOrUpdate(long movieId, String title, String posterUrl, String overview,
                               double rating, double popularity, String release, int favorite) {

        if (isMovieInDb(movieId)) {
            return updateMovie(movieId, rating, popularity);
        } else {
            return insertMovie(movieId, title, posterUrl, overview, rating, popularity,
                    release, favorite);
        }

    }

    boolean isMovieInDb(long movieId) {
        long movieRowId = -1;

        final String[] projection = {MovieEntry._ID};
        final String selection = MovieEntry.COLUMN_MOVIE_ID + " = ?";
        final String[] selectionArgs = {Long.toString(movieId)};

        final Cursor movieCursor = contentResolver.query(
                MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null); // sortOrder

        assert movieCursor != null;
        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieEntry._ID);
            movieRowId = movieCursor.getLong(movieIdIndex);
        }

        movieCursor.close();

        return movieRowId != -1;
    }

    private long updateMovie(long movieId, double rating, double popularity) {

        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieEntry.COLUMN_RATING, rating);
        movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);

        final String where = MovieEntry.COLUMN_MOVIE_ID + " = ?";
        final String[] selectionArgs = {Long.toString(movieId)};
        final Uri contentUri = MovieEntry.CONTENT_URI;
        return contentResolver.update(
                contentUri, movieValues, where, selectionArgs);
    }

    private long insertMovie(long movieId, String title, String posterUrl, String overview,
                                    double rating, double popularity, String release, int favorite) {

        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
        movieValues.put(MovieEntry.COLUMN_TITLE, title);
        movieValues.put(MovieEntry.COLUMN_POSTER, posterUrl);
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
        movieValues.put(MovieEntry.COLUMN_RATING, rating);
        movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
        movieValues.put(MovieEntry.COLUMN_RELEASE, release);
        movieValues.put(MovieEntry.COLUMN_FAVORITE, favorite);

        final Uri insertedUri = contentResolver.insert(
                MovieEntry.CONTENT_URI, movieValues);

        return ContentUris.parseId(insertedUri);
    }
}
