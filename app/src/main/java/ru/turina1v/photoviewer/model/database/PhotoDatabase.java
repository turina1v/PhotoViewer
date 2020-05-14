package ru.turina1v.photoviewer.model.database;

import androidx.room.RoomDatabase;

import ru.turina1v.photoviewer.model.entity.Hit;

@androidx.room.Database(entities = {Hit.class}, version = 1, exportSchema = false)
public abstract class PhotoDatabase extends RoomDatabase {
    public abstract PhotoDao photoDao();
}
