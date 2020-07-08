package ru.turina1v.photoviewer;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

import ru.turina1v.photoviewer.di.AppComponent;
import ru.turina1v.photoviewer.di.AppModule;
import ru.turina1v.photoviewer.di.DaggerAppComponent;
import ru.turina1v.photoviewer.model.AnalyticsUtils;

public class App extends Application {
    private static AppComponent component;
    private static AnalyticsUtils analyticsUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        component = generateAppComponent();
        analyticsUtils = new AnalyticsUtils(this);
        analyticsUtils.logEvent(AnalyticsUtils.EVENT_OPEN_APP);
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

    public static AnalyticsUtils getAnalytics(){
        return analyticsUtils;
    }
}
