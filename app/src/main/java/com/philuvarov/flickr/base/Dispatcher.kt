package com.philuvarov.flickr.base

import android.arch.lifecycle.Lifecycle.Event.ON_STOP
import android.arch.lifecycle.LifecycleOwner
import com.philuvarov.flickr.util.SchedulersProvider
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables


class Dispatcher<out VS : ViewState, MSG : Msg>(
        private val schedulers: SchedulersProvider,
        private val model: Model<MSG, VS>,
        private val driver: Driver<MSG, VS>) {

    fun bind(view: View<VS, MSG>, lifecycle: LifecycleOwner) {
        model.observe(
                view
                        .intents()
                        .publish { viewIntents ->
                            Observable.merge(
                                    driver.results(
                                            Observables
                                                    .combineLatest(viewIntents, model.states())
                                                    .bindUntilEvent(lifecycle, ON_STOP)
                                    ),
                                    viewIntents

                            )
                        }
                        .bindUntilEvent(lifecycle, ON_STOP)
        )

        view.render(
                model
                        .states()
                        .observeOn(schedulers.mainThread())
                        .bindUntilEvent(lifecycle, ON_STOP)
        )
    }

}