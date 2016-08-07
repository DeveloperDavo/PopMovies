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
        final int movies = MovieUriMatcher.MOVIE;

        // THEN
        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(moviesDir), movies);
    }

    public void test_match_reviews() {

        // GIVEN

        // content://com.example.android.popularmoviesapp/reviews"
        final Uri reviewsDir = MovieContract.ReviewEntry.CONTENT_URI;

        // WHEN
        final UriMatcher testMatcher = MovieUriMatcher.buildUriMatcher();
        final int reviews = MovieUriMatcher.REVIEW;

        // THEN
        assertEquals("Error: The REVIEW URI was matched incorrectly.",
                testMatcher.match(reviewsDir), reviews);
    }

    public void test_match_movieWithReviews() {

        // GIVEN
        final int movieId = 565545;
        final int reviewId = 1;

        // content://com.example.android.popularmoviesapp/movies/movie_id/reviews/reviews_id"
        final Uri movieWithReviewDir = MovieContract.MovieEntry.buildReviewMovie(movieId, reviewId);

        // WHEN
        final UriMatcher testMatcher = MovieUriMatcher.buildUriMatcher();
        final int reviews = MovieUriMatcher.MOVIE_WITH_REVIEW;

        // THEN
        assertEquals("Error: The MOVIE_WITH_REVIEW URI was matched incorrectly.",
                testMatcher.match(movieWithReviewDir), reviews);

    }

}
