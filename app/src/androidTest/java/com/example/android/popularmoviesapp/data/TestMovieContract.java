package com.example.android.popularmoviesapp.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by David on 04/08/16.
 */
public class TestMovieContract extends AndroidTestCase {
    private static final long MOVIE_ID = 1001;
    private static final long REVIEW_ID = 1;

    public void test_buildMovieUri() {
        final int id = 14;
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(id);
        assertNotNull(movieUri);
        assertEquals("Error: Uri doesn't match expected result",
                "content://com.example.android.popularmovies/movies/" + id,
                movieUri.toString());
    }

    public void test_buildReviewUri() {
        final int id = 3;
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewUri(id);
        assertNotNull(reviewUri);
        assertEquals("Error: Uri doesn't match expected result",
                "content://com.example.android.popularmovies/reviews/" + id,
                reviewUri.toString());
    }

    public void test_buildMovieWithReview() {
        final long movieId = 1001;
        final long reviewId = 1;
        Uri movieWithReviewUri = MovieContract.MovieEntry.buildMovieWithReview(movieId, reviewId);
        assertNotNull(movieWithReviewUri);
        assertEquals("Error: Uri doesn't match expected result",
                "content://com.example.android.popularmovies/movies/" + movieId + "/" + reviewId,
                movieWithReviewUri.toString());
    }
}
