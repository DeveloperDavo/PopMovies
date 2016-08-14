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

import static com.example.android.popularmoviesapp.data.MovieContract.*;

/**
 * Created by David on 13/07/16.
 * Retrieves and modifies the data in the database.
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

        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case MovieUriMatcher.MOVIES_CODE:
                return MovieEntry.CONTENT_TYPE;
            case MovieUriMatcher.REVIEWS_CODE:
                return ReviewEntry.CONTENT_TYPE;
            case MovieUriMatcher.SINGLE_MOVIE_CODE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase readableDatabase = movieDbHelper.getReadableDatabase();

        Cursor cursor;
        switch (URI_MATCHER.match(uri)) {
            case MovieUriMatcher.MOVIES_CODE: {
                cursor = readableDatabase.query(MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case MovieUriMatcher.REVIEWS_CODE: {
                cursor = readableDatabase.query(ReviewEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case MovieUriMatcher.SINGLE_MOVIE_CODE: {
                cursor = getCursorForSingleMovie(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // register to watch a content URI for changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int match = URI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case MovieUriMatcher.MOVIES_CODE: {
                returnUri = getMoviesInsertUri(uri, values);
                break;
            }
            case MovieUriMatcher.REVIEWS_CODE: {
                returnUri = getReviewsInsertUri(uri, values);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    private Uri getMoviesInsertUri(Uri uri, ContentValues values) {

        Uri returnUri;
        final long _id = movieDbHelper.getWritableDatabase().
                insert(MovieEntry.TABLE_NAME, null, values);
        if (_id > 0)
            returnUri = MovieEntry.buildMovieUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
        return returnUri;
    }

    private Uri getReviewsInsertUri(Uri uri, ContentValues values) {

        Uri returnUri;
        final long _id = movieDbHelper.getWritableDatabase().
                insert(ReviewEntry.TABLE_NAME, null, values);
        if (_id > 0)
            returnUri = ReviewEntry.buildReviewUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
        return returnUri;
    }

    @Override
    // delete returns the number of rows affected if a whereClause is passed in, 0
    // otherwise. To remove all rows and get a count pass "1" as the whereClause.
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase writableDatabase = movieDbHelper.getWritableDatabase();
        if (null == selection) selection = "1";

        final int match = URI_MATCHER.match(uri);
        int rowsDeleted;

        switch (match) {
            case MovieUriMatcher.MOVIES_CODE:
                rowsDeleted = writableDatabase.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MovieUriMatcher.REVIEWS_CODE:
                rowsDeleted = writableDatabase.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            // sync changes to the network
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase writableDatabase = movieDbHelper.getWritableDatabase();

        final int match = URI_MATCHER.match(uri);
        int rowsUpdated;

        switch (match) {
            case MovieUriMatcher.MOVIES_CODE:
                rowsUpdated = writableDatabase.update(MovieEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case MovieUriMatcher.REVIEWS_CODE:
                rowsUpdated = writableDatabase.update(ReviewEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            // sync changes to the network
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case MovieUriMatcher.MOVIES_CODE:
                return bulkInsertEntries(MovieEntry.TABLE_NAME, uri, values);
            // TODO reviews
            // TODO videos
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int bulkInsertEntries(String tableName, Uri uri, ContentValues[] values) {

        final SQLiteDatabase writableDatabase = movieDbHelper.getWritableDatabase();

        writableDatabase.beginTransaction();

        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = writableDatabase.insert(tableName, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
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

    private Cursor getCursorForSingleMovie(Uri uri, String[] projection, String sortOrder) {

        final SQLiteDatabase readableDatabase = movieDbHelper.getReadableDatabase();
        final int movieId = MovieEntry.getMovieIdFromUri(uri);

        return REVIEWS_BY_MOVIE_QUERY_BUILDER.query(readableDatabase,
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
                MovieEntry.TABLE_NAME + " INNER JOIN " +
                        ReviewEntry.TABLE_NAME +
                        " ON " + ReviewEntry.TABLE_NAME +
                        "." + ReviewEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID);
    }

    // movies.movie_id = ?
    private static final String SINGLE_MOVIE_SELECTION =
            MovieEntry.TABLE_NAME + "." +
                    MovieEntry.COLUMN_MOVIE_ID + " = ?";

    // movies.movie_id = ? AND reviews.review_id = ?
    // TODO not in use yet
    private static final String SINGLE_REVIEW_SELECTION =
            ReviewEntry.TABLE_NAME + "." +
                    MovieEntry.COLUMN_MOVIE_ID + " = ? AND " +
                    ReviewEntry.TABLE_NAME + "." +
                    ReviewEntry.COLUMN_REVIEW_ID + " = ?";
}



