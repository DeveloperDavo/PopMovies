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
    private static final String POSITION_PREFERENCE = "position";

    // TODO: workaround for savedInstanceState
    public static void setSelection(Context context, String selection) {
        setPreferenceText(context, selection, R.string.selection_key, SELECTION_PREFERENCE);
    }

    // TODO: workaround for savedInstanceState
    public static String getSelection(Context context) {
        return getPreferenceText(context, SELECTION_PREFERENCE, null, R.string.selection_key);
    }

    // TODO: workaround for savedInstanceState
    public static void setSelectionArg(Context context, String selectionArg) {
        setPreferenceText(context, selectionArg, R.string.selection_arg_key,
                SELECTION_ARG_PREFERENCE);
    }

    // TODO: workaround for savedInstanceState
    public static String getSelectionArg(Context context) {
        return getPreferenceText(context, SELECTION_ARG_PREFERENCE, null,
                R.string.selection_arg_key);
    }

    // TODO: workaround for savedInstanceState
    public static void setSortOrder(Context context, String sortOrder) {
        setPreferenceText(context, sortOrder, R.string.sort_order_key, SORT_ORDER_PREFERENCE);
    }

    // TODO: workaround for savedInstanceState
    public static String getSortOrder(Context context) {
        return getPreferenceText(context, SORT_ORDER_PREFERENCE,
                MovieEntry.COLUMN_POPULARITY + " DESC", R.string.sort_order_key);
    }

    // TODO: workaround for savedInstanceState
    public static void setPosition(Context context, int position) {
        setPreferenceText(context, Integer.toString(position),
                R.string.position_key, POSITION_PREFERENCE);
    }

    // TODO: workaround for savedInstanceState
    public static String getPosition(Context context) {
        return getPreferenceText(context, POSITION_PREFERENCE,
                Integer.toString(-1), R.string.position_key);
    }

    public static String formatRating(Context context, double ratingFromDb) {
        int formatId = R.string.format_user_rating;
        return String.format(context.getString(formatId), ratingFromDb);
    }

    /* Helper methods */
    private static void setPreferenceText(
            Context context, String text, int key, String preference) {
        SharedPreferences settings = context.getSharedPreferences(
                preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(context.getString(key), text);
        editor.commit();
    }

    private static String getPreferenceText(
            Context context, String preference, String defValue, int key) {
        SharedPreferences settings = context.getSharedPreferences(
                preference, Context.MODE_PRIVATE);
        return settings.getString(context.getString(key), defValue);
    }

}
