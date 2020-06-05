package ru.turina1v.photoviewer.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.turina1v.photoviewer.R;

public class ImageLoader {
    private static OkHttpClient client = new OkHttpClient.Builder().build();

    public static void loadImage(ImageView imageView, String url) {
        Picasso.get().load(url).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).into(imageView);
    }

    public static void loadImage(ImageView imageView, String url, Callback callback) {
        Picasso.get().load(url).error(R.drawable.ic_placeholder_error_detail).into(imageView, callback);
    }

    public static Single<Bitmap> downloadImageByPicasso(String url) {
        return Single.create(emitter -> Picasso.get().load(url).into(new Target() {
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
        }));
    }

    public static Single<Bitmap> downloadImage(String url) {
        return Single.create(emitter -> {
            final Request request = new Request.Builder().url(url).build();
            final Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                final Bitmap bitmap = BitmapFactory.decodeStream(response.body()
                        .byteStream());
                if (bitmap != null) {
                    emitter.onSuccess(bitmap);
                } else {
                    emitter.onError(new RuntimeException("Error decoding bitmap"));
                }
            } else {
                emitter.onError(new RuntimeException("Error downloading bitmap, code = " + response.code()));
            }
        });
    }
}
