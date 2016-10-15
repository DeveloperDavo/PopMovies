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
     * Ensures movies, reviews and videos tables exist
     * Ensures movies, reviews and videos table has the correct columns
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
        tableNameHashSet.add(VideoEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        final Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: Database has not been created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Databse was created without tables",
                tableNameHashSet.isEmpty());

        final Cursor movieCursor = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for movie information.", movieCursor.moveToFirst());

        // movie entry columns
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieEntry._ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieEntry.COLUMN_POSTER);
        movieColumnHashSet.add(MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieEntry.COLUMN_RATING);
        movieColumnHashSet.add(MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieEntry.COLUMN_RELEASE);
        movieColumnHashSet.add(MovieEntry.COLUMN_FAVORITE);

        int movieColumnIndex = movieCursor.getColumnIndex("name");
        do {
            String columnName = movieCursor.getString(movieColumnIndex);
            movieColumnHashSet.remove(columnName);
        } while (movieCursor.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie columns",
                movieColumnHashSet.isEmpty());

        final Cursor reviewCursor = db.rawQuery("PRAGMA table_info(" + ReviewEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for review information.", reviewCursor.moveToFirst());

        // review entry columns
        final HashSet<String> reviewColumnHashSet = new HashSet<>();
        reviewColumnHashSet.add(ReviewEntry._ID);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_MOVIE_ROW_ID);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_REVIEW_ID);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_AUTHOR);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_CONTENT);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_URL);

        int reviewColumnIndex = reviewCursor.getColumnIndex("name");
        do {
            String columnName = reviewCursor.getString(reviewColumnIndex);
            reviewColumnHashSet.remove(columnName);
        } while (reviewCursor.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required reviews columns",
                reviewColumnHashSet.isEmpty());

        Cursor videoCursor = db.rawQuery("PRAGMA table_info(" + VideoEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for video information.", videoCursor.moveToFirst());

        // video entry columns
        final HashSet<String> videoColumnHashSet = new HashSet<>();
        videoColumnHashSet.add(VideoEntry._ID);
        videoColumnHashSet.add(VideoEntry.COLUMN_MOVIE_ROW_ID);
        videoColumnHashSet.add(VideoEntry.COLUMN_VIDEO_ID);
        videoColumnHashSet.add(VideoEntry.COLUMN_VIDEO_KEY);
        videoColumnHashSet.add(VideoEntry.COLUMN_VIDEO_SITE);
        videoColumnHashSet.add(VideoEntry.COLUMN_VIDEO_TYPE);

        int videoColumnIndex = videoCursor.getColumnIndex("name");
        do {
            String columnName = videoCursor.getString(videoColumnIndex);
            videoColumnHashSet.remove(columnName);
        } while (videoCursor.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required videos columns",
                videoColumnHashSet.isEmpty());

        db.close();
    }

    public void test_query_MovieEntry() {
        insertMovieEntry();
    }

    /**
     * Ensures a review entry can be inserted an queried using the movieRowId obtained from
     * inserting a movie entry.
     */
    public void test_query_ReviewEntry() {

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
     * Ensures a video entry can be inserted an queried using the movieRowId obtained from
     * inserting a movie entry.
     */
    public void test_query_VideoEntry() {

        long movieRowId = insertMovieEntry();

        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        final ContentValues contentValues = TestUtilities.createVideoValues(movieRowId);

        final long rowId = db.insert(VideoEntry.TABLE_NAME, null, contentValues);

        assertTrue(rowId != -1);

        final Cursor cursor = db.query(
                VideoEntry.TABLE_NAME, // table
                null, // all columns
                null, // columns for the "where" clause
                null, // values for the "where" clause
                null, // columns to group by
                null, // columns ot filter by row groups
                null // sort order
        );

        assertTrue("Error: No Records returned from videos query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Videos Query Validation Failed", cursor, contentValues);

        assertFalse("Error: More than one record returned from videos query",
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
