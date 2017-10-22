package com.philuvarov.flickr.remote

import dagger.Reusable
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

@Reusable
class SignatureInterceptor @Inject constructor(
        private val urlDecorator: UrlDecorator
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        proceed(
                request()
                        .newBuilder()
                        .url(urlDecorator.decorate(request().url()))
                        .build()
        )
    }
}