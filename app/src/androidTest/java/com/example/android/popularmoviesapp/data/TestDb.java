package com.example.android.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import static com.example.android.popularmoviesapp.data.MovieContract.*;

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

    /**
     * Ensures database has been created
     * Ensures movies and reviews tables exist
     * Ensures movies and reviews table has the correct columns
     *
     * @throws Throwable
     * TODO too much responsibility for one test
     */
    public void testCreateDb() throws Throwable {

        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieEntry.TABLE_NAME);
        tableNameHashSet.add(ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: Database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Databse was created without tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: Unable to query the database for table information.", c.moveToFirst());

        // movie entry columns
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieEntry._ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieEntry.COLUMN_RATING);
        movieColumnHashSet.add(MovieEntry.COLUMN_RELEASE);
        movieColumnHashSet.add(MovieEntry.COLUMN_FAVORITE);

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
        c = db.rawQuery("PRAGMA table_info(" + ReviewEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for table information.", c.moveToFirst());

        // review entry columns
        final HashSet<String> reviewColumnHashSet = new HashSet<>();
        reviewColumnHashSet.add(ReviewEntry._ID);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_MOVIE_KEY);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_REVIEW_ID);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_AUTHOR);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_CONTENT);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_URL);

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
        insertMovieEntry();
    }

    /**
     * Ensures a review entry can be inserted an queried using the movieRowId obtained from
     * inserting a movie entry.
     */
    public void testReviewsTable() {

        long movieRowId = insertMovieEntry();

        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        final ContentValues contentValues = TestUtilities.createReviewValues(movieRowId);

        final long rowId = db.insert(ReviewEntry.TABLE_NAME, null, contentValues);

        assertTrue(rowId != -1);

        final Cursor cursor = db.query(
                ReviewEntry.TABLE_NAME, // table
                null, // all columns
                null, // columns for the "where" clause
                null, // values for the "where" clause
                null, // columns to group by
                null, // columns ot filter by row groups
                null // sort order
        );

        assertTrue("Error: No Records returned from reviews query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Reviews Query Validation Failed", cursor, contentValues);

        assertFalse("Error: More than one record returned from reviews query",
                cursor.moveToNext());

        cursor.close();
        db.close();
    }

    /**
     * Ensures a movie entry can be inserted and queried.
     * @return movieRowId
     */
    private long insertMovieEntry() {

        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        final ContentValues contentValues = TestUtilities.createMovieValues();

        final long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, contentValues);
        assertTrue(movieRowId != -1);

        final Cursor cursor = db.query(
                MovieEntry.TABLE_NAME, // table
                null, // all columns
                null, // columns for the "where" clause
                null, // values for the "where" clause
                null, // columns to group by
                null, // columns ot filter by row groups
                null // sort order
        );

        assertTrue("Error: No Records returned from movie query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed", cursor, contentValues);

        assertFalse("Error: More than one record returned from movie query",
                cursor.moveToNext());

        cursor.close();
        db.close();

        return movieRowId;
    }
}
