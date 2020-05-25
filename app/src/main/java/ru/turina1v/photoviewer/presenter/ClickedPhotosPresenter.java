package ru.turina1v.photoviewer.presenter;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import moxy.InjectViewState;
import moxy.MvpPresenter;
import ru.turina1v.photoviewer.App;
import ru.turina1v.photoviewer.model.database.PhotoDao;
import ru.turina1v.photoviewer.model.database.PhotoDatabase;
import ru.turina1v.photoviewer.model.entity.Hit;
import ru.turina1v.photoviewer.model.retrofit.PhotoLoader;
import ru.turina1v.photoviewer.view.clickedphotos.ClickedPhotosView;

@SuppressWarnings("FieldCanBeLocal")
@InjectViewState
public class ClickedPhotosPresenter extends MvpPresenter<ClickedPhotosView> {
    private static final String TAG = "ClickedPhotosPresenter";
    private static final long SESSION_TIME_MILLIS = 600_000; //10 минут, предполагаемый максимум времени просмотра активити

    @Inject
    PhotoDatabase database;
    @Inject
    PhotoLoader loader;
    private PhotoDao photoDao;
    private List<Hit> expiredPhotos = new ArrayList<>();
    private CompositeDisposable subscriptions;

    public ClickedPhotosPresenter() {
        App.getComponent().inject(this);
        photoDao = database.photoDao();
        subscriptions = new CompositeDisposable();
    }

    public void getPhotosFromDB() {
        subscriptions.add(photoDao.getAll()
                .map(this::preparePhotoList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        photos -> {
                            getViewState().updatePhotoRecycler(photos);
                            clearExpiredPhotos(expiredPhotos);
                            },
                        throwable -> Log.e(TAG, "onError", throwable)));
    }

    private List<Hit> preparePhotoList(List<Hit> rawPhotos) {
        Collections.sort(rawPhotos);
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < rawPhotos.size(); i++) {
            // создается запас времени в 10 мин, чтобы исключить ситуацию, когда при открытии списка ссылка еще действительна,
            // а при детальном просмотре фото - уже нет:
            long photoExpireTime = rawPhotos.get(i).getExpireTimestamp() - SESSION_TIME_MILLIS;
            if (photoExpireTime < currentTime) {
                List<Hit> expiredPhotosRaw = rawPhotos.subList(i, rawPhotos.size());
                expiredPhotos.addAll(expiredPhotosRaw);
                rawPhotos.removeAll(expiredPhotosRaw);
                break;
            }
        }
        return rawPhotos;
    }

    private void clearExpiredPhotos(List<Hit> expiredPhotos) {
        subscriptions.add(photoDao.deleteExpired(expiredPhotos)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        deleted -> Log.e(TAG, "onSuccess, items deleted"),
                        throwable -> Log.e(TAG, "onError", throwable)));
    }

    public void clearAll() {
        subscriptions.add(photoDao.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        deleted -> Log.e(TAG, "onSuccess, items deleted"),
                        throwable -> Log.e(TAG, "onError", throwable)));
    }
}
