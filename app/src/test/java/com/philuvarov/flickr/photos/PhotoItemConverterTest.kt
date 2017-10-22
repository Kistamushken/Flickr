package com.philuvarov.flickr.photos

import com.philuvarov.flickr.Is
import com.philuvarov.flickr.remote.model.Photo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

@Suppress("IllegalIdentifier")
class PhotoItemConverterTest {

    private val converter = createPhotoItemConverter()

    @Test
    fun `convert - converted item has domain photo id - always`() {
        val photo = photo()

        val item = converter.convert(photo)

        assertThat(item.id, Is(photo.id))
    }

    @Test
    fun `convert - returns photo with domain image url - photo has domain image url`() {
        val photo = photo(url = "url")

        val item = converter.convert(photo)

        assertThat(item.url, Is(photo.url))
    }

    @Test
    fun `convert - builds image url manually - photo does not provide image url`() {
        val photo = photo(url = null, farm = "farm", server = "server", secret = "secret")

        val item = converter.convert(photo)

        assertThat(item.url, Is("http://farmfarm.static.flickr.com/server/1_secret.jpg"))
    }

    private fun photo(id: Long = 1L,
                      url: String? = null,
                      farm: String = "farm",
                      server: String = "server",
                      secret: String = "secret") = Photo(id, url, farm, server, secret)

    private fun createPhotoItemConverter(): PhotoItemConverter {
        return PhotoItemConverter()
    }

}