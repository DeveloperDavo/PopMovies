package com.example.android.popularmoviesapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by davidswallow on 07/04/16.
 */
public class MovieAttributes {
    private final static String MD_RESULTS = "results";
    private final static String MD_TITLE = "original_title";
    private final static String MD_POSTER_PATH = "poster_path";
    private String originalTitle;
    private String posterUrl;
    private String[] posterUrls;
    private String synopsis;
    private String userRating;
    private String releaseDate;
    private JSONArray moviesData;
    private JSONObject movieData;

    public MovieAttributes(String dataJsonStr, int position) throws JSONException {
        parseMoviesData(dataJsonStr);
        parseMovieData(position);
        parsePosterUrl();
        parseOriginalTitle();
    }

    public MovieAttributes(String dataJsonStr) throws JSONException {
        parseMoviesData(dataJsonStr);
        parsePosterUrls();
    }

    // TODO: comment
    private void parseMoviesData(String dataJsonStr) throws JSONException {
        JSONObject data = new JSONObject(dataJsonStr);
        this.moviesData = data.getJSONArray(MD_RESULTS);
    }

    // TODO: comment
    private void parseMovieData(int position) throws JSONException {
        this.movieData = moviesData.getJSONObject(position);
    }

    // TODO: comment
    private void parsePosterUrls() throws JSONException {

        int dataSize = moviesData.length();
        String[] posterUrls = new String[dataSize];

        for (int i = 0; i < dataSize; i++) {

            // get movie poster
            posterUrls[i] = moviesData.getJSONObject(i).getString(MD_POSTER_PATH);

        }

        this.posterUrls = posterUrls;
    }

    // TODO: comment
    private void parsePosterUrl() throws JSONException {
        posterUrl = movieData.getString(MD_POSTER_PATH);

    }

    // TODO: comment
    private void parseOriginalTitle() throws JSONException {
        originalTitle = movieData.getString(MD_TITLE);

    }

    public String getOriginalTitle() {
        return this.originalTitle;
    }

    public String getPosterUrl() {
        String urlBase = "http://image.tmdb.org/t/p/w185/";
        return urlBase + this.posterUrl;
    }

    public String[] getPosterUrls() {
        return posterUrls;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
