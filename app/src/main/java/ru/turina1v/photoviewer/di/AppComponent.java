package ru.turina1v.photoviewer.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.turina1v.photoviewer.presenter.PhotoListPresenter;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(PhotoListPresenter presenter);
}
