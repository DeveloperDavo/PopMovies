package com.example.android.popularmoviesapp.sync;

import android.content.ContentResolver;
import android.test.AndroidTestCase;

import com.example.android.popularmoviesapp.data.TestUtilities;

import static com.example.android.popularmoviesapp.data.TestUtilities.FAVORITE;
import static com.example.android.popularmoviesapp.data.TestUtilities.MOVIE_ID;
import static com.example.android.popularmoviesapp.data.TestUtilities.OVERVIEW;
import static com.example.android.popularmoviesapp.data.TestUtilities.POPULARITY;
import static com.example.android.popularmoviesapp.data.TestUtilities.POSTER;
import static com.example.android.popularmoviesapp.data.TestUtilities.RATING;
import static com.example.android.popularmoviesapp.data.TestUtilities.RELEASE;
import static com.example.android.popularmoviesapp.data.TestUtilities.TITLE;

public class TestMovieSyncer extends AndroidTestCase {
    private static final double UPDATED_RATING = 7.4;
    private static final double UPDATED_POPULARITY = 23.19;

    private ContentResolver contentResolver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contentResolver = mContext.getContentResolver();
    }

    public void test_isMovieInDb_true() {

        // GIVEN
        MoviesSyncer moviesSyncer = (MoviesSyncer) Syncer.newInstance(contentResolver, Syncer.SOURCE_TOP_RATED);
        TestUtilities.createAndInsertMovieValues(getContext());

        // WHEN
        final boolean isMovieInDb = moviesSyncer.isMovieInDb(MOVIE_ID);

        // THEN
        assertTrue(isMovieInDb);

    }

    public void test_isMovieInDb_false() {

        // GIVEN
        MoviesSyncer moviesSyncer = (MoviesSyncer) Syncer.newInstance(contentResolver, Syncer.SOURCE_TOP_RATED);
        TestUtilities.createAndInsertMovieValues(getContext());
        final int movieId = 65;

        // WHEN
        final boolean isMovieInDb = moviesSyncer.isMovieInDb(movieId);

        // THEN
        assertFalse(isMovieInDb);

    }

    public void test_insertOrUpdate_insertsMovie() {

        // GIVEN
        MoviesSyncer moviesSyncer = (MoviesSyncer) Syncer.newInstance(contentResolver, Syncer.SOURCE_TOP_RATED);

        // WHEN
        final long movieRowId = moviesSyncer.insertOrUpdate(
                MOVIE_ID, TITLE, POSTER, OVERVIEW, RATING,
                POPULARITY, RELEASE, FAVORITE);

        // THEN
        assertFalse("Error: insertOrUpdate should not be -1", movieRowId == -1);

    }

    public void test_insertOrUpdate_updatesMovie() {

        // GIVEN
        MoviesSyncer moviesSyncer = (MoviesSyncer) Syncer.newInstance(contentResolver, Syncer.SOURCE_TOP_RATED);
        TestUtilities.createAndInsertMovieValues(getContext());
        final int expectedMoviesUpdated = 1;

        // WHEN
        final long moviesUpdated = moviesSyncer.insertOrUpdate(MOVIE_ID, TITLE, POSTER, OVERVIEW,
                UPDATED_RATING, UPDATED_POPULARITY, RELEASE, FAVORITE);

        // THEN
        assertEquals(expectedMoviesUpdated, moviesUpdated);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestUtilities.deleteAllRecordsFromProvider(mContext);
    }

}
