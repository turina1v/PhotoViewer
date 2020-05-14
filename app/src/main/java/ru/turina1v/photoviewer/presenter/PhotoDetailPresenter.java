package ru.turina1v.photoviewer.presenter;

import android.util.Log;

import moxy.InjectViewState;
import moxy.MvpPresenter;
import ru.turina1v.photoviewer.view.PhotoDetailView;

@InjectViewState
public class PhotoDetailPresenter extends MvpPresenter<PhotoDetailView> {
    private static final String TAG = "PictureDetailPresenter";

    public void showDetailPhoto(int picturePosition, String photoUrl) {
        Log.d(TAG, "picture position = " + picturePosition);
        getViewState().showPhoto(photoUrl);
    }
}
