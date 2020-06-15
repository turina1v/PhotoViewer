package ru.turina1v.photoviewer.view.photodetail;

import moxy.MvpView;

public interface PhotoDetailView extends MvpView {
    void showDetailPhoto(String photoUrl);
}
