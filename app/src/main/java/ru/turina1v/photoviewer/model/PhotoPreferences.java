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

    public static final String COLOR_RED = "red";
    public static final String COLOR_ORANGE = "orange";
    public static final String COLOR_YELLOW = "yellow";
    public static final String COLOR_GREEN = "green";
    public static final String COLOR_BLUE_LIGHT = "turquoise";
    public static final String COLOR_BLUE_DARK = "blue";
    public static final String COLOR_PURPLE = "lilac";
    public static final String COLOR_PINK = "pink";
    public static final String COLOR_WHITE = "white";
    public static final String COLOR_GRAY = "gray";
    public static final String COLOR_BLACK = "black";
    public static final String COLOR_BROWN = "brown";
    public static final String COLOR_TRANSPARENT = "transparent";
    public static final String COLOR_GRAYSCALE = "grayscale";

    private String[] colors = {COLOR_RED, COLOR_ORANGE, COLOR_YELLOW, COLOR_GREEN, COLOR_BLUE_LIGHT, COLOR_BLUE_DARK, COLOR_PURPLE,
    COLOR_PINK, COLOR_WHITE, COLOR_GRAY, COLOR_BLACK, COLOR_BROWN, COLOR_TRANSPARENT, COLOR_GRAYSCALE};

    public static final String PREFERENCES_EDITORS_CHOICE = "preferences_editors_choice";

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

    public void saveColor(String color, boolean preferred){
        SharedPreferences.Editor editor = preferences.edit();
        for (String c : colors){
            if (c.equals(color)){
                editor.putBoolean(color, preferred);
            }
        }
        editor.apply();
    }

    public boolean isColor(String color){
        for (String c : colors){
            if (c.equals(color)){
                return preferences.getBoolean(color, false);
            }
        }
        return false;
    }

    public String getColorQuery(){
        String colorQueryRaw = "";
        for (String color : colors){
            if (isColor(color)){
                colorQueryRaw = colorQueryRaw.concat("," + color);
            }
        }
        if ("".equals(colorQueryRaw)){
            return null;
        } else {
            return colorQueryRaw.substring(1);
        }
    }

    public void saveEditorsChoice(boolean isEditorsChoice) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFERENCES_EDITORS_CHOICE, isEditorsChoice);
        editor.apply();
    }

    public boolean isEditorsChoice() {
        return preferences.getBoolean(PREFERENCES_EDITORS_CHOICE, false);
    }

    public String getEditorsChoiceQuery(){
        if (isEditorsChoice()){
            return "true";
        } else {
            return null;
        }
    }

    public void clearSwitchColors(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(COLOR_RED);
        editor.remove(COLOR_ORANGE);
        editor.remove(COLOR_YELLOW);
        editor.remove(COLOR_GREEN);
        editor.remove(COLOR_BLUE_LIGHT);
        editor.remove(COLOR_BLUE_DARK);
        editor.remove(COLOR_PURPLE);
        editor.remove(COLOR_PINK);
        editor.remove(COLOR_WHITE);
        editor.remove(COLOR_GRAY);
        editor.remove(COLOR_BLACK);
        editor.remove(COLOR_BROWN);
        editor.apply();
    }

    public void clearAllColors(){
        SharedPreferences.Editor editor = preferences.edit();
        for (String color : colors){
            editor.remove(color);
        }
        editor.apply();
    }

    public void clearAll() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREFERENCES_QUERY);
        editor.remove(PREFERENCES_ORIENTATION);
        editor.remove(PREFERENCES_CATEGORY);
        editor.remove(PREFERENCES_CATEGORY_INDEX);
        editor.remove(PREFERENCES_EDITORS_CHOICE);
        editor.apply();
        clearAllColors();
    }
}
