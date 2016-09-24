package com.example.android.popularmoviesapp;

import android.content.Context;
import android.content.SharedPreferences;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

/**
 * Created by David on 22/08/16.
 */
public class Utility {

    private static final String SORT_ORDER_PREFERENCE = "sort_order";
    private static final String SELECTION_PREFERENCE = "selection";
    private static final String SELECTION_ARG_PREFERENCE = "selection_arg";
    private static final String POSITION_PREFERENCE = "postion";

    // TODO: workaround for savedInstanceState
    public static void setSelection(Context context, String selection) {
        SharedPreferences settings = context.getSharedPreferences(
                SELECTION_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(context.getString(R.string.selection_key), selection);
        editor.commit();
    }

    // TODO: workaround for savedInstanceState
    public static String getSelection(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                SELECTION_PREFERENCE, Context.MODE_PRIVATE);
        final String defValue = null;
        return settings.getString(context.getString(R.string.selection_key), defValue);
    }

    // TODO: workaround for savedInstanceState
    public static void setSelectionArg(Context context, String selectionArg) {
        SharedPreferences settings = context.getSharedPreferences(
                SELECTION_ARG_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(context.getString(R.string.selection_arg_key), selectionArg);
        editor.commit();
    }

    // TODO: workaround for savedInstanceState
    public static String getSelectionArg(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                SELECTION_ARG_PREFERENCE, Context.MODE_PRIVATE);
        final String defValue = null;
        return settings.getString(
                context.getString(R.string.selection_arg_key), defValue);
    }

    // TODO: workaround for savedInstanceState
    public static void setSortOrder(Context context, String sortOrder) {
        SharedPreferences settings = context.getSharedPreferences(
                SORT_ORDER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(context.getString(R.string.sort_order_key), sortOrder);
        editor.commit();
    }

    // TODO: workaround for savedInstanceState
    public static String getSortOrder(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                SORT_ORDER_PREFERENCE, Context.MODE_PRIVATE);
        final String defValue = MovieEntry.COLUMN_POPULARITY + " DESC";
        return settings.getString(context.getString(R.string.sort_order_key), defValue);
    }

    // TODO: workaround for savedInstanceState
    public static void setPosition(Context context, int position) {
        SharedPreferences settings = context.getSharedPreferences(
                POSITION_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(context.getString(R.string.position_key), Integer.toString(position));
        editor.commit();
    }

    // TODO: workaround for savedInstanceState
    public static String getPosition(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                POSITION_PREFERENCE, Context.MODE_PRIVATE);
        final int defValue = -1;
        return settings.getString(
                context.getString(R.string.position_key), Integer.toString(defValue));
    }

    public static String formatRating(Context context, double ratingFromDb) {
        int formatId = R.string.format_user_rating;
        return String.format(context.getString(formatId), ratingFromDb);
    }

}
