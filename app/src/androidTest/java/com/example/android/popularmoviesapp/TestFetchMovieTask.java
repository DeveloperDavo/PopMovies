package com.example.android.popularmoviesapp;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;
import com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;
import static com.example.android.popularmoviesapp.data.TestUtilities.FAVORITE;
import static com.example.android.popularmoviesapp.data.TestUtilities.MOVIE_ID;
import static com.example.android.popularmoviesapp.data.TestUtilities.OVERVIEW;
import static com.example.android.popularmoviesapp.data.TestUtilities.POPULARITY;
import static com.example.android.popularmoviesapp.data.TestUtilities.POSTER_PATH;
import static com.example.android.popularmoviesapp.data.TestUtilities.RATING;
import static com.example.android.popularmoviesapp.data.TestUtilities.RELEASE;
import static com.example.android.popularmoviesapp.data.TestUtilities.TITLE;

/**
 * Created by David on 04/08/16.
 */
public class TestFetchMovieTask extends AndroidTestCase {
    public static final double RATING_TO_BE_REPLACED = 7.4;
    public static final double POPULARITY_TO_BE_REPLACED = 23.19;

    private FetchMovieTask fetchMovieTask;

    @Override
    protected void setUp() throws Exception {
        deleteExistingEntries();
        fetchMovieTask = new FetchMovieTask(getContext());
        fetchMovieTask.insertOrUpdate(MOVIE_ID + 1, TITLE, POSTER_PATH, OVERVIEW, RATING - 1,
                POPULARITY + 1000, RELEASE, FAVORITE);
    }

    @TargetApi(11)
    // TODO: separate into separate test cases
    public void test_addMovie() {

        final long movieRowId = test_addMovie_returnsMovieRowIdUponSuccessfulInsert();

        test_queryMovie_hasSameId(movieRowId);
        test_queryMovie_hasInitialData();
        test_addMovie_updatesOneMovie();
        test_queryMovie_hasUpdatedData();

    }

    private long test_addMovie_returnsMovieRowIdUponSuccessfulInsert() {

        // WHEN
        final long movieRowId = fetchMovieTask.insertOrUpdate(
                MOVIE_ID, TITLE, POSTER_PATH, OVERVIEW, RATING_TO_BE_REPLACED,
                POPULARITY_TO_BE_REPLACED, RELEASE, FAVORITE);

        // THEN
        assertFalse("Error: insertOrUpdate should not be -1", movieRowId == -1);

        return movieRowId;
    }

    private void test_queryMovie_hasSameId(long movieRowId) {

        // GIVEN
        final Cursor movieCursor = queryMovie();

        if (movieCursor.moveToFirst()) {
            // WHEN
            final long expectedMovieRowId = movieCursor.getLong(0);

            // THEN
            assertEquals("Error: the queried value of movieRowId does not " +
                    "match the returned value from insertOrUpdate", expectedMovieRowId, movieRowId);
        } else {
            fail("Error: the id you used to queryMovie returned an empty cursor");
        }

    }

    private void test_queryMovie_hasInitialData() {
        test_queryMovie_hasCorrectData(MOVIE_ID, TITLE, POSTER_PATH, OVERVIEW,
                RATING_TO_BE_REPLACED, POPULARITY_TO_BE_REPLACED, RELEASE, FAVORITE);
    }

    private void test_addMovie_updatesOneMovie() {

        // GIVEN
        final int expected = 1;

        // WHEN
        final long moviesUpdated = fetchMovieTask.insertOrUpdate(MOVIE_ID, TITLE, POSTER_PATH, OVERVIEW,
                RATING, POPULARITY, RELEASE, FAVORITE);

        // THEN
        assertEquals("Error: adding the same movie again should only update one entry",
                expected, moviesUpdated);

    }

    private void test_queryMovie_hasUpdatedData() {
        test_queryMovie_hasCorrectData(MOVIE_ID, TITLE, POSTER_PATH, OVERVIEW,
                RATING, POPULARITY, RELEASE, FAVORITE);
    }

    private void test_queryMovie_hasCorrectData(
            long expectedMovieId, String expectedTitle, String expectedPosterPath,
            String expectedOverview, double expectedRating,
            double expectedPopularity, String expectedRelease, int expectedFavorite) {

        // GIVEN
        final Cursor movieCursor = queryMovie();

        if (movieCursor.moveToFirst()) {

            // WHEN
            final long movieId = movieCursor.getLong(1);
            final String title = movieCursor.getString(2);
            final String posterPath = movieCursor.getString(3);
            final String overview = movieCursor.getString(4);
            final double rating = movieCursor.getDouble(5);
            final double popularity = movieCursor.getDouble(6);
            final String release = movieCursor.getString(7);
            final int favorite = movieCursor.getInt(8);

            // THEN
            assertEquals("Error: the queried value of movieId is incorrect", expectedMovieId, movieId);
            assertEquals("Error: the queried value of title is incorrect", expectedTitle, title);
            assertEquals("Error: the queried value of posterPath is incorrect", expectedPosterPath, posterPath);
            assertEquals("Error: the queried value of overview is incorrect", expectedOverview, overview);
            assertEquals("Error: the queried value of rating is incorrect", expectedRating, rating);
            assertEquals("Error: the queried value of popularity is incorrect", expectedPopularity, popularity);
            assertEquals("Error: the queried value of release is incorrect", expectedRelease, release);
            assertEquals("Error: the queried value of favorite is incorrect", expectedFavorite, favorite);

            assertFalse("Error: there should be only one record returned from a movie queryMovie",
                    movieCursor.moveToNext());
        } else {
            fail("Error: the id you used to queryMovie returned an empty cursor");
        }
    }

    private Cursor queryMovie() {
        final String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_MOVIE_ID,
                MovieEntry.COLUMN_TITLE,
                MovieEntry.COLUMN_POSTER_PATH,
                MovieEntry.COLUMN_OVERVIEW,
                MovieEntry.COLUMN_RATING,
                MovieEntry.COLUMN_POPULARITY,
                MovieEntry.COLUMN_RELEASE,
                MovieEntry.COLUMN_FAVORITE,
        };
        final String selection = MovieEntry.COLUMN_MOVIE_ID + " = ?";
        final String[] selectionArgs = {Long.toString(MOVIE_ID)};

        return getContext().getContentResolver().query(
                MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
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
