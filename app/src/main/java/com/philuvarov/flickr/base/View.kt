package com.philuvarov.flickr.base

import com.philuvarov.flickr.photos.PhotoScreenState
import io.reactivex.Observable

interface View<in VS : ViewState, MSG : Msg> {

    fun intents(): Observable<MSG>

    fun render(state: Observable<out VS>)

    fun render(it: VS)
}