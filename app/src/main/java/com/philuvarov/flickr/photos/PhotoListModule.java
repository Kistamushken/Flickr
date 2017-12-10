package com.philuvarov.flickr.photos;

import com.philuvarov.flickr.base.Converter;
import com.philuvarov.flickr.base.Dispatcher;
import com.philuvarov.flickr.base.Driver;
import com.philuvarov.flickr.base.Msg;
import com.philuvarov.flickr.base.StateContainer;
import com.philuvarov.flickr.base.StateKeeper;
import com.philuvarov.flickr.base.UseCase;
import com.philuvarov.flickr.remote.model.Photo;
import com.philuvarov.flickr.util.SchedulersProvider;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class PhotoListModule {

    @Binds
    public abstract UseCase<? extends PhotoScreenAction, ? extends PhotoScreenState, ? extends PhotoListCommand> useCase(PhotoListUseCase useCase);

    @Binds
    public abstract Converter<? super Photo, PhotoItem> converter(PhotoItemConverter converter);

    @Binds
    public abstract StateKeeper stateKeeper(PhotoListStateManager stateManager);

    @Binds
    public abstract StateContainer<PhotoScreenState> stateContainer(PhotoListStateManager stateManager);

    @Binds
    public abstract Driver<? extends Msg> driver(PhotoListDriver driver);

    @Provides
    public static Dispatcher<? extends PhotoScreenState, PhotoScreenAction, PhotoListCommand> dispatcher(
            SchedulersProvider schedulersProvider,
            PhotoListUseCase useCase,
            PhotoListDriver driver) {

        return new Dispatcher<>(schedulersProvider, useCase, driver);
    }


}
