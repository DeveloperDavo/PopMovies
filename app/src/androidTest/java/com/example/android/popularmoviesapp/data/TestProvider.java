package com.example.android.popularmoviesapp.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by David on 13/07/16.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /* This test checks to make sure that the content provider is registered correctly. */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void test_getType_movies() {

        // content://com.example.android.popularmoviesapp/movies/
        final String type = mContext.getContentResolver().
                getType(MovieContract.MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.popularmoviesapp/movies
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);
    }

    public void test_getType_reviews() {

        // content://com.example.android.popularmoviesapp/reviews/
        final String type = mContext.getContentResolver().
                getType(MovieContract.ReviewEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.popularmoviesapp/reviews
        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
                MovieContract.ReviewEntry.CONTENT_TYPE, type);
    }

    public void test_getType_movie() {

        // GIVEN
        final int movieId = 789;

        // WHEN
        // content://com.example.android.popularmoviesapp/movies/789/
        final String type = mContext.getContentResolver().
                getType(MovieContract.MovieEntry.buildSingleMovie(movieId));
        // vnd.android.cursor.dir/com.example.android.popularmoviesapp/movies/789
        assertEquals("Error: the MovieEntry CONTENT_URI with reviews should return ReviewEntry.CONTENT_ITEM_TYPE",
                MovieContract.ReviewEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testMoviesQuery() {

        // create and insert movie values
        long movieRowId = TestUtilities.createAndInsertMovieValues(mContext);
        assertTrue("Unable to Insert MovieEntry into the Database", movieRowId != -1);

        // create movie values
        ContentValues movieValues = TestUtilities.createMovieValues();

        // test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testSingleMovieQuery, movie query", movieCursor, movieValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie Query did not properly set NotificationUri",
                    movieCursor.getNotificationUri(), MovieContract.MovieEntry.CONTENT_URI);
        }
    }

    public void testReviewsQuery() {
        // create and insert review values
        final long testMovieRowId = 1;
        long reviewRowId = TestUtilities.createAndInsertReviewValues(mContext, testMovieRowId);
        assertTrue("Unable to Insert ReviewEntry into the Database", reviewRowId != -1);

        // create review values
        ContentValues reviewValues = TestUtilities.createReviewValues(testMovieRowId);

        // test the basic content provider query
        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testReviewQuery", reviewCursor, reviewValues);
    }

    public void testInsertReadProvider() {

        final ContentValues testMovieValues = TestUtilities.createMovieValues();

        // register content observer
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().
                registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().
                insert(MovieContract.MovieEntry.CONTENT_URI, testMovieValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        final Cursor moviesCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // all columns
                null, // selection
                null, // selection args
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                moviesCursor, testMovieValues);

        final ContentValues testValues = TestUtilities.createReviewValues(movieRowId);

        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().
                registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, tco);

        Uri reviewInsertUri = mContext.getContentResolver()
                .insert(MovieContract.ReviewEntry.CONTENT_URI, testValues);
        assertTrue(reviewInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        final Cursor reviewsCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider. Error validating ReviewEntry insert.",
                reviewsCursor, testValues);

        // add expected movie values to review values
        testValues.putAll(testMovieValues);

        // joined data
        final Cursor singleMovieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildSingleMovie(TestUtilities.MOVIE_ID),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Movie and Review Data.",
                singleMovieCursor, testValues);
    }

    public void test_bulkInsert_movies() {

        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertMovieValues();

        // register content observer
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(
                MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(
                MovieContract.MovieEntry.CONTENT_URI, bulkInsertContentValues);

        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(TestUtilities.BULK_INSERT_SIZE, insertCount);

        // TODO: sort order
        String sortOrder = null;
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                sortOrder
        );

        assertEquals(cursor.getCount(), TestUtilities.BULK_INSERT_SIZE);

        cursor.moveToFirst();
        for ( int i = 0; i < TestUtilities.BULK_INSERT_SIZE; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("test_bulkInsert_movies.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our movies delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our reviews delete.
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, reviewObserver);

        deleteAllRecordsFromProvider();

        movieObserver.waitForNotificationOrFail();
        reviewObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
    }

    public void testUpdateMovies() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieValues();

        Uri movieUri = mContext.getContentResolver().
                insert(MovieContract.MovieEntry.CONTENT_URI, values);
        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + movieRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieContract.MovieEntry._ID, movieRowId);
        updatedValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Luke Skywalker has ben found");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieCursor = mContext.getContentResolver().
                query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI, updatedValues, MovieContract.MovieEntry._ID + "= ?",
                new String[] { Long.toString(movieRowId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry._ID + " = " + movieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateMovies.  Error validating movie entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI, // URI
                null, // all rows
                null // selection args
        );

        mContext.getContentResolver().delete(
                MovieContract.ReviewEntry.CONTENT_URI, // URI
                null, // all rows
                null // selection args
        );

        final Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // selection args
                null // sort order
        );
        assertEquals("Error: Records not deleted from movie table during delete", 0, movieCursor.getCount());
        movieCursor.close();

        final Cursor topRatedCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // selection args
                null // sort order
        );
        assertEquals("Error: Records not deleted from top_rated table during delete", 0, movieCursor.getCount());
        movieCursor.close();
    }

}

