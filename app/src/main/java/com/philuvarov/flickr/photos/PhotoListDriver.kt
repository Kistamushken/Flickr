package com.philuvarov.flickr.photos

import com.philuvarov.flickr.base.Driver
import com.philuvarov.flickr.photos.PhotoScreenAction.Initial
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadingError
import com.philuvarov.flickr.photos.PhotoScreenAction.PageLoaded
import com.philuvarov.flickr.photos.PhotoScreenAction.Query
import com.philuvarov.flickr.photos.PhotoScreenAction.QueryLoaded
import com.philuvarov.flickr.photos.PhotoScreenState.Empty
import com.philuvarov.flickr.photos.PhotoScreenState.Loaded
import com.philuvarov.flickr.remote.Api
import com.philuvarov.flickr.util.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_QUERY = "kittens"

@Singleton
class PhotoListDriver @Inject constructor(private val schedulers: SchedulersProvider,
                                          private val converter: PhotoItemConverter,
                                          private val api: Api) : Driver<PhotoScreenAction, PhotoScreenState>() {
    override val composer: ObservableTransformer<Pair<PhotoScreenAction, PhotoScreenState>, PhotoScreenAction> = ObservableTransformer {
            it
                    .filter { it.second !is Loaded }
                    .flatMap { (action, state) ->
                        when (action) {
                            is Query -> load(query = action.query, onSuccess = { QueryLoaded(it, action.query) })
                            is LoadMore -> load(
                                    state.page + 1,
                                    state.query,
                                    onSuccess = { PageLoaded(it, state.query, state.page + 1) },
                                    onError = { LoadingError(state.query, state.page) }
                            )
//                                    .delay(5, TimeUnit.SECONDS, Schedulers.computation())
                            is Initial -> if (state is Empty) load(onSuccess = { PageLoaded(it, DEFAULT_QUERY, 1) }) else Observable.empty()
                            else -> Observable.empty()
                        }
                    }

        }

    private fun load(
            page: Int = 1,
            query: String? = null,
            onSuccess: (List<PhotoItem>) -> PhotoScreenAction,
            onError: (Throwable) -> LoadingError = { LoadingError(query, page) }): Observable<PhotoScreenAction> {
        return getRequestObservable(query, page)
                .map { it -> it.photos }
                .map { it.map { converter.convert(it) } }
                .map { onSuccess(it) }
                .onErrorReturn { onError(it) }
                .subscribeOn(schedulers.io())
                .toObservable()
    }

    private fun getRequestObservable(query: String?, page: Int) =
            if (query.isNullOrBlank()) api.searchPhotos(page, "kittens") else api.searchPhotos(page, query!!)

}