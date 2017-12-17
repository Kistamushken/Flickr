package com.philuvarov.flickr.base

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable

abstract class Model<MSG : Msg, VS : ViewState> : ViewModel() {

    abstract fun observe(intents: Observable<MSG>)

    abstract fun states(): Observable<VS>

}