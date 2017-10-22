package com.philuvarov.flickr.remote.model

import com.google.gson.annotations.SerializedName

class PhotosResponse(@SerializedName("photo") val photos: List<Photo>,
                     @SerializedName("pages") val totalPages: Int)