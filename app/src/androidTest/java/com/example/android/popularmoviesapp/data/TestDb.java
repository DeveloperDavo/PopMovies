package com.example.android.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.test.AndroidTestCase;

import java.util.HashSet;

import static com.example.android.popularmoviesapp.data.MovieContract.*;

/**
 * Created by David on 10/07/16.
 * Note: modified from https://github.com/udacity/Sunshine-Version-2
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void setUp() {
        deleteTheDatabase();
    }

    public void test_createDb() throws Exception {
        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        rawQuery(db);
    }

    private Cursor rawQuery(SQLiteDatabase db) {

        // GIVEN
        final HashSet<String> tableNameHashSet = new HashSet<>();
        buildTableNameHashSet(tableNameHashSet);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);

        assertEquals(true, db.isOpen());

        // WHEN
        final Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        // THEN
        assertTrue("Error: Database has not been created correctly", c.moveToFirst());

        db.close();
        return c;
    }

    public void test_dBHasAtLeastOneTable() throws Exception {

        // GIVEN
        final HashSet<String> tableNameHashSet = new HashSet<>();
        buildTableNameHashSet(tableNameHashSet);
        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        final Cursor c = rawQuery(db);

        // WHEN
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // THEN
        assertTrue("Error: Database was created without tables", tableNameHashSet.isEmpty());

        db.close();

    }

    public void test_moviesTableExists() throws Exception {
        checkMoviesTableExists();
    }

    public void test_moviesTableHasAllColumns() throws Exception {

        // GIVEN
        final Cursor movieCursor = checkMoviesTableExists();
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        buildMoviesColumnHashSet(movieColumnHashSet);

        // WHEN
        int movieColumnIndex = movieCursor.getColumnIndex("name");
        do {
            String columnName = movieCursor.getString(movieColumnIndex);
            movieColumnHashSet.remove(columnName);
        } while (movieCursor.moveToNext());

        // THEN
        assertTrue("Error: The database doesn't contain all of the required movie columns",
                movieColumnHashSet.isEmpty());
    }

    @NonNull
    private Cursor checkMoviesTableExists() {

        // GIVEN
        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        // WHEN
        final Cursor movieCursor = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")",
                null);

        // THEN
        assertTrue("Error: Unable to query the database for movie information.", movieCursor.moveToFirst());

        db.close();
        return movieCursor;
    }

    public void test_videoTableExists() throws Exception {
        checkVideosTableExists();
    }

    public void test_videosTableHasAllColumns() throws Exception {

        // GIVEN
        final Cursor cursor = checkVideosTableExists();
        final HashSet<String> columns = new HashSet<>();
        buildVideosColumnHashSet(columns);

        // WHEN
        int columnIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnIndex);
            columns.remove(columnName);
        } while (cursor.moveToNext());

        // THEN
        assertTrue("Error: The database doesn't contain all of the required video columns",
                columns.isEmpty());
    }
    @NonNull
    private Cursor checkVideosTableExists() {

        // GIVEN
        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        // WHEN
        final Cursor videoCursor = db.rawQuery("PRAGMA table_info(" + VideoEntry.TABLE_NAME + ")",
                null);


        // THEN
        assertTrue("Error: Unable to query the database for video information.", videoCursor.moveToFirst());

        db.close();
        return videoCursor;
    }

    public void test_reviewsTableExists() throws Exception {
        checkReviewsTableExists();
    }

    public void test_reviewsTableHasAllColumns() throws Exception {

        // GIVEN
        final Cursor cursor = checkReviewsTableExists();
        final HashSet<String> columns = new HashSet<>();
        buildReviewsColumnHashSet(columns);

        // WHEN
        int columnIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnIndex);
            columns.remove(columnName);
        } while (cursor.moveToNext());

        // THEN
        assertTrue("Error: The database doesn't contain all of the required review columns",
                columns.isEmpty());
    }

    @NonNull
    private Cursor checkReviewsTableExists() {

        // GIVEN
        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        // WHEN
        final Cursor cursor = db.rawQuery("PRAGMA table_info(" + ReviewEntry.TABLE_NAME + ")", null);

        // THEN
        assertTrue("Error: Unable to query the database for review information.", cursor.moveToFirst());

        db.close();
        return cursor;
    }


    public void test_query_MovieEntry() {
        insertMovieEntry();
    }

    /**
     * Ensures a movie entry can be inserted and queried.
     * @return movieRowId
     */
    private long insertMovieEntry() {

        // GIVEN
        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        final ContentValues contentValues = TestUtilities.createMovieValues();

        // WHEN
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

        // THEN
        assertTrue("Error: No Records returned from movie query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed", cursor, contentValues);

        assertFalse("Error: More than one record returned from movie query",
                cursor.moveToNext());

        cursor.close();
        db.close();

        return movieRowId;
    }

    /**
     * Ensures a video entry can be inserted an queried using the movieRowId obtained from
     * inserting a movie entry.
     */
    public void test_query_VideoEntry() {

        // GIVEN
        long movieRowId = insertMovieEntry();

        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        final ContentValues contentValues = TestUtilities.createVideoValues(movieRowId);

        // WHEN
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

        // THEN
        assertTrue("Error: No Records returned from videos query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Videos Query Validation Failed", cursor, contentValues);

        assertFalse("Error: More than one record returned from videos query",
                cursor.moveToNext());

        cursor.close();
        db.close();
    }

    /**
     * Ensures a review entry can be inserted an queried using the movieRowId obtained from
     * inserting a movie entry.
     */
    public void test_query_ReviewEntry() {

        // GIVEN
        long movieRowId = insertMovieEntry();

        final SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        final ContentValues contentValues = TestUtilities.createReviewValues(movieRowId);

        // WHEN
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

        // THEN
        assertTrue("Error: No Records returned from reviews query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Reviews Query Validation Failed", cursor, contentValues);

        assertFalse("Error: More than one record returned from reviews query",
                cursor.moveToNext());

        cursor.close();
        db.close();
    }

    private void deleteTheDatabase() {
        mContext.deleteDatabase(com.example.android.popularmoviesapp.data.MovieDbHelper.DATABASE_NAME);
    }


    private void buildTableNameHashSet(HashSet<String> tableNameHashSet) {
        tableNameHashSet.add(MovieEntry.TABLE_NAME);
        tableNameHashSet.add(ReviewEntry.TABLE_NAME);
        tableNameHashSet.add(VideoEntry.TABLE_NAME);
    }

    private void buildMoviesColumnHashSet(HashSet<String> movieColumnHashSet) {
        movieColumnHashSet.add(MovieEntry._ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieEntry.COLUMN_POSTER);
        movieColumnHashSet.add(MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieEntry.COLUMN_RATING);
        movieColumnHashSet.add(MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieEntry.COLUMN_RELEASE);
        movieColumnHashSet.add(MovieEntry.COLUMN_FAVORITE);
    }

    private void buildVideosColumnHashSet(HashSet<String> videoColumnHashSet) {
        videoColumnHashSet.add(VideoEntry._ID);
        videoColumnHashSet.add(VideoEntry.COLUMN_MOVIE_ROW_ID);
        videoColumnHashSet.add(VideoEntry.COLUMN_VIDEO_ID);
        videoColumnHashSet.add(VideoEntry.COLUMN_VIDEO_KEY);
        videoColumnHashSet.add(VideoEntry.COLUMN_VIDEO_SITE);
        videoColumnHashSet.add(VideoEntry.COLUMN_VIDEO_TYPE);
    }

    private void buildReviewsColumnHashSet(HashSet<String> reviewColumnHashSet) {
        reviewColumnHashSet.add(ReviewEntry._ID);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_MOVIE_ROW_ID);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_REVIEW_ID);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_AUTHOR);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_CONTENT);
        reviewColumnHashSet.add(ReviewEntry.COLUMN_URL);
    }

}
