package ru.turina1v.photoviewer.view.cropimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import moxy.MvpAppCompatActivity;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.ImageLoader;
import ru.turina1v.photoviewer.model.entity.Hit;
import ru.turina1v.photoviewer.view.photodetail.PhotoDetailActivity;

public class CropImageActivity extends MvpAppCompatActivity implements CropImageView {
    @BindView(R.id.crop_image_view)
    com.theartofdev.edmodo.cropper.CropImageView cropImageView;
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

    private Bitmap initialBitmap;
    private Bitmap currentBitmap;

    private int screenHeight;
    private int screenWidth;
    private boolean isShowResultLayout = true;
    private boolean isResultButtonPressed = false;
    private boolean isMissCropWindowListener = false;
    private boolean isInitialImage = true;

    private CompositeDisposable subscriptions = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        ButterKnife.bind(this);
        initToolbar();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        Intent data = getIntent();
        Hit photo = data.getParcelableExtra(PhotoDetailActivity.EXTRA_PHOTO);
        subscriptions.add(ImageLoader.downloadImage(photo.getLargeImageUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::initCropImageView,
                        throwable -> Log.e("", "onError", throwable)));
    }

    @OnClick({R.id.button_free_crop, R.id.button_screen_crop, R.id.button_result_cancel, R.id.button_result_confirm})
    public void onButtonClick(View view) {
        switch (view.getId()) {
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

    private Rect getCurrentCropRect() {
        Rect fullImageRect = cropImageView.getWholeImageRect();
        int margin = fullImageRect.height() / 20;
        int newLeft = fullImageRect.left + margin;
        int newRight = fullImageRect.right - margin;
        int newTop = fullImageRect.top + margin;
        int newBottom = fullImageRect.bottom - margin;
        return new Rect(newLeft, newTop, newRight, newBottom);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void initCropImageView(Bitmap bitmap) {
        initialBitmap = bitmap;
        currentBitmap = bitmap;
        cropImageView.setImageBitmap(bitmap);
        cropImageView.setShowCropOverlay(true);
        cropImageView.setAutoZoomEnabled(false);
        cropImageView.setCropRect(getCurrentCropRect());
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
            cropImageView.setImageBitmap(result.getBitmap());
            isMissCropWindowListener = true;
            cropImageView.setCropRect(getCurrentCropRect());
            currentBitmap = result.getBitmap();
            //croppedImage = result.getBitmap();
            Uri uriResult = result.getUri();
            // bla bla
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!subscriptions.isDisposed()) {
            subscriptions.dispose();
        }
    }
}
