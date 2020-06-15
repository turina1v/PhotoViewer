package ru.turina1v.photoviewer.view.clickedphotos;

import java.util.List;

import moxy.MvpView;
import ru.turina1v.photoviewer.model.entity.Hit;

public interface ClickedPhotosView extends MvpView {
    void initPhotoRecycler();

    void updatePhotoRecycler(List<Hit> photos);

    void showLoader();

    void showErrorScreen(int stringId);

    void openDetailPhoto(Hit photo);
}
