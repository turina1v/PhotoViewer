package ru.turina1v.photoviewer.view;

import java.util.List;

import moxy.MvpView;
import ru.turina1v.photoviewer.model.entity.Hit;

public interface PhotoListView extends MvpView {
    void initPhotoRecycler(List<Hit> photos);
    void updatePhotoRecycler(List<Hit> photos);
    void saveQueryPreference(String query);
    void openDetailPhoto(int position, String photoUrl);
    void openSearchSettings();
    void openAppInfo();
    void saveLoadFromDB();
}
