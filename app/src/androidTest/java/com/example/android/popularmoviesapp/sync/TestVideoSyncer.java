package com.example.android.popularmoviesapp.sync;

import android.test.AndroidTestCase;

import com.example.android.popularmoviesapp.data.TestUtilities;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by David on 14/10/2016.
 */

public class TestVideoSyncer extends AndroidTestCase {

    private static final String ID = "abc123";
    private static final String KEY = "789xyz";
    private static final String SITE = "YouTube";
    private static final String TYPE = "Trailer";

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_PARAM = "api_key";

    private long movieRowId;

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

    public void test_insert() throws Exception {
        insertTestVideo();
    }

    private long insertTestVideo() {

        // WHEN
        long _id = VideoSyncer.insert(mContext, movieRowId, ID, KEY, SITE, TYPE);

        // THEN
        assertThat(_id).isNotEqualTo(-1);

        return _id;
    }

    public void test_update() throws Exception {

        // GIVEN
        final long _id = insertTestVideo();
        final String id = ID;
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
        super.tearDown();
    }
}
