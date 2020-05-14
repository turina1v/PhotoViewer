package ru.turina1v.photoviewer.view;

import moxy.MvpView;

public interface PhotoDetailView extends MvpView {
    void showPhoto(String photoUrl);
}
