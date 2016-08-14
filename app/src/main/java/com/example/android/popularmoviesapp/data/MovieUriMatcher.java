package com.example.android.popularmoviesapp.data;

import android.content.UriMatcher;

/**
 * Created by David on 07/08/16.
 * Adds and matches URIs
 */
public class MovieUriMatcher extends UriMatcher {

    static final int MOVIES_CODE = 100;
    static final int REVIEWS_CODE = 200;
    static final int SINGLE_MOVIE_CODE = 300;

    private static final String contentAuthority = MovieContract.CONTENT_AUTHORITY;

    public MovieUriMatcher(int code) {
        super(code);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_MOVIES, MOVIES_CODE);
        uriMatcher.addURI(contentAuthority,
                MovieContract.PATH_REVIEWS, REVIEWS_CODE);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH_MOVIES + "/#", SINGLE_MOVIE_CODE);
        // TODO single review
//        uriMatcher.addURI(contentAuthority,
//                MovieContract.PATH_MOVIES + "/#/" + MovieContract.PATH_REVIEWS + "/#",
//                SINGLE_MOVIE_CODE);
        // TODO videos
        return uriMatcher;
    }
}
