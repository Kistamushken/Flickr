package com.philuvarov.flickr.photos

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.philuvarov.flickr.util.Parcelables

@SuppressLint("ParcelCreator")
class PhotoItem(val id: Long, val url: String) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeLong(id)
            writeString(url)
        }
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR = Parcelables.creator {
            PhotoItem(
                    readLong(),
                    readString()
            )
        }
    }
}