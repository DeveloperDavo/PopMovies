package com.example.android.popularmoviesapp.sync;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.android.popularmoviesapp.sync.PosterSyncer.URL_BASE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by David on 08/10/16.
 */
public class TestPosterSyncer extends AndroidTestCase {

    private static final String POSTER_PATH = "9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg";

    public void test_sync() throws IOException {

        // GIVEN
        final Bitmap expectedBitmap = getBitmapFromInputStream();

        // WHEN
        final Bitmap actualBitmap = PosterSyncer.sync(POSTER_PATH);

        // THEN
        assertThat(actualBitmap).isNotNull();
        assertThat(actualBitmap.getByteCount()).isEqualTo(expectedBitmap.getByteCount());

    }

    public void test_getBitmapFromInputStream() throws IOException {
        getBitmapFromInputStream();
    }

    private Bitmap getBitmapFromInputStream() throws IOException {

        // GIVEN
        final HttpURLConnection urlConnection = connect();

        // WHEN
        final Bitmap bitmap = PosterSyncer.getBitmapFromInputStream(urlConnection);

        // GIVEN
        assertThat(bitmap).isNotNull();

        return bitmap;
    }

    public void test_connect() throws IOException {
        connect();
    }

    private HttpURLConnection connect() throws IOException {

        // GIVEN
        final URL url = buildUrl();

        // WHEN
        final HttpURLConnection urlConnection = PosterSyncer.connect(url);

        // THEN
        assertThat(urlConnection.getResponseMessage()).isEqualTo("OK");

        return urlConnection;
    }

    public void test_buildUrl() throws MalformedURLException {
        buildUrl();
    }

    private URL buildUrl() throws MalformedURLException {

        // WHEN
        final URL url = PosterSyncer.buildUrl(POSTER_PATH);

        // THEN
        assertThat(url.toString()).isEqualTo(URL_BASE + POSTER_PATH);

        return url;
    }
}
