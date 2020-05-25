package ru.turina1v.photoviewer.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.turina1v.photoviewer.presenter.ClickedPhotosPresenter;
import ru.turina1v.photoviewer.presenter.PhotoDetailPresenter;
import ru.turina1v.photoviewer.presenter.PhotoListPresenter;
import ru.turina1v.photoviewer.view.photolist.PhotoListActivity;
import ru.turina1v.photoviewer.view.searchsettings.SearchSettingsActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(PhotoListPresenter presenter);

    void inject(PhotoListActivity activity);

    void inject(SearchSettingsActivity activity);

    void inject(ClickedPhotosPresenter presenter);

    void inject(PhotoDetailPresenter presenter);
}
