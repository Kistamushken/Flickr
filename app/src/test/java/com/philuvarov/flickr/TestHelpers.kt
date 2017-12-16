package com.philuvarov.flickr

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers.trampoline
import org.hamcrest.Matcher
import org.hamcrest.core.Is.`is`


fun <T> Is(value: T): Matcher<T> {
    return `is`(value)
}

fun <T> Observable<T>.executeImmediately(): Disposable =
        subscribeOn(trampoline())
                .observeOn(trampoline())
                .subscribe()

fun <T : Any> Observable<T>.executeOnTestSubscriber() = TestObserver<T>()
        .apply { executeOn(this) }


fun <T, S> Observable<T>.executeOn(subscriber: S): Disposable where S : Observer<in T>, S : Disposable {
    return subscribeOn(trampoline())
            .observeOn(trampoline())
            .subscribeWith(subscriber)
}

fun <T> TestObserver<T>.assertLastValue(condition: T.() -> Boolean) {
    assertValueAt(valueCount() - 1) { condition(it) }
}
