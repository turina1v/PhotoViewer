package ru.turina1v.photoviewer.view.photodetail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Callback;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.ImageLoader;
import ru.turina1v.photoviewer.model.entity.Hit;
import ru.turina1v.photoviewer.presenter.PhotoDetailPresenter;
import ru.turina1v.photoviewer.view.cropimage.CropImageActivity;

public class PhotoDetailActivity extends MvpAppCompatActivity implements PhotoDetailView {
    public static final String EXTRA_PHOTO = "ru.turina1v.photoviewer.EXTRA_PHOTO";
    public static final String EXTRA_IS_SET_EXPIRED = "ru.turina1v.photoviewer.EXTRA_IS_SET_EXPIRED";
    private final int requestCodeCropImage = 222;

    @InjectPresenter
    PhotoDetailPresenter presenter;

    @BindView(R.id.photo_detail_view)
    PhotoView photoDetailView;
    @BindView(R.id.error_text)
    TextView errorTextView;
    @BindView(R.id.button_save)
    Button saveButton;
    @BindView(R.id.button_set_wallpaper)
    Button setWallpaperButton;
    @BindView(R.id.layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.swipe_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private Hit photo;

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
        photo = intent.getParcelableExtra(EXTRA_PHOTO);
        boolean isSetExpired = intent.getBooleanExtra(EXTRA_IS_SET_EXPIRED, true);
        if (photo != null) {
            presenter.showDetailPhoto(photo.getLargeImageUrl());
            presenter.savePhotoToDb(photo, isSetExpired);
            swipeRefreshLayout.setOnRefreshListener(() -> showPhoto(photo.getLargeImageUrl()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_detail, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_share);
        shareItem.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(getString(R.string.share_type));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            intent.putExtra(Intent.EXTRA_TEXT, photo.getLargeImageUrl());
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
            return false;
        });

        MenuItem editItem = menu.findItem(R.id.menu_edit);
        editItem.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(PhotoDetailActivity.this, CropImageActivity.class);
            intent.putExtra(EXTRA_PHOTO, photo);
            startActivityForResult(intent, requestCodeCropImage);
            return false;
        });
        return true;
    }

    @Override
    public void showPhoto(String photoUrl) {
        swipeRefreshLayout.setRefreshing(false);
        errorTextView.setVisibility(View.GONE);
        loaderLayout.setVisibility(View.VISIBLE);
        ImageLoader.loadImage(photoDetailView, photoUrl, new Callback() {
            @Override
            public void onSuccess() {
                loaderLayout.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                setWallpaperButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                loaderLayout.setVisibility(View.INVISIBLE);
                errorTextView.setVisibility(View.VISIBLE);
                if (e instanceof IOException) {
                    errorTextView.setText(R.string.load_info_network_error);
                } else {
                    errorTextView.setText(R.string.load_info_server_error);
                }
            }
        });
    }

    private void saveToGallery() {
        subscriptions.add(ImageLoader.downloadImage(photo.getLargeImageUrl())
                .doOnSuccess(bitmap -> MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, String.valueOf(System.currentTimeMillis()), ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bitmap -> Toast.makeText(this, R.string.toast_saved_to_gallery, Toast.LENGTH_LONG).show(),
                        throwable -> Log.e("", "saveToGallery", throwable)
                ));
    }

    @SuppressLint("MissingPermission")
    private void setWallpaper() {
        ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setMessage(getString(R.string.progress_set_wallpaper));
        progressBar.show();
        WallpaperManager wm = WallpaperManager.getInstance(this);
        subscriptions.add(ImageLoader.downloadImage(photo.getLargeImageUrl())
                .doOnSuccess(wm::setBitmap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bitmap -> {
                            Toast.makeText(this, R.string.toast_wallpaper_set, Toast.LENGTH_LONG).show();
                            progressBar.dismiss();
                        },
                        throwable -> {
                            Log.e("", "saveToGallery", throwable);
                            progressBar.dismiss();
                        }
                ));
    }

    private void requestGalleryPermission() {
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
        if (!subscriptions.isDisposed()) {
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
