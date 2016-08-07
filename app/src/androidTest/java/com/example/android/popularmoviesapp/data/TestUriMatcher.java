package com.example.android.popularmoviesapp.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by David on 04/08/16.
 */
public class TestUriMatcher extends AndroidTestCase {

    public void test_match_whenMoviesDir() {

        // GIVEN

        // content://com.example.android.popularmovies/movies"
        final Uri moviesDir = MovieContract.MovieEntry.CONTENT_URI;

        // WHEN
        final UriMatcher testMatcher = MovieUriMatcher.buildUriMatcher();
        final int movies = MovieUriMatcher.MOVIES;

        // THEN
        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                testMatcher.match(moviesDir), movies);
    }

    public void test_match_whenReviewsDir() {

        // GIVEN

        // content://com.example.android.popularmovies/reviews"
        final Uri reviewsDir = MovieContract.ReviewEntry.CONTENT_URI;

        // WHEN
        final UriMatcher testMatcher = MovieUriMatcher.buildUriMatcher();
        final int reviews = MovieUriMatcher.REVIEWS;

        // THEN
        assertEquals("Error: The REVIEWS URI was matched incorrectly.",
                testMatcher.match(reviewsDir), reviews);
    }

    public void test_match_whenMoviesWithReviewsDir() {

        // GIVEN
        final long movieID = 1001;

        // content://com.example.android.popularmovies/movies/movie_id/reviews"
        final Uri movieWithReviewsDir =
                MovieContract.MovieEntry.buildMovieWithReviews(movieID);

        // WHEN
        final UriMatcher testMatcher = MovieUriMatcher.buildUriMatcher();
        final int movieWithReviews = MovieUriMatcher.MOVIE_WITH_REVIEWS;

        // THEN
        assertEquals("Error: The MOVIE WITH REVIEWS URI was matched incorrectly.",
                testMatcher.match(movieWithReviewsDir), movieWithReviews);
    }


}
