package com.example.android.popularmoviesapp.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import static com.example.android.popularmoviesapp.data.MovieContract.*;

/**
 * Created by David on 04/08/16.
 */
public class TestMovieContract extends AndroidTestCase {

    private static final String BASE_URL = "content://com.example.android.popularmoviesapp/";

    public void test_buildMovieUri() {

        // GIVEN
        final int id = 14;

        // WHEN
        final Uri movieUri = MovieEntry.buildMovieUri(id);

        // THEN
        assertNotNull(movieUri);
        assertEquals("Error: Uri doesn't match expected result",
                BASE_URL + PATH_MOVIES + "/" + id,
                movieUri.toString());
    }

    public void test_buildReviewUri() {

        // GIVEN
        final int id = 3;

        // WHEN
        final Uri reviewUri = ReviewEntry.buildReviewUri(id);

        // THEN
        assertNotNull(reviewUri);
        assertEquals("Error: Uri doesn't match expected result",
                BASE_URL + PATH_REVIEWS + "/" + id,
                reviewUri.toString());
    }

    public void test_buildSingleReview() {

        // GIVEN
        final int reviewId = 10;

        // WHEN
        final Uri reviewUri = ReviewEntry.buildSingleReviewUri(reviewId);

        // THEN
        assertNotNull(reviewUri);
        assertEquals("Error: Uri doesn't match expected result",
                BASE_URL + PATH_REVIEWS + "/" + reviewId,
                reviewUri.toString());
    }

    public void test_buildSingleMovie() {

        // GIVEN
        final int movieId = 3;

        // WHEN
        final Uri movieUri = MovieEntry.buildSingleMovieUri(movieId);

        // THEN
        assertNotNull(movieUri);
        assertEquals("Error: Uri doesn't match expected result",
                BASE_URL + PATH_MOVIES + "/" + movieId, movieUri.toString());

    }
}
