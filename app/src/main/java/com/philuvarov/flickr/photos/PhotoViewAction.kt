package com.philuvarov.flickr.photos

import com.philuvarov.flickr.base.Msg

sealed class PhotoScreenMessage : Msg {
    object Initial : PhotoScreenMessage()
    object LoadMore : PhotoScreenMessage()
    class Query(val query: String) : PhotoScreenMessage()
    class PageLoaded(val photos: List<PhotoItem>, val query: String?, val page: Int) : PhotoScreenMessage()
    class QueryLoaded(val photos: List<PhotoItem>, val query: String?) : PhotoScreenMessage()
    class LoadingError(val query: String?, val page: Int) : PhotoScreenMessage()
    class Test : PhotoScreenMessage()
}