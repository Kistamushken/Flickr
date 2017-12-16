package com.philuvarov.flickr.photos

import com.jakewharton.rxrelay2.PublishRelay
import com.philuvarov.flickr.base.Model
import com.philuvarov.flickr.photos.PhotoScreenAction.Initial
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadingError
import com.philuvarov.flickr.photos.PhotoScreenAction.PageLoaded
import com.philuvarov.flickr.photos.PhotoScreenAction.Query
import com.philuvarov.flickr.photos.PhotoScreenAction.QueryLoaded
import com.philuvarov.flickr.photos.PhotoScreenAction.Test
import com.philuvarov.flickr.photos.PhotoScreenState.Empty
import com.philuvarov.flickr.photos.PhotoScreenState.Error
import com.philuvarov.flickr.photos.PhotoScreenState.Loading
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoListModel @Inject constructor() : Model<PhotoScreenAction, PhotoScreenState>() {

    private val actions: PublishRelay<PhotoScreenAction> = PublishRelay.create()

    private val states = composeStateStream()

    private val subscriptions = CompositeDisposable()

    private fun composeStateStream(): Observable<PhotoScreenState> {
        return actions
                .scan(Empty() as PhotoScreenState) { state, action -> reduce(state, action) }
                .replay(1)
                .autoConnect()
    }

    override fun observe(intents: Observable<PhotoScreenAction>) {
        subscriptions += intents.subscribe({ actions.accept(it) })
    }

    override fun states(): Observable<PhotoScreenState> = states

    override fun onCleared() {
        subscriptions.clear()
        super.onCleared()
    }

    private fun reduce(oldState: PhotoScreenState, action: PhotoScreenAction): PhotoScreenState {
        with(oldState) {
            return when (action) {
                is Initial -> {
                    when (this) {
                        is Empty -> toLoading()
                        is Error -> toLoaded()
                        is Loading -> toLoading()
                        else -> toLoaded()
                    }
                }
                is Query -> toLoading(query = action.query)
                is LoadMore -> toLoading()
                is PageLoaded -> {
                    if (oldState is Loading && page == action.page) {
                        toLoading()
                    } else {
                        toLoaded(photos.union(action.photos).toList(), action.query, action.page)
                    }
                }
                is QueryLoaded -> toLoaded(action.photos, action.query, 1)
                is LoadingError -> Error(photos, action.query, action.page)
                is Test -> oldState
            }
        }
    }

}