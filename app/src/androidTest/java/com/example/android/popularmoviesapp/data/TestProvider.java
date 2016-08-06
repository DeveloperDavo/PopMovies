package com.example.android.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

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
                MovieContract.TopRatedEntry.CONTENT_URI, // URI
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
                MovieContract.TopRatedEntry.CONTENT_URI, // URI
                null, // all columns
                null, // all rows
                null, // selection args
                null // sort order
        );
        assertEquals("Error: Records not deleted from top_rated table during delete", 0, movieCursor.getCount());
        movieCursor.close();
    }

    /*
      This helper function deletes all records from both database tables using the database
      functions only.  This is designed to be used to reset the state of the database until the
      delete functionality is available in the ContentProvider.
    */
    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.TopRatedEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    static private final int BULK_ENTRIES = 10;

    static ContentValues[] createBulkInsertWeatherValues(long movieRowId) {
        ContentValues[] returnContentValues = new ContentValues[BULK_ENTRIES];

        for (int i = 0; i < BULK_ENTRIES; i++) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieRowId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Title " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "Poster Path " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Overview " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, "01-01-200" + i % 9);
//            movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, i % 2);
            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
    }
}

