package ru.turina1v.photoviewer.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.turina1v.photoviewer.presenter.PhotoListPresenter;
import ru.turina1v.photoviewer.view.PhotoListActivity;
import ru.turina1v.photoviewer.view.SearchSettingsActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(PhotoListPresenter presenter);
    void inject(PhotoListActivity activity);
    void inject(SearchSettingsActivity activity);
}
