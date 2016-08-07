package com.example.android.popularmoviesapp.data;

import android.content.UriMatcher;

/**
 * Created by David on 07/08/16.
 */
public class MovieUriMatcher extends UriMatcher {

    static final int MOVIE = 100;
    static final int REVIEW = 200;
    static final int MOVIE_WITH_REVIEW = 300;

    private static final String contentAuthority = MovieContract.CONTENT_AUTHORITY;

    public MovieUriMatcher(int code) {
        super(code);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_MOVIES, MOVIE);
        uriMatcher.addURI(contentAuthority,
                MovieContract.PATH_REVIEWS, REVIEW);
        uriMatcher.addURI(contentAuthority,
                MovieContract.PATH_MOVIES + "/#/" + MovieContract.PATH_REVIEWS + "/#",
                MOVIE_WITH_REVIEW);
        return uriMatcher;
    }
}
