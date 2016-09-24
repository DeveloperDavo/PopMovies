package com.example.android.popularmoviesapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.popularmoviesapp.data.MovieContract;

/**
 * Created by David on 22/08/16.
 */
public class Utility {

    private static final String SORT_ORDER_PREFERENCE = "sort_order";

    public static void setSortOrder(Context context, String sortOrder) {
        SharedPreferences settings = context.getSharedPreferences(
                SORT_ORDER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(context.getString(R.string.sort_order_key), sortOrder);
        editor.commit();
    }

    public static String getSortOrder(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                SORT_ORDER_PREFERENCE, Context.MODE_PRIVATE);
        final String defValue = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        return settings.getString(
                context.getString(R.string.sort_order_key), defValue);
    }

    public static String formatRating(Context context, double ratingFromDb) {
        int formatId = R.string.format_user_rating;
        return String.format(context.getString(formatId), ratingFromDb);
    }
}
