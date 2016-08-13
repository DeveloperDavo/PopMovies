package com.example.android.popularmoviesapp.data;

import android.content.UriMatcher;

/**
 * Created by David on 07/08/16.
 */
public class MovieUriMatcher extends UriMatcher {

    static final int MOVIES = 100;
    static final int REVIEWS = 200;
    static final int MOVIE = 300;

    private static final String contentAuthority = MovieContract.CONTENT_AUTHORITY;

    public MovieUriMatcher(int code) {
        super(code);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(contentAuthority,
                MovieContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_MOVIES + "/#", MOVIE);
//        uriMatcher.addURI(contentAuthority,
//                MovieContract.PATH_MOVIES + "/#/" + MovieContract.PATH_REVIEWS + "/#",
//                MOVIE);
        return uriMatcher;
    }
}
