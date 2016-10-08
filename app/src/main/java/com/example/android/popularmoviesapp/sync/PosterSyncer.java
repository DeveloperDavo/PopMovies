package com.example.android.popularmoviesapp.sync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by David on 07/10/16.
 */
class PosterSyncer {
    private static final String LOG_TAG = PosterSyncer.class.getSimpleName();

    static final String URL_BASE = "http://image.tmdb.org/t/p/w185/";

    /**
     * Gets poster data with a http request.
     */
    static Bitmap sync(String posterPath) {

        HttpURLConnection urlConnection = null;
        Bitmap bitmap = null;

        try {
            final URL url = buildUrl(posterPath);
//            Log.d(LOG_TAG, "url: " + url);
            urlConnection = connect(url);
            bitmap = getBitmapFromInputStream(urlConnection);
        } catch (NullPointerException | IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return bitmap;

    }

    @NonNull
    static URL buildUrl(final String posterPath) throws MalformedURLException {
        final Uri builtUri = Uri.parse(URL_BASE + posterPath).buildUpon().build();

        return new URL(builtUri.toString());
    }

    @NonNull
    static HttpURLConnection connect(URL url) throws IOException {
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        return urlConnection;
    }

    static Bitmap getBitmapFromInputStream(HttpURLConnection urlConnection) throws IOException {
        final InputStream inputStream = urlConnection.getInputStream();
        return BitmapFactory.decodeStream(inputStream);
    }

}
