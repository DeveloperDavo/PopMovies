package com.example.android.popularmoviesapp.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmoviesapp.BuildConfig;
import com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

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
 * Created by David on 02/10/16.
 */

public class VideoSyncer {
    private static final String LOG_TAG = VideoSyncer.class.getSimpleName();

    /**
     * Gets video data with a http request.
     * Parses data as a JSON string
     * and persists it.
     */
    static void sync(Context context, long movieKey, long movieId) {

        // Declared outside in order to be closed in finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            final URL url = buildUrl(movieId);
//            Log.d(LOG_TAG, "url: " + url);
            urlConnection = connect(url);
            reader = persistDataFromServer(context, movieKey, urlConnection);
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            disconnectAndClose(urlConnection, reader);
        }
    }

    @NonNull
    static URL buildUrl(long movieId) throws MalformedURLException {
        // https://www.themoviedb.org/
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String API_PARAM = "api_key";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath("videos")
                .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        return new URL(builtUri.toString());
    }

    @NonNull
    private static HttpURLConnection connect(URL url) throws IOException {
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        return urlConnection;
    }

    @NonNull
    private static BufferedReader persistDataFromServer(
            Context context, long movieKey, HttpURLConnection urlConnection)
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

        final String videosJsonStr = buffer.toString();
//                Log.d(LOG_TAG, "videosJsonStr: " + videosJsonStr);

        parseAndPersistData(context, videosJsonStr, movieKey);

        return reader;
    }

    static void parseAndPersistData(Context context, String videosJsonStr,
                                    long movieKey) throws JSONException {

        final String MD_RESULTS = "results";

        final JSONObject data = new JSONObject(videosJsonStr);
        final JSONArray videos = data.getJSONArray(MD_RESULTS);

        for (int i = 0; i < videos.length(); i++) {
            parseAndPersistVideo(context, movieKey, videos, i);
        }
    }

    private static void parseAndPersistVideo(
            Context context, long movieKey, JSONArray videos, int i) throws JSONException {

        final String MD_KEY = "key";
        final String MD_ID = "id";
        final String MD_SITE = "site";
        final String MD_TYPE = "type";

        // get data from JSON String
        final JSONObject videosData = videos.getJSONObject(i);
        final String id = videosData.getString(MD_ID);
        final String key = videosData.getString(MD_KEY);
        final String site = videosData.getString(MD_SITE);
        final String type = videosData.getString(MD_TYPE);

        insertOrUpdate(context, movieKey, id, key, site, type);
    }

    private static long insertOrUpdate(
            Context context, long movieKey, String id, String key, String site, String type) {

        final Cursor videoCursor = queryVideoId(context, id);
        long _id = getRowIdFrom(videoCursor);

        if (-1 == _id) {
            return insert(context, movieKey, id, key, site, type);
        } else {
            return update(context, _id, movieKey, id, key, site, type);
        }
    }

    static Cursor queryVideoId(Context context, String videoId) {

        final String[] projection = null;
        final String selection = VideoEntry.COLUMN_VIDEO_ID + " = ?";
        final String[] selectionArgs = {videoId};

        return context.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null); // sortOrder
    }

    static long getRowIdFrom(Cursor videoCursor) {
        long _id = -1;
        if (videoCursor.moveToFirst()) {
            int videoKeyIndex = videoCursor.getColumnIndex(VideoEntry._ID);
            _id = videoCursor.getLong(videoKeyIndex);
        }
        return _id;
    }

    /**
     * @return _ID of video being inserted
     */
    static long insert(
            Context context, long movieKey, String id, String key, String site, String type) {

        final ContentValues videoValues = getContentValuesFrom(movieKey, id, key, site, type);

        final Uri insertedUri = context.getContentResolver().insert(
                VideoEntry.CONTENT_URI, videoValues);

        return ContentUris.parseId(insertedUri);
    }

    /**
     * @return number of videos updated
     */
    static int update(
            Context context, long _id, long movieKey, String id, String key, String site, String type) {

        final ContentValues videoValues = getContentValuesFrom(movieKey, id, key, site, type);

        final String where = VideoEntry._ID + " = ?";
        final String[] selectionArgs = {Long.toString(_id)};
        return context.getContentResolver().update(
                VideoEntry.CONTENT_URI, videoValues, where, selectionArgs);
    }

    @NonNull
    private static ContentValues getContentValuesFrom(
            long movieKey, String id, String key, String site, String type) {

        ContentValues videoValues = new ContentValues();

        videoValues.put(VideoEntry.COLUMN_MOVIE_KEY, movieKey);
        videoValues.put(VideoEntry.COLUMN_VIDEO_KEY, key);
        videoValues.put(VideoEntry.COLUMN_VIDEO_ID, id);
        videoValues.put(VideoEntry.COLUMN_VIDEO_SITE, site);
        videoValues.put(VideoEntry.COLUMN_VIDEO_TYPE, type);

        return videoValues;
    }

    private static void disconnectAndClose(HttpURLConnection urlConnection, BufferedReader reader) {
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
