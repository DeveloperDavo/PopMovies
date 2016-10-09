package com.example.android.popularmoviesapp.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;

import com.example.android.popularmoviesapp.Utility;
import com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by David on 08/10/16.
 */
@Suppress
public class TestMovieCursorBuilderOld extends AndroidTestCase {

    private SQLiteDatabase readableDatabase;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
        readableDatabase = movieDbHelper.getReadableDatabase();
        TestUtilities.deleteAllRecordsFromProvider(mContext);
    }

    public void test_build() {

        // GIVEN
        mContext.getContentResolver().bulkInsert(
                MovieEntry.CONTENT_URI, TestUtilities.createBulkInsertMovieValues());
        final MovieCursorBuilderOld movieCursorBuilder = new MovieCursorBuilderOld(
                readableDatabase, null, null, null, null);

        // WHEN
        final Cursor cursor = movieCursorBuilder.build();

        // THEN
        assertThat(cursor.getCount()).isEqualTo(TestUtilities.BULK_INSERT_SIZE);
    }

    public void test_build_whenSortingByRating() {

        // GIVEN
        mContext.getContentResolver().bulkInsert(
                MovieEntry.CONTENT_URI, TestUtilities.createBulkInsertMovieValues());
        final MovieCursorBuilderOld movieCursorBuilder = new MovieCursorBuilderOld(
                readableDatabase, null, null, null, Utility.SORT_BY_POPULARITY_DESC);

        // WHEN
        final Cursor cursor = movieCursorBuilder.build();

        // THEN
        assertThat(cursor.getCount()).isEqualTo(TestUtilities.BULK_INSERT_SIZE);
    }

    /*
        public void test_addToCursorListIncrementingByPopularity() {

            // GIVEN
            final String initialSelection = ;

            // WHEN
            final String SELECTION = movieCursorBuilder.addToCursorListIncrementingByPopularity();

            // THEN
            assertThat(SELECTION).isEqualTo("");

        }
    */
/*

    public void test_setUpSelection_whenSelectionIsNull() {

        // GIVEN
        final String initialSelection = null;
        final MovieCursorBuilderOld movieCursorBuilder = new MovieCursorBuilderOld(
                readableDatabase, null, initialSelection, null, null);

        // WHEN
        final String selection = movieCursorBuilder.setUpSelection(initialSelection);

        // THEN
        assertThat(selection).isEqualTo("");
    }

    public void test_setUpSelection_whenSelectionIsNotNull() {

        // GIVEN
        final String initialSelection = "notNull";
        final MovieCursorBuilderOld movieCursorBuilder = new MovieCursorBuilderOld(
                readableDatabase, null, initialSelection, null, null);

        // WHEN
        final String selection = movieCursorBuilder.setUpSelection(initialSelection);

        // THEN
        assertThat(selection).isEqualTo(initialSelection + " AND ");
    }

    public void test_buildCursor() {

        // GIVEN
        mContext.getContentResolver().bulkInsert(
                CONTENT_URI, TestUtilities.createBulkInsertMovieValues());
        final MovieCursorBuilderOld movieCursorBuilder = new MovieCursorBuilderOld(
                readableDatabase, null, null, null, null);

        // WHEN
        final Cursor cursor = movieCursorBuilder.buildCursor(null);

        // THEN
        assertThat(cursor.getCount()).isEqualTo(TestUtilities.BULK_INSERT_SIZE);
    }
*/

}
