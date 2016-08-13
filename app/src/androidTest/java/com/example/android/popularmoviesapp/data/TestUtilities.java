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
    final static long TEST_MOVIE_ID = 32343;
    final static long TEST_REVIEW_ID = 5;

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
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Star Wars: The Force Awakens");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "path_to_poster");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Luke Skywalker is no where to be found...");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, "8.5");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, "2015-12-17");
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
        return movieValues;
    }

    // default review values
    static ContentValues createReviewValues(long moviesRowId) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, moviesRowId);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, TEST_REVIEW_ID);
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
