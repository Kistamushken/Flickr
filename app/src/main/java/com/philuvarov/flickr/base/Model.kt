package com.philuvarov.flickr.base

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable

abstract class Model<VA : Msg, VS : ViewState> : ViewModel() {

    abstract fun observe(intents: Observable<VA>)

    abstract fun states(): Observable<VS>

}