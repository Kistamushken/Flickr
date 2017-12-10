package com.philuvarov.flickr.photos

import com.philuvarov.flickr.base.Cmd
import com.philuvarov.flickr.base.Converter
import com.philuvarov.flickr.base.Driver
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadingError
import com.philuvarov.flickr.photos.PhotoScreenAction.PageLoaded
import com.philuvarov.flickr.remote.Api
import com.philuvarov.flickr.remote.model.Photo
import com.philuvarov.flickr.util.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.ofType
import javax.inject.Inject

@PhotosScope
class PhotoListDriver @Inject constructor(private val schedulers: SchedulersProvider,
                                          private val converter: Converter<Photo, PhotoItem>,
                                          private val api: Api) : Driver<PhotoScreenAction>() {

    override val composer: ObservableTransformer<Cmd, PhotoScreenAction>
        get() = ObservableTransformer {
            it.publish {
                Observable.merge(
                        it.ofType<PhotoListCommand.LoadInitial>().flatMap {
                            load { PageLoaded(it, null, 1) }
                        },
                        it.ofType<PhotoListCommand.LoadNextPage>().flatMap { cmd ->
                            load { PageLoaded(it, cmd.query, cmd.page) }
                        },
                        it.ofType<PhotoListCommand.LoadQuery>().flatMap { cmd ->
                            load(query = cmd.query) { PhotoScreenAction.QueryLoaded(it, cmd.query) }
                        }
                )
            }
        }


    private fun load(page: Int = 1, query: String? = null, wrapper: (List<PhotoItem>) -> PhotoScreenAction): Observable<PhotoScreenAction> {
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