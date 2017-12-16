package com.philuvarov.flickr.photos

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class PhotoItem(val id: Long, val url: String) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhotoItem

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}