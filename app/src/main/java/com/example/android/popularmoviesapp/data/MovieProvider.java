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

    private static final SQLiteQueryBuilder REVIEWS_BY_MOVIE_QUERY_BUILDER;

    static {
        REVIEWS_BY_MOVIE_QUERY_BUILDER = new SQLiteQueryBuilder();

        // reviews INNER JOIN movies ON reviews.movie_id = movies.movie_id
        // TODO: check SQL logic
        REVIEWS_BY_MOVIE_QUERY_BUILDER.setTables(
                MovieContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    // movies.movie_id = ?
    private static final String MOVIE_SELECTION =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";

    // reviews.review_id = ?
    private static final String REVIEW_SELECTION =
            MovieContract.ReviewEntry.TABLE_NAME + "." +
                    MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ?";

    // movies.movie_id = ? AND reviews.review_id = ?
    private static final String MOVIE_WITH_REVIEW_SELECTION =
            MovieContract.ReviewEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? AND " +
                    MovieContract.ReviewEntry.TABLE_NAME + "." +
                    MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ?";

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // determine kind of request and query database
        Cursor retCursor;
        switch (URI_MATCHER.match(uri)) {
            case MovieUriMatcher.MOVIES: {
                retCursor = null;
                break;
            }
            case MovieUriMatcher.REVIEWS: {
                retCursor = null;
                break;
            }
            case MovieUriMatcher.MOVIE_WITH_REVIEWS: {
                retCursor = getReviewsByMovieId(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getReviewsByMovieId(Uri uri, String[] projection, String sortOrder) {
        final int movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return REVIEWS_BY_MOVIE_QUERY_BUILDER.query(movieDbHelper.getReadableDatabase(),
                projection,
                MOVIE_WITH_REVIEW_SELECTION,
                new String[]{Integer.toString(movieId)},
                null,
                null,
                sortOrder
        );
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
            case MovieUriMatcher.MOVIE_WITH_REVIEWS:
                // TODO
                return null;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
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
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
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
}



