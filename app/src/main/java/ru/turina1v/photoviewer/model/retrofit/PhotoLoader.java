package ru.turina1v.photoviewer.model.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.turina1v.photoviewer.model.entity.PhotoList;

@SuppressWarnings("FieldCanBeLocal")
public class PhotoLoader {
    private final String baseUrl = "https://pixabay.com";
    private final String apiKey = "16316299-ba7e12baf00b76dee8fff44c9";
    private final String imageType = "photo";
    private PhotoApi api;

    public PhotoLoader() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);
        api = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .build()
                .create(PhotoApi.class);
    }

    public Single<PhotoList> requestServer(String query) {
        return api.loadPhotosList(apiKey, query, imageType).subscribeOn(Schedulers.io());
    }
}