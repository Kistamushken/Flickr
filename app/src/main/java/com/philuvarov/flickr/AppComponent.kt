package com.philuvarov.flickr

import com.philuvarov.flickr.remote.ApiModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
        modules = arrayOf(
                AppModule::class,
                AndroidInjectionModule::class,
                ActivityBinder::class,
                ApiModule::class
        )
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

}