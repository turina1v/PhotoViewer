package ru.turina1v.photoviewer.di;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.turina1v.photoviewer.model.PhotoPreferences;
import ru.turina1v.photoviewer.model.database.PhotoDatabase;
import ru.turina1v.photoviewer.model.retrofit.PhotoLoader;

@Module
public class AppModule {
    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    PhotoDatabase provideDatabase(Context context){
        return Room.databaseBuilder(context,
                PhotoDatabase.class, "photo_database").build();
    }

    @Singleton
    @Provides
    Context provideContext(){
        return application;
    }

    @Singleton
    @Provides
    PhotoLoader providePhotoLoader(){
        return new PhotoLoader();
    }

    @Singleton
    @Provides
    PhotoPreferences providePhotoPreferences(Context context){
        return new PhotoPreferences(context);
    }
}
