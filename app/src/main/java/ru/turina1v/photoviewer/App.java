package ru.turina1v.photoviewer;

import android.app.Application;

import ru.turina1v.photoviewer.di.AppComponent;
import ru.turina1v.photoviewer.di.AppModule;
import ru.turina1v.photoviewer.di.DaggerAppComponent;

public class App extends Application {
    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = generateAppComponent();
    }

    public static AppComponent getComponent() {
        return component;
    }

    public AppComponent generateAppComponent() {
        return DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
    }
}
