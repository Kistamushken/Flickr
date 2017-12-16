package com.philuvarov.flickr.photos;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;

import com.philuvarov.flickr.base.Converter;
import com.philuvarov.flickr.base.Dispatcher;
import com.philuvarov.flickr.base.Driver;
import com.philuvarov.flickr.base.Msg;
import com.philuvarov.flickr.base.StateContainer;
import com.philuvarov.flickr.base.StateKeeper;
import com.philuvarov.flickr.base.UseCase;
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

    @Binds
    public abstract StateContainer<PhotoScreenState> stateContainer(PhotoListStateManager stateManager);

    @Provides
    @PhotosScope
    public static UseCase<PhotoScreenAction, PhotoScreenState, PhotoListCommand> useCase(PhotoListActivity activity,
                                                                                         ModelFactory modelFactory) {
        return ViewModelProviders.of(activity, modelFactory).get(PhotoListUseCase.class);
    }

    @Provides
    @PhotosScope
    public static Driver<? extends Msg> driver(PhotoListActivity activity,
                                               ModelFactory modelFactory) {
        return ViewModelProviders.of(activity, modelFactory).get(PhotoListDriver.class);
    }

    @Provides
    public static Dispatcher<? extends PhotoScreenState, PhotoScreenAction, PhotoListCommand> dispatcher(
            SchedulersProvider schedulersProvider,
            UseCase<PhotoScreenAction, PhotoScreenState, PhotoListCommand> useCase,
            PhotoListDriver driver) {

        return new Dispatcher<>(schedulersProvider, useCase, driver);
    }


}
