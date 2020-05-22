package ru.turina1v.photoviewer.model;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PhotoPreferences {
    private final String PREFERENCES_USER = "preferences_user";

    private final String PREFERENCES_QUERY = "preferences_query";

    private final String PREFERENCES_ORIENTATION = "preferences_orientation";
    public static final String ORIENTATION_VERTICAL = "vertical";
    public static final String ORIENTATION_HORIZONTAL = "horizontal";
    public static final String ORIENTATION_ALL = "all";

    private final String PREFERENCES_CATEGORY = "preferences_category";
    public static final String CATEGORY_ALL = "all";
    private final String PREFERENCES_CATEGORY_INDEX = "preferences_category_index";

    private SharedPreferences preferences;

    public PhotoPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_USER, MODE_PRIVATE);
    }

    public void saveQuery(String query) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_QUERY, query);
        editor.apply();
    }

    public void clearQuery() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREFERENCES_QUERY);
        editor.apply();
    }

    public String getQuery() {
        return preferences.getString(PREFERENCES_QUERY, null);
    }

    public void saveOrientation(String orientation) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_ORIENTATION, orientation);
        editor.apply();
    }

    public String getOrientation() {
        return preferences.getString(PREFERENCES_ORIENTATION, null);
    }

    public void saveCategory(String category) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_CATEGORY, category);
        editor.apply();
    }

    public String getCategory() {
        return preferences.getString(PREFERENCES_CATEGORY, null);
    }

    public void saveCategoryIndex(int index) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERENCES_CATEGORY_INDEX, index);
        editor.apply();
    }

    public int getCategoryIndex() {
        return preferences.getInt(PREFERENCES_CATEGORY_INDEX, 0);
    }

    public void clearAll() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREFERENCES_QUERY);
        editor.remove(PREFERENCES_ORIENTATION);
        editor.remove(PREFERENCES_CATEGORY);
        editor.remove(PREFERENCES_CATEGORY_INDEX);
        editor.apply();
    }
}
