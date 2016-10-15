package com.example.android.popularmoviesapp.sync;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmoviesapp.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by David on 15/10/2016.
 */

public abstract class AbstractSyncer {
    private static final String LOG_TAG = VideoSyncer.class.getSimpleName();

    protected Context context;
    protected long movieKey;
    protected long movieId;
    protected String path;

    public AbstractSyncer(Context context, long movieKey, long movieId, String path) {
        this.context = context;
        this.movieKey = movieKey;
        this.movieId = movieId;
        this.path = path;
    }

    /**
     * Gets video data with a http request.
     * Parses data as a JSON string
     * and persists it.
     */
    protected void sync() {

        // Declared outside in order to be closed in finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            final URL url = buildUrl();
//            Log.d(LOG_TAG, "url: " + url);
            urlConnection = connect(url);
            reader = persistDataFromServer(urlConnection);
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            disconnectAndClose(urlConnection, reader);
        }
    }

    @NonNull
    protected URL buildUrl() throws MalformedURLException {
        // https://www.themoviedb.org/
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String API_PARAM = "api_key";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(path)
                .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        return new URL(builtUri.toString());
    }

    @NonNull
    protected HttpURLConnection connect(URL url) throws IOException {
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        return urlConnection;
    }

    @NonNull
    protected BufferedReader persistDataFromServer(HttpURLConnection urlConnection)
            throws IOException, JSONException {

        // read the input stream into a string
        final InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // new line is not necessary, but helps for debugging
            buffer.append(line + "\n");
        }

        final String jsonString = buffer.toString();
//                Log.d(LOG_TAG, "jsonString: " + jsonString);

        parseAndPersistData(jsonString);

        return reader;
    }

    protected void parseAndPersistData(String jsonStr) throws JSONException {

        final String MD_RESULTS = "results";

        final JSONObject data = new JSONObject(jsonStr);
        final JSONArray dataArray = data.getJSONArray(MD_RESULTS);

        for (int i = 0; i < dataArray.length(); i++) {
            parseAndPersist(dataArray, i);
        }
    }

    protected abstract void parseAndPersist(JSONArray videos, int i) throws JSONException;

    protected void disconnectAndClose(HttpURLConnection urlConnection, BufferedReader reader) {
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (final IOException e) {
                Log.e(LOG_TAG, "Error closing stream", e);
            }
        }
    }
}