package com.philuvarov.flickr.photos

import android.os.Parcel
import android.os.Parcelable
import com.philuvarov.flickr.base.ViewState
import com.philuvarov.flickr.util.Parcelables.creator

sealed class PhotoScreenState(val photos: List<PhotoItem> = emptyList(),
                              val query: String? = null,
                              val page: Int = 1) : ViewState {

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeList(photos)
            writeString(query)
            writeInt(page)
        }
    }

    class Empty : PhotoScreenState() {

        companion object {
            @JvmField
            val CREATOR = creator { Empty() }
        }
    }

    class Loading(
            photos: List<PhotoItem> = emptyList(),
            query: String? = null,
            page: Int = 1) : PhotoScreenState(photos, query, page) {

        companion object {
            @JvmField
            val CREATOR = creator {
                Loading(
                        createList(),
                        readString(),
                        readInt()
                )
            }
        }
    }

    class Loaded(
            photos: List<PhotoItem> = emptyList(),
            query: String? = null,
            page: Int = 1) : PhotoScreenState(photos, query, page) {

        companion object {
            @JvmField
            val CREATOR = creator {
                Loaded(
                        createList(),
                        readString(),
                        readInt()
                )
            }
        }
    }

    class Error(
            photos: List<PhotoItem> = emptyList(),
            query: String? = null,
            page: Int = 1) : PhotoScreenState(photos, query, page) {

        companion object {
            @JvmField
            val CREATOR = creator {
                Error(
                        createList(),
                        readString(),
                        readInt()
                )
            }
        }

    }

}

private inline fun <reified I : Parcelable> Parcel.createList(): List<I> {
    return mutableListOf<I>().apply { readList(this, List::class.java.classLoader) }
}
