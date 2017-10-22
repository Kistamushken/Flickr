package com.philuvarov.flickr

import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.trampoline
import io.reactivex.subscribers.TestSubscriber
import org.hamcrest.Matcher
import org.hamcrest.core.Is.`is`
import org.reactivestreams.Subscriber


fun <T> Is(value: T): Matcher<T> {
    return `is`(value)
}

fun <T> Flowable<T>.executeImmediately(): Disposable =
        subscribeOn(trampoline())
                .observeOn(trampoline())
                .subscribe()

fun <T : Any> Flowable<T>.executeOnTestSubscriber() = TestSubscriber<T>()
        .apply { executeOn(this) }


fun <T, S> Flowable<T>.executeOn(subscriber: S): Disposable where S : Subscriber<in T>, S : Disposable {
    return subscribeOn(trampoline())
            .observeOn(trampoline())
            .subscribeWith(subscriber)
}