package com.philuvarov.flickr.remote.model

import com.google.gson.annotations.SerializedName

class Photo(@SerializedName("id") val id: Long,
            @SerializedName("url_n") val url: String?,
            @SerializedName("farm") val farm: String,
            @SerializedName("server") val server: String,
            @SerializedName("secret") val secret: String)