package com.philuvarov.flickr.photos

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class PhotoItem(val id: Long, val url: String) : Parcelable