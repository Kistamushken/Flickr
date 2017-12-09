package com.philuvarov.flickr.photos

import com.philuvarov.flickr.base.Msg

sealed class PhotoScreenAction : Msg {
    class Initial : PhotoScreenAction()
    class LoadMore : PhotoScreenAction() //TODO: Object
    class Query(val query: String) : PhotoScreenAction()
}