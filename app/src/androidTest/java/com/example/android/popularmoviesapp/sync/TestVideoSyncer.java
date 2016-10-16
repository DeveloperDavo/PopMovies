package com.example.android.popularmoviesapp.sync;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.android.popularmoviesapp.BuildConfig;
import com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;
import com.example.android.popularmoviesapp.data.TestUtilities;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by David on 14/10/2016.
 */

public class TestVideoSyncer extends AndroidTestCase {

    private static final int MOVIE_ID = 244786;

    private static final String VIDEO_ID = "54e013519251411956004b09";
    private static final String VIDEO_KEY = "fHAfUSBc0pg";
    private static final String VIDEO_SITE = "YouTube";
    private static final String VIDEO_TYPE = "Trailer";

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_PARAM = "api_key";

    private long movieRowId;

    private static final String VIDEOS_JSON_STRING = "{\n" +
            "  \"id\": " + MOVIE_ID + ",\n" +
            "  \"results\": [\n" +
            "    {\n" +
            "      \"id\": \"543d8f250e0a266f7d00059f\",\n" +
            "      \"iso_639_1\": \"en\",\n" +
            "      \"iso_3166_1\": \"US\",\n" +
            "      \"key\": \"7d_jQycdQGo\",\n" +
            "      \"name\": \"Whiplash trailer\",\n" +
            "      \"site\": \"" + VIDEO_SITE + "\",\n" +
            "      \"size\": 360,\n" +
            "      \"type\": \"" + VIDEO_TYPE + "\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"" + VIDEO_ID + "\",\n" +
            "      \"iso_639_1\": \"en\",\n" +
            "      \"iso_3166_1\": \"US\",\n" +
            "      \"key\": \"" + VIDEO_KEY + "\",\n" +
            "      \"name\": \"TV Promo\",\n" +
            "      \"site\": \"" + VIDEO_SITE + "\",\n" +
            "      \"size\": 720,\n" +
            "      \"type\": \"" + VIDEO_TYPE + "\"\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        movieRowId = TestUtilities.insertTestMovie(mContext);
    }

    public void test_buildUrl() throws Exception {

        // GIVEN
        final VideoSyncer videoSyncer = (VideoSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_VIDEOS);

        final String expectedUrl = BASE_URL
                + TestUtilities.MOVIE_ID
                + "/" + AbstractSyncer.SOURCE_VIDEOS + "?"
                + API_PARAM + "=" + BuildConfig.MOVIE_DB_API_KEY;

        // WHEN
        final URL url = videoSyncer.buildUrl();

        // THEN
        assertThat(url.toString()).isEqualTo(expectedUrl);
    }

    public void test_parseAndPersistData() throws Exception {

        // GIVEN
        final VideoSyncer videoSyncer = (VideoSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_VIDEOS);

        // WHEN
        videoSyncer.parseAndPersistData(VIDEOS_JSON_STRING);
        final Cursor cursor = videoSyncer.queryVideoId(VIDEO_ID);
        boolean isCursor = cursor.moveToLast();

        // THEN
        assertThat(isCursor).isTrue();
        assertThat(cursor.getLong(cursor.getColumnIndex(VideoEntry.COLUMN_MOVIE_ROW_ID)))
                .isEqualTo(movieRowId);
        assertThat(cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_VIDEO_ID)))
                .isEqualTo(VIDEO_ID);
        assertThat(cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_VIDEO_KEY)))
                .isEqualTo(VIDEO_KEY);
        assertThat(cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_VIDEO_SITE)))
                .isEqualTo(VIDEO_SITE);
        assertThat(cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_VIDEO_TYPE)))
                .isEqualTo(VIDEO_TYPE);

    }

    public void test_queryVideoId_withExistingVideo() throws Exception {

        // GIVEN
        final VideoSyncer videoSyncer = (VideoSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_VIDEOS);
        insertTestVideo(videoSyncer);

        // WHEN
        final Cursor cursor = videoSyncer.queryVideoId(VIDEO_ID);
        long videosUpdated = videoSyncer.getRowIdFrom(cursor);

        // THEN
        assertThat(videosUpdated).isNotEqualTo(-1);
    }

    public void test_queryVideoId_withNewVideo() throws Exception {

        // GIVEN
        final VideoSyncer videoSyncer = (VideoSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_VIDEOS);
        final String id = "fgh456"; // this id should never be inserted

        // WHEN
        final Cursor cursor = videoSyncer.queryVideoId(id);
        long rowId = videoSyncer.getRowIdFrom(cursor);

        // THEN
        assertThat(rowId).isEqualTo(-1);
    }

    public void test_insert() throws Exception {

        // GIVEN
        final VideoSyncer videoSyncer = (VideoSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_VIDEOS);

        // WHEN and THEN
        insertTestVideo(videoSyncer);
    }

    private long insertTestVideo(VideoSyncer videoSyncer) {

        // WHEN
        long rowId = videoSyncer.insert(VIDEO_ID, VIDEO_KEY, VIDEO_SITE, VIDEO_TYPE);

        // THEN
        assertThat(rowId).isNotEqualTo(-1);

        return rowId;
    }

    public void test_update() throws Exception {

        // GIVEN
        final VideoSyncer videoSyncer = (VideoSyncer) AbstractSyncer.newInstance(
                mContext, movieRowId, TestUtilities.MOVIE_ID, AbstractSyncer.SOURCE_VIDEOS);

        final long rowID = insertTestVideo(videoSyncer);

        // WHEN
        final long videosUpdated = videoSyncer.update(rowID, VIDEO_ID, VIDEO_KEY, VIDEO_SITE, VIDEO_TYPE);

        // THEN
        assertThat(videosUpdated).isEqualTo(1);
    }

    @Override
    protected void tearDown() throws Exception {
        TestUtilities.deleteAllRecordsFromProvider(mContext);
    }


}
