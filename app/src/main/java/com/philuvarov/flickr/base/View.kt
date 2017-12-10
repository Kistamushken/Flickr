package com.philuvarov.flickr.base

import io.reactivex.Observable

interface View<in VS : ViewState, MSG : Msg> {

    fun intents(): Observable<MSG>

    fun render(state: Observable<out VS>)

}