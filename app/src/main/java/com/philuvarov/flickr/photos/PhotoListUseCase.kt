package com.philuvarov.flickr.photos

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.philuvarov.flickr.base.Cmd
import com.philuvarov.flickr.base.Msg
import com.philuvarov.flickr.base.UseCase
import com.philuvarov.flickr.base.ViewState
import com.philuvarov.flickr.photos.PhotoListCommand.LoadInitial
import com.philuvarov.flickr.photos.PhotoListCommand.LoadNextPage
import com.philuvarov.flickr.photos.PhotoListCommand.LoadQuery
import com.philuvarov.flickr.photos.PhotoListCommand.None
import com.philuvarov.flickr.photos.PhotoScreenAction.Initial
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadMore
import com.philuvarov.flickr.photos.PhotoScreenAction.LoadingError
import com.philuvarov.flickr.photos.PhotoScreenAction.PageLoaded
import com.philuvarov.flickr.photos.PhotoScreenAction.Query
import com.philuvarov.flickr.photos.PhotoScreenAction.QueryLoaded
import com.philuvarov.flickr.photos.PhotoScreenState.Empty
import com.philuvarov.flickr.photos.PhotoScreenState.Error
import com.philuvarov.flickr.photos.PhotoScreenState.Loading
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoListUseCase @Inject constructor() : UseCase<PhotoScreenAction, PhotoScreenState, PhotoListCommand>() {

    private val actions: BehaviorRelay<PhotoScreenAction> = BehaviorRelay.create()

    private val states = composeStateStream()

    private val subscriptions = CompositeDisposable()

    private fun composeStateStream(): Observable<PhotoScreenState> {
        return actions
                .scan(Empty() as PhotoScreenState) { state, action -> reducez(state, action) }
//                .publish()
//                .autoConnect(1)
                .replay(1)
                .autoConnect(0)
    }

    override fun observe(intents: Observable<PhotoScreenAction>) {
        subscriptions += intents.subscribe({ actions.accept(it) })
    }

    override fun states(): Observable<PhotoScreenState> = states


    override fun onCleared() {
        subscriptions.clear()
        super.onCleared()
    }

    override fun process(intent: PhotoScreenAction): Observable<Pair<PhotoScreenState, PhotoListCommand>> {
        return Observable
                .just(intent)
                .scan(Empty() as PhotoScreenState to None as PhotoListCommand) { (state, _), action -> reduce(state, action) }
                .publish()
                .autoConnect()
    }

    override fun process(intents: Observable<PhotoScreenAction>): Observable<Pair<PhotoScreenState, PhotoListCommand>> {
        return Observable.merge(intents, actions)
                .scan(Empty() as PhotoScreenState to None as PhotoListCommand) { (state, _), action -> reduce(state, action) }
                .replay(1)
                .autoConnect(0)
    }


    private fun reducez(oldState: PhotoScreenState, action: PhotoScreenAction): PhotoScreenState {
        with(oldState) {
            return when (action) {
                is Initial -> {
                    when (this) {
                        is Empty -> toLoading()
                        is Error, is Loading -> if (photos.isEmpty()) toLoading() else toLoaded()
                        else -> toLoaded()
                    }
                }
                is Query -> toLoading()
                is LoadMore -> toLoading()
                is PageLoaded -> toLoaded(photos.union(action.photos).toList(), action.query, action.page)
                is QueryLoaded -> toLoaded(action.photos, action.query, 1)
                is LoadingError -> Error(photos, action.query, action.page)
                is PhotoScreenAction.Test -> oldState
            }
        }
    }

    private fun reduce(oldState: PhotoScreenState, action: PhotoScreenAction): Pair<PhotoScreenState, PhotoListCommand> {
        with(oldState) {
            return when (action) {
                is Initial -> {
                    when (this) {
                        is Empty -> toInitial()
                        is Error, is Loading -> if (photos.isEmpty()) toInitial() else toLoaded() to None
                        else -> toLoaded() to None
                    }
                }
                is Query -> toLoading() to LoadQuery(action.query)
                is LoadMore -> toLoading() to LoadNextPage(query, page + 1)
                is PageLoaded -> toLoaded(photos.union(action.photos).toList(), action.query, action.page) to None
                is QueryLoaded -> toLoaded(action.photos, action.query, 1) to None
                is LoadingError -> Error(photos, action.query, action.page) to None
                else -> toInitial()
            }
        }
    }

    private fun PhotoScreenState.toInitial() = toLoading() to LoadInitial

    private class DisposableSubject<MSG : Msg>(private val publishSubject: BehaviorRelay<MSG>) : DisposableObserver<MSG>() {
        override fun onNext(t: MSG) {
            publishSubject.accept(t)
        }

        override fun onError(e: Throwable) {
        }

        override fun onComplete() {
        }

    }

}

sealed class PhotoListCommand : Cmd {
    object None : PhotoListCommand()
    object LoadInitial : PhotoListCommand()
    class LoadQuery(val query: String) : PhotoListCommand()
    class LoadNextPage(val query: String?, val page: Int) : PhotoListCommand()
}