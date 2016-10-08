package com.example.android.popularmoviesapp.data;

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

    public final static long MOVIE_ROW_ID = 17;
    public final static long MOVIE_ID = 32343;
    public final static String TITLE = "Star Wars: The Force Awakens";
    public static final String POSTER_PATH = "path_to_poster";
    public static final String OVERVIEW = "Luke Skywalker is no where to be found...";
    public static final double RATING = 8.5;
    public static final double POPULARITY = 90.12;
    public static final String RELEASE = "2015-12-17";
    public static final int FAVORITE = 1;

    public final static long REVIEW_ID = 5;
    public static final int BULK_INSERT_SIZE = 10;
    public static final int BULK_INSERT_SIZE_REVIEWS = 3;

    public final static String VIDEO_ID = "videoId_ADFK";
    public final static String VIDEO_KEY = "videoKey_ASFDJK";
    private static final String VIDEO_SITE = "YouTube";
    private static final String VIDEO_TYPE = "Trailer";

    /**
     * Ensures an empty cursor is not returned.
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
     */
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            final String actualValue = valueCursor.getString(idx);
            assertEquals("Value '" + actualValue +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, actualValue);
        }
    }

    // default movie values
    public static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, MOVIE_ID);
        movieValues.put(MovieEntry.COLUMN_TITLE, TITLE);
        movieValues.put(MovieEntry.COLUMN_POSTER, POSTER_PATH);
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, OVERVIEW);
        movieValues.put(MovieEntry.COLUMN_RATING, RATING);
        movieValues.put(MovieEntry.COLUMN_POPULARITY, POPULARITY);
        movieValues.put(MovieEntry.COLUMN_RELEASE, RELEASE);
        movieValues.put(MovieEntry.COLUMN_FAVORITE, FAVORITE);
        return movieValues;
    }

    public static ContentValues createMovieValues(
            int movieId, String title, String posterPath, String overview,
            Double rating, Double popularity, String release, int favorite) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
        movieValues.put(MovieEntry.COLUMN_TITLE, title);
        movieValues.put(MovieEntry.COLUMN_POSTER, posterPath);
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
        movieValues.put(MovieEntry.COLUMN_RATING, rating);
        movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
        movieValues.put(MovieEntry.COLUMN_RELEASE, release);
        movieValues.put(MovieEntry.COLUMN_FAVORITE, favorite);
        return movieValues;
    }

    // default review values
    static ContentValues createReviewValues(long moviesRowId) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, moviesRowId);
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_ID, REVIEW_ID);
        reviewValues.put(ReviewEntry.COLUMN_AUTHOR, "David S");
        reviewValues.put(ReviewEntry.COLUMN_CONTENT, "Great movie :)");
        reviewValues.put(ReviewEntry.COLUMN_URL, "www.example.com");
        return reviewValues;
    }

    // default video values
    static ContentValues createVideoValues(long moviesRowId) {
        ContentValues videoValues = new ContentValues();
        videoValues.put(VideoEntry.COLUMN_MOVIE_KEY, moviesRowId);
        videoValues.put(VideoEntry.COLUMN_VIDEO_ID, VIDEO_ID);
        videoValues.put(VideoEntry.COLUMN_VIDEO_KEY, VIDEO_KEY);
        videoValues.put(VideoEntry.COLUMN_VIDEO_SITE, VIDEO_SITE);
        videoValues.put(VideoEntry.COLUMN_VIDEO_TYPE, VIDEO_TYPE);
        return videoValues;
    }

    /**
     * @param context
     * @return movieRowId
     */
    public static long createAndInsertMovieValues(Context context) {

        ContentValues testValues = TestUtilities.createMovieValues();
        return insertMovieValues(context, testValues);
    }

    private static long insertMovieValues(Context context, ContentValues testValues) {

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
            movieValues.put(MovieEntry.COLUMN_POPULARITY, i + 80.99);
            movieValues.put(MovieEntry.COLUMN_RELEASE, "release " + i);
            movieValues.put(MovieEntry.COLUMN_FAVORITE, i % 1);

            returnContentValues[i] = movieValues;
        }

        return returnContentValues;
    }

    static ContentValues[] createBulkInsertReviewValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_SIZE_REVIEWS];

        for (int i = 0; i < BULK_INSERT_SIZE_REVIEWS; i++) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, 1000);
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

    /*
         Students: The functions we provide inside of TestProvider use this utility class to test
         the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
         CTS tests.
         Note that this only tests that the onChange function is called; it does not test that the
         correct Uri is returned.
      */
    // TODO improve understanding
    static class TestContentObserver extends ContentObserver {
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

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
