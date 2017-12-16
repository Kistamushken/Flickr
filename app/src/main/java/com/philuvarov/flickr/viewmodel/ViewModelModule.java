package com.philuvarov.flickr.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.philuvarov.flickr.photos.PhotoListDriver;
import com.philuvarov.flickr.photos.PhotoListModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PhotoListModel.class)
    abstract ViewModel photoListModel(PhotoListModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PhotoListDriver.class)
    abstract ViewModel photoListDriver(PhotoListDriver model);

    @Binds
    abstract ViewModelProvider.Factory modelFactory(ModelFactory factory);
}
