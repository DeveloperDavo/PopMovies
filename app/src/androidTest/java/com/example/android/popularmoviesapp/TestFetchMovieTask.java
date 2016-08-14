package com.example.android.popularmoviesapp;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.android.popularmoviesapp.data.TestUtilities;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

/**
 * Created by David on 04/08/16.
 */
public class TestFetchMovieTask extends AndroidTestCase {

    /**
     * Adds a movie entry to movies.
     * Queries each column to see if it matches what was inserted.
     * Inserts the same movie entry to see if it returns the same row id.
     * At the end of the test the entry is deleted.
     * TODO needs to consider updating if id already exists
     * TODO test case has too much responsibility
     */
    @TargetApi(11)
    public void test_addMovie() {
        // start from a clean state
        getContext().getContentResolver().delete(MovieEntry.CONTENT_URI,
                MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{Long.toString(TestUtilities.MOVIE_ID)});

        final FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext());
        final long movieRowId = fetchMovieTask.addMovie(
                TestUtilities.MOVIE_ID,
                TestUtilities.TITLE,
                TestUtilities.POSTER_PATH,
                TestUtilities.OVERVIEW,
                TestUtilities.RATING,
                TestUtilities.RELEASE,
                TestUtilities.FAVORITE
        );

        assertFalse("Error: addMovie returned an invalid ID on insert",
                movieRowId == -1);

        // test all this twice
        for (int i = 0; i < 2; i++) {

            // does the ID point to our movie?
            final Cursor movieCursor = getContext().getContentResolver().query(
                    MovieEntry.CONTENT_URI,
                    new String[]{
                            MovieEntry._ID,
                            MovieEntry.COLUMN_MOVIE_ID,
                            MovieEntry.COLUMN_TITLE,
                            MovieEntry.COLUMN_POSTER_PATH,
                            MovieEntry.COLUMN_OVERVIEW,
                            MovieEntry.COLUMN_RATING,
                            MovieEntry.COLUMN_RELEASE,
                            MovieEntry.COLUMN_FAVORITE,
                    },
                    MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{Long.toString(TestUtilities.MOVIE_ID)},
                    null);

            // these match the indices of the projection
            if (movieCursor.moveToFirst()) {
                final long expectedMovieRowId = movieCursor.getLong(0);
                assertEquals("Error: the queried value of movieRowId does not " +
                        "match the returned value from addMovie", expectedMovieRowId, movieRowId);

                final long movieId = movieCursor.getLong(1);
                final String title = movieCursor.getString(2);
                final String posterPath = movieCursor.getString(3);
                final String overview = movieCursor.getString(4);
                final double rating = movieCursor.getDouble(5);
                final String release = movieCursor.getString(6);
                final int favorite = movieCursor.getInt(7);

                assertEquals("Error: the queried value of movieId is incorrect",
                        TestUtilities.MOVIE_ID, movieId);
                assertEquals("Error: the queried value of title is incorrect",
                        TestUtilities.TITLE, title);
                assertEquals("Error: the queried value of posterPath is incorrect",
                        TestUtilities.POSTER_PATH, posterPath);
                assertEquals("Error: the queried value of overview is incorrect",
                        TestUtilities.OVERVIEW, overview);
                assertEquals("Error: the queried value of rating is incorrect",
                        TestUtilities.RATING, rating);
                assertEquals("Error: the queried value of release is incorrect",
                        TestUtilities.RELEASE, release);
                assertEquals("Error: the queried value of favorite is incorrect",
                        TestUtilities.FAVORITE, favorite);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            // there should be no more records
            assertFalse("Error: there should be only one record returned from a movie query",
                    movieCursor.moveToNext());

            // add the movie again
            final long newMovieRowId = fetchMovieTask.addMovie(
                    TestUtilities.MOVIE_ID,
                    TestUtilities.TITLE,
                    TestUtilities.POSTER_PATH,
                    TestUtilities.OVERVIEW,
                    TestUtilities.RATING,
                    TestUtilities.RELEASE,
                    TestUtilities.FAVORITE
            );

            assertEquals("Error: inserting a movie again should return the same ID",
                    movieRowId, newMovieRowId);
        }

        // reset our state back to normal
        getContext().getContentResolver().delete(MovieEntry.CONTENT_URI,
                MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{Long.toString(TestUtilities.MOVIE_ID)});

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(MovieEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }
}
