package ru.turina1v.photoviewer.model;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PhotoPreferences {
    private final String PREFERENCES_USER = "preferences_user";
    private final String PREFERENCES_LOAD_FROM_DB = "preferences_load_from_db";
    private final String PREFERENCES_UPDATE_TIMESTAMP = "preferences_update_timestamp";

    private final String PREFERENCES_QUERY = "preferences_query";
    public static final String DEFAULT_QUERY = "cats";

    private final String PREFERENCES_ORIENTATION = "preferences_orientation";
    public static final String ORIENTATION_VERTICAL = "vertical";
    public static final String ORIENTATION_HORIZONTAL = "horizontal";
    public static final String ORIENTATION_ALL = "all";
    public static final String DEFAULT_ORIENTATION = "all";

    private final String PREFERENCES_CATEGORY = "preferences_category";
    public static final String CATEGORY_ALL = "all";
    private final String PREFERENCES_CATEGORY_INDEX = "preferences_category_index";

    private SharedPreferences preferences;

    public PhotoPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_USER, MODE_PRIVATE);
    }

    public void saveIsLoadFromDb(Boolean isLoadFromDb){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFERENCES_LOAD_FROM_DB, isLoadFromDb);
        editor.apply();
    }

    public boolean getIsLoadFromDb(){
        return preferences.getBoolean(PREFERENCES_LOAD_FROM_DB, false);
    }

    public void saveUpdateTimestamp(long updateTimestamp){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(PREFERENCES_UPDATE_TIMESTAMP, updateTimestamp);
        editor.apply();
    }

    public long getUpdateTimestamp(){
        return preferences.getLong(PREFERENCES_UPDATE_TIMESTAMP, 0);
    }

    public void saveQuery(String query){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_QUERY, query);
        editor.apply();
    }

    public String getQuery(){
        return preferences.getString(PREFERENCES_QUERY, DEFAULT_QUERY);
    }

    public void saveOrientation(String orientation){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_ORIENTATION, orientation);
        editor.apply();
    }

    public String getOrientation(){
        return preferences.getString(PREFERENCES_ORIENTATION, DEFAULT_ORIENTATION);
    }

    public void saveCategory(String category){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_CATEGORY, category);
        editor.apply();
    }

    public String getCategory(){
        return preferences.getString(PREFERENCES_CATEGORY, CATEGORY_ALL);
    }

    public void saveCategoryIndex(int index){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERENCES_CATEGORY_INDEX, index);
        editor.apply();
    }

    public int getCategoryIndex(){
        return preferences.getInt(PREFERENCES_CATEGORY_INDEX, 0);
    }
}
