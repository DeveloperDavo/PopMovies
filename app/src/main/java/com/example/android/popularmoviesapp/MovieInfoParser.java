package com.example.android.popularmoviesapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by davidswallow on 07/04/16.
 */
public class MovieInfoParser {

    private final static String MD_RESULTS = "results";
    private final static String MD_TITLE = "original_title";
    private final static String MD_POSTER_PATH = "poster_path";
    private final static String MD_OVERVIEW = "overview";
    private final static String MD_RATING = "vote_average";
    private final static String MD_RELEASE = "release_date";
    public final static String POSTER_URL_BASE = "http://image.tmdb.org/t/p/w185/";

    private JSONArray moviesData;
    private JSONObject movieData;

    public MovieInfoParser(String dataJsonStr, int position) throws JSONException {
        parseMoviesData(dataJsonStr);
        parseMovieData(position);
    }

    public MovieInfoParser(String dataJsonStr) throws JSONException {
        parseMoviesData(dataJsonStr);
    }

    private void parseMoviesData(String dataJsonStr) throws JSONException {
        JSONObject data = new JSONObject(dataJsonStr);
        this.moviesData = data.getJSONArray(MD_RESULTS);
    }

    private void parseMovieData(int position) throws JSONException {
        this.movieData = moviesData.getJSONObject(position);
    }

    public String[] parsePosterUrls() throws JSONException {

        int dataSize = moviesData.length();
        String[] posterUrls = new String[dataSize];

        for (int i = 0; i < dataSize; i++) {

            // get movie poster
            posterUrls[i] = moviesData.getJSONObject(i).getString(MD_POSTER_PATH);

        }

        return posterUrls;

    }

    public String parsePosterUrl() throws JSONException {
        return POSTER_URL_BASE + movieData.getString(MD_POSTER_PATH);
    }

    public String parseOriginalTitle() throws JSONException {
        return movieData.getString(MD_TITLE);

    }
    public String parseOverview() throws JSONException {
        return movieData.getString(MD_OVERVIEW);
    }

    public String parseRating() throws JSONException {
        return movieData.getString(MD_RATING);
    }

    public String parseRelease() throws JSONException {
        return movieData.getString(MD_RELEASE);
    }

}

