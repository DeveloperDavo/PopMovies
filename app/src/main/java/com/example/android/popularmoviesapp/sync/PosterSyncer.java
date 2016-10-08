package com.example.android.popularmoviesapp.sync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by David on 07/10/16.
 */
class PosterSyncer {
    private static final String LOG_TAG = PosterSyncer.class.getSimpleName();

    /**
     * Gets poster data with a http request.
     * Parses data as a JSON string
     * and persists it.
     * publishes the result on the UI.
     */
    static byte[] sync(String posterPath) {

        HttpURLConnection urlConnection = null;
        Bitmap bitmap = null;

        try {
            urlConnection = connect(posterPath);
            bitmap = getBitmapFromInputStream(urlConnection);
        } catch (NullPointerException | IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return convertBitmapIntoBytes(bitmap);

    }

    @NonNull
    private static HttpURLConnection connect(String posterPath) throws IOException {
        final String POSTER_URL_BASE = "http://image.tmdb.org/t/p/w185/";
        final Uri builtUri = Uri.parse(POSTER_URL_BASE + posterPath).buildUpon() //
                .build();

        final URL url = new URL(builtUri.toString());

//            Log.d(LOG_TAG, "url: " + url);

        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        return urlConnection;
    }

    private static Bitmap getBitmapFromInputStream(HttpURLConnection urlConnection) throws IOException {
        final InputStream inputStream = urlConnection.getInputStream();
        return BitmapFactory.decodeStream(inputStream);
    }

    // http://stackoverflow.com/questions/9357668/how-to-store-image-in-sqlite-database
    private static byte[] convertBitmapIntoBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

}
