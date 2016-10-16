package com.example.android.popularmoviesapp.sync;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.android.popularmoviesapp.BuildConfig;
import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;
import com.example.android.popularmoviesapp.data.TestUtilities;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by David on 15/10/2016.
 */

public class TestReviewSyncer extends AndroidTestCase {

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_PARAM = "api_key";

    private long movieRowId;

    public static final int MOVIE_ID = 244786;
    public static final String REVIEW_ID = "56ab260cc3a3681c54001f8a";
    public static final String REVIEW_AUTHOR = "Andres Gomez";
    public static final String REVIEW_CONTENT = "Fantastic movie with...";
    public static final String REVIEW_URL = "https://www.themoviedb.org/review/" + REVIEW_ID;

    private static final String REVIEWS_JSON_STRING = "{\n" +
            "  \"id\": " + MOVIE_ID + ",\n" +
            "  \"page\": 1,\n" +
            "  \"results\": [\n" +
            "    {\n" +
            "      \"id\": \"54c82e03c3a36870ba000a3d\",\n" +
            "      \"author\": \"MJM\",\n" +
            "      \"content\": \"DISGUSTING NONSENSE...\",\n" +
            "      \"url\": \"" + REVIEW_URL + "54c82e03c3a36870ba000a3d\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"" + REVIEW_ID + "\",\n" +
            "      \"author\": \"" + REVIEW_AUTHOR + "\",\n" +
            "      \"content\": \"" + REVIEW_CONTENT + "\",\n" +
            "      \"url\": \"" + REVIEW_URL + "\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"total_pages\": 1,\n" +
            "  \"total_results\": 2\n" +
            "}";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        movieRowId = TestUtilities.insertTestMovie(mContext);
    }

    public void test_buildUrl() throws Exception {

        // GIVEN
        final ReviewSyncer reviewSyncer = (ReviewSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_REVIEWS);

        final String expectedUrl = BASE_URL
                + TestUtilities.MOVIE_ID
                + "/" + AbstractSyncer.SOURCE_REVIEWS + "?"
                + API_PARAM + "=" + BuildConfig.MOVIE_DB_API_KEY;

        // WHEN
        final URL url = reviewSyncer.buildUrl();

        // THEN
        assertThat(url.toString()).isEqualTo(expectedUrl);
    }

    public void test_parseAndPersistData() throws Exception {

        // GIVEN
        final ReviewSyncer reviewSyncer = (ReviewSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_REVIEWS);

        // WHEN
        reviewSyncer.parseAndPersistData(REVIEWS_JSON_STRING);
        final Cursor cursor = reviewSyncer.queryReviewId(REVIEW_ID);
        boolean isCursor = cursor.moveToLast();

        // THEN
        assertThat(isCursor).isTrue();
        assertThat(cursor.getLong(cursor.getColumnIndex(ReviewEntry.COLUMN_MOVIE_ROW_ID)))
                .isEqualTo(movieRowId);
        assertThat(cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_REVIEW_ID)))
                .isEqualTo(REVIEW_ID);
        assertThat(cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_AUTHOR)))
                .isEqualTo(REVIEW_AUTHOR);
        assertThat(cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_CONTENT)))
                .isEqualTo(REVIEW_CONTENT);
        assertThat(cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_URL)))
                .isEqualTo(REVIEW_URL);

    }

    public void test_queryReview_withExistingReview() throws Exception {

        // GIVEN
        final ReviewSyncer reviewSyncer = (ReviewSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_REVIEWS);
        insertTestVideo(reviewSyncer);

        // WHEN
        final Cursor cursor = reviewSyncer.queryReviewId(REVIEW_ID);
        long reviewsUpdated = reviewSyncer.getRowIdFrom(cursor);

        // THEN
        assertThat(reviewsUpdated).isNotEqualTo(-1);
    }

    public void test_queryVideoId_withNewVideo() throws Exception {

        // GIVEN
        final ReviewSyncer reviewSyncer = (ReviewSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_REVIEWS);
        final String id = "fgh456"; // this id should never be inserted

        // WHEN
        final Cursor cursor = reviewSyncer.queryReviewId(REVIEW_ID);
        long rowId = reviewSyncer.getRowIdFrom(cursor);

        // THEN
        assertThat(rowId).isEqualTo(-1);
    }

    public void test_insert() throws Exception {

        // GIVEN
        final ReviewSyncer reviewSyncer = (ReviewSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_REVIEWS);

        // WHEN and THEN
        insertTestVideo(reviewSyncer);
    }

    private long insertTestVideo(ReviewSyncer reviewSyncer) {

        // WHEN
        long rowId = reviewSyncer.insert(REVIEW_ID, REVIEW_AUTHOR, REVIEW_CONTENT, REVIEW_URL);

        // THEN
        assertThat(rowId).isNotEqualTo(-1);

        return rowId;
    }

    public void test_update() throws Exception {

        // GIVEN
        final ReviewSyncer reviewSyncer = (ReviewSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_REVIEWS);
        final long rowId = insertTestVideo(reviewSyncer);

        // WHEN
        final long reviewsUpdated = reviewSyncer.update(rowId, REVIEW_ID, REVIEW_AUTHOR, REVIEW_CONTENT, REVIEW_URL);

        // THEN
        assertThat(reviewsUpdated).isEqualTo(1);
    }

    @Override
    protected void tearDown() throws Exception {
        TestUtilities.deleteAllRecordsFromProvider(mContext);
    }

}
