package com.philuvarov.flickr;

import com.philuvarov.flickr.util.SchedulersProvider;
import com.philuvarov.flickr.util.SchedulersProviderImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AppModule {

    @Binds
    public abstract SchedulersProvider schedulersProvider(SchedulersProviderImpl schedulersProvider);

}
