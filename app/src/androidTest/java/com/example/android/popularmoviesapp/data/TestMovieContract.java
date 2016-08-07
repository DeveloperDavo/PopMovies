package com.example.android.popularmoviesapp.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by David on 04/08/16.
 */
public class TestMovieContract extends AndroidTestCase {

    public void test_buildMovieUri() {

        // GIVEN
        final int id = 14;

        // WHEN
        final Uri movieUri = MovieContract.MovieEntry.buildMovieUri(id);

        // THEN
        assertNotNull(movieUri);
        assertEquals("Error: Uri doesn't match expected result",
                "content://com.example.android.popularmovies/" +
                        MovieContract.PATH_MOVIES + "/" + id,
                movieUri.toString());
    }

    public void test_buildReviewUri() {

        // GIVEN
        final int id = 3;

        // WHEN
        final Uri reviewUri = MovieContract.ReviewEntry.buildReviewUri(id);

        // THEN
        assertNotNull(reviewUri);
        assertEquals("Error: Uri doesn't match expected result",
                "content://com.example.android.popularmovies/" +
                        MovieContract.PATH_REVIEWS + "/" + id,
                reviewUri.toString());
    }

    public void test_buildMovieWithReviews() {

        // GIVEN
        final long movieId = 17;

        // WHEN
        final Uri movieWithReviewUri = MovieContract.MovieEntry.buildMovieWithReviews(movieId);

        // THEN
        assertNotNull(movieWithReviewUri);
        assertEquals("Error: Uri doesn't match expected result",
                "content://com.example.android.popularmovies/" +
                        MovieContract.PATH_MOVIES + "/" + movieId + "/" +
                        MovieContract.PATH_REVIEWS,
                movieWithReviewUri.toString());
    }

    public void test_buildMovieWithReview() {

        // GIVEN
        final long movieId = 1001;
        final long reviewId = 1;

        // WHEN
        final Uri movieWithReviewUri =
                MovieContract.MovieEntry.buildMovieWithReview(movieId, reviewId);

        // THEN
        assertNotNull(movieWithReviewUri);
        assertEquals("Error: Uri doesn't match expected result",
                "content://com.example.android.popularmovies/" +
                        MovieContract.PATH_MOVIES + "/" + movieId + "/" +
                        MovieContract.PATH_REVIEWS + "/" + reviewId,
                movieWithReviewUri.toString());
    }
}
