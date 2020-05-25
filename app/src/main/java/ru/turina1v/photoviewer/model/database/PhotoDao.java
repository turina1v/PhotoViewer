package ru.turina1v.photoviewer.model.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import ru.turina1v.photoviewer.model.entity.Hit;

@Dao
public interface PhotoDao {
    @Query("SELECT * FROM table_photos")
    Single<List<Hit>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertPhoto(Hit photo);

    @Delete
    Single<Integer> deleteExpired(List<Hit> expiredPhotos);

    @Query("DELETE FROM table_photos")
    Single<Integer> deleteAll();
}
