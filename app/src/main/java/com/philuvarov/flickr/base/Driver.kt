package com.philuvarov.flickr.base

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject

abstract class Driver<MSG : Msg> {
    private val commands: PublishSubject<Cmd> = PublishSubject.create()

    protected abstract val composer: ObservableTransformer<Cmd, MSG>

    fun results(): Observable<MSG> = commands.compose(composer)

    fun process(command: Cmd) {
        commands.onNext(command)
    }
}