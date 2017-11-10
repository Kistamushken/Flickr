package com.philuvarov.flickr.photos

import android.annotation.SuppressLint
import com.philuvarov.flickr.base.ViewState
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
sealed class PhotoScreenState : ViewState {

    abstract val photos: List<PhotoItem>
    abstract val query: String?
    abstract val page: Int

    @Parcelize
    data class Empty(override val photos: List<PhotoItem> = emptyList(),
                     override val query: String? = null,
                     override val page: Int = 1) : PhotoScreenState()

    @Parcelize
    data class Loading(override val photos: List<PhotoItem>,
                       override val query: String?,
                       override val page: Int) : PhotoScreenState()

    @Parcelize
    data class Loaded(override val photos: List<PhotoItem>,
                      override val query: String?,
                      override val page: Int) : PhotoScreenState()

    @Parcelize
    data class Error(override val photos: List<PhotoItem>,
                     override val query: String?,
                     override val page: Int) : PhotoScreenState()

}
