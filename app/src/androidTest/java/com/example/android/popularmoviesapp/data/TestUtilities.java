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

/**
 * Created by David on 10/07/16.
 */
public class TestUtilities extends AndroidTestCase {
    public final static long MOVIE_ID = 32343;
    public final static String TITLE = "Star Wars: The Force Awakens";
    public static final String POSTER_PATH = "path_to_poster";
    public static final String OVERVIEW = "Luke Skywalker is no where to be found...";
    public static final double RATING = 8.5;
    public static final String RELEASE = "2015-12-17";
    public static final int FAVORITE = 1;

    public final static long REVIEW_ID = 5;
    public static final int BULK_INSERT_SIZE = 10;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

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
    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, MOVIE_ID);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, TITLE);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, POSTER_PATH);
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, OVERVIEW);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, RATING);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, RELEASE);
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, FAVORITE);
        return movieValues;
    }

    // default review values
    static ContentValues createReviewValues(long moviesRowId) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, moviesRowId);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, REVIEW_ID);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "David S");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "Great movie :)");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, "www.example.com");
        return reviewValues;
    }

    static long createAndInsertMovieValues(Context context) {

        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues();

        long moviesRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert movie values", moviesRowId != -1);

        return moviesRowId;
    }

    static ContentValues[] createBulkInsertMovieValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_SIZE];

        for (int i = 0; i < BULK_INSERT_SIZE; i++) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, i + 1000);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "title " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "posterPath " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "overview " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, i + 0.99);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, "release " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, i % 1);

            returnContentValues[i] = movieValues;
        }

        // TODO try adding a movie with the same movie_id

        return returnContentValues;
    }

    static long createAndInsertReviewValues(Context context, long movieRowId) {

        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createReviewValues(movieRowId);

        long reviewRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert review values", reviewRowId != -1);

        return reviewRowId;
    }

    /*
         Students: The functions we provide inside of TestProvider use this utility class to test
         the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
         CTS tests.
         Note that this only tests that the onChange function is called; it does not test that the
         correct Uri is returned.
      */
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
