package com.philuvarov.flickr.photos

import android.annotation.SuppressLint
import com.philuvarov.flickr.base.ViewState
import com.philuvarov.flickr.photos.PhotoScreenState.Loaded
import com.philuvarov.flickr.photos.PhotoScreenState.Loading
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
sealed class PhotoScreenState : ViewState {

    abstract val photos: List<PhotoItem>
    abstract val query: String?
    abstract val page: Int

    @Parcelize
    data class Empty(override val photos: List<PhotoItem> = emptyList(),
                     override val query: String? = null,
                     override val page: Int = 1) : PhotoScreenState() {

        override fun toString(): String {
            return super.toString()
        }
    }

    @Parcelize
    data class Loading(override val photos: List<PhotoItem>,
                       override val query: String?,
                       override val page: Int) : PhotoScreenState() {

        override fun toString(): String {
            return super.toString()
        }
    }

    @Parcelize
    data class Loaded(override val photos: List<PhotoItem>,
                      override val query: String?,
                      override val page: Int) : PhotoScreenState() {

        override fun toString(): String {
            return super.toString()
        }
    }

    @Parcelize
    data class Error(override val photos: List<PhotoItem>,
                     override val query: String?,
                     override val page: Int) : PhotoScreenState() {

        override fun toString(): String {
            return super.toString()
        }
    }

    override fun toString(): String {
        return "State: ${this.javaClass.simpleName}, Photos: ${photos.size}, Query: $query, Page: $page"
    }

}

fun PhotoScreenState.toLoading(): Loading = Loading(photos, query, page)

fun PhotoScreenState.toLoaded(photos: List<PhotoItem> = this.photos,
                              query: String? = this.query,
                              page: Int = this.page): Loaded = Loaded(photos, query, page)

