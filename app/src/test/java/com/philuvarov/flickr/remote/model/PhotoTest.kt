package com.philuvarov.flickr.remote.model

import com.google.gson.GsonBuilder
import com.philuvarov.flickr.Is
import org.intellij.lang.annotations.Language
import org.junit.Assert.assertThat
import org.junit.Test

@Suppress("IllegalIdentifier")
class PhotoTest {

    private val gson = GsonBuilder().create()

    @Test
    fun  `parse photo`() {
        val response = gson.fromJson(RESPONSE, Photo::class.java)

        assertThat(response.id, Is(1L))
        assertThat(response.url!!, Is("url"))
    }
}

@Language("JavaScript")
private val RESPONSE = """
                    {
                        "id": "1",
                        "owner": "45491220@N03",
                        "secret": "c203d51993",
                        "server": "4494",
                        "farm": 5,
                        "title": "turtzioz",
                        "ispublic": 1,
                        "isfriend": 0,
                        "isfamily": 0,
                        "url_n": "url",
                        "height_m": "281",
                        "width_m": "500"
                    }
"""