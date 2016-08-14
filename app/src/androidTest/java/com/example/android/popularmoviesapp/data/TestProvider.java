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

import static com.example.android.popularmoviesapp.data.MovieContract.*;

/**
 * Created by David on 13/07/16.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    // TODO improve understanding
    public void testContentProviderIsRegisteredCorrectly() {

        final PackageManager pm = mContext.getPackageManager();

        final ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + CONTENT_AUTHORITY,
                    providerInfo.authority, CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void test_getType_movies() {

        // WHEN
        // content://com.example.android.popularmoviesapp/movies/
        final String type = mContext.getContentResolver().
                getType(MovieEntry.CONTENT_URI);

        // THEN
        // vnd.android.cursor.dir/com.example.android.popularmoviesapp/movies
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);
    }

    public void test_getType_reviews() {

        // WHEN
        // content://com.example.android.popularmoviesapp/reviews/
        final String type = mContext.getContentResolver().
                getType(ReviewEntry.CONTENT_URI);

        // THEN
        // vnd.android.cursor.dir/com.example.android.popularmoviesapp/reviews
        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type);
    }

    public void test_getType_movie() {

        // GIVEN
        final int movieId = 789;

        // WHEN
        // content://com.example.android.popularmoviesapp/movies/789/
        final String type = mContext.getContentResolver().
                getType(MovieEntry.buildSingleMovieUri(movieId));

        // THEN
        // vnd.android.cursor.dir/com.example.android.popularmoviesapp/movies/789
        assertEquals("Error: the MovieEntry CONTENT_URI with reviews should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);
    }

    public void test_query_movies() {
        queryMovies();
    }

    public void test_getNotificationUri_movies() {

        // GIVEN
        final Cursor movieCursor = queryMovies();

        if (Build.VERSION.SDK_INT >= 19) {

            // WHEN
            final Uri notificationUri = movieCursor.getNotificationUri();

            // THEN
            assertEquals("Error: Movie Query did not properly set NotificationUri",
                    notificationUri, MovieEntry.CONTENT_URI);
        }
    }

    private Cursor queryMovies() {

        // GIVEN
        long movieRowId = TestUtilities.createAndInsertMovieValues(mContext);
        assertTrue("Unable to Insert MovieEntry into the Database", movieRowId != -1);

        final ContentValues movieValues = TestUtilities.createMovieValues();

        // WHEN
        final Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // THEN
        TestUtilities.validateCursor("testSingleMovieQuery, movie query", movieCursor, movieValues);
        return movieCursor;
    }

    // TODO match style of test_query_movies
    public void test_query_reviews() {

        final long testMovieRowId = 1;
        long reviewRowId = TestUtilities.createAndInsertReviewValues(mContext, testMovieRowId);
        assertTrue("Unable to Insert ReviewEntry into the Database", reviewRowId != -1);

        // create review values
        ContentValues reviewValues = TestUtilities.createReviewValues(testMovieRowId);

        // test the basic content provider query
        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testReviewQuery", reviewCursor, reviewValues);
    }

    public void testInsertReadProvider() {
        insertEntriesIntoContentProvider();
    }

    /**
     * A movie entry is created and inserted into the content provider.
     * Ensures the queried movies URI returns a cursor.
     *
     * A review entry is created and inserted into the content provider.
     * Ensures the queried reviews URI returns a cursor.
     *
     * Ensures the queried movie URI, which is a join between the movie and review entry,
     * returns a cursor.
     *
     * TODO has too much responsibility
     */
    public void insertEntriesIntoContentProvider() {

        final ContentValues testMovieValues = TestUtilities.createMovieValues();

        // register content observer
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().
                registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().
                insert(MovieEntry.CONTENT_URI, testMovieValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        final Cursor moviesCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // all columns
                null, // selection
                null, // selection args
                null  // sort order
        );
        TestUtilities.validateCursor("insertEntriesIntoContentProvider. Error validating MovieEntry.",
                moviesCursor, testMovieValues);

        final ContentValues testValues = TestUtilities.createReviewValues(movieRowId);

        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().
                registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);

        Uri reviewInsertUri = mContext.getContentResolver()
                .insert(ReviewEntry.CONTENT_URI, testValues);
        assertTrue(reviewInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        final Cursor reviewsCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("insertEntriesIntoContentProvider. Error validating ReviewEntry insert.",
                reviewsCursor, testValues);

        // add expected movie values to review values
        testValues.putAll(testMovieValues);

        // joined data
        final Cursor singleMovieCursor = mContext.getContentResolver().query(
                MovieEntry.buildSingleMovieUri(TestUtilities.MOVIE_ID),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("insertEntriesIntoContentProvider.  Error validating joined Movie and Review Data.",
                singleMovieCursor, testValues);
    }


    /**
     * Ensures bulkInsert inserts the correct amount of movie entries.
     *
     * Ensures each movie entry can be validated by querying the movies table.
     *
     * TODO too much responsibility
     */
    public void test_bulkInsert_movies() {

        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertMovieValues();

        // register content observer
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(
                MovieEntry.CONTENT_URI, true, movieObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(
                MovieEntry.CONTENT_URI, bulkInsertContentValues);

        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(TestUtilities.BULK_INSERT_SIZE, insertCount);

        // TODO: sort order
        String sortOrder = null;
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                sortOrder
        );

        assertEquals(cursor.getCount(), TestUtilities.BULK_INSERT_SIZE);

        cursor.moveToFirst();
        for (int i = 0; i < TestUtilities.BULK_INSERT_SIZE; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("test_bulkInsert_movies.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    /**
     * Ensures all entries are deleted after being inserted
     */
    public void testDeleteRecords() {
        insertEntriesIntoContentProvider();

        // Register a content observer for our movies delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our reviews delete.
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, reviewObserver);

        deleteAllRecordsFromProvider();

        movieObserver.waitForNotificationOrFail();
        reviewObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
    }

    /**
     * Inserts a movie entry and ensures the overview column has been updated correctly
     */
    public void testUpdateMovies() {

        // GIVEN

        final ContentValues values = TestUtilities.createMovieValues();

        final Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + movieRowId);

        final ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieEntry._ID, movieRowId);
        updatedValues.put(MovieEntry.COLUMN_OVERVIEW, "Luke Skywalker has ben found");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        final Cursor movieCursor = mContext.getContentResolver().
                query(MovieEntry.CONTENT_URI, null, null, null, null);

        final TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        // WHEN

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updatedValues, MovieEntry._ID + "= ?",
                new String[]{Long.toString(movieRowId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        final Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,   // projection
                MovieEntry._ID + " = " + movieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        // THEN
        TestUtilities.validateCursor("testUpdateMovies.  Error validating movie entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI, // URI
                null, // all rows
                null // selection args
        );

        mContext.getContentResolver().delete(
                ReviewEntry.CONTENT_URI, // URI
                null, // all rows
                null // selection args
        );

        final Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // selection args
                null // sort order
        );
        assertEquals("Error: Records not deleted from movie table during delete", 0, movieCursor.getCount());
        movieCursor.close();

        final Cursor topRatedCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // selection args
                null // sort order
        );
        assertEquals("Error: Records not deleted from top_rated table during delete", 0, movieCursor.getCount());
        movieCursor.close();
    }

}

