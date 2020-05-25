package ru.turina1v.photoviewer.presenter;

import android.util.Log;

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
import ru.turina1v.photoviewer.view.photodetail.PhotoDetailView;

@InjectViewState
public class PhotoDetailPresenter extends MvpPresenter<PhotoDetailView> {
    private static final String TAG = "PictureDetailPresenter";
    private final long FULL_DAY_MILLIS = 86_400_000; //24 часа в милисекундах, срок хранения ссылок в базе

    @Inject
    PhotoDatabase database;
    private PhotoDao photoDao;
    private CompositeDisposable subscriptions;

    public PhotoDetailPresenter() {
        App.getComponent().inject(this);
        photoDao = database.photoDao();
        subscriptions = new CompositeDisposable();
    }

    public void showDetailPhoto(String photoUrl) {
        getViewState().showPhoto(photoUrl);
    }

    public void savePhotoToDb(Hit photo) {
        photo.setExpireTimestamp(System.currentTimeMillis() + FULL_DAY_MILLIS);
        subscriptions.add(photoDao.insertPhoto(photo).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        id -> Log.d(TAG, "onSuccess: photo added to DB"),
                        throwable -> Log.e(TAG, "onError", throwable)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!subscriptions.isDisposed()) {
            subscriptions.dispose();
        }
    }
}
