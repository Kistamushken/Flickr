package com.philuvarov.flickr.photos;

import com.philuvarov.flickr.base.Converter;
import com.philuvarov.flickr.base.Presenter;
import com.philuvarov.flickr.base.StateContainer;
import com.philuvarov.flickr.base.StateKeeper;
import com.philuvarov.flickr.base.UseCase;
import com.philuvarov.flickr.remote.model.Photo;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PhotoListModule {

    @Binds
    public abstract Presenter<? super PhotoScreenState, ? super PhotoListView> photosListViewPresenter(PhotoListPresenter photosListPresenter);

    @Binds
    public abstract UseCase<? super PhotoScreenAction, ? extends PhotoScreenState> useCase(PhotoListUseCase useCase);

    @Binds
    public abstract Converter<? super Photo, PhotoItem> converter(PhotoItemConverter converter);

    @Binds
    public abstract StateKeeper stateKeeper(PhotoListStateManager stateManager);

    @Binds
    public abstract StateContainer<PhotoScreenState> stateContainer(PhotoListStateManager stateManager);

}
