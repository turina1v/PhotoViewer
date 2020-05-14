package ru.turina1v.photoviewer.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.PicassoLoader;
import ru.turina1v.photoviewer.presenter.PhotoDetailPresenter;

public class PhotoDetailActivity extends MvpAppCompatActivity implements PhotoDetailView {
    public static final String EXTRA_POSITION = "ru.turina1v.photoviewer.EXTRA_POSITION";
    public static final String EXTRA_PHOTO_URL = "ru.turina1v.photoviewer.EXTRA_PHOTO_URL";

    @InjectPresenter
    PhotoDetailPresenter presenter;

    @BindView(R.id.photo_detail_view)
    ImageView photoDetailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        int picturePosition = intent.getIntExtra(EXTRA_POSITION, -1);
        String pictureUrl = intent.getStringExtra(EXTRA_PHOTO_URL);
        presenter.showDetailPhoto(picturePosition, pictureUrl);
    }

    @Override
    public void showPhoto(String photoUrl) {
        PicassoLoader.loadImage(photoDetailView, photoUrl);
    }
}
