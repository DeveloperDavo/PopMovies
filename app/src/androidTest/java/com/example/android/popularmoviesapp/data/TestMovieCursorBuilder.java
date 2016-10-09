package com.example.android.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;

import com.example.android.popularmoviesapp.Utility;
import com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by David on 09/10/16.
 */
@Suppress
public class TestMovieCursorBuilder extends AndroidTestCase {

    private SQLiteDatabase readableDatabase;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
        readableDatabase = movieDbHelper.getReadableDatabase();
        TestUtilities.deleteAllRecordsFromProvider(mContext);
    }

    public void test_build_whenNoSortOrder() {

        // GIVEN
        mContext.getContentResolver().bulkInsert(
                MovieEntry.CONTENT_URI, TestUtilities.createBulkInsertMovieValues());
        final MovieCursorBuilder movieCursorBuilder =
                new MovieCursorBuilder(readableDatabase, null, null, null, null);

        // WHEN
        final Cursor cursor = movieCursorBuilder.build();

        // THEN
        assertThat(cursor.getCount()).isEqualTo(TestUtilities.BULK_INSERT_SIZE);

    }

    public void test_build_whenSortingByRating() {

        // GIVEN
        mContext.getContentResolver().bulkInsert(
                MovieEntry.CONTENT_URI, TestUtilities.createBulkInsertMovieValues());
        final MovieCursorBuilder movieCursorBuilder =
                new MovieCursorBuilder(readableDatabase, null, null, null, Utility.SORT_BY_RATING_DESC);

        // WHEN
        final Cursor cursor = movieCursorBuilder.build();

        // THEN
        assertThat(cursor.getCount()).isEqualTo(TestUtilities.BULK_INSERT_SIZE);
    }

    public void test_build_whenSortingByPopularity() {

        // GIVEN
        mContext.getContentResolver().bulkInsert(
                MovieEntry.CONTENT_URI, TestUtilities.createBulkInsertMovieValues());
        final MovieCursorBuilder movieCursorBuilder =
                new MovieCursorBuilder(readableDatabase, null, null, null, Utility.SORT_BY_POPULARITY_DESC);

        // WHEN
        final Cursor cursor = movieCursorBuilder.build();

        // THEN
        assertThat(cursor.getCount()).isEqualTo(TestUtilities.BULK_INSERT_SIZE);
    }

    public void test_build_whenPopularityIsZero() {

        // GIVEN
        ContentValues contentValues = TestUtilities.createMovieValues();
        contentValues.remove(MovieEntry.COLUMN_POPULARITY);
        contentValues.put(MovieEntry.COLUMN_POPULARITY, 0.00);
        TestUtilities.insertMovieValues(mContext, contentValues);
        final MovieCursorBuilder movieCursorBuilder =
                new MovieCursorBuilder(readableDatabase, null, null, null, Utility.SORT_BY_POPULARITY_DESC);

        // WHEN
        final Cursor cursor = movieCursorBuilder.build();
//        Log.d(LOG_TAG, "cursorDump: " + DatabaseUtils.dumpCursorToString(cursor));

        // THEN
        assertThat(cursor.moveToFirst()).isFalse();
    }

    public void test_incrementBy_rating() {

        // GIVEN
        mContext.getContentResolver().bulkInsert(
                MovieEntry.CONTENT_URI, TestUtilities.createBulkInsertMovieValues());
        final MovieCursorBuilder movieCursorBuilder =
                new MovieCursorBuilder(readableDatabase, null, null, null, null);
        final String initialSelection = "";
        final int increments = 1;
        movieCursorBuilder.setIncrements(increments);

        // WHEN
        final String selection = movieCursorBuilder.incrementBy(MovieCursorBuilder.RATING, 0, initialSelection);

        // THEN
        assertThat(selection).isEqualTo(MovieEntry.COLUMN_RATING + " > " + 0.00 + " AND " + MovieEntry.COLUMN_RATING + " <= " + 10.00);

    }

    public void test_incrementBy_popularity() {

        // GIVEN
        mContext.getContentResolver().bulkInsert(
                MovieEntry.CONTENT_URI, TestUtilities.createBulkInsertMovieValues());
        final MovieCursorBuilder movieCursorBuilder =
                new MovieCursorBuilder(readableDatabase, null, null, null, null);
        final String initialSelection = "";
        final int increments = 2;
        movieCursorBuilder.setIncrements(increments);

        // 1st increment
        // WHEN
        String selection = movieCursorBuilder.incrementBy(MovieCursorBuilder.POPULARITY, 0, initialSelection);

        // THEN
        assertThat(selection).isEqualTo(MovieEntry.COLUMN_POPULARITY + " > " + 50.00 + " AND " + MovieEntry.COLUMN_POPULARITY + " <= " + 100.00);

        // 2nd increment
        // WHEN
        selection = movieCursorBuilder.incrementBy(MovieCursorBuilder.POPULARITY, 1, initialSelection);

        // THEN
        assertThat(selection).isEqualTo(MovieEntry.COLUMN_POPULARITY + " > " + 0.00 + " AND " + MovieEntry.COLUMN_POPULARITY + " <= " + 50.00);

    }

    public void test_setUpSelection_whenSelectionIsNull() {

        // GIVEN
        final String initialSelection = null;
        final MovieCursorBuilder movieCursorBuilder = new MovieCursorBuilder(
                readableDatabase, null, initialSelection, null, null);

        // WHEN
        final String selection = movieCursorBuilder.setUpSelection(initialSelection);

        // THEN
        assertThat(selection).isEqualTo("");
    }

    public void test_setUpSelection_whenSelectionIsNotNull() {

        // GIVEN
        final String initialSelection = "notNull";
        final MovieCursorBuilder movieCursorBuilder = new MovieCursorBuilder(
                readableDatabase, null, initialSelection, null, null);

        // WHEN
        final String selection = movieCursorBuilder.setUpSelection(initialSelection);

        // THEN
        assertThat(selection).isEqualTo(initialSelection + " AND ");
    }

    public void test_buildCursor() {

        // GIVEN
        mContext.getContentResolver().bulkInsert(
                MovieEntry.CONTENT_URI, TestUtilities.createBulkInsertMovieValues());
        final MovieCursorBuilder movieCursorBuilder = new MovieCursorBuilder(
                readableDatabase, null, null, null, null);

        // WHEN
        final Cursor cursor = movieCursorBuilder.buildCursor(null);

        // THEN
        assertThat(cursor.getCount()).isEqualTo(TestUtilities.BULK_INSERT_SIZE);
    }
}
