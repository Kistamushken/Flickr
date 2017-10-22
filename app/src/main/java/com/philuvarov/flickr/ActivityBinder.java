package com.philuvarov.flickr;

import com.philuvarov.flickr.photos.PhotoListActivity;
import com.philuvarov.flickr.photos.PhotoListModule;
import com.philuvarov.flickr.photos.PhotosScope;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBinder {

    @PhotosScope
    @ContributesAndroidInjector(modules = PhotoListModule.class)
    abstract PhotoListActivity bindPhotoListActivity();

}
