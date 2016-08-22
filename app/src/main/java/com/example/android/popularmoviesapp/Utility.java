package com.example.android.popularmoviesapp;

import android.content.Context;

/**
 * Created by David on 22/08/16.
 */
public class Utility {
    public static String formatRating(Context context, double ratingFromDb) {
        int formatId = R.string.format_user_rating;
        return String.format(context.getString(formatId), ratingFromDb);
    }
}
