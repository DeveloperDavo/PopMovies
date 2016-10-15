package com.example.android.popularmoviesapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.android.popularmoviesapp.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;
import static com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;
import static com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

/**
 * Created by David on 10/07/16.
 */
public class TestUtilities extends AndroidTestCase {
    private static final String LOG_TAG = TestUtilities.class.getSimpleName();

    public final static long MOVIE_ID = 32343;
    public final static String TITLE = "Star Wars: The Force Awakens";
    public static final String POSTER = "posterPath";
    public static final String OVERVIEW = "Luke Skywalker is no where to be found...";
    public static final double RATING = 8.5;
    public static final double POPULARITY = 90.12;
    public static final String RELEASE = "2015-12-17";
    public static final int FAVORITE = 1;

    static final int BULK_INSERT_SIZE = 10;
    static final int BULK_INSERT_SIZE_REVIEWS = 3;

    /**
     * Ensures an empty cursor is not returned.
     * Modified from https://github.com/udacity/Sunshine-Version-2
     *
     * @param error message that is different for each test case
     */
    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    /**
     * Ensures all columns are found and have the correct values.
     * Modified from https://github.com/udacity/Sunshine-Version-2
     */
    static void validateCurrentRecord(
            String errorMessage, Cursor valueCursor, ContentValues expectedValues) {

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);

            assertFalse("Column '" + columnName + "' not found. " + errorMessage, idx == -1);

            if (columnName.equals(MovieEntry.COLUMN_POSTER)) {
            } else {
                validate(errorMessage, valueCursor, entry, idx);
            }
        }
    }

    private static void validate(String errorMessage, Cursor valueCursor, Map.Entry<String, Object> entry, int idx) {
        String expectedValue = entry.getValue().toString();
        final String actualValue = valueCursor.getString(idx);
        assertEquals("Value '" + actualValue +
                "' did not match the expected value '" +
                expectedValue + "'. " + errorMessage, expectedValue, actualValue);
    }

    // default movie values
    public static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, MOVIE_ID);
        movieValues.put(MovieEntry.COLUMN_TITLE, TITLE);
        movieValues.put(MovieEntry.COLUMN_POSTER, POSTER);
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, OVERVIEW);
        movieValues.put(MovieEntry.COLUMN_RATING, RATING);
        movieValues.put(MovieEntry.COLUMN_POPULARITY, POPULARITY);
        movieValues.put(MovieEntry.COLUMN_RELEASE, RELEASE);
        movieValues.put(MovieEntry.COLUMN_FAVORITE, FAVORITE);
        return movieValues;
    }

    // default review values
    static ContentValues createReviewValues(long moviesRowId) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(ReviewEntry.COLUMN_MOVIE_ROW_ID, moviesRowId);
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_ID, 5);
        reviewValues.put(ReviewEntry.COLUMN_AUTHOR, "David S");
        reviewValues.put(ReviewEntry.COLUMN_CONTENT, "Great movie :)");
        reviewValues.put(ReviewEntry.COLUMN_URL, "www.example.com");
        return reviewValues;
    }

    // default video values
    static ContentValues createVideoValues(long moviesRowId) {
        ContentValues videoValues = new ContentValues();
        videoValues.put(VideoEntry.COLUMN_MOVIE_ROW_ID, moviesRowId);
        videoValues.put(VideoEntry.COLUMN_VIDEO_ID, "videoId_ADFK");
        videoValues.put(VideoEntry.COLUMN_VIDEO_KEY, "videoKey_ASFDJK");
        videoValues.put(VideoEntry.COLUMN_VIDEO_SITE, "YouTube");
        videoValues.put(VideoEntry.COLUMN_VIDEO_TYPE, "Trailer");
        return videoValues;
    }

    /**
     * @return movieRowId
     */
    public static long createAndInsertMovieValues(Context context) {

        ContentValues testValues = TestUtilities.createMovieValues();
        return insertMovieValues(context, testValues);
    }

    static long insertMovieValues(Context context, ContentValues testValues) {

        // WHEN
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long moviesRowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);

        // THEN
        assertTrue("Error: Failure inserting movie values", moviesRowId != -1);

        return moviesRowId;
    }

    static ContentValues[] createBulkInsertMovieValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_SIZE];

        for (int i = 0; i < BULK_INSERT_SIZE; i++) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, i + 1000);
            movieValues.put(MovieEntry.COLUMN_TITLE, "title " + i);
            movieValues.put(MovieEntry.COLUMN_POSTER, "posterPath " + i);
            movieValues.put(MovieEntry.COLUMN_OVERVIEW, "overview " + i);
            movieValues.put(MovieEntry.COLUMN_RATING, i + 0.99);
            movieValues.put(MovieEntry.COLUMN_POPULARITY, 0.01 + 10 * i);
            movieValues.put(MovieEntry.COLUMN_RELEASE, "release " + i);
            movieValues.put(MovieEntry.COLUMN_FAVORITE, i % 2);

            returnContentValues[i] = movieValues;
        }

        return returnContentValues;
    }

    static ContentValues[] createBulkInsertReviewValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_SIZE_REVIEWS];

        for (int i = 0; i < BULK_INSERT_SIZE_REVIEWS; i++) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(ReviewEntry.COLUMN_MOVIE_ROW_ID, 1000);
            reviewValues.put(ReviewEntry.COLUMN_REVIEW_ID, "abc" + i);
            reviewValues.put(ReviewEntry.COLUMN_AUTHOR, "author" + i);
            reviewValues.put(ReviewEntry.COLUMN_CONTENT, "I give this review " + i + " out of 10");
            reviewValues.put(ReviewEntry.COLUMN_URL, "www.examplereview.com");

            returnContentValues[i] = reviewValues;
        }

        return returnContentValues;
    }

    static long createAndInsertReviewValues(Context context, long movieRowId) {

        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);

        long reviewRowId = db.insert(ReviewEntry.TABLE_NAME, null, reviewValues);

        assertTrue("Error: Failure to insert review values", reviewRowId != -1);

        return reviewRowId;
    }

    static long createAndInsertVideoValues(Context context, long movieRowId) {

        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues videoValues = TestUtilities.createVideoValues(movieRowId);

        long videoRowId = db.insert(VideoEntry.TABLE_NAME, null, videoValues);

        assertTrue("Error: Failure to insert video values", videoRowId != -1);

        return videoRowId;
    }

    /**
     * sourced from https://github.com/udacity/Sunshine-Version-2
     */
    public static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    public static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    /**
     *
     * Modified from https://github.com/udacity/Sunshine-Version-2
     */
    // TODO: move to test case
    public static void deleteAllRecordsFromProvider(Context context) {
        deleteAllRecordsFromP(context);

        final Cursor movieCursor = context.getContentResolver().query(
                MovieEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // SELECTION args
                null // sort order
        );
        assertEquals("Error: Records not deleted from movie table during delete",
                0, movieCursor.getCount());
        movieCursor.close();

        final Cursor reviewCursor = context.getContentResolver().query(
                ReviewEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // SELECTION args
                null // sort order
        );
        assertEquals("Error: Records not deleted from reviews table during delete",
                0, reviewCursor.getCount());
        reviewCursor.close();

        final Cursor videoCursor = context.getContentResolver().query(
                VideoEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // SELECTION args
                null // sort order
        );
        assertEquals("Error: Records not deleted from videos table during delete",
                0, videoCursor.getCount());
        videoCursor.close();
    }

    // TODO rename
    public static void deleteAllRecordsFromP(Context context) {
        context.getContentResolver().delete(
                MovieEntry.CONTENT_URI, // URI
                null, // all rows
                null // SELECTION args
        );

        context.getContentResolver().delete(
                ReviewEntry.CONTENT_URI, // URI
                null, // all rows
                null // SELECTION args
        );

        context.getContentResolver().delete(
                VideoEntry.CONTENT_URI, // URI
                null, // all rows
                null // SELECTION args
        );
    }

    /**
     *
     * Modified from https://github.com/udacity/Sunshine-Version-2
     */
    public static long insertTestMovie(Context context) {
        final ContentValues testValues = TestUtilities.createMovieValues();

        // register content observer
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        context.getContentResolver().
                registerContentObserver(MovieEntry.CONTENT_URI, true, tco);

        final Uri movieInsertUri = context.getContentResolver().
                insert(MovieEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        context.getContentResolver().unregisterContentObserver(tco);

        return ContentUris.parseId(movieInsertUri);
    }

}
