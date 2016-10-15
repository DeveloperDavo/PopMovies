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

import static com.example.android.popularmoviesapp.data.MovieContract.CONTENT_AUTHORITY;
import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;
import static com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;
import static com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;
import static com.example.android.popularmoviesapp.data.TestUtilities.createAndInsertMovieValues;

/**
 * Created by David on 13/07/16.
 * Note: modified from https://github.com/udacity/Sunshine-Version-2
 */
public class TestProvider extends AndroidTestCase {

    private static final String LOG_TAG = TestProvider.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtilities.deleteAllRecordsFromProvider(mContext);
    }

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

    /****************
     * getType
     ***********************/

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
        final int rowId = 12;

        // WHEN
        // content://com.example.android.popularmoviesapp/movies/12/
        final String type = mContext.getContentResolver().
                getType(MovieEntry.buildMovieUri(rowId));

        // THEN
        // vnd.android.cursor.dir/com.example.android.popularmoviesapp/movies/789
        assertEquals("Error: the MovieEntry CONTENT_URI with reviews should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);
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

    /****************
     * insert
     ***********************/

    public void test_insert_movieEntry() {
        final ContentValues testValues = TestUtilities.createMovieValues();
        insertMovieEntry(testValues);
    }

    private long insertMovieEntry(ContentValues testValues) {

        // GIVEN

        // register content observer
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().
                registerContentObserver(MovieEntry.CONTENT_URI, true, tco);

        // WHEN
        final Uri movieInsertUri = mContext.getContentResolver().
                insert(MovieEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // THEN
        long movieRowId = ContentUris.parseId(movieInsertUri);
        assertTrue(movieRowId != -1);

        return movieRowId;
    }

    public void test_insert_videoEntry() throws Exception {
        long movieRowId = createAndInsertMovieValues(mContext);
        final ContentValues videoValues = TestUtilities.createVideoValues(movieRowId);
        insertVideoEntry(videoValues);
    }

    private long insertVideoEntry(ContentValues videoValues) {

        // GIVEN

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().
                registerContentObserver(VideoEntry.CONTENT_URI, true, tco);

        // WHEN
        Uri videoInsertUri = mContext.getContentResolver()
                .insert(VideoEntry.CONTENT_URI, videoValues);
        assertTrue(videoInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // THEN
        long videoRowId = ContentUris.parseId(videoInsertUri);
        assertTrue(videoRowId != -1);

        return videoRowId;
    }

    public void test_insert_reviewEntry() throws Exception {
        long movieRowId = createAndInsertMovieValues(mContext);
        final ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);
        insertReviewEntry(reviewValues);
    }

    private long insertReviewEntry(ContentValues reviewValues) {

        // GIVEN
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().
                registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);

        // WHEN
        Uri reviewInsertUri = mContext.getContentResolver()
                .insert(ReviewEntry.CONTENT_URI, reviewValues);
        assertTrue(reviewInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // THEN
        long reviewRowId = ContentUris.parseId(reviewInsertUri);
        assertTrue(reviewRowId != -1);

        return reviewRowId;

    }

    /****************
     * query
     ***********************/

    public void test_query_movies() {
        queryMovies();
    }

    private Cursor queryMovies() {

        // GIVEN
        final ContentValues movieValues = TestUtilities.createMovieValues();
        insertMovieEntry(movieValues);

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

    public void test_query_videos() {
        queryVideos();
    }

    private Cursor queryVideos() {

        // GIVEN
        final long testMovieRowId = createAndInsertMovieValues(mContext);
        final ContentValues videoValues = TestUtilities.createVideoValues(testMovieRowId);
        insertVideoEntry(videoValues);

        // WHEN
        final Cursor videoCursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // THEN
        TestUtilities.validateCursor("testVideoQuery", videoCursor, videoValues);

        return videoCursor;
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

    public void test_query_reviews() {
        queryReviews();
    }

    private Cursor queryReviews() {

        // GIVEN
        final long testMovieRowId = createAndInsertMovieValues(mContext);
        final ContentValues reviewValues = TestUtilities.createReviewValues(testMovieRowId);
        insertReviewEntry(reviewValues);

        // WHEN
        final Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // THEN
        TestUtilities.validateCursor("testReviewQuery", reviewCursor, reviewValues);

        return reviewCursor;
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

    /****************
     * update
     ***********************/


    public void test_update_movieEntry() throws Exception {

        // GIVEN
        final ContentValues testValues = TestUtilities.createMovieValues();
        final long movieRowId = insertMovieEntry(testValues);

        final ContentValues updatedValues = new ContentValues(testValues);
        updatedValues.put(MovieEntry._ID, movieRowId);
        updatedValues.put(MovieEntry.COLUMN_OVERVIEW, "Luke Skywalker has been found");

        // WHEN
        int updatedRows = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updatedValues, MovieEntry._ID + "= ?",
                new String[]{Long.toString(movieRowId)});

        // THEN
        final Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // projection
                MovieEntry._ID + " = ?",
                new String[]{String.valueOf(movieRowId)}, // Values for the "where" clause
                null // sort order
        );

        assert cursor != null;
        TestUtilities.validateCursor("testUpdateMovies. Error validating entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void test_update_videoEntry() throws Exception {

        // GIVEN
        final ContentValues movieValues = TestUtilities.createMovieValues();
        final long movieRowId = insertMovieEntry(movieValues);

        final ContentValues testValues = TestUtilities.createVideoValues(movieRowId);
        final long videoRowId = insertVideoEntry(testValues);

        final ContentValues updatedValues = new ContentValues(testValues);
        updatedValues.put(VideoEntry._ID, videoRowId);
        updatedValues.put(VideoEntry.COLUMN_VIDEO_TYPE, "Featurette");

        // WHEN
        int updatedRows = mContext.getContentResolver().update(
                VideoEntry.CONTENT_URI, updatedValues, VideoEntry._ID + "= ?",
                new String[]{Long.toString(videoRowId)});

        // THEN
        final Cursor cursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null, // projection
                VideoEntry._ID + " = ?",
                new String[]{String.valueOf(videoRowId)}, // Values for the "where" clause
                null // sort order
        );

        assert cursor != null;
        TestUtilities.validateCursor("testUpdateMovies. Error validating entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void test_update_reviewEntry() throws Exception {

        // GIVEN
        final ContentValues movieValues = TestUtilities.createMovieValues();
        final long movieRowId = insertMovieEntry(movieValues);

        final ContentValues testValues = TestUtilities.createReviewValues(movieRowId);
        final long reviewRowId = insertReviewEntry(testValues);

        final ContentValues updatedValues = new ContentValues(testValues);
        updatedValues.put(ReviewEntry._ID, reviewRowId);
        updatedValues.put(ReviewEntry.COLUMN_CONTENT, "Greatest movie ever");

        // WHEN
        int updatedRows = mContext.getContentResolver().update(
                ReviewEntry.CONTENT_URI, updatedValues, ReviewEntry._ID + "= ?",
                new String[]{Long.toString(reviewRowId)});

        // THEN
        final Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null, // projection
                ReviewEntry._ID + " = ?",
                new String[]{String.valueOf(reviewRowId)}, // Values for the "where" clause
                null // sort order
        );

        assert cursor != null;
        TestUtilities.validateCursor("testUpdateMovies. Error validating entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    /****************
     * delete
     ***********************/

    public void test_delete_movieEntry() throws Exception {

        // GIVEN
        final ContentValues testValues = TestUtilities.createMovieValues();
        insertMovieEntry(testValues);

        TestUtilities.TestContentObserver testObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, testObserver);

        // WHEN
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI, // URI
                null, // all rows
                null // SELECTION args
        );

        // THEN
        testObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(testObserver);

        final Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // SELECTION args
                null // sort order
        );

        assert cursor != null;
        assertEquals("Error: Records not deleted from table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void test_delete_videoEntry() throws Exception {

        // GIVEN
        final ContentValues movieValues = TestUtilities.createMovieValues();
        final long movieRowId = insertMovieEntry(movieValues);

        final ContentValues testValues = TestUtilities.createVideoValues(movieRowId);
        insertVideoEntry(testValues);

        TestUtilities.TestContentObserver testObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(VideoEntry.CONTENT_URI, true, testObserver);

        // WHEN
        mContext.getContentResolver().delete(
                VideoEntry.CONTENT_URI, // URI
                null, // all rows
                null // SELECTION args
        );

        // THEN
        testObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(testObserver);

        final Cursor cursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // SELECTION args
                null // sort order
        );

        assert cursor != null;
        assertEquals("Error: Records not deleted from table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void test_delete_reviewEntry() throws Exception {

        // GIVEN
        final ContentValues movieValues = TestUtilities.createMovieValues();
        final long movieRowId = insertMovieEntry(movieValues);

        final ContentValues testValues = TestUtilities.createReviewValues(movieRowId);
        insertReviewEntry(testValues);

        TestUtilities.TestContentObserver testObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, testObserver);

        // WHEN
        mContext.getContentResolver().delete(
                ReviewEntry.CONTENT_URI, // URI
                null, // all rows
                null // SELECTION args
        );

        // THEN
        testObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(testObserver);

        final Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // SELECTION args
                null // sort order
        );

        assert cursor != null;
        assertEquals("Error: Records not deleted from table during delete", 0, cursor.getCount());
        cursor.close();
    }
}
