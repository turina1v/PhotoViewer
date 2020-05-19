package ru.turina1v.photoviewer.model.retrofit;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.turina1v.photoviewer.model.entity.PhotoList;

public interface PhotoApi {
    @GET("api")
    Single<PhotoList> loadPhotosList(@Query("key") String key,
                                     @Query("q") String query,
                                     @Query("image_type") String imageType,
                                     @Query("orientation") String orientation,
                                     @Query("category") String category,
                                     @Query("per_page") String perPage);
}
