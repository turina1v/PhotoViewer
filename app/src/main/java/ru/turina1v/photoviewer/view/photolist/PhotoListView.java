package ru.turina1v.photoviewer.view.photolist;

import java.util.List;

import moxy.MvpView;
import ru.turina1v.photoviewer.model.entity.Hit;

public interface PhotoListView extends MvpView {
    void initPhotoRecycler();

    void updatePhotoRecycler(List<Hit> photos);

    void appendPhotoRecycler(List<Hit> photos);

    void openDetailPhoto(Hit photo);

    void openSearchSettings();

    void openLastClicked();

    void openAppInfo();
}
