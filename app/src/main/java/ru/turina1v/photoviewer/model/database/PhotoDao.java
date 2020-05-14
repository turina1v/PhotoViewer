package ru.turina1v.photoviewer.model.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import ru.turina1v.photoviewer.model.entity.Hit;

@Dao
public interface PhotoDao {
    @Query("SELECT * FROM table_photos")
    Single<List<Hit>> getAll();

    @Insert
    Single<List<Long>> insertList(List<Hit> photos);

    @Query("DELETE FROM table_photos")
    Single<Integer> deleteAll();
}
