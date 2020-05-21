package ru.turina1v.photoviewer.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.github.chrisbanes.photoview.PhotoView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Callback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.PicassoLoader;
import ru.turina1v.photoviewer.presenter.PhotoDetailPresenter;

public class PhotoDetailActivity extends MvpAppCompatActivity implements PhotoDetailView {
    public static final String EXTRA_POSITION = "ru.turina1v.photoviewer.EXTRA_POSITION";
    public static final String EXTRA_PHOTO_URL = "ru.turina1v.photoviewer.EXTRA_PHOTO_URL";

    @InjectPresenter PhotoDetailPresenter presenter;

    @BindView(R.id.photo_detail_view) PhotoView photoDetailView;
    @BindView(R.id.button_save) Button saveButton;
    @BindView(R.id.button_set_wallpaper) Button setWallpaperButton;
    @BindView(R.id.layout_loader) LinearLayout loaderLayout;

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private String photoUrl;

    @OnClick({R.id.button_save, R.id.button_set_wallpaper})
    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.button_save:
                requestGalleryPermission();
                break;
            case R.id.button_set_wallpaper:
                setWallpaper();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        initToolbar();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        int picturePosition = intent.getIntExtra(EXTRA_POSITION, -1);
        photoUrl = intent.getStringExtra(EXTRA_PHOTO_URL);
        presenter.showDetailPhoto(picturePosition, photoUrl);
    }

    @Override
    public void showPhoto(String photoUrl) {
        PicassoLoader.loadImage(photoDetailView, photoUrl, new Callback() {
            @Override
            public void onSuccess() {
                loaderLayout.setVisibility(View.GONE);
                saveButton.setVisibility(View.VISIBLE);
                setWallpaperButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void saveToGallery() {
        subscriptions.add(PicassoLoader.downloadImage(photoUrl).subscribe(
                bitmap -> {
                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, String.valueOf(System.currentTimeMillis()), "");
                    Toast.makeText(this, R.string.toast_saved_to_gallery, Toast.LENGTH_LONG).show();
                },
                throwable -> Log.e("", "saveToGallery", throwable)
        ));
    }

    @SuppressLint("MissingPermission")
    private void setWallpaper(){
        WallpaperManager wm = WallpaperManager.getInstance(this);
        subscriptions.add(PicassoLoader.downloadImage(photoUrl).subscribe(
                bitmap -> {
                    wm.setBitmap(bitmap);
                    Toast.makeText(this, R.string.toast_wallpaper_set, Toast.LENGTH_LONG).show();
                },
                throwable -> Log.e("", "saveToGallery", throwable)
        ));

    }

    private void requestGalleryPermission(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        saveToGallery();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {

                    }
                })
                .check();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!subscriptions.isDisposed()){
            subscriptions.dispose();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
