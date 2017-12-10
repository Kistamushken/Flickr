package com.philuvarov.flickr.base

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable

abstract class UseCase<VA : Msg, VS : ViewState, CMD: Cmd>: ViewModel() {

    abstract fun process(intents: Observable<VA>): Observable<Pair<VS, CMD>>

}