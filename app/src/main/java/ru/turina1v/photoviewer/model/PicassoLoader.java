package ru.turina1v.photoviewer.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import ru.turina1v.photoviewer.R;

public class PicassoLoader {
    public static void loadImage(ImageView imageView, String url) {
        Picasso.get().load(url).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).into(imageView);
    }

    public static void loadImage(ImageView imageView, String url, Callback callback) {
        Picasso.get().load(url).error(R.drawable.ic_placeholder_error_detail).into(imageView, callback);
    }

    public static Single<Bitmap> downloadImage(String url) {
        return Single.create(new SingleOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(SingleEmitter<Bitmap> emitter) throws Exception {
                Picasso.get().load(url).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        emitter.onSuccess(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        });
    }
}
