package com.example.android.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by David on 10/07/16.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void setUp() {
        deleteTheDatabase();
    }

    void deleteTheDatabase() {
        mContext.deleteDatabase(com.example.android.popularmoviesapp.data.MovieDbHelper.DATABASE_NAME);
    }

    // TODO: separate into two or three separate tests
    public void testCreateDb() throws Throwable {

        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was created without tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database " +
                "for table information.", c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for in movies
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RATING);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_FAVORITE);

        int movieColumnIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(movieColumnIndex);
            movieColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required movie
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie columns",
                movieColumnHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.ReviewEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database " +
                "for table information.", c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for in reviews
        final HashSet<String> reviewColumnHashSet = new HashSet<>();
        reviewColumnHashSet.add(MovieContract.ReviewEntry._ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_CONTENT);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_URL);

        int reviewColumnIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(reviewColumnIndex);
            reviewColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required reviews columns",
                reviewColumnHashSet.isEmpty());
        db.close();
    }

    public void testMovieTable() {
        insertMovieId();
    }

    public void testReviewsTable() {

        // get movie_id
        long movieRowId = insertMovieId();

        // Get reference to writable database
        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        // Create ContentValues of what you want to insert
        final ContentValues contentValues = TestUtilities.createReviewValues(movieRowId);

        // Insert ContentValues into database and get a row ID back
        final long rowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, contentValues);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Query the database and receive a Cursor back
        final Cursor cursor = db.query(
                MovieContract.ReviewEntry.TABLE_NAME, // table
                null, // all columns
                null, // columns for the "where" clause
                null, // values for the "where" clause
                null, // columns to group by
                null, // columns ot filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row
        assertTrue("Error: No Records returned from reviews query", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: Reviews Query Validation Failed", cursor, contentValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from reviews query",
                cursor.moveToNext());

        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }

    private long insertMovieId() {
        // Get reference to writable database
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        // Create ContentValues of what you want to insert
        ContentValues contentValues = TestUtilities.createMovieValues();

        // Insert ContentValues into database and get a row ID back
        long moviesRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
        assertTrue(moviesRowId != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME, // table
                null, // all columns
                null, // columns for the "where" clause
                null, // values for the "where" clause
                null, // columns to group by
                null, // columns ot filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row
        assertTrue("Error: No Records returned from movie query", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed", cursor, contentValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from movie query",
                cursor.moveToNext());

        // Finally, close the cursor and database
        cursor.close();
        db.close();

        return moviesRowId;
    }
}
