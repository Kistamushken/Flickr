package com.philuvarov.flickr.remote.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import dagger.Reusable
import java.lang.reflect.Type
import javax.inject.Inject

@Reusable
class PhotosResponseTypeAdapter @Inject constructor() : JsonDeserializer<PhotosResponse> {

    override fun deserialize(json: JsonElement,
                             typeOfT: Type,
                             context: JsonDeserializationContext): PhotosResponse {

        val js = json.asJsonObject
        val response = js.get("photos").asJsonObject
        val pages = response.get("pages").asInt
        val photos = context.deserialize<List<Photo>>(
                response.get("photo"),
                object : TypeToken<List<Photo>>() {}.type
        )

        return PhotosResponse(photos, pages)
    }
}