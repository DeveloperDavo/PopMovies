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
        final int _id = 14;

        // WHEN
        final Uri movieUri = MovieEntry.buildMovieUri(_id);

        // THEN
        assertNotNull(movieUri);
        assertEquals("Error: Uri doesn't match expected result",
                BASE_URL + PATH_MOVIES + "/" + _id,
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
        final Uri reviewUri = ReviewEntry.buildReviewUri(reviewId);

        // THEN
        assertNotNull(reviewUri);
        assertEquals("Error: Uri doesn't match expected result",
                BASE_URL + PATH_REVIEWS + "/" + reviewId,
                reviewUri.toString());
    }

    public void test_buildSingleMovie() {

        // GIVEN
        final int _id = 3;

        // WHEN
        final Uri movieUri = MovieEntry.buildMovieUri(_id);

        // THEN
        assertNotNull(movieUri);
        assertEquals("Error: Uri doesn't match expected result",
                BASE_URL + PATH_MOVIES + "/" + _id, movieUri.toString());

    }
}
