package com.philuvarov.flickr.remote


import com.philuvarov.flickr.remote.ApiModule.ApiValues.API_KEY
import dagger.Reusable
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Named

@Reusable
class UrlDecorator @Inject constructor(@Named(API_KEY) private val apiKey: String) {

    fun decorate(url: HttpUrl): HttpUrl = with(url.newBuilder()) {
        addQueryParameter("api_key", apiKey)
        addQueryParameter("format", "json")
        addQueryParameter("nojsoncallback", "1")
        addQueryParameter("per_page", "21")
        addQueryParameter("extras", "url_n")
        addQueryParameter("safe_search", "1")
        build()
    }

}
