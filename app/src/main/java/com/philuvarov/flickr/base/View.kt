package com.philuvarov.flickr.base

import io.reactivex.Observable

interface View<in VS: ViewState> {

    fun render(state: VS)

}

interface View2<in VS : ViewState, MSG : Msg> {

    fun intents(): Observable<MSG>

    fun render(state: VS)

}