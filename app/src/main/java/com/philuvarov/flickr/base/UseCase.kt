package com.philuvarov.flickr.base

import io.reactivex.Flowable

interface UseCase<in VA : ViewAction, out VS : ViewState> {

    fun handle(action: VA): Flowable<out VS>

}