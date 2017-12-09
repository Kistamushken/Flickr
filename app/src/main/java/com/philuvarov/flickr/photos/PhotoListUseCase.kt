package com.philuvarov.flickr.photos

import com.philuvarov.flickr.base.Converter
import com.philuvarov.flickr.base.Msg
import com.philuvarov.flickr.base.StateContainer
import com.philuvarov.flickr.base.UseCase
import com.philuvarov.flickr.photos.PhotoScreenAction.Initial
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenAction.Query
import com.philuvarov.flickr.photos.PhotoScreenState.Empty
import com.philuvarov.flickr.photos.PhotoScreenState.Error
import com.philuvarov.flickr.photos.PhotoScreenState.Loaded
import com.philuvarov.flickr.photos.PhotoScreenState.Loading
import com.philuvarov.flickr.remote.Api
import com.philuvarov.flickr.remote.model.Photo
import com.philuvarov.flickr.util.SchedulersProvider
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

@PhotosScope
class PhotoListUseCase @Inject constructor(
        private val api: Api,
        private val schedulers: SchedulersProvider,
        private val converter: Converter<Photo, PhotoItem>,
        private val messageStream: Observable<PhotoScreenAction>,
        stateContainer: StateContainer<PhotoScreenState>
) : UseCase<PhotoScreenAction, PhotoScreenState> {

    private var state by stateContainer

    private val ids: MutableSet<Long> = mutableSetOf()
        get() {
            return if (field.isEmpty()) {
                mutableSetOf<Long>().apply { addAll(state.photos.toIdList()) }
            } else {
                field
            }
        }

    init {
        messageStream
                .scan(PhotoScreenState.Empty() as PhotoScreenState) { t1, t2 ->
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                .flatMap { reduce(state, action) }
                .doOnNext { state = it }
                .replay(1)
                .autoConnect()
    }

    fun subscribe() {
        messageStream
                .scan(PhotoScreenState.Empty() as PhotoScreenState) { state, action -> reducez(state, action) }
                .replay(1)
                .autoConnect()
    }

    override fun handle(action: PhotoScreenAction): Flowable<Pair<PhotoScreenState, Cmd>> {
        return Flowable
                .just(action)
                .scan(PhotoScreenState.Empty() as PhotoScreenState, { oldState, action ->
                    reducez(oldState, action)
                })
                .flatMap { reduce(state, action) }
                .doOnNext { state = it }

    }

    private fun reducez(oldState: PhotoScreenState, action: PhotoScreenAction): Pair<PhotoScreenState, Cmd> {
        return when (action) {
            is Initial -> {
                when (state) {
                    is Empty -> Loading to Cmd.LoadInitial
                    is Error, is Loading -> if (state.photos.isEmpty()) Loading to Cmd.LoadInitial else state.toLoaded()Loaded(state.photos, state.query, state.page)
                    else -> Loaded(state.photos, state.query, state.page)
                }
            }
            is Query -> load(query = action.query)
            is LoadMore -> load(state.page + 1, state.query)
        }
    }

    private fun currentState(): Flowable<Loaded> {
        return Flowable
                .just(Loaded(state.photos, state.query, state.page))
                .subscribeOn(schedulers.trampoline())
    }

//    private fun load(page: Int = 1, query: String? = null): Flowable<PhotoScreenState> {
//        return getRequestObservable(query, page)
//                .toFlowable()
//                .map { it -> it.photos }
//                .map { it.map { converter.convert(it) } }
//                .map {
//                    if (page == 1) {
//                        ids.clear()
//                        ids.addAll(it.toIdList())
//                        it
//                    } else {
//                        state.photos + it.filter { !ids.contains(it.id) }.onEach { ids.add(it.id) }
//                    }
//                }
//                .map { Loaded(it, query, page) as PhotoScreenState }
//                .startWith(Loading(state.photos, query, page))
//                .onErrorReturn { Error(state.photos, query, page - 1) }
//                .subscribeOn(schedulers.io())
//    }
//
//    private fun List<PhotoItem>.toIdList() = map { it.id }
//
//    private fun getRequestObservable(query: String?, page: Int) =
//            if (query.isNullOrBlank()) api.getRecent(page) else api.searchPhotos(page, query!!)

}

sealed class Cmd {
    object None : Cmd()
    object LoadInitial : Cmd()

}