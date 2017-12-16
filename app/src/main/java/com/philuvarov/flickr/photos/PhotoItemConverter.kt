package com.philuvarov.flickr.photos

import com.philuvarov.flickr.base.Converter
import com.philuvarov.flickr.remote.model.Photo
import dagger.Reusable
import javax.inject.Inject

@Reusable
class PhotoItemConverter @Inject constructor() : Converter<Photo, PhotoItem> {

    override fun convert(value: Photo) = PhotoItem(value.id, value.url ?: buildUrl(value))

    private fun buildUrl(photo: Photo): String {
        return BASE_URL
                .replace("{farm}", photo.farm)
                .replace("{server}", photo.server)
                .replace("{id}", "${photo.id}")
                .replace("{secret}", photo.secret)

    }

}

private const val BASE_URL = "http://farm{farm}.static.flickr.com/{server}/{id}_{secret}.jpg"
