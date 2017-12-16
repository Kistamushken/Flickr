package com.philuvarov.flickr.base

import android.arch.lifecycle.ViewModel

import com.jakewharton.rxrelay2.PublishRelay
import com.philuvarov.flickr.photos.PhotoScreenState
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.Observables

abstract class Driver<MSG : Msg> : ViewModel() {

    private val inner: PublishRelay<Pair<MSG, PhotoScreenState>> = PublishRelay.create()

    private val sideEffects = lazy {
        val x = 0
        inner
                .compose(composer)
                .replay(1)
                .autoConnect()
    }

    protected abstract val composer: ObservableTransformer<Pair<MSG, PhotoScreenState>, MSG>

    override fun onCleared() {
        super.onCleared()
    }

    fun results(actions: Observable<MSG>, states: Observable<PhotoScreenState>): Observable<MSG> {
        Observables.combineLatest(actions, states).subscribe({ inner.accept(it) })
        return sideEffects.value
    }

    fun results(actions: Observable<Pair<MSG, PhotoScreenState>>): Observable<MSG> {
        actions.subscribe({ inner.accept(it) })
        return sideEffects.value
    }

}