package com.philuvarov.flickr.base

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

abstract class Driver<MSG : Msg, VS : ViewState> : ViewModel() {

    private val inner: PublishRelay<Pair<MSG, VS>> = PublishRelay.create()

    private val subscriptions = CompositeDisposable()

    private val sideEffects = lazy {
        inner
                .compose(composer)
                .replay(1)
                .autoConnect()
    }

    protected abstract val composer: ObservableTransformer<Pair<MSG, VS>, MSG>

    override fun onCleared() {
        subscriptions.clear()
        super.onCleared()
    }

    fun results(actions: Observable<Pair<MSG, VS>>): Observable<MSG> {
        subscriptions += actions.subscribe({ inner.accept(it) })
        return sideEffects.value
    }

}