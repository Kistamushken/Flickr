package com.philuvarov.flickr.photos

import com.jakewharton.rxrelay2.PublishRelay
import com.philuvarov.flickr.base.Model
import com.philuvarov.flickr.base.StateContainer
import com.philuvarov.flickr.photos.PhotoScreenMessage.Initial
import com.philuvarov.flickr.photos.PhotoScreenMessage.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenMessage.LoadingError
import com.philuvarov.flickr.photos.PhotoScreenMessage.PageLoaded
import com.philuvarov.flickr.photos.PhotoScreenMessage.Query
import com.philuvarov.flickr.photos.PhotoScreenMessage.QueryLoaded
import com.philuvarov.flickr.photos.PhotoScreenMessage.Test
import com.philuvarov.flickr.photos.PhotoScreenState.Empty
import com.philuvarov.flickr.photos.PhotoScreenState.Error
import com.philuvarov.flickr.photos.PhotoScreenState.Loading
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoListModel @Inject constructor(stateContainer: StateContainer<PhotoScreenState>) : Model<PhotoScreenMessage, PhotoScreenState>() {

    private val actions: PublishRelay<PhotoScreenMessage> = PublishRelay.create()

    private val states = lazy { composeStateStream() }

    private val subscriptions = CompositeDisposable()

    private var state by stateContainer

    private fun composeStateStream(): Observable<PhotoScreenState> {
        return actions
                .scan(state) { state, action -> reduce(state, action) }
                .replay(1)
                .autoConnect()
                .doOnNext { state = it }
    }

    override fun observe(intents: Observable<PhotoScreenMessage>) {
        subscriptions += intents.subscribe({ actions.accept(it) })
    }

    override fun states(): Observable<PhotoScreenState> = states.value

    override fun onCleared() {
        subscriptions.clear()
        super.onCleared()
    }

    private fun reduce(oldState: PhotoScreenState, message: PhotoScreenMessage): PhotoScreenState {
        with(oldState) {
            return when (message) {
                is Initial -> {
                    when (this) {
                        is Empty -> toLoading()
                        is Error -> toLoaded()
                        is Loading -> toLoading()
                        else -> toLoaded()
                    }
                }
                is Query -> toLoading(query = message.query)
                is LoadMore -> toLoading()
                is PageLoaded -> {
                    if (oldState is Loading && page == message.page) {
                        toLoading()
                    } else {
                        toLoaded(photos.union(message.photos).toList(), message.query, message.page)
                    }
                }
                is QueryLoaded -> toLoaded(message.photos, message.query, 1)
                is LoadingError -> Error(photos, message.query, message.page)
                is Test -> oldState
            }
        }
    }

}