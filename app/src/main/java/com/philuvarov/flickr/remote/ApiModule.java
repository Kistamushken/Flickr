package com.philuvarov.flickr.remote;

import android.support.annotation.StringDef;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.philuvarov.flickr.App;
import com.philuvarov.flickr.R;
import com.philuvarov.flickr.remote.model.PhotosResponse;
import com.philuvarov.flickr.remote.model.PhotosResponseTypeAdapter;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.philuvarov.flickr.remote.ApiModule.ApiValues.API_KEY;
import static com.philuvarov.flickr.remote.ApiModule.ApiValues.ENDPOINT;

@Module
public abstract class ApiModule {

    @Provides
    @Singleton
    public static Api api(Retrofit retrofit) {
        return retrofit.create(Api.class);
    }

    @Provides
    @Singleton
    public static Retrofit retrofit(OkHttpClient httpClient,
                                    Gson gson,
                                    @Named(ENDPOINT) String endpoint) {
        return new Retrofit.Builder()
                .baseUrl(endpoint)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }

    @Provides
    @Singleton
    public static OkHttpClient okHttpClient(SignatureInterceptor signatureInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(signatureInterceptor)
                .build();
    }

    @Provides
    @Singleton
    public static Gson gson(PhotosResponseTypeAdapter photosResponseTypeAdapter) {
        return new GsonBuilder()
                .registerTypeAdapter(PhotosResponse.class, photosResponseTypeAdapter)
                .create();
    }

    @Provides
    @Named(ENDPOINT)
    public static String endpoint(App application) {
        return application.getResources().getString(R.string.endpoint);
    }

    @Provides
    @Named(API_KEY)
    public static String apiKey(App application) {
        return application.getResources().getString(R.string.api_key);
    }

    @StringDef
    @interface ApiValues {
        String API_KEY = "ApiKey";
        String ENDPOINT = "Endpoint";
    }

}
