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
import android.test.suitebuilder.annotation.Suppress;
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

    public void test_getType_movie() {

        // GIVEN
        final int _id = 12;

        // WHEN
        // content://com.example.android.popularmoviesapp/movies/12/
        final String type = mContext.getContentResolver().
                getType(MovieEntry.buildMovieUri(_id));

        // THEN
        // vnd.android.cursor.dir/com.example.android.popularmoviesapp/movies/789
        assertEquals("Error: the MovieEntry CONTENT_URI with reviews should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);
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

    public void test_getType_videos() {

        // WHEN
        // content://com.example.android.popularmoviesapp/videos/
        final String type = mContext.getContentResolver().
                getType(VideoEntry.CONTENT_URI);

        // THEN
        // vnd.android.cursor.dir/com.example.android.popularmoviesapp/videos
        assertEquals("Error: the VideoEntry CONTENT_URI should return VideoEntry.CONTENT_TYPE",
                VideoEntry.CONTENT_TYPE, type);
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

    public void test_query_reviews() {
        queryReviews();
    }

    public void test_getNotificationUri_reviews() {

        // GIVEN
        final Cursor reviewCursor = queryReviews();

        if (Build.VERSION.SDK_INT >= 19) {

            // WHEN
            final Uri notificationUri = reviewCursor.getNotificationUri();

            // THEN
            assertEquals("Error: Review Query did not properly set NotificationUri",
                    notificationUri, ReviewEntry.CONTENT_URI);
        }
    }

    private Cursor queryReviews() {
        final long testMovieRowId = 1;
        long reviewRowId = TestUtilities.createAndInsertReviewValues(mContext, testMovieRowId);
        assertTrue("Unable to Insert ReviewEntry into the Database", reviewRowId != -1);

        // create review values
        final ContentValues reviewValues = TestUtilities.createReviewValues(testMovieRowId);

        // test the basic content provider query
        final Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testReviewQuery", reviewCursor, reviewValues);

        return reviewCursor;
    }

    public void test_query_videos() {
        queryVideos();
    }

    public void test_getNotificationUri_videos() {

        // GIVEN
        final Cursor videoCursor = queryVideos();

        if (Build.VERSION.SDK_INT >= 19) {

            // WHEN
            final Uri notificationUri = videoCursor.getNotificationUri();

            // THEN
            assertEquals("Error: Video Query did not properly set NotificationUri",
                    notificationUri, VideoEntry.CONTENT_URI);
        }
    }

    private Cursor queryVideos() {
        final long testMovieRowId = 1;
        long videoRowId = TestUtilities.createAndInsertVideoValues(mContext, testMovieRowId);
        assertTrue("Unable to Insert VideoEntry into the Database", videoRowId != -1);

        // create video values
        final ContentValues videoValues = TestUtilities.createVideoValues(testMovieRowId);

        // test the basic content provider query
        final Cursor videoCursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testVideoQuery", videoCursor, videoValues);

        return videoCursor;
    }

    public void testInsertReadProvider() {
        test_insert_and_query();
    }

    /**
     * A movie entry is created and inserted into the content provider.
     * Ensures the queried movies URI returns a cursor.
     *
     * A review entry is created and inserted into the content provider.
     * Ensures the queried reviews URI returns a cursor.
     *
     * A video entry is created and inserted into the content provider.
     * Ensures the queried videos URI returns a cursor.
     *
     * Ensures the queried movie URI, which is a join between the movie, review and video entry,
     * returns a cursor.
     *
     * TODO has too much responsibility
     */
    public void test_insert_and_query() {

        final ContentValues testValues = TestUtilities.createMovieValues();

        // register content observer
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().
                registerContentObserver(MovieEntry.CONTENT_URI, true, tco);

        final Uri movieInsertUri = mContext.getContentResolver().
                insert(MovieEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieInsertUri);
        assertTrue(movieRowId != -1);

        final Cursor moviesCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // all columns
                null, // selection
                null, // selection args
                null  // sort order
        );
        TestUtilities.validateCursor("Error validating movie data",
                moviesCursor, testValues);

        final ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);

        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().
                registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);

        Uri reviewInsertUri = mContext.getContentResolver()
                .insert(ReviewEntry.CONTENT_URI, reviewValues);
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
        TestUtilities.validateCursor("Error validating review data",
                reviewsCursor, reviewValues);

        // add review values to test values
        testValues.putAll(reviewValues);

        final ContentValues videoValues = TestUtilities.createVideoValues(movieRowId);

        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().
                registerContentObserver(VideoEntry.CONTENT_URI, true, tco);

        Uri videoInsertUri = mContext.getContentResolver()
                .insert(VideoEntry.CONTENT_URI, videoValues);
        assertTrue(videoInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        final Cursor videosCursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("Error validating video data",
                videosCursor, videoValues);

        // add review values to testValues
        testValues.putAll(videoValues);
    }


    /**
     * Ensures bulkInsert inserts the correct amount of movie entries.
     *
     * Ensures each movie entry can be validated by querying the movies table.
     *
     * TODO: too much responsibility
     * TODO: bulkInsert is not in use at the moment so the test has been suppressed
     */
    @Suppress
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
     * Ensures bulkInsert inserts the correct amount of review entries.
     *
     * Ensures each review entry can be validated by querying the reviews table.
     *
     * TODO too much responsibility
     */
    public void test_bulkInsert_reviews() {

        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertReviewValues();

        // register content observer
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(
                ReviewEntry.CONTENT_URI, true, reviewObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(
                ReviewEntry.CONTENT_URI, bulkInsertContentValues);

        reviewObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);

        assertEquals(TestUtilities.BULK_INSERT_SIZE_REVIEWS, insertCount);

        String sortOrder = null;
        Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                sortOrder
        );

        assertEquals(cursor.getCount(), TestUtilities.BULK_INSERT_SIZE_REVIEWS);

        cursor.moveToFirst();
        for (int i = 0; i < TestUtilities.BULK_INSERT_SIZE_REVIEWS; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("test_bulkInsert_reviews.  " +
                    "Error validating ReviewEntry " + i, cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    /**
     * Ensures all entries are deleted after being inserted
     */
    public void testDeleteRecords() {
        test_insert_and_query();

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

        mContext.getContentResolver().delete(
                VideoEntry.CONTENT_URI, // URI
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
        assertEquals("Error: Records not deleted from movie table during delete",
                0, movieCursor.getCount());
        movieCursor.close();

        final Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // selection args
                null // sort order
        );
        assertEquals("Error: Records not deleted from reviews table during delete",
                0, reviewCursor.getCount());
        reviewCursor.close();

        final Cursor videoCursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // selection args
                null // sort order
        );
        assertEquals("Error: Records not deleted from videos table during delete",
                0, videoCursor.getCount());
        videoCursor.close();
    }
}

