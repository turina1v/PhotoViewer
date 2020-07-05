package ru.turina1v.photoviewer.view.photolist;

import ru.turina1v.photoviewer.model.entity.Hit;

public interface OnPhotoClickListener {
    void onPhotoClick(Hit photo);

    void onCommercialClick(String url);
}
