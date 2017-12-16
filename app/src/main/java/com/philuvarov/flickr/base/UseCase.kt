package com.philuvarov.flickr.base

import android.arch.lifecycle.ViewModel
import com.philuvarov.flickr.photos.PhotoListCommand
import com.philuvarov.flickr.photos.PhotoScreenAction
import com.philuvarov.flickr.photos.PhotoScreenState
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject

abstract class UseCase<VA : Msg, VS : ViewState, CMD: Cmd>: ViewModel() {

    abstract fun process(intent: VA): Observable<Pair<PhotoScreenState, PhotoListCommand>>
    abstract fun observe(intents: Observable<VA>)
    abstract fun states(): Observable<VS>
    abstract fun process(intents: Observable<VA>): Observable<Pair<PhotoScreenState, PhotoListCommand>>
}