package com.example.android.popularmoviesapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;
import static com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;

/**
 * Created by David on 13/07/16.
 * Retrieves and modifies the data in the database.
 */
public class MovieProvider extends ContentProvider {
    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private MovieDbHelper movieDbHelper;

    private static final UriMatcher URI_MATCHER = MovieUriMatcher.buildUriMatcher();

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case MovieUriMatcher.MOVIES_CODE:
                return MovieEntry.CONTENT_TYPE;
            case MovieUriMatcher.REVIEWS_CODE:
                return ReviewEntry.CONTENT_TYPE;
            case MovieUriMatcher.VIDEOS_CODE:
                return VideoEntry.CONTENT_TYPE;
            case MovieUriMatcher.SINGLE_MOVIE_CODE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase readableDatabase = movieDbHelper.getReadableDatabase();

        Cursor cursor;
        switch (URI_MATCHER.match(uri)) {
            case MovieUriMatcher.MOVIES_CODE: {
                cursor = readableDatabase.query(MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
//                Log.d(LOG_TAG, "cursorDump: " + DatabaseUtils.dumpCursorToString(cursor));
                break;
            }
            case MovieUriMatcher.REVIEWS_CODE: {
                cursor = readableDatabase.query(ReviewEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case MovieUriMatcher.VIDEOS_CODE: {
                cursor = readableDatabase.query(VideoEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case MovieUriMatcher.SINGLE_MOVIE_CODE: {
                cursor = new SingleMovieCursorBuilder(getContext(), uri).build();
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
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final int match = URI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case MovieUriMatcher.MOVIES_CODE: {
                returnUri = insertMovieEntries(uri, values);
                break;
            }
            case MovieUriMatcher.REVIEWS_CODE: {
                returnUri = insertReviewEntries(uri, values);
                break;
            }
            case MovieUriMatcher.VIDEOS_CODE: {
                returnUri = insertVideoEntries(uri, values);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    private Uri insertMovieEntries(Uri uri, ContentValues values) {

        Uri returnUri;
        final long _id = movieDbHelper.getWritableDatabase().
                insert(MovieEntry.TABLE_NAME, null, values);
        if (_id > 0)
            returnUri = MovieEntry.buildMovieUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
        return returnUri;
    }

    private Uri insertReviewEntries(Uri uri, ContentValues values) {

        Uri returnUri;
        final long _id = movieDbHelper.getWritableDatabase().
                insert(ReviewEntry.TABLE_NAME, null, values);
        if (_id > 0)
            returnUri = ReviewEntry.buildReviewUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
        return returnUri;
    }

    private Uri insertVideoEntries(Uri uri, ContentValues values) {

        Uri returnUri;
        final long _id = movieDbHelper.getWritableDatabase().
                insert(VideoEntry.TABLE_NAME, null, values);
        if (_id > 0)
            returnUri = VideoEntry.buildVideoUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
        return returnUri;
    }

    @Override
    // delete returns the number of rows affected if a whereClause is passed in, 0
    // otherwise. To remove all rows and get a count pass "1" as the whereClause.
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase writableDatabase = movieDbHelper.getWritableDatabase();
        if (null == selection) selection = "1";

        final int match = URI_MATCHER.match(uri);
        int rowsDeleted;

        switch (match) {
            case MovieUriMatcher.MOVIES_CODE:
                rowsDeleted = writableDatabase.delete(
                        MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MovieUriMatcher.REVIEWS_CODE:
                rowsDeleted = writableDatabase.delete(
                        ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MovieUriMatcher.VIDEOS_CODE:
                rowsDeleted = writableDatabase.delete(
                        VideoEntry.TABLE_NAME, selection, selectionArgs);
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
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

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
            case MovieUriMatcher.VIDEOS_CODE:
                rowsUpdated = writableDatabase.update(VideoEntry.TABLE_NAME,
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
    public int bulkInsert(@NonNull Uri uri, ContentValues[] values) {

        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case MovieUriMatcher.MOVIES_CODE:
                return bulkInsertEntries(MovieEntry.TABLE_NAME, uri, values);
            case MovieUriMatcher.REVIEWS_CODE:
                return bulkInsertEntries(ReviewEntry.TABLE_NAME, uri, values);
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int bulkInsertEntries(String tableName, Uri uri, ContentValues[] values) {

        final SQLiteDatabase writableDatabase = movieDbHelper.getWritableDatabase();

        writableDatabase.beginTransaction();

        int rowsInserted = 0;
        try {
            for (ContentValues value : values) {
                if (value == null) {
                    throw new IllegalArgumentException("Cannot have null content values");
                }
                long _id = -1;
                try {
                    _id = writableDatabase.insertOrThrow(tableName, null, value);
                } catch (SQLiteConstraintException e) {
                    Log.w(LOG_TAG, "Value is already in database.");
                }
                if (_id != -1) {
                    rowsInserted++;
                }
            }
            if (rowsInserted > 0) {
                // If no errors, declare a successful transaction.
                // database will not populate if this is not called
                writableDatabase.setTransactionSuccessful();
            }
        } finally {
            writableDatabase.endTransaction();
        }
        if (rowsInserted > 0) {
            // if there was successful insertion, notify the content resolver that there
            // was a change
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsInserted;
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
