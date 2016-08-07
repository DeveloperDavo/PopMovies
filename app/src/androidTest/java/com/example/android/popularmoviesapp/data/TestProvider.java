package com.example.android.popularmoviesapp.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
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

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
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
        db.delete(MovieContract.ReviewEntry.TABLE_NAME, null, null);
        db.close();
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

    static private final int BULK_ENTRIES = 10;

    static ContentValues[] createBulkInsertMoviesValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_ENTRIES];

        for (int i = 0; i < BULK_ENTRIES; i++) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, i + 1000);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Title " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "Poster Path " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Overview " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, "01-01-200" + i % 9);
            movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, i % 2);
            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
    }
}

