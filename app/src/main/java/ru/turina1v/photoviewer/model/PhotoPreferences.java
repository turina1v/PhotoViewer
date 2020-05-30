package ru.turina1v.photoviewer.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Objects;

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

    private final String PREFERENCES_EDITORS_CHOICE = "preferences_editors_choice";
    public static final String PREFERENCES_ORDER = "preferences_order";
    public static final String ORDER_POPULAR = "popular";
    public static final String ORDER_LATEST = "latest";

    private final String PREFERENCES_SAFE_SEARCH = "preferences_safe_search";

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

    public void saveColor(String color, boolean preferred) {
        SharedPreferences.Editor editor = preferences.edit();
        for (String c : colors) {
            if (c.equals(color)) {
                editor.putBoolean(color, preferred);
            }
        }
        editor.apply();
    }

    public boolean isColor(String color) {
        for (String c : colors) {
            if (c.equals(color)) {
                return preferences.getBoolean(color, false);
            }
        }
        return false;
    }

    public String getColorQuery() {
        String colorQueryRaw = "";
        for (String color : colors) {
            if (isColor(color)) {
                colorQueryRaw = colorQueryRaw.concat("," + color);
            }
        }
        if ("".equals(colorQueryRaw)) {
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

    public String getEditorsChoiceQuery() {
        if (isEditorsChoice()) {
            return "true";
        } else {
            return null;
        }
    }

    public void saveOrder(String order) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_ORDER, order);
        editor.apply();
    }

    public String getOrder() {
        return preferences.getString(PREFERENCES_ORDER, null);
    }

    public void saveSafeSearch(boolean isSafeSearch) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFERENCES_SAFE_SEARCH, isSafeSearch);
        editor.apply();
    }

    public boolean isSafeSearch() {
        return preferences.getBoolean(PREFERENCES_SAFE_SEARCH, true);
    }

    public String getSafeSearchQuery() {
        if (isSafeSearch()) {
            return "true";
        } else {
            return null;
        }
    }

    public void clearSwitchColors() {
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

    public void clearAllColors() {
        SharedPreferences.Editor editor = preferences.edit();
        for (String color : colors) {
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
        editor.remove(PREFERENCES_ORDER);
        editor.remove(PREFERENCES_SAFE_SEARCH);
        editor.apply();
        clearAllColors();
    }

    public PreferencesState getPreferenceState(){
        return new PreferencesState(this);
    }

    public static class PreferencesState {
        private String query;
        private String orientation;
        private String category;
        private boolean isColorRed;
        private boolean isColorOrange;
        private boolean isColorYellow;
        private boolean isColorGreen;
        private boolean isColorBlueLight;
        private boolean isColorBlueDark;
        private boolean isColorPurple;
        private boolean isColorPink;
        private boolean isColorWhite;
        private boolean isColorGray;
        private boolean isColorBlack;
        private boolean isColorBrown;
        private boolean isColorTransparent;
        private boolean isColorGrayscale;
        private boolean editorsChoice;
        private String order;
        private boolean isSafeSearch;

        private PreferencesState(PhotoPreferences photoPreferences) {
            query = photoPreferences.getQuery();
            orientation = photoPreferences.getOrientation();
            category = photoPreferences.getCategory();
            isColorRed = photoPreferences.isColor(COLOR_RED);
            isColorOrange = photoPreferences.isColor(COLOR_ORANGE);
            isColorYellow = photoPreferences.isColor(COLOR_YELLOW);
            isColorGreen = photoPreferences.isColor(COLOR_GREEN);
            isColorBlueLight = photoPreferences.isColor(COLOR_BLUE_LIGHT);
            isColorBlueDark = photoPreferences.isColor(COLOR_BLUE_DARK);
            isColorPurple = photoPreferences.isColor(COLOR_PURPLE);
            isColorPink = photoPreferences.isColor(COLOR_PINK);
            isColorWhite = photoPreferences.isColor(COLOR_WHITE);
            isColorGray = photoPreferences.isColor(COLOR_GRAY);
            isColorBlack = photoPreferences.isColor(COLOR_BLACK);
            isColorBrown = photoPreferences.isColor(COLOR_BROWN);
            isColorTransparent = photoPreferences.isColor(COLOR_TRANSPARENT);
            isColorGrayscale = photoPreferences.isColor(COLOR_GRAYSCALE);
            editorsChoice = photoPreferences.isEditorsChoice();
            order = photoPreferences.getOrder();
            isSafeSearch = photoPreferences.isSafeSearch();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PreferencesState that = (PreferencesState) o;
            return isColorRed == that.isColorRed &&
                    isColorOrange == that.isColorOrange &&
                    isColorYellow == that.isColorYellow &&
                    isColorGreen == that.isColorGreen &&
                    isColorBlueLight == that.isColorBlueLight &&
                    isColorBlueDark == that.isColorBlueDark &&
                    isColorPurple == that.isColorPurple &&
                    isColorPink == that.isColorPink &&
                    isColorWhite == that.isColorWhite &&
                    isColorGray == that.isColorGray &&
                    isColorBlack == that.isColorBlack &&
                    isColorBrown == that.isColorBrown &&
                    isColorTransparent == that.isColorTransparent &&
                    isColorGrayscale == that.isColorGrayscale &&
                    editorsChoice == that.editorsChoice &&
                    isSafeSearch == that.isSafeSearch &&
                    Objects.equals(query, that.query) &&
                    Objects.equals(orientation, that.orientation) &&
                    Objects.equals(category, that.category) &&
                    Objects.equals(order, that.order);
        }

        @Override
        public int hashCode() {
            return Objects.hash(query, orientation, category, isColorRed, isColorOrange, isColorYellow, isColorGreen, isColorBlueLight, isColorBlueDark, isColorPurple, isColorPink, isColorWhite, isColorGray, isColorBlack, isColorBrown, isColorTransparent, isColorGrayscale, editorsChoice, order, isSafeSearch);
        }
    }
}
