package ru.turina1v.photoviewer.view.photodetail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.ImageLoader;
import ru.turina1v.photoviewer.model.entity.Hit;
import ru.turina1v.photoviewer.presenter.PhotoDetailPresenter;

public class PhotoDetailActivity extends MvpAppCompatActivity implements PhotoDetailView {
    public static final String EXTRA_PHOTO = "ru.turina1v.photoviewer.EXTRA_PHOTO";
    public static final String EXTRA_IS_SET_EXPIRED = "ru.turina1v.photoviewer.EXTRA_IS_SET_EXPIRED";

    @InjectPresenter
    PhotoDetailPresenter presenter;

    @BindView(R.id.crop_image_view)
    CropImageView cropImageView;
    @BindView(R.id.error_text)
    TextView errorTextView;
    @BindView(R.id.control_buttons_layout)
    LinearLayout controlButtonsLayout;
    @BindView(R.id.button_save)
    ImageView saveButton;
    @BindView(R.id.button_set_wallpaper)
    ImageView setWallpaperButton;
    @BindView(R.id.layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.button_free_crop)
    ImageView freeCropButton;
    @BindView(R.id.button_screen_crop)
    ImageView screenCropButton;
    @BindView(R.id.button_result_cancel)
    ImageView resultCancelButton;
    @BindView(R.id.button_result_confirm)
    ImageView resultConfirmButton;
    @BindView(R.id.control_crop_layout)
    LinearLayout cropLayout;
    @BindView(R.id.control_result_layout)
    LinearLayout resultLayout;

    private int screenHeight;
    private int screenWidth;
    private Bitmap initialBitmap;
    private Bitmap currentBitmap;
    private boolean isShowResultLayout = true;
    private boolean isResultButtonPressed = false;
    private boolean isMissCropWindowListener = false;
    private boolean isEditMode = false;
    private boolean isImageEdited = false;

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private Hit photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        initToolbar();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        initCropImageView();
        photo = intent.getParcelableExtra(EXTRA_PHOTO);
        boolean isSetExpired = intent.getBooleanExtra(EXTRA_IS_SET_EXPIRED, true);
        if (photo != null) {
            presenter.showDetailPhoto(photo.getLargeImageUrl());
            presenter.savePhotoToDb(photo, isSetExpired);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEditMode) {
            menu.findItem(R.id.menu_edit).setVisible(false);
            menu.findItem(R.id.menu_share).setVisible(false);
            menu.findItem(R.id.menu_reset).setVisible(true);
            if (isImageEdited) {
                menu.findItem(R.id.menu_apply_blocked).setVisible(false);
                menu.findItem(R.id.menu_apply_enabled).setVisible(true);
            } else {
                menu.findItem(R.id.menu_apply_blocked).setVisible(true);
                menu.findItem(R.id.menu_apply_enabled).setVisible(false);
            }
        } else {
            menu.findItem(R.id.menu_edit).setVisible(true);
            menu.findItem(R.id.menu_share).setVisible(true);
            menu.findItem(R.id.menu_reset).setVisible(false);
            menu.findItem(R.id.menu_apply_enabled).setVisible(false);
            menu.findItem(R.id.menu_apply_blocked).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.menu_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType(getString(R.string.share_type));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                intent.putExtra(Intent.EXTRA_TEXT, photo.getLargeImageUrl());
                startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
                break;
            case R.id.menu_edit:
                isEditMode = true;
                cropImageView.setShowCropOverlay(true);
                isMissCropWindowListener = true;
                cropImageView.setCropRect(getCurrentCropRect());
                controlButtonsLayout.setVisibility(View.INVISIBLE);
                cropLayout.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                break;
            case R.id.menu_reset:
                isMissCropWindowListener = true;
                cropImageView.setImageBitmap(initialBitmap);
                currentBitmap = initialBitmap;
                isImageEdited = false;
                invalidateOptionsMenu();
                resultLayout.setVisibility(View.INVISIBLE);
                cropLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.menu_apply_enabled:
                isMissCropWindowListener = true;
                cropImageView.setImageBitmap(currentBitmap);
                cropImageView.setShowCropOverlay(false);
                cropLayout.setVisibility(View.INVISIBLE);
                controlButtonsLayout.setVisibility(View.VISIBLE);
                isEditMode = false;
                isImageEdited = false;
                initialBitmap = currentBitmap;
                invalidateOptionsMenu();
                resultLayout.setVisibility(View.INVISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.button_save, R.id.button_set_wallpaper, R.id.button_free_crop, R.id.button_screen_crop,
            R.id.button_result_cancel, R.id.button_result_confirm})
    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.button_save:
                requestGalleryPermission();
                break;
            case R.id.button_set_wallpaper:
                setWallpaper();
                break;
            case R.id.button_free_crop:
                isMissCropWindowListener = true;
                cropImageView.clearAspectRatio();
                cropImageView.setCropRect(getCurrentCropRect());
                break;
            case R.id.button_screen_crop:
                cropImageView.setAspectRatio(screenWidth, screenHeight);
                break;
            case R.id.button_result_cancel:
                resultLayout.setVisibility(View.INVISIBLE);
                cropLayout.setVisibility(View.VISIBLE);
                isMissCropWindowListener = true;
                cropImageView.setCropRect(getCurrentCropRect());
                isShowResultLayout = true;
                isResultButtonPressed = true;
                break;
            case R.id.button_result_confirm:
                cropImageView.getCroppedImageAsync();
                resultLayout.setVisibility(View.INVISIBLE);
                cropLayout.setVisibility(View.VISIBLE);
                isResultButtonPressed = true;
                break;
        }
    }

    @Override
    public void showDetailPhoto(String photoUrl) {
        subscriptions.add(ImageLoader.downloadImage(photo.getLargeImageUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bitmap -> {
                            if (initialBitmap == null) {
                                initialBitmap = bitmap;
                                currentBitmap = bitmap;
                            } else {
                                currentBitmap = bitmap;
                            }
                            cropImageView.setImageBitmap(bitmap);
                            errorTextView.setVisibility(View.GONE);
                            loaderLayout.setVisibility(View.INVISIBLE);
                        },
                        throwable -> Log.e("", "onError", throwable)));
    }

    private void saveToGallery() {
        subscriptions.add(Single.fromCallable(() -> {
            MediaStore.Images.Media.insertImage(getContentResolver(), currentBitmap, String.valueOf(System.currentTimeMillis()), "");
            return new Object();
        }).subscribeOn(Schedulers.io())
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
        subscriptions.add(Single.fromCallable(() -> {
            wm.setBitmap(currentBitmap);
            return new Object();
        }).subscribeOn(Schedulers.io())
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

    private void initCropImageView() {
        cropImageView.setShowCropOverlay(false);
        cropImageView.setAutoZoomEnabled(false);
        cropImageView.setAspectRatio(screenWidth, screenHeight);
        cropImageView.setOnCropWindowChangedListener(() -> {
            if (isMissCropWindowListener || isShowResultLayout || isResultButtonPressed) {
                if (isMissCropWindowListener) {
                    isMissCropWindowListener = false;
                } else if (isShowResultLayout) {
                    cropLayout.setVisibility(View.INVISIBLE);
                    resultLayout.setVisibility(View.VISIBLE);
                    isShowResultLayout = false;
                } else {
                    isShowResultLayout = true;
                    isResultButtonPressed = false;
                }
            }
        });
        cropImageView.setOnCropImageCompleteListener((view, result) -> {
            if (!isImageEdited) {
                isImageEdited = true;
                invalidateOptionsMenu();
            }
            cropImageView.setImageBitmap(result.getBitmap());
            isMissCropWindowListener = true;
            cropImageView.setCropRect(getCurrentCropRect());
            currentBitmap = result.getBitmap();
        });
    }

    private Rect getCurrentCropRect() {
        Rect fullImageRect = cropImageView.getWholeImageRect();
        int margin = fullImageRect.height() / 20;
        int newLeft = fullImageRect.left + margin;
        int newRight = fullImageRect.right - margin;
        int newTop = fullImageRect.top + margin;
        int newBottom = fullImageRect.bottom - margin;
        return new Rect(newLeft, newTop, newRight, newBottom);
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
    public boolean onSupportNavigateUp() {
        if (isEditMode) {
            isEditMode = false;
            isMissCropWindowListener = true;
            cropImageView.setImageBitmap(initialBitmap);
            cropImageView.setShowCropOverlay(false);
            invalidateOptionsMenu();
            cropLayout.setVisibility(View.INVISIBLE);
            resultLayout.setVisibility(View.INVISIBLE);
            isResultButtonPressed = true;
            controlButtonsLayout.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            isEditMode = false;
            isMissCropWindowListener = true;
            cropImageView.setImageBitmap(initialBitmap);
            cropImageView.setShowCropOverlay(false);
            invalidateOptionsMenu();
            cropLayout.setVisibility(View.INVISIBLE);
            resultLayout.setVisibility(View.INVISIBLE);
            isResultButtonPressed = true;
            controlButtonsLayout.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }
}
