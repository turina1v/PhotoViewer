package ru.turina1v.photoviewer.presenter;

import android.util.Log;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import moxy.InjectViewState;
import moxy.MvpPresenter;
import ru.turina1v.photoviewer.App;
import ru.turina1v.photoviewer.model.database.PhotoDao;
import ru.turina1v.photoviewer.model.database.PhotoDatabase;
import ru.turina1v.photoviewer.model.entity.PhotoList;
import ru.turina1v.photoviewer.model.retrofit.PhotoLoader;
import ru.turina1v.photoviewer.view.PhotoListView;

@SuppressWarnings("FieldCanBeLocal")
@InjectViewState
public class PhotoListPresenter extends MvpPresenter<PhotoListView> {
    private static final String TAG = "PicturesListPresenter";
    private final long FULL_DAY_MILLIS = 86_400_000; //24 часа в милисекундах, срок хранения ссылок в базе

    @Inject
    PhotoDatabase database;
    @Inject
    PhotoLoader loader;
    private PhotoDao photoDao;
    private CompositeDisposable subscriptions;

    public PhotoListPresenter() {
        App.getComponent().inject(this);
        photoDao = database.photoDao();
        subscriptions = new CompositeDisposable();
    }

    public void downloadPhotosList(String query) {
        Single<PhotoList> single = loader.requestServer(prepareQuery(query));
        subscriptions.add(single.doOnSuccess(photoList -> clearDBTable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        photoList -> {
                            getViewState().initPhotoRecycler(photoList.getHits());
                            getViewState().saveQueryPreference(prepareQuery(query));
                            putPhotosToDB(photoList);
                        },
                        throwable -> Log.e(TAG, "onError", throwable)));
    }

    public void updatePhotoList(String query) {
        Single<PhotoList> single = loader.requestServer(prepareQuery(query));
        subscriptions.add(single.doOnSuccess(photoList -> clearDBTable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        photoList -> {
                            getViewState().updatePhotoRecycler(photoList.getHits());
                            getViewState().saveQueryPreference(prepareQuery(query));
                            putPhotosToDB(photoList);
                        },
                        throwable -> Log.e(TAG, "onError", throwable)));
    }

    public void getPhotosFromDB() {
        subscriptions.add(photoDao.getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        photos -> getViewState().initPhotoRecycler(photos),
                        throwable -> Log.e(TAG, "onError", throwable)));
    }

    private void putPhotosToDB(PhotoList photoList) {
        subscriptions.add(photoDao.insertList(photoList.getHits()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        longs -> getViewState().saveLoadFromDB(),
                        throwable -> Log.e(TAG, "onError", throwable)));
    }

    private void clearDBTable() {
        subscriptions.add(photoDao.deleteAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        longs -> Log.e(TAG, "onSuccess, table cleared"),
                        throwable -> Log.e(TAG, "onError", throwable)));
    }

    public boolean isUpdateNeeded(long updateTimestamp) {
        if (updateTimestamp == 0) {
            return true;
        }
        long nowTimestamp = System.currentTimeMillis();
        return nowTimestamp >= updateTimestamp;
    }

    public long getUpdateTimestamp(long currentTimestamp) {
        return currentTimestamp + FULL_DAY_MILLIS;
    }

    public String prepareQuery(String query) {
        return query.replace(" ", "+");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!subscriptions.isDisposed()) {
            subscriptions.dispose();
        }
    }
}
