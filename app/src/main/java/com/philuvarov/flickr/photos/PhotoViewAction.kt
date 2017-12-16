package com.philuvarov.flickr.photos

import com.philuvarov.flickr.base.Msg

sealed class PhotoScreenAction : Msg {
    object Initial : PhotoScreenAction()
    object LoadMore : PhotoScreenAction()
    class Query(val query: String) : PhotoScreenAction()
    class PageLoaded(val photos: List<PhotoItem>, val query: String?, val page: Int) : PhotoScreenAction()
    class QueryLoaded(val photos: List<PhotoItem>, val query: String?) : PhotoScreenAction()
    class LoadingError(val query: String?, val page: Int) : PhotoScreenAction()
    class Test : PhotoScreenAction()
}