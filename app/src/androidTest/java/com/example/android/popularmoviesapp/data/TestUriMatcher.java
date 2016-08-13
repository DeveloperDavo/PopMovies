package com.example.android.popularmoviesapp.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by David on 04/08/16.
 */
public class TestUriMatcher extends AndroidTestCase {

    public void test_match_movies() {

        // GIVEN

        // content://com.example.android.popularmoviesapp/movies"
        final Uri moviesDir = MovieContract.MovieEntry.CONTENT_URI;

        // WHEN
        final UriMatcher testMatcher = MovieUriMatcher.buildUriMatcher();
        final int movies = MovieUriMatcher.MOVIES;

        // THEN
        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                testMatcher.match(moviesDir), movies);
    }

    public void test_match_reviews() {

        // GIVEN

        // content://com.example.android.popularmoviesapp/reviews"
        final Uri reviewsDir = MovieContract.ReviewEntry.CONTENT_URI;

        // WHEN
        final UriMatcher testMatcher = MovieUriMatcher.buildUriMatcher();
        final int reviews = MovieUriMatcher.REVIEWS;

        // THEN
        assertEquals("Error: The REVIEWS URI was matched incorrectly.",
                testMatcher.match(reviewsDir), reviews);
    }

    public void test_match_movie() {

        // GIVEN
        final int movieId = 565545;

        // content://com.example.android.popularmoviesapp/movies/movie_id/"
        final Uri movieItem = MovieContract.MovieEntry.buildSingleMovie(movieId);

        // WHEN
        final UriMatcher testMatcher = MovieUriMatcher.buildUriMatcher();
        final int reviews = MovieUriMatcher.MOVIE;

        // THEN
        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(movieItem), reviews);

    }

}
