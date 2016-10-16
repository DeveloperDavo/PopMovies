package com.example.android.popularmoviesapp.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by David on 02/10/16.
 */

public class VideoSyncer extends AbstractSyncer {
    private static final String LOG_TAG = VideoSyncer.class.getSimpleName();

    @Override
    protected void parseAndPersist(JSONArray dataArray, int i) throws JSONException {

        final String MD_KEY = "key";
        final String MD_ID = "id";
        final String MD_SITE = "site";
        final String MD_TYPE = "type";

        // get data from JSON String
        final JSONObject videosData = dataArray.getJSONObject(i);
        final String id = videosData.getString(MD_ID);
        final String key = videosData.getString(MD_KEY);
        final String site = videosData.getString(MD_SITE);
        final String type = videosData.getString(MD_TYPE);

        insertOrUpdate(id, key, site, type);
    }

    private long insertOrUpdate(String id, String key, String site, String type) {

        final Cursor videoCursor = queryVideoId(id);
        long rowId = getRowIdFrom(videoCursor);

        if (-1 == rowId) {
            return insert(id, key, site, type);
        } else {
            return update(rowId, id, key, site, type);
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
        long rowId = -1;
        if (videoCursor.moveToFirst()) {
            int videoKeyIndex = videoCursor.getColumnIndex(VideoEntry._ID);
            rowId = videoCursor.getLong(videoKeyIndex);
        }
        return rowId;
    }

    /**
     * @return _ID of video being inserted
     */
    long insert(String id, String key, String site, String type) {

        final ContentValues videoValues = getContentValuesFrom(id, key, site, type);

        final Uri insertedUri = context.getContentResolver().insert(
                VideoEntry.CONTENT_URI, videoValues);

        return ContentUris.parseId(insertedUri);
    }

    /**
     * @return number of videos updated
     */
    int update(long rowId, String id, String key, String site, String type) {

        final ContentValues videoValues = getContentValuesFrom(id, key, site, type);

        final String where = VideoEntry._ID + " = ?";
        final String[] selectionArgs = {Long.toString(rowId)};
        return context.getContentResolver().update(
                VideoEntry.CONTENT_URI, videoValues, where, selectionArgs);
    }

    @NonNull
    private ContentValues getContentValuesFrom(String id, String key, String site, String type) {

        ContentValues videoValues = new ContentValues();

        videoValues.put(VideoEntry.COLUMN_MOVIE_ROW_ID, movieRowId);
        videoValues.put(VideoEntry.COLUMN_VIDEO_KEY, key);
        videoValues.put(VideoEntry.COLUMN_VIDEO_ID, id);
        videoValues.put(VideoEntry.COLUMN_VIDEO_SITE, site);
        videoValues.put(VideoEntry.COLUMN_VIDEO_TYPE, type);

        return videoValues;
    }

}
