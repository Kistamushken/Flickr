package com.philuvarov.flickr.base

interface Presenter<in VS: ViewState, in V : View<VS>> {

    fun bind(view: V)

    fun unbind()

}