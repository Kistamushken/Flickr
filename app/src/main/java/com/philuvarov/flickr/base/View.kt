package com.philuvarov.flickr.base

interface View<in VS: ViewState> {

    fun render(state: VS)

}