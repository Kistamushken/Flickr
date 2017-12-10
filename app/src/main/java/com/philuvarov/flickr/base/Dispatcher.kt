package com.philuvarov.flickr.base

import android.arch.lifecycle.LifecycleOwner
import com.philuvarov.flickr.util.SchedulersProvider
import io.reactivex.Observable

class Dispatcher<out VS : ViewState, MSG : Msg, CMD : Cmd>(
        private val schedulers: SchedulersProvider,
        private val useCase: UseCase<MSG, VS, CMD>,
        private val driver: Driver<MSG>) {


    fun bind(view: View<VS, MSG>, lifecycle: LifecycleOwner) {
        view.render(
                Observable.merge(view.intents(), driver.results())
                        .compose { useCase.process(it) }
                        .doOnNext { driver.process(it.second) }
                        .map { it.first }
                        .observeOn(schedulers.mainThread())
        )

    }

}