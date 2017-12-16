package com.philuvarov.flickr

import com.philuvarov.flickr.remote.ApiModule
import com.philuvarov.flickr.viewmodel.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
        modules = [(AppModule::class), (AndroidInjectionModule::class), (ActivityBinder::class), (ApiModule::class), (ViewModelModule::class)]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

}