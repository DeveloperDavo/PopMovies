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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // determine kind of request and query database
        Cursor retCursor;
        switch (URI_MATCHER.match(uri)) {
            case MovieUriMatcher.MOVIE: {
                // TODO
                retCursor = null;
                break;
            }
            case MovieUriMatcher.REVIEW: {
                // TODO
                retCursor = null;
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // use Uri Matcher to determine kind of URI
        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case MovieUriMatcher.MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MovieUriMatcher.REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case MovieUriMatcher.MOVIE_WITH_REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
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
            case MovieUriMatcher.MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MovieUriMatcher.REVIEW: {
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
            case MovieUriMatcher.MOVIE:
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

    private Cursor getCursorForMovies(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return movieDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getCursorForReviews(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return movieDbHelper.getReadableDatabase().query(MovieContract.ReviewEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getCursorForMovieWithReviews(Uri uri, String[] projection, String sortOrder) {
        final int movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);
        final int reviewId = MovieContract.MovieEntry.getReviewIdFromUri(uri);

        return REVIEWS_BY_MOVIE_QUERY_BUILDER.query(movieDbHelper.getReadableDatabase(),
                projection,
                MOVIE_WITH_REVIEW_SELECTION,
                new String[]{Integer.toString(movieId), Integer.toString(reviewId)},
                null,
                null,
                sortOrder
        );
    }

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

    // movies.movie_id = ? AND reviews.review_id = ?
    private static final String MOVIE_WITH_REVIEW_SELECTION =
            MovieContract.ReviewEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? AND " +
                    MovieContract.ReviewEntry.TABLE_NAME + "." +
                    MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ?";
}



