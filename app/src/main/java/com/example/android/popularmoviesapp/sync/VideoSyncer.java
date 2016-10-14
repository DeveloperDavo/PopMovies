package com.example.android.popularmoviesapp.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmoviesapp.BuildConfig;
import com.example.android.popularmoviesapp.data.MovieContract;
import com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
     * publishes the result on the UI.
     */
    static void syncVideos(Context context, long movieKey, long movieId) {

        // Declared outside in order to be closed in finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String videosJsonStr;

        try {
            // https://www.themoviedb.org/
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_PARAM = "api_key";

            // TODO: the only difference compared to syncMovies
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(String.valueOf(movieId))
                    .appendPath("videos")
                    .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

//            Log.d(LOG_TAG, "url: " + url);

            // create the request to TMDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // read the input stream into a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // nothing to do.
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // new line is not necessary, but helps for debugging
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // stream was empty
            }
            videosJsonStr = buffer.toString();
//                Log.d(LOG_TAG, "videosJsonStr: " + videosJsonStr);
            parseAndPersistVideosData(context, videosJsonStr, movieKey);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // no movie data found
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
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

    private static void parseAndPersistVideosData(Context context, String videosJsonStr,
                                                  long movieKey)
            throws JSONException {

        final String MD_RESULTS = "results";
        final String MD_KEY = "key";
        final String MD_ID = "id";
        final String MD_SITE = "site";
        final String MD_TYPE = "type";

        final JSONObject data = new JSONObject(videosJsonStr);
        final JSONArray videos = data.getJSONArray(MD_RESULTS);

        for (int i = 0; i < videos.length(); i++) {

            // get data from JSON String
            final JSONObject videosData = videos.getJSONObject(i);
            final String id = videosData.getString(MD_ID);
            final String key = videosData.getString(MD_KEY);
            final String site = videosData.getString(MD_SITE);
            final String type = videosData.getString(MD_TYPE);

            insertOrUpdate(context, id, movieKey, key, site, type);

        }
    }

    /**
     * @return video row id upon insert and number of rows updated upon update
     */
    static long insertOrUpdate(Context context, String id, long movieKey, String key,
                               String site, String type) {

        long videoRowId = checkIfVideoIdExists(context, id);

        if (videoRowId == -1) {
            return insertVideo(context, id, movieKey, key, site, type);
        } else {
            return updateVideo(context, id, key, site, type);
        }
    }

    private static long checkIfVideoIdExists(Context context, String videoId) {
        long videoRowId = -1;

        final String[] projection = {VideoEntry._ID};
        final String selection = VideoEntry.COLUMN_VIDEO_ID + " = ?";
        final String[] selectionArgs = {videoId};

        final Cursor videoCursor = context.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null); // sortOrder

        if (videoCursor.moveToFirst()) {
            int videoKeyIndex = videoCursor.getColumnIndex(VideoEntry._ID);
            videoRowId = videoCursor.getLong(videoKeyIndex);
        }

        videoCursor.close();
        return videoRowId;
    }

    private static long insertVideo(Context context, String id, long movieKey, String key, String site, String type) {

        long videoRowId;
        ContentValues videoValues = new ContentValues();

        videoValues.put(VideoEntry.COLUMN_MOVIE_KEY, movieKey );
        videoValues.put(VideoEntry.COLUMN_VIDEO_KEY, key);
        videoValues.put(VideoEntry.COLUMN_VIDEO_ID, id);
        videoValues.put(VideoEntry.COLUMN_VIDEO_SITE, site);
        videoValues.put(VideoEntry.COLUMN_VIDEO_TYPE, type);

        final Uri insertedUri = context.getContentResolver().insert(
                VideoEntry.CONTENT_URI, videoValues);

        // extract videoRowId from URI
        videoRowId = ContentUris.parseId(insertedUri);
        return videoRowId;
    }

    private static long updateVideo(Context context, String id, String key, String site, String type) {

        ContentValues videoValues = new ContentValues();

        videoValues.put(VideoEntry.COLUMN_VIDEO_KEY, key);
        videoValues.put(VideoEntry.COLUMN_VIDEO_SITE, site);
        videoValues.put(VideoEntry.COLUMN_VIDEO_TYPE, type);

        final String where = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
        final String[] selectionArgs = {id};
        return context.getContentResolver().update(
                VideoEntry.CONTENT_URI, videoValues, where, selectionArgs);
    }
}
