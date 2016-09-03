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
        final int movies = MovieUriMatcher.MOVIES_CODE;

        // THEN
        assertEquals("Error: The MOVIES_CODE URI was matched incorrectly.",
                testMatcher.match(moviesDir), movies);
    }

    public void test_match_reviews() {

        // GIVEN

        // content://com.example.android.popularmoviesapp/reviews"
        final Uri reviewsDir = MovieContract.ReviewEntry.CONTENT_URI;

        // WHEN
        final UriMatcher testMatcher = MovieUriMatcher.buildUriMatcher();
        final int reviews = MovieUriMatcher.REVIEWS_CODE;

        // THEN
        assertEquals("Error: The REVIEWS_CODE URI was matched incorrectly.",
                testMatcher.match(reviewsDir), reviews);
    }

    // TODO test_match_videos

    public void test_match_movie() {

        // GIVEN
        final int _id = 17;

        // content://com.example.android.popularmoviesapp/movies/_id/"
        final Uri movieItem = MovieContract.MovieEntry.buildMovieUri(_id);

        // WHEN
        final UriMatcher testMatcher = MovieUriMatcher.buildUriMatcher();
        final int singleMovie = MovieUriMatcher.SINGLE_MOVIE_CODE;

        // THEN
        assertEquals("Error: The SINGLE_MOVIE_CODE URI was matched incorrectly.",
                testMatcher.match(movieItem), singleMovie);

    }

}
