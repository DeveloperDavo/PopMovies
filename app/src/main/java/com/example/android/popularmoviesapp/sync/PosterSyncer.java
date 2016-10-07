package com.example.android.popularmoviesapp.sync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
            Uri builtUri = Uri.parse(posterPath).buildUpon().build();

            URL url = new URL(builtUri.toString());

//            Log.d(LOG_TAG, "url: " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // nothing to do.
            }
            bitmap = BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return getBitmapAsByteArray(bitmap);

    }

    // http://stackoverflow.com/questions/9357668/how-to-store-image-in-sqlite-database
    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();

    }

}
