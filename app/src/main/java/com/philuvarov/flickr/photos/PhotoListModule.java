package com.philuvarov.flickr.photos;

import android.arch.lifecycle.ViewModelProviders;

import com.philuvarov.flickr.base.Converter;
import com.philuvarov.flickr.base.Dispatcher;
import com.philuvarov.flickr.base.Driver;
import com.philuvarov.flickr.base.Model;
import com.philuvarov.flickr.base.StateKeeper;
import com.philuvarov.flickr.remote.model.Photo;
import com.philuvarov.flickr.util.SchedulersProvider;
import com.philuvarov.flickr.viewmodel.ModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class PhotoListModule {

    @Binds
    public abstract Converter<? super Photo, PhotoItem> converter(PhotoItemConverter converter);

    @Binds
    public abstract StateKeeper stateKeeper(PhotoListStateManager stateManager);

    @Provides
    @PhotosScope
    public static Model<PhotoScreenMessage, PhotoScreenState> useCase(PhotoListActivity activity,
                                                                      ModelFactory modelFactory) {
        return ViewModelProviders.of(activity, modelFactory).get(PhotoListModel.class);
    }

    @Provides
    @PhotosScope
    public static Driver<PhotoScreenMessage, PhotoScreenState> driver(PhotoListActivity activity,
                                                                      ModelFactory modelFactory) {
        return ViewModelProviders.of(activity, modelFactory).get(PhotoListDriver.class);
    }

    @Provides
    @PhotosScope
    public static Dispatcher<? extends PhotoScreenState, PhotoScreenMessage> dispatcher(
            SchedulersProvider schedulersProvider,
            Model<PhotoScreenMessage, PhotoScreenState> model,
            Driver<PhotoScreenMessage, PhotoScreenState> driver) {

        return new Dispatcher<>(schedulersProvider, model, driver);
    }


}
