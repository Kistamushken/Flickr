package com.philuvarov.flickr.base

import io.reactivex.Flowable

interface UseCase<in VA : Msg, out VS : ViewState> {

    fun handle(action: VA): Flowable<out VS>

}