package com.philuvarov.flickr.base

import android.arch.lifecycle.Lifecycle.Event.*
import android.arch.lifecycle.LifecycleOwner
import com.philuvarov.flickr.photos.PhotoScreenState
import com.philuvarov.flickr.util.SchedulersProvider
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables


class Dispatcher<out VS : ViewState, MSG : Msg, CMD : Cmd>(
        private val schedulers: SchedulersProvider,
        private val useCase: UseCase<MSG, VS, CMD>,
        private val driver: Driver<MSG>) {

    fun bind(view: View<VS, MSG>, lifecycle: LifecycleOwner) {

        useCase.observe(
                view
                        .intents()
                        .publish {
                            Observable.merge(
                                    driver.results(Observables.combineLatest(it, useCase.states() as Observable<PhotoScreenState>).bindUntilEvent(lifecycle, ON_STOP)),
                                    it

                            )
                        }
                        .bindUntilEvent(lifecycle, ON_STOP)
        )

        view.render(
                useCase
                        .states()
                        .observeOn(schedulers.mainThread())
                        .bindUntilEvent(lifecycle, ON_STOP)
        )
    }

}