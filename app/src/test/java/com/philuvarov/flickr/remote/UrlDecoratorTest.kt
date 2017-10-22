package com.philuvarov.flickr.remote

import com.philuvarov.flickr.Is
import okhttp3.HttpUrl
import org.junit.Assert.assertThat
import org.junit.Test

private const val API_KEY = "key"

@Suppress("IllegalIdentifier")
class UrlDecoratorTest {

    private val initialUrl = HttpUrl.parse("http://www.example.com")!!

    private val decorator = UrlDecorator(API_KEY)

    @Test
    fun `decorate - adds api key - always`() {
        val url = decorator.decorate(initialUrl)

        url.assertParameter("api_key", "key")
    }

    @Test
    fun `decorate - sets response parameter to json - always`() {
        val url = decorator.decorate(initialUrl)

        url.assertParameter("format", "json")
    }

    @Test
    fun `decorate - adds no json callback - always`() {
        val url = decorator.decorate(initialUrl)

       url.assertParameter("nojsoncallback", "1")
    }

    @Test
    fun `decorate - sets per_page parameter to 21 - always`() {
        val url = decorator.decorate(initialUrl)

       url.assertParameter("safe_search", "1")
    }

    @Test
    fun `decorate - adds extra image size - always`() {
        val url = decorator.decorate(initialUrl)

       url.assertParameter("extras", "url_n")
    }

    @Test
    fun `decorate - adds safe search flag - always`() {
        val url = decorator.decorate(initialUrl)

       url.assertParameter("safe_search", "1")
    }

    private fun HttpUrl.assertParameter(key: String, value: String) {
        assertThat(queryParameterNames().contains(key), Is(true))
        assertThat(queryParameterValues(key).size, Is(1))
        assertThat(queryParameterValues(key)[0], Is(value))
    }
}

