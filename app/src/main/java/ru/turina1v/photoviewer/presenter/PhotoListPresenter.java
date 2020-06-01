package ru.turina1v.photoviewer.presenter;

import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import moxy.InjectViewState;
import moxy.MvpPresenter;
import ru.turina1v.photoviewer.App;
import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.retrofit.PhotoLoader;
import ru.turina1v.photoviewer.view.photolist.PhotoListView;

@InjectViewState
public class PhotoListPresenter extends MvpPresenter<PhotoListView> {
    private static final String TAG = "PicturesListPresenter";

    @Inject
    PhotoLoader loader;
    private CompositeDisposable subscriptions;

    private int page = 1;
    private boolean isLoading = false;
    private boolean isEndReached = false;

    public PhotoListPresenter() {
        App.getComponent().inject(this);
        subscriptions = new CompositeDisposable();
    }

    public void downloadPhotoList(String query, String orientation, String category, String colorQuery,
                                  String editorsChoice, String order, String safesearch) {
        getViewState().showLoader();
        subscriptions.add(loader.requestServer(prepareQuery(query), orientation, category, colorQuery, editorsChoice,
                order, safesearch, getPageString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        photoList -> getViewState().updatePhotoRecycler(photoList.getHits()),
                        throwable -> {
                            if (throwable instanceof IOException){
                                getViewState().showErrorScreen(R.string.load_info_network_error);
                            } else {
                                getViewState().showErrorScreen(R.string.load_info_server_error);
                            }
                            Log.e(TAG, "onError", throwable);
                        }));
    }

    public void appendPhotoList(String query, String orientation, String category, String colorQuery,
                                String editorsChoice, String order, String safesearch) {
        if (isLoading || isEndReached) {
            return;
        }
        isLoading = true;
        page++;
        subscriptions.add(loader.requestServer(prepareQuery(query), orientation, category, colorQuery, editorsChoice,
                order, safesearch, getPageString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        photoList -> {
                            if (photoList.getHits().size() > 0) {
                                getViewState().appendPhotoRecycler(photoList.getHits());
                            } else {
                                isEndReached = true;
                            }
                            isLoading = false;
                        },
                        throwable -> {
                            if (throwable instanceof IOException){
                                getViewState().showErrorToast(R.string.load_info_network_error_short);
                            }
                            Log.e(TAG, "onError", throwable);
                            isLoading = false;
                        }));
    }

    private String prepareQuery(String query) {
        if (query == null) {
            return null;
        } else {
            return query.replace(" ", "+");
        }
    }

    private String getPageString() {
        return String.valueOf(page);
    }

    public void resetPage() {
        page = 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!subscriptions.isDisposed()) {
            subscriptions.dispose();
        }
    }
}
