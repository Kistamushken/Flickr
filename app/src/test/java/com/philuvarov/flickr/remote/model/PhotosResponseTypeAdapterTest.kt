package com.philuvarov.flickr.remote.model

import com.google.gson.GsonBuilder
import com.philuvarov.flickr.Is
import org.intellij.lang.annotations.Language
import org.junit.Assert.assertThat
import org.junit.Test

@Suppress("IllegalIdentifier")
class PhotosResponseTypeAdapterTest {

    private val gson = GsonBuilder()
            .registerTypeAdapter(PhotosResponse::class.java, PhotosResponseTypeAdapter())
            .create()


    @Test
    fun `parse photos response`() {
        val response = gson.fromJson(RESPONSE, PhotosResponse::class.java)

        assertThat(response.totalPages, Is(2))
        assertThat(response.photos.isEmpty(), Is(false))
    }

}

@Language("JavaScript")
private val RESPONSE = """
    {
        "photos": {
                "page": 1,
                "pages": 2,
                "perpage": 3,
                "total": "4",
                "photo": [
                    {
                        "id": "23963189578",
                        "owner": "45491220@N03",
                        "secret": "c203d51993",
                        "server": "4494",
                        "farm": 5,
                        "title": "turtzioz",
                        "ispublic": 1,
                        "isfriend": 0,
                        "isfamily": 0,
                        "url_n": "https://farm5.staticflickr.com/4494/23963189578_c203d51993.jpg",
                        "height_m": "281",
                        "width_m": "500"
                    }
                ]
        }
    }
"""