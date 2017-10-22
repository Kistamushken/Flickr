package com.philuvarov.flickr

import dagger.android.DaggerApplication

class App : DaggerApplication() {

    override fun applicationInjector() = DaggerAppComponent
            .builder()
            .application(this)
            .build()
}
