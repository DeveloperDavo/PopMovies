package com.example.android.popularmoviesapp.sync;

import android.test.AndroidTestCase;

import com.example.android.popularmoviesapp.data.TestUtilities;

import static com.example.android.popularmoviesapp.data.TestUtilities.*;
import static com.example.android.popularmoviesapp.data.MovieContract.*;

public class TestMovieSyncer extends AndroidTestCase {
    private static final double UPDATED_RATING = 7.4;
    private static final double UPDATED_POPULARITY = 23.19;

    @Override
    protected void setUp() throws Exception {
        deleteExistingEntries();
    }

    public void test_isMovieInDb_true() {

        // GIVEN
        TestUtilities.createAndInsertMovieValues(getContext());

        // WHEN
        final boolean isMovieInDb = MoviesSyncer.isMovieInDb(getContext(), MOVIE_ID);

        // THEN
        assertTrue(isMovieInDb);

    }

    public void test_isMovieInDb_false() {

        // GIVEN
        TestUtilities.createAndInsertMovieValues(getContext());
        final int movieId = 65;

        // WHEN
        final boolean isMovieInDb = MoviesSyncer.isMovieInDb(getContext(), movieId);

        // THEN
        assertFalse(isMovieInDb);

    }

    // TODO: syncing posters is causing a null pointer exception
    public void test_insertOrUpdate_insertsMovie() {

        // WHEN
        final long movieRowId = MoviesSyncer.insertOrUpdate(getContext(),
                MOVIE_ID, TITLE, POSTER, OVERVIEW, RATING,
                POPULARITY, RELEASE, FAVORITE);

        // THEN
        assertFalse("Error: insertOrUpdate should not be -1", movieRowId == -1);

    }

    public void test_insertOrUpdate_updatesMovie() {

        // GIVEN
        TestUtilities.createAndInsertMovieValues(getContext());
        final int expectedMoviesUpdated = 1;

        // WHEN
        final long moviesUpdated = MoviesSyncer.insertOrUpdate(
                getContext(), MOVIE_ID, TITLE, POSTER, OVERVIEW,
                UPDATED_RATING, UPDATED_POPULARITY, RELEASE, FAVORITE);

        // THEN
        assertEquals(expectedMoviesUpdated, moviesUpdated);

    }

    @Override
    protected void tearDown() throws Exception {
        // reset our state back to normal
        deleteExistingEntries();

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(MovieEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }

    private void deleteExistingEntries() {
        getContext().getContentResolver().delete(MovieEntry.CONTENT_URI,
                null,
                null);
        getContext().getContentResolver().delete(ReviewEntry.CONTENT_URI,
                null,
                null);
        getContext().getContentResolver().delete(VideoEntry.CONTENT_URI,
                null,
                null);
    }

}
