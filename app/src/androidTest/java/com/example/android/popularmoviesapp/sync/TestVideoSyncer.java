package com.example.android.popularmoviesapp.sync;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.android.popularmoviesapp.BuildConfig;
import com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;
import com.example.android.popularmoviesapp.data.TestUtilities;

import java.net.URL;

import static com.example.android.popularmoviesapp.sync.VideoSyncer.queryVideoId;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by David on 14/10/2016.
 */

public class TestVideoSyncer extends AndroidTestCase {

    public static final String VIDEO_ID = "54e013519251411956004b09";
    public static final String VIDEO_KEY = "fHAfUSBc0pg";
    public static final String VIDEO_SITE = "YouTube";
    public static final String VIDEO_TYPE = "Trailer";

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_PARAM = "api_key";

    private long movieRowId;

    private static final String VIDEOS_JSON_STRING = "{\n" +
            "  \"id\": 244786,\n" +
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
        final long movieId = 128;
        final String expectedUrl = BASE_URL
                + movieId
                + "/" + "videos" + "?"
                + API_PARAM + "=" + BuildConfig.MOVIE_DB_API_KEY;

        // WHEN
        final URL url = VideoSyncer.buildUrl(movieId);

        // THEN
        assertThat(url.toString()).isEqualTo(expectedUrl);
    }

    public void test_parseAndPersistData() throws Exception {

        // WHEN
        VideoSyncer.parseAndPersistData(mContext, VIDEOS_JSON_STRING, movieRowId);
        final Cursor cursor = queryVideoId(mContext, VIDEO_ID);
        boolean isCursor = cursor.moveToLast();

        // THEN
        assertThat(isCursor).isTrue();
        assertThat(cursor.getLong(cursor.getColumnIndex(VideoEntry.COLUMN_MOVIE_KEY)))
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
        insertTestVideo();

        // WHEN
        final Cursor cursor = VideoSyncer.queryVideoId(mContext, VIDEO_ID);
        long videosUpdated = VideoSyncer.getRowIdFrom(cursor);

        // THEN
        assertThat(videosUpdated).isNotEqualTo(-1);
    }

    public void test_queryVideoId_withNewVideo() throws Exception {

        // GIVEN
        final String id = "fgh456"; // this id should never be inserted

        // WHEN
        final Cursor cursor = VideoSyncer.queryVideoId(mContext, VIDEO_ID);
        long _id = VideoSyncer.getRowIdFrom(cursor);

        // THEN
        assertThat(_id).isEqualTo(-1);
    }

    public void test_insert() throws Exception {
        insertTestVideo();
    }

    private long insertTestVideo() {

        // WHEN
        long _id = VideoSyncer.insert(mContext, movieRowId, VIDEO_ID, VIDEO_KEY, VIDEO_SITE, VIDEO_TYPE);

        // THEN
        assertThat(_id).isNotEqualTo(-1);

        return _id;
    }

    public void test_update() throws Exception {

        // GIVEN
        final long _id = insertTestVideo();
        final String id = VIDEO_ID;
        final String key = null;
        final String site = null;
        final String type = null;

        // WHEN
        final long videosUpdated = VideoSyncer.update(mContext, _id, movieRowId, id, key, site, type);

        // THEN
        assertThat(videosUpdated).isEqualTo(1);
    }

    @Override
    protected void tearDown() throws Exception {
        TestUtilities.deleteAllRecordsFromProvider(mContext);
    }
}
