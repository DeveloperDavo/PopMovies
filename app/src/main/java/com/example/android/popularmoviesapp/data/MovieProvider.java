package com.example.android.popularmoviesapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by David on 13/07/16.
 */
public class MovieProvider extends ContentProvider {

    private MovieDbHelper movieDbHelper;

    private static final UriMatcher URI_MATCHER = MovieUriMatcher.buildUriMatcher();

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // use Uri Matcher to determine kind of URI
        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case MovieUriMatcher.MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MovieUriMatcher.REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case MovieUriMatcher.MOVIE:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // determine kind of request and query database
        Cursor cursor;
        switch (URI_MATCHER.match(uri)) {
            case MovieUriMatcher.MOVIES: {
                cursor = getCursorForMovies(projection, selection, selectionArgs, sortOrder);
                break;
            }
            case MovieUriMatcher.REVIEWS: {
                cursor = getCursorForReviews(projection, selection, selectionArgs, sortOrder);
                break;
            }
            case MovieUriMatcher.MOVIE: {
                cursor = getCursorForSingleMovie(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case MovieUriMatcher.MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MovieUriMatcher.REVIEWS: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MovieUriMatcher.MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MovieUriMatcher.REVIEWS:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsUpdated;

        switch (match) {
            case MovieUriMatcher.MOVIES:
                rowsUpdated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MovieUriMatcher.REVIEWS:
                rowsUpdated = db.update(
                        MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case MovieUriMatcher.MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            // TODO reviews
            // TODO videos
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        movieDbHelper.close();
        super.shutdown();
    }

    private Cursor getCursorForMovies(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return movieDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getCursorForReviews(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return movieDbHelper.getReadableDatabase().query(MovieContract.ReviewEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getCursorForSingleMovie(Uri uri, String[] projection, String sortOrder) {
        final int movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return REVIEWS_BY_MOVIE_QUERY_BUILDER.query(movieDbHelper.getReadableDatabase(),
                projection,
                SINGLE_MOVIE_SELECTION,
                new String[]{Integer.toString(movieId)},
                null,
                null,
                sortOrder
        );
    }

    private static final SQLiteQueryBuilder REVIEWS_BY_MOVIE_QUERY_BUILDER;

    static {
        REVIEWS_BY_MOVIE_QUERY_BUILDER = new SQLiteQueryBuilder();

        // reviews INNER JOIN movies ON reviews.movie_key = movies._id
        REVIEWS_BY_MOVIE_QUERY_BUILDER.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.ReviewEntry.TABLE_NAME +
                        " ON " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID);
    }

    // movies.movie_id = ?
    private static final String SINGLE_MOVIE_SELECTION =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";

    // movies.movie_id = ? AND reviews.review_id = ?
    private static final String SINGLE_REVIEW_SELECTION =
            MovieContract.ReviewEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? AND " +
                    MovieContract.ReviewEntry.TABLE_NAME + "." +
                    MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ?";
}



