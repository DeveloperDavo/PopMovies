package com.example.android.popularmoviesapp.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmoviesapp.BuildConfig;
import com.example.android.popularmoviesapp.Utility;
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

    private Context context;
    private long movieKey;
    private long movieId;

    public VideoSyncer(Context context, long movieKey, long movieId) {
        this.context = context;
        this.movieKey = movieKey;
        this.movieId = movieId;
    }

    /**
     * Gets video data with a http request.
     * Parses data as a JSON string
     * and persists it.
     */
    void sync() {

        // Declared outside in order to be closed in finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            final URL url = buildUrl();
//            Log.d(LOG_TAG, "url: " + url);
            urlConnection = Utility.connect(url);
            reader = persistDataFromServer(urlConnection);
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            disconnectAndClose(urlConnection, reader);
        }
    }

    @NonNull
    URL buildUrl() throws MalformedURLException {
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
    private BufferedReader persistDataFromServer(HttpURLConnection urlConnection)
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

        parseAndPersistData(videosJsonStr);

        return reader;
    }

    void parseAndPersistData(String videosJsonStr) throws JSONException {

        final String MD_RESULTS = "results";

        final JSONObject data = new JSONObject(videosJsonStr);
        final JSONArray videos = data.getJSONArray(MD_RESULTS);

        for (int i = 0; i < videos.length(); i++) {
            parseAndPersistVideo(videos, i);
        }
    }

    private void parseAndPersistVideo(JSONArray videos, int i) throws JSONException {

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

        insertOrUpdate(id, key, site, type);
    }

    private long insertOrUpdate(String id, String key, String site, String type) {

        final Cursor videoCursor = queryVideoId(id);
        long _id = getRowIdFrom(videoCursor);

        if (-1 == _id) {
            return insert(id, key, site, type);
        } else {
            return update(_id, id, key, site, type);
        }
    }

    Cursor queryVideoId(String videoId) {

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

    long getRowIdFrom(Cursor videoCursor) {
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
    long insert(
            String id, String key, String site, String type) {

        final ContentValues videoValues = getContentValuesFrom(id, key, site, type);

        final Uri insertedUri = context.getContentResolver().insert(
                VideoEntry.CONTENT_URI, videoValues);

        return ContentUris.parseId(insertedUri);
    }

    /**
     * @return number of videos updated
     */
    int update(
            long _id, String id, String key, String site, String type) {

        final ContentValues videoValues = getContentValuesFrom(id, key, site, type);

        final String where = VideoEntry._ID + " = ?";
        final String[] selectionArgs = {Long.toString(_id)};
        return context.getContentResolver().update(
                VideoEntry.CONTENT_URI, videoValues, where, selectionArgs);
    }

    @NonNull
    private ContentValues getContentValuesFrom(String id, String key, String site, String type) {

        ContentValues videoValues = new ContentValues();

        videoValues.put(VideoEntry.COLUMN_MOVIE_KEY, movieKey);
        videoValues.put(VideoEntry.COLUMN_VIDEO_KEY, key);
        videoValues.put(VideoEntry.COLUMN_VIDEO_ID, id);
        videoValues.put(VideoEntry.COLUMN_VIDEO_SITE, site);
        videoValues.put(VideoEntry.COLUMN_VIDEO_TYPE, type);

        return videoValues;
    }

    private void disconnectAndClose(HttpURLConnection urlConnection, BufferedReader reader) {
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
