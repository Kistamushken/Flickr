package com.philuvarov.flickr.photos

import com.philuvarov.flickr.base.ViewAction

sealed class PhotoScreenAction : ViewAction {
    class Initial : PhotoScreenAction()
    class LoadMore : PhotoScreenAction()
    class Query(val query: String) : PhotoScreenAction()
}