package com.example.android.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

/**
 * Created by David on 16/10/2016.
 */

public class TestSingleMovieCursorBuilder extends AndroidTestCase {
    private SQLiteDatabase readableDatabase;

    @Override
    protected void setUp() throws Exception {
        final MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        readableDatabase = movieDbHelper.getReadableDatabase();
        super.setUp();
    }

    public void test_buildMovieCursor() throws Exception {

        // GIVEN
        final ContentValues movieValues = TestUtilities.createMovieValues();
        final long movieRowId = TestUtilities.insertMovieValues(mContext, movieValues);
        final Uri uri = MovieEntry.buildMovieUri(movieRowId);

        ContentValues movieNotToFindValues = TestUtilities.createMovieValues();
        movieNotToFindValues.remove(MovieEntry.COLUMN_MOVIE_ID);
        movieNotToFindValues.put(MovieEntry.COLUMN_MOVIE_ID, "2389");
        TestUtilities.insertMovieValues(mContext, movieNotToFindValues);

        // WHEN
        final Cursor cursor = new SingleMovieCursorBuilder(mContext, uri)
                .buildMovieCursor(readableDatabase);

        // THEN
        TestUtilities.validateCursor("Error", cursor, movieValues);

    }

    public void test_buildVideosCursor() throws Exception {

        // GIVEN
        ContentValues testValues = TestUtilities.createMovieValues();
        final long movieRowId = TestUtilities.insertMovieValues(mContext, testValues);
        final Uri uri = MovieEntry.buildMovieUri(movieRowId);

        final ContentValues videoValues = TestUtilities.createVideoValues(movieRowId);
        TestUtilities.insertVideoValues(mContext, videoValues);
        testValues.putAll(videoValues);

        // WHEN
        final Cursor cursor = new SingleMovieCursorBuilder(mContext, uri)
                .buildVideosCursor(readableDatabase);

        // THEN
        TestUtilities.validateCursor("Error", cursor, testValues);
    }

    public void test_buildReviewsCursor() throws Exception {

        // GIVEN
        ContentValues testValues = TestUtilities.createMovieValues();
        final long movieRowId = TestUtilities.insertMovieValues(mContext, testValues);
        final Uri uri = MovieEntry.buildMovieUri(movieRowId);

        final ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);
        TestUtilities.insertReviewValues(mContext, reviewValues);
        testValues.putAll(reviewValues);

        // WHEN
        final Cursor cursor = new SingleMovieCursorBuilder(mContext, uri)
                .buildReviewsCursor(readableDatabase);

        // THEN
        TestUtilities.validateCursor("Error", cursor, reviewValues);

    }

    @Override
    protected void tearDown() throws Exception {
        TestUtilities.deleteAllRecordsFromProvider(mContext);
        super.tearDown();
    }
}
