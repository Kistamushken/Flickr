package com.philuvarov.flickr.util

import android.os.Parcel
import android.os.Parcelable

object Parcelables {

    @JvmStatic
    fun <T : Parcelable> creator(builder: Parcel.() -> T) = object : Parcelable.Creator<T> {

        override fun createFromParcel(source: Parcel) = builder(source)

        @Suppress("UNCHECKED_CAST")
        override fun newArray(size: Int) = arrayOfNulls<Any>(size) as Array<T?>

    }
}