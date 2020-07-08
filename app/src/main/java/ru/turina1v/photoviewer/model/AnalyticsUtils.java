package ru.turina1v.photoviewer.model;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsUtils {
    public static String EVENT_OPEN_APP = "open_app";
    public static String EVENT_OPEN_DETAIL_IMAGE = "open_detail_image";
    public static String EVENT_SHARE_IMAGE = "share_image";
    public static String EVENT_SET_WALLPAPER = "set_wallpaper";
    public static String EVENT_DOWNLOAD_IMAGE = "download_image";
    public static String EVENT_START_EDITING_IMAGE = "start_editing_image";
    public static String EVENT_IMAGE_APPLY_CHANGES = "image_apply_changes";
    public static String EVENT_CANCEL_EDITING_IMAGE = "cancel_editing_image";
    public static String EVENT_CLOSE_EDITOR_WITHOUT_CHANGES = "close_editor_without_changes";
    public static String EVENT_CLICK_PIXABAY_LINK_MAIN = "click_pixabay_link_main";
    public static String EVENT_CLICK_PIXABAY_LINK_ABOUT = "click_pixabay_link_about";

    private FirebaseAnalytics firebaseAnalytics;

    public AnalyticsUtils(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void logEvent(String eventName){
        if (firebaseAnalytics != null){
            firebaseAnalytics.logEvent(eventName, null);
        }
    }
}
