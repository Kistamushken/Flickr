package com.philuvarov.flickr.photos

import android.arch.lifecycle.Transformations.map
import android.util.Log
import com.philuvarov.flickr.base.Cmd
import com.philuvarov.flickr.base.Converter
import com.philuvarov.flickr.base.Driver
import com.philuvarov.flickr.photos.PhotoScreenAction.*
import com.philuvarov.flickr.remote.Api
import com.philuvarov.flickr.remote.model.Photo
import com.philuvarov.flickr.util.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.ofType
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoListDriver @Inject constructor(private val schedulers: SchedulersProvider,
                                          private val converter: PhotoItemConverter,
                                          private val api: Api) : Driver<PhotoScreenAction>() {
    override val composer: ObservableTransformer<Pair<PhotoScreenAction, PhotoScreenState>, PhotoScreenAction> = ObservableTransformer {
            it
                    .filter { it.second !is PhotoScreenState.Loaded }
                    .flatMap { (action, state) ->
                        when (action) {
                            is PhotoScreenAction.Query -> load(query = action.query) { QueryLoaded(it, action.query) }
                            is PhotoScreenAction.LoadMore -> load(state.page + 1, state.query) { PageLoaded(it, state.query, state.page + 1) }.delay(5, TimeUnit.SECONDS, Schedulers.computation())
                            is Initial -> if (state is PhotoScreenState.Empty) load { PageLoaded(it, null, 1) } else Observable.empty()
                            else -> Observable.empty()
                        }
                    }

        }

    private fun load(page: Int = 1, query: String? = null, wrapper: (List<PhotoItem>) -> PhotoScreenAction): Observable<PhotoScreenAction> {
        Log.e("Page:", "$page")
        return getRequestObservable(query, page)
                .map { it -> it.photos }
                .map { it.map { converter.convert(it) } }
                .map { wrapper(it) }
                .onErrorReturn { LoadingError(query, page - 1) }
                .subscribeOn(schedulers.io())
                .toObservable()
    }

    private fun getRequestObservable(query: String?, page: Int) =
            if (query.isNullOrBlank()) api.getRecent(page) else api.searchPhotos(page, query!!)

}