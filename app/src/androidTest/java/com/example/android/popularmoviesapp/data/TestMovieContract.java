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
        final int rowId = 14;

        // WHEN
        final Uri movieUri = MovieEntry.buildMovieUri(rowId);

        // THEN
        assertNotNull(movieUri);
        assertEquals("Error: Uri doesn't match expected result",
                BASE_URL + PATH_MOVIES + "/" + rowId,
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

    public void test_buildVideoUri() {

        // GIVEN
        final int rowId = 1;

        // WHEN
        final Uri videoUri = VideoEntry.buildVideoUri(rowId);

        // THEN
        assertNotNull(videoUri);
        assertEquals("Error: Uri doesn't match expected result",
                BASE_URL + PATH_VIDEOS + "/" + rowId,
                videoUri.toString());
    }

}
