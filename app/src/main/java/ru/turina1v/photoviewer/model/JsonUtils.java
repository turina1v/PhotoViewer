package ru.turina1v.photoviewer.model;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class JsonUtils {

    public static String loadJSONFromAsset(Context context, String url) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(url);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}