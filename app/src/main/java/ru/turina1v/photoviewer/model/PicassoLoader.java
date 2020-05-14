package ru.turina1v.photoviewer.model;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PicassoLoader {
    public static void loadImage(ImageView imageView, String url){
        Picasso.get().load(url).into(imageView);
    }
}
